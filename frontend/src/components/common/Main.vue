<template>
  <div v-if="isMain">
    
    <div class="best-post-area" @mouseover="changeDisplay()" @mouseout="changeDisplay()">
      <div>
        <i 
          class="fas fa-chevron-circle-up fa-2x arrow" 
          :class="{ active: isArrow }" 
        ></i>
      </div>
      <div 
        class="best-post" 
        id="best-post"
        :class="{ active: isBestPost }">
        <h3 class="best-post-main">MAAT KKUN Best</h3>
        <hr>
        <div class="tmp" v-show="!isLoading" v-for="(recv, index) in displayRecvList" :key="`recv_${recv[0].postId}`">
          <p class="best-post-index">{{ index+1 }}</p><router-link class="best-post-title" :to="{ name: constants.URL_TYPE.POST.DETAIL, params:{ id: recv[0].postId } }">{{ recv[0].title }}</router-link>
        </div>
        <div v-if="isLoading" class="main-loading">
          <Loading />
        </div>
      </div>
    </div>


    <div style="position:relative;">

  <img
    :src="imageCandidate"
    alt="Food"
    style="width:100%; height:400px;"
  />

    <div style="position:absolute;left:15%;bottom: 40%;width: 70%;">
      
      <input type="text" v-model="searchKeyword" id="myInput" @keyup.enter="moveSearchPage(searchKeyword)" placeholder="#태그 #제목 #내용" title="Type in a name">
      <br>
      <!-- <div class="post-list-link">
        <div class="main-link" :class="{ active: isRecentList }" @click.prevent="changeMainRecentList">
          <router-link :to="{ name: constants.URL_TYPE.POST.MAIN }"><i class="far fa-clock fa-lg" style="margin-right: 5px;"></i>최신순</router-link>
        </div>
        <div class="main-link" :class="{ active: isLikeList }" @click.prevent="changeMainLikeList">
          <router-link :to="{ name: constants.URL_TYPE.POST.LIKE }"><i class="far fa-heart fa-lg" style="margin-right: 5px;"></i>좋아요</router-link>
        </div>
        <div class="main-link" :class="{ active: isHitList }" @click.prevent="changeMainHitList">
          <router-link :to="{ name: constants.URL_TYPE.POST.VIEWS }"><i class="fas fa-fire-alt fa-lg" style="margin-right: 5px;"></i>조회순</router-link>
        </div>
      </div> -->
      <button class="create_button" @click="goRecommend">
        추천 받아보실래요?
      </button>
      <br><br>

      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import constants from '@/lib/constants.js'
import Loading from '@/components/common/Loading.vue'
import '@/assets/css/style.css'


export default {
  name: 'Main',
  components: {
    Loading
  },
  data() {
    return {
      constants,
      searchKeyword: '',
      isRecentList: false,
      isLikeList: true,
      isHitList: true,
      isLoading: true,
      isArrow: false,
      isBestPost: false,
      articles: null,
      displayRecvList: [],
      imageCandidate:'',
    }
  },
  computed: {
    ...mapState(['isMain', 'recvList'])
  },
  methods: {
    ...mapActions(['changeMain', 'sendPostId']),
    selectPicture(){
      var pizza = '../../assets/img/pizza.jpg'
      var eclair = '../../assets/img/eclair.jpg'
      var steak = '../../assets/img/steak.jpg'
      var candidates=[];
      candidates.push(pizza,eclair,steak)
      this.imageCandidate = candidates[Math.floor(Math.random()*(2-0+1)) + 0]
      console.log(this.imageCandidate)
    },
    changeDisplay(){
      if (!this.isArrow) {
        this.isArrow = true,
        this.isBestPost = true
      } else {
        this.isArrow = false,
        this.isBestPost = false
      }
    },
    goRecommend() {
      this.$router.push('/post/kind')
    },
    moveSearchPage(content) {
      this.$router.push({ name: constants.URL_TYPE.POST.SEARCH, query: { search: content }})
      this.searchKeyword = ''
    },
    filterRecvList(tmpSortRecvList) {
      const tmpFilterRecvList = tmpSortRecvList.slice(0, 10)
      tmpFilterRecvList.forEach(recv => {
        let tmp = this.articles.filter(item => {
          return item.postId === +recv[0]
        })
        this.displayRecvList.push(tmp)
      })
    },
    sortRecvList() {
      const tmpSortRecvList = []
      for (let idx in this.recvList) {
        tmpSortRecvList.push([idx, this.recvList[idx]])
      }
      tmpSortRecvList.sort((a, b) => {
        return b[1] - a[1];
      })
      this.filterRecvList(tmpSortRecvList)
    },
  },
  created() {
  },
  mounted() {
    this.selectPicture();
    setTimeout(() => {
      this.sendPostId({ articleId: null, status: 'list' })
        .then(() => {
          this.articles = JSON.parse(window.sessionStorage.getItem('articles'))
          setTimeout(() => {
            this.isLoading = false
            this.sortRecvList()
          }, 300)
        })
    }, 200)
  },
  updated() {
  },
}
</script>

<style scoped>
.best-post-area {
  z-index: 100;
  position: fixed;
  bottom: 12%;
  right: 5%;
  width: 350px;
}
/* 
.best-post-area:hover {
  
} */

.tmp {
  display: flex;
  align-items: center;
}

.arrow {
  position: absolute;
  bottom: 0;
  right: 0;
  transition: transform .3s ease-in;
}

.arrow.active {
  transform: rotate(-90deg)
}

.best-post {
  /* display: flex; */
  display: none;
  width: 300px;
  flex-direction: column;
  border: 1px solid;
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
}

.best-post.active {
  display: flex;
  animation: fadein 1s;
  -moz-animation: fadein 1s;
  -webkit-animation: fadein 1s;
}

@keyframes fadein {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.best-post-main {
  margin: 1rem auto;
  font-size: 20px;
  color: var(--primary-color);
  text-shadow: 1px 2px 2px var(--secondary-color);
}

.best-post div p {
  word-break: normal;
  margin: 7px 1rem;
  font-size: 13px;
}

.best-post-title {
  /* font-weight: bold; */
  /* text-shadow: 1px 1px 1px var(--primary-color); */
  text-shadow: 1px 1px 1px gray;
}

.best-post-title:hover {
  color: var(--third-color);
}

.best-post hr {
  width: 100%;
  margin-bottom: 5px;
  border-bottom: 2px solid rgba(200, 200, 200, 0.4);
}

.best-post-index {
  border: 2px solid var(--primary-color);
  padding: 2px;
  font-weight: bold;
  /* color: var(--primary-color) */
}

.post-list-link {
  margin-top: 1rem;
  display: flex;
}

.main-link {
  cursor: pointer;
  margin-top: 1rem;
  padding: 10px;
  border-bottom: 2px solid var(--secondary-color);
}

.main-link.active {
  border: none;
  opacity: 0.2;
}

#myInput {
  box-sizing: border-box;
  background-image: url('/css/searchicon.png');
  background-position: 10px 10px;
  background-repeat: no-repeat;
  width: 100%;
  font-size: 16px;
  padding: 12px 20px 12px 40px;
  border: 2px solid var(--primary-color);
  margin-bottom: 12px;
}

.main-loading {
  margin-top: 4rem;
  display: flex;
  justify-content: center;
}
header{
  display: block;
}

.picture{
  background-image: url('../../assets/img/pizza.jpg');
  overflow:hidden;

  position: relative;
background-position: center;
  background-size:cover;

  padding-top:300px;



 

}
.picture:before{
    display: block;
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 0;

    width: 100%;
}

</style>