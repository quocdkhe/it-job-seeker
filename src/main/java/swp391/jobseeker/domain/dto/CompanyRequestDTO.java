package swp391.jobseeker.domain.dto;

public class CompanyRequestDTO {
    private long userId;
    private long companyId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public CompanyRequestDTO(long userId, long companyId) {
        this.userId = userId;
        this.companyId = companyId;
    }

}
