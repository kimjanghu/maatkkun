<template>
  <div class="recommend-main">
    <h2>어떤 종류의 음식점을 추천받고 싶나요?</h2>
    <div class="kind-area-wrapper">
      <div class="kind-area" :class="{ active: isRestaurant }" @mouseleave="outSelectFood()" @mouseover="overSelectFood()" @click.prevent="handClick();">
        <i v-if="!selectFood" class="fas fa-utensils select-kind"></i>
        <div v-else>
          <div class="main-food">
            <div class="food-name korean" :class="{ active: isKorean }" @click="changeHashTag(`한식`)">한식</div>
            <div class="food-name chinese" :class="{ active: isChinese }" @click="changeHashTag(`중식`)">중식</div>
            <div class="food-name western" :class="{ active: isWestern }" @click="changeHashTag(`양식`)">양식</div>
            <div class="food-name japanese" :class="{ active: isJapanese }" @click="changeHashTag(`일식`)">일식</div>
            <div class="food-name snack" :class="{ active: isSnack }" @click="changeHashTag(`분식`)">분식</div>
          </div>
        </div>
      </div>
      <div class="kind-area" :class="{ active: isCafe }" @click.prevent="handCafe()">
        <i class="fas fa-coffee select-kind"></i>
      </div>
      <div class="kind-area" :class="{ active: isDrink }" @click.prevent="handDrink()">
        <i class="fas fa-wine-bottle select-kind"></i>
      </div>
      <button class="recommend-btn" style="margin-top:2px;" @click.prevent="recommendToMe">GO!</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import '@/assets/css/checkbox.css'
import constants from '@/lib/constants'


export default {
  name: 'Kind',
  data() {
    return {
      SERVER_URL: process.env.VUE_APP_API_URL,
      hashtags: [],
      wantRecommend: {
        food: '',
        isCafe: null,
        isDrink: null,
      },
      isRestaurant: false,
      isCafe: false,
      isDrink: false,
      selectFood: false,
      isKorean: false,
      isChinese: false,
      isWestern: false,
      isJapanese: false,
      isSnack: false
    }
  },
  methods: {
    changeHashTag(food) {
      if (this.hashtags.includes(food)) {
        const idx = this.hashtags.indexOf(food)
        this.hashtags.splice(idx, 1)
      } else {
        this.hashtags.push(food)
      }
      if (food === '한식') {
        this.isKorean = !this.isKorean
      } else if (food === '중식') {
        this.isChinese = !this.isChinese
      } else if (food === '양식') {
        this.isWestern = !this.isWestern
      } else if (food === '일식') {
        this.isJapanese = !this.isJapanese
      } else {
        this.isSnack = !this.isSnack
      }
    },
    recommendToMe() {
      if (this.isRestaurant) {
        this.wantRecommend.food = this.hashtags.join(",")
      } else {
        this.wantRecommend.food = null;
      }

      if (this.isCafe) {
        this.wantRecommend.isCafe = 1
      } else {
        this.wantRecommend.isCafe = null;
      }

      if (this.isDrink) {
        this.wantRecommend.isDrink = 1
      } else {
        this.wantRecommend.isDrink = null
      }
      axios.post(`${this.SERVER_URL}/articles/getRecommentList/`, { "wantRecommend": this.wantRecommend })
        .then((res) => {
          this.$router.push({ name: constants.URL_TYPE.POST.RECOMMEND, params: {recommendList : res.data,wantRecommend:this.wantRecommend}})
        })
        .catch(err => console.log(err))
    },
    handClick() {
      if (this.hashtags.length) {
        this.isRestaurant = true
      } else {
        this.isRestaurant = false
      }
    },
    handCafe() {
      this.isCafe = !this.isCafe
    },
    handDrink() {
      this.isDrink = !this.isDrink
    },
    overSelectFood() {
      this.selectFood = true
    },
    outSelectFood() {
      this.selectFood = false
    }
  },
  created() {
  },
}
</script>

<style scoped>
.recommend-main {
  width: 80%;
  margin: 0 auto;
}

.recommend-main h2 {
  margin: 2rem;
  text-align: center;
}

.kind-area-wrapper {
  position: relative;
  margin: 0 auto;
  width: 80%;
  display: flex;
  justify-content: center;
}

.kind-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 200px;
  height: 355px;
  border: 10px solid var(--primary-color);
  border-radius: 7px;
  margin: 30px;
  cursor: pointer;
}

.kind-area:hover {
  transition-duration: .5s;
  background-color: var(--primary-color);
  color: #fff;
}

.kind-area.active {
  background-color: var(--primary-color);
  color: #fff;
}

.select-kind {
  font-size: 4rem;
}

.select-kind:hover {
  font-size: 4rem;
}

.select-button {
  background-color: #F2E205;
  opacity: 0.3;
  border: none;
  font-size: 1em;
  font-weight: 400;
  cursor: pointer;
  border: none;
  border-radius: 10px;
  color: #000000;
}

.recommend-btn {
  position: absolute;
  bottom: -2rem;
  right: 0;
  border: none;
  color: #000;
}

.main-food {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}

.food-name {
  width: 180px;
  height: 67px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--secondary-color);
  background-color: #fff;
  border-bottom: 3px solid var(--primary-color);
}

.food-name.korean.active {
  background-color: var(--primary-color);
  color: #fff;
}

.food-name.korean.active {
  background-color: var(--primary-color);
  color: #fff;
}

.food-name.chinese.active {
  background-color: var(--primary-color);
  color: #fff;
}

.food-name.western.active {
  background-color: var(--primary-color);
  color: #fff;
}

.food-name.japanese.active {
  background-color: var(--primary-color);
  color: #fff;
}

.food-name.snack.active {
  background-color: var(--primary-color);
  color: #fff;
}
</style>