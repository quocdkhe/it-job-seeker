package swp391.jobseeker.domain.dto;

import java.util.Set;

import swp391.jobseeker.domain.Level;
import swp391.jobseeker.domain.Skill;

public class JobCriteriaDTO {
    private Set<Skill> requiredSkills;
    private long provinceCode;
    private Set<String> workingModels;
    private Set<Level> levels;
    private boolean mixedSkill;

    public Set<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<Skill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Set<String> getWorkingModels() {
        return workingModels;
    }

    public void setWorkingModels(Set<String> workingModels) {
        this.workingModels = workingModels;
    }

    public long getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(long provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Set<Level> getLevels() {
        return levels;
    }

    public void setLevels(Set<Level> levels) {
        this.levels = levels;
    }

    public boolean isMixedSkill() {
        return mixedSkill;
    }

    public void setMixedSkill(boolean mixedSkill) {
        this.mixedSkill = mixedSkill;
    }

}
