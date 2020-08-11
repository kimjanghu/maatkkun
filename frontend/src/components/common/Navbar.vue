<template>
  <div id="navbar" v-if="isNavbar">
    <h1>
      <!-- <router-link @click.native="changeMain(true)" :to="{ name: constants.URL_TYPE.POST.MAIN }">MAAT KKUN</router-link> -->
      <a href="/">MAAT KUUN</a>
    </h1>
    <div class="right">
      <div v-if="!isLoggedIn">
        <router-link class="nav-link" :to="{ name: constants.URL_TYPE.USER.LOGIN }">Login</router-link>
      </div>
      <div v-if="isLoggedIn">
        <router-link class="nav-link" :to="{ name:constants.URL_TYPE.POST.CREATE }">Create</router-link>
        <router-link class="nav-link" :to="{ name:constants.URL_TYPE.USER.LOGOUT }">Logout</router-link>
        <!-- <a href="/user/logout" class="nav-link">Logout</a> -->
        <router-link class="nav-link" :to="{ name:constants.URL_TYPE.USER.MYPAGE, params:{ id: loginId }}">Mypage</router-link>
      </div>
    </div>
  </div>
</template>   

<script>
import constants from '@/lib/constants'
import '@/assets/css/navbar.css'
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'Navbar',
  components: {},
  props: {
    isNavbar: {
      type: Boolean
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  watch: {},
  methods: {
    ...mapActions(['changeMain', 'sendPostId']),
  },
  data() {
    return {
      constants,
      loginId: this.$cookies.get('auth-token')
    }
  },
  created() {
    // console.log(JSON.parse(window.localStorage.getItem('userInfo')))
  },
  
  mounted() {
  },
  updated() {
    // console.log(JSON.parse(window.localStorage.getItem('userInfo')).uid)
  },
};
</script>

<style scoped>
.nav-link {
  margin-left: 20px;
}
</style>
