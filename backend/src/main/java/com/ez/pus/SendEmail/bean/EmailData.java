package com.ez.pus.SendEmail.bean;

import java.util.*;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Component
@Getter
@Setter
@ToString
public class EmailData {

	private String receipient;
	private String mailBody;
	private String mailSubject;

	private List<String> attachments;
	private HashMap<String, String> dynamicValues;

	private String applicationName;
	private String templateName;
}