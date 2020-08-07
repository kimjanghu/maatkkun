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
  import { Viewer } from '@toast-ui/vue-editor'
  import { mapActions } from 'vuex'
  import constants from '@/lib/constants'
  import axios from 'axios'
  import SERVER from '@/api/drf'
  import Comment from '@/components/common/Comment.vue'
  import '@/assets/css/modal.css'


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
      }
    },
    methods: {
      ...mapActions(['changeMain']),
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
      this.changeMain(false)
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