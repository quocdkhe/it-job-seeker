package swp391.jobseeker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.Topic;
import swp391.jobseeker.repository.TopicRepository;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicsRepository) {
        this.topicRepository = topicsRepository;
    }

    public List<Topic> getAllList() {
        return topicRepository.findAll();
    }
}
