package swp391.jobseeker.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Job;
import swp391.jobseeker.domain.Level;
import swp391.jobseeker.domain.Province;
import swp391.jobseeker.domain.dto.JobCriteriaDTO;
import swp391.jobseeker.repository.JobRepository;
import swp391.jobseeker.repository.LevelRepository;
import swp391.jobseeker.repository.ProvinceRepository;
import swp391.jobseeker.service.specification.JobSpecification;

@Service
public class JobService {
    private final LevelRepository levelRepository;
    private final JobRepository jobRepository;
    private final ProvinceRepository provinceRepository;
    private static final int PAGE_SIZE_SEEKER = 10;
    private static final int PAGE_SIZE_RECRUITER = 7;

    public JobService(LevelRepository levelRepository, JobRepository jobRepository,
            ProvinceRepository provinceRepository) {
        this.levelRepository = levelRepository;
        this.jobRepository = jobRepository;
        this.provinceRepository = provinceRepository;
    }

    public List<Province> getAllProvincesByJobId(long jobId) {
        return this.provinceRepository.findProvinceByJobId(jobId);
    }

    public List<Level> getAllLevels() {
        return levelRepository.findAll();
    }

    public void handleSaveJob(Job job) {
        this.jobRepository.save(job);
    }

    public Page<Job> getAllJobsByCompanyId(long id, int page) {
        Company company = new Company();
        company.setId(id);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE_RECRUITER);
        return jobRepository.findByCompany(company, pageable);
    }

    public Job findJobById(long id) {
        return jobRepository.findById(id);
    }

    public long calculateDaysAgo(Timestamp lastUpdatedTime) {
        // Convert Timestamp to LocalDateTime
        LocalDateTime dateTime = lastUpdatedTime.toLocalDateTime();

        // Convert LocalDateTime to LocalDate if you want to ignore time
        LocalDate dbDate = dateTime.toLocalDate();

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate days between the two dates
        return ChronoUnit.DAYS.between(dbDate, currentDate);
    }

    public void handleAddFavoriteJob(long jobId, long userId) {
        this.jobRepository.addFavoriteJob(userId, jobId);
    }

    public void handleDeleteFavoriteJob(long jobId, long userId) {
        this.jobRepository.deleteFavoriteJob(userId, jobId);
    }

    public boolean checkJobIsLiked(long jobId, long userId) {
        return this.jobRepository.isJobLikedByUser(userId, jobId);
    }

    public Page<Job> getAllJobsPageable(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE_SEEKER);
        return jobRepository.findAll(pageable);
    }

    public Page<Job> getAllJobsWithSpecification(int page, JobCriteriaDTO jobCriteriaDTO) {
        Specification<Job> specification = Specification.where(null);
        if (jobCriteriaDTO.getRequiredSkills() != null && !jobCriteriaDTO.getRequiredSkills().isEmpty()) {
            specification = jobCriteriaDTO.isMixedSkill() ? specification
                    .and(JobSpecification.hasAllRequiredSkills(jobCriteriaDTO.getRequiredSkills()))
                    : specification.and(JobSpecification.hasOneOfTheseSkills(jobCriteriaDTO.getRequiredSkills()));
        }
        if (jobCriteriaDTO.getProvinceCode() != 0) {
            specification = specification
                    .and(JobSpecification.isAvailableInCity(jobCriteriaDTO.getProvinceCode()));
        }

        if (jobCriteriaDTO.getWorkingModels() != null && !jobCriteriaDTO.getWorkingModels().isEmpty()) {
            specification = specification
                    .and(JobSpecification.availableWorkingModels(jobCriteriaDTO.getWorkingModels()));
        }

        if (jobCriteriaDTO.getLevels() != null && !jobCriteriaDTO.getLevels().isEmpty()) {
            specification = specification
                    .and(JobSpecification.availableLevels(jobCriteriaDTO.getLevels()));
        }
        specification = specification.and(JobSpecification.isOpening(true));
        Pageable pageable = PageRequest.of(page, PAGE_SIZE_SEEKER);
        return jobRepository.findAll(specification, pageable);
    }

    public Job getJobById(long id) {
        return jobRepository.findById(id);
    }

    public void handleDeleteJob(long id) {
        jobRepository.deleteById(id);
    }

    public void handleActivateJob(long jobId) {
        jobRepository.activateJob(jobId);
    }

    public void handleDeactivateJob(long jobId) {
        jobRepository.deactivateJob(jobId);
    }

    public Map<String, Long> getJobCountByLevel() {
        List<Object[]> results = jobRepository.countJobsByLevel();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0], // Level Name
                        result -> (Long) result[1] // Job Count
                ));
    }

    public Province getProvinceByNameLike(String name) {
        return provinceRepository.findByNameContainingIgnoreCase(name);
    }

    public long countOpenJobsByCompanyId(long companyId) {
        return jobRepository.countOpenJobsByCompanyId(companyId);
    }

    public List<Object[]> getJobStatisticsByCompanyId(long companyId) {
        return jobRepository.findJobStatisticsByCompanyId(companyId);
    }

    public boolean isJobAppliedByUser(long userId, long jobId) {
        return jobRepository.isResumeSubmittedWithinLastWeek(userId, jobId);
    }

    public boolean isJobBeingApplied(long jobId) {
        return jobRepository.existsByJobIdInResumes(jobId);
    }

    public boolean checkJobIsOpening(long jobId) {
        return jobRepository.isJobOpening(jobId);
    }

}
