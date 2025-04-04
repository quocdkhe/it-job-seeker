package swp391.jobseeker.controller.recruiter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Skill;
import swp391.jobseeker.domain.User;
import swp391.jobseeker.service.CompanyService;
import swp391.jobseeker.service.FileService;
import swp391.jobseeker.service.SkillService;
import swp391.jobseeker.service.UserService;

@Controller
public class RecruiterCompanyController {

    private final CompanyService companyService;
    private final SkillService skillService;
    private final FileService fileService;
    private final UserService userService;

    public RecruiterCompanyController(CompanyService companyService,
            SkillService skillService, FileService fileService, UserService userService) {
        this.companyService = companyService;
        this.skillService = skillService;
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping("/recruiter/edit-company")
    public String getDetailUser(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        long companyId = (Long) session.getAttribute("companyId");
        Company workingCompany = companyService.getCompanyById(companyId);
        model.addAttribute("company", workingCompany);
        model.addAttribute("skills", skillService.getAllSkills());
        return "recruiter/edit-company";
    }

    @PostMapping("/recruiter/edit-company")
    public String handleEditCompany(Model model, @ModelAttribute("company") Company company,
            @RequestParam("imageFile") MultipartFile file) {
        Company currentCompany = companyService.getCompanyById(company.getId());
        currentCompany.setType(company.getType());
        currentCompany.setIndustry(company.getIndustry());
        currentCompany.setCountry(company.getCountry());
        currentCompany.setSkills(company.getSkills());
        currentCompany.setDescription(company.getDescription());
        currentCompany.setWebsite(company.getWebsite());
        currentCompany.setSize(company.getSize());
        currentCompany.setOtPolicy(company.getOtPolicy());

        if (!file.isEmpty()) {
            // Xóa ảnh cũ nếu có
            if (currentCompany.getLogo() != null) {
                fileService.handleDeleteImage(currentCompany.getLogo(), "company-logo");
            }
            // Lưu ảnh mới
            String avatarPath = fileService.handleSaveUploadFile(file, "company-logo", "image");
            currentCompany.setLogo(avatarPath);
        }
        companyService.handleSaveCompany(currentCompany);
        return "redirect:/recruiter/edit-company" + "?update-company-success";
    }

    @GetMapping("/recruiter/follower")
    public String getFollower(Model model, HttpServletRequest request,
            @RequestParam("page") Optional<String> pageOptional) {
        HttpSession session = request.getSession(false);
        long companyId = (Long) session.getAttribute("companyId");
        long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<User> followersRaw = this.userService.getAllFollowerPageable(companyId, pageable);
        model.addAttribute("followers", followersRaw.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", followersRaw.getTotalPages());

        // model.addAttribute("followers", company.getSubscribers());

        return "recruiter/follower";
    }
}
