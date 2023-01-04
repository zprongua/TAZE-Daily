package com.tazedaily.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tazedaily.IntegrationTest;
import com.tazedaily.domain.Bookmark;
import com.tazedaily.repository.BookmarkRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookmarkResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookmarkResourceIT {

    private static final String ENTITY_API_URL = "/api/bookmarks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BookmarkRepository bookmarkRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookmarkMockMvc;

    private Bookmark bookmark;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bookmark createEntity(EntityManager em) {
        Bookmark bookmark = new Bookmark();
        return bookmark;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bookmark createUpdatedEntity(EntityManager em) {
        Bookmark bookmark = new Bookmark();
        return bookmark;
    }

    @BeforeEach
    public void initTest() {
        bookmark = createEntity(em);
    }

    @Test
    @Transactional
    void createBookmark() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();
        // Create the Bookmark
        restBookmarkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookmark)))
            .andExpect(status().isCreated());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate + 1);
        Bookmark testBookmark = bookmarkList.get(bookmarkList.size() - 1);
    }

    @Test
    @Transactional
    void createBookmarkWithExistingId() throws Exception {
        // Create the Bookmark with an existing ID
        bookmark.setId(1L);

        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookmarkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookmark)))
            .andExpect(status().isBadRequest());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookmarks() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList
        restBookmarkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmark.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookmarksWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookmarkRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookmarkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookmarkRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookmarksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookmarkRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookmarkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookmarkRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBookmark() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get the bookmark
        restBookmarkMockMvc
            .perform(get(ENTITY_API_URL_ID, bookmark.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookmark.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingBookmark() throws Exception {
        // Get the bookmark
        restBookmarkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookmark() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();

        // Update the bookmark
        Bookmark updatedBookmark = bookmarkRepository.findById(bookmark.getId()).get();
        // Disconnect from session so that the updates on updatedBookmark are not directly saved in db
        em.detach(updatedBookmark);

        restBookmarkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBookmark.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBookmark))
            )
            .andExpect(status().isOk());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
        Bookmark testBookmark = bookmarkList.get(bookmarkList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        bookmark.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookmarkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookmark.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookmark))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        bookmark.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookmarkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookmark))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        bookmark.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookmarkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookmark)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookmarkWithPatch() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();

        // Update the bookmark using partial update
        Bookmark partialUpdatedBookmark = new Bookmark();
        partialUpdatedBookmark.setId(bookmark.getId());

        restBookmarkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookmark.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookmark))
            )
            .andExpect(status().isOk());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
        Bookmark testBookmark = bookmarkList.get(bookmarkList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateBookmarkWithPatch() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();

        // Update the bookmark using partial update
        Bookmark partialUpdatedBookmark = new Bookmark();
        partialUpdatedBookmark.setId(bookmark.getId());

        restBookmarkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookmark.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookmark))
            )
            .andExpect(status().isOk());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
        Bookmark testBookmark = bookmarkList.get(bookmarkList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        bookmark.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookmarkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookmark.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookmark))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        bookmark.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookmarkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookmark))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        bookmark.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookmarkMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bookmark)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookmark() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        int databaseSizeBeforeDelete = bookmarkRepository.findAll().size();

        // Delete the bookmark
        restBookmarkMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookmark.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
