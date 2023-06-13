<template>
    <v-dialog v-model="isOpen" max-width="500">
      <v-img :src="selectedImage.fileURL" :alt="selectedImage.fileName" class="modal-image" />
  
      <v-card>
        <v-card-title>{{ selectedImage.title }}</v-card-title>
        <v-card-text>
          <p><strong>파일명:</strong> {{ selectedImage.fileName }}</p>
          <p><strong>아이디:</strong> {{ selectedImage.userId }}</p>
          <p><strong>등록일자:</strong> {{ selectedImage.uploadDate }}</p>
        </v-card-text>
      </v-card>
  
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="closeModal">닫기</v-btn>
      </v-card-actions>
    </v-dialog>
</template>

<script>
export default {
  props: {
    modalOpen: {
      type: Boolean,
      required: true
    },
  },
  data() {
    return {
      isOpen: false,
    };
  },
  watch: {
    modalOpen(value) {
        console.log("열렸다느ㅡㄴ데"+value);
      this.isOpen = value;
    }
  },
  methods: {
    closeModal() {
        console.log("닫힘");
      this.isOpen = false;
    //   this.$emit('close');
    }

  }
};
</script>
  
<style>
  .modal-image {
    max-width: 100%;
    max-height: 500px;
    object-fit: contain;
  }
</style>
  