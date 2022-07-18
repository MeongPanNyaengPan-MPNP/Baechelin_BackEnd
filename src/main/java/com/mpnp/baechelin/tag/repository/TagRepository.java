package com.mpnp.baechelin.tag.repository;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    void deleteAllByReviewId(Review review);

    List<Tag> findAllByReviewId(Review review);

    void deleteAByReviewId(Review review);
}
