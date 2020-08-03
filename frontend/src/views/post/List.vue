<template> 

  <div class="post">
    <div class="wrapB">

      <input type="text" v-model="searchKeyword" id="myInput" v-on:keyup.enter="searchResult(searchKeyword)" placeholder="#태그 #제목 #내용" title="Type in a name">
      <br>
      <div class="post-list-link">
        <router-link
          class="main-link" 
          :class="{ active: isRecentList }" 
          :to="{ name: constants.URL_TYPE.POST.MAIN }" 
          @click.prevent="changeMainRecentList"
        ><i class="far fa-clock fa-lg" style="margin-right: 5px;"></i>최신순</router-link>
        <router-link
          class="main-link" 
          :class="{ active: isLikeList }" 
          :to="{ name: constants.URL_TYPE.POST.MAIN }" 
          @click.prevent="changeMainLikeList"
        ><i class="far fa-heart fa-lg" style="margin-right: 5px;"></i>좋아요</router-link>
      </div>
      
      <br><br>
      <!-- <div class="search-input">
        <input v-model="searchKeyword" type="text" />
        <button @click="searchResult(searchKeyword)">검색</button>
      </div> -->

      <section class="post-list">

        <div v-for="(article, index) in articles.list" :key="article.id" :article="article">
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
                <div class="tag" v-for="(tag, index) in article.hashtag.split(',')" :key="`hash_${index}`">
                  <span class="tag-btn"># {{ tag }}</span>
                </div>
                <!-- <span>{{article.hashtag}} </span> -->
              </div>
            </a>

            <div class="writer-wrap">
              <div class="comment-like-wrap">
                <i class="far fa-comment-alt fa-lg"></i><p style="margin: 0 5px;">{{ articles.comment[index] }}</p>
              </div>
              <div v-if="isLoggedIn" class="comment-like-wrap">
                <i :ref="article.postId" @click="change(article); checkLike(article);" v-if="includes(article)" class="fas fa-heart fa-lg animated delay-1s redheart" style="color: red;"></i>
                <i :ref="article.postId" @click="change(article); checkLike(article);" v-if="!includes(article)" class="far fa-heart fa-lg animated infinite bounce delay-1s blankheart" style="color: gray;"></i><p style="margin-left: 5px;">{{ article.likes }}</p>
              </div>
              <div v-else class="comment-like-wrap">
                <i class="fas fa-heart fa-lg redheart" style="color: red;"></i><p style="margin-left: 5px;">{{ article.likes }}</p>
              </div>
              
              <!-- <div>
                <i v-bind:ref="article.postId" @click="change(article); checkLike(article);"  class="far fa-heart fa-lg animated infinite bounce delay-1s" style="color: gray;"></i>
                <span class="article_likes">{{article.likes}}</span>
              </div> -->

            </div>
          </div>
        </div>
        
        
      </section>
    </div>
    </div>

</template>

<script>
import '@/assets/css/post.css';
import axios from 'axios'
import { mapActions, mapState, mapGetters } from 'vuex';
// import SERVER from '@/api/drf'
import constants from '@/lib/constants'
// import { mapGetters } from 'vuex'
export default {
  name: 'Post',
  components: {},
  computed:{
    ...mapState(['articles']),
    ...mapGetters(['isLoggedIn'])
  },
  updated(){

  },
  watch: {},
  created() {
    this.getArticles()
  },
  mounted(){
  
    if(this.$cookies.get('auth-token')){
      axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
        .then((res)=>{
          var liked_list = res.data.likedpost.split(',').map(i=>parseInt(i))
          var result = liked_list.slice(0,-1)
          this.likedposts = result
          }
        )
    }
  },
  
  
  methods: {
    ...mapActions(['getArticles', 'searchResult']),
    includes(one){
      
      if(this.likedposts.includes(one.postId )){
        // console.log(one.postId)
        return true
      }
      else{
        return false
      }
      
    },
    // changePostid(postid){
    //       this.$refs[postid][0]["className"] = "fas fa-heart fa-lg animated delay-1s";
    //       this.$refs[postid][0]["style"]["color"] = "red";
    //       console.log(postid)
    // },

    change(post){
      
      var cl = this.$refs[post.postId][0]["className"];
      console.log(cl)
      
      if(cl === "fas fa-heart fa-lg animated delay-1s" || cl ==="fas fa-heart fa-lg animated delay-1s redheart"){
          this.$refs[post.postId][0]["className"] = "far fa-heart fa-lg animated infinite bounce delay-1s";
          this.$refs[post.postId][0]["style"]["color"] = "gray";
          post.likes-=1
          
      }
      else{
          this.$refs[post.postId][0]["className"] = "fas fa-heart fa-lg animated delay-1s";
          this.$refs[post.postId][0]["style"]["color"] = "red";
          post.likes+=1
      }


    },
    checkLike(one){
      one.userid=this.$cookies.get('auth-token')
      // console.log(one.userid)
      axios.post(`${this.SERVER_URL}/articles/like`,one)
      .then(()=>{
        // console.log(res)
        axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
        .then((res)=>{
          console.log(res)
        })
      })
    },
    detailPage(articleId){
      this.$router.push({ name: constants.URL_TYPE.POST.DETAIL, params: { id: articleId }})
    },
    changeMainRecentList() {
      this.isRecentList = false,
      this.isLikeList = true
    },
    changeMainLikeList() {
      this.isRecentList = true,
      this.isLikeList = false
    }
  },
  data(){
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL,
      articleData: {},
      likedposts:[],
      searchKeyword:"",
      isRecentList: false,
      isLikeList: true
    };
  }
};
</script>

<style scoped>

</style>