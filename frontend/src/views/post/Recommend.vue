<template>
    <div class="main-wrapper">

        <div v-if="isSeveral" id="mapmap" style="width:100%;height:350px;"></div>


        <div class="map_act_btn_wrap clear_box"></div>
        <p id="result"></p>

        <div v-if="!isSeveral" id="map" style="width:100%;height:350px;"></div>

        <br />
        <div class="post">
            <div class="wrapB">
                <section class="recommend-list">
                    <div v-for="recommend in recommendList" :key="recommend.id">
                        <div class="post-card"> 
                            <a @click="detailPage(recommend.postId)">
                                <img :src="recommend.content"
                                    :style="{backgroundImage:'url(https://www.ipcc.ch/site/assets/uploads/sites/3/2019/10/img-placeholder.png)'}"
                                    class="post-img" />
                                <div class="contents">
                                    <h3>{{recommend.title}}</h3>
                                    <br>
                                    <span class="date"><i class="far fa-user"></i> {{recommend.nickname}}</span>
                                    <br>
                                    <span class="date"><i class="far fa-clock"></i> {{recommend.createDate}}</span>
                                    <br>
                                    <span class="date"><i class="far fa-eye"></i> {{ recommend.hits }}</span>
                                    <br>
                                    <span class="date"><i class="fas fa-star"></i> {{ recommend.starpoint }}</span>
                                    <br>
                                    <div class="tag" v-for="(tag, index) in recommend.hashtag.split(',')"
                                        :key="`hash_${index}`">
                                        <span class="tag-btn"># {{ tag }}</span>
                                    </div>
                                </div>
                            </a>
                            <div class="writer-wrap">

                                <div class="comment-like-wrap">
                                    <i class="fas fa-heart fa-lg redheart" style="color: red;"></i>
                                    <p style="margin-left: 5px;">{{ recommend.likes }} likes</p>
                                </div>
                            </div>
                        </div>

                    </div>
                </section>
            </div>
        </div>

        <button class="create_button" style="margin-top:2px;" @click.prevent="recommendToMe();initTmap();">다른
            메뉴를 추천해주세요!</button>
        <br>
        <br>
        <hr>
        <br>
        <p>피드백을 주세요</p>

        <button v-if="!lowStar" class="create_button" style="margin-right:2px;" @click="handStar(); recommendToMe();initTmap();">별점이 너무 낮아요!</button>
        <button v-if="lowStar" class="select_button" @click.prevent="handStar();" style="margin-right:2px;">별점 피드백 적용 완료!</button>
        <button v-if="!lowWatch" class="create_button" style="margin-right:2px;" @click="handWatch(); recommendToMe(); initTmap();">조회수가 너무 낮아요!</button>
        <button v-if="lowWatch" class="select_button" @click.prevent="handWatch();" style="margin-right:2px;">조회수 피드백 적용 완료!</button>
        <button v-if="!lowLike" class="create_button" style="margin-right:2px;" @click="handLike(); recommendToMe(); initTmap();">좋아요가 너무 낮아요!</button>
        <button v-if="lowLike" class="select_button" @click.prevent="handLike();" style="margin-right:2px;">좋아요 피드백 적용 완료!</button>
        <br>
        <button class="create_button" style="margin-top:10px;" @click="fn_spread('hiddenContent02');">가는 길을 더 자세하게 알고
            싶어요!</button>
        <div id="hiddenContent02" class="example01" style="display: none; margin-top:20px;">
            <ul v-for="direction in directions" :key="direction.id">
                <li style='text-align:left;'>
                    {{direction}}
                </li>
            </ul>
        </div>




    </div>

</template>


