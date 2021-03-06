import Vue from 'vue'
import Vuex from 'vuex'

// import cookies from 'vue-cookies'
// import router from '@/router'
import axios from 'axios'
import SERVER from '@/api/drf'
import constants from '@/lib/constants'
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'
import userStore from '@/store/modules/userStore'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    constants,
    // authToken: cookies.get('auth-token'),
    articles: [],
    likeArticles: [],
    hitArticles: [],
    starArticles: [],
    searchArticles: [],
    recvList: [],
    socketStatus: null,
    postInfo: null
  },
  getters: {
    // isLoggedIn: state => !!state.authToken,
    // config: state => ({
    //   headers: {
    //     Authorization: `Token ${state.authToken}`
    //   },
    // })
  },
  mutations: {
    // SET_TOKEN(state, token) {
    //   state.authToken = token
    //   cookies.set('auth-token', token)
    // },
    // SET_USERINFO(state, userInfo) {
    //   state.userInfo = userInfo
    //   window.localStorage.setItem('userInfo', JSON.stringify(userInfo))
    // },
    // SET_SIGNUP(state, check) {
    //   state.isSignup = check
    // },


    SET_RECV_DATA(state, recvList) {
      state.recvList = recvList
    },
    SET_SOCKET_IN(state, socketIn) {
      state.socketStatus = socketIn
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
    SET_STAR_ARTICLES(state, starArticles) {
      state.starArticles = starArticles
    },
    SET_SEARCH_ARTICLES(state, searchArticles) {
      state.searchArticles = searchArticles
    },
    SET_POSTINFO(state, post) {
      state.post = post
    }, 
  },
  actions: {
    // User
    // signup({ commit }, signupInfo) {
    //   if (!signupInfo.signupData.nickname.trim()) {
    //     alert('닉네임은 필수 입력 항목입니다.');
    //   } else if (!signupInfo.signupData.email.trim()) {
    //     alert('이메일은 필수 입력 항목입니다.');
    //   } else if (!signupInfo.signupData.password.trim()) {
    //     alert('비밀번호는 필수 입력 항목입니다.');
    //   } else if (!signupInfo.signupData.passwordConfirm.trim()) {
    //     alert('비밀번호를 한번 더 입력해주세요.');
    //   } else if (!signupInfo.checkValidation.checkedNickname) {
    //     alert('닉네임 중복 확인을 해주세요');
    //   } else if (!signupInfo.checkValidation.checkedEmail) {
    //     alert('이메일 중복확인을 해주세요');
    //   } else if (!signupInfo.checkValidation.match) {
    //     alert('인증번호를 다시 확인해주세요');
    //   } else {
    //     axios.post(process.env.VUE_APP_API_URL + SERVER.ROUTES.signup, signupInfo.signupData)
    //       .then(() => {
    //         commit('SET_TOKEN', null)
    //         cookies.remove('auth-token')
    //         alert('Welcome blog MAAT KKUN')
    //         router.push({ name: constants.URL_TYPE.USER.LOGIN })
    //       })
    //       .catch(err => alert(err));
    //   }
    // },
    // login({ commit }, loginData) {
    //   axios.post(process.env.VUE_APP_API_URL + SERVER.ROUTES.login, loginData)
    //     .then(res => {
    //       commit('SET_USERINFO', { uid: res.data.uid, nickname: res.data.nickname })
    //       commit('SET_TOKEN', res.data.uid)
    //       // router.push('/')
    //       window.document.location.href = '/'
    //     })
    //     .catch(() => alert('Check login information again'))
    // },
    // logout({ commit }) {
    //   const check = confirm('로그아웃 하시겠습니까?')
    //   if (check) {
    //     commit('SET_TOKEN', null)
    //     cookies.remove('auth-token')
    //     window.localStorage.removeItem('userInfo')
    //     router.push('/')
    //     alert('로그아웃 되었습니다.')
    //   }
    // },


    // Post
    searchPost({ commit }, keyword){
      axios.get(`${process.env.VUE_APP_API_URL}/articles/searchArticle/${keyword}`)
        .then((res)=>{
          commit('SET_SEARCH_ARTICLES', res.data)
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
    async sortArticles({ dispatch }) {
      await dispatch('sendPostId', { articleId: null, status: 'list' })
      await dispatch('getArticles')
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
          commit('SET_HIT_ARTICLES', res.data)
        })
        .catch(err => console.log(err))
    },
    getStarArticles({ commit }) {
      axios.get(process.env.VUE_APP_API_URL + SERVER.ROUTES.starList)
        .then(res => {
          commit('SET_STAR_ARTICLES', res.data)
        })
        .catch(err => console.log(err))
    },
    sendPostId({ commit }, articleData) {
      if (this.stompClient && this.stompClient.connected) {
        const msg = { 
          postId: articleData.articleId,
          status: articleData.status
        };
        commit('SET_SOCKET_IN', true)
        this.stompClient.send("/receive", JSON.stringify(msg), {})
      }
    },
    connectWebsocket({ commit }) {
      let socket = new SockJS(process.env.VUE_APP_API_URL)
      this.stompClient = Stomp.over(socket)
      this.stompClient.connect({}, () => {
        // 소켓 연결 성공
        this.connected = true
        this.stompClient.subscribe("/send", res => {
          // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
          commit('SET_RECV_DATA', JSON.parse(res.body))
        });
      },
      err => {
        console.log('소켓 연결 실패', err)
        this.connected = false
      });
    }
  },
  modules: {
    userStore
  },
})