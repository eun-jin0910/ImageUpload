<template>
  <v-app>
    <v-container>
      <v-row>
        <v-col cols="12">
          <v-btn-toggle v-model="activeButton" mandatory>
            <v-btn value="imagePreview">
              <v-icon>mdi-image</v-icon>
            </v-btn>
            <v-btn value="imageList">
              <v-icon>mdi-view-list</v-icon>
            </v-btn>
            
          </v-btn-toggle>
        </v-col>
      </v-row>
    </v-container>

    <component :is="activeButton" />
  </v-app>
</template>

<script>
import ImageList from './ImageList.vue';
import ImagePreview from './ImagePreview.vue';

export default {
  components: {
    ImageList,
    ImagePreview
  },
  data() {
    return {
      activeButton: 'imagePreview'
    };
  }
};
</script>

<style>
</style>
