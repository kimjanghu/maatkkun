<template>
  <div id="join">
    <div class="join-container">
      <div class="join">
        <div class="join-header">
          <h3 style="text-align: center;">회원가입</h3>
        </div>
        <div class="join-content">
          <form class="join-form">
            <div class="form-control" id="nickname-control">
              <input
                v-model="signupInfo.signupData.nickname"
                id="nickname"
                placeholder="닉네임을 입력해주세요"
                type="text"
                @change="checkNickname"
              />
              <button @click.prevent="nicknameDuplicateCheck" class="check-again">확인</button>
              <small>Error message</small>
            </div>

            <div class="form-control" id="email-control">
              <input
                v-model="signupInfo.signupData.email"
                id="email"
                placeholder="이메일을 입력해주세요"
                type="text"
                @change="checkEmail"
              />
              <button @click.prevent="emailDuplicateCheck" class="check-again">확인</button>
              <small>Error message</small>
            </div>

            <div class="form-control" id="certification-number">
              <input v-model="checkNumber" placeholder="인증번호를 입력해주세요" type="text" />
              <button @click.prevent="checkCertificationNumber" class="check-again">확인</button>
              <small>Error message</small>
            </div>

            <div class="form-control" id="password-control">
              <input
                @keyup="checkLength"
                v-model="signupInfo.signupData.password"
                id="password"
                :type="passwordType"
                placeholder="비밀번호를 입력해주세요"
              />
              <span :class="{active : passwordType==='text'}">
                <i class="fas fa-eye"></i>
              </span>
              <small>Error message</small>
            </div>

            <div class="form-control" id="password-confirm-control">
              <input
                @keyup="checkPasswordsMatch"
                v-model="signupInfo.signupData.passwordConfirm"
                id="password-confirm"
                :type="passwordConfirmType"
                placeholder="비밀번호를 한번 더 입력해주세요"
              />
              <span :class="{active : passwordConfirmType==='text'}">
                <i class="fas fa-eye"></i>
              </span>
              <small>Error message</small>
            </div>
          
            <input @click.prevent="signup(signupInfo)" type="submit" class="submit-btn" />
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
  components: {},
  data: () => {
    return {
      SERVER_URL: process.env.VUE_APP_API_URL,
      signupInfo: {
        signupData: {
          nickname: '',
          email: '',
          password: '',
          passwordConfirm: '',
        },
        checkValidation: {
          checkedEmail: false,
          checkedNickname: false,
          match: false,
        },
      },
      certificationNumber: '',
      checkNumber: '',
      passwordType: 'password',
      passwordConfirmType: 'password'
    };
  },
  created() {},
  methods: {
    // bring signup method in store
    ...mapActions(['signup']),
    // Show error message
    showError(value, message) {
      const control = document.getElementById(value);
      control.className = "form-control error";
      const small = control.querySelector("small");
      small.innerText = message;
    },
    // Show success
    showSuccess(value) {
      const control = document.getElementById(value);
      control.className = "form-control success";
    },
    // Check password
    checkPasswordsMatch() {
      if (this.signupInfo.signupData.password !== this.signupInfo.signupData.passwordConfirm) {
        this.showError("password-confirm-control", "Passwords do not match");
      } else {
        this.showSuccess("password-confirm-control");
      }
    },
    // Check certification number
    checkCertificationNumber() {
      if (+this.checkNumber === this.certificationNumber) {
        this.signupInfo.checkValidation.match = true;
        alert("인증이 완료되었습니다.");
      } else {
        alert("인증번호를 잘못 입력하셨습니다.");
      }
    },
    // Check password length
    checkLength() {
      const min = 8;
      if (this.signupInfo.signupData.password.length < min) {
        this.showError(
          "password-control",
          `Password length must be at least ${min} characters`
        );
      } else {
        this.showSuccess("password-control");
      }
    },
    // Check nickname
    checkNickname() {
      if (this.signupInfo.signupData.nickname.trim() === "") {
        this.showError("nickname-control", "Nickname necessary");
      } else {
        this.showSuccess("nickname-control");
      }
    },
    // Check email validation
    checkEmail() {
      const re = /^(([^<>()\]\\.,;:\s@"]+(\.[^<>()\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      if (re.test(this.signupInfo.signupData.email.trim())) {
        this.showSuccess("email-control");
      } else if (!this.signupInfo.signupData.email.trim()) {
        this.showError("email-control", "Email necessary");
      } else {
        this.showError("email-control", "Email is not valid");
      }
    },
    // Check nickname duplicate
    nicknameDuplicateCheck() {
      axios.get(this.SERVER_URL + `/accounts/nicknameCheck?nickname=${this.signupInfo.signupData.nickname}`)
        .then(res => {
          if (res.data === "닉네임사용가능") {
            console.log(this.SERVER_URL)
            this.signupInfo.checkValidation.checkedNickname = true;
            alert("사용 가능한 닉네임입니다.");
          } else {
            alert("이미 존재하는 닉네임입니다.");
          }
        })
        .catch(err => console.log(err));
    },
    // Check email duplicate
    emailDuplicateCheck() {
      axios.get(this.SERVER_URL + `/accounts/emailCheck?email=${this.signupInfo.signupData.email}`)
        .then(res => {
          console.log(res)
          if (res.data === "아이디사용가능") {
            alert("사용 가능한 이메일입니다. 인증번호를 보냈습니다. 확인해주세요.")
            this.signupInfo.checkValidation.checkedEmail = true
            axios.get(this.SERVER_URL + `/accounts/emailAuth?email=${this.signupInfo.signupData.email}`)
              .then(res => {
                console.log(res);
                this.certificationNumber = res.data;
              });
          } else {
            alert("이미 존재하는 이메일입니다.");
          }
        })
        .catch(err => console.log(err));
    }
  },
};
</script>

<style scope>
.join-btn {
  cursor: pointer;
}

/* .modal {
  width: 500px !important;
} */

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
  border-radius: 9px;
  padding: 10px 0;
  width: 100%;
}


/* .form-control.success input {
  border-color: var(--success-color);
}

.form-control.error input {
  border-color: var(--error-color);
}

.form-control small {
  color: var(--error-color);
  position: absolute;
  bottom: 0;
  left: 0;
  visibility: hidden;
}

.form-control.error small {
  visibility: visible;
} */

.join-container .join {
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  position: absolute;
  overflow: hidden;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-width: 100%;
  width: 500px;
}

.join-header {
  background: var(--primary-color);
  color: #fff;
  padding: 10px;
}

.join-header h3 {
  margin: 0;

}

.join-content {
  padding: 10px;
}

.join-form div {
  margin: 15px 0;
}

.join-form label {
  display: block;
  margin-bottom: 5px;
}

.join-form .form-input {
  padding: 8px;
  width: 100%;
}

.check-again {
  position: absolute;
  right: -2px;
  top: 0;
}

.form-control span {
  position: absolute;
  right: 9px;
  top: 11px;
  opacity: 0.3;
}
</style>