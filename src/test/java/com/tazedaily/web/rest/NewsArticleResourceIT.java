package com.tazedaily.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tazedaily.IntegrationTest;
import com.tazedaily.domain.NewsArticle;
import com.tazedaily.domain.enumeration.Genre;
import com.tazedaily.repository.NewsArticleRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NewsArticleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NewsArticleResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_ARTICLE = "AAAAAAAAAA";
    private static final String UPDATED_ARTICLE = "BBBBBBBBBB";

    private static final Genre DEFAULT_GENRE = Genre.BUSINESS;
    private static final Genre UPDATED_GENRE = Genre.ENTERTAINMENT;

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_LIKES = 1;
    private static final Integer UPDATED_LIKES = 2;

    private static final String ENTITY_API_URL = "/api/news-articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NewsArticleRepository newsArticleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNewsArticleMockMvc;

    private NewsArticle newsArticle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NewsArticle createEntity(EntityManager em) {
        NewsArticle newsArticle = new NewsArticle()
            .title(DEFAULT_TITLE)
            .author(DEFAULT_AUTHOR)
            .article(DEFAULT_ARTICLE)
            .genre(DEFAULT_GENRE)
            .date(DEFAULT_DATE)
            .likes(DEFAULT_LIKES);
        return newsArticle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NewsArticle createUpdatedEntity(EntityManager em) {
        NewsArticle newsArticle = new NewsArticle()
            .title(UPDATED_TITLE)
            .author(UPDATED_AUTHOR)
            .article(UPDATED_ARTICLE)
            .genre(UPDATED_GENRE)
            .date(UPDATED_DATE)
            .likes(UPDATED_LIKES);
        return newsArticle;
    }

    @BeforeEach
    public void initTest() {
        newsArticle = createEntity(em);
    }

    @Test
    @Transactional
    void createNewsArticle() throws Exception {
        int databaseSizeBeforeCreate = newsArticleRepository.findAll().size();
        // Create the NewsArticle
        restNewsArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(newsArticle)))
            .andExpect(status().isCreated());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeCreate + 1);
        NewsArticle testNewsArticle = newsArticleList.get(newsArticleList.size() - 1);
        assertThat(testNewsArticle.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testNewsArticle.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testNewsArticle.getArticle()).isEqualTo(DEFAULT_ARTICLE);
        assertThat(testNewsArticle.getGenre()).isEqualTo(DEFAULT_GENRE);
        assertThat(testNewsArticle.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testNewsArticle.getLikes()).isEqualTo(DEFAULT_LIKES);
    }

    @Test
    @Transactional
    void createNewsArticleWithExistingId() throws Exception {
        // Create the NewsArticle with an existing ID
        newsArticle.setId(1L);

        int databaseSizeBeforeCreate = newsArticleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNewsArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(newsArticle)))
            .andExpect(status().isBadRequest());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllNewsArticles() throws Exception {
        // Initialize the database
        newsArticleRepository.saveAndFlush(newsArticle);

        // Get all the newsArticleList
        restNewsArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(newsArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].article").value(hasItem(DEFAULT_ARTICLE)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].likes").value(hasItem(DEFAULT_LIKES)));
    }

    @Test
    @Transactional
    void getNewsArticle() throws Exception {
        // Initialize the database
        newsArticleRepository.saveAndFlush(newsArticle);

        // Get the newsArticle
        restNewsArticleMockMvc
            .perform(get(ENTITY_API_URL_ID, newsArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(newsArticle.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.article").value(DEFAULT_ARTICLE))
            .andExpect(jsonPath("$.genre").value(DEFAULT_GENRE.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.likes").value(DEFAULT_LIKES));
    }

    @Test
    @Transactional
    void getNonExistingNewsArticle() throws Exception {
        // Get the newsArticle
        restNewsArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNewsArticle() throws Exception {
        // Initialize the database
        newsArticleRepository.saveAndFlush(newsArticle);

        int databaseSizeBeforeUpdate = newsArticleRepository.findAll().size();

        // Update the newsArticle
        NewsArticle updatedNewsArticle = newsArticleRepository.findById(newsArticle.getId()).get();
        // Disconnect from session so that the updates on updatedNewsArticle are not directly saved in db
        em.detach(updatedNewsArticle);
        updatedNewsArticle
            .title(UPDATED_TITLE)
            .author(UPDATED_AUTHOR)
            .article(UPDATED_ARTICLE)
            .genre(UPDATED_GENRE)
            .date(UPDATED_DATE)
            .likes(UPDATED_LIKES);

        restNewsArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNewsArticle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNewsArticle))
            )
            .andExpect(status().isOk());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeUpdate);
        NewsArticle testNewsArticle = newsArticleList.get(newsArticleList.size() - 1);
        assertThat(testNewsArticle.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNewsArticle.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testNewsArticle.getArticle()).isEqualTo(UPDATED_ARTICLE);
        assertThat(testNewsArticle.getGenre()).isEqualTo(UPDATED_GENRE);
        assertThat(testNewsArticle.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNewsArticle.getLikes()).isEqualTo(UPDATED_LIKES);
    }

    @Test
    @Transactional
    void putNonExistingNewsArticle() throws Exception {
        int databaseSizeBeforeUpdate = newsArticleRepository.findAll().size();
        newsArticle.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNewsArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, newsArticle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(newsArticle))
            )
            .andExpect(status().isBadRequest());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNewsArticle() throws Exception {
        int databaseSizeBeforeUpdate = newsArticleRepository.findAll().size();
        newsArticle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNewsArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(newsArticle))
            )
            .andExpect(status().isBadRequest());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNewsArticle() throws Exception {
        int databaseSizeBeforeUpdate = newsArticleRepository.findAll().size();
        newsArticle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNewsArticleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(newsArticle)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNewsArticleWithPatch() throws Exception {
        // Initialize the database
        newsArticleRepository.saveAndFlush(newsArticle);

        int databaseSizeBeforeUpdate = newsArticleRepository.findAll().size();

        // Update the newsArticle using partial update
        NewsArticle partialUpdatedNewsArticle = new NewsArticle();
        partialUpdatedNewsArticle.setId(newsArticle.getId());

        partialUpdatedNewsArticle.title(UPDATED_TITLE).article(UPDATED_ARTICLE).genre(UPDATED_GENRE).date(UPDATED_DATE);

        restNewsArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNewsArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNewsArticle))
            )
            .andExpect(status().isOk());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeUpdate);
        NewsArticle testNewsArticle = newsArticleList.get(newsArticleList.size() - 1);
        assertThat(testNewsArticle.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNewsArticle.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testNewsArticle.getArticle()).isEqualTo(UPDATED_ARTICLE);
        assertThat(testNewsArticle.getGenre()).isEqualTo(UPDATED_GENRE);
        assertThat(testNewsArticle.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNewsArticle.getLikes()).isEqualTo(DEFAULT_LIKES);
    }

    @Test
    @Transactional
    void fullUpdateNewsArticleWithPatch() throws Exception {
        // Initialize the database
        newsArticleRepository.saveAndFlush(newsArticle);

        int databaseSizeBeforeUpdate = newsArticleRepository.findAll().size();

        // Update the newsArticle using partial update
        NewsArticle partialUpdatedNewsArticle = new NewsArticle();
        partialUpdatedNewsArticle.setId(newsArticle.getId());

        partialUpdatedNewsArticle
            .title(UPDATED_TITLE)
            .author(UPDATED_AUTHOR)
            .article(UPDATED_ARTICLE)
            .genre(UPDATED_GENRE)
            .date(UPDATED_DATE)
            .likes(UPDATED_LIKES);

        restNewsArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNewsArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNewsArticle))
            )
            .andExpect(status().isOk());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeUpdate);
        NewsArticle testNewsArticle = newsArticleList.get(newsArticleList.size() - 1);
        assertThat(testNewsArticle.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNewsArticle.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testNewsArticle.getArticle()).isEqualTo(UPDATED_ARTICLE);
        assertThat(testNewsArticle.getGenre()).isEqualTo(UPDATED_GENRE);
        assertThat(testNewsArticle.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNewsArticle.getLikes()).isEqualTo(UPDATED_LIKES);
    }

    @Test
    @Transactional
    void patchNonExistingNewsArticle() throws Exception {
        int databaseSizeBeforeUpdate = newsArticleRepository.findAll().size();
        newsArticle.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNewsArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, newsArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(newsArticle))
            )
            .andExpect(status().isBadRequest());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNewsArticle() throws Exception {
        int databaseSizeBeforeUpdate = newsArticleRepository.findAll().size();
        newsArticle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNewsArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(newsArticle))
            )
            .andExpect(status().isBadRequest());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNewsArticle() throws Exception {
        int databaseSizeBeforeUpdate = newsArticleRepository.findAll().size();
        newsArticle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNewsArticleMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(newsArticle))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NewsArticle in the database
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNewsArticle() throws Exception {
        // Initialize the database
        newsArticleRepository.saveAndFlush(newsArticle);

        int databaseSizeBeforeDelete = newsArticleRepository.findAll().size();

        // Delete the newsArticle
        restNewsArticleMockMvc
            .perform(delete(ENTITY_API_URL_ID, newsArticle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NewsArticle> newsArticleList = newsArticleRepository.findAll();
        assertThat(newsArticleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
