package com.mpnp.baechelin.tag.repository;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Modifying
    @Query("delete from Tag t where t.reviewId= :review")
    void deleteAllByReviewId(@Param("review") Review review);

    List<Tag> findAllByReviewId(Review review);

}
