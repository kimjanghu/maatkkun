<template>
  <div>
    <div v-if="isLoggedIn" class="post-like-wrap">
      <i 
        :id="`recent_${article.postId}`" 
        ref="postId" 
        v-if="includes(article)" 
        @click="change(article); checkLike(article);"
        class="fas fa-heart fa-lg animated delay-1s redheart" 
        style="color: red;"
      ></i>
      <i 
        :id="`recent_${article.postId}`" 
        ref="postId" 
        v-if="!includes(article)" 
        @click="change(article); checkLike(article);"
        class="far fa-heart fa-lg animated infinite bounce delay-1s blankheart" 
        style="color: gray;"
      ></i>
      <p style="margin-left: 5px;">{{ article.likes }}</p>
    </div>
    <div v-else class="post-like-wrap">
      <i class="fas fa-heart fa-lg redheart" style="color: red;"></i>
      <p style="margin-left: 5px;">{{ article.likes }}</p>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import axios from 'axios'

export default {
  name: 'Like',
  props: {
    article: {
      type: Object
    },
    likedposts: {
      type: Array
    }
  },
  data() {
    return {
      SERVER_URL: process.env.VUE_APP_API_URL,
    }
  },
  computed: {
    ...mapGetters('userStore', ['isLoggedIn'])
  },
  methods: {
    includes(post){
      if(this.likedposts.includes(post.postId)){
        return true
      }
      else{
        return false
      }
    },
    checkLike(post){
      post.userid = this.$cookies.get('auth-token')
      axios.post(`${this.SERVER_URL}/articles/like`, post)
      .then(()=>{
        axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
      })
    },
    change(post){
      const recentColor = document.getElementById(`recent_${post.postId}`)
      if (recentColor['className'] === 'fas fa-heart fa-lg animated delay-1s' || recentColor['className'] === 'fas fa-heart fa-lg animated delay-1s redheart') {
        recentColor['className'] = 'far fa-heart fa-lg animated infinite bounce delay-1s'
        recentColor['style']['color'] = 'gray'
        post.likes -= 1
      } else {
        recentColor['className'] = 'fas fa-heart fa-lg animated delay-1s'
        recentColor['style']['color'] = 'red'
        post.likes += 1
      }
    },
  },
  created() {
  }
}
</script>

<style scoped>
.post-like-wrap {
  display: flex;
  align-items: center;
}
</style>