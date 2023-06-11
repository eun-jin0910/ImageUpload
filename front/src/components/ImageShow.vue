<template>
    <v-container>
      <v-row>
        <v-col cols="12">
          <v-btn-toggle v-model="activeButton" mandatory>
            <v-btn @click="showImageList">Image List</v-btn>
            <v-btn @click="showImagePreview">Image Preview</v-btn>
          </v-btn-toggle>
        </v-col>
      </v-row>
  
      <ImageList v-show="activeButton === 'imageList'" />
      <ImagePreview v-show="activeButton === 'imagePreview'" />
    </v-container>
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
        activeButton: 'imageList' // Set default active button
      };
    },
    methods: {
      showImageList() {
        this.activeButton = 'imageList';
      },
      showImagePreview() {
        this.activeButton = 'imagePreview';
      }
    }
  };
  </script>
  
  <style>
  </style>
  