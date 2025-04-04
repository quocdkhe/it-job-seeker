package swp391.jobseeker.controller.recruiter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.CompanyImages;
import swp391.jobseeker.domain.User;
import swp391.jobseeker.service.BranchService;
import swp391.jobseeker.service.CompanyImagesService;
import swp391.jobseeker.service.CompanyService;
import swp391.jobseeker.service.EmailSenderService;
import swp391.jobseeker.service.FileService;
import swp391.jobseeker.service.JobService;
import swp391.jobseeker.service.ResumeService;
import swp391.jobseeker.service.UserService;

@Controller
public class RecruiterController {

    private final FileService fileService;
    private final UserService userService;
    private final CompanyService companyService;
    private final CompanyImagesService companyImagesService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService sendEmailService;
    private final JobService jobService;
    private final BranchService branchService;
    private final ResumeService resumeService;

    public RecruiterController(FileService fileService, UserService userService, ResumeService resumeService,
            PasswordEncoder passwordEncoder, CompanyService companyService, CompanyImagesService companyImagesService,
            EmailSenderService sendEmailService, JobService jobService, BranchService branchService) {
        this.fileService = fileService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.companyImagesService = companyImagesService;
        this.sendEmailService = sendEmailService;
        this.jobService = jobService;
        this.branchService = branchService;
        this.resumeService = resumeService;
    }

    @GetMapping("/recruiter/edit-recruiter")
    public String getEditingPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "recruiter/edit-recruiter";
    }

    @PostMapping("/recruiter/edit-recruiter")
    public String updateInfoAndAvatar(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile file,
            HttpSession session,
            Model model,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "recruiter/edit-recruiter";
        }

        User currentUser = userService.getUserById(user.getId());
        currentUser.setFullName(user.getFullName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhone(user.getPhone());
        currentUser.setAddress(user.getAddress());
        currentUser.setGender(user.isGender());
        currentUser.setBirthDate(user.getBirthDate());

        if (file != null && !file.isEmpty()) {
            try {
                if (currentUser.getAvatar() != null) {
                    fileService.handleDeleteImage(currentUser.getAvatar(), "avatar");
                }
                String avatarPath = fileService.handleSaveUploadFile(file, "avatar", "image");
                currentUser.setAvatar(avatarPath);
                session.setAttribute("avatar", avatarPath);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Failed to update avatar.");
                return "recruiter/edit-recruiter";
            }
        }

        userService.handleSaveUser(currentUser);

        session.setAttribute("fullName", currentUser.getFullName());
        session.setAttribute("avatar", currentUser.getAvatar());

        return "redirect:/recruiter/edit-recruiter?update-success";
    }

    @PostMapping("/recruiter/update-pwd")
    public String changePassword(@ModelAttribute("user") User user,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model, HttpServletRequest request) {

        User currentUser = userService.getUserById(user.getId());

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            model.addAttribute("user", currentUser);
            return "redirect:/recruiter/edit-recruiter?old-pwd-error";

        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("user", currentUser);
            return "redirect:/recruiter/edit-recruiter?pwd-not-match";
        }

        if (confirmPassword.length() < 6) {
            model.addAttribute("user", currentUser);
            return "redirect:/recruiter/edit-recruiter?pwd-length-error";
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        currentUser.setPassword(encodedPassword);

        userService.handleSaveUser(currentUser);
        model.addAttribute("user", currentUser);
        return "redirect:/recruiter/edit-recruiter?pwd-change-success";
    }

    @GetMapping("/recruiter/manage-company-image/edit-company/{id}")
    public String getEditCompanyPage(@PathVariable("id") int id, Model model) {
        Company company = companyService.getCompanyById(id);
        model.addAttribute("company", company);
        return "recruiter/edit-company";
    }

    @GetMapping("/recruiter/manage-company-image")
    public String getCompanyImagesPage(
            Model model,
            @RequestParam("page") Optional<String> pageOptional,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        long seekerId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(seekerId);
        model.addAttribute("user", user);

        long companyId = user.getWorkingCompany().getId();
        model.addAttribute("companyId", companyId);

        int page = 1;
        if (pageOptional.isPresent()) {
            String pageValue = pageOptional.get();
            if (pageValue.matches("\\d+")) {
                page = Integer.parseInt(pageValue);
            }
        }

        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<CompanyImages> imagesRaw = companyImagesService.getAllImagesByCompanyIdPageable(companyId, pageable);

        model.addAttribute("companyImages", imagesRaw.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", imagesRaw.getTotalPages());

        return "recruiter/manage-company-image";
    }

    @PostMapping("/send-follower-email")
    public String sendFollowerEmail(Model model, HttpServletRequest request,
            @RequestParam("recipientEmail") String recipientEmail,
            @RequestParam("emailSubject") String emailSubject,
            @RequestParam("emailHeader") String emailHeader,
            @RequestParam("emailDescription") String emailDescription) {
        HttpSession session = request.getSession(false);
        long companyId = (Long) session.getAttribute("companyId");
        Company company = companyService.getCompanyById(companyId);

        sendEmailService.sendFollowerEmail(recipientEmail, emailSubject, emailHeader, emailDescription,
                company.getName());

        model.addAttribute("emailSent", true);

        return "redirect:/recruiter/follower";
    }

    @PostMapping("/send-follower-email-all")
    public String sendFollowerEmailAll(Model model, HttpServletRequest request,
            @RequestParam("emailSubject") String emailSubject,
            @RequestParam("emailHeader") String emailHeader,
            @RequestParam("emailDescription") String emailDescription) {

        HttpSession session = request.getSession(false);
        long companyId = (Long) session.getAttribute("companyId");
        Company company = companyService.getCompanyById(companyId);

        List<User> users = userService.getAllFollower(companyId);

        for (User user : users) {
            sendEmailService.sendFollowerEmail(user.getEmail(), emailSubject, emailHeader, emailDescription,
                    company.getName());
        }

        model.addAttribute("emailSentAll", true);

        return "redirect:/recruiter/follower";
    }

    @GetMapping("/recruiter/dashboard")
    public String getRecruiterDashboard(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long companyId = (Long) session.getAttribute("companyId");
        Company company = companyService.getCompanyById(companyId);

        model.addAttribute("countOpeningJobs", jobService.countOpenJobsByCompanyId(companyId));
        model.addAttribute("countCompanyBranches", branchService.countBranchesByCompanyId(companyId));
        model.addAttribute("countCompanyLikes", companyService.countCompanyLikes(companyId));
        model.addAttribute("countOpeningResume", resumeService.countResume(company));
        model.addAttribute("company", company);

        List<Object[]> jobStats = jobService.getJobStatisticsByCompanyId(companyId);
        model.addAttribute("jobStatistics", jobStats);

        return "recruiter/dashboard";
    }
}
