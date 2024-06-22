package com.ez.pus.SendEmail.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ez.pus.SendEmail.bean.EmailData;
import com.ez.pus.SendEmail.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class EmailNotificationController {

	public final NotificationService notificationService;

	@PostMapping("/sendSimpleMail")
	public String sendSimpleMail(@RequestBody EmailData emailData) {
		return notificationService.sendSimpleMail(emailData);
	}

	@PostMapping("/sendMailWithAttachment")
	public String sendMailWithAttachment(@RequestBody EmailData emailData) {
		return notificationService.sendMailWithAttachment(emailData);
	}

	@PostMapping("/sendMailWithHTML_Body")
	public String sendMailWithHTML_Body(@RequestBody EmailData emailData) {
		return notificationService.sendMailWithHTML_Body(emailData);
	}

	@PostMapping("/sendMailWithDynamic_HTML_Body")
	public String sendMailWithDynamic_HTML_Body(@RequestBody EmailData emailData) {
		return notificationService.sendMailWithDynamic_HTML_Body(emailData);
	}
}