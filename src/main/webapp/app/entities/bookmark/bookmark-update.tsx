import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { INewsArticle } from 'app/shared/model/news-article.model';
import { getEntities as getNewsArticles } from 'app/entities/news-article/news-article.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { IBookmark } from 'app/shared/model/bookmark.model';
import { getEntity, updateEntity, createEntity, reset } from './bookmark.reducer';

export const BookmarkUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const newsArticles = useAppSelector(state => state.newsArticle.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const bookmarkEntity = useAppSelector(state => state.bookmark.entity);
  const loading = useAppSelector(state => state.bookmark.loading);
  const updating = useAppSelector(state => state.bookmark.updating);
  const updateSuccess = useAppSelector(state => state.bookmark.updateSuccess);

  const handleClose = () => {
    navigate('/bookmark');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getNewsArticles({}));
    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...bookmarkEntity,
      ...values,
      newsarticle: newsArticles.find(it => it.id.toString() === values.newsarticle.toString()),
      user: users.find(it => it.id.toString() === values.user.toString()),
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
          ...bookmarkEntity,
          newsarticle: bookmarkEntity?.newsarticle?.id,
          user: bookmarkEntity?.user?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="tazeDailyApp.bookmark.home.createOrEditLabel" data-cy="BookmarkCreateUpdateHeading">
            <Translate contentKey="tazeDailyApp.bookmark.home.createOrEditLabel">Create or edit a Bookmark</Translate>
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
                  id="bookmark-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                id="bookmark-newsarticle"
                name="newsarticle"
                data-cy="newsarticle"
                label={translate('tazeDailyApp.bookmark.newsarticle')}
                type="select"
              >
                <option value="" key="0" />
                {newsArticles
                  ? newsArticles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="bookmark-user" name="user" data-cy="user" label={translate('tazeDailyApp.bookmark.user')} type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/bookmark" replace color="info">
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

export default BookmarkUpdate;
