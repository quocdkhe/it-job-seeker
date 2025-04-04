package swp391.jobseeker.domain;

import java.sql.Timestamp;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String workingModel;
    private String salary;
    private boolean isOpening;
    private Timestamp updatedAt;

    @ManyToMany
    @JoinTable(name = "job_skill", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> skills;

    @ManyToMany
    @JoinTable(name = "job_branch", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "branch_id"))
    private Set<Branch> availableBranches;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "job_level", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "level_id"))
    private Set<Level> levels;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkingModel() {
        return workingModel;
    }

    public void setWorkingModel(String workingModel) {
        this.workingModel = workingModel;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public Set<Branch> getAvailableBranches() {
        return availableBranches;
    }

    public void setAvailableBranches(Set<Branch> availableBranches) {
        this.availableBranches = availableBranches;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<Level> getLevels() {
        return levels;
    }

    public void setLevels(Set<Level> levels) {
        this.levels = levels;
    }

    public boolean getIsOpening() {
        return isOpening;
    }

    public void setIsOpening(boolean isOpening) {
        this.isOpening = isOpening;
    }

}
