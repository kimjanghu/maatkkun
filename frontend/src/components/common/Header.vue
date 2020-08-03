<template>
  <div id="header" v-if="isHeader">
    <h1>
      <router-link :to="{ name: constants.URL_TYPE.POST.MAIN }">MAAT KKUN</router-link>
    </h1>
    <div class="right">
      <div v-if="!isLoggedIn">
        <router-link class="nav-link" :to="{ name: constants.URL_TYPE.USER.LOGIN }">Login</router-link>
      </div>
      <div v-if="isLoggedIn">
        <router-link class="nav-link" :to="{ name:constants.URL_TYPE.POST.CREATE }">Create</router-link>
        <router-link class="nav-link" :to="{ name:constants.URL_TYPE.USER.LOGOUT }">Logout</router-link>
        <router-link class="nav-link" :to="{ name:constants.URL_TYPE.USER.MYPAGE, params:{ id: loginId }}">Mypage</router-link>
      </div>
    </div>
  </div>
</template>   

<script>
// import axios from 'axios'
import constants from '@/lib/constants'
import '@/assets/css/header.css'
// import Login from '@/views/user/Login.vue'
import { mapGetters } from 'vuex'

// const SERVER_URL = "http://localhost:8080"

export default {
  name: 'Header',
  mounted() {},
  components: {},
  props: {
    isHeader: {
      type: Boolean
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  watch: {},
  created() {},
  methods: {},
  data: function() {
    return {
      constants,
      loginId: this.$cookies.get('auth-token'),
      keyword: '',
    };
  }
};
</script>

<style scoped>
.nav-link {
  margin-left: 20px;
}
</style>
