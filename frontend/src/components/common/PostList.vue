<template> 
  <div class="post">
    <div class="wrapB">
      <section class="post-list">
        <div v-for="(article, index) in articles.list" :key="article.id">
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
                <span class="date"><i class="fas fa-star" style="color: #F2E205;"></i> {{ article.starpoint }}</span>
                <br>
                <div class="tag" v-for="(tag, index) in article.hashtag.split(',')" :key="`hash_${index}`">
                  <span class="tag-btn"># {{ tag }}</span>
                </div>
              </div>
            </a>

            <div class="writer-wrap">
              <div class="comment-like-wrap">
                <i class="far fa-comment-alt fa-lg"></i><p style="margin: 0 5px;">{{ articles.comment[index] }}</p>
              </div>
              <div v-if="isLoggedIn" class="comment-like-wrap">
                <i 
                  :id="`recent_${article.postId}`" 
                  :ref="article.postId" 
                  @click.prevent="change(article); checkLike(article);" 
                  v-if="includes(article)" 
                  class="fas fa-heart fa-lg animated delay-1s redheart" 
                  style="color: red;"></i>
                <i 
                  :id="`recent_${article.postId}`" 
                  :ref="article.postId" 
                  @click.prevent="change(article); checkLike(article);" 
                  v-if="!includes(article)" 
                  class="far fa-heart fa-lg animated infinite bounce delay-1s blankheart" 
                  style="color: gray;"></i>
                <p style="margin-left: 5px;">{{ article.likes }}</p>
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
import { mapGetters } from 'vuex'
import constants from '@/lib/constants'

export default {
  name: 'PostList',
  props: {
    articles: [Array, Object],
    likedposts: {
      type: Array
    }
  },
  data(){
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL
    };
  },
  computed:{
    ...mapGetters('userStore', ['isLoggedIn'])
  },
  watch: {},
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
      this.$emit('check-like', post)
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
    detailPage(articleId){
      this.$router.push({ name: constants.URL_TYPE.POST.DETAIL, params: { id: articleId }})
    },
  },
  created() {
  },
  mounted(){
    
  },
  updated() {
  },
};
</script>

<style scoped>

</style>