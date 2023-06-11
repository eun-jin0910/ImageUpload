<template>
    <v-container>
      <div class="">
        <!-- <h1>이미지 업로드</h1> -->
        <v-btn color="primary" @click="openDialog">이미지 업로드</v-btn>
      </div>
  
      <v-dialog v-model="dialog" max-width="500">
        <v-card>
          <v-card-title>이미지 업로드</v-card-title>
          <v-card-text>
            <v-text-field v-model="userId" label="사용자 이름"></v-text-field>
            <v-text-field v-model="uploaderPw" label="비밀번호"></v-text-field>
            <v-text-field v-model="uploaderTitle" label="제목"></v-text-field>
            <v-text-field v-model="currentDateTime" label="등록일" readonly></v-text-field>
            <input type="file" @change="handleFileSelect">
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="dialog = false">취소</v-btn>
            <v-btn color="blue darken-1" text @click="uploadImage">업로드</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        dialog: false,
        selectedFile: null,
        uploaderName: '',
        uploaderTitle: '',
        currentDateTime: '',
      };
    },
    methods: {
      openDialog() {
        this.dialog = true;
      },
      handleFileSelect(event) {
        this.selectedFile = event.target.files[0];
      },
      uploadImage() {
        const formData = new FormData();
        formData.append('image', this.selectedFile);
        formData.append('uploaderName', this.uploaderName);
        formData.append('uploaderTitle', this.uploaderTitle);
        formData.append('currentDateTime', this.currentDateTime);
  
        axios
          .post('http://localhost:8080/image', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          })
          .then(response => {
            console.log(response);
          })
          .catch(error => {
            console.error(error);
          });
  
        this.dialog = false;
      },
    },
    created() {
      this.currentDateTime = new Date().toLocaleString();
    },
  };
  </script>
  
  <style>
  </style>
  