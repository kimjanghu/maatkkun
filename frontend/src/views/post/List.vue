<template> 
  <div class="post">
    <div class="wrapB">
    
      <div class="post-list">
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
                <i 
                  :id="`recent_${article.postId}`" 
                  :ref="article.postId" 
                  @click.prevent="checkLike(article)" 
                  v-if="includes(article)" 
                  class="fas fa-heart fa-lg animated delay-1s redheart" 
                  style="color: red;"></i>
                <i 
                  :id="`recent_${article.postId}`" 
                  :ref="article.postId" 
                  @click.prevent="checkLike(article)" 
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
      </div>
    </div>
  </div>
</template>

<script>
import '@/assets/css/post.css'
import axios from 'axios'
import { mapActions, mapState, mapGetters } from 'vuex';
// import SERVER from '@/api/drf'
import constants from '@/lib/constants'
// import Stomp from 'webstomp-client'
// import SockJS from 'sockjs-client'
// import { mapGetters } from 'vuex'
export default {
  name: 'Post',
  components: {},
  computed:{
    // ...mapState(['articles', 'likeArticles', 'hitArticles']),
    ...mapState(['articles']),
    ...mapGetters(['isLoggedIn'])
  },
  watch: {},
  methods: {
    // ...mapActions(['getArticles', 'getLikeArticles', 'getHitArticles', 'searchResult']),
    ...mapActions(['getArticles', 'changeMain', 'sendPostId']),
    includes(one){
      // console.log(one)
      if(this.likedposts.includes(one.postId)){
        // console.log(one.postId)
        return true
      }
      else{
        return false
      }
      
    },
    change(post){
      // console.log(this.$refs[post])
      // var cl = this.$refs[post.postId][0]["className"];
      // // console.log(cl)
      
      // if(cl === "fas fa-heart fa-lg animated delay-1s" || cl ==="fas fa-heart fa-lg animated delay-1s redheart"){
      //     this.$refs[post.postId][0]["className"] = "far fa-heart fa-lg animated infinite bounce delay-1s";
      //     this.$refs[post.postId][0]["style"]["color"] = "gray";
      //     post.likes-=1
          
      // }
      // else{
      //     this.$refs[post.postId][0]["className"] = "fas fa-heart fa-lg animated delay-1s";
      //     this.$refs[post.postId][0]["style"]["color"] = "red";
      //     post.likes+=1
      // }
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
    checkLike(one){
      // console.log(one)
      one.userid=this.$cookies.get('auth-token')
      // console.log(one.userid)
      axios.post(`${this.SERVER_URL}/articles/like`,one)
      .then(()=>{
        // console.log(res)
        axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
        .then(res=>{
          this.getArticles()
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
    // connect() {
    //   const serverURL = "http://localhost:8080"
    //   let socket = new SockJS(serverURL);
    //   this.stompClient = Stomp.over(socket);
    //   console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`)
    //   this.stompClient.connect(
    //     {},
    //     frame => {
    //       // 소켓 연결 성공
    //       this.connected = true;
    //       console.log('소켓 연결 성공', frame);
    //       // 서버의 메시지 전송 endpoint를 구독합니다.
    //       // 이런형태를 pub sub 구조라고 합니다.
    //       this.stompClient.subscribe("/send", res => {
    //         console.log(res);
    //         console.log('구독으로 받은 메시지 입니다.', res.body);

    //         // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
    //         // this.recvList.push(JSON.parse(res.body)) //넣어주기
    //       });
    //     },
    //     error => {
    //       // 소켓 연결 실패
    //       console.log('소켓 연결 실패', error);
    //       this.connected = false;
    //     }
    //   );        
    // }
  },
  data(){
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL,
      likedposts:[],
      // searchKeyword:"",
      // isRecentList: false,
      // isLikeList: true,
      // isHitList: true
    };
  },
  created() {
    this.getArticles()
    this.changeMain(true)
  },
  mounted(){
    if(this.$cookies.get('auth-token')){
      axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
        .then((res)=>{
          // var liked_list = res.data.likedpost.split(',').map(i=>parseInt(i))
          // var result = liked_list.slice(0,-1)
          // this.likedposts = result
          this.checkLikeList(res)
        }
      )
    }
  },
  updated() {
    // this.getArticles()
    
    // console.log(1)
  },
};
</script>

<style scoped>

</style>