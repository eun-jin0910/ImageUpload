<template>
  <v-container>
    <div class="table-container">
      <table>
        <colgroup>
          <col width="2%" />
          <col width="15%" />
          <col width="13%" />
          <col width="5%" />
          <col width="8%" />
          <col width="11%" />
          <col width="2%" />
          <col width="2%" />
        </colgroup>
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>파일명</th>
            <th>파일종류</th>
            <th>아이디</th>
            <th>등록일자</th>
            <th>저장</th>
            <th>삭제</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="image in sortedImages" :key="image.fileURL">
              <td class="text-center">{{ image.no }}</td>
              <td>
                  <div class="image-title">
                      {{ image.title }}
                      <router-link :to="{ name: 'ImageDetail', params: { image: image, fileURL: image.fileURL } }">
                        <v-icon small @click="openModal(image)">
                        mdi-image
                        </v-icon>
                      </router-link>
                  </div>
              </td>
              <td>{{ image.fileName }}</td>
              <td class="text-center">{{ image.fileType }}</td>
              <td class="text-center">{{ image.userId }}</td>
              <td class="text-center">{{ image.uploadDate }}</td>
              <td class="text-center">
                  <v-icon class="blue--text" @click="downloadImage(image.fileURL)">
                      mdi-download
                  </v-icon>
              </td>
              <td class="text-center">
                <v-icon small class="red--text" @click="openModal(image)">
                  mdi-close
                </v-icon>
              </td> 
          </tr>
        </tbody>
      </table>
    </div>
    <v-dialog v-model="modalOpen" max-width="400">
      <delete-modal :image="selectedImage" @close="closeModal" />
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';
import DeleteModal from '../components/DeleteModal.vue';

export default {
  components: { DeleteModal },
  data() {
    return {
      images: [],
      modalOpen: false,
      selectedImage: null
    };
  },
  created() {
    this.fetchImages();
  },
  computed: {
      sortedImages() {
        return this.images.slice().sort((a, b) => new Date(b.uploadDate) - new Date(a.uploadDate)).map((image, index, array) => {
          image.no = array.length - index;
          return image;
        });
      },

  },
  methods: {
    fetchImages() {
      axios
        .get('http://localhost:8080/images')
        .then(response => {
          this.images = response.data;
        })
        .catch(error => {
          console.error(error);
        });
    },
    downloadImage(fileURL) {
      console.log('Downloading image:', fileURL);
    },
    openModal(image) {
      this.selectedImage = image;
      this.modalOpen = true;
    },
    deleteImage() {
      const fileURL = this.selectedImage.fileURL;
      console.log('Deleting image:', fileURL);
      this.closeModal();
    },
    closeModal() {
      this.modalOpen = false;
    }
  },
};
</script>

<style>
table,
td,
th {
  border: 0.5px solid #ccc;
  border-collapse: collapse;
  padding: 2px 8px;
}
thead {
  background-color: #f7f7f7;
}
</style>
