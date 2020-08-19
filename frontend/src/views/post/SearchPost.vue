<template>
  <div>
    <p class="search-keyword">검색어: {{ searchKeyword }}</p>
    <PostList :likedposts="likedposts" :articles="searchArticles" />
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import axios from 'axios'
import PostList from '@/components/common/PostList.vue'


export default {
  name: 'SearchPost',
  components: {
    PostList
  },
  data() {
    return {
      searchKeyword: '',
      SERVER_URL: process.env.VUE_APP_API_URL,
      likedposts: []
    }
  },
  computed: {
    ...mapState(['searchArticles'])
  },
  methods: {
    ...mapActions(['searchPost']),
    checkLikeList(res) {
      if (res.data.likedpost) {
        const liked_list = res.data.likedpost.split(',').map(i=>parseInt(i))
        const result = liked_list.slice(0,-1)
        this.likedposts = result
      }
    }
  },
  created() {
    this.searchKeyword = this.$route.query.search
    this.searchPost(this.searchKeyword)
  },
  mounted() {
    if(this.$cookies.get('auth-token')){
      axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
        .then((res)=>{
          this.checkLikeList(res)
        }
      )
    }
  }
}
</script>

<style scoped>
.search-keyword {
  width: 80%;
  margin: 1rem auto 0 auto;
  font-size: 30px;
}
</style>