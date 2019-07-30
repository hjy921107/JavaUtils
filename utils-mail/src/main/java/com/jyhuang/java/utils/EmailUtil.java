package com.jyhuang.java.utils;

import org.springframework.util.StringUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;


public class EmailUtil {

    public static void main(String[] args) {
        try {
            String title = "利比亚客机遭劫持6大疑问：劫机者怎样通过安检的(1)";
            String content = "当地时间23日，俄罗斯总统普京在莫斯科国际贸易中心举行年度记者会。记者会持续了近4个小时，普京一共回答了来自俄罗斯各个地区及全世界记者的47个问题。自2001年起，普京都会在每年12月中下旬举行年度记者会，这是他的第12次记者会。";
            // List<File> fileList = new ArrayList<File>();
            //fileList.add(new File("C:/Users/Rex/Desktop/log4j.properties"));
            //EmailUtil.sendEmail(title, content, "rex_test@yeah.net", "123@qq.com", "456@qq.com", fileList);
            EmailUtil.sendEmail(title, content, "joye.huang@analyticservice.net");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param subject 邮件标题
     * @param content 邮件内容
     * @param to      收件人（多个收件人用英文逗号“,”隔开）
     *
     * @throws Exception
     */
    private static void sendEmail(String subject, String content, String to) throws Exception {
        Message msg = createMessage(subject, content, to, null);
        // 连接邮件服务器、发送邮件
        Transport.send(msg);
    }

    /**
     * @param subject        邮件标题
     * @param content        邮件内容
     * @param to             收件人（多个收件人用英文逗号“,”隔开）
     * @param type
     * @param otherRecipient 抄送人或暗送人（多个抄送人或暗送人用英文逗号“,”隔开）
     *
     * @throws Exception
     */
    public static void sendEmail(String subject, String content, String to, RecipientType type,
            String otherRecipient) throws Exception {
        Message msg = createMessage(subject, content, to, type, otherRecipient, null);
        // 连接邮件服务器、发送邮件
        Transport.send(msg);
    }

    /**
     * @param subject 邮件标题
     * @param content 邮件内容
     * @param to      收件人（多个收件人用英文逗号“,”隔开）
     * @param cc      抄送人（多个抄送人用英文逗号“,”隔开）
     * @param bcc     暗送人（多个暗送人用英文逗号“,”隔开）
     *
     * @throws Exception
     */
    public static void sendEmail(String subject, String content, String to, String cc, String bcc)
            throws Exception {
        Message msg = createMessage(subject, content, to, cc, bcc, null);
        // 连接邮件服务器、发送邮件
        Transport.send(msg);
    }

    /**
     * @param subject  邮件标题
     * @param content  邮件内容
     * @param to       收件人（多个收件人用英文逗号“,”隔开）
     * @param fileList 附件
     *
     * @throws Exception
     */
    public static void sendEmail(String subject, String content, String to, List<File> fileList)
            throws Exception {
        Message msg = createMessage(subject, content, to, fileList);
        // 连接邮件服务器、发送邮件
        Transport.send(msg);
    }

    /**
     * @param subject        邮件标题
     * @param content        邮件内容
     * @param to             收件人（多个收件人用英文逗号“,”隔开）
     * @param type
     * @param otherRecipient 抄送人或暗送人（多个抄送人或暗送人用英文逗号“,”隔开）
     * @param fileList       附件
     *
     * @throws Exception
     */
    public static void sendEmail(String subject, String content, String to, RecipientType type,
            String otherRecipient, List<File> fileList) throws Exception {
        Message msg = createMessage(subject, content, to, type, otherRecipient, fileList);
        // 连接邮件服务器、发送邮件
        Transport.send(msg);
    }

    /**
     * @param subject  邮件标题
     * @param content  邮件内容
     * @param to       收件人（多个收件人用英文逗号“,”隔开）
     * @param cc       抄送人（多个抄送人用英文逗号“,”隔开）
     * @param bcc      暗送人（多个暗送人用英文逗号“,”隔开）
     * @param fileList 附件
     *
     * @throws Exception
     */
    public static void sendEmail(String subject, String content, String to, String cc, String bcc,
            List<File> fileList) throws Exception {
        Message msg = createMessage(subject, content, to, cc, bcc, fileList);
        // 连接邮件服务器、发送邮件
        Transport.send(msg);
    }

    /**
     * @param subject  邮件标题
     * @param content  邮件内容
     * @param to       收件人（多个收件人用英文逗号“,”隔开）
     * @param cc       抄送人（多个抄送人用英文逗号“,”隔开）
     * @param bcc      暗送人（多个暗送人用英文逗号“,”隔开）
     * @param fileList 附件
     *
     * @return 邮箱对象
     *
     * @throws Exception
     */
    private static Message createMessage(String subject, String content, String to, String cc,
            String bcc, List<File> fileList) throws Exception {
        Message msg = createMessage(subject, content, to, RecipientType.CC, cc, fileList);
        msg.setRecipients(RecipientType.BCC, InternetAddress.parse(bcc));
        msg.setSentDate(new Date());     //设置信件头的发送日期

        return msg;
    }

    /**
     * @param subject        邮件标题
     * @param content        邮件内容
     * @param to             收件人（多个收件人用英文逗号“,”隔开）
     * @param otherRecipient 抄送人或暗送人（多个抄送人或暗送人用英文逗号“,”隔开）
     * @param fileList       附件
     *
     * @return 邮箱对象
     *
     * @throws Exception
     */
    private static Message createMessage(String subject, String content, String to,
            RecipientType type, String otherRecipient, List<File> fileList) throws Exception {
        Message msg = createMessage(subject, content, to, fileList);
        msg.setRecipients(type, InternetAddress.parse(otherRecipient));

        return msg;
    }

    /**
     * @param subject  邮件标题
     * @param content  邮件内容
     * @param to       收件人（多个收件人用英文逗号“,”隔开）
     * @param fileList 附件
     *
     * @return 邮箱对象
     *
     * @throws Exception
     */
    private static Message createMessage(String subject, String content, String to,
            List<File> fileList) throws Exception {
        checkEmail(subject, content, fileList);
        //邮件内容
        Multipart mp = createMultipart(content, fileList);
        Message msg = new MimeMessage(createSession());
        msg.setFrom(new InternetAddress(EmailConfig.getFrom()));
        msg.setSubject(subject);
        msg.setRecipients(RecipientType.TO, InternetAddress.parse(to));
        msg.setContent(mp); //Multipart加入到信件  
        msg.setSentDate(new Date());     //设置信件头的发送日期

        return msg;
    }

    /**
     * @param content  邮件正文内容
     * @param fileList 附件
     *
     * @return 邮件内容对象
     *
     * @throws MessagingException
     * @throws UnsupportedEncodingException Multipart
     */
    private static Multipart createMultipart(String content, List<File> fileList)
            throws MessagingException, UnsupportedEncodingException {
        //邮件内容
        Multipart mp = new MimeMultipart();
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setContent(content, "text/html;charset=gb2312");
        mp.addBodyPart(mbp);

        if (fileList != null && fileList.size() > 0) {
            //附件
            FileDataSource fds;
            for (File file : fileList) {
                mbp = new MimeBodyPart();
                fds = new FileDataSource(file);//得到数据源  
                mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
                mbp.setFileName(MimeUtility.encodeText(file.getName()));  //得到文件名同样至入BodyPart  
                mp.addBodyPart(mbp);
            }
        }

        return mp;
    }

    /**
     * @param subject  邮件标题
     * @param content  邮件正文
     * @param fileList 邮件附件
     *                 void
     *
     * @throws Exception
     */
    private static void checkEmail(String subject, String content, List<File> fileList)
            throws Exception {
        if (StringUtils.isEmpty(subject)) {
            throw new Exception("邮件标题不能为空");
        }

        if (StringUtils.isEmpty(content) && (fileList == null || fileList.size() == 0)) {
            throw new Exception("邮件内容不能为空");
        }
    }

    /**
     * @return Session
     */
    private static Session createSession() {
        return Session.getDefaultInstance(EmailConfig.getSessionProperties(),
                EmailAuthenticator.createEmailAuthenticator());
    }
}