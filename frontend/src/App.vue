<template>
  <div id="app">
    <Navbar :isNavbar="isNavbar" />
    <Main :isMain="isMain" />
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
  data() {
    return {
      isNavbar: true,
      isMain: true,
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
      let userArray = [constants.URL_TYPE.USER.LOGIN, constants.URL_TYPE.USER.JOIN]
      let mainArray = [
        constants.URL_TYPE.USER.MYPAGE, 
        constants.URL_TYPE.USER.EDIT,
        constants.URL_TYPE.POST.CREATE,
        constants.URL_TYPE.POST.DETAIL,
        constants.URL_TYPE.POST.TEMPORARY,
        constants.URL_TYPE.POST.RECOMMEND,
        constants.URL_TYPE.POST.KIND,
        constants.URL_TYPE.POST.SEARCH
      ]

      let isNavbar = true
      let isMain = true
      userArray.map(path => {
        if (url === path) {
          isNavbar = false
          isMain = false
        }
      })
      mainArray.map(path => {
        if (url === path) {
          isMain = false
        }
      })
      this.isNavbar = isNavbar
      this.isMain = isMain
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

#app:after {
  content: '';
  display: block;
  padding-bottom: 100px;
}
</style>