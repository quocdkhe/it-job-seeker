package swp391.jobseeker.controller.admin;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;

import swp391.jobseeker.domain.Feedback;
import swp391.jobseeker.domain.SupportRequest;
import swp391.jobseeker.service.EmailSenderService;
import swp391.jobseeker.service.FeedbackService;
import swp391.jobseeker.service.SupportRequestService;

@Controller
public class FeedbackController {
    private final SupportRequestService supportRequestService;
    private final FeedbackService feedbackService;
    private final EmailSenderService emailSenderService;

    public FeedbackController(
            FeedbackService feedbackService,
            SupportRequestService supportRequestService,
            EmailSenderService emailSenderService) {

        this.supportRequestService = supportRequestService;
        this.feedbackService = feedbackService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/admin/feedback")
    public String getChooseFeedbackPage() {
        return "admin/manage-feedback";
    }

    @GetMapping("/admin/feedback/mark-read/{id}")
    public String markRead(@PathVariable("id") int id) {
        SupportRequest sp = supportRequestService.getSupportRequestById(id);
        sp.setChecked(true);
        supportRequestService.handleSaveSupportRequest(sp);
        return "redirect:/admin/feedback-support?mark-read-success";
    }

    @GetMapping("/admin/feedback/reply/{feedbackId}")
    public String feedbackUser(@PathVariable("feedbackId") int id, Model model) {

        SupportRequest fb = supportRequestService.getSupportRequestById(id);
        fb.setChecked(true);
        supportRequestService.handleSaveSupportRequest(fb);
        model.addAttribute("support", fb);
        return "admin/send-feedback-support";
    }

    @GetMapping("/admin/feedback-support")
    public String getAccountPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, 5, Sort.by("isChecked").ascending());
        Page<SupportRequest> supportRaw = this.supportRequestService.getAllSupportRequestPageable(pageable);

        model.addAttribute("feedbackList", supportRaw);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", supportRaw.getTotalPages());

        return "admin/manage-feedback-support";
    }

    @GetMapping("/admin/feedback-product")
    public String getChooseFeedbackProduct(Model model, @RequestParam("page") Optional<String> pageOptional) {

        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, 5, Sort.by("isChecked").ascending());
        Page<Feedback> supportRaw = this.feedbackService.getAllFeedbackPageable(pageable);

        model.addAttribute("feedbackList", supportRaw);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", supportRaw.getTotalPages());

        return "admin/manage-feedback-product";
    }

    @PostMapping("/admin/mark-feedback-read")
    public String markReadFeedBack(@RequestParam("id") long id, Model model) {
        Feedback feedback = feedbackService.getFeedbackById(id);
        feedback.setChecked(true);
        feedbackService.saveFeedback(feedback);
        return "redirect:/admin/feedback-product?mark-read-success";
    }

    @PostMapping("/admin/feedback/send")
    public String send(
            @RequestParam("email") String email,
            @RequestParam("topic") String topic,
            @RequestParam("content") String content,
            @RequestParam(value = "imageFile1", required = false) MultipartFile imageFile1,
            @RequestParam(value = "imageFile2", required = false) MultipartFile imageFile2,
            Model model) {

        emailSenderService.sendEmailResponse(email, topic, content, imageFile1, imageFile2);
        return "redirect:/admin/feedback-support?send-email-admin";
    }

}
