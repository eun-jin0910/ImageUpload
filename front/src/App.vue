<template>
  <v-app>
    <layout-header></layout-header>
    <v-main>
      <v-container fluid class="main-content">
        <router-view></router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import LayoutHeader from './components/LayoutHeader.vue';
export default {
  components: { 
    LayoutHeader, 
  },
};
</script>

<style>
</style>
