<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title id="title">IMAGE UPLOAD</v-toolbar-title>
    </v-app-bar>
    <v-navigation-drawer v-model="drawer" app class="menu-drawer">
      <v-list dense class="menu-list">
        <v-list-item-group v-model="selectedItem">
          <v-list-item v-for="(item, index) in menuItems" :key="index" :to="item.route">
            <v-list-item-icon>
              <v-icon class="menu-icon">{{ item.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-navigation-drawer>
    <v-main>
      <v-container fluid>
        <v-row>
          <v-col>
            <router-view></router-view>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
export default {
  data() {
    return {
      drawer: true, 
      selectedItem: null,
      menuItems: [
        { title: 'HOME', route: '/', icon: 'mdi-home' },
        { title: 'IMAGE', route: '/images', icon: 'mdi-image' },
        { title: 'UPLOAD', route: '/upload', icon: 'mdi-cloud-upload' },
      ],
    };
  },
};
</script>

<style>
@font-face {
  font-family: 'Noto Sans KR', sans-serif;
  src: url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap');
}
@font-face {
  font-family: 'NanumSquareNeo-Variable';
  src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_11-01@1.0/NanumSquareNeo-Variable.woff2')
    format('woff2');
  font-weight: normal;
  font-style: normal;
}
* {
  font-family: 'NanumSquareNeo-Variable';
}
#title {
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 700;
}
.menu-drawer {
  width: 200px;
}
.menu-list {
  padding: 0;
}
.menu-list .v-list-item {
  padding: 0;
}
.menu-list .v-list-item .v-list-item-icon {
  min-width: unset;
}
.menu-list .v-list-item .v-list-item-content {
  margin-left: 12px;
}
.menu-list .v-list-item .v-list-item-title {
  font-size: 14px;
  font-weight: 500;
}
.menu-icon {
  padding-left: 15px;
}
</style>
