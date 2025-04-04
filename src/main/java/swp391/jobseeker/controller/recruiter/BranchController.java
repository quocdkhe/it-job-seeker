package swp391.jobseeker.controller.recruiter;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import swp391.jobseeker.domain.Branch;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.service.BranchService;

@Controller
public class BranchController {

    private final BranchService branchService;

    @Value("${gomaps.key}")
    private String mapsApiKey;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping("/recruiter/manage-branch")
    public String getBranchPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long companyId = (Long) session.getAttribute("companyId");
        List<Branch> branches = branchService.getAllBranchesByCompanyId(companyId);
        model.addAttribute("branches", branches);
        model.addAttribute("branch", new Branch());
        return "recruiter/manage-branch";
    }

    @GetMapping("/recruiter/manage-branch/add")
    public String addBranch(Model model) {
        Branch branch = new Branch();
        model.addAttribute("branch", branch);
        model.addAttribute("MAPS_API_KEY", mapsApiKey);
        return "recruiter/add-branch";
    }

    @PostMapping("/recruiter/manage-branch/add")
    public String handleAddBranch(@ModelAttribute("branch") Branch branch, HttpServletRequest request,
            @RequestParam String detail) {
        HttpSession session = request.getSession(false);
        long companyId = (Long) session.getAttribute("companyId");
        Company company = new Company();
        company.setId(companyId);
        branch.setCompany(company);
        String[] detailParts = detail.split(",", 2);
        String detailBeforeComma = detailParts.length > 0 ? detailParts[0] : detail;
        branch.setAddress(
                detailBeforeComma.isEmpty() ? branch.getAddress() : detailBeforeComma + ", " + branch.getAddress());
        branchService.handleSaveBranch(branch);
        return "redirect:/recruiter/manage-branch?add-branch-success";
    }

    @GetMapping("/recruiter/manage-branch/delete/{id}")
    public String deleteBranch(@PathVariable("id") long branchId) {
        if (branchService.isBranchCanBeDeleted(branchId)) {
            return "redirect:/recruiter/manage-branch?delete-branch-failed";
        }
        branchService.handleDeleteBranch(branchId);
        return "redirect:/recruiter/manage-branch?delete-branch-success";
    }

    @GetMapping("/recruiter/manage-branch/edit/{id}")
    public String getEditBranch(@PathVariable("id") long branchId, Model model) {
        Branch branch = branchService.getBranchById(branchId);
        model.addAttribute("MAPS_API_KEY", mapsApiKey);
        model.addAttribute("updateBranch", branch);
        return "recruiter/edit-branch";
    }

    @PostMapping("/recruiter/manage-branch/edit")
    public String handleEditBranch(@ModelAttribute("updateBranch") Branch branch, HttpServletRequest request,
            @RequestParam String detail) {
        HttpSession session = request.getSession(false);
        long companyId = (Long) session.getAttribute("companyId");
        // Good practice
        Company company = new Company();
        company.setId(companyId);
        branch.setCompany(company);
        String[] detailParts = detail.split(",", 2);
        String detailBeforeComma = detailParts.length > 0 ? detailParts[0] : detail;
        branch.setAddress(
                detailBeforeComma.isEmpty() ? branch.getAddress() : detailBeforeComma + ", " + branch.getAddress());
        branchService.handleSaveBranch(branch);
        return "redirect:/recruiter/manage-branch?update-branch-success";
    }
}
