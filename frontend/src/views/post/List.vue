<template>
  <div>
    <PostNavbar class="post-navbar" :listStatus="listStatus" />
    <PostList :likedposts="likedposts" :articles="articles" />
  </div>
</template>

<script>
import '@/assets/css/post.css'
import axios from 'axios'
import { mapState, mapActions } from 'vuex'
import constants from '@/lib/constants'
import PostNavbar from '@/components/common/PostNavbar.vue'
import PostList from '@/components/common/PostList.vue'


export default {
  name: 'Post',
  components: {
    PostNavbar,
    PostList
  },
  data(){
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL,
      likedposts: [],
      listStatus: {
        isRecentList: false,
        isLikeList: true,
        isHitList: true,
        isStarList: true
      }
    }
  },
  computed:{
    ...mapState(['articles'])
  },
  watch: {},
  methods: {
    ...mapActions(['getArticles']),
    checkLikeList(res) {
      if (res.data.likedpost) {
        const liked_list = res.data.likedpost.split(',').map(i=>parseInt(i))
        const result = liked_list.slice(0,-1)
        this.likedposts = result
      }
    }
  },
  created() {
  },
  mounted(){
    if(this.$cookies.get('auth-token')){
      axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
        .then((res)=>{
          this.checkLikeList(res)
        }
      )
    }
  },
  updated() {
  },
}
</script>

<style scoped>

</style>