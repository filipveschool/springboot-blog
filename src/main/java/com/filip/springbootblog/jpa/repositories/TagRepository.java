package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    Tag findByTagValueIgnoreCase(String tagValue);

    @Override
    Set<Tag> findAll();

    /*
    @NamedNativeQueries({
        @NamedNativeQuery(
                name = "getTagCloud",
                query = "select count(*) as `tagCount`, t.tag_value, t.tag_id from tags t " +
                        " inner join post_tag_ids pt on t.tag_id = pt.tag_id " +
                        " inner join posts p on pt.post_id = p.post_id " +
                        " where p.is_published = true " +
                        "group by t.tag_value order by tagCount DESC;",
                resultClass = Tag.class)
})
     */

    @Query(value = "select count(*) as `tagCount`, t.tag_value, t.tag_id from tags t " +
            " inner join post_tag_ids pt on t.tag_id = pt.tag_id " +
            " inner join posts p on pt.post_id = p.post_id " +
            " where p.is_published = true " +
            "group by t.tag_value order by tagCount DESC",
            nativeQuery = true)
    List<Tag> getTagCloud();
}
