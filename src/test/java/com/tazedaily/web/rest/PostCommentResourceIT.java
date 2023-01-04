package com.tazedaily.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tazedaily.IntegrationTest;
import com.tazedaily.domain.PostComment;
import com.tazedaily.repository.PostCommentRepository;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PostCommentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PostCommentResourceIT {

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_TIME_STAMP = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIME_STAMP = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/post-comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Mock
    private PostCommentRepository postCommentRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostCommentMockMvc;

    private PostComment postComment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostComment createEntity(EntityManager em) {
        PostComment postComment = new PostComment().body(DEFAULT_BODY).author(DEFAULT_AUTHOR).timeStamp(DEFAULT_TIME_STAMP);
        return postComment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostComment createUpdatedEntity(EntityManager em) {
        PostComment postComment = new PostComment().body(UPDATED_BODY).author(UPDATED_AUTHOR).timeStamp(UPDATED_TIME_STAMP);
        return postComment;
    }

    @BeforeEach
    public void initTest() {
        postComment = createEntity(em);
    }

    @Test
    @Transactional
    void createPostComment() throws Exception {
        int databaseSizeBeforeCreate = postCommentRepository.findAll().size();
        // Create the PostComment
        restPostCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postComment)))
            .andExpect(status().isCreated());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeCreate + 1);
        PostComment testPostComment = postCommentList.get(postCommentList.size() - 1);
        assertThat(testPostComment.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testPostComment.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testPostComment.getTimeStamp()).isEqualTo(DEFAULT_TIME_STAMP);
    }

    @Test
    @Transactional
    void createPostCommentWithExistingId() throws Exception {
        // Create the PostComment with an existing ID
        postComment.setId(1L);

        int databaseSizeBeforeCreate = postCommentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postComment)))
            .andExpect(status().isBadRequest());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPostComments() throws Exception {
        // Initialize the database
        postCommentRepository.saveAndFlush(postComment);

        // Get all the postCommentList
        restPostCommentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postComment.getId().intValue())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].timeStamp").value(hasItem(DEFAULT_TIME_STAMP.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostCommentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(postCommentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPostCommentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(postCommentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostCommentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(postCommentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPostCommentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(postCommentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPostComment() throws Exception {
        // Initialize the database
        postCommentRepository.saveAndFlush(postComment);

        // Get the postComment
        restPostCommentMockMvc
            .perform(get(ENTITY_API_URL_ID, postComment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(postComment.getId().intValue()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.timeStamp").value(DEFAULT_TIME_STAMP.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPostComment() throws Exception {
        // Get the postComment
        restPostCommentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPostComment() throws Exception {
        // Initialize the database
        postCommentRepository.saveAndFlush(postComment);

        int databaseSizeBeforeUpdate = postCommentRepository.findAll().size();

        // Update the postComment
        PostComment updatedPostComment = postCommentRepository.findById(postComment.getId()).get();
        // Disconnect from session so that the updates on updatedPostComment are not directly saved in db
        em.detach(updatedPostComment);
        updatedPostComment.body(UPDATED_BODY).author(UPDATED_AUTHOR).timeStamp(UPDATED_TIME_STAMP);

        restPostCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPostComment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPostComment))
            )
            .andExpect(status().isOk());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
        PostComment testPostComment = postCommentList.get(postCommentList.size() - 1);
        assertThat(testPostComment.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testPostComment.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testPostComment.getTimeStamp()).isEqualTo(UPDATED_TIME_STAMP);
    }

    @Test
    @Transactional
    void putNonExistingPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().size();
        postComment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postComment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postComment))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().size();
        postComment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postComment))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().size();
        postComment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostCommentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postComment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostCommentWithPatch() throws Exception {
        // Initialize the database
        postCommentRepository.saveAndFlush(postComment);

        int databaseSizeBeforeUpdate = postCommentRepository.findAll().size();

        // Update the postComment using partial update
        PostComment partialUpdatedPostComment = new PostComment();
        partialUpdatedPostComment.setId(postComment.getId());

        partialUpdatedPostComment.body(UPDATED_BODY).timeStamp(UPDATED_TIME_STAMP);

        restPostCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostComment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPostComment))
            )
            .andExpect(status().isOk());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
        PostComment testPostComment = postCommentList.get(postCommentList.size() - 1);
        assertThat(testPostComment.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testPostComment.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testPostComment.getTimeStamp()).isEqualTo(UPDATED_TIME_STAMP);
    }

    @Test
    @Transactional
    void fullUpdatePostCommentWithPatch() throws Exception {
        // Initialize the database
        postCommentRepository.saveAndFlush(postComment);

        int databaseSizeBeforeUpdate = postCommentRepository.findAll().size();

        // Update the postComment using partial update
        PostComment partialUpdatedPostComment = new PostComment();
        partialUpdatedPostComment.setId(postComment.getId());

        partialUpdatedPostComment.body(UPDATED_BODY).author(UPDATED_AUTHOR).timeStamp(UPDATED_TIME_STAMP);

        restPostCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostComment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPostComment))
            )
            .andExpect(status().isOk());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
        PostComment testPostComment = postCommentList.get(postCommentList.size() - 1);
        assertThat(testPostComment.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testPostComment.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testPostComment.getTimeStamp()).isEqualTo(UPDATED_TIME_STAMP);
    }

    @Test
    @Transactional
    void patchNonExistingPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().size();
        postComment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, postComment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(postComment))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().size();
        postComment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(postComment))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().size();
        postComment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostCommentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(postComment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePostComment() throws Exception {
        // Initialize the database
        postCommentRepository.saveAndFlush(postComment);

        int databaseSizeBeforeDelete = postCommentRepository.findAll().size();

        // Delete the postComment
        restPostCommentMockMvc
            .perform(delete(ENTITY_API_URL_ID, postComment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PostComment> postCommentList = postCommentRepository.findAll();
        assertThat(postCommentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
