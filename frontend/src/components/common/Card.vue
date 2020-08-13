<template>
  <div class="post">
    <div class="wrapB">
      <section class="post-list">
        <div v-for="(article, index) in searchArticles.list" :key="article.id">
          <div class="post-card">
            <a @click="detailPage(article.postId)" >
              <img
                :src="article.content"
                :style="{backgroundImage:'url(https://www.ipcc.ch/site/assets/uploads/sites/3/2019/10/img-placeholder.png)'}"
                class="post-img"
              />

              <div class="contents">
                <h3>{{ article.title }}</h3>
                <br>
                <span class="date"><i class="far fa-user"></i> {{ article.nickname }}</span>
                <br>
                <span class="date"><i class="far fa-clock"></i> {{ article.createDate }}</span>
                <br>
                <span class="date"><i class="far fa-eye"></i> {{ article.hits }}</span>
                <br>
                <div class="tag" v-for="(tag, index) in article.hashtag.split(',')" :key="`hash_${index}`">
                  <span class="tag-btn"># {{ tag }}</span>
                </div>
                <!-- <span>{{article.hashtag}} </span> -->
              </div>
            </a>

            <div class="writer-wrap">
              <div class="comment-like-wrap">
                <i class="far fa-comment-alt fa-lg"></i><p style="margin: 0 5px;">{{ searchArticles.comment[index] }}</p>
              </div>
              <div v-if="isLoggedIn" class="comment-like-wrap">
                <i 
                  :id="`like_${article.postId}`" 
                  :ref="article.postId" 
                  @click.prevent="checkLike(article)" 
                  v-if="includes(article)" 
                  class="fas fa-heart fa-lg animated delay-1s redheart" 
                  style="color: red;"></i>
                <i 
                  :id="`like_${article.postId}`" 
                  :ref="article.postId" 
                  @click.prevent="checkLike(article)" 
                  v-if="!includes(article)" 
                  class="far fa-heart fa-lg animated infinite bounce delay-1s blankheart" 
                  style="color: gray;"></i>
                <p style="margin-left: 5px;">{{ article.likes }}</p>
              </div>
              <div v-else class="comment-like-wrap">
                <i 
                  class="fas fa-heart fa-lg redheart" 
                  style="color: red;"
                ></i>
                <p style="margin-left: 5px;">{{ article.likes }}</p>
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
import axios from 'axios'
import constants from '@/lib/constants'
import { mapState, mapGetters, mapActions } from 'vuex';

export default {
  name: 'Card',
  props: {
    searchKeyword: {
      type: String
    }
  },
  data() {
    return {
      constants,
      likedposts: [],
      posts: [],
      SERVER_URL: process.env.VUE_APP_API_URL
    }
  },
  computed: {
    ...mapState([
      'searchArticles', 
      'articles', 
      'likeArticles', 
      'hitArticles'
    ]),
    ...mapGetters(['isLoggedIn'])
  },
  methods: {
    ...mapActions([
      'searchPost', 
      'getArticles', 
      'getLikeArticles', 
      'getHitArticles',
      'datailPage'
    ]),
    includes(post){
      if(this.likedposts.includes(post.postId)){
        return true
      }
      else{
        return false
      }
    },
    // change(post){
    //   const recentColor = document.getElementById(`like_${post.postId}`)
    //   if (recentColor['className'] === 'fas fa-heart fa-lg animated delay-1s' || recentColor['className'] === 'fas fa-heart fa-lg animated delay-1s redheart') {
    //     recentColor['className'] = 'far fa-heart fa-lg animated infinite bounce delay-1s'
    //     recentColor['style']['color'] = 'gray'
    //     post.likes -= 1
    //   } else {
    //     recentColor['className'] = 'fas fa-heart fa-lg animated delay-1s'
    //     recentColor['style']['color'] = 'red'
    //     post.likes += 1
    //   }
    // },
    checkLike(one){
      one.userid=this.$cookies.get('auth-token')
      axios.post(`${this.SERVER_URL}/articles/like`,one)
      .then(()=>{
        axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
        .then(res=>{
          this.searchPost(this.searchKeyword)
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
    },
    // checkListType() {
    //   switch (this.listType) {
    //     case 'listArticles':
    //       this.getArticles()
    //         .then(() => {
    //           this.posts = this.articles
    //         })
    //       break;
    //     case 'likeArticles':
    //       this.getLikeArticles()
    //         .then(() => {
    //           this.posts = this.likeArticles
    //         })
    //       break;
    //     case 'hitArticles':
    //       this.getHitArticles()
    //         .then(() => {
    //           this.posts = this.hitArticles
    //         })
    //       break;
    //     default:
    //       this.searchPost(this.listType)
    //         .then(() => {
    //           console.log(this.listType)
    //           this.posts = this.searchArticles
    //           console.log(this.searchArticles)
    //         })
    //   }
    // },
  },
  created() {
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
  },
}
</script>

<style>

</style>