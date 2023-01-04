package com.tazedaily.repository;

import com.tazedaily.domain.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Bookmark entity.
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    @Query("select bookmark from Bookmark bookmark where bookmark.user.login = ?#{principal.username}")
    List<Bookmark> findByUserIsCurrentUser();

    default Optional<Bookmark> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Bookmark> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Bookmark> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct bookmark from Bookmark bookmark left join fetch bookmark.user",
        countQuery = "select count(distinct bookmark) from Bookmark bookmark"
    )
    Page<Bookmark> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct bookmark from Bookmark bookmark left join fetch bookmark.user")
    List<Bookmark> findAllWithToOneRelationships();

    @Query("select bookmark from Bookmark bookmark left join fetch bookmark.user where bookmark.id =:id")
    Optional<Bookmark> findOneWithToOneRelationships(@Param("id") Long id);
}
