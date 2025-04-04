package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import swp391.jobseeker.domain.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    
    
    List<Topic> findAll();
}
