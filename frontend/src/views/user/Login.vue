<template>
  <div>
    <div class="login-container">
      <router-link class="main-title logo" :to="{ name: constants.URL_TYPE.POST.MAIN}">MAATKKUN</router-link>
      <div class="login">
        <div class="login-header">
          <h3 style="text-align: center;">login</h3>
        </div>
        <div class="login-content">
          <form class="login-form">
            <div class="form-control" id="email-control">
              <label for="email">Email</label>
              <input
                @change="checkEmail"
                type="text"
                id="email"
                v-model="loginData.email"
                placeholder="이메일을 입력해주세요"
              />
              <small>Error message</small>
            </div>
            <div>
              <label for="password">Password</label>
              <input type="password" id="password" v-model="loginData.password" placeholder="영문, 숫자 혼용 8자 이상" />
            </div>
            <input @click.prevent="login(loginData)" type="submit" class="submit-btn" style="color:#000;" />
          </form>
          <div class="add-option">
            <div class="wrap">
              <p>아직 회원이 아니신가요?</p>
              <router-link :to="{ name:constants.URL_TYPE.USER.JOIN }" class="btn--text">회원가입</router-link>
            </div>
          </div>
        </div>
      </div>
      <p class="copyright">Copyright <i class="far fa-copyright"> 2020</i> MAATKKUN. All Rights Reserved.</p>
    </div>
  </div>
</template>


<script>
import constants from '@/lib/constants'
import { mapActions } from 'vuex'
import '@/assets/css/formControl.css'

export default {
  components: {
  },
  data: () => {
    return {
      constants,
      loginData: {
        email: '',
        password: ''
      }
    }
  },
  methods: {
    ...mapActions('userStore', ['login']),
    showError(value, message) {
      const control = document.getElementById(value)
      control.className = "form-control error"
      const small = control.querySelector("small")
      small.innerText = message
    },
    showSuccess(value) {
      const control = document.getElementById(value)
      control.className = "form-control success"
    },
    checkEmail() {
      const re = /^(([^<>()\]\\.,;:\s@"]+(\.[^<>()\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
      if (re.test(this.loginData.email.trim())) {
        this.showSuccess("email-control")
      } else {
        this.showError("email-control", "Email is not valid")
      }
    },
  },
  created() {
  },
  mounted() {
  },
  
}
</script>

<style scoped>
.logo {
  text-align: center;
  position: absolute;
  left: 50%;
  top: 10%;
  transform: translateX(-50%);
  font-size: 3rem;
  cursor: pointer;
  color: var(--primary-color);
}

.login-btn {
  cursor: pointer;
}

button,
input[type='submit'] {
  background-color: var(--primary-color);
  border: 1px solid #696969;
  color: #fff;
  cursor: pointer;
  padding: 10px 12px;
}

input {
  border: 1px solid;
  padding: 10px 0;
  width: 100%;
}

.form-control {
  margin-bottom: 10px;
  padding-bottom: 20px;
  position: relative;
}

.login-container .login {
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

.login-header {
  background: var(--primary-color);
  color: #000;
  padding: 10px;
}

.login-header h3 {
  margin: 0;

}

.login-content {
  padding: 10px;
}

.login-form div {
  margin: 15px 0;
}

.login-form label {
  display: block;
  text-align: center;
  margin-bottom: 1rem;
}

.login-form .form-input {
  padding: 8px;
  width: 100%;
}

.add-option {
  width: 100%;
}
.add-option .wrap {
  width: 100%;
  display: flex;
  justify-content: center;
  margin: 10px 0;
}

.add-option p {
  margin-right: 10px;
  color: #aaa;
}

.add-option a {
  margin-top: 4px;
}

.copyright {
  z-index: 100;
  position: fixed;
  bottom: 0;
  color: #000;
  left: 50%;
  transform: translateX(-50%);
}
</style>