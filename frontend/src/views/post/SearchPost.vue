<template>
  <div>
    <Card :searchKeyword="searchKeyword" />
  </div>
</template>

<script>
// import { mapActions } from 'vuex'
import Card from '@/components/common/Card.vue'

export default {
  name: 'SearchPost',
  components: {
    Card
  },
  data() {
    return {
      searchKeyword: ''
    }
  },
  computed: {
    // ...mapState(['searchArticles'])
  },
  methods: {
    // ...mapActions(['searchPost'])
  },
  created() {
    this.searchKeyword = this.$route.query.search
  },
  mounted() {
    // this.searchPost(this.$route.query.search)
  }
}
</script>

<style>

</style>