<template>
  <div>
    <PostNavbar class="post-navbar" :listStatus="listStatus" />
    <PostList :likedposts="likedposts" :articles="likeArticles" />
  </div>
</template>

<script>
import '@/assets/css/post.css'
import axios from 'axios'
import { mapState, mapActions } from 'vuex'
import constants from '@/lib/constants.js'
import PostNavbar from '@/components/common/PostNavbar.vue'
import PostList from '@/components/common/PostList.vue'


export default {
  name: 'Like',
  components: {
    PostNavbar,
    PostList
  },
  data() {
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL,
      likedposts: [],
      listStatus: {
        isRecentList: true,
        isLikeList: false,
        isHitList: true,
        isStarList: true
      }
    }
  },
  computed: {
    ...mapState(['likeArticles'])
  },
  methods: {
    ...mapActions(['getLikeArticles']),
    checkLikeList(res) {
      if (res.data.likedpost) {
        const liked_list = res.data.likedpost.split(',').map(i=>parseInt(i))
        const result = liked_list.slice(0,-1)
        this.likedposts = result
      }
    }
  },
  created() {
    this.getLikeArticles()
  },
  mounted() {
    if(this.$cookies.get('auth-token')){
      axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
        .then((res)=>{
          this.checkLikeList(res)
        }
      )
    }
  },
  updated() {
  }
}
</script>

<style>

</style>