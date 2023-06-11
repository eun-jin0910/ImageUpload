<template>
    <v-container>
      <!-- <h1>이미지 미리보기</h1> -->
      <div class="image-grid">
        <img
          v-for="image in images"
          :key="image.fileURL"
          :src="image.fileURL"
          :alt="image.fileName"
          class="image"
          @click="openModal(image)"
        />
      </div>
  
      <v-dialog v-model="modalOpen" max-width="500">
        <v-img :src="selectedImage.fileURL" :alt="selectedImage.fileName" class="modal-image" />
  
        <v-card>
          <v-card-title>{{ selectedImage.fileName }}</v-card-title>
          <v-card-text>
            <p><strong>파일명:</strong> {{ selectedImage.fileName }}</p>
            <p><strong>아이디:</strong> {{ selectedImage.userId }}</p>
            <p><strong>등록일자:</strong> {{ selectedImage.uploadDate }}</p>
          </v-card-text>
        </v-card>
  
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="closeModal">닫기</v-btn>
        </v-card-actions>
      </v-dialog>
    </v-container>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        images: [],
        modalOpen: false,
        selectedImage: {},
      };
    },
    created() {
      this.fetchImages();
    },
    methods: {
      fetchImages() {
        axios
          .get('http://localhost:8080/images')
          .then(response => {
            this.images = Object.values(response.data);
          })
          .catch(error => {
            console.error(error);
          });
      },
      openModal(image) {
        this.selectedImage = image;
        this.modalOpen = true;
      },
      closeModal() {
        this.modalOpen = false;
      },
    },
  };
  </script>
  
  <style>
  .image-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 10px;
  }
  
  .image {
    margin: 20px;
    margin-left: 0 !important;
    padding: 0 !important;
    border-radius: 4px;
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.2);
    width: 100%;
    height: 200px;
    object-fit: cover;
    cursor: pointer;
  }
  
  .modal-image {
    max-width: 100%;
    max-height: 500px;
    object-fit: contain;
  }
  </style>
  