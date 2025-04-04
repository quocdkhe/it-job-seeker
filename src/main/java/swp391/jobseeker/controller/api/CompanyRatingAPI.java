package swp391.jobseeker.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import swp391.jobseeker.domain.dto.CompanyRatingsDTO;
import swp391.jobseeker.service.ReviewService;

@RestController
public class CompanyRatingAPI {

    private final ReviewService reviewService;

    public CompanyRatingAPI(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/companies/{companyId}/rating")
    public ResponseEntity<CompanyRatingsDTO> getCompanyRating(@PathVariable long companyId) {
        CompanyRatingsDTO companyRatings = this.reviewService.getCompanyRatings(companyId);
        return new ResponseEntity<>(companyRatings, HttpStatus.OK);
    }
}
