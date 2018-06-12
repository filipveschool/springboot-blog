package com.filip.springbootblog.jpa.services.interfaces;

import com.filip.springbootblog.jpa.dto.SiteOptionDTO;
import com.filip.springbootblog.jpa.models.SiteImage;
import com.filip.springbootblog.jpa.models.SiteOption;
import org.springframework.transaction.annotation.Transactional;

public interface ISiteService {

    @Transactional
    SiteImage getHomeBanner(long siteImageId);

    @Transactional
    SiteImage getHomeBanner();

    SiteOption update(SiteOptionDTO siteOptionDTO);

    @Transactional(readOnly = true)
    SiteOption findOptionByName(String name);
}
