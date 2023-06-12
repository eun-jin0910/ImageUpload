<template>
    <v-container>
      <div class="table-container">
        <table>
          <colgroup>
            <col width="2%" />
            <col width="12%" />
            <col width="16%" />
            <col width="4%" />
            <col width="8%" />
            <col width="10%" />
            <col width="3%" />
            <col width="3%" />
          </colgroup>
          <thead>
            <tr>
              <th>번호</th>
              <th>제목</th>
              <th>파일명</th>
              <th>파일종류</th>
              <th>아이디</th>
              <th>등록일자</th>
              <th>수정</th>
              <th>저장</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="image in paginatedImages" :key="image.fileURL">
                <td class="text-center">{{ image.no }}</td>
                <td>
                    <div class="image-title">
                        {{ image.title }}
                        <v-icon small @click="openModal(image)">
                        mdi-image
                        </v-icon>
                    </div>
                </td>
                <td>{{ image.fileName }}</td>
                <td class="text-center">{{ image.fileType }}</td>
                <td class="text-center">{{ image.userId }}</td>
                <td class="text-center">{{ image.uploadDate }}</td>
                <td class="text-center">
                    <v-icon @click="editImage(image)">
                        mdi-square-edit-outline
                    </v-icon>
                </td>
                <td class="text-center">
                    <v-icon @click="downloadImage(image.fileURL)">
                        mdi-download
                    </v-icon>
                </td>
            </tr>
          </tbody>
        </table>
      </div>
      <v-pagination
      v-model="currentPage"
      :total-visible="5"
      :length="Math.ceil(sortedImages.length / itemsPerPage)"
      @input="paginateImages"
      class="pagination"
      size="small"
      prev-icon="mdi-chevron-left"
      next-icon="mdi-chevron-right"
      prev-icon-disabled="mdi-chevron-left"
      next-icon-disabled="mdi-chevron-right"
    ></v-pagination>
    </v-container>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        images: [],
        currentPage: 1,
        itemsPerPage: 20,
      };
    },
    created() {
      this.fetchImages();
    },
    computed: {
        paginatedImages() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            const end = start + this.itemsPerPage;
            return this.sortedImages.slice(start, end);
        },
        sortedImages() {
            return this.images.slice().sort((a, b) => new Date(b.uploadDate) - new Date(a.uploadDate)).map((image, index, array) => {
                image.no = array.length - index;
                return image;
            });
        },

    },
    methods: {
      fetchImages() {
        axios
          .get('http://localhost:8080/images')
          .then(response => {
            this.images = response.data;
          })
          .catch(error => {
            console.error(error);
          });
      },
      downloadImage(fileURL) {
     
          console.log('Downloading image:', fileURL);
        },
    paginateImages() {
      this.currentPage = 1; 
    },
    },
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
  .pagination {
  margin-top: 10px;
  text-align: center;
}
  </style>
  
