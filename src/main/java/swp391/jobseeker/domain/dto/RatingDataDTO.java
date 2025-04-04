package swp391.jobseeker.domain.dto;

import java.util.Map;

public class RatingDataDTO {
    
    private double average;
    private int totalReviews;
    private int isRecommended;
    private Map<Integer, Integer> stars;

    public RatingDataDTO(double average, int totalReviews, int isRecommended, Map<Integer, Integer> stars) {
        this.average = average;
        this.totalReviews = totalReviews;
        this.isRecommended = isRecommended;
        this.stars = stars;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Map<Integer, Integer> getStars() {
        return stars;
    }

    public void setStars(Map<Integer, Integer> stars) {
        this.stars = stars;
    }

    public int getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(int isRecommended) {
        this.isRecommended = isRecommended;
    }

    
}
