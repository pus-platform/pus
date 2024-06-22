package com.ez.pus.SendEmail.service;

import com.ez.pus.SendEmail.bean.EmailData;

public interface NotificationService {

	public String sendSimpleMail(EmailData emailData);

	public String sendMailWithAttachment(EmailData emailData);

	public String sendMailWithHTML_Body(EmailData emailData);

	public String sendMailWithDynamic_HTML_Body(EmailData emailData);

	default void sendNotification(String to, String subject, String message) {
        EmailData emailData = new EmailData();
        emailData.setReceipient(to);
        emailData.setMailSubject(subject);
        emailData.setMailBody(message);
        sendSimpleMail(emailData);
    }
}