package swp391.jobseeker.service.specification;

import java.util.Set;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import swp391.jobseeker.domain.Job;
import swp391.jobseeker.domain.Job_;
import swp391.jobseeker.domain.Level;
import swp391.jobseeker.domain.Province;
import swp391.jobseeker.domain.Province_;
import swp391.jobseeker.domain.Skill;
import swp391.jobseeker.domain.Branch;
import swp391.jobseeker.domain.Branch_;

@Service
public class JobSpecification {

    public static Specification<Job> isAvailableInCity(long provinceCode) {
        return (root, query, criteriaBuilder) -> {
            if (provinceCode == 0) {
                return criteriaBuilder.conjunction(); // Return all if no filtering needed
            }

            // Join availableBranches
            Join<Job, Branch> branches = root.join(Job_.AVAILABLE_BRANCHES);
            // Join Province from Branch
            Join<Branch, Province> province = branches.join(Branch_.PROVINCE);

            if (provinceCode == -1) {
                // Handle the case where the provinceCode is -1 (provinces not in (1, 32, 50))
                // Add a "NOT IN" condition for the provinces
                return criteriaBuilder.not(province.get(Province_.CODE).in(1, 32, 50));
            }
            // Otherwise, compare the specific provinceCode provided
            return criteriaBuilder.equal(province.get(Province_.CODE), provinceCode);
        };
    }

    /**
     * SELECT j.id, j.title
     * FROM jobs j
     * JOIN job_skill js ON j.id = js.job_id
     * JOIN skills s ON js.skill_id = s.id
     * WHERE s.name IN ('Java', 'Spring Boot', 'Spring')
     * GROUP BY j.id
     * HAVING COUNT(DISTINCT s.name) = 3;
     * 
     * @param requiredSkills
     * @return
     */

    public static Specification<Job> hasAllRequiredSkills(Set<Skill> requiredSkills) {
        return (root, query, criteriaBuilder) -> {
            if (requiredSkills == null || requiredSkills.isEmpty()) {
                return criteriaBuilder.conjunction(); // Return all jobs if no required skills
            }
            // Join Job with its skills
            Join<Job, Skill> jobSkills = root.join(Job_.SKILLS);
            // Create a predicate for matching the required skills
            Predicate predicate = jobSkills.in(requiredSkills);
            // Use group by to group jobs by their id
            query.groupBy(root.get(Job_.ID));
            // Add having clause to ensure the count of matched skills equals the size of
            // requiredSkills
            query.having(criteriaBuilder.equal(criteriaBuilder.count(root.get(Job_.ID)), requiredSkills.size()));
            return predicate;
        };
    }

    public static Specification<Job> hasOneOfTheseSkills(Set<Skill> skills) {
        return (root, query, criteriaBuilder) -> {
            if (skills == null || skills.isEmpty()) {
                return criteriaBuilder.conjunction(); // Return all jobs if no required skills
            }
            // Join Job with its skills
            Join<Job, Skill> jobSkills = root.join(Job_.SKILLS);
            // Create a predicate for matching the required skills
            return jobSkills.in(skills);
        };
    }

    /**
     * Select * from jobs
     * where working_model in ('Hybrid', 'At office');
     * 
     * @param workingModels
     * @return
     */
    public static Specification<Job> availableWorkingModels(Set<String> workingModels) {
        return (root, query, criteriaBuilder) -> {
            if (workingModels == null || workingModels.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get(Job_.WORKING_MODEL).in(workingModels);
        };
    }

    /**
     * 
     * @param levels
     * @return
     */
    public static Specification<Job> availableLevels(Set<Level> levels) {
        return (root, query, criteriaBuilder) -> {
            if (levels == null || levels.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Job, Level> jobLevels = root.join(Job_.LEVELS);
            return jobLevels.in(levels);
        };
    }

    public static Specification<Job> isOpening(boolean isOpening) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Job_.IS_OPENING), isOpening);
    }

}
