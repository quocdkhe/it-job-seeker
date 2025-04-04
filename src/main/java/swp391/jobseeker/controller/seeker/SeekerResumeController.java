package swp391.jobseeker.controller.seeker;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import swp391.jobseeker.controller.admin.AccountController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Job;
import swp391.jobseeker.domain.Level;
import swp391.jobseeker.domain.Province;
import swp391.jobseeker.domain.Resume;
import swp391.jobseeker.domain.User;
import swp391.jobseeker.service.EmailSenderService;
import swp391.jobseeker.service.FileService;
import swp391.jobseeker.service.JobService;
import swp391.jobseeker.service.ResumeService;
import swp391.jobseeker.service.UserService;

@Controller
public class SeekerResumeController {

    private final AccountController accountController;
    private final UserService userService;
    private final JobService jobService;
    private final ResumeService resumeService;
    private final FileService fileService;
    private final EmailSenderService emailSenderService;

    public SeekerResumeController(UserService userService, JobService jobService, ResumeService resumeService,
            FileService fileService, EmailSenderService emailSenderService, AccountController accountController) {
        this.userService = userService;
        this.jobService = jobService;
        this.resumeService = resumeService;
        this.fileService = fileService;
        this.emailSenderService = emailSenderService;
        this.accountController = accountController;
    }

    @GetMapping("/jobs/{id}/apply")
    public String getApplyCVPage(Model model, @PathVariable("id") int jobId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        String currentCV = user.getCurrentCV();

        if (!jobService.checkJobIsOpening(jobId)) {
            return "redirect:/jobs/" + jobId + "?job-closed";
        }

        if (currentCV != null) {
            model.addAttribute("currentCV", currentCV);
        }
        Resume resume = new Resume();
        resume.setUser(user);
        resume.setPhoneNumber(user.getPhone());
        resume.setFullName(user.getFullName());
        Job job = jobService.getJobById(jobId);
        List<Province> provinces = jobService.getAllProvincesByJobId(jobId);
        Set<Level> levels = job.getLevels();
        if (currentCV != null) {
            String cvFilePath = "/cv/" + user.getUsername() + "/" + user.getCurrentCV();
            model.addAttribute("cvFilePath", cvFilePath);
        }
        model.addAttribute("provinces", provinces);
        model.addAttribute("job", job);
        model.addAttribute("resume", resume);
        model.addAttribute("levels", levels);
        return "seeker/apply-cv";
    }

    @PostMapping("jobs/apply")
    public String handleApplyCV(@Valid Resume resume, BindingResult bindingResult,
            @RequestParam("cvOption") String cvOption,
            @RequestParam("cvFile") MultipartFile file,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);

        if (bindingResult.hasErrors()) {
            return "redirect:/jobs/" + resume.getJob().getId() + "/apply?phone-number-invalid";
        }
        if ("upload".equals(cvOption)) {
            if (file != null && !file.isEmpty()) {
                String fileName = fileService.handleSaveUploadFile(file, user.getUsername(), "cv");
                resume.setResume(fileName);
                resume.setUser(user);
            } else {
                return "redirect:/jobs/" + resume.getJob().getId() + "/apply?cv-file-error";
            }
        } else if (user.getCurrentCV() != null) {
            resume.setResume(user.getCurrentCV());
        } else {
            return "redirect:/jobs/" + resume.getJob().getId() + "/apply?cv-file-error";
        }
        resume.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        resume.setStatus("Pending");
        resumeService.handleSaveResume(resume);
        emailSenderService.sendAfterApplyingCV(resume.getEmail(), user.getFullName(), resume.getJob().getTitle(),
                resume.getCompany().getName(),
                resume.getPreferredLocation(), resume.getPreferredLevel());
        return "seeker/send-cv-completed";
    }

    @GetMapping("/testing-send-cv")
    public String getMethodName() {
        return "/seeker/send-cv-completed";
    }

    @GetMapping("/cv-status")
    public String getCVStatus(HttpServletRequest request, Model model,
            @RequestParam("page") Optional<String> pageOptional) {
        HttpSession session = request.getSession(false);
        long seekerId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(seekerId);
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 6);
        Page<Resume> getAllResume = resumeService.getUserResumePageable(pageable, seekerId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", getAllResume.getTotalPages());
        model.addAttribute("resumeList", getAllResume);
        model.addAttribute("user", user);
        return "seeker/cv-status";
    }

}
