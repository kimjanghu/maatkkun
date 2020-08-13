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
      <div class="user-article" :class="{ active: isArticleList }" @click="changeArticleList">
        <p class="user-article-title">{{ userInfo.nickname }}'s Article List</p>
      </div>
      <div class="user-like" :class="{ active: isLikeList }" @click="changeLikeList">
        <p class="user-like-title">{{ userInfo.nickname }}'s Like List</p>
      </div>
    </div>

    <div v-show="!isArticleList" class="user-post-list">
      <UserList :userList="userPostList" />
    </div>

    <div v-show="!isLikeList" class="user-post-list">
      <UserList :userList="userPostLikedList" />
    </div>
    <hr class="last-hr">
  </div>
</template>

<script>
import axios from 'axios'
import SERVER from '@/api/drf'
import constants from '@/lib/constants'
import UserList from '@/components/common/UserList.vue'
import { mapActions } from 'vuex'

export default {
  name: 'Mypage',
  components: {
    UserList
  },
  data() {
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL,
      clickUserId: this.$route.params.id,
      userId: {
        uid: JSON.parse(window.localStorage.getItem('userInfo')).uid
      },
      userPostList: [],
      userPostLikedList: [],
      userInfo: null,
      isArticleList: false,
      isLikeList: true
    }
  },
  methods: {
    ...mapActions(['changeMain']),
    // Get user Information
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
    // Get user post list
    getUserPost() {
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      axios.post(`${this.SERVER_URL}${SERVER.ROUTES.userPostList}`, { 'uid': +this.clickUserId }, config)
        .then(res => {
          this.userPostList = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    // Get user liked post list
    getUserLikedPost() {
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      axios.post(`${this.SERVER_URL}${SERVER.ROUTES.userLikeList}`, { 'uid': this.clickUserId }, config)
        .then(res => {
          this.userPostLikedList = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    // Change article list status
    changeArticleList() {
      this.isArticleList = false
      this.isLikeList = true
    },
    // change like list status
    changeLikeList() {
      this.isArticleList = true
      this.isLikeList = false
    }
  },
  mounted() {
  },
  created() {
    console.log(this.clickUserId)
    this.getUserInfo()
    this.getUserPost()
    this.getUserLikedPost()
    this.changeMain(false)
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
  opacity: 0.2;
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

.user-post-list {
  width: 95%;
  margin: 0 auto;
}

.last-hr {
  border: 2px solid var(--secondary-color);
  margin-top: 2rem;
  margin-bottom: 5rem;
}
</style>
