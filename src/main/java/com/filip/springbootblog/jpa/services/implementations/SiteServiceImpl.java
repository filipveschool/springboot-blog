package com.filip.springbootblog.jpa.services.implementations;

import com.filip.springbootblog.jpa.common.SiteOptions;
import com.filip.springbootblog.jpa.dto.SiteOptionDTO;
import com.filip.springbootblog.jpa.exceptions.SiteOptionNotFoundException;
import com.filip.springbootblog.jpa.models.SiteImage;
import com.filip.springbootblog.jpa.models.SiteOption;
import com.filip.springbootblog.jpa.repositories.SiteImageRepository;
import com.filip.springbootblog.jpa.repositories.SiteOptionRepository;
import com.filip.springbootblog.jpa.services.interfaces.ISiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service("siteService")
public class SiteServiceImpl implements ISiteService {

    @Autowired
    private SiteOptionRepository siteOptionRepository;

    @Autowired
    private SiteOptions siteOptions;

    @Autowired
    private SiteImageRepository siteImageRepository;

    /**
     * <p>Retrieves Home Page Banner based on random selection from Active Banners
     * in table <strong>site_images</strong>.</p>
     *
     * <p>Used when number of active banners is greater than the number of days in the month.</p>
     *
     * @return SiteImage object
     */
    @Override
    public SiteImage getHomeBanner(long siteImageId) {
        return siteImageRepository.findBySiteImageId(siteImageId);
    }


    private SiteImage getNewCurrentSiteImage() {
        Collection<SiteImage> siteImages = siteImageRepository.findByBannerImageTrueAndIsActiveTrue();
        int activeBannerCount = siteImages.size();
        int randomNum = ThreadLocalRandom.current().nextInt(0, activeBannerCount);
        SiteImage siteImage = (SiteImage) siteImages.toArray()[randomNum];
        siteImage.setIsCurrent(true);
        siteImageRepository.save(siteImage);
        return siteImage;
    }

    private void resetCurrentSiteImage(int dayOfYear) {
        Collection<SiteImage> all = siteImageRepository.findAll();
        all.forEach(a -> {
            a.setDayOfYear(dayOfYear);
            a.setIsCurrent(false);
        });
        siteImageRepository.saveAll(all);
    }

    @Override
    public SiteImage getHomeBanner() {
        int dayOfYear = LocalDateTime.now().getDayOfYear();

        SiteImage siteImage = siteImageRepository.findByIsCurrentTrueAndDayOfYear(dayOfYear);
        if (siteImage == null) {
            resetCurrentSiteImage(dayOfYear);
            siteImage = getNewCurrentSiteImage();
        }
        return siteImage;
    }

    @Override
    public SiteOption update(SiteOptionDTO siteOptionDTO) {
        log.debug("Updating siteOption property {} with value: {}",
                siteOptionDTO.getName(), siteOptionDTO.getValue());

        SiteOption found = findOptionByName(siteOptionDTO.getName());
        found.update(siteOptionDTO.getName(), siteOptionDTO.getValue());

        try {
            siteOptions.setSiteOptionProperty(siteOptionDTO.getName(), siteOptionDTO.getValue());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error updating SiteOption Properties " + e.getMessage());
        }
        return found;
    }

    @Override
    public SiteOption findOptionByName(String name) {

        log.debug("Finding siteOption property with name: {}", name);
        SiteOption found = siteOptionRepository.findByNameIgnoreCase(name);

        if (found == null) {
            log.debug("No siteOption property with name: {}", name);
            throw new SiteOptionNotFoundException("No siteOption with property name: " + name);
        }

        return found;
    }


    public Sort sortBySiteImageIdAsc() {
        return new Sort(Sort.Direction.ASC, "siteImageId");
    }

}
