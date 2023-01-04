import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { INewsArticle } from 'app/shared/model/news-article.model';
import { Genre } from 'app/shared/model/enumerations/genre.model';
import { getEntity, updateEntity, createEntity, reset } from './news-article.reducer';

export const NewsArticleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const newsArticleEntity = useAppSelector(state => state.newsArticle.entity);
  const loading = useAppSelector(state => state.newsArticle.loading);
  const updating = useAppSelector(state => state.newsArticle.updating);
  const updateSuccess = useAppSelector(state => state.newsArticle.updateSuccess);
  const genreValues = Object.keys(Genre);

  const handleClose = () => {
    navigate('/news-article');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...newsArticleEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          genre: 'BUSINESS',
          ...newsArticleEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="tazeDailyApp.newsArticle.home.createOrEditLabel" data-cy="NewsArticleCreateUpdateHeading">
            <Translate contentKey="tazeDailyApp.newsArticle.home.createOrEditLabel">Create or edit a NewsArticle</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="news-article-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('tazeDailyApp.newsArticle.title')}
                id="news-article-title"
                name="title"
                data-cy="title"
                type="text"
              />
              <ValidatedField
                label={translate('tazeDailyApp.newsArticle.author')}
                id="news-article-author"
                name="author"
                data-cy="author"
                type="text"
              />
              <ValidatedField
                label={translate('tazeDailyApp.newsArticle.article')}
                id="news-article-article"
                name="article"
                data-cy="article"
                type="text"
              />
              <ValidatedField
                label={translate('tazeDailyApp.newsArticle.genre')}
                id="news-article-genre"
                name="genre"
                data-cy="genre"
                type="select"
              >
                {genreValues.map(genre => (
                  <option value={genre} key={genre}>
                    {translate('tazeDailyApp.Genre.' + genre)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('tazeDailyApp.newsArticle.date')}
                id="news-article-date"
                name="date"
                data-cy="date"
                type="date"
              />
              <ValidatedField
                label={translate('tazeDailyApp.newsArticle.likes')}
                id="news-article-likes"
                name="likes"
                data-cy="likes"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/news-article" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default NewsArticleUpdate;
