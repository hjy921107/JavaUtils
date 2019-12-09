package com.jyhuang.java.utils;

import org.apache.log4j.Logger;

import java.util.Properties;

public class EmailConfig {
    private static final String MAIL_DEBUT = "mail.debug";
    private static final String MAIL_AUTH = "mail.smtp.auth";
    private static final String MAIL_PORT = "mail.smtp.port";
    private static final String MAIL_HOST = "mail.host";
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_SSL_IS_OPEN = "mail.ssl.isOpen";
    private static final String MAIL_ENCRYPTION_METHOD = "mail.encryption.method";
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

    //邮件是否开启 SSL 加密
    private static String sslIsOpen;

    //邮件加密方式
    private static String encryptMethod;

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
            EmailConfig.auth = PropsUtils.getProperties(EmailConfig.MAIL_AUTH);
            EmailConfig.port = PropsUtils.getProperties(EmailConfig.MAIL_PORT);
            EmailConfig.debug = PropsUtils.getProperties(EmailConfig.MAIL_DEBUT);
            EmailConfig.from = PropsUtils.getProperties(EmailConfig.MAIL_FROM);
            EmailConfig.host = PropsUtils.getProperties(EmailConfig.MAIL_HOST);
            EmailConfig.pass = PropsUtils.getProperties(EmailConfig.MAIL_PASS);
            EmailConfig.protocol = PropsUtils.getProperties(EmailConfig.MAIL_TRANSPORT_PROTOCOL);
            EmailConfig.sslIsOpen = PropsUtils.getProperties(EmailConfig.MAIL_SSL_IS_OPEN);
            EmailConfig.encryptMethod = PropsUtils
                    .getProperties(EmailConfig.MAIL_ENCRYPTION_METHOD);
            EmailConfig.user = PropsUtils.getProperties(EmailConfig.MAIL_USER);

            sessionProperties.setProperty(EmailConfig.MAIL_AUTH, EmailConfig.auth);
            sessionProperties.setProperty(EmailConfig.MAIL_PORT, EmailConfig.port);
            sessionProperties.setProperty(EmailConfig.MAIL_DEBUT, EmailConfig.debug);
            sessionProperties.setProperty(EmailConfig.MAIL_HOST, EmailConfig.host);
            sessionProperties
                    .setProperty(EmailConfig.MAIL_TRANSPORT_PROTOCOL, EmailConfig.protocol);
            // sessionProperties
            //         .setProperty(EmailConfig.MAIL_ENCRYPTION_METHOD, EmailConfig.encryptMethod);
            sessionProperties.setProperty(EmailConfig.MAIL_USER, EmailConfig.user);
            sessionProperties.setProperty(EmailConfig.MAIL_PASS, EmailConfig.pass);

            if ("1".equals(EmailConfig.sslIsOpen)) { // 开启 SSL 加密
                // --------------------------------- 配置 SSL/TLS ----------------------------------
                if ("SSL".equals(EmailConfig.encryptMethod)) { // 开启 ssl 必须配置，注意对应端口配置
                    sessionProperties.put("mail.smtp.ssl.enable", "true");
                } else if ("TLS".equals(EmailConfig.encryptMethod)) { // 开启 tls 必须配置，注意对应端口配置
                    sessionProperties.put("mail.smtp.starttls.enable", "true");
                    // sessionProperties.put("mail.smtp.socketFactory.fallback", "true");
                } else {
                    logger.error("邮箱加密方式配置不正确，目前仅支持 SSL、TLS，请确认！");
                    throw new Exception("邮箱加密方式配置不正确，目前仅支持 SSL、TLS，请确认！");
                }

                // 下面配置三选一即可（对于使用纯配置（不引入 MailSSLSocketFactory 对象），可以使用方法二、三）

                // 方法一：
                // MailSSLSocketFactory sf = new MailSSLSocketFactory();

                // 信任所有 host
                // sf.setTrustAllHosts(true);
                // 或者指定 host
                // sf.setTrustedHosts(new String[] { "my-server" });

                // sessionProperties.put("mail.smtp.ssl.socketFactory", sf);

                // 方法二：
                // 与方法一类似，直接使用类名配置 socketFactory
                // sessionProperties.put("mail.smtp.ssl.socketFactory", "javax.net.ssl.SSLSocketFactory");

                // 方法三：
                sessionProperties.put("mail.smtp.ssl.checkserveridentity", true);
            } else { // 非 SSL 加密
                // ---------------------------------- NO SSL/TLS ---------------------------------------
                // 无须配置上面关于 SSL/TSL 部分内容
            }
        } catch (Exception e) {
            logger.error("邮箱配置信息初始化异常", e);
            throw new RuntimeException("邮箱配置信息初始化异常");
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