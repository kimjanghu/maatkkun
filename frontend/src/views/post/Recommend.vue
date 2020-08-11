<template>
    <div class="main-wrapper">

        <div v-if="isSeveral" id="mapmap" style="width:100%;height:350px;"></div>


        <div class="map_act_btn_wrap clear_box"></div>
        <p id="result"></p>

        <div v-if="!isSeveral" id="map" style="width:100%;height:350px;"></div>

        <br />
        <div class="post">
            <div class="wrapB">
                <section class="post-list">
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

        <button class="create_button" style="margin-top:2px;" @click="recommendToMe();initTmap();">다른
            메뉴를 추천해주세요!</button>
        <br>
        <br>
        <hr>
        <br>
        <p>피드백을 주세요</p>

        <button class="create_button" style="margin-right:2px;">평점이 너무 낮아요!</button>
        <button class="create_button" style="margin-right:2px;">조회수가 너무 낮아요!</button>
        <button class="create_button" style="margin-right:2px;">좋아요가 너무 낮아요!</button>
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
                    passList: "126.987319,37.565778_126.983072,37.573028",
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
                wantRecommend: this.$route.params.wantRecommend,
                isSeveral: false,




            }
        },

        methods: {
            ...mapActions(['changeMain']),
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
                axios.post(`${this.SERVER_URL}/articles/getRecommentList/`, {
                        "wantRecommend": this.wantRecommend
                    })
                    .then((res) => {
                        console.log(res)
                        this.recommendList = res.data

                    })
                    .catch(err => console.log(err))

            },

            fn_spread(id) {
                var getID = document.getElementById(id);
                getID.style.display = (getID.style.display == 'block') ? 'none' : 'block';
            },
            search() {
                var querystring = Object.entries(this.mapdata).map(e => e.join('=')).join('&')

                axios.post(
                        `https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json&callback=result&${querystring}`
                    )
                    .then((res) => {
                        
                        var resultData = res.data.features;
                        // console.log(resultData)  


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

                    })
                    .catch(err => console.log(err))

            },
            initTmap() {
                var container = document.getElementById('mapmap')
                console.log(container.innerHTML)
                container.innerHTML= null;
               
                this.tmap = new Tmapv2.Map("mapmap", {
                    center: new Tmapv2.LatLng(37.50350600000003, 127.026588),
                    zoom: 15,
                    zoomControl: true,
                    scrollwheel: true
                });
               
                if (Object.keys(this.recommendList).length == 2 && this.recommendList[Object.keys(this.recommendList)[
                        0]].lat && this.recommendList[Object.keys(this.recommendList)[0]].lon && this.recommendList[
                        Object.keys(this.recommendList)[1]].lat && this.recommendList[Object.keys(this.recommendList)[
                        1]].lon) {
    
                    this.marker_s = new Tmapv2.Marker({

                        position: new Tmapv2.LatLng(this.mapdata.startY,
                            this.mapdata.startX),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_s.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });
                    this.marker_e = new Tmapv2.Marker({

                        position: new Tmapv2.LatLng(this.mapdata.endY,
                            this.mapdata.endX),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_e.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });

                } else if (Object.keys(this.recommendList).length == 3) {
                    this.marker_s = new Tmapv2.Marker({

                        position: new Tmapv2.LatLng(this.recommendList[Object.keys(this.recommendList)[0]].lon,
                            this.recommendList[Object.keys(this.recommendList)[0]].lat),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_s.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });
                    this.marker_e = new Tmapv2.Marker({

                        position: new Tmapv2.LatLng(this.recommendList[Object.keys(this.recommendList)[2]].lon,
                            this.recommendList[Object.keys(this.recommendList)[2]].lat),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_e.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });
                    this.marker_p1 = new Tmapv2.Marker({
                        position: new Tmapv2.LatLng(this.recommendList[Object.keys(this.recommendList)[1]].lon,
                            this.recommendList[Object.keys(this.recommendList)[1]].lat),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });


                } else {
                    this.marker_s = new Tmapv2.Marker({

                        position: new Tmapv2.LatLng(37.5024399, 127.0260036),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_s.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });
                    this.marker_e = new Tmapv2.Marker({

                        position: new Tmapv2.LatLng(37.5004042, 127.0313759),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_e.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });
                    this.marker_p1 = new Tmapv2.Marker({
                        position: new Tmapv2.LatLng(37.5004038, 127.0248098),
                        icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png",
                        iconSize: new Tmapv2.Size(24, 38),
                        map: this.tmap
                    });


                }
                this.search();





            },
            select() {
                if (Object.keys(this.recommendList).length == 1 && this.recommendList[Object.keys(this.recommendList)[
                        0]].lon && this.recommendList[Object.keys(this.recommendList)[0]].lat) {
                    this.mapdata.startX = this.recommendList[Object.keys(this.recommendList)[0]].lon;
                    this.mapdata.startY = this.recommendList[Object.keys(this.recommendList)[0]].lat;
                } else if (Object.keys(this.recommendList).length == 2 && this.recommendList[Object.keys(this
                        .recommendList)[0]].lon && this.recommendList[Object.keys(this.recommendList)[0]].lat && this
                    .recommendList[Object.keys(this.recommendList)[1]].lon && this.recommendList[Object.keys(this
                        .recommendList)[1]].lat) {
                    this.mapdata.startX = this.recommendList[Object.keys(this.recommendList)[0]].lon;
                    this.mapdata.startY = this.recommendList[Object.keys(this.recommendList)[0]].lat;
                    this.mapdata.endX = this.recommendList[Object.keys(this.recommendList)[1]].lon;
                    this.mapdata.endY = this.recommendList[Object.keys(this.recommendList)[1]].lat;

                } else if (Object.keys(this.recommendList).length == 3) {
                    this.mapdata.startX = this.recommendList[Object.keys(this.recommendList)[0]].lon;
                    this.mapdata.startY = this.recommendList[Object.keys(this.recommendList)[0]].lat;
                    this.mapdata.passList = this.recommendList[Object.keys(this.recommendList)[1]].lon + this
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
</style>