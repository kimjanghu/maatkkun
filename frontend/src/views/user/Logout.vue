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
  }
}
</script>

<style>

</style>