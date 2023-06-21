<template>
  <v-app>
    <v-container>
      <v-row align="center" justify="space-between">
        <v-col cols="auto">
          <v-btn-toggle v-model="activeButton">
            <v-btn value="image-board">
              <v-icon size="24">mdi-image</v-icon>
            </v-btn>
            <v-btn value="image-list">
              <v-icon size="24">mdi-view-list</v-icon>
            </v-btn>
          </v-btn-toggle>
        </v-col>
        <v-col class="delete-button-col" cols="auto">
          <v-btn class="delete-button" @click="deleteAll"><strong>전체삭제</strong></v-btn>
        </v-col>
      </v-row>
    </v-container>
    <component :is="activeButton"/>
  </v-app>
</template>

<script>
import ImageList from './ImageList.vue';
import ImageBoard from './ImageBoard.vue';

export default {
  components: {
    ImageList,
    ImageBoard
  },
  data() {
    return {
      activeButton: 'image-board'
    };
  },
  methods: {
    deleteAll() {
      this.$axios.delete('/images')
      .then(response => {
        console.log(response);
        this.$EventBus.$emit('deleteAll');
      })
      .catch(error => {
        console.log(error);
      })
    }
  }
};
</script>

<style>
.delete-button-col {
  text-align: right;
}
</style>
