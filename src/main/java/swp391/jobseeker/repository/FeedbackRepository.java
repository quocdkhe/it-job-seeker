package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import swp391.jobseeker.domain.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
  
    Feedback findById(long id);
    List<Feedback> findByUserId(Long userId);
    List<Feedback> findAll();
    @SuppressWarnings("null")
    Page<Feedback> findAll(Pageable pageable);
   
}
