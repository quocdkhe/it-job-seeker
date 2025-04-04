package swp391.jobseeker.controller.recruiter;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import swp391.jobseeker.domain.CompanyImages;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.User;

import swp391.jobseeker.service.CompanyImagesService;
import swp391.jobseeker.service.UserService;
import swp391.jobseeker.service.FileService;

@Controller
public class CompanyImagesController {
    private static final String USER_ID = "userId";
    private CompanyImagesService companyImagesService;
    private final FileService fileService;
    private final UserService userService;

    public CompanyImagesController(FileService fileService, CompanyImagesService companyImagesService,
            UserService userService) {
        this.fileService = fileService;
        this.companyImagesService = companyImagesService;
        this.userService = userService;
    }

    // Thêm hình ảnh
    @PostMapping("/recruiter/manage-company-image/add-image")
    public String addImage(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("description") String description,
            HttpServletRequest request,
            Model model) {
        try {
            HttpSession session = request.getSession(false);
            long seekerId = (Long) session.getAttribute(USER_ID);
            User user = userService.getUserById(seekerId);
            model.addAttribute("user", user);

            Company company = user.getWorkingCompany();

            if (!imageFile.isEmpty()) {
                String fileName = fileService.handleSaveUploadFile(imageFile, "company-images", "image");

                CompanyImages image = new CompanyImages();
                image.setImage(fileName);
                image.setDescription(description);
                image.setCompany(company);

                companyImagesService.save(image);
            }
            return "redirect:/recruiter/manage-company-image?add-image-success";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm hình ảnh!");
            return "error";
        }
    }

    // Chỉnh sửa hình ảnh
    @PostMapping("/recruiter/manage-company-image/update-image")
    public String updateImage(
            @RequestParam("id") Long id,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam("description") String description,
            HttpServletRequest request,
            Model model) {
        try {
            HttpSession session = request.getSession(false);
            long seekerId = (Long) session.getAttribute(USER_ID);
            User user = userService.getUserById(seekerId);
            model.addAttribute("user", user);

            Company company = user.getWorkingCompany();

            CompanyImages image = companyImagesService.findById(id);
            if (image != null) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String fileName = fileService.handleSaveUploadFile(imageFile, "company-images", "image");
                    image.setImage(fileName);
                }
                image.setDescription(description);
                image.setCompany(company);

                companyImagesService.save(image);
            }
            return "redirect:/recruiter/manage-company-image?update-image-success";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi chỉnh sửa hình ảnh!");
            return "error";
        }
    }

    @GetMapping("/recruiter/manage-company-image/delete-image/{id}")
    public String deleteImage(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        try {
            HttpSession session = request.getSession(false);
            long seekerId = (Long) session.getAttribute(USER_ID);
            User user = userService.getUserById(seekerId);
            model.addAttribute("user", user);
            CompanyImages image = companyImagesService.findById(id);
            fileService.handleDeleteImage(image.getImage(), "company-images");
            companyImagesService.delete(image);

            return "redirect:/recruiter/manage-company-image?delete-image-success";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xóa hình ảnh!");
            return "error";
        }
    }
}
