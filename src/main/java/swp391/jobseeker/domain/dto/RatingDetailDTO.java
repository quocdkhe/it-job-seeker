package swp391.jobseeker.domain.dto;

import java.util.Map;

public class RatingDetailDTO {

    private int id;
    private String name;
    private double avg;
    private Map<Integer, Integer> stars;
    
    public RatingDetailDTO(int id, String name, double avg, Map<Integer, Integer> stars) {
        this.id = id;
        this.name = name;
        this.avg = avg;
        this.stars = stars;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public Map<Integer, Integer> getStars() {
        return stars;
    }

    public void setStars(Map<Integer, Integer> stars) {
        this.stars = stars;
    }

    
}
