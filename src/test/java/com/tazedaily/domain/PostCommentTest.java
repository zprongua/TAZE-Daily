package com.tazedaily.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tazedaily.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostCommentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostComment.class);
        PostComment postComment1 = new PostComment();
        postComment1.setId(1L);
        PostComment postComment2 = new PostComment();
        postComment2.setId(postComment1.getId());
        assertThat(postComment1).isEqualTo(postComment2);
        postComment2.setId(2L);
        assertThat(postComment1).isNotEqualTo(postComment2);
        postComment1.setId(null);
        assertThat(postComment1).isNotEqualTo(postComment2);
    }
}
