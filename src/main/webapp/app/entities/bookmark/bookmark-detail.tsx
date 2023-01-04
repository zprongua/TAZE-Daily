import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './bookmark.reducer';

export const BookmarkDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const bookmarkEntity = useAppSelector(state => state.bookmark.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bookmarkDetailsHeading">
          <Translate contentKey="tazeDailyApp.bookmark.detail.title">Bookmark</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{bookmarkEntity.id}</dd>
          <dt>
            <Translate contentKey="tazeDailyApp.bookmark.newsarticle">Newsarticle</Translate>
          </dt>
          <dd>{bookmarkEntity.newsarticle ? bookmarkEntity.newsarticle.id : ''}</dd>
          <dt>
            <Translate contentKey="tazeDailyApp.bookmark.user">User</Translate>
          </dt>
          <dd>{bookmarkEntity.user ? bookmarkEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/bookmark" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/bookmark/${bookmarkEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BookmarkDetail;
