<template>
  <v-container>
    <div class="table-container">
      <table>
        <colgroup>
          <col width="2%" />
          <col width="10%" />
          <col width="18%" />
          <col width="4%" />
          <col width="12%" />
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
          <tr v-for="image in paginatedImages" :key="image.fileURL">
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
      <delete-modal :image="selectedImage" @close="closeModal" @delete="deleteImage"/>
    </v-dialog>
    <v-pagination v-model="currentPage" :total-visible="7" :length="totalPages" @input="changePage" class="pagination"/>
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
      selectedImage: null,
      currentPage: 1,
      pageSize: 20
    };
  },
  created() {
    this.fetchImages();
    this.$EventBus.$on('deleteAll', this.handleDeleteAll);
    this.$EventBus.$on('upload', this.fetchImages);
  },
  computed: {
    sortedImages() {
      return this.images.slice().sort((a, b) => new Date(b.uploadDate) - new Date(a.uploadDate)).map((image, index, array) => {
        image.no = array.length - index;
        return image;
      });
    },
    paginatedImages() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      const endIndex = startIndex + this.pageSize;
      return this.sortedImages.slice(startIndex, endIndex);
    },
    totalPages() {
      return Math.ceil(this.sortedImages.length / this.pageSize);
    }
  },
  methods: {
    handleDeleteAll() {
      this.images = [];
    },
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
    deleteImage(id) {
      this.images = this.images.filter(e => e.id !== id)
      this.closeModal();
    },
    closeModal() {
      this.modalOpen = false;
    },
    changePage(page) {
      this.currentPage = page;
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
.pagination {
  padding-top: 5px;
}

</style>
