<template> 

  <div class="post">
    <div class="wrapB">

      <input type="text" v-model="searchKeyword" id="myInput" v-on:keyup.enter="searchResult(searchKeyword)" placeholder="#태그 #제목 #내용" title="Type in a name">
      <br>
      <h3>Recent</h3>
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
                <span class="date">작성자 : {{article.nickname}}</span><br>
                <span class="date">작성일 : {{article.createDate}}</span><br>
                
                <span class="comment"> 댓글 {{articles.comment[index]}}</span>
                <span>{{article.hashtag}} </span>
              </div>
            </a>

            <div class="writer-wrap">
              <div v-if="isLoggedIn">
              <i  v-bind:ref="article.postId" @click="change(article); checkLike(article);" v-if="includes(article)" class="fas fa-heart fa-lg animated delay-1s redheart" style="color: red;"></i>
              <i v-bind:ref="article.postId" @click="change(article); checkLike(article);" v-if="!includes(article)" class="far fa-heart fa-lg animated infinite bounce delay-1s blankheart" style="color: gray;"></i>
              </div>
              <span class="article_likes">♥ {{article.likes}}</span>
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
    }
  },
  data(){
    return {
      SERVER_URL: process.env.VUE_APP_API_URL,
      articleData: {},
      likedposts:[],
      searchKeyword:"",

    };
  }
};
</script>

<style scoped>
/* @media(max-width: 947px) {
  .post-list {
    grid-template-columns: repeat(2, 2fr);
  }

  .post-card {
    width: 500px;
  }
} */
</style>