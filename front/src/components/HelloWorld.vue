<template>
  <v-container>
      <h1>이미지 업로드</h1>
      <input type="file" @change="handleFileSelect">
  </v-container>
</template>

<script>
import axios from 'axios';
export default {
  methods: {
    handleFileSelect(event) {
    console.log("handleFileSelect");
    const image = event.target.files[0];
    const formData = new FormData();
    formData.append('imageFile', image);
      
    axios.post('http://localhost:8080/image', formData, {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      })
      .then(response => {
        console.log(response);
      })
      .catch(error => {
        console.error(error);
      });
    }
  }
}
</script>

<style>
</style>
