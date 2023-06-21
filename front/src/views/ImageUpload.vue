<template>
  <v-container class="upload-container">
    <div class="drop-area" @drop.prevent="handleFileDrop" @dragover.prevent>
      <p class="drag-text">
        <v-icon x-large class="custom-icon upload-icon large-icon" id="upload-icon">mdi-cloud-upload</v-icon><br>
        여기에 파일을 드래그하세요</p>
      <v-btn class="upload-button" color="primary" @click="openDialog">이미지 업로드</v-btn>
    </div>
    <v-dialog v-model="dialog" max-width="400">
      <v-card>
        <v-card-title>이미지 업로드</v-card-title>
        <v-card-text>
          <v-text-field v-model="title" label="제목"></v-text-field>
          <v-text-field v-model="password" label="비밀번호"></v-text-field>
          <div class="file-input-container">
            <input type="file" @change="handleFileSelect" ref="fileInput" style="display: none;" accept="image/*" multiple>
            <v-btn class="file-button" color="primary" @click="$refs.fileInput.click()">파일 선택</v-btn>
            <div v-if="selectedFiles.length > 0">
              <div v-for="(file, index) in selectedFiles" :key="index">
                <div class="file-item">
                  <span class="file-name">{{ file.name }}</span>
                  <span class="delete-file" @click="removeFile(index)">x</span>
               </div>
              </div>
            </div>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="closeDialog">취소</v-btn>
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
      title: '',
      password: '',
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
    },
    closeDialog() {
      this.selectedFiles = [];
      this.dialog = false;
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
      const startTime = performance.now();
      console.log("업로드 시작");
      const uploadPromises = this.selectedFiles.map(file => {
        console.log("단일 이미지 파일 업로드 시작");
        const formData = new FormData();
        formData.append('images', file);
        formData.append('password', this.password || 'undefined');
        formData.append('title', this.title || 'undefined');
        return this.$axios.post('/image', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
      });
      Promise.all(uploadPromises)
        .then(responses => {
          console.log(responses); 
        })
        .catch(error => {
          console.error(error);
        })
        .finally(() => {
          console.log("모든 업로드 완료");
          const uploadTime = (performance.now() - startTime) / 1000;
          console.log(`작업 수행 시간: ${uploadTime}초`);
          this.$EventBus.$emit('upload');
          this.selectedFiles = [];
          this.dialog = false;
        });
      },
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
  margin-left: 3px;
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
