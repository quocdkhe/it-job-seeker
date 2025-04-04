package swp391.jobseeker.service.Validation.PasswordValidation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Mật khẩu không hợp lệ"; // Thông báo lỗi mặc định

    Class<?>[] groups() default {}; // Nhóm validation (tùy chọn)

    Class<?>[] payload() default {}; // Metadata tùy chỉnh (tùy chọn)

}
