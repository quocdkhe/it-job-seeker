package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swp391.jobseeker.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findById(long id);

    User findByUsername(String username);

    @SuppressWarnings("null")
    Page<User> findAll(Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsById(long id);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    void deleteById(long userId);

    boolean existsByPhone(String phone);

    @Query(value = "SELECT u.* FROM users u INNER JOIN company_like cl ON u.id = cl.user_id WHERE cl.company_id = :companyId", countQuery = "SELECT COUNT(*) FROM company_like cl WHERE cl.company_id = :companyId", nativeQuery = true)
    Page<User> findFollowersByCompanyId(@Param("companyId") long companyId, Pageable pageable);

    @Query(value = "SELECT u.* FROM users u INNER JOIN company_like cl ON u.id = cl.user_id WHERE cl.company_id = :companyId",  nativeQuery = true)
    List<User> getFollowersByCompanyId(@Param("companyId") long companyId);

    long countByIsActivatedTrue();

}
