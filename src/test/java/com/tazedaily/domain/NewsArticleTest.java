package com.tazedaily.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tazedaily.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NewsArticleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NewsArticle.class);
        NewsArticle newsArticle1 = new NewsArticle();
        newsArticle1.setId(1L);
        NewsArticle newsArticle2 = new NewsArticle();
        newsArticle2.setId(newsArticle1.getId());
        assertThat(newsArticle1).isEqualTo(newsArticle2);
        newsArticle2.setId(2L);
        assertThat(newsArticle1).isNotEqualTo(newsArticle2);
        newsArticle1.setId(null);
        assertThat(newsArticle1).isNotEqualTo(newsArticle2);
    }
}
