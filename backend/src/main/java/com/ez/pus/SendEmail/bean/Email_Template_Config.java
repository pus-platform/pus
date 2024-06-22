package com.ez.pus.SendEmail.bean;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
@Table
public class Email_Template_Config {

    @Id
    private String id;

    @Column
    private String applicationName;

    @Column
    private String templateName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String dynamic_Mail_Body;

}
