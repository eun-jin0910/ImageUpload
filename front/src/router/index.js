import Vue from 'vue';
import Router from 'vue-router';
import ImageList from '../components/ImageList.vue';

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/list',
      name: 'ImageList',
      component: ImageList
    }
  ]
});