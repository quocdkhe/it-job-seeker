package swp391.jobseeker.controller.admin;

import java.util.List;
import java.util.Map;
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
import swp391.jobseeker.domain.Role;
import swp391.jobseeker.domain.User;
import swp391.jobseeker.service.CompanyService;
import swp391.jobseeker.service.FileService;
import swp391.jobseeker.service.JobService;
import swp391.jobseeker.service.SkillService;
import swp391.jobseeker.service.SupportRequestService;
import swp391.jobseeker.service.UserService;

@Controller
public class AccountController {

    private static final String ROLES_ATTRIBUTE = "roles";
    private static final String AVATAR_FOLDER = "avatar";
    private static final String IMG_FOLDER = "image";

    private final FileService fileService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final JobService jobService;
    private final SkillService skillService;
    private final SupportRequestService supportRequestService;

    public AccountController(FileService fileService, UserService userService,
            PasswordEncoder passwordEncoder, CompanyService companyService,
            JobService jobService, SkillService skillService, SupportRequestService supportRequestService) {
        this.fileService = fileService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.jobService = jobService;
        this.skillService = skillService;
        this.supportRequestService = supportRequestService;
    }

    @GetMapping("/admin/account")
    public String getAccountPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<User> usersRaw = this.userService.getAllUsersPageable(pageable);
        model.addAttribute("users", usersRaw.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersRaw.getTotalPages());
        return "admin/manage-account";
    }

    @GetMapping("/admin/account/add-user")
    public String getAddUserPage(Model model) {
        List<Role> roles = userService.getAllRoles();
        List<Company> companies = companyService.getCompaniesWithIdAndName();
        model.addAttribute(ROLES_ATTRIBUTE, roles);
        model.addAttribute("user", new User());
        model.addAttribute("companies", companies);
        return "admin/add-user";
    }

    @PostMapping("/admin/account/add-user")
    public String saveUser(@Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile file,
            Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            List<Role> roles = userService.getAllRoles();
            model.addAttribute(ROLES_ATTRIBUTE, roles);
            return "admin/add-user";
        }
        String avatar = fileService.handleSaveUploadFile(file, AVATAR_FOLDER, IMG_FOLDER);
        user.setAvatar(avatar);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActivated(true);
        this.userService.handleSaveUser(user);
        return "redirect:/admin/account?add-success";
    }

    @GetMapping("/admin/account/user-detail/{id}")
    public String getDetailUser(@PathVariable("id") int id, Model model) {
        List<Role> roles = userService.getAllRoles();
        model.addAttribute(ROLES_ATTRIBUTE, roles);
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/user-detail";
    }

    @GetMapping("/admin/account/deactivate/{id}")
    public String acountDeactivate(@PathVariable("id") int id, Model model) {
        User user = userService.getUserById(id);
        user.setActivated(false);
        this.userService.handleSaveUser(user);
        return "redirect:/admin/account?deactivate-success";
    }

    @GetMapping("/admin/account/activate/{id}")
    public String acoountActivate(@PathVariable("id") int id, Model model) {
        User user = userService.getUserById(id);
        user.setActivated(true);
        this.userService.handleSaveUser(user);
        return "redirect:/admin/account?activate-success";
    }

    @GetMapping("/admin/edit-admin")
    public String getAdminEditingPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "admin/edit-admin";
    }

    @PostMapping("/admin/edit-admin")
    public String updateAdminInfoAndAvatar(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile file,
            HttpSession session,
            Model model,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "admin/edit-admin";
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
                    fileService.handleDeleteImage(currentUser.getAvatar(), AVATAR_FOLDER);
                }
                String avatarPath = fileService.handleSaveUploadFile(file, AVATAR_FOLDER, IMG_FOLDER);
                currentUser.setAvatar(avatarPath);
                session.setAttribute(AVATAR_FOLDER, avatarPath);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Failed to update avatar.");
                return "admin/edit-admin";
            }
        }

        userService.handleSaveUser(currentUser);

        session.setAttribute("fullName", currentUser.getFullName());
        session.setAttribute(AVATAR_FOLDER, currentUser.getAvatar());

        return "redirect:/admin/edit-admin?update-success";
    }

    @PostMapping("/admin/update-pwd")
    public String changeAdminPassword(@ModelAttribute("user") User user,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model, HttpServletRequest request) {

        User currentUser = userService.getUserById(user.getId());

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            model.addAttribute("user", currentUser);
            return "redirect:/admin/edit-admin?old-pwd-error";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("user", currentUser);
            return "redirect:/admin/edit-admin?pwd-not-match";
        }

        if (confirmPassword.length() < 6) {
            model.addAttribute("user", currentUser);
            return "redirect:/admin/edit-admin?pwd-length-error";
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        currentUser.setPassword(encodedPassword);

        userService.handleSaveUser(currentUser);
        model.addAttribute("user", currentUser);
        return "redirect:/admin/edit-admin?pwd-change-success";
    }

    @PostMapping("/admin/account/change-role")
    public String changeUserRole(@RequestParam("userId") long userId, @RequestParam("role") String roleName,
            @RequestParam(value = "companyId", required = false) Long companyId) {
        User user = userService.getUserById(userId);
        Role role = userService.getRoleByName(roleName);
        Company company = (companyId != null && companyId > 0) ? companyService.getCompanyById(companyId) : null;
        user.setRole(role);
        user.setWorkingCompany(company);
        userService.handleSaveUser(user);
        return "redirect:/admin/account?change-role-success";
    }

    @GetMapping("/admin/dashboard")
    public String getAdminDashboard(Model model) {
        model.addAttribute("activeUserNumber", userService.getNumberOfActiveUser());
        model.addAttribute("activeCompanyNumber", companyService.countByIsApproved(true));
        model.addAttribute("inactiveCompanyNumber", companyService.countByIsApproved(false));
        model.addAttribute("uncheckSupportRequestNumber", supportRequestService.countUncheckSupportRequest());

        Map<String, Long> jobCountByLevel = jobService.getJobCountByLevel();
        model.addAttribute("jobCountByLevel", jobCountByLevel);
        Map<String, Integer> skillData = skillService.getTopSkillsWithOther();
        model.addAttribute("popularSkillDesc", skillData);
        return "admin/dashboard";
    }
}
