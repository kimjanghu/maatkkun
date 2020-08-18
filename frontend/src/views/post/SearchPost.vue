<template>
  <div>
    <Card :searchKeyword="searchKeyword" />
  </div>
</template>

<script>
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
  },
  methods: {
  },
  created() {
    this.searchKeyword = this.$route.query.search
  },
  mounted() {
  }
}
</script>

<style>

</style>