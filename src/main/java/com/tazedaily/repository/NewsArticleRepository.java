package com.tazedaily.repository;

import com.tazedaily.domain.NewsArticle;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the NewsArticle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {}
