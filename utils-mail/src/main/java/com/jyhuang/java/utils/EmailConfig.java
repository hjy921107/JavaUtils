package com.jyhuang.java.utils;

import org.apache.log4j.Logger;

import java.util.Properties;

public class EmailConfig {
    private static final String MAIL_DEBUT = "mail.debug";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_HOST = "mail.host";
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_USER = "mail.user";
    private static final String MAIL_PASS = "mail.pass";
    private static final String MAIL_FROM = "mail.from";

    private static final Logger logger = Logger.getLogger(EmailConfig.class);

    //是否开启debug调试
    private static String debug;

    //发送服务器是否需要身份验证
    private static String auth;

    //发送邮件端口
    private static String port;

    //邮件服务器主机名
    private static String host;

    //发送邮件协议名称
    private static String protocol;

    //发送邮件用户名
    private static String user;

    //发送邮件邮箱密码
    private static String pass;

    //发送邮件发件人
    private static String from;

    //创建单例Session配置信息
    private static Properties sessionProperties = new Properties();

    //创建单例邮箱配置信息
    private static EmailConfig emailConfig = new EmailConfig();

    private EmailConfig() {
        try {
            EmailConfig.auth = PropsUtils.getProperties(EmailConfig.MAIL_SMTP_AUTH);
            EmailConfig.port = PropsUtils.getProperties(EmailConfig.MAIL_SMTP_PORT);
            EmailConfig.debug = PropsUtils.getProperties(EmailConfig.MAIL_DEBUT);
            EmailConfig.from = PropsUtils.getProperties(EmailConfig.MAIL_FROM);
            EmailConfig.host = PropsUtils.getProperties(EmailConfig.MAIL_HOST);
            EmailConfig.pass = PropsUtils.getProperties(EmailConfig.MAIL_PASS);
            EmailConfig.protocol = PropsUtils.getProperties(EmailConfig.MAIL_TRANSPORT_PROTOCOL);
            EmailConfig.user = PropsUtils.getProperties(EmailConfig.MAIL_USER);

            sessionProperties.setProperty(EmailConfig.MAIL_SMTP_AUTH, EmailConfig.auth);
            sessionProperties.setProperty(EmailConfig.MAIL_SMTP_PORT, EmailConfig.port);
            sessionProperties.setProperty(EmailConfig.MAIL_DEBUT, EmailConfig.debug);
            sessionProperties.setProperty(EmailConfig.MAIL_HOST, EmailConfig.host);
            sessionProperties
                    .setProperty(EmailConfig.MAIL_TRANSPORT_PROTOCOL, EmailConfig.protocol);
            sessionProperties.setProperty(EmailConfig.MAIL_USER, EmailConfig.user);
            sessionProperties.setProperty(EmailConfig.MAIL_PASS, EmailConfig.pass);

            // -------------------- 配置 SSL（可将下面的 prop 写入到配置文件） --------------------
            sessionProperties.put("mail.smtp.ssl.enable", "true"); // 开启 ssl（必须），注意端口
            // 下面配置二选一即可（对于使用纯配置（不引入 MailSSLSocketFactory），可以使用方法二）
            // 方法一：
            // MailSSLSocketFactory sf = new MailSSLSocketFactory();
            // 信任所有 host，或者指定 host
            // sf.setTrustAllHosts(true); or sf.setTrustedHosts(new String[] { "my-server" });
            // sessionProperties.put("mail.smtp.ssl.socketFactory", sf);
            // 方法二：
            sessionProperties.put("mail.smtp.ssl.checkserveridentity", true);
        } catch (Exception e) {
            logger.error("邮箱配置信息初始化异常", e);
        }
    }

    public static String getDebug() {
        return debug;
    }

    public static String getAuth() {
        return auth;
    }

    public static String getHost() {
        return host;
    }

    public static String getProtocol() {
        return protocol;
    }

    static String getUser() {
        return user;
    }

    static String getPass() {
        return pass;
    }

    static String getFrom() {
        return from;
    }

    public static EmailConfig createEmailConfig() {
        return emailConfig;
    }

    static Properties getSessionProperties() {
        return sessionProperties;
    }

    public static String getPort() {
        return port;
    }
}