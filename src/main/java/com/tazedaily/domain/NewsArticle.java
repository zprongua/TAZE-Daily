package com.tazedaily.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tazedaily.domain.enumeration.Genre;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A NewsArticle.
 */
@Entity
@Table(name = "news_article")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NewsArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "article")
    private String article;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Genre genre;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "likes")
    private Integer likes;

    @OneToMany(mappedBy = "newsArticle")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "newsArticle", "user" }, allowSetters = true)
    private Set<PostComment> postComments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NewsArticle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public NewsArticle title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return this.author;
    }

    public NewsArticle author(String author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArticle() {
        return this.article;
    }

    public NewsArticle article(String article) {
        this.setArticle(article);
        return this;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Genre getGenre() {
        return this.genre;
    }

    public NewsArticle genre(Genre genre) {
        this.setGenre(genre);
        return this;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public NewsArticle date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getLikes() {
        return this.likes;
    }

    public NewsArticle likes(Integer likes) {
        this.setLikes(likes);
        return this;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Set<PostComment> getPostComments() {
        return this.postComments;
    }

    public void setPostComments(Set<PostComment> postComments) {
        if (this.postComments != null) {
            this.postComments.forEach(i -> i.setNewsArticle(null));
        }
        if (postComments != null) {
            postComments.forEach(i -> i.setNewsArticle(this));
        }
        this.postComments = postComments;
    }

    public NewsArticle postComments(Set<PostComment> postComments) {
        this.setPostComments(postComments);
        return this;
    }

    public NewsArticle addPostComment(PostComment postComment) {
        this.postComments.add(postComment);
        postComment.setNewsArticle(this);
        return this;
    }

    public NewsArticle removePostComment(PostComment postComment) {
        this.postComments.remove(postComment);
        postComment.setNewsArticle(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NewsArticle)) {
            return false;
        }
        return id != null && id.equals(((NewsArticle) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NewsArticle{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", author='" + getAuthor() + "'" +
            ", article='" + getArticle() + "'" +
            ", genre='" + getGenre() + "'" +
            ", date='" + getDate() + "'" +
            ", likes=" + getLikes() +
            "}";
    }
}
