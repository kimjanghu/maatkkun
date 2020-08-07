<template>
  <div id="app">
    <Navbar :isNavbar="isNavbar" />
    <Main />
    <router-view />
  </div>
</template>

<script>
import './assets/css/style.css';
import Navbar from './components/common/Navbar.vue';
import Main from './components/common/Main.vue'
import constants from './lib/constants';

export default {
  name: 'App',
  components: {
    Navbar,
    Main
  },
  created() {
    let url = this.$route.name;

    this.checkUrl(url);
    //  this.islogin=this.$cookies.isKey('uid')
  },
  watch: {
    $route(to) {
      this.checkUrl(to.name);
    }
  },
  methods: {
    checkUrl(url) {
      let array = [constants.URL_TYPE.USER.LOGIN, constants.URL_TYPE.USER.JOIN];

      let isNavbar = true;
      array.map(path => {
        if (url === path) isNavbar = false;
      });
      this.isNavbar = isNavbar;
    }
  },
  data: function() {
    return {
      isNavbar: true,
      constants
    };
  }
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