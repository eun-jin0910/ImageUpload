<template>
  <v-card>
    <v-card-title>이미지 수정</v-card-title>
    <v-card-text>
      <v-form ref="form">
        <v-text-field v-model="title" label="제목"></v-text-field>
        <v-text-field v-model="password" label="비밀번호"></v-text-field>
        <div class="file-input-container">
          <input type="file" @change="handleFileSelect" ref="fileInput" style="display: none;">
          <v-btn class="file-button" color="primary" @click="$refs.fileInput.click()" :disabled="selectedFiles.length > 0">파일 선택</v-btn>
          <div v-if="selectedFiles.length === 1">
            <div class="file-item">
              <span class="file-name">{{ selectedFiles[0].name }}</span>
              <span class="delete-file" @click="removeFile(0)">x</span>
            </div>
          </div>
        </div>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="blue darken-1" text @click="closeModal">취소</v-btn>
      <v-btn color="blue darken-1" text @click="editImage">저장</v-btn>
    </v-card-actions>
  </v-card>
</template>
  
<script>
export default {
  props: {
    image: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      title: '',
      uploadDate: '',
      password: '',
      selectedFiles: [],
    };
  },
  created() {
    this.uploadDate = new Date().toLocaleString();
    this.title = this.image.title;
    this.selectedFiles = [{ name: this.image.fileURL }]; 
  },
  methods: {
    onFileInputChange(event) {
      this.file = event.target.files[0];
    },
    editImage() {
      const selectedFile = this.selectedFiles[0];
      console.log('selectedFile', selectedFile);
      const formData = new FormData();
      formData.append('id', this.image.id);
      formData.append('password', this.password || 'undefined');
      formData.append('title', this.title || 'undefined');
      if (selectedFile.name != this.image.fileURL) {
        console.log(this.image.fileURL);
        formData.append('image', selectedFile);
        this.$axios.put('/image', formData)
        .then(response => {
          console.log(response);
          this.$emit('close');
          this.$refs.form.reset();
        })
        .catch(error => {
          console.log(error);
        });
      } else {
        this.$axios.patch('/image', formData)
        .then(response => {
          console.log(response);
          this.$emit('close');
          this.$refs.form.reset();
        })
        .catch(error => {
          console.log(error);
        })
      }
    },
    handleFileSelect(event) {
      this.selectedFiles = Array.from(event.target.files); 
    },
    removeFile(index) {
      this.selectedFiles.splice(index, 1); 
    },
    closeModal() {
      this.$emit('close');
      this.$refs.form.reset();
    }
  }
};
</script>

<style>
.file-item {
  display: inline-block;
}
.file-name {
  text-align: left;
}
</style>
  