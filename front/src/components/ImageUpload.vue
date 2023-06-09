<template>
    <v-container>
        <div class="">
            <h1>이미지 업로드</h1>
            <input type="file" @change="handleFileSelect">
        </div>
        <div>
            <div class="image-grid">
                <img v-for="image in images" :key="image.fileURL" :src="image.fileURL" alt="Image" 
                class="image" id="image" accept="image/png, image/gif, image/jpeg" multiple>
            </div>
        </div>
    </v-container>
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
        },

        handleFileSelect(event) {
            console.log("handleFileSelect");
            const file = event.target.files[0];
            console.log('file', file);

            const formData = new FormData();
            formData.append('image', file);
            // formData.append('userId', 'asdf');

            console.log('formData', formData);
            console.log(formData.get('image'));

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
    margin-left: 0 !important;
    padding: 0 !important;
    border-radius: 4px;
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.2);
    width: 200px;
    height: 200px;
    object-fit: cover;
}
</style>
  