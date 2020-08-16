<template>
  <div v-if="article">
    <div class="main-wrapper">
      <p class="main-detail-page">MAAT GGUN Page</p>
      <div class="post-title">
        <p class="post-title-text">{{ article.title }}</p>
        <p class="post-nickname">by <router-link class="nickname" :to="{ name: constants.URL_TYPE.USER.MYPAGE, params:{ id: article.userid }}">{{ article.nickname }}</router-link></p>
      </div>
      <div class="box-container">
        <p class="realtime-post">{{ recvList[articleId] }} 명이 보고있습니다</p>
        <Like :article="article" />
        <div class="star-point">
          <p class="star-title">맛</p>
          <star-rating v-model="article.taste" :increment="0.5" :max-rating="5" active-color="#F2E205"
            :rounded-corners="true" :read-only="true" :star-size="20">
          </star-rating>
        </div>
        <div class="star-point" style='display:flex;  justify-content:flex-start;align-items:center;'>
          <p class="star-title">분위기</p>
          <star-rating v-model="article.atmosphere" v-bind:increment="0.5" v-bind:max-rating="5" active-color="#F2E205"
            :rounded-corners="true" :read-only="true" v-bind:star-size="20">
          </star-rating>
        </div>
        <div class="star-point" style='display:flex;  justify-content:flex-start;align-items:center;'>
          <p class="star-title">가격</p>
          <star-rating v-model="article.price" v-bind:increment="0.5" v-bind:max-rating="5" active-color="#F2E205"
            :rounded-corners="true"  :read-only="true" v-bind:star-size="20">
          </star-rating>
        </div>
        <p class="copy-url" @click="CopyUrlToClipboard">URL 복사</p>  
        <p class="post-date">{{ article.createDate }}</p>
        <!-- <div v-if="isLoggedIn" class="post-like-wrap">
          <i ref="postId" v-if="included" @click="change(); checkLike(article);"
            class="fas fa-heart fa-lg animated delay-1s redheart" style="color: red;"></i>
          <i ref="postId" v-if="!included" @click="change(); checkLike(article);"
            class="far fa-heart fa-lg animated infinite bounce delay-1s blankheart" style="color: gray;"></i>
          <p style="margin-left: 5px;">{{ article.likes }} likes</p>
        </div>
        <div v-else class="post-like-wrap">
          <i class="fas fa-heart fa-lg redheart" style="color: red;"></i>
          <p style="margin-left: 5px;">{{ article.likes }} likes</p>
        </div> -->
      </div>
      <hr>
      <div class="main-text-area">
        <p class="content-text">Content</p>
        <div style="margin-top:3px;">
          <p style="text-align:center;">
            <Viewer v-if="article.content != null" :initialValue="article.content" />
          </p>
        </div>
        
        <hr>
        <p class="content-text">Menu</p>
        <div style="margin-top:3px;">
          <p style="text-align:center;">
            <Viewer v-if="article.menu != null" :initialValue="article.menu" />
          </p>
        </div>

        <hr>
        <div class="location-title">
          <p class="content-text">Location</p>
          <p class="store-name">{{ article.placename }}</p>
        </div>
        <div id="map" style="width:100%;height:350px;"></div>
        <br>
      </div>
      <div class="post-button-area">
        <button class="update-post-button" @click.prevent="goupdateArticle()">수정</button>
        <button class="update-post-button delete-button" @click.prevent="deleteArticle()">삭제</button>
      </div>
      <div class="tag-area">
        <div class="tag" v-for="(tag, index) in article.hashtag.split(',')" :key="`hash_${index}`">
          <p class="tag-btn">#{{ tag }}</p>
        </div>
      </div>
      
      <!-- <div class="box" style="font-weight:bold;"> {{ article.hashtag }}</div> -->
      <hr>
      <Comment :article="article" />
    </div>
  </div>
</template>

<script>
import 'codemirror/lib/codemirror.css'
import '@toast-ui/editor/dist/toastui-editor.css'
import { Viewer } from '@toast-ui/vue-editor'
import { mapState, mapGetters, mapActions } from 'vuex'
import constants from '@/lib/constants'
import axios from 'axios'
import SERVER from '@/api/drf'
import Comment from '@/components/common/Comment.vue'
import Like from '@/components/common/Like.vue'
import '@/assets/css/modal.css'
import StarRating from 'vue-star-rating'


