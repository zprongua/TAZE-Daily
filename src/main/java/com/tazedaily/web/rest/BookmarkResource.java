package com.tazedaily.web.rest;

import com.tazedaily.domain.Bookmark;
import com.tazedaily.repository.BookmarkRepository;
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
 * REST controller for managing {@link com.tazedaily.domain.Bookmark}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BookmarkResource {

    private final Logger log = LoggerFactory.getLogger(BookmarkResource.class);

    private static final String ENTITY_NAME = "bookmark";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookmarkRepository bookmarkRepository;

    public BookmarkResource(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    /**
     * {@code POST  /bookmarks} : Create a new bookmark.
     *
     * @param bookmark the bookmark to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookmark, or with status {@code 400 (Bad Request)} if the bookmark has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bookmarks")
    public ResponseEntity<Bookmark> createBookmark(@RequestBody Bookmark bookmark) throws URISyntaxException {
        log.debug("REST request to save Bookmark : {}", bookmark);
        if (bookmark.getId() != null) {
            throw new BadRequestAlertException("A new bookmark cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Bookmark result = bookmarkRepository.save(bookmark);
        return ResponseEntity
            .created(new URI("/api/bookmarks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bookmarks/:id} : Updates an existing bookmark.
     *
     * @param id the id of the bookmark to save.
     * @param bookmark the bookmark to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookmark,
     * or with status {@code 400 (Bad Request)} if the bookmark is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookmark couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bookmarks/{id}")
    public ResponseEntity<Bookmark> updateBookmark(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Bookmark bookmark
    ) throws URISyntaxException {
        log.debug("REST request to update Bookmark : {}, {}", id, bookmark);
        if (bookmark.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookmark.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookmarkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Bookmark result = bookmarkRepository.save(bookmark);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookmark.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bookmarks/:id} : Partial updates given fields of an existing bookmark, field will ignore if it is null
     *
     * @param id the id of the bookmark to save.
     * @param bookmark the bookmark to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookmark,
     * or with status {@code 400 (Bad Request)} if the bookmark is not valid,
     * or with status {@code 404 (Not Found)} if the bookmark is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookmark couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bookmarks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Bookmark> partialUpdateBookmark(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Bookmark bookmark
    ) throws URISyntaxException {
        log.debug("REST request to partial update Bookmark partially : {}, {}", id, bookmark);
        if (bookmark.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookmark.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookmarkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Bookmark> result = bookmarkRepository
            .findById(bookmark.getId())
            .map(existingBookmark -> {
                return existingBookmark;
            })
            .map(bookmarkRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookmark.getId().toString())
        );
    }

    /**
     * {@code GET  /bookmarks} : get all the bookmarks.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookmarks in body.
     */
    @GetMapping("/bookmarks")
    public List<Bookmark> getAllBookmarks(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Bookmarks");
        if (eagerload) {
            return bookmarkRepository.findAllWithEagerRelationships();
        } else {
            return bookmarkRepository.findAll();
        }
    }

    /**
     * {@code GET  /bookmarks/:id} : get the "id" bookmark.
     *
     * @param id the id of the bookmark to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookmark, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bookmarks/{id}")
    public ResponseEntity<Bookmark> getBookmark(@PathVariable Long id) {
        log.debug("REST request to get Bookmark : {}", id);
        Optional<Bookmark> bookmark = bookmarkRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(bookmark);
    }

    /**
     * {@code DELETE  /bookmarks/:id} : delete the "id" bookmark.
     *
     * @param id the id of the bookmark to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id) {
        log.debug("REST request to delete Bookmark : {}", id);
        bookmarkRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
