package swp391.jobseeker.controller.seeker;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import swp391.jobseeker.domain.Job;
import swp391.jobseeker.domain.Level;
import swp391.jobseeker.domain.Skill;
import swp391.jobseeker.domain.dto.JobCriteriaDTO;
import swp391.jobseeker.service.JobService;
import swp391.jobseeker.service.SkillService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SeekerJobController {

    private final JobService jobService;
    private final SkillService skillService;

    public SeekerJobController(JobService jobService, SkillService skillService) {
        this.jobService = jobService;
        this.skillService = skillService;
    }

    @GetMapping("/jobs/{id}")
    public String getJobDetailsPage(Model model, @PathVariable("id") long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Job job = this.jobService.findJobById(id);
        long daysAgo = jobService.calculateDaysAgo(job.getUpdatedAt());
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null && jobService.checkJobIsLiked(id, userId)) {
                model.addAttribute("liked", "true");
            }
            if (userId != null && jobService.isJobAppliedByUser(userId, id)) {
                model.addAttribute("applied", "true");
            }
        }
        model.addAttribute("daysAgo", daysAgo);
        model.addAttribute("job", job);
        return "seeker/job-detail";
    }

    @GetMapping("/jobs")
    public String getJobListPage(Model model, @RequestParam(name = "page", defaultValue = "1") int page,
            JobCriteriaDTO jobCriteriaDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String queryString = request.getQueryString();
        if (queryString != null && queryString.contains("page")) {
            queryString = queryString.replace("page=" + page + "&", "");
        }

        List<Skill> skills = skillService.getAllSkills();
        Page<Job> jobsPage = jobService.getAllJobsWithSpecification(page - 1, jobCriteriaDTO);
        List<Job> jobs = jobsPage.getContent();
        List<Level> levels = jobService.getAllLevels();

        model.addAttribute("levels", levels);
        model.addAttribute("jobs", jobs);
        if (!jobs.isEmpty()) {
            Job first = jobs.get(0);
            model.addAttribute("job", first);
            long daysAgo = jobService.calculateDaysAgo(first.getUpdatedAt());
            model.addAttribute("daysAgo", daysAgo);
            if (session != null) {
                Long userId = (Long) session.getAttribute("userId");
                if (userId != null && jobService.checkJobIsLiked(first.getId(), userId)) {
                    model.addAttribute("liked", "true");
                }
                if (userId != null && jobService.isJobAppliedByUser(userId, first.getId())) {
                    model.addAttribute("applied", "true");
                }
            }
        }
        model.addAttribute("queryString", queryString);
        model.addAttribute("skills", skills);
        model.addAttribute("totalPages", jobsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("jobCriteria", jobCriteriaDTO);
        return "seeker/job-list";
    }

    @PostMapping("/job-search")
    public String postMethodName(@RequestParam(name = "provinceCode", defaultValue = "0") int provinceCode,
            @RequestParam("keyword") String keyword) {
        String queryString = "?";
        queryString += "provinceCode=" + provinceCode;
        if (!keyword.isEmpty()) {
            Skill skill = skillService.getSkillByNameLike(keyword);
            if (skill != null) {
                queryString += "&requiredSkills=" + skill.getId();
            }
        }
        return "redirect:/jobs" + queryString;
    }
}
