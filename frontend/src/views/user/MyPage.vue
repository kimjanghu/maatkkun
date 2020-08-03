<template>
  <div v-if="userInfo" class="mypage">
    <h2 class="mypage-title">{{ userInfo.nickname }}'s Page</h2>
    <br>
    <div class="user-info">
      <div class="profile">
        <div class="profile-picture"></div>
        <router-link v-if="clickUserId === userId.uid" class="edit-user-btn" :to="{ name: constants.URL_TYPE.USER.EDIT }">회원정보 수정</router-link>
      </div>
      


      <div v-if="userInfo" class="user-detail-info">
        <div class="email-title-area">
          <p class="email-title">Email</p>
          <hr>
          <p class="email-name">{{ userInfo.email }}</p>
        </div>
        <br>
        <div class="introduction-area">
          <p class="introduction-title">Introduction</p>
          <hr>
          <p class="introduction-name">{{ userInfo.content }}</p>
        </div>
      </div>
    </div>
    
    <div class="user-list">
      <div class="user-article" :class="{ active: isArticleList }" @click.stop.prevent="changeList">
        <p class="user-article-title">User Article List</p>
      </div>
      <div class="user-like" :class="{ active: isLikeList }" @click.stop.prevent="changeList">
        <p class="user-like-title">User Like List</p>
      </div>
    </div>

    <div v-show="!isArticleList" class="user-post-list">
      <div v-for="(post, index) in userPostList" :key="`post_${post.postId}`">
        <br>
        {{ post }}
        <p class="user-post"><router-link :to="{ name: constants.URL_TYPE.POST.DETAIL, params:{ id: post.postId }}">{{ post.title }}</router-link></p>
        <br>
        {{ post.hashtag.split(',') }}
        {{ hashList[index] }}
        <div class="heart">
          <i class="fas fa-heart fa-lg redheart" style="color: red;"></i><p style="margin-left: 5px;">{{ post.likes }}</p>
        </div>
        <hr v-if="index+1 < userPostList.length" style="border: 1px solid var(--third-color)">
      </div>
    </div>

    <div v-show="!isLikeList" class="user-post-list">
      <div v-for="(post, index) in userPostLikedList" :key="`post_${post.postId}`">
        <br>
        {{ post }}
        <p class="user-post"><router-link :to="{ name: constants.URL_TYPE.POST.DETAIL, params:{ id: post.postId }}">{{ post.title }}</router-link></p>
        <br>
        <div class="heart">
          <i class="fas fa-heart fa-lg redheart" style="color: red;"></i><p style="margin-left: 5px;">{{ post.likes }}</p>
        </div>
        <hr v-if="index+1 < userPostList.length" style="border: 1px solid var(--third-color)">
      </div>
    </div>
    <hr class="last-hr">
  </div>
</template>

<script>
import axios from 'axios'
import SERVER from '@/api/drf'
import constants from '@/lib/constants'
import { mapActions } from 'vuex'

export default {
  name: 'Mypage',
  components: {
  },
  data() {
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL,
      clickUserId: this.$route.params.id,
      userId: {
        uid: this.$cookies.get(`auth-token`)
      },
      userPostList: null,
      userPostLikedList: null,
      userInfo: null,
      hashList: [],
      isArticleList: false,
      isLikeList: true
    }
  },
  methods: {
    ...mapActions(['detailPage']),
    getUserInfo() {
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      axios.post(`${this.SERVER_URL}${SERVER.ROUTES.userInfo}`, { 'uid': this.clickUserId }, config)
        .then(res => {
          this.userInfo = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    getUserPost() {
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      axios.post(`${this.SERVER_URL}${SERVER.ROUTES.userPostList}`, { 'uid': +this.clickUserId }, config)
        .then(res => {
          this.userPostList = res.data
          for (const idx in res.data) {
            const hash = res.data[idx].hashtag.split(',')
            this.hashList.push(hash)
            console.log(this.hashList)
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getUserLikedPost() {
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      axios.post(`${this.SERVER_URL}/articles/likedList`, { 'uid': this.clickUserId }, config)
        .then(res => {
          this.userPostLikedList = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    changeList() {
      this.isArticleList = !this.isArticleList
      this.isLikeList = !this.isLikeList
    },
  },
  mounted() {
  },
  created() {
    this.getUserPost()
    this.getUserInfo()
    this.getUserLikedPost()
  },
}
</script>

<style scoped>
.mypage {
  margin: 0 auto; 
  width: 60%;
  text-align: center;
}

.mypage-title {
  font-size: 3rem;
  margin: 0 0 1.5rem 0;
}

.user-info {
  position: relative;
  display: flex;
  justify-content: space-evenly;
  margin-bottom: 10px;
}

.user-detail-info {
  width: 30rem;
}

.profile {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.profile .profile-picture {
  background-color:#86efef;
  width: 200px;
  height: 200px;
  border-radius: 100px;
  text-align: center;
  font-size: 12px;
  vertical-align: middle;
  line-height: 150px;
}

.profile .edit-user-btn {
  margin-top: 1rem;
  background-color: var(--primary-color);
  padding: 10px;
  text-align: center;
  vertical-align: middle;
  border-radius: 3px;
  color: #fff;
}

.user-list {
  margin-top: 2rem;
  display: flex;
  justify-content: space-around;
  border-bottom: 2px solid var(--secondary-color);
}

.user-post-list {
  width: 95%;
  margin: 0 auto;
}

.user-article,
.user-like {
  width: 50%;
  border: 1px solid var(--primary-color);
  border-radius: 5px 5px 0 0;
  box-shadow: 0 0 10px 1px var(--primary-color);
  cursor: pointer;
}

.user-article.active,
.user-like.active {
  width: 50%;
  border: none;
  opacity: 0.3;
  box-shadow: none;
  /* transition: transform 0.3s ease-in; */
}


.user-article-title,
.user-like-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--secondary-color);
}



.email-title-area,
.introduction-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.email-title,
.introduction-title {
  font-weight: bold;
  font-size: 20px;
}

.email-title-area hr,
.introduction-area hr {
  width: 90%;
  border: 1px solid var(--secondary-color);
}

.email-name,
.introduction-name {
  margin: 5px 0;
}

/* .like-list-container {
  box-sizing: border-box;
  position: relative;
} */

/* .like-list {
  position: absolute;
  margin-top: 1rem;
  background-color: #fff;
  border: 1px solid;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  max-width: 100%;
  width: 500px;
  height: 200px;
  overflow: auto;
} */

.user-post {
  text-align: left;
  font-size: 30px;
  margin: 1rem 0 0 2rem;
  /* margin-left: 2rem; */
}


.last-hr {
  border: 2px solid var(--secondary-color);
  margin-bottom: 5rem;
}

.heart {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin: 0 2rem 1rem 0;
  /* margin-bottom: 1rem; 
  margin-right: 2rem; */
}
</style>
