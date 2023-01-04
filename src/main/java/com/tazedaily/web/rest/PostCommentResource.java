package com.tazedaily.web.rest;

import com.tazedaily.domain.PostComment;
import com.tazedaily.repository.PostCommentRepository;
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
 * REST controller for managing {@link com.tazedaily.domain.PostComment}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PostCommentResource {

    private final Logger log = LoggerFactory.getLogger(PostCommentResource.class);

    private static final String ENTITY_NAME = "postComment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostCommentRepository postCommentRepository;

    public PostCommentResource(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }

    /**
     * {@code POST  /post-comments} : Create a new postComment.
     *
     * @param postComment the postComment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postComment, or with status {@code 400 (Bad Request)} if the postComment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/post-comments")
    public ResponseEntity<PostComment> createPostComment(@RequestBody PostComment postComment) throws URISyntaxException {
        log.debug("REST request to save PostComment : {}", postComment);
        if (postComment.getId() != null) {
            throw new BadRequestAlertException("A new postComment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PostComment result = postCommentRepository.save(postComment);
        return ResponseEntity
            .created(new URI("/api/post-comments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /post-comments/:id} : Updates an existing postComment.
     *
     * @param id the id of the postComment to save.
     * @param postComment the postComment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postComment,
     * or with status {@code 400 (Bad Request)} if the postComment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postComment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/post-comments/{id}")
    public ResponseEntity<PostComment> updatePostComment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PostComment postComment
    ) throws URISyntaxException {
        log.debug("REST request to update PostComment : {}, {}", id, postComment);
        if (postComment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postComment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PostComment result = postCommentRepository.save(postComment);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postComment.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /post-comments/:id} : Partial updates given fields of an existing postComment, field will ignore if it is null
     *
     * @param id the id of the postComment to save.
     * @param postComment the postComment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postComment,
     * or with status {@code 400 (Bad Request)} if the postComment is not valid,
     * or with status {@code 404 (Not Found)} if the postComment is not found,
     * or with status {@code 500 (Internal Server Error)} if the postComment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/post-comments/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PostComment> partialUpdatePostComment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PostComment postComment
    ) throws URISyntaxException {
        log.debug("REST request to partial update PostComment partially : {}, {}", id, postComment);
        if (postComment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postComment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PostComment> result = postCommentRepository
            .findById(postComment.getId())
            .map(existingPostComment -> {
                if (postComment.getBody() != null) {
                    existingPostComment.setBody(postComment.getBody());
                }
                if (postComment.getAuthor() != null) {
                    existingPostComment.setAuthor(postComment.getAuthor());
                }
                if (postComment.getTimeStamp() != null) {
                    existingPostComment.setTimeStamp(postComment.getTimeStamp());
                }

                return existingPostComment;
            })
            .map(postCommentRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postComment.getId().toString())
        );
    }

    /**
     * {@code GET  /post-comments} : get all the postComments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postComments in body.
     */
    @GetMapping("/post-comments")
    public List<PostComment> getAllPostComments(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all PostComments");
        if (eagerload) {
            return postCommentRepository.findAllWithEagerRelationships();
        } else {
            return postCommentRepository.findAll();
        }
    }

    /**
     * {@code GET  /post-comments/:id} : get the "id" postComment.
     *
     * @param id the id of the postComment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postComment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/post-comments/{id}")
    public ResponseEntity<PostComment> getPostComment(@PathVariable Long id) {
        log.debug("REST request to get PostComment : {}", id);
        Optional<PostComment> postComment = postCommentRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(postComment);
    }

    /**
     * {@code DELETE  /post-comments/:id} : delete the "id" postComment.
     *
     * @param id the id of the postComment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/post-comments/{id}")
    public ResponseEntity<Void> deletePostComment(@PathVariable Long id) {
        log.debug("REST request to delete PostComment : {}", id);
        postCommentRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
