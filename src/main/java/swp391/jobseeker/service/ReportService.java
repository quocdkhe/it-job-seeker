package swp391.jobseeker.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import swp391.jobseeker.domain.Report;
import swp391.jobseeker.domain.Review;
import swp391.jobseeker.repository.ReportRepository;
import swp391.jobseeker.repository.ReviewRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;

    public ReportService(ReportRepository reportRepository, ReviewRepository reviewRepository) {
        this.reportRepository = reportRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public void reportReview(long reviewId, String reason) {
        Review review = reviewRepository.findById(reviewId);
        Report report = new Report();
        report.setReview(review);
        report.setReason(reason);
        reportRepository.save(report);
        review.incrementReportCount();
        reviewRepository.save(review);
    }

    public Set<String> getUniqueReasons() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream()
                      .map(Report::getReason)
                      .collect(Collectors.toSet());
    }

    @Transactional
    public void deleteReportByReviewId(long reviewId) {
        reportRepository.deleteByReviewId(reviewId);
    }
}
