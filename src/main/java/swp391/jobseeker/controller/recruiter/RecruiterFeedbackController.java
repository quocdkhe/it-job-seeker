package swp391.jobseeker.controller.recruiter;

import java.util.List;

import javax.naming.Binding;

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
import swp391.jobseeker.domain.Feedback;
import swp391.jobseeker.domain.SupportRequest;
import swp391.jobseeker.domain.User;
import swp391.jobseeker.domain.Topic;
import swp391.jobseeker.service.FeedbackService;
import swp391.jobseeker.service.FileService;
import swp391.jobseeker.service.SupportRequestService;
import swp391.jobseeker.service.TopicService;
import swp391.jobseeker.service.UserService;

@Controller
public class RecruiterFeedbackController {
    private final FileService fileService;
    private final UserService userService;
    private final SupportRequestService supportRequestService;
    private final FeedbackService feedbackService;
    private final TopicService topicService;

    public RecruiterFeedbackController(FileService fileService, UserService userService,
            FeedbackService feedbackService,
            SupportRequestService supportRequestService, TopicService topicService) {
        this.fileService = fileService;
        this.userService = userService;
        this.supportRequestService = supportRequestService;
        this.feedbackService = feedbackService;
        this.topicService = topicService;
    }

    @GetMapping("/recruiter/feedback")
    public String getChooseFeedbackPage() {
        return "recruiter/choose-feedback-type";
    }

    @GetMapping("/recruiter/feedback/feedback-product")
    public String feedbackCommentRecruiter(Model model, HttpServletRequest request) {
        Feedback feedback = new Feedback();
        List<Topic> topics = topicService.getAllList();

        model.addAttribute("topics", topics);
        model.addAttribute("feedback", feedback);
        return "recruiter/feedback-recruiter-comment";
    }

    @PostMapping("/feedback/submit-recruiter")
    public String feedbackRecruiter(
            @ModelAttribute("feedback") Feedback feedback,
            @RequestParam("userId") long userId) {

        User user = new User();
        user.setId(userId);
        feedback.setUser(user);
        feedbackService.saveFeedback(feedback);
        return "redirect:/recruiter/feedback?comment-support-success";
    }

    @GetMapping("/recruiter/feedback/feedback-support")
    public String feedbackSupportRecruiter(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        SupportRequest supportRequest = new SupportRequest();
        supportRequest.setEmail(user.getEmail());
        supportRequest.setPhone(user.getPhone());
        model.addAttribute("user", user);
        model.addAttribute("supportRequest", supportRequest);
        return "recruiter/feedback-recruiter-support";
    }

    @PostMapping("/support/submit-recruiter")
    public String supportRecruiter(
            @Valid @ModelAttribute("supportRequest") SupportRequest support, BindingResult bindingResult,
            @RequestParam(value = "imageFile1", required = false) MultipartFile file1,
            @RequestParam(value = "imageFile2", required = false) MultipartFile file2,
            @RequestParam(value = "userId") long userId,
            HttpSession session,
            Model model,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "recruiter/feedback-recruiter-support";
        }
        User user = userService.getUserById(userId);
        SupportRequest supportRequest = new SupportRequest();
        supportRequest.setEmail(support.getEmail());
        supportRequest.setPhone(support.getPhone());
        supportRequest.setTopic(support.getTopic());
        supportRequest.setDescription(support.getDescription());
        supportRequest.setChecked(false);
        supportRequest.setUser(user);
        // Update user information

        // Handle avatar upload if a file is provided
        if (file1 != null && !file1.isEmpty()) {
            try {

                String avatarPath = fileService.handleSaveUploadFile(file1, "support", "image");
                supportRequest.setImage1(avatarPath);

            } catch (Exception e) {
                model.addAttribute("errorMessage", "Failed to up img");
                return "redirect:/recruiter/feedback?feedback-support-fail";
            }
        }
        if (file2 != null && !file2.isEmpty()) {
            try {

                String avatarPath = fileService.handleSaveUploadFile(file2, "support", "image");
                supportRequest.setImage2(avatarPath);

            } catch (Exception e) {
                model.addAttribute("errorMessage", "Failed to up img");
                return "redirect:/recruiter/feedback?feedback-support-fail";
            }
        }
        supportRequestService.handleSaveSupportRequest(supportRequest);
        return "redirect:/recruiter/feedback?feedback-support-success";
    }
}
