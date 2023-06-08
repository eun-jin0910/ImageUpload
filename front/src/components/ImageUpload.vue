<template>
    <v-container>
        <div class="">
            <h1>이미지 업로드</h1>
            <input type="file" @change="handleFileSelect">
        </div>
        <div class="image-grid">
            <img v-for="image in images" :key="image.fileURL" :src="image.fileURL" alt="Image" class="image">
        </div>
    </v-container>
</template>
  
<script>
// import axios from 'axios';
//   export default {
//     methods: {
//         handleFileSelect(event) {
//         console.log("handleFileSelect");
//         const image = event.target.files[0];
//         const formData = new FormData();
//         formData.append('imageFile', image);
//         axios({
//         url: 'http://localhost:8080/image',
//         method: 'post',
//         data: {
//             foo: 'diary'
//         }
//         });
//         axios.post('http://localhost:8080/image', formData, {
//           headers: {
//             "Content-Type": "multipart/form-data"
//           }
//         })
//         .then(response => {
//           console.log(response);
//         })
//         .catch(error => {
//           console.error(error);
//         });
//       }
//     }
//   }
// import axios from "axios"
// axios.defaults.baseURL = "http://localhost:8080/";
// export default {
//     methods: {
//         handleFileSelect(event) {
//             const imageFile = event.target.files[0];
//             const formData = new FormData();
//             formData.append('imageFile', imageFile);
//             const config = {
//                 status: 200,
//                 statusText: 'OK',
//                 headers: {}
//             }
            
//             axios.post('/image', formData, config)
//             .then(response => console.log(response))
//             .catch(error => console.log(error));
            
//         }
//     }
// }
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
            const imageFile = event.target.files[0];
            const formData = new FormData();
            formData.append('imageFile', imageFile);

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
    margin-right: 0 !important;
    padding: 0 !important;
    border-radius: 4px;
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.2);
    width: 200px;
    height: 200px;
    object-fit: cover;
}
</style>
  