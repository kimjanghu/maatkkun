<template>
    <div class="main-wrapper">
        <h2>어떤 종류의 음식점을 추천받고 싶나요?</h2>
        <button v-if="!isRestaurant" class="create_button" @click.prevent="fn_spread('hiddenContent03'); handClick();"
            style="margin-right:2px;">식사</button>
        <button v-if="isRestaurant" class="select_button" @click.prevent="fn_spread('hiddenContent03'); handClick();"
            style="margin-right:2px;">식사</button>
        <div id="hiddenContent03" class="example02" style="display: none; margin-top:20px;">
            <div class="checkbox-container" id="cook">

                <input type="checkbox" id="Korean" value="한식" v-model="hashtags">
                <label for="Korean">한식</label>

                <input type="checkbox" id="Chinese" value="중식" v-model="hashtags">
                <label for="Chinese">중식</label>

                <input type="checkbox" id="Western" value="양식" v-model="hashtags">
                <label for="Western">양식</label>

                <input type="checkbox" id="Japanese" value="일식" v-model="hashtags">
                <label for="Japanese">일식</label>

                <input type="checkbox" id="Bunsik" value="분식" v-model="hashtags">
                <label for="Bunsik">분식</label>


            </div>
        </div>
        <button v-if="!isCafe" class="create_button" @click.prevent="handCafe" style="margin-right:2px;">카페</button>
        <button v-if="isCafe" class="select_button" @click.prevent="handCafe" style="margin-right:2px;">카페</button>
        <button v-if="!isDrink" class="create_button" @click.prevent="handDrink" style="margin-right:2px;">술집</button>
        <button v-if="isDrink" class="select_button" @click.prevent="handDrink" style="margin-right:2px;">술집</button>
        <br>
        <br>
        <button class="create_button" style="margin-top:2px;" @click.prevent="recommendToMe">추천해주세요!</button>
    </div>

</template>

<script>
 import axios from 'axios'
    import { mapActions } from 'vuex'
    import '@/assets/css/checkbox.css'
    import constants from '@/lib/constants'
    export default {
        name:"Kind",
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

            }


        },
        methods: {
            ...mapActions(['changeMain']),
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
                axios.post(`${this.SERVER_URL}/articles/getRecommentList/`, {
                        "wantRecommend": this.wantRecommend
                    })
                    .then((res) => {
                        console.log(res)
                        this.$router.push({ name: constants.URL_TYPE.POST.RECOMMEND, params: {recommendList : res.data,wantRecommend:this.wantRecommend}})
                    })
                    .catch(err => console.log(err))

            },
            handClick() {
                this.isRestaurant = !this.isRestaurant
            },
            handCafe() {
                this.isCafe = !this.isCafe
            },
            handDrink() {
                this.isDrink = !this.isDrink
            },
            fn_spread(id) {
                var getID = document.getElementById(id);
                getID.style.display = (getID.style.display == 'block') ? 'none' : 'block';
            },

        },
          created() {
            this.changeMain(false)
        },
    }
</script>

<style scoped>
 .select_button {
        background-color: #FFB182;

        border: none;
        font-size: 1em;
        font-weight: 400;
        cursor: pointer;
        border: none;
        border-radius: 10px;
        color: #000000;
    }

</style>