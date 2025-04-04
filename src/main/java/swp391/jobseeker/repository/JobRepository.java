package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByCompany(Company company, Pageable pageable);

    Job findById(long id);

    void deleteById(long id);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM job_like WHERE user_id = :userId AND job_id = :jobId)", nativeQuery = true)
    public boolean isJobLikedByUser(@Param("userId") Long userId, @Param("jobId") Long jobId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO job_like (user_id, job_id) VALUES (:userId, :jobId)", nativeQuery = true)
    void addFavoriteJob(@Param("userId") Long userId, @Param("jobId") Long jobId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM job_like WHERE user_id = :userId AND job_id = :jobId", nativeQuery = true)
    void deleteFavoriteJob(@Param("userId") Long userId, @Param("jobId") Long jobId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE jobs SET is_opening = true WHERE id = :jobId", nativeQuery = true)
    void activateJob(@Param("jobId") long jobId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE jobs SET is_opening = false WHERE id = :jobId", nativeQuery = true)
    void deactivateJob(@Param("jobId") long jobId);

    boolean existsById(long id);

    Page<Job> findAll(Pageable pageable);

    Page<Job> findAll(Specification<Job> specification, Pageable pageable);

    @Query("SELECT l.name, COUNT(j) FROM Job j JOIN j.levels l GROUP BY l.name")
    List<Object[]> countJobsByLevel();

    @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId AND j.isOpening = true")
    long countOpenJobsByCompanyId(@Param("companyId") long companyId);

    @Query("SELECT j, "
            + "(SELECT COUNT(u) FROM User u JOIN u.favoriteJobs fj WHERE fj.id = j.id), "
            + "(SELECT COUNT(r) FROM Resume r WHERE r.job.id = j.id) "
            + "FROM Job j WHERE j.company.id = :companyId")
    List<Object[]> findJobStatisticsByCompanyId(@Param("companyId") long companyId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM job_level WHERE job_id = :jobId AND level_id = 2)", nativeQuery = true)
    boolean isJobAcceptingFresher(@Param("jobId") long jobId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM resumes r WHERE (CURRENT_DATE - CAST(r.updated_at AS DATE)) <= 7 AND job_id = :jobId AND user_id = :userId)", nativeQuery = true)
    boolean isResumeSubmittedWithinLastWeek(@Param("userId") long userId, @Param("jobId") long jobId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM resumes WHERE job_id = :jobId)", nativeQuery = true)
    boolean existsByJobIdInResumes(@Param("jobId") long jobId);

    @Query(value = "SELECT is_opening FROM jobs WHERE id = :jobId", nativeQuery = true)
    boolean isJobOpening(@Param("jobId") long jobId);

}
