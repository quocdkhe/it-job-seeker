package swp391.jobseeker.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Skill;
import swp391.jobseeker.service.CompanyService;
import swp391.jobseeker.service.EmailSenderService;
import swp391.jobseeker.service.FileService;
import swp391.jobseeker.service.SkillService;
import swp391.jobseeker.service.UserService;

@Controller
public class CompanyController {

    private final FileService logoService;
    private final CompanyService companyService;
    private final SkillService skillService;
    private final UserService userService;
    private final EmailSenderService emailSenderService;

    public CompanyController(FileService logoService, CompanyService companyService,
            SkillService skillService, UserService userService, EmailSenderService emailSenderService) {
        this.logoService = logoService;
        this.companyService = companyService;
        this.skillService = skillService;
        this.userService = userService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/admin/manage-company/add-company")
    public String showAddCompanyForm(Model model) {
        List<Skill> getAllSkills = skillService.getAllSkills();
        model.addAttribute("company", new Company()); // Khởi tạo đối tượng rỗng
        model.addAttribute("skills", getAllSkills);
        return "admin/add-company";
    }

    @PostMapping("/admin/manage-company/add-company")
    public String saveCompany(@ModelAttribute("company") Company company, @RequestParam("imageFile") MultipartFile file,
            Model model, HttpSession session) {
        String avatar = logoService.handleSaveUploadFile(file, "company-logo", "image");
        company.setLogo(avatar);
        company.setApproved(true);
        this.companyService.handleSaveCompany(company);
        return "redirect:/admin/manage-company?add-company-success";
    }

    @GetMapping("/admin/manage-company/edit-company/{id}")
    public String getEditCompanyPage(Model model, @PathVariable("id") long id) {
        Company company = companyService.getCompanyById(id);
        List<Skill> getAllSkills = skillService.getAllSkills();
        model.addAttribute("company", company);
        model.addAttribute("skills", getAllSkills);
        return "admin/edit-company";
    }

    @PostMapping("/admin/manage-company/edit-company")
    public String handleEditCompany(Model model, @ModelAttribute("company") Company company,
            @RequestParam("imageFile") MultipartFile file) {
        Company currentCompany = companyService.getCompanyById(company.getId());
        currentCompany.setName(company.getName());
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
                logoService.handleDeleteImage(currentCompany.getLogo(), "company-logo");
            }
            // Lưu ảnh mới
            String avatarPath = logoService.handleSaveUploadFile(file, "company-logo", "image");
            currentCompany.setLogo(avatarPath);
        }
        companyService.handleSaveCompany(currentCompany);
        return "redirect:/admin/manage-company/edit-company/" + company.getId() + "?update-company-success";
    }

    @GetMapping("/admin/manage-company")
    public String getCompanyList(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid page number");
        }
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Company> companiesRaw = this.companyService.getAllCompanyPageable(pageable);
        model.addAttribute("companies", companiesRaw.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", companiesRaw.getTotalPages());
        return "admin/manage-company";
    }

    @GetMapping("/admin/manage-company/activate/{id}")
    public String handleActivateCompany(@PathVariable("id") int id) {
        Company company = companyService.getCompanyById(id);
        company.setApproved(true);
        this.companyService.handleSaveCompany(company);
        company.getRecruiters().forEach(recruiter -> {
            recruiter.setActivated(true);
            userService.handleSaveUser(recruiter);
            emailSenderService.sendRecruiterApprovalEmail(recruiter.getEmail(), company.getName(), true);
        });
        return "redirect:/admin/manage-company";
    }

    @GetMapping("/admin/manage-company/delete/{id}")
    public String handleDeleteCompany(@PathVariable("id") long id) {
        this.companyService.getCompanyById(id).getRecruiters().forEach(recruiter -> {
            this.userService.deleteById(recruiter.getId());
            emailSenderService.sendRecruiterApprovalEmail(recruiter.getEmail(), recruiter.getWorkingCompany().getName(),
                    false);
        });

        this.companyService.deleteById(id);
        return "redirect:/admin/manage-company?company-deleted";
    }

}
