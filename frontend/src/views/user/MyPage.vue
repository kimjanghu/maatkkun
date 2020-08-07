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
      <div v-if="userPostList">
        <div v-for="(post, index) in userPostList" :key="`post_${post.postId}`">
          <br>
          <p class="user-post"><router-link class="user-post-hover" :to="{ name: constants.URL_TYPE.POST.DETAIL, params:{ id: post.postId }}">{{ post.title }}</router-link></p>
          <p class="user-post-name"><i class="far fa-user fa-lg" style="margin-right: 5px;"></i>{{ post.nickname }}</p>
          <br>
          <div class="heart">
            <div class="tag" v-for="(tag, index) in post.hashtag.split(',')" :key="`hash_${index}`">
              <p class="tag-btn">#{{ tag }}</p>
            </div>
            <i class="fas fa-heart fa-lg redheart" style="color: red;"></i><p style="margin-left: 5px;">{{ post.likes }}</p>
          </div>
          <hr v-if="index+1 < userPostList.length" style="border: 1px solid var(--third-color)">
        </div>
      </div>
      <div v-else>
        <p class="empty-text">게시글이 없습니다.</p>
      </div>
    </div>

    <div v-show="!isLikeList" class="user-post-list">
      <div v-if="userPostLikedList">
        <div v-for="(post, index) in userPostLikedList" :key="`post_${post.postId}`">
          <br>
          <p class="user-post"><router-link class="user-post-hover" :to="{ name: constants.URL_TYPE.POST.DETAIL, params:{ id: post.postId }}">{{ post.title }}</router-link></p>
          <p class="user-post-name"><i class="far fa-user fa-lg" style="margin-right: 5px;"></i>{{ post.nickname }}</p>
          <br>
          <div class="heart">
            <div class="tag" v-for="(tag, index) in post.hashtag.split(',')" :key="`hash_${index}`">
              <p class="tag-btn">#{{ tag }}</p>
            </div>
            <i class="fas fa-heart fa-lg redheart" style="color: red;"></i><p style="margin-left: 5px;">{{ post.likes }}</p>
          </div>
          <hr v-if="index+1 < userPostLikedList.length" style="border: 1px solid var(--third-color)">
        </div>
      </div>
      <div v-else>
        <p class="empty-text">게시글이 없습니다.</p>
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
        uid: JSON.parse(window.localStorage.getItem('userInfo')).uid
      },
      userPostList: null,
      userPostLikedList: null,
      userInfo: null,
      isArticleList: false,
      isLikeList: true
    }
  },
  methods: {
    ...mapActions(['changeMain']),
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
      axios.post(`${this.SERVER_URL}${SERVER.ROUTES.userLikeList}`, { 'uid': this.clickUserId }, config)
        .then(res => {
          this.userPostLikedList = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    changeArticleList() {
      this.isArticleList = false
      this.isLikeList = true
    },
    changeLikeList() {
      this.isArticleList = true
      this.isLikeList = false
    }
  },
  mounted() {
  },
  created() {
    this.getUserPost()
    this.getUserInfo()
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

.user-post {
  text-align: left;
  font-size: 30px;
  margin: 1rem 0 0 2rem;
}

.user-post-hover:hover {
  color: var(--fourth-color);
}

.user-post-name {
  font-size: 13px;
  text-align: left;
  margin: 1rem 0 0 2rem;
}

.heart {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin: 0 2rem 1rem 0;
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

.last-hr {
  border: 2px solid var(--secondary-color);
  margin-top: 2rem;
  margin-bottom: 5rem;
}

.empty-text {
  margin-top: 2rem;
}
</style>
