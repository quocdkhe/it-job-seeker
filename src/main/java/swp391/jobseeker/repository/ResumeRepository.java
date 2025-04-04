package swp391.jobseeker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Page<Resume> findByCompany(Company company, Pageable pageable);
    Page<Resume> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Resume r " +
        "WHERE r.job.company = :company AND r.status = 'Pending'")
    Long countPendingResumesByJobCompany(@Param("company") Company company);
    Resume findById(long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE resumes SET status = :status WHERE id = :id", nativeQuery = true)
    int updateResumeStatus(@Param("id") Long id, @Param("status") String status);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Resume r WHERE r.resume = :resumeName")
    boolean existsByResumeName(@Param("resumeName") String resumeName);
}
