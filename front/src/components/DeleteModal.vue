<template>
  <v-card>
    <v-card-title>이미지를 삭제하시겠습니까?</v-card-title>
    <v-card-text>
      <v-form ref="form">
        <v-text-field
          v-model="password"
          label="비밀번호"
          type="password"
          outlined
          dense
          required
        ></v-text-field>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-btn color="error" text @click="deleteImage">삭제</v-btn>
      <v-spacer></v-spacer>
      <v-btn text @click="closeModal">취소</v-btn>
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
        password: ''
      };
    },
    methods: {
      deleteImage() {
        const requestData = {
          id: this.image.id,
          password: this.password || "undefined",
        };
        console.log("ID : " + this.image.id);
        console.log("PW : " + this.password);
        this.$axios.delete('/image', {
            data: requestData,
            headers: {
            'Content-Type': 'application/json'
            }
          })
          .then(response => {
              console.log(response);
              this.$emit('delete', this.image.id);
              this.closeModal();
              })
          .catch(error => {
          console.log(error);
          this.error = '이미지 삭제에 실패했습니다.';
        });
      },
      closeModal() {
        this.$emit('close');
        this.$refs.form.reset();
      }
    }
  };
</script>

<style>

</style>
  