export default {
  name: 'Detail',
  components: {
    Viewer,
    Comment,
    Like,
    StarRating,
  },
  data() {
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL,
      content: null,
      menu:null,
      loginUser: '',
      articleId: this.$route.params.id,
      article: null,
      map: '',
      marker: '',
      geocoder: '',
      isModal: false,
      likedposts: '',
      included: true,
    }
  },
  computed: {
    ...mapState(['recvList']),
    ...mapGetters(['isLoggedIn'])
  },
  methods: {
    ...mapActions(['changeMain', 'sendPostId']),
    initMap() {
      var container = document.getElementById('map')
      var options = {
        center: new kakao.maps.LatLng(33.450701, 126.570667),
        level: 3
      }
      this.map = new kakao.maps.Map(container, options)
      this.geocoder = new kakao.maps.services.Geocoder();
      var address_name = this.article.address
      this.geocoder.addressSearch(address_name, (result, status) => {
        // 정상적으로 검색이 완료됐으면 
        if (status === kakao.maps.services.Status.OK) {
          console.log(address_name)

          var coords = new kakao.maps.LatLng(result[0].y, result[0].x);


          // 결과값으로 받은 위치를 마커로 표시합니다
          var marker = new kakao.maps.Marker({
            map: this.map,
            position: coords
          });
          var title = address_name;

          // 인포윈도우로 장소에 대한 설명을 표시합니다
          var infowindow = new kakao.maps.InfoWindow({
            content: '<div style="width:150px;text-align:center;padding:6px 0;">' +
              title + '</div>'
          });
          infowindow.open(this.map, marker);

          // 지도의 중심을 결과값으로 받은 위치로 이동시킵니다
          this.map.setCenter(coords);
        }
      });
    },
    includes() {
      if (this.articleId in this.likedposts) {
        this.included = true;
      } else {
        this.included = false;
      }
    },
    change() {
      var cl = this.$refs["postId"].getAttribute('class');
      console.log(this.$refs["postId"].getAttribute('style'))

      if (cl === "fas fa-heart fa-lg animated delay-1s" || cl === "fas fa-heart fa-lg animated delay-1s redheart") {
        this.$refs["postId"].setAttribute("class", "far fa-heart fa-lg animated infinite bounce delay-1s");
        this.$refs["postId"].setAttribute('style', "color:gray");
        this.article.likes -= 1

      } else {
          this.$refs["postId"].setAttribute("class", "fas fa-heart fa-lg animated delay-1s");
          this.$refs["postId"].setAttribute('style',"color:red");
        this.article.likes += 1
      }


    },
    checkLike(one) {
      one.userid = this.$cookies.get('auth-token')
      // console.log(one.userid)
      axios.post(`${this.SERVER_URL}/articles/like`, one)
        .then(() => {
          // console.log(res)
          axios.post(`${this.SERVER_URL}/accounts/userDetail`, {
              "uid": this.$cookies.get('auth-token')
            })
            .then((res) => {
              console.log(res)
            })
        })
    },
    CopyUrlToClipboard() {
      const tmpInput = document.createElement('textarea');
      document.body.appendChild(tmpInput);
      tmpInput.value = window.document.location.href;
      tmpInput.select();
      document.execCommand('copy');
      document.body.removeChild(tmpInput);
      alert("URL이 클립보드에 복사되었습니다")
    },
    goupdateArticle() {
      if (this.loginUser === this.article.userid) {
        this.$router.push({
          name: constants.URL_TYPE.POST.CREATE,
          params: {
            articleData: this.article
          }
        })
      } else {
        alert('해당 게시글 작성자만 게시글을 수정할 수 있습니다.')
      }
    },
    deleteArticle() {
      if (this.loginUser === this.article.userid) {
        const check = confirm('게시글을 삭제하시겠습니까?')
        if (check) {
          axios.delete(`${this.SERVER_URL}${SERVER.ROUTES.delete}?postId=${this.article.postId}`)
          .then(() => {
            alert('게시글이 삭제되었습니다.')
            this.$router.push({
              name: constants.URL_TYPE.POST.MAIN
            })
          })
        }
      } else {
        alert('해당 게시글 작성자만 게시글을 삭제할 수 있습니다')
      }
    },
    detailPage() {
      axios.get(this.SERVER_URL + `${SERVER.ROUTES.detail}?postId=${+this.articleId}`)
        .then(res => {
          this.article = res.data
          this.article.taste = +this.article.taste
          this.article.price = +this.article.price
          this.article.atmosphere = +this.article.atmosphere
          // this.content = this.article.content
          // this.menu = this.article.menu
          // console.log(this.content)
          // console.log(this.content.split('=/.=/.'))
          // console.log(res.data)
        })
        .catch(err => console.log(err))
    },
    disconnect(){
      this.sendPostId({ articleId: this.articleId, status: 'out' })
    }

  },
  
  created() {
    // this.sendPostId({ articleId: this.articleId, status: 'in' })
    setTimeout(() => {
      this.sendPostId({ articleId: this.articleId, status: 'in' })
    }, 250)
    this.detailPage()
    this.changeMain(false)
    // if (this.$cookies.get('auth-token')) {
    //   axios.post(`${this.SERVER_URL}/accounts/userDetail`, {
    //       "uid": this.$cookies.get('auth-token')
    //     })
    //     .then((res) => {
    //       var liked_list = res.data.likedpost.split(',').map(i => parseInt(i))
    //       var result = liked_list.slice(0, -1)
    //       this.likedposts = result
    //       if (this.likedposts.includes(this.articleId)) {
    //         this.included = true;
    //       } else {
    //         this.included = false;
    //       }

    //     })
    // }
    window.addEventListener('beforeunload', () => {
      this.sendPostId({ articleId: this.articleId, status: 'out' })
    })
  },
  mounted() {
    console.log(this.recvList)
    window.addEventListener("click", e => {
      const modal = document.getElementById("modal");
      e.target === modal ? (this.isModal = false) : false;
    });
  },
  updated() {
    this.loginUser = parseInt(this.$cookies.get('auth-token'))

    if (window.kakao && window.kakao.maps) {
      this.initMap();
    } else {
      const script = document.createElement('script');
      /* global kakao */
      script.onload = () => kakao.maps.load(this.initMap);
      script.src = 'http://dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=caabdd0825a33e45f0ba6cdb6c0570af';
      document.head.appendChild(script);
    }
  }
}
</script>

