import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/post-comment">
        <Translate contentKey="global.menu.entities.postComment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/news-article">
        <Translate contentKey="global.menu.entities.newsArticle" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/bookmark">
        <Translate contentKey="global.menu.entities.bookmark" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
