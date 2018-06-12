package com.filip.springbootblog.jpa.annotations;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@SuppressWarnings({"SpringElInspection", "ELValidationInJSP"})
@Caching(

        evict = {
                @CacheEvict(cacheNames = "posts", key = "#result.postId"),
                @CacheEvict(cacheNames = "posts", key = "#result.postName"),
                @CacheEvict(cacheNames = "pagedPosts",
                        allEntries = true,
                        beforeInvocation = true)
        }
)
@Target({ElementType.METHOD})
@Retention(RUNTIME)
public @interface CachePostUpdate {
}

