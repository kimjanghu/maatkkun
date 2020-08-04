<template>
  <div>Logout Page</div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Logout',
  methods: {
    ...mapActions(['logout'])
  },
  created() {
    this.logout()
    alert('로그아웃 되었습니다.')
  }
}
</script>

<style>

</style>