import cookies from 'vue-cookies'
import router from '@/router'
import axios from 'axios'
import SERVER from '@/api/drf'
import constants from '@/lib/constants'

const userStore = {
  namespaced: true,
  state: {
    constants,
    authToken: cookies.get('auth-token')
  },
  getters: {
    isLoggedIn: state => !!state.authToken,
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.authToken = token
      cookies.set('auth-token', token)
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
          .then(() => {
            commit('SET_TOKEN', null)
            cookies.remove('auth-token')
            alert('Welcome blog MAAT KKUN')
            router.push({ name: constants.URL_TYPE.USER.LOGIN })
          })
          .catch(err => alert(err));
      }
    },
    login({ commit }, loginData) {
      axios.post(process.env.VUE_APP_API_URL + SERVER.ROUTES.login, loginData)
        .then(res => {
          commit('SET_TOKEN', res.data.uid)
          const userInfo = { uid: res.data.uid, nickname: res.data.nickname }
          window.localStorage.setItem('userInfo', JSON.stringify(userInfo))
          window.document.location.href = '/home'
        })
        .catch(() => alert('Check login information again'))
    },
    logout({ commit }) {
      const check = confirm('로그아웃 하시겠습니까?')
      if (check) {
        commit('SET_TOKEN', null)
        cookies.remove('auth-token')
        window.localStorage.removeItem('userInfo')
        router.push('/home')
        alert('로그아웃 되었습니다.')
      } else {
        router.go(-1)
      }
    },
  }
}

export default userStore