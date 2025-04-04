package swp391.jobseeker.domain.dto;

import java.util.List;

public class CompanyRatingsDTO {

    private RatingDataDTO ratingData;
    private List<RatingDetailDTO> ratingDetails;

    public CompanyRatingsDTO(RatingDataDTO ratingData, List<RatingDetailDTO> ratingDetails) {
        this.ratingData = ratingData;
        this.ratingDetails = ratingDetails;
    }

    public RatingDataDTO getRatingData() {
        return ratingData;
    }

    public void setRatingData(RatingDataDTO ratingData) {
        this.ratingData = ratingData;
    }

    public List<RatingDetailDTO> getRatingDetails() {
        return ratingDetails;
    }

    public void setRatingDetails(List<RatingDetailDTO> ratingDetails) {
        this.ratingDetails = ratingDetails;
    }

    
}
