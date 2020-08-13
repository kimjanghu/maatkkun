<template>
  <div class="post">
    <div class="wrapB">
      <PostNavbar :listStatus="listStatus" />
      <section class="post-list">
        <div v-for="(article, index) in hitArticles.list" :key="article.id">
          <div class="post-card">
            <a @click="detailPage(article.postId)" >
              <img
                :src="article.content"
                :style="{backgroundImage:'url(https://www.ipcc.ch/site/assets/uploads/sites/3/2019/10/img-placeholder.png)'}"
                class="post-img"
              />

              <div class="contents">
                <h3>{{article.title}}</h3>
                <br>
                <span class="date"><i class="far fa-user"></i> {{article.nickname}}</span>
                <br>
                <span class="date"><i class="far fa-clock"></i> {{article.createDate}}</span>
                <br>
                <span class="date"><i class="far fa-eye"></i> {{ article.hits }}</span>
                <br>
                <div class="tag" v-for="(tag, index) in article.hashtag.split(',')" :key="`hash_${index}`">
                  <span class="tag-btn"># {{ tag }}</span>
                </div>
              </div>
            </a>

            <div class="writer-wrap">
              <div class="comment-like-wrap">
                <i class="far fa-comment-alt fa-lg"></i><p style="margin: 0 5px;">{{ hitArticles.comment[index] }}</p>
              </div>
              <div v-if="isLoggedIn" class="comment-like-wrap">
                <i 
                  :id="`views_${article.postId}`"
                  :ref="article.postId" 
                  @click="checkLike(article)" 
                  v-if="includes(article)" 
                  class="fas fa-heart fa-lg animated delay-1s redheart" 
                  style="color: red;"
                ></i>
                <i 
                  :id="`views_${article.postId}`"
                  :ref="article.postId" 
                  @click="checkLike(article)" 
                  v-if="!includes(article)" 
                  class="far fa-heart fa-lg animated infinite bounce delay-1s blankheart" 
                  style="color: gray;"
                ></i><p style="margin-left: 5px;">{{ article.likes }}</p>
              </div>
              <div v-else class="comment-like-wrap">
                <i class="fas fa-heart fa-lg redheart" style="color: red;"></i><p style="margin-left: 5px;">{{ article.likes }}</p>
              </div>
            </div>
          </div>
        </div>
      
      </section>
    </div>
  </div>
</template>

<script>
import '@/assets/css/post.css'
import { mapState, mapActions, mapGetters} from 'vuex'
import axios from 'axios'
import constants from '@/lib/constants.js'
import PostNavbar from '@/components/common/PostNavbar.vue'

export default {
  name: 'Views',
  components: {
    PostNavbar
  },
  data() {
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL,
      likedposts: [],
      listStatus: {
        isRecentList: true,
        isLikeList: true,
        isHitList: false
      }
    }
  },
  computed: {
    ...mapState(['hitArticles']),
    ...mapGetters(['isLoggedIn'])
  },
  methods: {
    ...mapActions(['getHitArticles']),
    includes(post){
      if(this.likedposts.includes(post.postId )){
        return true
      }
      else{
        return false
      }  
    },
    checkLike(post){
      post.userid=this.$cookies.get('auth-token')
      // console.log(post.userid)
      axios.post(`${this.SERVER_URL}/articles/like`, post)
      .then(()=>{
        // console.log(res)
        axios.post(`${this.SERVER_URL}/accounts/userDetail`,{ 'uid': this.$cookies.get('auth-token') })
        .then(res=>{
          this.getHitArticles()
          this.checkLikeList(res)
        })
      })
    },
    detailPage(articleId){
      this.$router.push({ name: constants.URL_TYPE.POST.DETAIL, params: { id: articleId }})
    },
    checkLikeList(res) {
      const liked_list = res.data.likedpost.split(',').map(i=>parseInt(i))
      const result = liked_list.slice(0,-1)
      this.likedposts = result
    }
  },
  created() {
    this.getHitArticles()
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