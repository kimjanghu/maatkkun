<template>
  <div style="padding-top: 0;" v-if="isMain">
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
        <h3 class="best-post-main">MAATKKUN Best</h3>
        <hr>
        <div class="tmp" v-show="!isLoading" v-for="(recv, index) in displayRecvList" :key="`recv_${recv[0].postId}`">
          <p class="best-post-index">{{ index+1 }}</p>
          <router-link 
            class="best-post-title" 
            :to="{ name: constants.URL_TYPE.POST.DETAIL, params:{ id: recv[0].postId } }"
          >{{ recv[0].title }} <span class="realtime-count">{{ recvList[recv[0].postId] }}</span>
          </router-link>
        </div>
        <div v-if="isLoading" class="main-loading">
          <Loading />
        </div>
      </div>
    </div>

    <div class="wrapB">
      <div id="main-food-area" class="main-food-area">
        <p class="main-title main-page-logo">MAATKKUN</p>
        <img :src="require(`../../assets/img/${foodImg}`)" alt="food-image" />
        <div>
          <input 
            type="text" 
            v-model="searchKeyword" 
            class="myInput" 
            id="myInput"
            @keyup.enter="moveSearchPage(searchKeyword)" 
            placeholder="#태그 #제목 #내용" 
            title="Type in a name" 
          />
          <i class="fas fa-search fa-2x search-icon"></i>
        </div>
  
        <button class="recommend-button" @click="goRecommend">
          Let's Go MAATKKUN
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import constants from '@/lib/constants.js'
import Loading from '@/components/common/Loading.vue'


export default {
  name: 'Main',
  components: {
    Loading
  },
  props: {
    isMain: {
      type: Boolean
    }
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
      mainArticle: null,
      foodImg: '',
      displayRecvList: []
    }
  },
  computed: {
    ...mapState(['recvList', 'articles'])
  },
  methods: {
    ...mapActions(['sendPostId', 'sortArticles', 'getArticles']),
    changeDisplay() {
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
    async filterRecvList(tmpSortRecvList) {
      console.log(tmpSortRecvList)
      console.log(4)
      // await this.getArticles()
      console.log(this.articles)
      console.log(5)
      const tmpFilterRecvList = tmpSortRecvList.slice(0, 10)
      console.log(tmpFilterRecvList)
      await tmpFilterRecvList.forEach(recv => {
        let tmp = this.articles.list.filter(item => {
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
    selectImage() {
      const imgArray = [
        'steak.jpg',
        'pizza.jpg',
        'eclair.jpg'
      ]
      const image = imgArray[Math.floor(Math.random() * imgArray.length)];
      this.setImage(image)
    },
    setImage(image) {
      this.foodImg = ''
      this.foodImg = image
    }
  },
  created() {
    this.selectImage()
  },
  mounted() {
    setTimeout(() => {
      this.sendPostId({ articleId: null, status: 'list' })
        .then(() => {
          setTimeout(() => {
            this.isLoading = false
            this.sortRecvList()
          }, 2000)
        })
    }, 300)
    
  },
  updated() {
  },
}
</script>

<style scoped>
.main-food-area {
  display: block;
  position: relative;
}

.main-page-logo {
  z-index: 3;
  position: absolute;
  font-size: 4rem;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: var(--primary-color);
}

.main-food-area img {
  background-size: cover;
  width: 100%;
  height: 500px;
  border-bottom-left-radius : 10px;
  border-bottom-right-radius: 10px;
}

.main-food-area::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  border-bottom-left-radius : 10px;
  border-bottom-right-radius: 10px;
}

.recommend-button {
  z-index: 1;
  bottom: 2.5rem;
  right: 2.5rem;
  position: absolute;
  color:#fff;
  text-shadow: 3px 3px 3px var(--secondary-color);
}

.myInput {
  position: absolute;
  box-sizing: border-box;
  z-index: 2;
  top: 60%;
  left: 50%;
  width: 60%;
  transform: translateX(-50%);
  background-position: 10px 10px;
  background-repeat: no-repeat;
  width: 60%;
  font-size: 16px;
  padding: 12px 20px 12px 40px;
  border: 2px solid var(--primary-color);
  margin-bottom: 12px;
}

.search-icon {
  position: absolute;
  z-index: 3;
  top: 61.5%;
  right: 23%;
  color: var(--secondary-color);
}

.best-post-area {
  z-index: 100;
  position: fixed;
  bottom: 12%;
  right: 5%;
  width: 350px;
}

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
}

.realtime-count {
  color: var(--third-color);
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

.main-loading {
  margin-top: 4rem;
  display: flex;
  justify-content: center;
}

@keyframes fadein {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>