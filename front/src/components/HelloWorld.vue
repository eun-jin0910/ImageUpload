<template>
  <v-app>
    <ImageSearch/>
    <ImageList/>
    <ImageUpload/>
    <ImagePreview/>
  </v-app>
</template>

<script>
import ImageUpload from './ImageUpload';
import ImageList from './ImageList.vue';
import ImagePreview from './ImagePreview.vue';
import ImageSearch from './ImageSearch.vue';

  export default {
    components: {
      ImageUpload,
      ImageList,
      ImagePreview,
      ImageSearch,
  },
    data () {
      return {
        dialog: false,
      }
    },
  }
</script>

<style>
</style>
