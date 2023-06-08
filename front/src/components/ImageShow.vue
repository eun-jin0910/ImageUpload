<template>
    <div class="image-grid">
      <img v-for="image in images" :key="image.fileURL" :src="image.fileURL" alt="Image" class="image">
      
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
        images: []
        };
    },
    created() {
        this.fetchImages();
    },
    methods: {
        fetchImages() {
        axios.get('http://localhost:8080/images')
            .then(response => {
            this.images = Object.values(response.data);
            })
            .catch(error => {
            console.error(error);
            });
        }
    }
};
</script>

<style>
.image-grid {
    display: grid;
    grid-template-columns: repeat(10, 1fr);
    gap: 10px;
}
.image {
    margin: 20px;
    margin-right: 0 !important;
    padding: 0 !important;
    border-radius: 4px;
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.2);
    width: 200px;
    height: 200px;
    object-fit: cover;
}
</style>