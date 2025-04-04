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

import swp391.jobseeker.domain.Skill;
import swp391.jobseeker.service.SkillService;

@Controller
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/admin/manage-skill")
    public String getSkillPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid page number");
        }
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Skill> skills = this.skillService.getAllSkillsPageable(pageable);

        model.addAttribute("skills", skills.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", skills.getTotalPages());
        return "admin/manage-skill";
    }

    @PostMapping("/admin/add-skill")
    public String addSkill(@RequestParam("skillName") String skillName, Model model) {
        skillName = skillName.trim();
        if(skillService.checkSkillByName(skillName)){
            return "redirect:/admin/manage-skill?skillexist";
        }
        Skill skill = new Skill();
        skill.setName(skillName);
        skillService.handleSaveSkill(skill);
        return "redirect:/admin/manage-skill?skill-add-success";
    }

    @GetMapping("/admin/delete-skill/{id}")
    public String deleteSkill(@PathVariable("id") long id, Model model) {
        if(skillService.checkSkillUsedByAnyCompanyOrJob(id)) {
            return "redirect:/admin/manage-skill?skill-delete-failed";
        }
        skillService.deleteById(id);
        return "redirect:/admin/manage-skill?skill-delete-success";
    }

    @PostMapping("/admin/edit-skill")
    public String editSkill(@RequestParam("skillName") String skillName,@RequestParam("skillId") long id, Model model) {
        skillName = skillName.trim();
        if(skillService.checkSkillByName(skillName)){
            return "redirect:/admin/manage-skill?skillexist";
        }

        Skill skill = skillService.getSkillById(id);
        skill.setName(skillName);
        skillService.handleSaveSkill(skill);
        return "redirect:/admin/manage-skill?skill-edit-success";
    }

}
