package swp391.jobseeker.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Review;
import swp391.jobseeker.service.ReportService;
import swp391.jobseeker.service.ReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReportController {
    private final ReportService reportService;
    private final ReviewService reviewService;

    public ReportController(ReportService reportService, ReviewService reviewService) {
        this.reportService = reportService;
        this.reviewService = reviewService;
    }

    @GetMapping("/admin/review-report")
    public String getAllReportedReview(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            page = Integer.parseInt(pageOptional.orElse("1"));
        } catch (NumberFormatException e) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<Review> reportedReviews = this.reviewService.getReportedReviewsPageable(0, pageable);
        model.addAttribute("reportedReviews", reportedReviews.getContent());
        model.addAttribute("totalPages", reportedReviews.getTotalPages());
        Set<String> uniqueReason = this.reportService.getUniqueReasons();
        model.addAttribute("uniqueReasons", uniqueReason);
        model.addAttribute("currentPage", page);
        return "admin/review-report";
    }

    @GetMapping("/admin/review-report/detail-review-report/{id}")
    public String getReportedReviewDetail(Model model, @PathVariable("id") long id) {
        Review review = this.reviewService.getReviewById(id);
        Company company = review.getCompany();
        model.addAttribute("company", company);
        model.addAttribute("review", review);
        return "admin/review-report-detail";
    }

    @GetMapping("/admin/review-report/delete-review-report/{id}")
    public String deleteReview(@PathVariable("id") long reviewId, HttpServletRequest request) {
        Review review = reviewService.getReviewById(reviewId);
        reportService.deleteReportByReviewId(reviewId);
        reviewService.deleteReview(review);
        return "redirect:/admin/review-report?delete-review-success";
    }

}
