package com.filip.springbootblog.jpa.utils;

import com.filip.springbootblog.jpa.dto.CategoryDTO;
import com.filip.springbootblog.jpa.dto.PostDTO;
import com.filip.springbootblog.jpa.dto.TagDTO;
import com.filip.springbootblog.jpa.models.Category;
import com.filip.springbootblog.jpa.models.CurrentUser;
import com.filip.springbootblog.jpa.models.Post;
import com.filip.springbootblog.jpa.models.Tag;
import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class PostUtils {

    private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");


    public static Post postDtoToPost(PostDTO dto) {

        return Post.getBuilder(dto.getUserId(),
                dto.getPostTitle(),
                dto.getPostName(),
                dto.getPostLink(),
                dto.getPostContent(),
                dto.getPostType(),
                dto.getDisplayType())
                .isPublished(dto.getIsPublished())
                .postSource(dto.getPostSource())
                .postImage(dto.getPostImage())
                .build();
    }

    //    public static Post postDtoToSolrPost(PostDTO dto) {
//        Post post = postDtoToPost(dto);
//        post.setTags(tagsDTOsToTags(dto.getTags()));
//        return post;
//    }


    public static PostDTO postToPostDTO(Post post) {

        return PostDTO.getBuilder(post.getUserId(),
                post.getPostTitle(),
                post.getPostName(),
                post.getPostLink(),
                post.getPostContent(),
                post.getPostType(),
                post.getDisplayType(),
                post.getCategory().getCategoryId(), post.getPostMeta().getTwitterCardType())
                .isPublished(post.getIsPublished())
                .postSource(post.getPostSource())
                .postImage(post.getPostImage())
                .postId(post.getPostId())
                .tags(tagsToTagDTOs(post.getTags()))
                .build();
    }

    public static String createPostSource(String url) {
        String domain = null;
        if (StringUtils.isEmpty(url) || url.equals("NA"))
            return null;
        else {
            try {
                URL linkURL = new URL(url);
                domain = linkURL.getHost();
            } catch (MalformedURLException e) {
                log.error("Malformed Url: " + e.getMessage());
            }
        }
        return domain;
    }

    public static String createSlug(String title) {
        Slugify slugify;
        String slug = null;
        try {
            slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            log.error(String.format("IOException for title: %s -- Exception: %s", title, e.getMessage()));
        }
        return slug;
    }

    public static String removeTags(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        Matcher m = REMOVE_TAGS.matcher(string);
        return m.replaceAll("");
    }

    public static Boolean isPostOwner(CurrentUser currentUser, Long userId) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getId().equals(userId);
    }

    public static String formatPostContent(Post post) {
        String content = post.getPostContent();
        String imageHtml = "<img alt=\"\" src=\"%s\"  class=\"%s-image\"/>\n";
        String thumbnail = String.format(imageHtml, post.getPostImage(), "thumbnail");
        String feature = String.format(imageHtml, post.getPostImage(), "feature");

        switch (post.getDisplayType()) {
            case LINK_SUMMARY:
                content = StringUtils.prependIfMissing(content, thumbnail);
                break;
            case LINK_FEATURE:
                content = StringUtils.appendIfMissing(content, feature);
                break;
            case LINK:
                break;
        }
        return content;
    }

    public static Set<TagDTO> tagsToTagDTOs(Set<Tag> tags) {
        Set<TagDTO> tagDTOs = new LinkedHashSet<>();
        for (Tag tag : tags) {
            tagDTOs.add(new TagDTO(tag.getTagId(), tag.getTagValue()));
        }
        return tagDTOs;
    }

    public static CategoryDTO categoryToCategoryDTO(Category category) {
        return new CategoryDTO(category.getCategoryId(), category.getCategoryValue(), category.getCategoryCount(),
                category.getIsActive(), category.getIsDefault());
    }

    public static List<CategoryDTO> categoriesToCategoryDTOs(List<Category> categories) {
        List<CategoryDTO> categoryDTOS = categories.stream().map(category -> new CategoryDTO(
                category.getCategoryId(), category.getCategoryValue(), category.getCategoryCount(),
                category.getIsActive(), category.getIsDefault())).collect(Collectors.toList());
        return categoryDTOS;
    }

    public static Set<Tag> tagsDTOsToTags(Set<TagDTO> tagDTOs) {
        Set<Tag> tags = tagDTOs.stream().map(tagDTO -> new Tag(tagDTO.getTagId(), tagDTO.getTagValue())).collect(Collectors.toCollection(LinkedHashSet::new));
        return tags;
    }

    public static List<String> tagsToTagValues(Set<Tag> tags) {
        List<String> tagValues = tags.stream().map(Tag::getTagValue).collect(Collectors.toList());
        return tagValues;
    }


    // region display content

    public static void printPosts(List<Post> posts) {
        for (Post post :
                posts) {
            System.out.println(post.getPostTitle()
                    + "\n" + post.getPostContent() + " : " + post.getPostType() + "\n------------------------");
        }
    }

    // endregion
}
