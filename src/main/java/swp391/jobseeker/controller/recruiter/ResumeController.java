package swp391.jobseeker.controller.recruiter;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import swp391.jobseeker.domain.Resume;
import swp391.jobseeker.domain.dto.EmailContentDTO;
import swp391.jobseeker.service.EmailSenderService;
import swp391.jobseeker.service.ResumeService;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ResumeController {

    private final ResumeService resumeService;
    private final EmailSenderService emailSenderService;

    public ResumeController(ResumeService resumeService, EmailSenderService emailSenderService) {
        this.resumeService = resumeService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("recruiter/resume")
    public String getResumePage(@RequestParam(value = "page", defaultValue = "1") int page, Model model,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        long companyId = (Long) session.getAttribute("companyId");
        Page<Resume> allResumePageable = resumeService.getResumePageable(page - 1, companyId);
        int totalPages = allResumePageable.getTotalPages();
        List<Resume> allResume = allResumePageable.getContent();
        model.addAttribute("allResume", allResume);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "recruiter/manage-cv";
    }

    @GetMapping("recruiter/resume/contact/{id}")
    public String getContactPage(Model model, @PathVariable("id") int resumeId) {
        Resume resume = resumeService.getResumeById(resumeId);
        String emailTemplate = emailSenderService.getTemplateForAcceptingCV(resume.getFullName(),
                resume.getJob().getTitle(), resume.getJob().getCompany().getName(), resume.getPreferredLocation(),
                resume.getPreferredLevel());
        EmailContentDTO emailContentDTO = new EmailContentDTO();
        emailContentDTO.setUpdatedStatus(resume.getStatus());
        model.addAttribute("emailContentDTO", emailContentDTO);
        model.addAttribute("resume", resume);
        model.addAttribute("emailTemplate", emailTemplate);
        return "recruiter/contact";
    }

    @PostMapping("recruiter/resume/contact/{id}")
    public String postMethodName(@PathVariable("id") int resumeId, EmailContentDTO emailContentDTO) {
        String updateStatus = emailContentDTO.getUpdatedStatus();
        resumeService.handleChangeCVStatus(resumeId, updateStatus);
        emailSenderService.sendResumeFeedback(emailContentDTO);
        return "redirect:/recruiter/resume";
    }

}
