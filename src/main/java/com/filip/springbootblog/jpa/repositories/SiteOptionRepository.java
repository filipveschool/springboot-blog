package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.SiteOption;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SiteOptionRepository extends CrudRepository<SiteOption, Long> {
    SiteOption findByNameIgnoreCase(String optionName);

    @Override
    Collection<SiteOption> findAll();

    SiteOption save(SiteOption siteOption);
}
