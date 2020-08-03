import Vue from 'vue'
import Vuex from 'vuex'

import cookies from 'vue-cookies'
import router from '@/router'
import axios from 'axios'
import constants from '@/lib/constants'
import SERVER from '@/api/drf'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    authToken: cookies.get('auth-token'),
    articles: [],
    postInfo: null
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
    // SET_TOKEN(state, token) {
    //   state.authToken = token
    //   cookies.set('auth-token', token)
    // },
    SET_TOKEN(state, token) {
      state.authToken = token
      cookies.set('auth-token', token)
    },
    SET_ARTICLES(state, articles) {
      state.articles = articles
    },
    SET_POSTINFO(state, post) {
      state.post = post
    }
    //   SET_USERINFO(state, userInfo) {
    //     state.userInfo = userInfo
    //     window.localStorage.setItem('userInfo', userInfo);
    //   }
  },
  actions: {
    // postAuthData({ commit }, info) {
    //   axios.post(SERVER.URL + info.location, info.data)
    //     .then(res => {
    //       commit('SET_TOKEN', res.data.key)
    //       router.push({ name: 'Home' })
    //     })
    //     .catch(err => alert(err.response))
    // },
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
    // signup({ dispatch }, signupData) {
    //   const info = {
    //     data: signupData,
    //     location: SERVER.ROUTES.signup
    //   }
    //   dispatch('postAuthData', info)
    // },
    login({ commit }, loginData) {
      // console.log(loginData)
      // const info = {
      //   data: loginData,
      //   location: SERVER.ROUTES.login
      // }
      // commit('SET_USERINFO', loginData)
      // dispatch('postAuthData', info)
      axios.post(process.env.VUE_APP_API_URL + SERVER.ROUTES.login, loginData)
        .then(res => {
          commit('SET_TOKEN', res.data.uid)
          router.push('/')
        })
        .catch(() => alert('Check login information again'))
    },
    logout({ commit }) {
      commit('SET_TOKEN', null)
      cookies.remove('auth-token')
      router.push('/')
    },
    // logout({ getters, commit }) {
    //   axios.post(SERVER.URL + SERVER.ROUTES.logout, null, getters.config)
    //     .then(() => {
    //       // console.log(res)
    //       commit('SET_TOKEN', null)
    //       cookies.remove('auth-token')
    //       window.localStorage.removeItem('userInfo');
    //       router.push({ name: 'Home' })
    //     })
    //     .catch(err => console.log(err.response.data))
    // },
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
          console.log(res);
          commit('SET_ARTICLES', res.data)
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
    detailPage({ commit }, postId) {
      axios.get(process.env.VUE_APP_API_URL + `${SERVER.ROUTES.detail}?postId=${postId}`)
        .then((res)=>{
          commit('SET_POSTINFO', res.data)
          router.push({ name: constants.URL_TYPE.POST.DETAIL,params:{ articleData: res.data }})
        })
        .catch(err=>console.log(err))
    }
  },
  modules: {},
})