package com.tazedaily.repository;

import com.tazedaily.domain.PostComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PostComment entity.
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    @Query("select postComment from PostComment postComment where postComment.user.login = ?#{principal.username}")
    List<PostComment> findByUserIsCurrentUser();

    default Optional<PostComment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PostComment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PostComment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct postComment from PostComment postComment left join fetch postComment.user",
        countQuery = "select count(distinct postComment) from PostComment postComment"
    )
    Page<PostComment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct postComment from PostComment postComment left join fetch postComment.user")
    List<PostComment> findAllWithToOneRelationships();

    @Query("select postComment from PostComment postComment left join fetch postComment.user where postComment.id =:id")
    Optional<PostComment> findOneWithToOneRelationships(@Param("id") Long id);
}
