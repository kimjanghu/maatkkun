<template>
  <div v-if="article">
    <div class="main-wrapper">
      <div class="box-container">
        <div class="box" style="font-weight:bold;"> {{article.createDate}}</div>
        <div class="box" style="font-weight:bold;"> {{article.hashtag}}</div>
      </div>
      <br>
      <div class="post-title">
        <p style="font-weight: bold; font-size: 20px">제목</p>
        <p>: {{ article.title }}</p>
      </div>
      <br>

      <div>
        <div style="margin-top:3px;">
          <p style="text-align:center;">
            <Viewer v-if="content != null" :initialValue="content" />
          </p>
        </div>
        <br>
        <div v-if="isLoggedIn" class="comment-like-wrap">
          <i ref="postId" v-if="included" @click="change(); checkLike(article);"
            class="fas fa-heart fa-lg animated delay-1s redheart" style="color: red;"></i>
          <i ref="postId" v-if="!included" @click="change(); checkLike(article);"
            class="far fa-heart fa-lg animated infinite bounce delay-1s blankheart" style="color: gray;"></i>
          <p style="margin-left: 5px;">{{ article.likes }} likes</p>
        </div>
        <div v-else class="comment-like-wrap">
          <i class="fas fa-heart fa-lg redheart" style="color: red;"></i>
          <p style="margin-left: 5px;">{{ article.likes }} likes</p>
        </div>
        <br>
        <div id="map" style="width:100%;height:350px;"></div>
        <br>
        <p>음식점 이름 : {{article.placename}}</p>
        <button class="create_button" style="margin-right:2px;" @click.prevent="changeModal">공유하기</button>
        <div v-show="isModal" class="modal-container" id="modal">
          <div class="modal">
            <p>공유</p>
            <input type="text" id="ShareUrl">
            <span class="btn-type1"><button class="create_button" @click="CopyUrlToClipboard">URL 복사</button></span>

          </div>

        </div>
      </div>
      <br>
      <br>
      <Comment :article="article" />
      <div class="twobuttons">
        <button class="create_button" style="margin-right:2px;" @click.prevent="goupdateArticle()">게시글 수정</button>
        <button class="create_button" @click.prevent="deleteArticle()">게시글 삭제</button>
      </div>
    </div>
  </div>


</template>

<script>
  import 'codemirror/lib/codemirror.css'
  import '@toast-ui/editor/dist/toastui-editor.css'
  import {
    Viewer
  } from '@toast-ui/vue-editor'
  import constants from '@/lib/constants'
  import axios from 'axios'
  import SERVER from '@/api/drf'
  import Comment from '@/components/common/Comment.vue'
  import '@/assets/css/modal.css'
  import {
    mapGetters
  } from 'vuex';


  export default {
    name: 'Detail',
    components: {
      Viewer,
      Comment
    },
    data() {
      return {
        SERVER_URL: process.env.VUE_APP_API_URL,
        content: null,
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
      ...mapGetters(['isLoggedIn'])

    },
    methods: {
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

      CopyUrlToClipboard()

      {

        var obShareUrl = document.getElementById("ShareUrl");

        obShareUrl.value = window.document.location.href;



        obShareUrl.select();

        document.execCommand("copy");



        obShareUrl.blur();



        alert("URL이 클립보드에 복사되었습니다");

      }

      ,

      changeModal() {
        this.isModal = !this.isModal
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
          axios.delete(`${this.SERVER_URL}${SERVER.ROUTES.delete}?postId=${this.article.postId}`)
            .then(() => {
              this.$router.push({
                name: constants.URL_TYPE.POST.MAIN
              })
            })
        } else {
          alert('해당 게시글 작성자만 게시글을 삭제할 수 있습니다')
        }
      },
      detailPage() {
        axios.get(this.SERVER_URL + `${SERVER.ROUTES.detail}?postId=${+this.articleId}`)
          .then(res => {
            this.article = res.data
          })
          .catch(err => console.log(err))
      },
    },
    updated() {
      this.content = this.article.content
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
    },
    created() {
      this.detailPage()
      if (this.$cookies.get('auth-token')) {
        axios.post(`${this.SERVER_URL}/accounts/userDetail`, {
            "uid": this.$cookies.get('auth-token')
          })
          .then((res) => {
            var liked_list = res.data.likedpost.split(',').map(i => parseInt(i))
            var result = liked_list.slice(0, -1)
            this.likedposts = result
            if (this.likedposts.includes(this.articleId)) {
              this.included = true;
            } else {
              this.included = false;
            }

          })
      }
    },
    mounted() {
      window.addEventListener("click", e => {
        const modal = document.getElementById("modal");
        e.target === modal ? (this.isModal = false) : false;
      });


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

  @media(min-width:560px) {
    .main-wrapper {
      position: relative;
      align-items: center;
      width: 60%;
      margin: 0 auto;
    }
  }

  .box-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
  }

  .post-title {
    display: flex;
    align-items: center;
  }
</style>