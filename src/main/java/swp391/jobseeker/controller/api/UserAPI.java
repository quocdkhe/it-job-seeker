package swp391.jobseeker.controller.api;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import swp391.jobseeker.domain.Job;
import swp391.jobseeker.service.CompanyService;
import swp391.jobseeker.service.JobService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UserAPI {
    private final JobService jobService;
    private final CompanyService companyService;

    public UserAPI(JobService jobService, CompanyService companyService) {
        this.jobService = jobService;
        this.companyService = companyService;
    }

    @PostMapping("/seeker/api/add-favorite-job/{id}")
    public ResponseEntity<String> addFavoriteJob(@PathVariable("id") long jobId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                jobService.handleAddFavoriteJob(jobId, userId);
                return new ResponseEntity<>("ok", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("User not logged in", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/seeker/api/delete-favorite-job/{id}")
    public ResponseEntity<String> deleteFavoriteJob(@PathVariable("id") long jobId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                jobService.handleDeleteFavoriteJob(jobId, userId);
                return new ResponseEntity<>("ok", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("User not logged in", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/seeker/api/follow-company/{id}")
    public ResponseEntity<String> addFollowingCompany(@PathVariable("id") long companyId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                companyService.handleAddFollowingCompany(userId, companyId);
                return new ResponseEntity<>("ok", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("User not logged in", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/seeker/api/unfollow-company/{id}")
    public ResponseEntity<String> deleteFollowingCompany(@PathVariable("id") long companyId,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                companyService.handleUnfollowingCompany(userId, companyId);
                return new ResponseEntity<>("ok", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("User not logged in", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/api/job-json/{id}")
    public ResponseEntity<Job> getJobDetailsJson(@PathVariable("id") long id) {
        Job job = jobService.getJobById(id);
        return new ResponseEntity<>(job, HttpStatus.OK);
    }

    @GetMapping("/api/liked/{jobId}")
    public ResponseEntity<Boolean> isJobLiked(@PathVariable("jobId") long jobId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null && jobService.checkJobIsLiked(jobId, userId)) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

    @GetMapping("/api/applied/{jobId}")
    public ResponseEntity<Boolean> isJobApplied(@PathVariable("jobId") long jobId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null && jobService.isJobAppliedByUser(userId, jobId)) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

}
