package swp391.jobseeker.service.Validation.DobValidation;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Service
public class DobValidator implements ConstraintValidator<ValidDob, LocalDate> {
    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        if (dob == null) {
            return true;
        }
        LocalDate now = LocalDate.now();
        if(dob.isAfter(now)){
            return false;
        }
        Period period = Period.between(dob, now);
        return period.getYears() >= 18 && period.getYears() <= 100;

    }
    
}
