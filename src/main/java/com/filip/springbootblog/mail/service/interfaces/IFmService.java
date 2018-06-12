package com.filip.springbootblog.mail.service.interfaces;

import com.filip.springbootblog.jpa.models.Post;
import com.filip.springbootblog.jpa.models.PostMeta;
import com.filip.springbootblog.jpa.models.User;

public interface IFmService {

    String displayTestTemplate(User user);

    String getNoLinksMessage();

    String getNoResultsMessage(String search);

    String getNoLikesMessage();

    String createRssPostContent(Post post);

    String createPostHtml(Post post, String templateName);

    String createPostHtml(Post post);

    String createPostAtoZs();

    String getNoMoreLikeThisMessage();

    String getRobotsTxt();

    String getFileUploadingScript();

    String getFileUploadedScript();

    String getTwitterTemplate(PostMeta postMeta);
}
