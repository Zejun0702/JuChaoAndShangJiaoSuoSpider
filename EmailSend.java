import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailSend {

    private static String receiverOne = "";
    private static String receiverTwo = "";
    private static String subjectContent = "";
    private static String emailContent = "";
    public EmailSend(String[] receiverList,String subject,String email){
        receiverOne = receiverList[0];
        receiverTwo = receiverList[1];
        emailContent = email;
        subjectContent = subject;

    }
    public void send (){
            String from = "bzj0702@gmail.com";
            String host = "smtp.gmail.com";
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("bzj0702@gmail.com", "hafewdmrdvpolzcw");
                }

            });
            session.setDebug(true);
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverOne));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverTwo));
                String subject = MimeUtility.encodeWord(subjectContent, "UTF-8", "Q");
                message.setSubject(subject);
                message.setContent(emailContent,"text/html;charset=gb2312");
                System.out.println("sending...");
                Transport.send(message);
                System.out.println("Sent message successfully....");
            } catch (MessagingException mex) {
                mex.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
