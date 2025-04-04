package swp391.jobseeker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import swp391.jobseeker.domain.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Modifying
    @Query("DELETE FROM Report r WHERE r.review.id = :reviewId")
    void deleteByReviewId(long reviewId);
}
