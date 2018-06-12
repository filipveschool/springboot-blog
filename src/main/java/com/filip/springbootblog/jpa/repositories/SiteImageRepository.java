package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.SiteImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SiteImageRepository extends CrudRepository<SiteImage, Long> {

    SiteImage findBySiteImageId(Long id);

    SiteImage findByIsCurrentTrueAndDayOfYear(Integer dayofYear);

    Collection<SiteImage> findAll();

    Collection<SiteImage> findByBannerImageTrueAndIsActiveTrue();

    Collection<SiteImage> findByBannerImageTrue();
}
