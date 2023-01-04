import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './news-article.reducer';

export const NewsArticleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const newsArticleEntity = useAppSelector(state => state.newsArticle.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="newsArticleDetailsHeading">
          <Translate contentKey="tazeDailyApp.newsArticle.detail.title">NewsArticle</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{newsArticleEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="tazeDailyApp.newsArticle.title">Title</Translate>
            </span>
          </dt>
          <dd>{newsArticleEntity.title}</dd>
          <dt>
            <span id="author">
              <Translate contentKey="tazeDailyApp.newsArticle.author">Author</Translate>
            </span>
          </dt>
          <dd>{newsArticleEntity.author}</dd>
          <dt>
            <span id="article">
              <Translate contentKey="tazeDailyApp.newsArticle.article">Article</Translate>
            </span>
          </dt>
          <dd>{newsArticleEntity.article}</dd>
          <dt>
            <span id="genre">
              <Translate contentKey="tazeDailyApp.newsArticle.genre">Genre</Translate>
            </span>
          </dt>
          <dd>{newsArticleEntity.genre}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="tazeDailyApp.newsArticle.date">Date</Translate>
            </span>
          </dt>
          <dd>
            {newsArticleEntity.date ? <TextFormat value={newsArticleEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="likes">
              <Translate contentKey="tazeDailyApp.newsArticle.likes">Likes</Translate>
            </span>
          </dt>
          <dd>{newsArticleEntity.likes}</dd>
        </dl>
        <Button tag={Link} to="/news-article" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/news-article/${newsArticleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default NewsArticleDetail;
