package com.filip.springbootblog.jpa.common;

import com.filip.springbootblog.jpa.enums.UserRegistration;
import com.filip.springbootblog.jpa.models.SiteOption;
import com.filip.springbootblog.jpa.repositories.SiteOptionRepository;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

@Getter
@Setter
@Component
@DependsOn("databasePopulator")
public class SiteOptions {

    @Autowired
    private SiteOptionRepository siteOptionRepository;


    private String siteName;
    private String siteDescription;
    private Boolean addGoogleAnalytics;
    private String googleAnalyticsTrackingId;
    private UserRegistration userRegistration;

    @PostConstruct
    public void init() throws
            IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Collection<SiteOption> siteOptionKeyValues = siteOptionRepository.findAll();

        Map<String, Object> options = new Hashtable<>();
        for (SiteOption siteOption : siteOptionKeyValues) {
            options.put(siteOption.getName(), siteOption.getValue());
        }
        for (String key : options.keySet()) {
            for (Field f : this.getClass().getDeclaredFields()) {
                if (f.getName().toUpperCase().equals(key.toUpperCase())) {
                    setSiteOptionProperty(key, options.get(key));
                }
            }
        }
    }


    @Override
    public String toString() {
        return "SiteOptions{" +
                "siteName='" + siteName + '\'' +
                ", siteDescription='" + siteDescription + '\'' +
                ", addGoogleAnalytics=" + addGoogleAnalytics +
                ", googleAnalyticsTrackingId='" + googleAnalyticsTrackingId + '\'' +
                ", userRegistration=" + userRegistration +
                '}';
    }

    // endregion

    // region Utils

    private static final String OPTION_VALUE_TYPE_BOOLEAN = "Boolean";
    private static final String OPTION_VALUE_TYPE_INTEGER = "Integer";
    private static final String OPTION_VALUE_TYPE_USERREGISTRATION = "UserRegistration";

    public void setSiteOptionProperty(String property, Object value)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (PropertyUtils.isWriteable(this, property)) {

            switch (PropertyUtils
                    .getPropertyDescriptor(this, property).getPropertyType().getSimpleName()) {
                case OPTION_VALUE_TYPE_BOOLEAN:
                    value = Boolean.valueOf(value.toString());
                    break;
                case OPTION_VALUE_TYPE_INTEGER:
                    value = Integer.parseInt(value.toString());
                    break;
                case OPTION_VALUE_TYPE_USERREGISTRATION:
                    value = UserRegistration.valueOf(value.toString());
                    break;
                default:
                    break;
            }
            PropertyUtils.setProperty(this, property, value);
        }
    }

    // endregion
}
