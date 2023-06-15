<template>
  <v-container class="upload-container">
    <div class="drop-area" @drop.prevent="handleFileDrop" @dragover.prevent>
      <p v-if="selectedFiles.length === 0" class="drag-text">
        <v-icon x-large class="custom-icon upload-icon large-icon">mdi-cloud-upload</v-icon><br>
        여기에 파일을 드래그하세요</p>
      <div v-else>
        <div v-for="(file, index) in selectedFiles" :key="index" class="file-item">
          <span class="file-name">{{ file.name }}</span>
          <span class="delete-file" @click="removeFile(index)">x</span>
        </div>
      </div>
      <v-btn class="upload-button" color="primary" @click="openDialog">이미지 업로드</v-btn>
    </div>

    

    <v-dialog v-model="dialog" max-width="500">
      <v-card>
        <v-card-title>이미지 업로드</v-card-title>
        <v-card-text>
          <v-text-field v-model="userId" label="사용자 아이디"></v-text-field>
          <v-text-field v-model="userPw" label="비밀번호"></v-text-field>
          <v-text-field v-model="title" label="제목"></v-text-field>
          <v-text-field v-model="uploadDate" label="등록일" readonly></v-text-field>
          <div class="file-input-container">
            <input type="file" @change="handleFileSelect" ref="fileInput" style="display: none;" multiple>
            <v-btn class="file-button" color="primary" @click="$refs.fileInput.click()">파일 선택</v-btn>
            <div v-if="selectedFiles.length > 0">
              <div v-for="(file, index) in selectedFiles" :key="index" class="file-item">
                <span class="file-name">{{ file.name }}</span>
                <span class="delete-file" @click="removeFile(index)">x</span>
              </div>
            </div>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="dialog = false">취소</v-btn>
          <v-btn color="blue darken-1" text @click="uploadImage" :disabled="isUploadDisabled">업로드</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      userId: '',
      title: '',
      uploadDate: '',
      userPw: '',
      dialog: false,
      selectedFiles: [],
      uploadCompleted: false,
    };
  },
  computed: {
    isUploadDisabled() {
      return (
        this.selectedFiles.length === 0
      );
    },
  },
  methods: {
    openDialog() {
      this.dialog = true;
      this.$nextTick(() => {
        this.$refs.fileInput.value = '';
      });
    },
    handleFileSelect(event) {
      this.selectedFiles = Array.from(event.target.files);
    },
    handleFileDrop(event) {
      const files = event.dataTransfer.files;
      if (files.length > 0) {
        this.selectedFiles = Array.from(files);
        this.dialog = true;
      }
    },
    removeFile(index) {
      this.selectedFiles.splice(index, 1);
    },
    uploadImage() {
      const formData = new FormData();
      this.selectedFiles.forEach(file => {
        formData.append('images', file);
      });
      formData.append('userId', this.userId || 'undefined');
      formData.append('userPw', this.userPw || 'undefined');
      formData.append('title', this.title || 'undefined');
      formData.append('uploadDate', this.uploadDate);
      this.$axios.post('/image', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        })
        .then(response => {
          console.log(response);
          window.location.reload(); 
        })
        .catch(error => {
          console.error(error);
        });
        this.selectedFiles = [];
        this.dialog = false;
    },    
  },
  created() {
    this.uploadDate = new Date().toLocaleString();
  },
};
</script>

<style>
.upload-container {
  text-align: center;
}
.drop-area {
  border: 2px dashed #ccc;
  padding: 50px 20px;
  text-align: center;
  cursor: pointer;
}
.file-input {
  display: none;
}
.file-item {
  display: flex;
  align-items: center;
}
.file-name {
  margin-right: 8px;
  text-align: center;
}
.delete-file {
  color: red;
  cursor: pointer;
}
.upload-button {
  margin-top: 5px;
}
</style>
