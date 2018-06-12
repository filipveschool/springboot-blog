package com.filip.springbootblog.mail.service.implementations;

import com.filip.springbootblog.jpa.common.ApplicationSettings;
import com.filip.springbootblog.jpa.models.Post;
import com.filip.springbootblog.jpa.models.PostMeta;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.mail.service.interfaces.IFmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Slf4j
@Service("fmService")
public class FmServiceImpl implements IFmService {

    @Autowired
    private ApplicationSettings applicationSettings;


    @Override
    public String displayTestTemplate(User user) {
        return null;
    }

    @Override
    public String getNoLinksMessage() {
        return null;
    }

    @Override
    public String getNoResultsMessage(String search) {
        return null;
    }

    @Override
    public String getNoLikesMessage() {
        return null;
    }

    @Override
    public String createRssPostContent(Post post) {
        return null;
    }

    @Override
    public String createPostHtml(Post post, String templateName) {
        return null;
    }

    @Override
    public String createPostHtml(Post post) {
        return null;
    }

    @Override
    public String createPostAtoZs() {
        return null;
    }

    @Override
    public String getNoMoreLikeThisMessage() {
        return null;
    }

    @Override
    public String getRobotsTxt() {
        return null;
    }

    @Override
    public String getFileUploadingScript() {
        return null;
    }

    @Override
    public String getFileUploadedScript() {
        return null;
    }

    @Override
    public String getTwitterTemplate(PostMeta postMeta) {
        return null;
    }
}
