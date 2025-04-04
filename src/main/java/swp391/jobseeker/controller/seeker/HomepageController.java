package swp391.jobseeker.controller.seeker;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.User;
import swp391.jobseeker.service.CompanyService;
import swp391.jobseeker.service.EmailSenderService;
import swp391.jobseeker.service.FileService;
import swp391.jobseeker.service.GoogleCaptchaService;
import swp391.jobseeker.service.SkillService;
import swp391.jobseeker.service.UserService;

@Controller
public class HomepageController {

    private final UserService userService;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleCaptchaService captchaService;
    private final SkillService skillService;
    private final FileService fileService;
    private final EmailSenderService sendEmailService;

    @Value("${recaptcha.sitekey}")
    private String recaptchaSiteKey;

    public HomepageController(UserService userService, PasswordEncoder passwordEncoder,
            CompanyService companyService, GoogleCaptchaService captchaService,
            FileService fileService, SkillService skillService, EmailSenderService sendEmailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.captchaService = captchaService;
        this.skillService = skillService;
        this.fileService = fileService;
        this.sendEmailService = sendEmailService;
    }

    @GetMapping("/")
    public String getHomepage(Model model) {
        List<Company> companies = companyService.findTopCompaniesHomepage();
        model.addAttribute("companies", companies);
        return "seeker/index";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "auth/signin";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
        return "auth/signup";
    }

    @PostMapping("/register")
    public String handleRegister(Model model, @RequestParam("g-recaptcha-response") String recaptchaResponse,
            @Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        boolean isCaptchaValid = captchaService.verify(recaptchaResponse);
        if (!isCaptchaValid) {
            return "redirect:/register?captcha-error";
        }
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }
        // handle register
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setRole(this.userService.getRoleByName("Seeker"));
        user.setActivated(true);
        userService.handleSaveUser(user);
        return "redirect:/login?register-success";
    }

    @GetMapping("/access-denied")
    public String errorRedirect() {
        return "seeker/error";
    }

    @GetMapping("/resume-template")
    public String getResumeTemplatePage(Model model) {
        model.addAttribute("skills", skillService.getAllSkills());
        return "seeker/resume-template";
    }

    @GetMapping("/follow/companies")
    public String getFollowedCompanies(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        model.addAttribute("followedCompanies", user.getFavoriteCompanies());
        return "seeker/followCompanies";
    }

    @GetMapping("/follow/jobs")
    public String getFollowedJobs(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        model.addAttribute("followedJobs", user.getFavoriteJobs());
        return "seeker/followJobs";
    }

    @GetMapping("/company-registration")
    public String getCompanyRegistrationPage(Model model) {
        Company pendingCompany = new Company();
        User pendingUser = new User();
        pendingUser.setWorkingCompany(pendingCompany);
        model.addAttribute("pendingUser", new User());
        return "seeker/company-registration";
    }

    @PostMapping("/company-registration")
    public String handleCompanyRegistration(Model model,
            @RequestParam("imageFile1") MultipartFile file,
            @Valid @ModelAttribute("pendingUser") User user,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "seeker/company-registration";
        }

        // Save Company FIRST
        companyService.handleSaveCompany(user.getWorkingCompany());

        // Save User
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(userService.getRoleByName("Recruiter"));
        user.setActivated(false);
        userService.handleSaveUser(user);

        // Save Company Logo
        if (!file.isEmpty()) {
            String avatar = fileService.handleSaveUploadFile(file, "company-logo", "image");
            user.getWorkingCompany().setLogo(avatar);
        }
        user.getWorkingCompany().setApproved(false);
        companyService.handleSaveCompany(user.getWorkingCompany());

        sendEmailService.sendEmployerRegistrationEmail(user.getEmail());
        model.addAttribute("sentRequest", true);
        return "seeker/company-registration";
    }

    @GetMapping("/companies")
    public String getAllCompanies(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 6);
        Page<Company> getAllCompanies = companyService.getAllCompanyPageable(pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", getAllCompanies.getTotalPages());
        model.addAttribute("companies", getAllCompanies);
        return "seeker/allCompany";
    }

    @GetMapping("/about-us")
    public String getAboutUsPage() {
        return "seeker/about-us";
    }

    @GetMapping("/privacy-policy")
    public String getPrivacyPolicyPage() {
        return "seeker/privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String getTermsOfServicePage() {
        return "seeker/terms-of-service";
    }

    @GetMapping("/contact")
    public String getContactPage() {
        return "seeker/contact";
    }

}
