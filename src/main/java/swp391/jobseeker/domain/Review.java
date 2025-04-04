package swp391.jobseeker.domain;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int overallRating;

    private String header;
    private String overtimePolicy;
    private String overtimeReason;
    private String workingReason;
    private String improvement;
    private int salaryRating;
    private int managerRating;
    private int teamworkRating;
    private int cultureRating;
    private int environmentRating;

    private boolean isRecommended;

    @Column(updatable = true)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "review")
    private Set<Report> report;

    private int reportCount;

    public void incrementReportCount() {
        this.reportCount++;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int overallRating) {
        this.overallRating = overallRating;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getOvertimePolicy() {
        return overtimePolicy;
    }

    public void setOvertimePolicy(String overtimePolicy) {
        this.overtimePolicy = overtimePolicy;
    }

    public String getOvertimeReason() {
        return overtimeReason;
    }

    public void setOvertimeReason(String overtimeReason) {
        this.overtimeReason = overtimeReason;
    }

    public String getWorkingReason() {
        return workingReason;
    }

    public void setWorkingReason(String workingReason) {
        this.workingReason = workingReason;
    }

    public String getImprovement() {
        return improvement;
    }

    public void setImprovement(String improvement) {
        this.improvement = improvement;
    }

    public int getSalaryRating() {
        return salaryRating;
    }

    public void setSalaryRating(int salaryRating) {
        this.salaryRating = salaryRating;
    }

    public int getManagerRating() {
        return managerRating;
    }

    public void setManagerRating(int managerRating) {
        this.managerRating = managerRating;
    }

    public int getTeamworkRating() {
        return teamworkRating;
    }

    public void setTeamworkRating(int teamworkRating) {
        this.teamworkRating = teamworkRating;
    }

    public int getCultureRating() {
        return cultureRating;
    }

    public void setCultureRating(int cultureRating) {
        this.cultureRating = cultureRating;
    }

    public int getEnvironmentRating() {
        return environmentRating;
    }

    public void setEnvironmentRating(int environmentRating) {
        this.environmentRating = environmentRating;
    }

    public boolean getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(boolean isRecommended) {
        this.isRecommended = isRecommended;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<Report> getReport() {
        return report;
    }

    public void setReport(Set<Report> report) {
        this.report = report;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }
    
}
