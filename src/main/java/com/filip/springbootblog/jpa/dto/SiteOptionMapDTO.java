package com.filip.springbootblog.jpa.dto;

import com.filip.springbootblog.jpa.enums.UserRegistration;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class SiteOptionMapDTO implements Serializable {

    @NotEmpty
    private String siteName;

    @NotEmpty
    private String siteDescription;

    @NotNull
    private Boolean addGoogleAnalytics;

    private String googleAnalyticsTrackingId;

    private UserRegistration userRegistration;

    public SiteOptionMapDTO() {

    }


    public static Builder withGeneralSettings(
            String siteName,
            String siteDescription,
            Boolean addGoogleAnalytics,
            String googleAnalyticsTrackingId,
            UserRegistration userRegistration) {

        return new Builder(siteName, siteDescription, addGoogleAnalytics, googleAnalyticsTrackingId, userRegistration);
    }

    public static class Builder {

        private SiteOptionMapDTO built;

        public Builder(String siteName, String siteDescription, Boolean addGoogleAnalytics, String googleAnalyticsTrackingId, UserRegistration userRegistration) {
            built = new SiteOptionMapDTO();
            built.siteName = siteName;
            built.siteDescription = siteDescription;
            built.addGoogleAnalytics = addGoogleAnalytics;
            built.googleAnalyticsTrackingId = googleAnalyticsTrackingId;
            built.userRegistration = userRegistration;
        }

        public SiteOptionMapDTO build() {
            return built;
        }
    }

}
