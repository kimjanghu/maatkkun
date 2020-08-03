<template>
  <div v-if="userInfo">
    <div class="edit-user-container">
      <div class="edit-user">
        <div class="edit-user-content">
          <form class="edit-user-form">
            <div class="form-control">
              <label for="edit-nickname">Nickname</label>
              <input
                id="edit-nickname" 
                type="text" 
                :value="userInfo.nickname" 
                @input="changeData.changeNickname=$event.target.value" 
              />
              <button @click.prevent="nicknameDuplicateCheck" class="check-again">확인</button>
              <small>Error message</small>
            </div>
            
            <div class="form-control" id="change-password-control">
              <label for="edit-password">Password</label>
              <input
                @keyup="checkLength"
                v-model="changeData.changePassword" 
                id="edit-password" 
                type="password" 
              />
              <span>
                <i class="fas fa-eye"></i>
              </span>
              <small>Error message</small>
            </div>
            
            <div class="form-control" id="change-confirm-password">
              <label for="edit-confirm-password">Confirm Password</label>
              <input
                @keyup="checkPasswordsMatch"
                v-model="changeData.confirmPassword" 
                id="edit-confirm-password" 
                type="password" 
              />
              <span>
                <i class="fas fa-eye"></i>
              </span>
              <small>Error message</small>
            </div>

            <div>
              <label for="introduce">Introduce yourself</label>
              <textarea
                :value="userInfo.content" 
                @input="changeData.introduction=$event.target.value"
                class="introduce" 
                id="introduce" />
            </div>
            
            
            <input @click.prevent="emitEditUser" type="submit" value="수정" />
            <input @click.prevent="dropUser" class="drop-btn" type="submit" value="회원 탈퇴">
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import '../../assets/css/formControl.css'
import axios from 'axios'
import { mapActions } from 'vuex'

export default {
  name: 'EditUserForm',
  props: {
    userInfo: {
      type: Object
    }
  },
  data() {
    return {
      SERVER_URL: process.env.VUE_APP_API_URL,
      changeData: {
        changeNickname: '',
        changePassword: '',
        confirmPassword: '',
        introduction: ''
      }
    }
  },
  methods: {
    ...mapActions(['logout']),
    // Show error message
    showError(value, message) {
      const control = document.getElementById(value)
      control.className = 'form-control error'
      const small = control.querySelector('small')
      small.innerText = message
    },
    // Show success
    showSuccess(value) {
      const control = document.getElementById(value)
      control.className = 'form-control success'
    },
    // Check password
    checkPasswordsMatch() {
      if (this.changeData.changePassword !== this.changeData.confirmPassword) {
        this.showError(
          'change-confirm-password', 
          'Passwords do not match'
          )
      } else {
        this.showSuccess('change-confirm-password')
      }
    },
    // Check password length
    checkLength() {
      const min = 8
      if (this.changeData.changePassword.length < min) {
        this.showError(
          'change-password-control',
          `Password length must be at least ${min} characters`
        )
      } else {
        this.showSuccess('change-password-control')
      }
    },
    // Check nickname duplicate
    nicknameDuplicateCheck() {
      if (this.changeData.changeNickname.trim()) {
        axios.get(this.SERVER_URL + `/accounts/nicknameCheck?nickname=${this.changeData.changeNickname}`)
        .then(res => {
          if (res.data === '닉네임사용가능') {
            alert('사용 가능한 닉네임입니다.')
          } else {
            alert('이미 존재하는 닉네임입니다.')
          }
        })
        .catch(err => console.log(err))
      } else {
        alert('변경사항이 없습니다.')
      }
    },
    emitEditUser() {
      if (this.changeData.changePassword !== this.changeData.confirmPassword) {
        alert('비밀번호를 확인해주세요')
        return;
      }
      if (!this.changeData.changePassword.trim()) {
        this.changeData.changePassword = this.userInfo.password
        this.changeData.confirmPassword = this.userInfo.passwordConfirm
      } 
      if (!this.changeData.changeNickname.trim()) {
        this.changeData.changeNickname = this.userInfo.nickname
      }
      if (!this.changeData.introduction.trim()) {
        this.changeData.introduction = this.userInfo.content
      }
      if (
        this.changeData.introduction ||
        this.changeData.changePassword ||
        this.changeData.changeNickname
      ) {
        this.$emit('edit-user-data', this.changeData)
      }
    },
    dropUser(){
      const isexit = confirm("회원탈퇴를 정말 하시겠습니까?")
      if(isexit){
          this.islogin=false
          axios.delete(`${this.SERVER_URL}/accounts/dropUser?uid=${this.$cookies.get('auth-token')}`)
            .then(() => {
              alert("회원탈퇴가 완료되었습니다.")
              this.logout()
            })
            .catch(err => console.log(err))
      }
    }

  },
}
</script>

<style scoped>
button,
input[type='submit'] {
  background-color: var(--primary-color);
  border: 1px solid #696969;
  color: #fff;
  cursor: pointer;
  padding: 10px 12px;
}

input {
  border-radius: 9px;
  border: 1px solid;
  padding: 10px 0;
  width: 100%;
}

textarea {
  border-radius: 9px;
}

.form-control {
  margin-bottom: 10px;
  padding-bottom: 20px;
  position: relative;
}

.edit-user-container .edit-user {
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  position: absolute;
  overflow: hidden;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-width: 100%;
  width: 400px;
}

.edit-user-content {
  padding: 10px;
}

.edit-user-form div {
  margin: 15px 0;
}

.edit-user-form label {
  display: block;
  margin-bottom: 5px;
}

.edit-user-form .form-input {
  padding: 8px;
  width: 100%;
}

.introduce {
  border: 1px solid;
  width: 100%;
  height: 80px;
}

.form-control span {
  position: absolute;
  right: 9px;
  top: 35px;
  opacity: 0.3;
}

.check-again {
  position: absolute;
  right: -2px;
  top: 21px;
}

.drop-btn {
  margin-top: 15px;
}
</style>