<template>
  <v-container>
    <div class="table-container">
      <table>
        <colgroup>
          <col width="2%" />
          <col width="11%" />
          <col width="16%" />
          <col width="4%" />
          <col width="10%" />
          <col width="2%" />
          <col width="2%" />
        </colgroup>
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>파일명</th>
            <th>파일종류</th>
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
                      <router-link :to="{ name: 'ImageDetail', params: { id: image.id } }">
                        <v-icon small>
                        mdi-image
                        </v-icon>
                      </router-link>
                  </div>
              </td>
              <td>{{ image.fileName }}</td>
              <td class="text-center">{{ image.fileType }}</td>
              <td class="text-center">{{ formatDate(image.uploadDate) }}</td>
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
import DeleteModal from '../components/DeleteModal.vue';
import moment from 'moment';

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
      this.$axios.get('/images')
      .then(response => {
        this.images = response.data;
      })
      .catch(error => {
        console.error(error);
      });
    },
    downloadImage(fileURL) {
      this.$axios.get(fileURL, { responseType: 'blob' }) 
      .then(response => {
        console.log('fileURL', fileURL);
        const blob = response.data;
        const url = URL.createObjectURL(blob); // Blob 객체 URL 생성
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'image.jpg');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url); // Blob 객체 URL 해제
      })
      .catch(error => {
        console.error(error);
      });
    },
    formatDate(date) {
      return moment(date).format('YYYY. M. D. A hh:mm:ss');
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
