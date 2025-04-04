package swp391.jobseeker.service;

import java.util.Random;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import swp391.jobseeker.domain.dto.EmailContentDTO;

@Service
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public int generateCode() {
        Random random = new Random();
        return 1000000 + random.nextInt(9000000);
    }

    public void sendEmailResponse(String toEmail, String subject, String content,
            MultipartFile imageFile1, MultipartFile imageFile2) {
        try {
            // Tạo một đối tượng MimeMessage mới
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Tạo nội dung email với HTML
            StringBuilder body = new StringBuilder();
            body.append("<!DOCTYPE html>")
                    .append("<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                    .append("<title>").append(subject).append("</title></head>")
                    .append("<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>")
                    .append("<div style='width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>")
                    .append("<div style='background: #28a745; color: white; padding: 15px; font-size: 20px; border-top-left-radius: 10px; border-top-right-radius: 10px;'>")
                    .append(subject)
                    .append("</div>")
                    .append("<div style='padding: 20px; color: #333; text-align: left;'>")
                    .append("<p>Xin chào,</p>")
                    .append("<p>Cảm ơn bạn đã liên hệ với chúng tôi. Chúng tôi rất vui khi được hỗ trợ bạn với các yêu cầu dưới đây:</p>")
                    .append("<p><strong>Yêu cầu của bạn:</strong><br>").append(content).append("</p>")
                    .append("<p>Chúng tôi đã giải quyết yêu cầu của bạn như sau:</p>")
                    .append("<p><strong>Phản hồi của chúng tôi:</strong><br>")
                    .append("Chúng tôi đã đính kèm thông tin chi tiết dưới đây để giải đáp thắc mắc của bạn. ")
                    .append("Nếu bạn có bất kỳ câu hỏi nào thêm, đừng ngần ngại liên hệ lại với chúng tôi.</p>");

            // Chỉ thêm hình ảnh nếu có ảnh
            if (imageFile1 != null && !imageFile1.isEmpty() && imageFile2 != null && !imageFile2.isEmpty()) {
                body.append("<div style='display: flex; justify-content: space-between; gap: 20px;'>")
                        .append("<img src='cid:image1' alt='Hướng dẫn chi tiết' style='width: 48%; height: auto;'>")
                        .append("<img src='cid:image2' alt='Thông tin bổ sung' style='width: 48%; height: auto;'>")
                        .append("</div>");
            } else if (imageFile1 != null && !imageFile1.isEmpty()) {
                body.append("<div style='text-align: center;'>")
                        .append("<img src='cid:image1' alt='Hướng dẫn chi tiết' style='width: 100%; height: auto;'>")
                        .append("</div>");
            } else if (imageFile2 != null && !imageFile2.isEmpty()) {
                body.append("<div style='text-align: center;'>")
                        .append("<img src='cid:image2' alt='Thông tin bổ sung' style='width: 100%; height: auto;'>")
                        .append("</div>");
            }

            body.append("<p>Chúng tôi luôn sẵn sàng hỗ trợ bạn. Cảm ơn bạn đã chọn dịch vụ của chúng tôi!</p>")
                    .append("<p>Trân trọng,</p>")
                    .append("<p><strong>Đội ngũ hỗ trợ của Công ty JobSeeker</strong></p>")
                    .append("</div>")
                    .append("<div style='margin-top: 20px; padding: 10px; font-size: 14px; color: #666; background: #f4f4f4; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;'>")
                    .append("&copy; 2025 Công ty JobSeeker. Mọi quyền được bảo lưu.")
                    .append("</div>")
                    .append("</div>")
                    .append("</body></html>");

            // Cấu hình thông tin email
            helper.setFrom("jobseeker19886@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body.toString(), true); // true để gửi email dưới dạng HTML

            // Thêm ảnh vào email và gắn CID cho ảnh
            String cid1 = "image1";
            String cid2 = "image2";

            // Kiểm tra và gán các ảnh vào email
            if (imageFile1 != null && !imageFile1.isEmpty()) {
                helper.addInline(cid1, imageFile1, imageFile1.getContentType()); // Thêm ảnh 1
            }
            if (imageFile2 != null && !imageFile2.isEmpty()) {
                helper.addInline(cid2, imageFile2, imageFile2.getContentType()); // Thêm ảnh 2
            }

            // Gửi email
            javaMailSender.send(message);
            System.out.println("Support email sent to: " + toEmail + " successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public void sendEmail(String toEmail, int otpCode) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Tiêu đề email
            String subject = "🔐 Yêu cầu đặt lại mật khẩu - Mã OTP của bạn";

            // Nội dung email HTML
            String body = "<!DOCTYPE html>"
                    + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Khôi phục mật khẩu</title></head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                    + "<div style='width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                    + "<div style='background: #007bff; color: white; padding: 15px; font-size: 20px; border-top-left-radius: 10px; border-top-right-radius: 10px;'>"
                    + "🔐 Yêu cầu đặt lại mật khẩu"
                    + "</div>"
                    + "<div style='padding: 20px; color: #333; text-align: left;'>"
                    + "<p>Xin chào,</p>"
                    + "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình. Vui lòng sử dụng mã OTP dưới đây để tiếp tục quá trình đặt lại mật khẩu:</p>"
                    + "<div style='text-align: center; margin: 20px 0; font-size: 24px; font-weight: bold; color: #007bff; padding: 10px; border: 2px dashed #007bff; display: inline-block;'>"
                    + otpCode + "</div>"
                    + "<p>Để trở về đăng nhập, hãy nhấn vào nút bên dưới:</p>"
                    + "<div style='text-align: center;'>"
                    + "<a href='http://localhost:8080/login' style='display: inline-block; background: #007bff; color: white; padding: 10px 20px; text-decoration: none; font-size: 16px; border-radius: 5px;'>Quay Về Đăng Nhập</a>"
                    + "</div>"
                    + "<p>Trân trọng,<br>Đội ngũ hỗ trợ</p>"
                    + "</div>"
                    + "<div style='margin-top: 20px; padding: 10px; font-size: 14px; color: #666; background: #f4f4f4; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;'>"
                    + "&copy; 2025 Công ty XYZ. Mọi quyền được bảo lưu."
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            helper.setFrom("jobseeker19886@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
            System.out.println("Password reset email sent to: " + toEmail + " successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending password reset email: " + e.getMessage());
        }
    }

    public void sendAcceptCV(String toEmail, String seekerName, String companyName, String hrName) {
        String subject = "🎉 Chúc mừng " + seekerName + " đã trúng tuyển vào " + companyName + "!";
        String emailBody = "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Chúc mừng bạn đã trúng tuyển!</title></head>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                + "<div style='width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                + "<div style='background: #4CAF50; color: white; padding: 10px; font-size: 20px; border-top-left-radius: 10px; border-top-right-radius: 10px;'>"
                + "🎉 Chúc mừng bạn đã trúng tuyển! 🎉"
                + "</div>"
                + "<div style='padding: 20px; color: #333; text-align: left;'>"
                + "<h2 style='color: #4CAF50;'>Xin chào " + seekerName + ",</h2>"
                + "<p>Chúng tôi vui mừng thông báo rằng bạn đã xuất sắc vượt qua vòng tuyển dụng và chính thức được nhận vào làm việc tại <b>"
                + companyName + "</b>.</p>"
                + "<p>Chúng tôi đánh giá cao năng lực và sự nhiệt huyết của bạn. Thông tin chi tiết về công việc và quy trình tiếp theo sẽ được gửi trong email này.</p>"
                + "<h3>Thông tin công việc:</h3>"
                + "<ul>"
                + "<li><b>Vị trí:</b> [Tên Vị Trí]</li>"
                + "<li><b>Ngày bắt đầu:</b> [Ngày Nhận Việc]</li>"
                + "<li><b>Địa điểm làm việc:</b> [Địa Chỉ Công Ty]</li>"
                + "<li><b>Người liên hệ:</b> " + hrName + "</li>"
                + "</ul>"
                + "<p>Hãy xác nhận lại thông tin này bằng cách nhấn vào nút bên dưới:</p>"
                + "<a href='[Link Xác Nhận]' style='display: inline-block; background: #4CAF50; color: white; padding: 10px 20px; margin-top: 20px; text-decoration: none; font-size: 16px; border-radius: 5px;'>Xác Nhận Nhận Việc</a>"
                + "<p>Chúng tôi mong sớm được chào đón bạn gia nhập đội ngũ của chúng tôi! 🚀</p>"
                + "</div>"
                + "<div style='margin-top: 20px; padding: 10px; font-size: 14px; color: #666; background: #f4f4f4; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;'>"
                + "&copy; 2025 " + companyName + ". Mọi quyền được bảo lưu."
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("jobseeker19886@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(emailBody, true); // Tham số `true` để bật chế độ HTML

            javaMailSender.send(message);

            System.out.println("Mail sent to: " + toEmail + " successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public void sendFeedbackResponse(String toEmail, String userName, String feedbackMessage, String companyName,
            String supportEmail) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Tiêu đề email chuyên nghiệp
            String subject = "📩 Cảm ơn bạn đã gửi phản hồi tới " + companyName;

            // Nội dung email HTML
            String emailBody = "<!DOCTYPE html>"
                    + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Phản hồi của bạn đã được nhận</title></head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                    + "<div style='width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                    // Tiêu đề trong email
                    + "<div style='background: #007bff; color: white; padding: 15px; font-size: 20px; border-top-left-radius: 10px; border-top-right-radius: 10px;'>"
                    + "📩 Cảm ơn bạn đã gửi phản hồi!"
                    + "</div>"
                    // Nội dung chính của email
                    + "<div style='padding: 20px; color: #333; text-align: left;'>"
                    + "<p>Xin chào <b>" + userName + "</b>,</p>"
                    + "<p>Chúng tôi rất trân trọng phản hồi của bạn và luôn cố gắng không ngừng cải thiện dịch vụ của mình.</p>"
                    + "<p>Dưới đây là nội dung phản hồi bạn đã gửi:</p>"
                    // Hiển thị phản hồi của người dùng
                    + "<div style='background: #f8f9fa; padding: 15px; border-left: 5px solid #007bff; font-style: italic;'>"
                    + feedbackMessage
                    + "</div>"
                    + "<p>Đội ngũ hỗ trợ của chúng tôi sẽ xem xét phản hồi này và nếu cần thiết, chúng tôi sẽ liên hệ lại với bạn qua email.</p>"
                    + "<p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ chúng tôi qua email: "
                    + "<a href='mailto:" + supportEmail + "' style='color: #007bff; text-decoration: none;'>"
                    + supportEmail + "</a></p>"
                    + "<p>Trân trọng,</p>"
                    + "<p><b>Đội ngũ " + companyName + "</b></p>"
                    + "</div>"
                    // Chân trang email
                    + "<div style='margin-top: 20px; padding: 10px; font-size: 14px; color: #666; background: #f4f4f4; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;'>"
                    + "&copy; 2025 " + companyName + ". Mọi quyền được bảo lưu."
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            helper.setFrom(supportEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(emailBody, true); // Kích hoạt HTML

            javaMailSender.send(message);
            System.out.println("Feedback response email sent to: " + toEmail + " successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending feedback response email: " + e.getMessage());
        }
    }

    public void sendEmployerRegistrationEmail(String toEmail) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Tiêu đề email
            String subject = "📢 Xác nhận đăng ký nhà tuyển dụng";

            // Nội dung email HTML
            String body = "<!DOCTYPE html>"
                    + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Xác nhận đăng ký</title></head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                    + "<div style='width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                    + "<div style='background: #007bff; color: white; padding: 15px; font-size: 20px; border-top-left-radius: 10px; border-top-right-radius: 10px;'>"
                    + "📢 Xác nhận đăng ký nhà tuyển dụng"
                    + "</div>"
                    + "<div style='padding: 20px; color: #333; text-align: left;'>"
                    + "<p>Xin chào,</p>"
                    + "<p>Chúng tôi đã nhận được yêu cầu đăng ký tài khoản nhà tuyển dụng của bạn. Yêu cầu của bạn sẽ được kiểm duyệt trong thời gian sớm nhất.</p>"
                    + "<p>Hệ thống sẽ thông báo kết quả xét duyệt qua email này.</p>"
                    + "<p>Vui lòng kiểm tra email thường xuyên để cập nhật trạng thái đăng ký.</p>"
                    + "<div style='text-align: center; margin: 20px 0;'>"
                    + "</div>"
                    + "<p>Trân trọng,<br>Đội ngũ hỗ trợ</p>"
                    + "</div>"
                    + "<div style='margin-top: 20px; padding: 10px; font-size: 14px; color: #666; background: #f4f4f4; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;'>"
                    + "&copy; 2025 Công ty XYZ. Mọi quyền được bảo lưu."
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            helper.setFrom("jobseeker19886@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
            System.out.println("Employer registration email sent to: " + toEmail + " successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending employer registration email: " + e.getMessage());
        }
    }

    public void sendRecruiterApprovalEmail(String toEmail, String companyName, boolean isApproved) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Tiêu đề email
            String subject = isApproved
                    ? "🎉 Chúc mừng! Yêu cầu đăng ký nhà tuyển dụng của bạn đã được chấp nhận"
                    : "⛔ Thông báo về yêu cầu đăng ký nhà tuyển dụng";

            // Nội dung email HTML
            String body = "<!DOCTYPE html>"
                    + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Thông báo</title></head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                    + "<div style='width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                    + "<div style='background: " + (isApproved ? "#28a745" : "#dc3545")
                    + "; color: white; padding: 15px; font-size: 20px; border-top-left-radius: 10px; border-top-right-radius: 10px;'>"
                    + (isApproved ? "🎉 Chúc mừng!" : "⛔ Thông báo")
                    + "</div>"
                    + "<div style='padding: 20px; color: #333; text-align: left;'>"
                    + "<p>Xin chào,</p>";

            if (isApproved) {
                body += "<p>Chúng tôi rất vui thông báo rằng yêu cầu đăng ký làm nhà tuyển dụng của bạn đã được <strong>chấp nhận</strong>.</p>"
                        + "<p>Job Seeker mong rằng doanh nghiệp / công ty " + "<strong> " + companyName + "</strong>"
                        + " sẽ đạt nhiều thành tựu và phát triển trong thời gian sắp tới.</p>"
                        + "<p>Bạn có thể đăng nhập vào hệ thống ngay bây giờ để chỉnh sửa và bổ sung thông tin công ty của mình.</p>";
            } else {
                body += "<p>Chúng tôi rất tiếc phải thông báo rằng yêu cầu đăng ký nhà tuyển dụng của bạn <strong>không được chấp nhận</strong> vào lúc này.</p>"
                        + "<p>Job Seeker mong rằng doanh nghiệp / công ty " + "<strong> " + companyName + "</strong>"
                        + " sẽ có cơ hội hợp tác cùng nhau trong hành trình sắp tới.</p>"
                        + "<p>Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi.</p>";
            }

            body += "<p>Trân trọng,<br>Đội ngũ hỗ trợ</p>"
                    + "</div>"
                    + "<div style='margin-top: 20px; padding: 10px; font-size: 14px; color: #666; background: #f4f4f4; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;'>"
                    + "&copy; 2025 Công ty XYZ. Mọi quyền được bảo lưu."
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            helper.setFrom("jobseeker19886@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
            System.out.println("Recruiter approval email sent to: " + toEmail + " successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending recruiter approval email: " + e.getMessage());
        }
    }

    public void sendFollowerEmail(String toEmail, String subject, String header, String content, String companyName) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Nội dung email HTML
            String body = "<!DOCTYPE html>"
                    + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Thông báo mới</title></head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                    + "<div style='width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                    + "<div style='background: #007bff; color: white; padding: 15px; font-size: 20px; border-top-left-radius: 10px; border-top-right-radius: 10px;'>"
                    + header + "</div>"
                    + "<div style='padding: 20px; color: #333; text-align: left;'>"
                    + "<p>Xin chào, chúng tôi là <strong>" + companyName + "</strong>. </p>"
                    + "<p>" + content + "</p>"
                    + "<div style='text-align: center; margin-top: 20px;'>"
                    + "<a href='http://localhost:8080/login' style='display: inline-block; background: #007bff; color: white; padding: 10px 20px; text-decoration: none; font-size: 16px; border-radius: 5px;'>Truy cập hệ thống</a>"
                    + "</div>"
                    + "<p>Trân trọng,<br>Đội ngũ hỗ trợ</p>"
                    + "</div>"
                    + "<div style='margin-top: 20px; padding: 10px; font-size: 14px; color: #666; background: #f4f4f4; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;'>"
                    + "&copy; 2025 Công ty XYZ. Mọi quyền được bảo lưu."
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            helper.setFrom("jobseeker19886@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
            System.out.println("Email sent to: " + toEmail + " successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public void sendAfterApplyingCV(String toEmail, String fullName, String jobTitle, String companyName,
            String preferredLocation, String level) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Create the header and content dynamically
            String header = "Xác nhận đăng ký ứng tuyển";
            String content = "Cảm ơn bạn " + fullName + " đã gửi hồ sơ ứng tuyển cho vị trí " + jobTitle + " tại "
                    + companyName + ".<br>"
                    + "Chúng tôi đã nhận được CV của bạn và sẽ xem xét trong thời gian sớm nhất. Dưới đây là thông tin chi tiết về công việc bạn đã ứng tuyển:<br>"
                    + "<strong>Vị trí: </strong> " + jobTitle + "<br>"
                    + "<strong>Công ty: </strong> " + companyName + "<br>"
                    + "<strong>Địa điểm ưu tiên: </strong> " + preferredLocation + "<br>"
                    + "<strong>Trình độ: </strong> " + level + "<br><br>"
                    + "Chúng tôi sẽ liên hệ với bạn nếu bạn được chọn vào vòng tiếp theo.<br>";

            // Nội dung email HTML
            String body = "<!DOCTYPE html>"
                    + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Thông báo mới</title></head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                    + "<div style='width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                    + "<div style='background: #007bff; color: white; padding: 15px; font-size: 20px; border-top-left-radius: 10px; border-top-right-radius: 10px;'>"
                    + header + "</div>"
                    + "<div style='padding: 20px; color: #333; text-align: left;'>"
                    + "<p>Xin chào, " + fullName + "</p>"
                    + "<p>" + content + "</p>"
                    + "<div style='text-align: center; margin-top: 20px;'>"
                    + "<a href='http://localhost:8080/login' style='display: inline-block; background: #007bff; color: white; padding: 10px 20px; text-decoration: none; font-size: 16px; border-radius: 5px;'>Truy cập hệ thống</a>"
                    + "</div>"
                    + "<p>Trân trọng,<br>Đội ngũ hỗ trợ</p>"
                    + "</div>"
                    + "<div style='margin-top: 20px; padding: 10px; font-size: 14px; color: #666; background: #f4f4f4; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;'>"
                    + "&copy; 2025 Công ty XYZ. Mọi quyền được bảo lưu."
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            // Set email details
            helper.setFrom("jobseeker19886@gmail.com"); // Sender email address
            helper.setTo(toEmail); // Recipient email address
            helper.setSubject("Xác nhận đăng ký ứng tuyển"); // Subject of the email
            helper.setText(body, true); // Set email body as HTML content

            // Send the email
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public String getTemplateForAcceptingCV(String fullName, String jobTitle, String companyName,
            String preferredLocation, String level) {
        return "Cảm ơn bạn " + fullName + " đã gửi hồ sơ ứng tuyển cho vị trí " + jobTitle + " tại "
                + companyName + ".<br>"
                + "Dưới đây là thông tin chi tiết về công việc bạn đã ứng tuyển:<br>"
                + "<strong>Vị trí: </strong> " + jobTitle + "<br>"
                + "<strong>Công ty: </strong> " + companyName + "<br>"
                + "<strong>Địa điểm phỏng vấn: </strong> " + preferredLocation + "<br>"
                + "<strong>Trình độ: </strong> " + level + "<br><br>"
                + "Nếu có bất cứ thắc mắc, vui lòng liên hệ với chúng tôi.<br>";
    }

    public void sendResumeFeedback(EmailContentDTO emailContentDTO) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Create the header and content dynamically

            // Nội dung email HTML
            String body = "<!DOCTYPE html>"
                    + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Thông báo mới</title></head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                    + "<div style='width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                    + "<div style='background: #007bff; color: white; padding: 15px; font-size: 20px; border-top-left-radius: 10px; border-top-right-radius: 10px;'>"
                    + emailContentDTO.getHeader() + "</div>"
                    + "<div style='padding: 20px; color: #333; text-align: left;'>"
                    + "<p>" + emailContentDTO.getSubject() + "</p>"
                    + emailContentDTO.getContent()
                    + "<div style='text-align: center; margin-top: 20px;'>"
                    + "</div>"
                    + "<p>Trân trọng,<br>Đội ngũ hỗ trợ</p>"
                    + "</div>"
                    + "<div style='margin-top: 20px; padding: 10px; font-size: 14px; color: #666; background: #f4f4f4; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;'>"
                    + "&copy; 2025 Công ty XYZ. Mọi quyền được bảo lưu."
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            // Set email details
            helper.setFrom("jobseeker19886@gmail.com"); // Sender email address
            helper.setTo(emailContentDTO.getEmailTo()); // Recipient email address
            helper.setSubject(emailContentDTO.getSubject()); // Subject of the email
            helper.setText(body, true); // Set email body as HTML content

            // Send the email
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

}
