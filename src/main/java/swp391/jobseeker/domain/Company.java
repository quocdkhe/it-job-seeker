package swp391.jobseeker.domain;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private boolean isApproved;
    private String type;
    private String industry;
    private String country;

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String description;

    private String size;
    private String logo;
    private String website;
    private String otPolicy;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private Set<Branch> branches;

    @OneToMany(mappedBy = "workingCompany")
    @JsonIgnore
    private Set<User> recruiters;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "company_skill", joinColumns = @JoinColumn(name = "company_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> skills;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private Set<Job> jobs;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private Set<Review> reviews;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private Set<CompanyImages> companyImages;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private Set<Resume> applyingCV;

    @ManyToMany(mappedBy = "favoriteCompanies")
    @JsonIgnore
    private Set<User> subscribers;

    public Company() {
    }

    public Company(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<CompanyImages> getCompanyImages() {
        return companyImages;
    }

    public void setCompanyImages(Set<CompanyImages> companyImages) {
        this.companyImages = companyImages;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Set<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Set<Branch> branches) {
        this.branches = branches;
    }

    public Set<User> getRecruiters() {
        return recruiters;
    }

    public void setRecruiters(Set<User> recruiters) {
        this.recruiters = recruiters;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public String getOtPolicy() {
        return otPolicy;
    }

    public void setOtPolicy(String otPolicy) {
        this.otPolicy = otPolicy;
    }

    public Set<Resume> getApplyingCV() {
        return applyingCV;
    }

    public void setApplyingCV(Set<Resume> applyingCV) {
        this.applyingCV = applyingCV;
    }

    public Set<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<User> subscribers) {
        this.subscribers = subscribers;
    }

}
