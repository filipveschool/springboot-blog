package com.filip.springbootblog.jpa.services.implementations;

import com.filip.springbootblog.jpa.dto.SiteOptionDTO;
import com.filip.springbootblog.jpa.models.SiteImage;
import com.filip.springbootblog.jpa.models.SiteOption;
import com.filip.springbootblog.jpa.repositories.SiteOptionRepository;
import com.filip.springbootblog.jpa.services.interfaces.ISiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("siteService")
public class SiteServiceImpl implements ISiteService {

    @Autowired
    private SiteOptionRepository siteOptionRepository;

    //@Autowired
    //private SiteOptions siteOptions;

    @Override
    public SiteImage getHomeBanner(long siteImageId) {
        return null;
    }

    @Override
    public SiteImage getHomeBanner() {
        return null;
    }

    @Override
    public SiteOption update(SiteOptionDTO siteOptionDTO) {
        return null;
    }

    @Override
    public SiteOption findOptionByName(String name) {
        return null;
    }
}
