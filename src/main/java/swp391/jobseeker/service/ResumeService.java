package swp391.jobseeker.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.User;
import swp391.jobseeker.domain.Resume;
import swp391.jobseeker.repository.ResumeRepository;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final int PAGE_SIZE = 7;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public void handleSaveResume(Resume resume) {
        this.resumeRepository.save(resume);
    }

    public Page<Resume> getResumePageable(int page, long companyId) {
        Company company = new Company(companyId);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return this.resumeRepository.findByCompany(company, pageable);
    }

    public Page<Resume> getUserResumePageable(Pageable page, long userId) {
        return this.resumeRepository.findByUserId(userId, page);
    }

    public long countResume(Company company) {
        return this.resumeRepository.countPendingResumesByJobCompany(company);
    }
    public int updateCVStatus(long resumeId, String status) {
        return resumeRepository.updateResumeStatus(resumeId, status);
    }

    public Resume getResumeById(long id) {
        return resumeRepository.findById(id);
    }

    public void handleChangeCVStatus(long id, String status) {
        resumeRepository.updateResumeStatus(id, status);
    }

    public boolean checkResumeExist(String resumeName) {
        return resumeRepository.existsByResumeName(resumeName);
    }

}
