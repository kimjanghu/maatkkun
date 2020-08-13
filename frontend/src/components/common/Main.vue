<template>
  <div v-if="isMain">
    <div class="best-post">
      <h3 class="best-post-main">MAAT KKUN Best</h3>
      <hr>
      
      <div v-show="!isLoading" v-for="(recv, index) in displayRecvList" :key="`recv_${recv[0].postId}`">
        <p><router-link class="best-post-title" :to="{ name: constants.URL_TYPE.POST.DETAIL, params:{ id: recv[0].postId } }">{{ index+1 }}. {{ recv[0].title }}</router-link></p>
      </div>
      <div v-if="isLoading" class="main-loading">
        <Loading />
      </div>
    </div>
    <div class="wrapB">
      <button class="create_button" @click="goRecommend">
        추천 받아보실래요?
      </button>
      <br><br>

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
  data() {
    return {
      constants,
      searchKeyword: '',
      isRecentList: false,
      isLikeList: true,
      isHitList: true,
      isLoading: true,
      articles: null,
      displayRecvList: []
    }
  },
  computed: {
    ...mapState(['isMain', 'recvList'])
  },
  methods: {
    ...mapActions(['changeMain', 'sendPostId']),
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
    setTimeout(() => {
      this.sendPostId({ articleId: null, status: 'list' })
        .then(() => {
          this.articles = JSON.parse(window.sessionStorage.getItem('articles'))
          setTimeout(() => {
            this.isLoading = false
            this.sortRecvList()
          }, 100)
        })
    }, 200)
  },
  updated() {
  },
}
</script>

<style scoped>
.best-post {
  position: fixed;
  top: 20%;
  right: 5%;
  display: flex;
  flex-direction: column;
  width: 200px;
}

.best-post-main {
  margin: 1rem auto;
  font-size: 20px;
  color: var(--primary-color);
}

.best-post div p {
  word-break: normal;
  margin: 7px 1rem;
  font-size: 13px;
}

.best-post-title:hover {
  color: var(--third-color);
}

.best-post hr {
  width: 100%;
  margin-bottom: 5px;
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
</style>