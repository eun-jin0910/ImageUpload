<template>
  <v-app>
    <image-list/>
    <image-upload/>
    <image-board/>
  </v-app>
</template>

<script>
import ImageUpload from './ImageUpload';
import ImageList from './ImageList.vue';
import ImageBoard from './ImageBoard.vue';

export default {
  components: {
    ImageUpload,
    ImageList,
    ImageBoard,
  },
}
</script>

<style>
</style>
