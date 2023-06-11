<template>
    <v-container>
        <div class="table-container">
            <table>
            <colgroup>
                <col width="2%" />
                <col width="10%" />
                <col width="10%" />
                <col width="8%" />
                <col width="8%" />
            </colgroup>
            <thead>
                <tr>
                    <th>번호</th>
                    <th>제목</th>
                    <th>파일명</th>
                    <th>아이디</th>
                    <th>등록일자</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="image in images" :key="image.fileURL">
                    <td>{{ image.no }}</td>
                    <td>{{ image.title }}</td>
                    <td>{{ image.fileName }}</td>
                    <td>{{ image.userId }}</td>
                    <td>{{ image.uploadDate }}</td>
                </tr>
            </tbody>
        </table>
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
            this.images = response.data;
        })
        .catch(error => {
            console.error(error);
        });
    }
}
};
</script>

<style>
table,
td,
th {
    border: 0.5px solid #ccc;
    border-collapse: collapse;
    padding: 2px 8px;
}
thead {
    background-color: #f7f7f7;
}
</style>
