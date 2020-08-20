<template>
  <div class="intro">
    <div class="first-area">
      <p class="introduction">맛을 즐거움으로 삼는 맛꾼을 위한 블로그.</p>
      <p class="go-main" @click="moveToHome">MAATKKUN<i class="fas fa-angle-double-right arrow"></i></p>
    </div>
    <div class="second-area">
      <div class="open-blog">
        <p class="open-blog-title">"Open Blog"</p>
        <p class="open-blog-content">
          맛집 <span>'공유'</span> 취지에 집중하기 위해<br>
          블로그 내에서 모든 사용자가 포스팅할 수 있는
          <span>'Open Blog'</span> 구현.
        </p>
      </div>
      <div class="open-blog">
        <p class="open-blog-title">"맛집 코스 추천"</p>
        <p class="open-blog-content">
          <span>'지도 API'</span>를 활용하여<br>
          블로그에 포스팅된 맛집을 기반으로하는<br>
          <span>'코스 추천 기능'</span> 구현.
        </p>
      </div>
      <div class="open-blog">
        <p class="open-blog-title">"Real Time"</p>
        <p class="open-blog-content">
          <span>'코스 추천'</span>을 결정하는 지표이자 <br>
          맛집 인기 척도를 파악할 수 있는<br>
          게시글 별 <span>'실시간 동시접속자'</span>
          기능 구현
        </p>
      </div>
    </div>
    <div class="third-area">
      <p class="team-name">TEAM <span>JON-DDOK</span></p>
      <hr>
      <div class="introduction-area">
        <p class="position"><span>L</span>eader - Backend</p>
        <div class="introduction-main">
          <div class="picture-area">
            <img class="team-picture" src="../assets/img/leeownho.jpg" alt="leeownho">
            <p class="name">LeeOwnho</p>
          </div>
          <p class="member-introduction">
            안녕하세요. 사용자의 시선을 가진 개발자 이원호입니다.
          </p>
        </div>
      </div>
      <hr>
      <div class="introduction-area">
        <p class="position"><span>M</span>ember - Frontend</p>
        <div class="introduction-main">
          <div class="picture-area">
            <img class="team-picture" src="../assets/img/kimjanghu.jpg" alt="kimjanghu">
            <p class="name">KimJanghu</p>
          </div>
          <p class="member-introduction">
            안녕하세요.<br>
            세상이 놀랄만한 UI/UX를 사용자에게 제공하고 싶은 Frontend 개발자 김장후입니다.<br>
            혼자가 아닌 다양한 개발자와 함께 나아가겠습니다.
          </p>
        </div>
      </div>
      <hr>
      <div class="introduction-area">
        <p class="position"><span>M</span>ember - Frontend</p>
        <div class="introduction-main">
          <div class="picture-area">
            <img class="team-picture" src="../assets/img/yusejeong.jpg" alt="yusejeong">
            <p class="name">YuSejeong</p>
          </div>
          <p class="member-introduction">
            모든것에 불만이 많은 개발자 유세정입니다.<br>
            이 모든 불편함과 불만을 해결하는 개발자가 되겠습니다.
          </p>
        </div>
      </div>
      <hr>
      <div class="top-button" @click.prevent="scrollToTop">
        <p>Top</p>
        <i class="fas fa-chevron-up"></i>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Intro',
  methods: {
    scrollToTop() {
      window.scroll({
        behavior: 'smooth',
        top: 0
      })
    },
    moveToHome() {
      this.$router.push('/home')
    }
  }
}
</script>

<style scoped>
hr {
  width: 80%;
  margin: 30px auto;
}

.intro {
  padding: 0;
  margin: 0 auto;
  width: 85%;
}

.first-area {
  position: relative;
  background-image: url('../assets/img/MAATKKUN-nobackground.png');
  background-repeat: no-repeat;
  background-position: center;
  height: 630px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.first-area::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.7);
}

.introduction {
  z-index: 1;
  font-size: 70px;
  text-shadow: 3px 5px 5px #fff;
}

.go-main {
  z-index: 1;
  position: absolute;
  bottom: 2rem;
  right: 2rem;
  font-size: 20px;
  cursor: pointer;
}

.go-main .arrow {
  margin-left: 10px;
}

.go-main:hover {
  color: #fff;
  text-shadow: 3px 3px 3px #000;
}

.second-area {
  height: 800px;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  background-color: rgba(36, 35, 38, 0.05);
}

.open-blog {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #fff;
  border: 1px solid #000;
  width: 350px;
  height: 350px;
  box-shadow: 20px 20px 20px rgba(0, 0, 0, 0.3);
}

.open-blog-title {
  font-size: 50px;
  margin-bottom: 1rem;
}

.open-blog-content {
  text-align: center;
  font-size: 20px;
}

.open-blog-content span {
  color: var(--fourth-color);
  font-size: 23px;
}

.third-area {
  height: 1400px;
}

.team-name {
  margin: 120px 0 80px 0;
  font-size: 30px;
  text-align: center;
}

.team-name span {
  color: var(--primary-color);
  font-size: 35px;
}

.introduction-area {
  width: 80%;
  margin: 0 auto;
}

.position {
  margin: 0 0 30px 40px;
  font-size: 18px;
}

.position span {
  font-size: 25px;
  color: var(--primary-color);
}

.introduction-main {
  display: flex;
  align-items: center;
}

.team-picture {
  background-image: url('../assets/img/leeownho.jpg');
  background-size: cover;
  width: 250px;
  height: 250px;
  border: 2px solid #000;
  border-radius: 50%;
  margin-left: 100px;
}

.name {
  width: 250px;
  text-align: center;
  margin-top: 1rem;
  margin-left: 100px;
}

.member-introduction {
  margin-left: 3rem;
  font-size: 20px;
}

.top-button {
  cursor: pointer;
  position: fixed;
  display: flex;
  flex-direction: column;
  align-items: center;
  left: 50%;
  bottom: 0;
}
</style>