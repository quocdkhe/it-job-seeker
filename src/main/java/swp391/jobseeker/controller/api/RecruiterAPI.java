package swp391.jobseeker.controller.api;

import org.springframework.web.bind.annotation.RestController;

import swp391.jobseeker.domain.dto.ResumeStatusDTO;
import swp391.jobseeker.service.JobService;
import swp391.jobseeker.service.ResumeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class RecruiterAPI {

    private final JobService jobService;
    private final ResumeService resumeService;

    public RecruiterAPI(JobService jobService, ResumeService resumeService) {
        this.jobService = jobService;
        this.resumeService = resumeService;
    }

    @PostMapping("/recruiter/manage-jobs/open/{id}")
    public ResponseEntity<Boolean> handleOpenJob(@PathVariable("id") int id) {
        jobService.handleActivateJob(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/recruiter/manage-jobs/close/{id}")
    public ResponseEntity<Boolean> handleCloseJob(@PathVariable("id") int id) {
        // handle open job
        jobService.handleDeactivateJob(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

}
