package swp391.jobseeker.service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.Review;
import swp391.jobseeker.domain.dto.CompanyRatingsDTO;
import swp391.jobseeker.domain.dto.RatingDataDTO;
import swp391.jobseeker.domain.dto.RatingDetailDTO;
import swp391.jobseeker.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void handleSaveReview(Review review) {
        this.reviewRepository.save(review);
    }

    private RatingDataDTO calculateRatingData(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return new RatingDataDTO(0.0, 0, 0, Map.of(1, 0, 2, 0, 3, 0, 4, 0, 5, 0));
        }
        int totalReviews = reviews.size();
        double average = reviews.stream().mapToInt(Review::getOverallRating).average().orElse(0.0);

        // Phân bố sao
        Map<Integer, Integer> stars = reviews.stream()
                .collect(Collectors.groupingBy(
                        Review::getOverallRating,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
        for (int i=1; i<=5; i++) {
            stars.putIfAbsent(i, 0); // Đảm bảo có giá trị cho tất cả sao từ 1-5
        }

        int isRecommended = (int)reviews.stream().filter(Review::getIsRecommended).count();
        return new RatingDataDTO(average, totalReviews, isRecommended, stars);
    }

    private List<RatingDetailDTO> calculateRatingDetail(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        // Danh sách các tiêu chí cần tính
        List<Map<String, Object>> criteria = Arrays.asList(
                Map.of("id", 1, "name", "Thu nhập", "ratingField", "salaryRating"),
                Map.of("id", 2, "name", "Cấp trên", "ratingField", "managerRating"),
                Map.of("id", 3, "name", "Khả năng làm việc nhóm", "ratingField", "teamworkRating"),
                Map.of("id", 4, "name", "Văn hóa công ty", "ratingField", "cultureRating"),
                Map.of("id", 5, "name", "Môi trường làm việc", "ratingField", "environmentRating"));
        
        return criteria.stream().map(criterion -> {
            String field = (String) criterion.get("ratingField");
            int id = (int) criterion.get("id");
            String name = (String) criterion.get("name");

            // Tính trung bình cho tiêu chí
            double avg = reviews.stream()
                .mapToInt(review -> switch (field) {
                    case "salaryRating" -> review.getSalaryRating();
                    case "managerRating" -> review.getManagerRating();
                    case "teamworkRating" -> review.getTeamworkRating();
                    case "cultureRating" -> review.getCultureRating();
                    case "environmentRating" -> review.getEnvironmentRating();
                    default -> 0;
                })
                .average()
                .orElse(0.0);

            // Phân bố sao cho tiêu chí
            Map<Integer, Integer> stars = reviews.stream()
                .collect(Collectors.groupingBy(
                    review -> switch (field) {
                        case "salaryRating" -> review.getSalaryRating();
                        case "managerRating" -> review.getManagerRating();
                        case "cultureRating" -> review.getCultureRating();
                        case "environmentRating" -> review.getEnvironmentRating();
                        default -> 0;
                    },
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
            for (int i = 1; i <= 5; i++) {
                stars.putIfAbsent(i, 0);
            }

            return new RatingDetailDTO(id, name, avg, stars);
        }).toList();
    }

    public CompanyRatingsDTO getCompanyRatings(long companyId) {
        List<Review> reviews = this.reviewRepository.findByCompanyId(companyId);
        RatingDataDTO ratingData = calculateRatingData(reviews);
        List<RatingDetailDTO> ratingDetails = calculateRatingDetail(reviews);
        return new CompanyRatingsDTO(ratingData, ratingDetails);
    }

    public List<Review> getCompanyReviews(long companyId) {
        return this.reviewRepository.findByCompanyId(companyId);
    }

    public Page<Review> getCompanyReviewsPageable(long companyId, Pageable pageable) {
        return this.reviewRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable);
    }

    public Review getReviewByUserIdAndCompanyId(long userId, long companyId) {
        return this.reviewRepository.findByUserIdAndCompanyId(userId, companyId);
    }

    public void deleteReview(Review review) {
        this.reviewRepository.deleteById(review.getId());
    }

    public Review getReviewById(long reviewId) {
        return this.reviewRepository.findById(reviewId);
    }

    public List<Review> getReportedReviews(int minReportCount) {
        return reviewRepository.findByReportCountGreaterThanOrderByReportCountDesc(minReportCount);
    }

    public Page<Review> getReportedReviewsPageable(int minReportCount, Pageable pageable) {
        return reviewRepository.findByReportCountGreaterThanOrderByReportCountDesc(minReportCount, pageable);
    }

}
