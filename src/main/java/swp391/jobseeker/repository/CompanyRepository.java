package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import swp391.jobseeker.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findById(long id);

    Company findByName(String name);

    @SuppressWarnings("null")
    Page<Company> findAll(Pageable pageable);

    void deleteById(long id);

    @Query("SELECT c.id, c.name FROM Company c")
    List<Object[]> findAllCompanyIdsAndNames();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO company_like (user_id, company_id) VALUES (:userId, :companyId)", nativeQuery = true)
    void addFollowingCompany(long userId, long companyId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM company_like WHERE user_id = :userId AND company_id = :companyId", nativeQuery = true)
    void deleteFollowingCompany(long userId, long companyId);

    @Query(value = "SELECT COUNT(*) > 0 FROM company_like WHERE user_id = :userId AND company_id = :companyId", nativeQuery = true)
    boolean isUserFollowingCompany(long userId, long companyId);

    long countByIsApproved(boolean isApproved);

    @Query(value = "SELECT COUNT(*) FROM company_like WHERE company_id = :companyId", nativeQuery = true)
    long countCompanyLikes(long companyId);

}
