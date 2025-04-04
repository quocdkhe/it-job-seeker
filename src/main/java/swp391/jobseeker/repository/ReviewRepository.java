package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swp391.jobseeker.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review save(Review review);

    List<Review> findByCompanyId(long companyId);

    Page<Review> findByCompanyIdOrderByCreatedAtDesc(long companyId, Pageable pageable);

    Review findByUserIdAndCompanyId(long userId, long companyId);

    Review findById(long id);

    List<Review> findByReportCountGreaterThanOrderByReportCountDesc(int minReportCount);

    Page<Review> findByReportCountGreaterThanOrderByReportCountDesc(int minReportCount, Pageable pageable);
}
