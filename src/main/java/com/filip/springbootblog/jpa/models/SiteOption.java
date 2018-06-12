package com.filip.springbootblog.jpa.models;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "site_options")
public class SiteOption implements Serializable {

    private static final long serialVersionUID = 6690621866489266673L;

    @Id
    @Column(name = "option_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @Column(name = "option_name")
    @NotEmpty
    private String name;

    @Column(name = "option_value", columnDefinition = "TEXT")
    private String value;

    public SiteOption() {
        
    }

    public void update(final String optionName, final String optionValue) {
        this.name = optionName;
        this.value = optionValue;
    }
}
