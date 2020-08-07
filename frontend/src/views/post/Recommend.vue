<template>
    <div class="main-wrapper">
        <div id="map_wrap" class="map_wrap3">
            <div id="map_div"></div>
        </div>
        <div class="map_act_btn_wrap clear_box"></div>
        <p id="result"></p>
        <br />
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
        <button class="create_button" style="margin-top:2px;" @click="recommendToMe">추천해주세요!</button>
        <br>
        <hr>
        <br>
        <p>피드백을 주세요</p>
        
        <button class="create_button" style="margin-right:2px;">평점이 너무 낮아요!</button>
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
    import axios from 'axios'
    import jQuery from 'jquery'
    import { mapActions } from 'vuex'
    import '@/assets/css/checkbox.css'
    
    export default {
        name: "Recommend",
        data() {
            return {
                map: '',
                marker_s: '',
                marker_e: '',
                marker_p1: '',
                marker_p2: '',
                totalMarkerArr: [],
                drawInfoArr: [],
                resultdrawArr: [],
                recommend_list: [],
                mapdata: {
                    appKey: "l7xx85f17a9a757349b59a2d9eb9d1382cb3",
                    startX: "127.0260036",
                    startY: "37.5024399",
                    endX: "127.0313759",
                    endY: "37.5004042",
                    passList: "127.0248098,37.5004038",
                    reqCoordType: "WGS84GEO",
                    resCoordType: "EPSG3857",

                    startName: "출발지",
                    endName: "도착지",

                },
                polyline_: '',
                directions: [],
                listnumber: 0,
                hashtags: [],
                wantRecommend: {
                    food:'',
                    isCafe:null,
                    isDrink:null,
                    
                },
                isRestaurant: false,
                isCafe: false,
                isDrink: false,


            }
        },
        methods: {
            ...mapActions(['changeMain']),
            recommendToMe(){
                if(this.isRestaurant)
                {
                    this.wantRecommend.food = this.hashtags.join(",")

                }
                if(this.isCafe){
                    
                    this.wantRecommend.isCafe = 1

                }

                if(this.isDrink){
                    this.wantRecommend.isDrink = 1

                }

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
            initTmap() {
                this.map = new Tmapv2.Map('map_div', {
                    center: new Tmapv2.LatLng(37.49795, 127.0254483),
                    width: '100%',
                    height: '400px',
                    zoom: 15,
                    zoomControl: true,
                    scrollwheel: true
                });

                this.marker_s = new Tmapv2.Marker({

                    position: new Tmapv2.LatLng(37.5024399, 127.0260036),
                    icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_s.png",
                    iconSize: new Tmapv2.Size(24, 38),
                    map: this.map
                });
                this.marker_e = new Tmapv2.Marker({

                    position: new Tmapv2.LatLng(37.5004042, 127.0313759),
                    icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_e.png",
                    iconSize: new Tmapv2.Size(24, 38),
                    map: this.map
                });
                this.marker_p1 = new Tmapv2.Marker({
                    position: new Tmapv2.LatLng(37.5004038, 127.0248098),
                    icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png",
                    iconSize: new Tmapv2.Size(24, 38),
                    map: this.map
                });

                this.marker_p2 = new Tmapv2.Marker({
                    position: new Tmapv2.LatLng(37.565778, 126.987319),
                    icon: "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png",
                    iconSize: new Tmapv2.Size(24, 38),
                    map: this.map
                });

                var querystring = Object.entries(this.mapdata).map(e => e.join('=')).join('&')

                axios.post(
                        `https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json&callback=result&${querystring}`
                    )
                    .then((res) => {



                        var resultData = res.data.features;


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
                                    map: this.map
                                });


                            }
                        } //for문 [E]

                        this.drawLine(this.drawInfoArr);

                    })
                    .catch(err => console.log(err))

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
                    map: this.map
                });

                this.resultdrawArr.push(polyline_);


            }
        }

        ,
        created() {
            this.changeMain(false)
        },
        mounted() {
            if (window.Tmapv2 && window.Tmapv2.Map) {
                this.initTmap();
            } else {
                const script = document.createElement('script');
                /* global Tmapv2 */
                script.onload = () => Tmapv2.Map.load(this.initTmap);
                script.src =
                    "https://apis.openapi.sk.com/tmap/js?version=1&format=javascript&appKey=l7xx85f17a9a757349b59a2d9eb9d1382cb3";
                document.head.appendChild(script);
            }


        },
        updated() {

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