<template>
    <v-container>
        <v-row>
            <v-col cols="12" md="10">
            <v-text-field
                v-model="searchQuery"
                label="이미지 검색"
                outlined
                dense
                @input="handleSearch"
            >
                <template #prepend-inner>
                <v-icon color="grey lighten-1">mdi-magnify</v-icon>
                </template>
            </v-text-field>
            </v-col>
            <v-col cols="12" md="2">
            <v-btn
                color="primary"
                @click="handleSubmit"
            >
                검색
            </v-btn>
            </v-col>
        </v-row>
    </v-container>
  </template>
  
  <script>

  </script>
  
  <style>
  </style>