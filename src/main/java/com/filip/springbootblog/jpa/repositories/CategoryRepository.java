package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryValueIgnoreCase(String categoryValue);

    @Override
    List<Category> findAll(Sort sort);

    List<Category> findByIsActiveTrue(Sort sort);

    Category findByCategoryId(Long categoryId);

    /*
    @NamedNativeQueries({
        @NamedNativeQuery(
                name = "getCategoryCounts",
                query = "select count(*) as `categoryCount`, c.category_value, c.category_id, " +
                        "c.is_active, c.is_default from categories c " +
                        "inner join post_category_ids pc on c.category_id = pc.category_id " +
                        "inner join posts p on pc.post_id = p.post_id " +
                        " where p.is_published = true " +
                        "group by c.category_value order by categoryCount DESC;",
                resultClass = Category.class)
})
     */

    @Query(nativeQuery = true,
            value = "select count(*) as `categoryCount`, c.category_value, c.category_id, " +
                    "c.is_active, c.is_default from categories c " +
                    "inner join post_category_ids pc on c.category_id = pc.category_id " +
                    "inner join posts p on pc.post_id = p.post_id " +
                    " where p.is_published = true " +
                    "group by c.category_value order by categoryCount DESC"
    )
    List<Category> getCategoryCounts();
}
