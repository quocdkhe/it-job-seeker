package swp391.jobseeker.service.Validation.DobValidation;



import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DobValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDob {
    String message() default "Ngày sinh không hợp lệ"; // Thông báo lỗi mặc định

    Class<?>[] groups() default {}; // Nhóm validation (tùy chọn)

    Class<? extends Payload>[] payload() default {}; // Metadata tùy chỉnh (tùy chọn)
}
