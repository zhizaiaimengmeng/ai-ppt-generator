package com.pptai.service;

import com.pptai.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend-url: http://localhost:3000}")
    private String frontendUrl;
    
    @Async
    public void sendVerificationEmail(User user) {
        try {
            String subject = "验证您的邮箱 - AI PPT Generator";
            String verificationUrl = frontendUrl + "/auth/verify-email?token=" + user.getVerificationToken();
            
            String htmlContent = createVerificationEmailHtml(user.getNickname(), verificationUrl);
            
            sendEmail(user.getEmail(), subject, htmlContent);
            
            log.info("验证邮件已发送到：{}", user.getEmail());
        } catch (Exception e) {
            log.error("发送验证邮件失败：{}", e.getMessage());
        }
    }
    
    @Async
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            String subject = "重置密码 - AI PPT Generator";
            String resetUrl = frontendUrl + "/auth/reset-password?token=" + resetToken;
            
            String htmlContent = createPasswordResetEmailHtml(user.getNickname(), resetUrl);
            
            sendEmail(user.getEmail(), subject, htmlContent);
            
            log.info("密码重置邮件已发送到：{}", user.getEmail());
        } catch (Exception e) {
            log.error("发送密码重置邮件失败：{}", e.getMessage());
        }
    }
    
    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    
    private String createVerificationEmailHtml(String nickname, String verificationUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                              color: white; padding: 30px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .button { display: inline-block; padding: 12px 30px; background: #409eff; 
                              color: white; text-decoration: none; border-radius: 4px; margin-top: 20px; }
                    .footer { text-align: center; padding: 20px; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>欢迎使用 AI PPT Generator</h1>
                    </div>
                    <div class="content">
                        <p>亲爱的 %s，</p>
                        <p>感谢您注册 AI PPT Generator！请点击下方按钮验证您的邮箱：</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">验证邮箱</a>
                        </p>
                        <p>如果按钮无法点击，请复制以下链接到浏览器：</p>
                        <p style="word-break: break-all; color: #409eff;">%s</p>
                        <p>此验证链接将在 24 小时后失效。</p>
                        <p>如果您没有注册此账号，请忽略此邮件。</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 AI PPT Generator. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, nickname, verificationUrl, verificationUrl);
    }
    
    private String createPasswordResetEmailHtml(String nickname, String resetUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                              color: white; padding: 30px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .button { display: inline-block; padding: 12px 30px; background: #f56c6c; 
                              color: white; text-decoration: none; border-radius: 4px; margin-top: 20px; }
                    .warning { background: #fef0f0; border: 1px solid #fde2e2; padding: 15px; 
                               border-radius: 4px; margin-top: 20px; }
                    .footer { text-align: center; padding: 20px; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>密码重置请求</h1>
                    </div>
                    <div class="content">
                        <p>亲爱的 %s，</p>
                        <p>我们收到您重置密码的请求。请点击下方按钮重置密码：</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">重置密码</a>
                        </p>
                        <p>如果按钮无法点击，请复制以下链接到浏览器：</p>
                        <p style="word-break: break-all; color: #f56c6c;">%s</p>
                        <div class="warning">
                            <p><strong>安全提示：</strong></p>
                            <ul>
                                <li>此链接将在 24 小时后失效</li>
                                <li>如果您没有请求重置密码，请忽略此邮件</li>
                                <li>不要将此链接分享给任何人</li>
                            </ul>
                        </div>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 AI PPT Generator. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, nickname, resetUrl, resetUrl);
    }
}
