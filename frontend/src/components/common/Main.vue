<template>
  <div v-show="isMain">
    <div class="best-post">
      <h3 class="best-post-main">MAAT KKUN Best</h3>
      <hr>
      <div v-for="(recv, index) in displayRecvList" :key="`recv_${recv[0].postId}`">
        <p><router-link class="best-post-title" :to="{ name: constants.URL_TYPE.POST.DETAIL, params:{ id: recv[0].postId } }">{{ index+1 }}. {{ recv[0].title }}</router-link></p>
      </div>
    </div>
    <div class="wrapB">
      <button class="create_button" @click="goRecommend">
        추천 받아보실래요?
      </button>
      <br><br>

      <input type="text" v-model="searchKeyword" id="myInput" v-on:keyup.enter="searchResult(searchKeyword)" placeholder="#태그 #제목 #내용" title="Type in a name">
      <br>
      <div class="post-list-link">
        <div class="main-link" :class="{ active: isRecentList }" @click.prevent="changeMainRecentList">
          <router-link :to="{ name: constants.URL_TYPE.POST.MAIN }"><i class="far fa-clock fa-lg" style="margin-right: 5px;"></i>최신순</router-link>
        </div>
        <div class="main-link" :class="{ active: isLikeList }" @click.prevent="changeMainLikeList">
          <router-link :to="{ name: constants.URL_TYPE.POST.LIKE }"><i class="far fa-heart fa-lg" style="margin-right: 5px;"></i>좋아요</router-link>
        </div>
        <div class="main-link" :class="{ active: isHitList }" @click.prevent="changeMainHitList">
          <router-link :to="{ name: constants.URL_TYPE.POST.VIEWS }"><i class="fas fa-fire-alt fa-lg" style="margin-right: 5px;"></i>조회순</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import constants from '@/lib/constants.js'

export default {
  name: 'Main',
  data() {
    return {
      constants,
      searchKeyword: '',
      isRecentList: false,
      isLikeList: true,
      isHitList: true,
      displayRecvList: []
    }
  },
  computed: {
    ...mapState(['isMain', 'recvList', 'articles'])
  },
  methods: {
    ...mapActions(['searchResult', 'changeMain', 'sendPostId']),
    goRecommend(){
      this.$router.push('/post/kind')
    },
    changeMainRecentList() {
      this.isRecentList = false,
      this.isLikeList = true,
      this.isHitList = true
    },
    changeMainLikeList() {
      this.isRecentList = true,
      this.isLikeList = false
      this.isHitList = true
    },
    changeMainHitList() {
      this.isRecentList = true,
      this.isLikeList = true,
      this.isHitList = false
    },
    filterRecvList(tmpSortRecvList) {
      const tmpFilterRecvList = tmpSortRecvList.slice(0, 10)
      tmpFilterRecvList.forEach(recv => {
        let tmp = this.articles.list.filter(item => {
          return item.postId === +recv[0]
        })
        this.displayRecvList.push(tmp)
      })
      // console.log(this.displayRecvList)
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
      setTimeout(() => {
        this.sortRecvList()
      }, 200)
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
  /* padding: 2rem; */
  display: flex;
  flex-direction: column;
  /* align-items: center; */
  /* justify-content: center; */
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
</style>