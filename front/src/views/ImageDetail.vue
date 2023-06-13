<template>
  <v-container>
    <div class="img-container">
      <img :src="image.fileURL" :alt="image.fileName" class="edit-img"/>
      <v-card>
        <br>
        <v-card-title>{{ image.title }}</v-card-title>
        <v-card-text>
          <br>
          <table class="info-table">
            <tbody>
              <tr>
                <td><strong>파일명</strong></td>
                <td>{{ image.fileName }}</td>
              </tr>
              <tr>
                <td><strong>아이디</strong></td>
                <td>{{ image.userId }}</td>
              </tr>
              <tr>
                <td><strong>등록일자</strong></td>
                <td>{{ image.uploadDate }}</td>
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
      <v-btn color="primary" @click="goToEdit" class="btn">수정하기</v-btn>
    </v-row>
  </v-container>
</template>


<script>
export default {
  data() {
    return {
      image: null,
      editing: false,
    };
  },
  created() {
    this.image = this.$route.params.image;
    const hash = window.location.hash;
    const value = hash.slice(2); // 앞의 '/#' 제거
    this.fetchImage(value);
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    goToEdit() {
      this.editing = true;
    },
    saveChanges() {
      
    }
  }
};

</script>

<style scoped>
.img-container {
  display: flex;
}
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
