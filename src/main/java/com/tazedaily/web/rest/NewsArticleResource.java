package com.tazedaily.web.rest;

import com.tazedaily.domain.NewsArticle;
import com.tazedaily.repository.NewsArticleRepository;
import com.tazedaily.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tazedaily.domain.NewsArticle}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class NewsArticleResource {

    private final Logger log = LoggerFactory.getLogger(NewsArticleResource.class);

    private static final String ENTITY_NAME = "newsArticle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NewsArticleRepository newsArticleRepository;

    public NewsArticleResource(NewsArticleRepository newsArticleRepository) {
        this.newsArticleRepository = newsArticleRepository;
    }

    /**
     * {@code POST  /news-articles} : Create a new newsArticle.
     *
     * @param newsArticle the newsArticle to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new newsArticle, or with status {@code 400 (Bad Request)} if the newsArticle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/news-articles")
    public ResponseEntity<NewsArticle> createNewsArticle(@RequestBody NewsArticle newsArticle) throws URISyntaxException {
        log.debug("REST request to save NewsArticle : {}", newsArticle);
        if (newsArticle.getId() != null) {
            throw new BadRequestAlertException("A new newsArticle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NewsArticle result = newsArticleRepository.save(newsArticle);
        return ResponseEntity
            .created(new URI("/api/news-articles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /news-articles/:id} : Updates an existing newsArticle.
     *
     * @param id the id of the newsArticle to save.
     * @param newsArticle the newsArticle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated newsArticle,
     * or with status {@code 400 (Bad Request)} if the newsArticle is not valid,
     * or with status {@code 500 (Internal Server Error)} if the newsArticle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/news-articles/{id}")
    public ResponseEntity<NewsArticle> updateNewsArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody NewsArticle newsArticle
    ) throws URISyntaxException {
        log.debug("REST request to update NewsArticle : {}, {}", id, newsArticle);
        if (newsArticle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, newsArticle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!newsArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NewsArticle result = newsArticleRepository.save(newsArticle);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, newsArticle.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /news-articles/:id} : Partial updates given fields of an existing newsArticle, field will ignore if it is null
     *
     * @param id the id of the newsArticle to save.
     * @param newsArticle the newsArticle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated newsArticle,
     * or with status {@code 400 (Bad Request)} if the newsArticle is not valid,
     * or with status {@code 404 (Not Found)} if the newsArticle is not found,
     * or with status {@code 500 (Internal Server Error)} if the newsArticle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/news-articles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NewsArticle> partialUpdateNewsArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody NewsArticle newsArticle
    ) throws URISyntaxException {
        log.debug("REST request to partial update NewsArticle partially : {}, {}", id, newsArticle);
        if (newsArticle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, newsArticle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!newsArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NewsArticle> result = newsArticleRepository
            .findById(newsArticle.getId())
            .map(existingNewsArticle -> {
                if (newsArticle.getTitle() != null) {
                    existingNewsArticle.setTitle(newsArticle.getTitle());
                }
                if (newsArticle.getAuthor() != null) {
                    existingNewsArticle.setAuthor(newsArticle.getAuthor());
                }
                if (newsArticle.getArticle() != null) {
                    existingNewsArticle.setArticle(newsArticle.getArticle());
                }
                if (newsArticle.getGenre() != null) {
                    existingNewsArticle.setGenre(newsArticle.getGenre());
                }
                if (newsArticle.getDate() != null) {
                    existingNewsArticle.setDate(newsArticle.getDate());
                }
                if (newsArticle.getLikes() != null) {
                    existingNewsArticle.setLikes(newsArticle.getLikes());
                }

                return existingNewsArticle;
            })
            .map(newsArticleRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, newsArticle.getId().toString())
        );
    }

    /**
     * {@code GET  /news-articles} : get all the newsArticles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of newsArticles in body.
     */
    @GetMapping("/news-articles")
    public List<NewsArticle> getAllNewsArticles() {
        log.debug("REST request to get all NewsArticles");
        return newsArticleRepository.findAll();
    }

    /**
     * {@code GET  /news-articles/:id} : get the "id" newsArticle.
     *
     * @param id the id of the newsArticle to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the newsArticle, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/news-articles/{id}")
    public ResponseEntity<NewsArticle> getNewsArticle(@PathVariable Long id) {
        log.debug("REST request to get NewsArticle : {}", id);
        Optional<NewsArticle> newsArticle = newsArticleRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(newsArticle);
    }

    /**
     * {@code DELETE  /news-articles/:id} : delete the "id" newsArticle.
     *
     * @param id the id of the newsArticle to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/news-articles/{id}")
    public ResponseEntity<Void> deleteNewsArticle(@PathVariable Long id) {
        log.debug("REST request to delete NewsArticle : {}", id);
        newsArticleRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
