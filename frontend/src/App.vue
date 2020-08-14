<template>
  <div id="app">
    <Navbar :isNavbar="isNavbar" />
    <Main />
    <router-view />
  </div>
</template>

<script>
import './assets/css/style.css'
import Navbar from './components/common/Navbar.vue'
import Main from './components/common/Main.vue'
import constants from './lib/constants'
import { mapActions } from 'vuex'


export default {
  name: 'App',
  components: {
    Navbar,
    Main
  },
  data: function() {
    return {
      isNavbar: true,
      constants
    };
  },
  computed: {
  },
  watch: {
    $route(to) {
      this.checkUrl(to.name);
    }
  },
  methods: {
    ...mapActions(['connectWebsocket', 'sendPostId', 'getArticles']),
    checkUrl(url) {
      let array = [constants.URL_TYPE.USER.LOGIN, constants.URL_TYPE.USER.JOIN];

      let isNavbar = true;
      array.map(path => {
        if (url === path) isNavbar = false;
      });
      this.isNavbar = isNavbar;
    }
  },
  created() {
    this.connectWebsocket()
    let url = this.$route.name
    this.checkUrl(url)
    this.getArticles()
  },
  mounted() {
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--secondary-color);
}
</style>