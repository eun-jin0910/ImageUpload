import Vue from 'vue';
import Router from 'vue-router';

/* Router Modules */
import HelloWorld from '../components/HelloWorld.vue';
import ImageList from '../components/ImageList.vue';
import ImagePreview from '../components/ImagePreview.vue';
import ImageUpload from '../components/ImageUpload.vue';
import ImageShow from '../components/ImageShow.vue';
import ImageSearch from '../components/ImageSearch.vue';

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/list',
      name: 'ImageList',
      component: ImageList
    },
    {
      path: '/preview',
      name: 'ImagePreview',
      component: ImagePreview
    },
    {
      path: '/show',
      name: 'ImageShow',
      component: ImageShow
    },
    {
      path: '/upload',
      name: 'ImageUpload',
      component: ImageUpload
    },
    {
      path: '/search',
      name: 'ImageSearch',
      component: ImageSearch
    },
  ]
});