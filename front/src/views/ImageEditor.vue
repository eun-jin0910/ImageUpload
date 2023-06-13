<template>
  <v-container>
    <div class="img-container">
      <img :src="image.fileURL" :alt="image.fileName" class="edit-img"/>
      <v-card>
        <v-card-title>{{ image.title }}</v-card-title>
        <v-card-text>
          <p><strong>파일명:</strong> {{ image.fileName }}</p>
          <p><strong>아이디:</strong> {{ image.userId }}</p>
          <p><strong>등록일자:</strong> {{ image.uploadDate }}</p>
          <p><strong>파일종류:</strong> {{ image.fileType }}</p>
          <p><strong>파일경로:</strong> {{ image.fileURL }}</p>
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
      image: null
    };
  },
  created() {
    this.image = this.$route.params.image;
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    goToEdit() {
      this.$router.push({ path: '/edit', params: { image: this.image } });
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
</style>
