<template>
  <v-container>
    <div class="img-container">
      <img :src="image && image.fileURL" :alt="image && image.fileName" class="edit-img"/>
      <v-card>
        <br>
        <v-card-title class="image-title">{{ image.title }}</v-card-title>
        <v-card-text>
          <br>
          <table class="info-table">
            <tbody>
              <tr>
                <td><strong>파일명</strong></td>
                <td>{{ image.fileName }}</td>
              </tr>
              <tr>
                <td><strong>등록일자</strong></td>
                <td>{{ formatDate(image.uploadDate) }}</td>
              </tr>
              <tr>
                <td><strong>파일종류</strong></td>
                <td>{{ image.fileType }}</td>
              </tr>
              <tr>
                <td><strong>파일경로</strong></td>
                <td>{{ image.fileURL }}</td>
              </tr>
            </tbody>
          </table>
        </v-card-text>
      </v-card>
    </div>
    <v-row class="button-row">
      <v-btn color="primary" @click="goBack" class="btn">이전으로</v-btn>
      <v-btn color="primary" @click="openModal(image)" class="btn" >수정하기</v-btn>
    </v-row>
    <v-dialog v-model="modalOpen" max-width="400">
      <edit-modal :image="image" @close="closeModal" />
    </v-dialog>
  </v-container>
</template>

<script>
import EditModal from '../components/EditModal.vue';
import moment from 'moment';

export default {
  components: { EditModal },
  data() {
    return {
      image: {},
      editing: false,
      modalOpen: false,
    };
  },
  mounted() {
    console.log(this.$route);
    this.fetchImage(this.$route.params.id);
  },
  methods: {
    fetchImage(id) {
      this.$axios.get('/image/' + id)
      .then(response => {
        this.image = response.data
        console.log(response)
      })
      .catch(error => {
        console.log(error)
      })
    },
    formatDate(date) {
      return moment(date).format('YYYY. M. D. A hh:mm:ss');
    },
    goBack() {
      this.$router.go(-1);
    },
    goToEdit() {
      this.editing = true;
    },
    openModal(image) {
      this.image = image;
      this.modalOpen = true;
    },
    closeModal() {
      this.modalOpen = false;
      window.location.reload();
    }
  }
};

</script>

<style scoped>
.img-container {
  display: flex;
  justify-content: center;
  width: 100%;
  max-width: 1000px;
  margin: 25px auto;
}
.edit-img {
  max-width: 600px;
  max-height: 600px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}
.image-title {
  margin-left: 6px;
  font-size: 2rem;
}
.button-row {
  justify-content: center;
  margin-top: 30px;
}
.btn {
  margin: 10px;
}
.info-table {
  border-collapse: collapse;
  width: 100%;
  border: none;
}
.info-table td {
  padding: 8px;
  border: none; 
  vertical-align: top;
}
.info-table td:first-child {
  font-weight: bold;
  white-space: nowrap;
  width: 30px; 
}
.info-table tr:last-child td {
  border-bottom: none;
}

.info-table tr:last-child td {
  border-bottom: none;
}
</style>
