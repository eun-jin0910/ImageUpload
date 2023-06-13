<template>
  <v-app>
    <v-container>
      <v-row align="center">
        <v-col cols="auto">
          <v-btn-toggle v-model="activeButton" mandatory>
            <v-btn value="image-board">
              <v-icon size="24">mdi-image</v-icon>
            </v-btn>
            <v-btn value="image-list">
              <v-icon size="24">mdi-view-list</v-icon>
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
import ImageBoard from './ImageBoard.vue';

export default {
  components: {
    ImageList,
    ImageBoard
  },
  data() {
    return {
      activeButton: 'image-board'
    };
  }
};
</script>

<style>
</style>
