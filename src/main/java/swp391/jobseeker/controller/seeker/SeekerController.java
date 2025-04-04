package swp391.jobseeker.controller.seeker;

import org.apache.commons.codec.digest.DigestUtils;
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
import swp391.jobseeker.domain.User;
import swp391.jobseeker.service.EmailSenderService;
import swp391.jobseeker.service.FileService;
import swp391.jobseeker.service.UserService;
import swp391.jobseeker.service.ResumeService;

@Controller
public class SeekerController {
    private final FileService fileService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final ResumeService resumeService;

    public SeekerController(FileService fileService, UserService userService,
            EmailSenderService emailSenderService, PasswordEncoder passwordEncoder, ResumeService resumeService) {
        this.fileService = fileService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
        this.resumeService = resumeService;
    }

    @PostMapping("/seeker/delete-account")
    public String deleteAccount(@RequestParam("userId") Long userId) {
        userService.deleteById(userId);
        return "redirect:/login?delete-user-success";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("newPassword") String newPassword,
            @RequestParam("id") int userId,
            Model model) {

        User user = userService.getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.handleSaveUser(user);

        return "redirect:/login?pwd-change-success";
    }

    @GetMapping("/forget-pass")
    public String forgetPass() {
        return "auth/forget-pass";
    }

    @GetMapping("/auth/verification")
    public String verification(@RequestParam(value = "userId", required = false) Long userId, Model model) {
        if (userId != null) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("userId", userId);
        }
        return "auth/verification";
    }

    @GetMapping("/resend-code")
    public String resendCode(Model model,
            @RequestParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        int code = emailSenderService.generateCode();
        String hashedCode = DigestUtils.sha256Hex(String.valueOf(code));
        model.addAttribute("hashedCode", hashedCode);
        model.addAttribute("userId", user.getId());
        emailSenderService.sendEmail(user.getEmail(), code);
        return "redirect:/auth/verification?resend-success&userId=" + userId;
    }

    @PostMapping("/forget-pass")
    public String forgetPass(Model model, @RequestParam("username") String username,
            @RequestParam("email") String email) {
        User user = userService.getUserByUsername(username);
        if (user == null || !user.getEmail().equals(email)) {
            return "redirect:/forget-pass?invalid-user";
        }

        int code = emailSenderService.generateCode();
        String hashedCode = DigestUtils.sha256Hex(String.valueOf(code));
        model.addAttribute("hashedCode", hashedCode);
        model.addAttribute("userId", user.getId());
        emailSenderService.sendEmail(user.getEmail(), code);
        return "auth/verification";
    }

    @GetMapping("/seeker/edit-info")
    public String getAccountPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long seekerId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(seekerId);
        model.addAttribute("user", user);
        return "seeker/edit-info";
    }

    @PostMapping("/seeker/edit-info")
    public String updateUserInfo(@Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            Model model, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "seeker/edit-info";
        }
        User u = userService.getUserById(user.getId());
        u.setFullName(user.getFullName());
        u.setEmail(user.getEmail());
        u.setPhone(user.getPhone());
        u.setAddress(user.getAddress());
        u.setBirthDate(user.getBirthDate());
        u.setGender(user.isGender());
        userService.handleSaveUser(u);

        HttpSession session = request.getSession();
        session.setAttribute("fullName", user.getFullName());
        return "redirect:/seeker/edit-info?edit-success";
    }

    @PostMapping("/seeker/update-pwd")
    public String editPwd(@ModelAttribute("user") User user,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model, HttpServletRequest request) {

        User currentUser = userService.getUserById(user.getId());

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            model.addAttribute("user", currentUser);
            return "redirect:/seeker/edit-info?old-pwd-error";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("user", currentUser);
            return "redirect:/seeker/edit-info?pwd-not-match";
        }

        if (confirmPassword.length() < 6) {
            model.addAttribute("user", currentUser);
            return "redirect:/seeker/edit-info?pwd-length-error";
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        currentUser.setPassword(encodedPassword);

        userService.handleSaveUser(currentUser);
        model.addAttribute("user", currentUser);
        return "redirect:/seeker/edit-info?pwd-change-success";
    }

    @PostMapping("/seeker/update-avatar")
    public String updateAvatar(@RequestParam("imageFile") MultipartFile file, HttpSession session, Model model) {
        try {
            long userId = (Long) session.getAttribute("userId");
            User user = userService.getUserById(userId);

            if (!file.isEmpty()) {
                if (user.getAvatar() != null) {
                    fileService.handleDeleteImage(user.getAvatar(), "avatar");
                }

                String avatarPath = fileService.handleSaveUploadFile(file, "avatar", "image");
                user.setAvatar(avatarPath);

                userService.handleSaveUser(user);

                session.setAttribute("avatar", avatarPath);
            }

            return "redirect:/seeker/edit-info?update-avatar-success";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update avatar.");
            return "redirect:/seeker/edit-info?update-avatar-fail";
        }
    }

    @GetMapping("/resume")
    public String Resume(HttpSession session, Model model) {
        long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);

        String cv = user.getCurrentCV();
        model.addAttribute("cv", cv);
        model.addAttribute("user", user);
        return "seeker/resume";
    }

    @PostMapping("/resume/add-cv")
    public String addCV(
            @RequestParam("upCV") MultipartFile upCV,
            HttpServletRequest request,
            Model model) {
        try {
            HttpSession session = request.getSession(false);
            long seekerId = (Long) session.getAttribute("userId");
            User user = userService.getUserById(seekerId);
            model.addAttribute("user", user);
            String name = user.getUsername();

            if (!upCV.isEmpty()) {
                String fileName = fileService.handleSaveUploadFile(upCV, name, "cv");
                boolean exists = resumeService.checkResumeExist(user.getCurrentCV());
                if (user.getCurrentCV() != null && !user.getCurrentCV().isEmpty() && exists == false) {
                    fileService.handleDeleteCV(user.getCurrentCV(), name);
                }
                user.setCurrentCV(fileName);
                userService.handleSaveUser(user);
            }
            return "redirect:/resume?up-cv-success";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm CV!");
            return "error";
        }
    }

}
