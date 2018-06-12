package com.filip.springbootblog.mail.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;

@Configuration
@PropertySource("classpath:/mail.properties")
@Import({ApplicationConfig.class})
public class MailConfig {


//    @Bean
//    public FreeMarkerConfigurer freemarkerConfigurer() throws IOException {
//        FreeMarkerConfigurer result = new FreeMarkerConfigurer();
//        result.setTemplateLoaderPath("classpath:/freemarker/");
//        return result;
//    }

//    @Bean
//    public FreeMarkerViewResolver freemarkerViewResolver() {
//        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
//        resolver.setPrefix("");
//        resolver.setCache(false);
//        resolver.setOrder(3);
//        return resolver;
//    }

    @Bean
    public MessageSource mailMessageSource() {
        ResourceBundleMessageSource msgsource = new ResourceBundleMessageSource();
        msgsource.setBasename("mail-messages");
        return msgsource;
    }

}



