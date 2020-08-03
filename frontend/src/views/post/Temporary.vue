<template>
    <div class="main-wrapper">
        <div class="box-container">
            <div class="box" style="font-weight:bold;"> {{article.createDate}}</div>
            <div class="box" style="font-weight:bold;"> {{article.hashtag}}</div>


        </div>
        <br>
        <p style="font-weight:bold; margin-left:0.5rem;">제목 : {{article.title}}</p>
        <br>
        <div>


            <div style="margin-top:3px;">

                <p style="text-align:center;">
                    <Viewer v-if="content != null" :initialValue="content" style="border: 1px black solid" />
                </p>


            </div>
            <br>
            <div id="map" style="width:100%;height:350px;"></div>
            <br>
            <br>
            <Comment :article="article" />
            <div class="twobuttons">
                <button class="create_button" style="margin-right:2px;" @click.prevent="goupdateArticle()">임시저장글
                    수정</button>
                <button class="create_button" style="margin-right:2px;" @click.prevent="deleteArticle()">임시저장글
                    삭제</button>
                <button class="create_button" @click.prevent="goBack()">뒤로 가기</button>
            </div>
        </div>
    </div>
</template>

<script>
    import "codemirror/lib/codemirror.css";
    import "@toast-ui/editor/dist/toastui-editor.css";
    import {
        Viewer
    } from "@toast-ui/vue-editor";
    import constants from '@/lib/constants'
    import axios from 'axios'
    // import SERVER from '@/api/drf'
    import Comment from '@/components/common/Comment.vue'


    export default {
        name: "Temporary",
        components: {
            Viewer,
            Comment
        },
        data() {
            return {
                SERVER_URL: process.env.VUE_APP_API_URL,
                content: null,
                loginUser: '',
                article: this.$route.params.articleData,
                map: '',
                marker: '',
                geocoder: '',
            }
        },
        methods: {
            goBack() {
                this.$router.push('/post/create')

            },
            initMap() {

                var container = document.getElementById('map')
                var options = {
                    center: new kakao.maps.LatLng(33.450701, 126.570667),
                    level: 3
                }
                this.map = new kakao.maps.Map(container, options)
                this.geocoder = new kakao.maps.services.Geocoder();
                var address_name = this.article.address

                this.geocoder.addressSearch(address_name, (result, status) => {

                    // 정상적으로 검색이 완료됐으면 
                    if (status === kakao.maps.services.Status.OK) {
                        console.log(address_name)

                        var coords = new kakao.maps.LatLng(result[0].y, result[0].x);


                        // 결과값으로 받은 위치를 마커로 표시합니다
                        var marker = new kakao.maps.Marker({
                            map: this.map,
                            position: coords
                        });
                        var title = address_name;

                        // 인포윈도우로 장소에 대한 설명을 표시합니다
                        var infowindow = new kakao.maps.InfoWindow({
                            content: '<div style="width:150px;text-align:center;padding:6px 0;">' +
                                title + '</div>'
                        });
                        infowindow.open(this.map, marker);

                        // 지도의 중심을 결과값으로 받은 위치로 이동시킵니다
                        this.map.setCenter(coords);
                    }
                });

            },

            goupdateArticle() {

                if (this.loginUser === this.article.userid) {

                    this.$router.push({
                        name: constants.URL_TYPE.POST.CREATE,
                        params: {
                            articleData: this.article,
                            isTemporary: true,


                        }
                    })
                } else {
                    alert("해당 게시글 작성자만 게시글을 수정할 수 있습니다.")
                }

            },
            deleteArticle() {
                if (this.loginUser === this.article.userid) {
                    axios.delete(`${this.SERVER_URL}/subarticles/dropSubarticle?postId=${this.article.postId}`)
                        .then((res) => {
                            console.log(res)
                            this.$router.push({
                                name: constants.URL_TYPE.POST.MAIN
                            })

                        })


                } else {
                    alert("해당 게시글 작성자만 게시글을 삭제할 수 있습니다")
                }
            }

        },
        mounted() {
            this.content = this.article.content
            this.loginUser = parseInt(this.$cookies.get('auth-token'))

            if (window.kakao && window.kakao.maps) {
                this.initMap();


            } else {
                const script = document.createElement('script');
                /* global kakao */
                script.onload = () => kakao.maps.load(this.initMap);
                script.src =
                    'http://dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=caabdd0825a33e45f0ba6cdb6c0570af';
                document.head.appendChild(script);
            }



        },

    }
</script>
<style scoped>
    .main-wrapper {
        position: relative;
        align-items: center;
        width: 100%;
        margin: 0 auto;
    }

    @media(min-width:560px) {
        .main-wrapper {
            position: relative;
            align-items: center;
            width: 70%;
            margin: 0 auto;
        }
    }

    .box-container {
        display: flex;
        flex-direction: row;
        justify-content: space-between;

    }
</style>