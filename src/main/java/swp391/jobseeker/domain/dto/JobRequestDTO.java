package swp391.jobseeker.domain.dto;

public class JobRequestDTO {
    private long jobId;
    private long userId;

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "JobRequest [jobId=" + jobId + ", userId=" + userId + "]";
    }
}
