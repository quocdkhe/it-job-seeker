package swp391.jobseeker.controller.seeker;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import swp391.jobseeker.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import swp391.jobseeker.domain.Branch;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Review;
import swp391.jobseeker.service.BranchService;
import swp391.jobseeker.service.CompanyService;
import swp391.jobseeker.service.ReportService;
import swp391.jobseeker.service.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SeekerCompanyController {

    private final ReviewService reviewService;
    private final BranchService branchService;
    private final CompanyService companyService;
    private final ReportService reportService;

    @Value("${gomaps.key}")
    private String mapsApiKey;

    public SeekerCompanyController(BranchService branchService, CompanyService companyService,
            ReviewService reviewService, ReportService reportService) {
        this.reportService = reportService;
        this.branchService = branchService;
        this.companyService = companyService;
        this.reviewService = reviewService;
    }

    @GetMapping("companies/{id}")
    public String getCompanyDetailPage(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        // Check if session exists
        HttpSession session = request.getSession(false);

        // Retrieve the company details
        Company company = companyService.getCompanyById(id);
        Set<Branch> branches = company.getBranches();
        String citiesString = branchService.convertBranchesSetToCity(branches);

        // If user is logged in, check if they are following the company
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null && companyService.isUserFollowingCompany(userId, id)) {
                model.addAttribute("following", "true");
            }
        }
        model.addAttribute("cities", citiesString);
        model.addAttribute("company", company);
        model.addAttribute("MAPS_API_KEY", mapsApiKey);
        return "seeker/company-detail";
    }

    @GetMapping("companies/review/{id}")
    public String getCompanyReviewPage(@PathVariable("id") long id, Model model, HttpServletRequest request,
            @RequestParam("page") Optional<String> pageOptional) {
        // Check if session exists
        HttpSession session = request.getSession(false);

        // Retrieve the company details
        Company company = companyService.getCompanyById(id);
        Set<Branch> branches = company.getBranches();
        String citiesString = branchService.convertBranchesSetToCity(branches);

        // If user is logged in, check if they are following the company
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null && companyService.isUserFollowingCompany(userId, id)) {
                model.addAttribute("following", "true");
            }
        }

        // Pagination
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 3);
        Page<Review> reviewRaw = reviewService.getCompanyReviewsPageable(id, pageable);
        model.addAttribute("reviews", reviewRaw.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviewRaw.getTotalPages());

        // get your review
        if (session != null) {
            long userId = (long) session.getAttribute("userId");
            Review yourReview = reviewService.getReviewByUserIdAndCompanyId(userId, id);
            model.addAttribute("yourReview", yourReview);
        }
        model.addAttribute("cities", citiesString);
        model.addAttribute("company", company);
        return "seeker/company-review";
    }

    @GetMapping("review/write-review/{id}")
    public String getWriteReviewPage(Model model, HttpServletRequest request, @PathVariable("id") long id) {
        Company company = companyService.getCompanyById(id);
        Review review = new Review();
        review.setIsRecommended(true);
        review.setOvertimePolicy("Satisfied");
        model.addAttribute("review", review);
        model.addAttribute("company", company);
        return "seeker/company-writeReview";
    }

    @PostMapping("companies/write-review/{id}")
    public String postWriteReviewPage(@ModelAttribute("review") Review review, HttpServletRequest request,
            @PathVariable("id") long id) {
        HttpSession session = request.getSession(false);
        long userId = (long) session.getAttribute("userId");
        // create an empty user object and set the id to the current user id
        User user = new User();
        user.setId(userId);
        review.setUser(user);
        review.setId(0);
        // create an empty company object and set the id to the current company id
        Company company = new Company();
        company.setId(id);
        review.setCompany(company);
        // check if the user has already written a review for this company
        Review existingReview = reviewService.getReviewByUserIdAndCompanyId(userId, id);
        if (existingReview != null) {
            review.setId(existingReview.getId());
            review.setReportCount(0);
            review.setCreatedAt(LocalDateTime.now());
        } else {
            review.setId(0);
        }
        reviewService.handleSaveReview(review);
        return "redirect:/companies/review/" + id + "?write-review-success";
    }

    @GetMapping("companies/delete-review/{reviewId}")
    public String deleteReview(@PathVariable("reviewId") long reviewId, HttpServletRequest request) {
        Review review = reviewService.getReviewById(reviewId);
        reportService.deleteReportByReviewId(reviewId);
        reviewService.deleteReview(review);
        return "redirect:/companies/review/" + review.getCompany().getId() + "?delete-review-success";
    }

    @GetMapping("companies/edit-review/{reviewId}")
    public String getEditReviewPage(@PathVariable("reviewId") long reviewId, Model model) {
        Review review = reviewService.getReviewById(reviewId);
        Company company = review.getCompany();
        model.addAttribute("company", company);
        model.addAttribute("review", review);
        return "seeker/company-editReview";
    }

    @PostMapping("companies/edit-review/{id}")
    public String editReview(@ModelAttribute("review") Review review, HttpServletRequest request,
            @PathVariable("id") long id) {
        HttpSession session = request.getSession(false);
        long userId = (long) session.getAttribute("userId");
        // create an empty user object and set the id to the current user id
        User user = new User();
        user.setId(userId);
        review.setUser(user);
        review.setId(0);
        // create an empty company object and set the id to the current company id
        Company company = new Company();
        company.setId(id);
        review.setCompany(company);
        // check if the user has already written a review for this company
        Review existingReview = reviewService.getReviewByUserIdAndCompanyId(userId, id);
        if (existingReview != null) {
            review.setId(existingReview.getId());
            review.setReportCount(0);
            review.setCreatedAt(LocalDateTime.now());
        } else {
            review.setId(0);
        }
        reviewService.handleSaveReview(review);
        return "redirect:/companies/review/" + id + "?edit-review-success";
    }

    @PostMapping("companies/review/report/{companyId}/{reviewId}")
    public String reportReview(@PathVariable("reviewId") long reviewId, @RequestParam("reason") String reason,
            @PathVariable("companyId") long companyId) {
        reportService.reportReview(reviewId, reason);
        return "redirect:/companies/review/" + companyId + "?report-review-success";
    }

}
