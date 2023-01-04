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
import { IPostComment } from 'app/shared/model/post-comment.model';
import { getEntity, updateEntity, createEntity, reset } from './post-comment.reducer';

export const PostCommentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const newsArticles = useAppSelector(state => state.newsArticle.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const postCommentEntity = useAppSelector(state => state.postComment.entity);
  const loading = useAppSelector(state => state.postComment.loading);
  const updating = useAppSelector(state => state.postComment.updating);
  const updateSuccess = useAppSelector(state => state.postComment.updateSuccess);

  const handleClose = () => {
    navigate('/post-comment');
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
      ...postCommentEntity,
      ...values,
      newsArticle: newsArticles.find(it => it.id.toString() === values.newsArticle.toString()),
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
          ...postCommentEntity,
          newsArticle: postCommentEntity?.newsArticle?.id,
          user: postCommentEntity?.user?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="tazeDailyApp.postComment.home.createOrEditLabel" data-cy="PostCommentCreateUpdateHeading">
            <Translate contentKey="tazeDailyApp.postComment.home.createOrEditLabel">Create or edit a PostComment</Translate>
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
                  id="post-comment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('tazeDailyApp.postComment.body')}
                id="post-comment-body"
                name="body"
                data-cy="body"
                type="text"
              />
              <ValidatedField
                label={translate('tazeDailyApp.postComment.author')}
                id="post-comment-author"
                name="author"
                data-cy="author"
                type="text"
              />
              <ValidatedField
                label={translate('tazeDailyApp.postComment.timeStamp')}
                id="post-comment-timeStamp"
                name="timeStamp"
                data-cy="timeStamp"
                type="date"
              />
              <ValidatedField
                id="post-comment-newsArticle"
                name="newsArticle"
                data-cy="newsArticle"
                label={translate('tazeDailyApp.postComment.newsArticle')}
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
              <ValidatedField
                id="post-comment-user"
                name="user"
                data-cy="user"
                label={translate('tazeDailyApp.postComment.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/post-comment" replace color="info">
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

export default PostCommentUpdate;
