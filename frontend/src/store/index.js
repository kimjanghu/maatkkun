import Vue from 'vue'
import Vuex from 'vuex'

import cookies from 'vue-cookies'
import router from '@/router'
import axios from 'axios'
import SERVER from '@/api/drf'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    authToken: cookies.get('auth-token'),
    articles: [],
    likeArticles: [],
    hitArticles: [],
    postInfo: null,
    isMain: true
  },
  getters: {
    isLoggedIn: state => !!state.authToken,
    config: state => ({
      headers: {
        Authorization: `Token ${state.authToken}`
      }
    })
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.authToken = token
      cookies.set('auth-token', token)
    },
    SET_USERINFO(state, userInfo) {
      state.userInfo = userInfo
      window.localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },

    SET_ARTICLES(state, articles) {
      state.articles = articles
    },
    SET_LIKE_ARTICLES(state, likeArticles) {
      state.likeArticles = likeArticles
    },
    SET_HIT_ARTICLES(state, hitArticles) {
      state.hitArticles = hitArticles
    },
    SET_POSTINFO(state, post) {
      state.post = post
    },
    SET_MAIN(state, main) {
      state.isMain = main
    }
  },
  actions: {
    // User
    signup({ commit }, signupInfo) {
      if (!signupInfo.signupData.nickname.trim()) {
        alert('닉네임은 필수 입력 항목입니다.');
      } else if (!signupInfo.signupData.email.trim()) {
        alert('이메일은 필수 입력 항목입니다.');
      } else if (!signupInfo.signupData.password.trim()) {
        alert('비밀번호는 필수 입력 항목입니다.');
      } else if (!signupInfo.signupData.passwordConfirm.trim()) {
        alert('비밀번호를 한번 더 입력해주세요.');
      } else if (!signupInfo.checkValidation.checkedNickname) {
        alert('닉네임 중복 확인을 해주세요');
      } else if (!signupInfo.checkValidation.checkedEmail) {
        alert('이메일 중복확인을 해주세요');
      } else if (!signupInfo.checkValidation.match) {
        alert('인증번호를 다시 확인해주세요');
      } else {
        axios.post(process.env.VUE_APP_API_URL + SERVER.ROUTES.signup, signupInfo.signupData)
          .then(res => {
            commit('SET_TOKEN', res.data.uid)
            alert('Welcome blog MAAT KKUN');
            router.push('/');
          })
          .catch(err => alert(err));
      }
    },
    login({ commit }, loginData) {
      axios.post(process.env.VUE_APP_API_URL + SERVER.ROUTES.login, loginData)
        .then(res => {
          commit('SET_USERINFO', { uid: res.data.uid, nickname: res.data.nickname })
          commit('SET_TOKEN', res.data.uid)
          router.push('/')
        })
        .catch(() => alert('Check login information again'))
    },
    logout({ commit }) {
      commit('SET_TOKEN', null)
      cookies.remove('auth-token')
      window.localStorage.removeItem('userInfo')
      router.push('/')
    },
    // Post
    searchResult({ commit },keyword){
      console.log(keyword)
      axios.get(`${process.env.VUE_APP_API_URL}/articles/searchArticle/${keyword}`)
        .then((res)=>{
          commit('SET_ARTICLES', res.data)
        })
        .catch(err=>console.log(err))
    },
    getArticles({ commit }) {
      axios.get(process.env.VUE_APP_API_URL + SERVER.ROUTES.list)
        .then(res => {
          commit('SET_ARTICLES', res.data)
        })
        .catch(err => console.log(err))
    },
    getLikeArticles({ commit }) {
      axios.get(process.env.VUE_APP_API_URL + SERVER.ROUTES.likeList)
        .then(res => {
          commit('SET_LIKE_ARTICLES', res.data)
        })
        .catch(err => console.log(err))
    },
    getHitArticles({ commit }) {
      axios.get(process.env.VUE_APP_API_URL + SERVER.ROUTES.hitList)
        .then(res => {
          console.log(process.env.VUE_APP_API_URL + SERVER.ROUTES.hitList)
          console.log(res.data)
          commit('SET_HIT_ARTICLES', res.data)
        })
        .catch(err => console.log(err))
    },
    getSubarticles({ commit }) {
      axios.get(process.env.VUE_APP_API_URL + SERVER.ROUTES.temporaryList)
        .then(res => {
          commit('SET_ARTICLES', res.data)
        })
        .catch(err => console.log(err))
    },
    changeMain({ commit }, main) {
      commit('SET_MAIN', main)
    }
  },
  modules: {},
})