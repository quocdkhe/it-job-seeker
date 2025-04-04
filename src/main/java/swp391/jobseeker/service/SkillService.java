package swp391.jobseeker.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.Skill;
import swp391.jobseeker.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill getSkillByNameLike(String name) {
        return this.skillRepository.findFirstByNameContainingIgnoreCase(name);
    }

    public Skill handleSaveSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public List<Skill> getAllSkills() {
        return this.skillRepository.findAll();
    }

    public Skill getSkillById(long id) {
        return this.skillRepository.findById(id);
    }

    public Page<Skill> getAllSkillsPageable(Pageable pageable) {
        return this.skillRepository.findAll(pageable);
    }

    public boolean checkSkillByName(String name) {
        return this.skillRepository.existsByNameIgnoreCase(name);
    }

    public void deleteById(Long id) {
        this.skillRepository.deleteById(id);
    }
    public boolean checkSkillUsedByAnyCompanyOrJob(long skillId) {
        return this.skillRepository.isSkillUsedByAnyCompanyOrJob(skillId);
    }

    public Map<String, Integer> getTopSkillsWithOther() {
        List<Object[]> results = skillRepository.findTopSkills();

        Map<String, Integer> skillCounts = new LinkedHashMap<>();
        int otherCount = 0;

        for (Object[] row : results) {
            String skillName = (String) row[0];
            int count = ((Number) row[1]).intValue();

            if (skillCounts.size() < 9) { // Ensure we only take the top 9 explicitly
                skillCounts.put(skillName, count);
            } else {
                otherCount += count; // Group remaining into "Other"
            }
        }

        if (otherCount > 0) {
            skillCounts.put("Khác", otherCount);
        }

        return skillCounts;
    }
}
