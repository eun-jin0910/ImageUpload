<template>
  <v-container>
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
        <v-card-title>{{ selectedImage.title }}</v-card-title><br>
        <v-card-text>
          <p><strong>파일명 : </strong> {{ selectedImage.fileName }}</p>
          <p><strong>등록일자 : </strong> {{ selectedImage.uploadDate }}</p>
        </v-card-text>
      </v-card>
      <v-card-actions class="modal-actions">
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="downloadImage(selectedImage.fileURL)">
          저장
        </v-btn>
        
        <router-link :to="{ name: 'ImageDetail', params: { id: selectedImage.id } }">
          <v-btn color="primary" text>상세보기</v-btn>
        </router-link>
        
        <v-btn color="blue darken-1" text @click="closeModal">닫기</v-btn>
      </v-card-actions>
    </v-dialog>
  </v-container>
</template>

<script>
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
      this.$axios.get('/images')
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
    downloadImage(fileURL) {
      this.$axios.get(fileURL, { responseType: 'blob' }) 
      .then(response => {
        const blob = response.data;
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'image.jpg');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
      })
      .catch(error => {
        console.error(error);
      });
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
  margin-right: 20px;
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
.modal-actions {
  background-color: rgba(255, 255, 255, 0.9);
}
</style>
