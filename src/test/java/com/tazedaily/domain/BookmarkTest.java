package com.tazedaily.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tazedaily.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookmarkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bookmark.class);
        Bookmark bookmark1 = new Bookmark();
        bookmark1.setId(1L);
        Bookmark bookmark2 = new Bookmark();
        bookmark2.setId(bookmark1.getId());
        assertThat(bookmark1).isEqualTo(bookmark2);
        bookmark2.setId(2L);
        assertThat(bookmark1).isNotEqualTo(bookmark2);
        bookmark1.setId(null);
        assertThat(bookmark1).isNotEqualTo(bookmark2);
    }
}