<script>
    import '@/assets/css/post.css'
    import axios from 'axios'
    import jQuery from 'jquery'
    import {
        mapActions
    } from 'vuex'
    import '@/assets/css/checkbox.css'
    import constants from '@/lib/constants'

    export default {
        name: "Recommend",
        data() {
            return {
                SERVER_URL: process.env.VUE_APP_API_URL,
                tmap: '',
                map: '',
                marker_s: '',
                marker_e: '',
                marker_p1: '',
                marker_p2: '',
                totalMarkerArr: [],
                drawInfoArr: [],
                resultdrawArr: [],
                mapdata: {
                    appKey: "l7xx85f17a9a757349b59a2d9eb9d1382cb3",
                    startX: "127.026588",
                    startY: "37.50350600000003",
                    endX: "127.026588",
                    endY: "37.50350600000003",
                    passList: "126.987319,37.565778",
                    reqCoordType: "WGS84GEO",
                    resCoordType: "EPSG3857",

                    startName: "출발지",
                    endName: "도착지",

                },
                polyline_: '',
                directions: [],
                listnumber: 0,

                isRestaurant: false,
                isCafe: false,
                isDrink: false,
                recommendList: this.$route.params.recommendList,

                wantRecommend: {
                    food : this.$route.params.wantRecommend.food,
                    isCafe : this.$route.params.wantRecommend.isCafe,
                    isDrink: this.$route.params.wantRecommend.isDrink,
                    like: null,
                    watch:null,
                    star:null,
                    }
                    ,
                isSeveral: false,

                lowStar:false,
                lowWatch:false,
                lowLike:false,




            }
        },

        methods: {
            ...mapActions(['changeMain']),
            handLike(){
                this.lowLike=!this.lowLike
                if(!this.lowLike){
                    this.wantRecommend.like =null
                }
            },
            handWatch(){
                 this.lowWatch=!this.lowWatch
                 if(!this.lowWatch){
                     this.wantRecommend.watch=null
                 }
            },
            handStar(){
                this.lowStar=!this.lowStar
                if(!this.lowStar){
                     this.wantRecommend.star=null
                 }

            },

            initMap() {
                var container = document.getElementById('map')
                var options = {
                    center: new kakao.maps.LatLng(33.450701, 126.570667),
                    level: 3
                }
                this.map = new kakao.maps.Map(container, options)
                this.geocoder = new kakao.maps.services.Geocoder();
                var address_name = this.recommendList[Object.keys(this.recommendList)[0]].address
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
            detailPage(articleId) {
                this.$router.push({
                    name: constants.URL_TYPE.POST.DETAIL,
                    params: {
                        id: articleId
                    }
                })
            },
            recommendToMe() {
                if(this.lowLike){
                    this.wantRecommend.like="low"
                }
               
                if(this.lowWatch){
                    this.wantRecommend.watch="low"
                }
                 
                if(this.lowStar){
                    this.wantRecommend.star="low"
                }
                
                if(this.lowStar || this.lowLike || this.lowWatch){
                     axios.post(`${this.SERVER_URL}/articles/getRecommentList/`, {
                        "wantRecommend": this.wantRecommend
                    })
                    .then((res) => {
                        console.log(res)
                        this.recommendList = res.data

                    })
                    .catch(err => console.log(err))

                }
                else{axios.post(`${this.SERVER_URL}/articles/getRecommentList/`, {
                        "wantRecommend": this.$route.params.wantRecommend
                    })
                    .then((res) => {
                        console.log(res)
                        this.recommendList = res.data

                    })
                    .catch(err => console.log(err))
                }

            },

            fn_spread(id) {
                var getID = document.getElementById(id);
                getID.style.display = (getID.style.display == 'block') ? 'none' : 'block';
            },
            search() {
                
                
            },
            initTmap() {
                this.select();
                this.directions=[];
                this.listnumber=0;
                var container = document.getElementById('mapmap')
                container.innerHTML= null;
               
                this.tmap = new Tmapv2.Map("mapmap", {
                    center: new Tmapv2.LatLng(37.5009039, 127.0338411),
                    zoom: 15,
                    zoomControl: true,
                    scrollwheel: true
                });
               
                    this.marker_s = new Tmapv2.Marker({

                        position: new Tmapv2.LatLng(this.mapdata.startY, this.mapdata.startX),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_s.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });
                    this.marker_e = new Tmapv2.Marker({

                        position: new Tmapv2.LatLng(this.mapdata.endY,this.mapdata.endX),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_e.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });
                    if(this.mapdata.passList){
                        var pass = this.mapdata.passList.split(',')
                        console.log(pass)
                        this.marker_p1 = new Tmapv2.Marker({
                        position: new Tmapv2.LatLng(pass[1],pass[0]),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });

                    }
                    var querystring = Object.entries(this.mapdata).map(e => e.join('=')).join('&')

                axios.post(
                        `https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json&callback=result&${querystring}`
                    )
                    .then((res) => {
                        
                        var resultData = res.data.features;
                        console.log(resultData)  


                        //결과 출력
                        var tDistance = "총 거리 : " +
                            ((resultData[0].properties.totalDistance) / 1000)
                            .toFixed(1) + "km,";
                        var tTime = " 총 시간 : " +
                            ((resultData[0].properties.totalTime) / 60)
                            .toFixed(0) + "분";


                        jQuery("#result").text(tDistance + tTime);
                        //기존 그려진 라인 & 마커가 있다면 초기화

                        if (this.resultdrawArr.length > 0) {
                            for (var i in this.resultdrawArr) {
                                this.resultdrawArr[i]
                                    .setMap(null);
                            }
                            this.resultdrawArr = [];
                        }

                        this.drawInfoArr = [];

                        for (var k in resultData) {
                            var geometry = resultData[k].geometry;
                            var properties = resultData[k].properties;
                            var direction = resultData[k].properties.description;



                            if (direction[0] !== ',' && !direction.includes(',')) {
                                this.listnumber += 1
                                this.directions.push(this.listnumber + ' : ' + direction)

                            }

                            if (geometry.type == "LineString") {
                                for (var j in geometry.coordinates) {
                                    // 경로들의 결과값(구간)들을 포인트 객체로 변환 
                                    var latlng = new Tmapv2.Point(
                                        geometry.coordinates[j][0],
                                        geometry.coordinates[j][1]);
                                    // 포인트 객체를 받아 좌표값으로 변환
                                    var convertPoint = new Tmapv2.Projection.convertEPSG3857ToWGS84GEO(
                                        latlng);
                                    // 포인트객체의 정보로 좌표값 변환 객체로 저장
                                    var convertChange = new Tmapv2.LatLng(
                                        convertPoint._lat,
                                        convertPoint._lng);
                                    // 배열에 담기
                                    this.drawInfoArr.push(convertChange);




                                }
                            } else {
                                var markerImg = "";
                                var pType = "";
                                var size;

                                if (properties.pointType == "S") { //출발지 마커
                                    markerImg =
                                        "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_s.png";
                                    pType = "S";
                                    size = new Tmapv2.Size(24, 38);
                                } else if (properties.pointType == "E") { //도착지 마커
                                    markerImg =
                                        "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_e.png";
                                    pType = "E";
                                    size = new Tmapv2.Size(24, 38);
                                } else { //각 포인트 마커
                                    markerImg = "http://topopen.tmap.co.kr/imgs/point.png";
                                    pType = "P";
                                    size = new Tmapv2.Size(8, 8);
                                }

                                // 경로들의 결과값들을 포인트 객체로 변환 
                                var latlon = new Tmapv2.Point(
                                    geometry.coordinates[0],
                                    geometry.coordinates[1]);

                                // 포인트 객체를 받아 좌표값으로 다시 변환
                                var convertPoint2 = new Tmapv2.Projection.convertEPSG3857ToWGS84GEO(latlon);

                                var routeInfoObj = {
                                    markerImage: markerImg,
                                    lng: convertPoint2._lng,
                                    lat: convertPoint2._lat,
                                    pointType: pType
                                };


                                // Marker 추가
                                new Tmapv2.Marker({
                                    position: new Tmapv2.LatLng(
                                        routeInfoObj.lat,
                                        routeInfoObj.lng),
                                    icon: routeInfoObj.markerImage,
                                    iconSize: size,
                                    map: this.tmap
                                });


                            }
                        } //for문 [E]

                        this.drawLine(this.drawInfoArr);
                        this.tmap.setCenter()

                    })
                    .catch(err => console.log(err))

                console.log(this.mapdata)





            },
            select() {
                if (Object.keys(this.recommendList).length == 2 && this.recommendList[Object.keys(this
                        .recommendList)[0]].lon && this.recommendList[Object.keys(this.recommendList)[0]].lat && this
                    .recommendList[Object.keys(this.recommendList)[1]].lon && this.recommendList[Object.keys(this
                        .recommendList)[1]].lat) {
                    this.mapdata.startX = this.recommendList[Object.keys(this.recommendList)[0]].lon;
                    this.mapdata.startY = this.recommendList[Object.keys(this.recommendList)[0]].lat;
                    this.mapdata.endX = this.recommendList[Object.keys(this.recommendList)[1]].lon;
                    this.mapdata.endY = this.recommendList[Object.keys(this.recommendList)[1]].lat;
                    this.mapdata.passList=""

                } else if (Object.keys(this.recommendList).length == 3) {
                    this.mapdata.startX = this.recommendList[Object.keys(this.recommendList)[0]].lon;
                    this.mapdata.startY = this.recommendList[Object.keys(this.recommendList)[0]].lat;
                    this.mapdata.passList = this.recommendList[Object.keys(this.recommendList)[1]].lon +','+ this
                        .recommendList[Object.keys(this.recommendList)[1]].lat;
                    this.mapdata.endX = this.recommendList[Object.keys(this.recommendList)[2]].lon;
                    this.mapdata.endY = this.recommendList[Object.keys(this.recommendList)[2]].lat;

                }

            },



            addComma(num) {
                var regexp = /\B(?=(\d{3})+(?!\d))/g;
                return num.toString().replace(regexp, ',');
            },
            drawLine(arrPoint) {

                var polyline_ = new Tmapv2.Polyline({
                    path: arrPoint,
                    strokeColor: "#DD0000",
                    strokeWeight: 6,
                    map: this.tmap
                });

                this.resultdrawArr.push(polyline_);


            }
        }

        ,
        created() {
            this.changeMain(false)
            if (Object.keys(this.recommendList).length >= 2) {
                this.isSeveral = true;
            } else {
                this.isSeveral = false;
            }
        },
        mounted() {
            this.select();
            if (Object.keys(this.recommendList).length >= 2) {
                this.isSeveral = true;
                if (window.Tmapv2 && window.Tmapv2.Map) {
                    this.initTmap();
                    // this.search();
                } else {
                    const script = document.createElement('script');
                    /* global Tmapv2 */
                    script.onload = () => Tmapv2.Map.load(this.initTmap);
                    script.src =
                        "https://apis.openapi.sk.com/tmap/js?version=1&format=javascript&appKey=l7xx85f17a9a757349b59a2d9eb9d1382cb3";
                    document.head.appendChild(script);
                }

            }
            else if (Object.keys(this.recommendList).length == 1) {
                this.initMap();
               
            }












        },
        updated() {
            console.log("data바뀜")
            if (Object.keys(this.recommendList).length == 1) {
                this.isSeveral = false;
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

            } else if (Object.keys(this.recommendList).length >= 2){
                this.select();
            }
       


        }
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
    .post .recomend-list {
    width: 100%;
    margin-top: 15px;
    align-items: center;
    display: grid;
    
    grid-gap: 30px;

  }
  .post .recommend-list>div .post-card {
  /* border: 2px solid var(--secondary-color); */
  margin:0 auto;
  margin-bottom:15px;
  border-radius: 10px;
  width: 80%;
  float: center;
}

.post .recommend-list>div .post-card:hover {
  box-shadow: 0 0 7px 3px var(--primary-color);
  transition: transform 0.3s ease-in;
  transform: translateY(-7px);
}

.post .recommend-list>div .post-card .post-img {
  width: 100%;
  float: left;
  border-radius: 10px 10px 0 0;
  background-position: center center;
  background-repeat: no-repeat;
  background-size: cover;
  /* border: 1px solid #000; */
  height: 200px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}

.post .recommend-list>div .post-card .contents {
  width: 100%;
  float: left;
  padding: 20px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  border: 1px solid #000;
  border-top: none;
}

.post .recommend-list>div .post-card .contents h3 {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  word-wrap: normal;
  font-size: 1em;
  font-weight: 600;
  height: 1.5em;
}

.post .recommend-list>div .post-card .contents p.content {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 0.813em;
  height: 3.5em;
}

.post .recommend-list>div .post-card .contents span {
  float: right;
  font-size: 0.813em;
  margin-top: 2px;
  margin-left: 5px;
}

.post .recommend-list>div .post-card .writer-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
  padding: 10px 20px;
  border: 1px solid #000;
  border-radius: 0 0 10px 10px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  border-top: none;
}

</style>