<style scoped>
.main-wrapper {
  position: relative;
  align-items: center;
  width: 100%;
  margin: 0 auto;
}

.main-detail-page {
  font-size: 16px;
  opacity: 0.5;
}

.box-container {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: flex-end;
}

.content-text {
  margin: 0 0 1rem 1rem;
  font-size: 16px;
  opacity: 0.5;
}

.post-title {
  display: flex;
  align-items: flex-end;
}

.post-title-text {
  font-weight: bold;
  font-size: 50px;
  margin: 2rem 0 0 1rem;
}

.post-nickname {
  margin: 0 0 1rem 1rem;
}

.post-nickname .nickname:hover {
  color: var(--primary-color)
}

.post-date {
  font-size: 13px;
  opacity: 0.5;
}

.realtime-post {
  color: var(--third-color);
}

.copy-url {
  cursor: pointer;
  font-size: 13px;
  font-weight: bold;
  margin-top: 5px;
}

.location-title {
  margin: 2rem 0 1rem 0;
  display: flex;
}


.store-name {
  color: var(--third-color);
  margin-left: 1rem;
}

.post-button-area {
  display: flex;
  justify-content: flex-end;
  margin: 2rem 0;
}

.update-post-button {
  padding: 5px 7px;
  background-color: var(--primary-color);
  border: none;
}

.delete-button {
  margin-left: 5px;
}

hr {
  margin: 2rem 0;
}

.tag-area {
  display: flex;
}

.tag {
  display: flex;
}

.tag-btn {
  width: 65px;
  margin-right: 10px;
  background-color: var(--fifth-color);
  text-align: center;
  border-radius: 3px;
  color: #fff;
}

.star-point {
  display: flex; 
  align-items:center;
}

.star-title {
  padding-top: 7px;
  margin-right: 5px;
}

@media(min-width:560px) {
  .main-wrapper {
    position: relative;
    align-items: center;
    width: 60%;
    margin: 0 auto;
  }
}
</style>