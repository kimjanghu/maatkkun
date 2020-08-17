<template>
  <div id="navbar" v-if="isNavbar">
    <h1>
      <a class="main-title" href="/">MAATKKUN</a>
    </h1>
    <div class="right">
      <div v-if="!isLoggedIn">
        <router-link class="nav-link" :to="{ name: constants.URL_TYPE.USER.LOGIN }">Login</router-link>
      </div>
      <div v-if="isLoggedIn">
        <router-link class="nav-link" :to="{ name:constants.URL_TYPE.POST.CREATE }">Create</router-link>
        <router-link class="nav-link" :to="{ name:constants.URL_TYPE.USER.LOGOUT }">Logout</router-link>
        <router-link class="nav-link" :to="{ name:constants.URL_TYPE.USER.MYPAGE, params:{ id: loginId }}"><i class="fas fa-user-circle fa-lg"></i> {{ nickname }}</router-link>
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
  data() {
    return {
      constants,
      loginId: this.$cookies.get('auth-token'),
      nickname: null
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  watch: {},
  methods: {
    ...mapActions(['changeMain', 'sendPostId']),
    setNickname() {
      const nickname = JSON.parse(window.localStorage.getItem('userInfo'))
      if (nickname) {
        this.nickname = nickname.nickname
      }
    }
  },
  created() {
    this.setNickname()
  },
  mounted() {
  },
  updated() {
  },
};
</script>

<style scoped>
.nav-link {
  margin-left: 20px;
}
</style>
