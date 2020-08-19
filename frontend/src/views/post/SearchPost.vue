<template>
  <div>
    <div class="search-area">
      <div class="search-bar-area">
        <p class="main-title main-logo">MAATKKUN</p>
        <div @keypress.enter="moveSearchPage(searchKeyword);" class="inner-search-bar">
          <input class="search-bar" type="text" v-model="searchKeyword" />
          <i @click="moveSearchPage(searchKeyword);" class="fas fa-search search-icon"></i>
        </div>
      </div>
    </div>
    <PostList :likedposts="likedposts" :articles="searchArticles" />
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import axios from 'axios'
import constants from '@/lib/constants.js'
import PostList from '@/components/common/PostList.vue'


export default {
  name: 'SearchPost',
  components: {
    PostList
  },
  data() {
    return {
      constants,
      searchKeyword: '',
      SERVER_URL: process.env.VUE_APP_API_URL,
      likedposts: []
    }
  },
  computed: {
    ...mapState(['searchArticles'])
  },
  methods: {
    ...mapActions(['searchPost']),
    checkLikeList(res) {
      if (res.data.likedpost) {
        const liked_list = res.data.likedpost.split(',').map(i=>parseInt(i))
        const result = liked_list.slice(0,-1)
        this.likedposts = result
      }
    },
    moveSearchPage(content) {
      if (content.trim()) {
        this.$router.push({ name: constants.URL_TYPE.POST.SEARCH, query: { search: content }})
        this.searchPost(content);
      } else {
        alert('검색어를 입력해주세요')
      }
    },
  },
  created() {
    this.searchKeyword = this.$route.query.search
    this.searchPost(this.searchKeyword)
  },
  mounted() {
    if(this.$cookies.get('auth-token')){
      axios.post(`${this.SERVER_URL}/accounts/userDetail`,{"uid":this.$cookies.get('auth-token')})
        .then((res)=>{
          this.checkLikeList(res)
        }
      )
    }
  }
}
</script>

<style scoped>
.search-area {
  margin: 0 auto;
  width: 80%;
  display: flex;
  justify-content: center;
}

.main-logo {
  font-size: 40px;
  color: var(--primary-color);
}

.search-bar-area {
  display: flex;
  align-items: center;
}

.search-bar {
  border-radius: 20px;
  margin-left: 1rem;
  padding: 13px;
  width: 400px;
}

.inner-search-bar {
  position: relative;
}

.search-icon {
  cursor: pointer;
  position: absolute;
  top: 50%;
  right: 1rem;
  transform: translateY(-50%);
  font-size: 29px;
}
</style>