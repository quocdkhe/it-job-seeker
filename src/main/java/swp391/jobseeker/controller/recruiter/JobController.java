package swp391.jobseeker.controller.recruiter;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import swp391.jobseeker.domain.Branch;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Job;
import swp391.jobseeker.domain.Level;
import swp391.jobseeker.domain.Skill;
import swp391.jobseeker.service.BranchService;
import swp391.jobseeker.service.JobService;
import swp391.jobseeker.service.SkillService;

@Controller
public class JobController {

    private final SkillService skillService;
    private final BranchService branchService;
    private final JobService jobService;

    public JobController(SkillService skillService, BranchService branchService, JobService jobService) {
        this.skillService = skillService;
        this.branchService = branchService;
        this.jobService = jobService;
    }

    @GetMapping("/recruiter/manage-jobs")
    public String getJobPage(Model model, HttpServletRequest request,
            @RequestParam(name = "page", defaultValue = "1") int page) {
        HttpSession session = request.getSession();
        long companyId = (long) session.getAttribute("companyId");
        Page<Job> jobPageable = jobService.getAllJobsByCompanyId(companyId, page - 1);
        List<Job> jobs = jobPageable.getContent();
        model.addAttribute("totalPages", jobPageable.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("jobs", jobs);
        return "recruiter/manage-jobs";
    }

    @GetMapping("recruiter/manage-jobs/add-job")
    public String getAddJobPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        long companyId = (long) session.getAttribute("companyId");
        List<Branch> branches = branchService.getAllBranchesByCompanyId(companyId);
        Job job = new Job();
        List<Skill> skills = skillService.getAllSkills();
        List<Level> levels = jobService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("job", job);
        model.addAttribute("skills", skills);
        model.addAttribute("branches", branches);
        return "recruiter/add-job";
    }

    @GetMapping("/recruiter/manage-jobs/delete/{id}")
    public String handleDeleteJob(@PathVariable long id) {
        if (jobService.isJobBeingApplied(id)) {
            return "redirect:/recruiter/manage-jobs?delete-job-failed";
        }
        jobService.handleDeleteJob(id);
        return "redirect:/recruiter/manage-jobs?delete-job-success";
    }

    @PostMapping("recruiter/manage-jobs/add-job")
    public String handleAddNewJobs(@ModelAttribute("job") Job job, HttpServletRequest request) {
        HttpSession session = request.getSession();
        long companyId = (long) session.getAttribute("companyId");
        Company company = new Company();
        company.setId(companyId);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        job.setUpdatedAt(now);
        job.setCompany(company);
        jobService.handleSaveJob(job);
        return "redirect:/recruiter/manage-jobs?add-job-success";
    }

    @GetMapping("recruiter/manage-jobs/update/{id}")
    public String getUpdateJobPage(@PathVariable long id, Model model,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        long companyId = (long) session.getAttribute("companyId");
        List<Branch> branches = branchService.getAllBranchesByCompanyId(companyId);
        Job job = jobService.findJobById(id);
        List<Skill> skills = skillService.getAllSkills();
        List<Level> levels = jobService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("job", job);
        model.addAttribute("skills", skills);
        model.addAttribute("branches", branches);
        return "recruiter/update-job";
    }

    @PostMapping("recruiter/manage-jobs/update")
    public String handleUpdateJobs(@ModelAttribute("job") Job updatedJob) {
        long jobId = updatedJob.getId();
        Job job = jobService.findJobById(jobId);
        job.setAvailableBranches(updatedJob.getAvailableBranches());
        job.setDescription(updatedJob.getDescription());
        job.setIsOpening(updatedJob.getIsOpening());
        job.setLevels(updatedJob.getLevels());
        job.setSalary(updatedJob.getSalary());
        job.setTitle(updatedJob.getTitle());
        job.setSkills(updatedJob.getSkills());
        job.setWorkingModel(updatedJob.getWorkingModel());
        job.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        jobService.handleSaveJob(job);
        return "redirect:/recruiter/manage-jobs/update/" + jobId + "?update-job-success";
    }

}
