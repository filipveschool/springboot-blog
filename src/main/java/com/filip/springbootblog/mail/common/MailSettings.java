package com.filip.springbootblog.mail.common;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@PropertySource("file:${mail.properties.file.path}mail.properties")
@ConfigurationProperties(prefix = "mail")
public class MailSettings {

    private String serverHost;
    private Integer serverPort;
    private String serverUsername;
    private String serverPassword;

    private Boolean smtpAuth;
    private Boolean smtpStartTlsEnable;

    private String contactTo;
    private String contactCC;
    private Boolean sendContactCC;

    private String developerTo;
}
