package com.jyhuang.java.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class EmailAuthenticator extends Authenticator {
    // 创建单例邮件账户信息
    private static EmailAuthenticator emailAuthenticator = new EmailAuthenticator();

    private EmailAuthenticator() {

    }

    static EmailAuthenticator createEmailAuthenticator() {
        return emailAuthenticator;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(EmailConfig.getUser(), EmailConfig.getPass());
    }
}
