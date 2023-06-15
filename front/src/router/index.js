import Vue from 'vue';
import Router from 'vue-router';

/* Router Modules */
import LayoutHeader from '../components/LayoutHeader.vue';
import ImageHome from '../views/ImageHome.vue';
import ImageList from '../views/ImageList.vue';
import ImageBoard from '../views/ImageBoard.vue';
import ImageUpload from '../views/ImageUpload.vue';
import ImageShow from '../views/ImageShow.vue';
import ImageDetail from '../views/ImageDetail.vue';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/header',
      name: 'LayoutHeader',
      component: LayoutHeader
    },
    {
      path: '/',
      name: 'ImageHome',
      component: ImageHome
    },
    {
      path: '/images/list',
      name: 'ImageList',
      component: ImageList
    },
    {
      path: '/images/board',
      name: 'ImageBoard',
      component: ImageBoard
    },
    {
      path: '/images',
      name: 'ImageShow',
      component: ImageShow
    },
    {
      path: '/upload',
      name: 'ImageUpload',
      component: ImageUpload
    },
    {
      path: '/image/:id',
      name: 'ImageDetail',
      component: ImageDetail
    },
  ]
});
