package swp391.jobseeker.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.CompanyImages;
import swp391.jobseeker.domain.Feedback;
import swp391.jobseeker.repository.FeedbackRepository;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public void saveFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    public Page<Feedback> getAllFeedbackPageable(Pageable pageable) {
        return this.feedbackRepository.findAll(pageable);
    }

    public void delete(Feedback feedback) {
        feedbackRepository.delete(feedback);
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public Feedback getFeedbackById(long id) {
        return feedbackRepository.findById(id);
    }
}
