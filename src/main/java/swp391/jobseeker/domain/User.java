package swp391.jobseeker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import swp391.jobseeker.service.Validation.DobValidation.ValidDob;
import swp391.jobseeker.service.Validation.PasswordValidation.ValidPassword;
import swp391.jobseeker.service.Validation.UsernameValidation.ValidUsername;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean isActivated;

    @ValidUsername
    private String username;

    @ValidPassword
    private String password;

    private String email;

    @Pattern(regexp = "^\\d{9,10}$", message = "Số điện thoại phải gồm 9 đến 10 chữ số")
    private String phone;

    @ValidDob
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private boolean gender;
    private String address;
    private String fullName;
    private String avatar;
    private String provider;
    private String currentCV;

    @PrePersist
    public void prePersist() {
        if (this.provider == null) {
            this.provider = "LOCAL";
        }
    }

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "working_company_id")
    private Company workingCompany; // For HR only

    @ManyToMany
    @JoinTable(name = "job_like", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "job_id"))
    private Set<Job> favoriteJobs;

    @ManyToMany
    @JoinTable(name = "company_like", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "company_id"))
    private Set<Company> favoriteCompanies;

    @OneToMany(mappedBy = "user")
    private Set<Review> reviews;

    // public User(long id) {
    // this.id = id;
    // }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Company getWorkingCompany() {
        return workingCompany;
    }

    public void setWorkingCompany(Company workingCompany) {
        this.workingCompany = workingCompany;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Set<Job> getFavoriteJobs() {
        return favoriteJobs;
    }

    public void setFavoriteJobs(Set<Job> favoriteJobs) {
        this.favoriteJobs = favoriteJobs;
    }

    public Set<Company> getFavoriteCompanies() {
        return favoriteCompanies;
    }

    public void setFavoriteCompanies(Set<Company> favoriteCompanies) {
        this.favoriteCompanies = favoriteCompanies;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public String getCurrentCV() {
        return currentCV;
    }

    public void setCurrentCV(String currentCV) {
        this.currentCV = currentCV;
    }

}
