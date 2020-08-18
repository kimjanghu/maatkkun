<template>
  <div>
    <div v-show="!isUserChecked" class="not-user-checked">
        <input class="password-input" type="password" v-model="password" placeholder="비밀번호를 입력해주세요">
        <button class="password-btn" @click.prevent="checkPassword">확인</button>
    </div>
    <div v-show="isUserChecked">
      <EditUserForm :userInfo="userInfo" @edit-user-data="updateUserInfo" />
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import SERVER from '@/api/drf'
import EditUserForm from '@/components/form/EditUserForm.vue'


export default {
  name: 'EditUserInfo',
  components: {
    EditUserForm
  },
  data() {
    return {
      SERVER_URL: process.env.VUE_APP_API_URL,
      userId: {
        uid: this.$cookies.get(`auth-token`)
      },
      userInfo: null,
      isUserChecked: false,
      password: ''
    }
  },
  methods: {
    checkPassword() {
      if (this.password === this.userInfo.password) {
        this.isUserChecked = !this.isUserChecked
      } else {
        alert('비밀번호를 확인해주세요')
      }
    },
    getUserInfo() {
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      axios.post(`${this.SERVER_URL}${SERVER.ROUTES.userInfo}`, this.userId, config)
        .then(res => {
          this.userInfo = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    updateUserInfo(editUserInfo) {
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      this.userInfo.nickname = editUserInfo.changeNickname
      this.userInfo.password = editUserInfo.changePassword
      this.userInfo.passwordConfirm = editUserInfo.confirmPassword
      this.userInfo.content = editUserInfo.introduction
      axios.put(`${this.SERVER_URL}${SERVER.ROUTES.updateUserInfo}`, this.userInfo, config)
        .then(() => {
          window.localStorage.setItem('userInfo', JSON.stringify({ 
            "uid": this.$cookies.get(`auth-token`),
            "nickname": this.userInfo.nickname
          }))
          alert('회원 정보가 수정되었습니다.')
          this.$router.push('/')
        })
        .catch(err => console.log(err))
    }
  },
  created() {
    this.getUserInfo()
  }
}
</script>

<style scoped>
.password-input {
  border-radius: 5px;
  width: 100%;
}

.password-btn {
  position: absolute;
  border-radius: 5px;
  top: 0;
  right: 0;
}

.not-user-checked {
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  position: absolute;
  overflow: hidden;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-width: 100%;
  width: 400px;
}
</style>