package V1Learn.spring.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {

    JavaMailSender mailSender;
    SpringTemplateEngine templateEngine;
    RedisService redisService;

    @Value("${spring.mail.from}")
    @NonFinal
    String emailFrom;

    @Value("${endpoint.confirmUser}")
    @NonFinal
    String apiConfirmUser;


    public String sendEmail(String recipients, String subject, String content, MultipartFile[] files) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage(); // khởi tạo đối tượng email có hỗ trợ HTML, đính kèm tệp.
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name()); //thao tác
        helper.setFrom(emailFrom, "Quang Vinh"); // email người gửi, tên hiển thi

        if (recipients.contains(",")) { // send to multiple users
            helper.setTo(InternetAddress.parse(recipients));
        } else { // send to single user
            helper.setTo(recipients);
        }

        // Send attach files
        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);

        log.info("Email has sent to successfully, recipients: {}", recipients);

        return "Sent";
    }

    public void sendConfirmLink(String emailTo,  String verifyCode) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending confirming link to user, email={}", emailTo);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();


        Map<String, Object> properties = new HashMap<>();
        properties.put("verifyCode", verifyCode);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Quang Vinh");
        helper.setTo(emailTo);
        helper.setSubject("Your Verification Code");
        String html = templateEngine.process("confirm-email", context);
        helper.setText(html, true);

        mailSender.send(message);

    }
    
    public void sendPaymentReminder(String emailTo, String courseTitle) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending payment reminder email to {}", emailTo);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("courseTitle", courseTitle);
        String html = templateEngine.process("payment-reminder", context); // file payment-reminder.html

        helper.setFrom(emailFrom, "Quang Vinh");
        helper.setTo(emailTo);
        helper.setSubject("Nhắc nhở thanh toán khóa học: " + courseTitle);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Sent payment reminder to {}", emailTo);
    }


    public String generateVerifyCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));  // Tạo mã 6 chữ số ngẫu nhiên
    }
}

