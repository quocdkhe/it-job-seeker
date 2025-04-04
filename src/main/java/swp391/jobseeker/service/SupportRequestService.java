package swp391.jobseeker.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.SupportRequest;
import swp391.jobseeker.repository.SupporterRequestRepository;

@Service
public class SupportRequestService {
    private final SupporterRequestRepository supporterRequestRepository;

    public SupportRequestService(SupporterRequestRepository supporterRequestRepository) {
        this.supporterRequestRepository = supporterRequestRepository;
    }

    public void handleSaveSupportRequest(SupportRequest supportRequest) {
        this.supporterRequestRepository.save(supportRequest);
    }

    public Page<SupportRequest> getAllSupportRequestPageable(Pageable pageable) {
        return this.supporterRequestRepository.findAll(pageable);
    }

    public List<SupportRequest> getAllSupportRequest() {
        return this.supporterRequestRepository.findAll();
    }

    public SupportRequest getSupportRequestById(int id) {
        return this.supporterRequestRepository.findById(id);
    }

    public void deleteSupportRequest(int id) {
        this.supporterRequestRepository.deleteById(id);
    }

    public long countUncheckSupportRequest() {
        return this.supporterRequestRepository.countByIsCheckedFalse();
    }
}
