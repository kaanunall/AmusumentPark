package application;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private static final String FROM_EMAIL = "**";
    private static final String APP_PASSWORD = "**";
    
    public boolean sendPasswordResetEmail(String toEmail, String verificationCode) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Luna Park - Şifre Sıfırlama");
            
            // HTML içerik
            String htmlContent = String.format("""
                <html>
                <body>
                    <h2>Şifre Sıfırlama Talebi</h2>
                    <p>Sayın Kullanıcımız,</p>
                    <p>Şifre sıfırlama talebiniz alınmıştır.</p>
                    <p><strong>Doğrulama Kodunuz: %s</strong></p>
                    <p>Bu kod 10 dakika süreyle geçerlidir.</p>
                    <p>Eğer bu talebi siz yapmadıysanız, lütfen dikkate almayınız.</p>
                    <br>
                    <p>Saygılarımızla,<br>Luna Park Ekibi</p>
                </body>
                </html>
                """, verificationCode);

            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String generateVerificationCode() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
} 