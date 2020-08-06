<template>
  <div class="main-wrapper">
    <button class="create_button" @click.prevent="changeTemoporaryModal();getSubarticles()">임시저장 목록 가져오기</button>
    <br><br><br>
    <table class="table table-bordered">
      <tbody>
        <tr>
          <th>제목</th>
          <td><input type="text" v-model="articleData.title" placeholder="제목을 입력하세요. " name="subject" /></td>
        </tr>


      </tbody>

    </table>
    <div v-show="isTemporaryModal" class="modal-container" id="temporaryModal">
      <div class="modal">
        <div class="modal" style="width:100%;height:100%;position:relative;overflow:hidden;"></div>
        <div id="mainWrapper">

          <ul>
            <li>임시저장 목록 </li>
            <ul id="ulTable">
              <li>
                <ul>
                  <li>No</li>
                  <li>Title</li>
                  <li>작성일</li>
                  <li>작성자</li>


                </ul>
              </li>
              <li v-for="subarticle in subarticles" :key="subarticle.id">
                <ul @click="detailPage(subarticle)">
                  <li>{{subarticle.id}}</li>
                  <li class="left">{{subarticle.title}}</li>
                  <li>{{subarticle.createDate}}</li>
                  <li>{{subarticle.userid}}</li>

                </ul>
              </li>

            </ul>
          </ul>


        </div>


      </div>
    </div>
    <br>

    <div>
      <Editor ref="toastuiEditor" :initialValue="articleData.content" />
    </div>

    <!-- <Map /> -->
<br>
    <button class="create_button" @click.prevent="changeModal">
      음식점 위치가 어디인가요?
    </button>
    <br>
    <br>


    <div v-show="isModal" class="modal-container" id="modal">
      <div class="modal">
        <div class="map_wrap">
          <div id="map" class="modal" style="width:100%;height:100%;position:relative;overflow:hidden;"></div>
          <div id="menu_wrap" class="bg_white">
            <div class="option">
              <div>
                <form @submit.prevent="searchPlaces(); return false;">
                  키워드 : <input type="text" v-model="searchKeyword" id="keyword" size="15">
                  <button type="submit">검색하기</button>
                </form>
              </div>
            </div>
            <hr>
            <ul id="placesList"></ul>
            <div id="pagination"></div>
          </div>
        </div>
      </div>
    </div>


    <div id="restaurant" class="form-group d-flex mt-3">
      음식점 주소 <br>
      {{articleData.address}}
    </div>
    <div id="restaurant_name" class="form-group d-flex mt-3">
      음식점 이름 <br>
      {{articleData.placename}}
    </div>
    <br>
    <br>





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

      <input type="checkbox" id="Soju" value="술집" v-model="hashtags">
      <label for="Soju">술집</label>

      
    </div>
    <br>
    

    <br><br>
    <div class="twobuttons">
      <button class="create_button"  style="margin-right:2px;" v-if="!isUpdate || isTemporary" @click="createArticle">작성하기</button>
      <button class="create_button" v-if="!isUpdate && !isTemporary" @click="goTemporary">임시저장</button>
      <button class="create_button" v-if="isUpdate" @click="updateArticle">수정하기</button>
    </div>
  </div>

</template>

<script>
  import axios from 'axios'
  import 'codemirror/lib/codemirror.css';
  import '@toast-ui/editor/dist/toastui-editor.css';
  import {
    Editor
  } from '@toast-ui/vue-editor';
  import SERVER from '@/api/drf'
  import constants from '@/lib/constants'

  // import Map from '@/components/common/Map.vue'
  import '@/assets/css/modal.css'
  import '@/assets/css/checkbox.css'


  export default {
    name: 'Create',
    components: {
      Editor,
      // Map
    },
    data() {
      return {
        SERVER_URL: process.env.VUE_APP_API_URL,
        articleData: {
          title: null,
          content: null,
          address: '',
          hashtag: '',
          lat: '',
          lon: '',
          userid: this.$cookies.get('auth-token'),
          url:'',
          placename:'',




        },
        hashtags: [],

        preArticleData: this.$route.params.articleData,
        newImgSrc: '',
        searchKeyword: "",
        markers: [],
        container: '',
        options: '',
        map: '',
        marker: '',
        ps: '',

        placeDecision: "Choose your restaurant",
        isModal: false,
        isTemporaryModal: false,
        subarticles: [],

      }
    },
    methods: {
      detailPage(one) {
        axios.get(`${this.SERVER_URL}/subarticles/detail/${one.postId}`)
          .then((res) => {
            console.log(res)
            this.$router.push({
              name: constants.URL_TYPE.POST.TEMPORARY,
              params: {
                articleData: res.data
              }
            })
          })
          .catch(err => console.log(err))
      },
      getSubarticles() {
        const user = parseInt(this.$cookies.get('auth-token'))
        axios.get(`${this.SERVER_URL}/subarticles/list/${user}`)
          .then((res) => {
            console.log(res.data)
            this.subarticles = res.data
          })
          .catch(err => console.log(err))

      },
      changeTemoporaryModal() {
        this.isTemporaryModal = !this.isTemporaryModal
      },

      changeModal() {
        this.isModal = !this.isModal
      },

      getTemporary() {

      },


      createAction() {

        // var content = this.$refs.toastuiEditor.invoke('getMarkdown');
        var content2 = this.$refs.toastuiEditor.invoke('getHtml');
        // console.log(content)
        this.articleData.content = content2;

      },
      initMap() {
        var container = document.getElementById('map')
        var options = {
          center: new kakao.maps.LatLng(33.450701, 126.570667),
          level: 3
        }
        this.map = new kakao.maps.Map(container, options) //마커추가하려면 객체를 아래와 같이 하나 만든다. 
        this.marker = new kakao.maps.Marker({
          position: this.map.getCenter()
        })
        this.marker.setMap(this.map);
        this.ps = new kakao.maps.services.Places();

      },



      // 지도를 표시할 div 


      // 키워드 검색을 요청하는 함수입니다
      searchPlaces() {

        var keyword = "역삼동"+document.getElementById('keyword').value;

        if (!keyword.replace(/^\s+|\s+$/g, '')) {
          alert('키워드를 입력해주세요!');
          return false;
        }

        // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
        this.ps.keywordSearch(keyword, this.placesSearchCB);

      },

      // 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
      placesSearchCB(data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {
          console.log(data)


          // 정상적으로 검색이 완료됐으면
          // 검색 목록과 마커를 표출합니다
          this.displayPlaces(data);

          // 페이지 번호를 표출합니다
          this.displayPagination(pagination);

        } else if (status === kakao.maps.services.Status.ZERO_RESULT) {

          alert('검색 결과가 존재하지 않습니다.');
          return;

        } else if (status === kakao.maps.services.Status.ERROR) {

          alert('검색 결과 중 오류가 발생했습니다.');
          return;

        }
      },

      // 검색 결과 목록과 마커를 표출하는 함수입니다
      displayPlaces(places) {
        var listEl = document.getElementById('placesList'),
          menuEl = document.getElementById('menu_wrap'),
          fragment = document.createDocumentFragment(),
          bounds = new kakao.maps.LatLngBounds();


        // 검색 결과 목록에 추가된 항목들을 제거합니다
        this.removeAllChildNods(listEl);


        // 지도에 표시되고 있는 마커를 제거합니다
        this.removeMarker();

        for (var i = 0; i < places.length; i++) {

          // 마커를 생성하고 지도에 표시합니다
          var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
            marker = this.addMarker(placePosition, i),
            itemEl = this.getListItem(i, places[i]); // 검색 결과 항목 Element를 생성합니다
          var title = places[i].place_name;
          var address = places[i].address_name;
          var placeurl = places[i].place_url;

          var lat = places[i].x;
          var lon = places[i].y;
          // console.log(address.includes("역삼동"))

          if(address.includes("역삼동")){
            // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
          // LatLngBounds 객체에 좌표를 추가합니다
          bounds.extend(placePosition);
          var map = this.map;
          // console.log(places[i]);

          (function (marker, title, abc, address,placeurl,lat,lon) {
            var infowindow = new kakao.maps.InfoWindow({
              content: '<div style="width:150px;text-align:center;padding:6px 0;">' + title + '</div>',
              removable: true
            })
            kakao.maps.event.addListener(marker, 'mouseover', function () {
              infowindow.open(map, marker);
            });
            kakao.maps.event.addListener(marker, 'mouseout', function () {
              infowindow.close();
            });
            kakao.maps.event.addListener(marker, 'click', function () {
              var a = confirm("이 식당이 맞습니까?")
              if (a) {
                // abc.placeDecision = title
                abc.articleData.lat = lat;
                abc.articleData.lon = lon;
                abc.articleData.address = address;
                abc.articleData.url = placeurl;
                abc.articleData.placename = title;
                abc.isModal = !abc.isModal;
                console.log(abc.articleData.lat)
                
              }
            });

            itemEl.onmouseover = function () {
              infowindow.open(map, marker);
            }

            itemEl.onmouseout = function () {
              infowindow.close();
            };
            itemEl.onclick = function () {
              var a = confirm("이 식당이 맞습니까?")
              if (a) {
                // abc.articleData.address = title
                abc.articleData.lat = lat;
                abc.articleData.lon = lon;
                abc.articleData.address = address;
                abc.articleData.url = placeurl;
                abc.articleData.placename = title;
                abc.isModal = !abc.isModal;
              }
            }

          })(marker, title, this, address,placeurl,lat,lon);




          fragment.appendChild(itemEl);

          }
         


          
        }


        // 검색결과 항목들을 검색결과 목록 Elemnet에 추가합니다
        listEl.appendChild(fragment);
        menuEl.scrollTop = 0;

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
        this.map.setBounds(bounds);
      },

      // 검색결과 항목을 Element로 반환하는 함수입니다
      getListItem(index, places) {

        var el = document.createElement('li'),
          itemStr = '<span class="markerbg marker_' + (index + 1) + '"></span>' +
          '<div class="info">' +
          '   <h5>' + places.place_name + '</h5>';

        if (places.road_address_name) {
          itemStr += '    <span>' + places.road_address_name + '</span>' +
            '   <span class="jibun gray">' + places.address_name + '</span>';
        } else {
          itemStr += '    <span>' + places.address_name + '</span>';
        }

        itemStr += '  <span class="tel">' + places.phone + '</span>' +
          '</div>';

        el.innerHTML = itemStr;
        el.className = 'item';

        return el;
      },
      // 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
      addMarker(position, idx) {
        var imageSrc =
          'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
          imageSize = new kakao.maps.Size(36, 37), // 마커 이미지의 크기
          imgOptions = {
            spriteSize: new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
            spriteOrigin: new kakao.maps.Point(0, (idx * 46) + 10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
            offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
          },
          markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
          marker = new kakao.maps.Marker({
            position: position, // 마커의 위치
            image: markerImage
          });

        marker.setMap(this.map); // 지도 위에 마커를 표출합니다
        this.markers.push(marker); // 배열에 생성된 마커를 추가합니다

        return marker;
      },
      // 지도 위에 표시되고 있는 마커를 모두 제거합니다
      removeMarker() {
        for (var i = 0; i < this.markers.length; i++) {
          this.markers[i].setMap(null);
        }
        this.markers = [];
      },
      // 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
      displayPagination(pagination) {
        var paginationEl = document.getElementById('pagination'),
          fragment = document.createDocumentFragment(),
          i;



        // 기존에 추가된 페이지번호를 삭제합니다
        while (paginationEl.hasChildNodes()) {
          paginationEl.removeChild(paginationEl.lastChild);
        }

        for (i = 1; i <= pagination.last; i++) {
          var el = document.createElement('a');
          el.href = "#";
          el.innerHTML = i;

          if (i === pagination.current) {
            el.className = 'on';
          } else {
            el.onclick = (function (i) {
              return function () {
                pagination.gotoPage(i);
              }
            })(i);
          }

          fragment.appendChild(el);
        }
        paginationEl.appendChild(fragment);
      },

      // 검색결과 목록의 자식 Element를 제거하는 함수입니다
      removeAllChildNods(el) {
        while (el.hasChildNodes()) {
          el.removeChild(el.lastChild);
        }
      },
      createArticle() {
        const config = {
          headers: {
            Authorization: `Token ${this.$cookies.get('auth-token')}`
          }
        }
        this.articleData.hashtag = this.hashtags.join(",")
        this.createAction();
        axios.post(this.SERVER_URL + '/articles/register/', this.articleData, config)
          .then((res) => {
            console.log(res)
            if (this.isTemporary) {
              axios.delete(`${this.SERVER_URL}/subarticles/dropSubarticle?postId=${this.preArticleData.postId}`)
            }

            this.$router.push('/')
          })
          .catch(err => console.log(err.response))
      },
      updateArticle() {
        const config = {
          headers: {
            Authorization: `Token ${this.$cookies.get('auth-token')}`
          }
        }
        // console.log(this.articleData)
        this.articleData.hashtag = this.hashtags.join(",")
        this.createAction();
        axios.put(`${this.SERVER_URL}${SERVER.ROUTES.update}`, this.articleData, config)
          .then(() => {
            console.log(this.articleData.postId)
            this.$router.push({ name: constants.URL_TYPE.POST.DETAIL, params: { id: this.articleData.postId } })
          })
          .catch(err => console.log(err))



      },
      goTemporary() {
        const config = {
          headers: {
            Authorization: `Token ${this.$cookies.get('auth-token')}`
          }
        }
        this.articleData.hashtag = this.hashtags.join(",")
        this.createAction();
        axios.post(this.SERVER_URL + '/subarticles/register/', this.articleData, config)
          .then((res) => {
            console.log(res)
            this.$router.push('/')
          })
          .catch(err => console.log(err.response))

      }

    },
    computed: {
      isUpdate() {
        if (this.$route.params.articleData && !this.$route.params.isTemporary) {
          return true
        } else {
          return false
        }
      },
      isTemporary() {
        if (this.$route.params.isTemporary) {
          return true
        } else {
          return false
        }
      },
    },
    created() {
      // 수정하러 왔으면 이전 데이터로 덮어 씌움

      if (this.isUpdate) {
        this.articleData = this.preArticleData
        if (this.articleData.hashtag) {
          this.hashtags = this.articleData.hashtag.split(",")

        }

      }
      if (this.isTemporary) {
        this.articleData.title = this.preArticleData.title
        this.articleData.address = this.preArticleData.address
        this.articleData.content = this.preArticleData.content
        this.articleData.hashtag = this.preArticleData.hashtag
        this.articleData.lat = this.preArticleData.lat
        this.articleData.lon = this.preArticleData.lon
        this.articleData.likes = this.preArticleData.likes
        this.articleData.userid = this.preArticleData.userid
        this.articleData.url = this.preArticleData.url
        this.articleData.placename = this.preArticleData.placename
      }
    },


    mounted() {

      if (window.kakao && window.kakao.maps) {
        setTimeout(() => {
          this.initMap()
        }, 100)
      } else {
        const script = document.createElement('script');
        /* global kakao */
        script.onload = () => kakao.maps.load(this.initMap);
        script.src = 'http://dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=caabdd0825a33e45f0ba6cdb6c0570af';
        document.head.appendChild(script);
      }

      window.addEventListener("click", e => {
        const modal = document.getElementById("modal");
        e.target === modal ? (this.isModal = false) : false;
      });
      window.addEventListener("click", e => {
        const modal = document.getElementById("temporaryModal");
        e.target === modal ? (this.isTemporaryModal = false) : false;
      });
    },
    updated() {
      if (this.isModal) {
        this.map.relayout()
      }
    },
  }
</script>

<style>
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
    width: 60%;
    margin: 0 auto;
  }
}

.main-wrapper:after {
  content: "";
  display: block;
  padding-bottom: 100%;
}

.create_button {

  border: none;
  font-size: 1em;
  font-weight: 400;
  cursor: pointer;
  border: none;
  border-radius: 10px;
  color: #000000;
}

.twobuttons {
  text-align: center;
}

ul,
li {
  list-style: none;
  text-align: center;
  padding: 0;
  margin: 0;
}

#mainWrapper {

  margin: 0 auto;
  /*가운데 정렬*/
}

#mainWrapper>ul>li:first-child {
  text-align: center;
  font-size: 14pt;
  height: 30px;
  vertical-align: middle;
  line-height: 30px;
}

#ulTable>li:first-child>ul>li {
  background-color: #ffffff;
  font-weight: bold;
  text-align: center;
  border-top: 1px solid black;
  border-bottom: 1px solid black;
}

#ulTable>li>ul {
  clear: both;
  padding: 0px;
  position: relative;
  min-width: 40px;
}

#ulTable>li>ul>li {
  float: left;
  font-size: 10pt;
  border-bottom: 1px solid silver;
  vertical-align: baseline;
  width:100%;
}

#ulTable>li>ul>li:first-child {
  width: 10%;
}

#ulTable>li>ul>li:first-child+li {
  width: 30%;
}


#ulTable>li>ul>li:first-child+li+li {
  width: 30%;
}

/*작성일 열 크기*/
#ulTable>li>ul>li:first-child+li+li+li {
  width: 30%;
}

/*작성자 열 크기*/
#ulTable>li>ul>li:first-child+li+li+li+li {
  width: 10%;
}



.map_wrap,
.map_wrap * {
  margin: 0;
  padding: 0;
  font-family: 'Malgun Gothic', dotum, '돋움', sans-serif;
  font-size: 12px;
}

.map_wrap a,
.map_wrap a:hover,
.map_wrap a:active {
  color: #000;
  text-decoration: none;
}

.map_wrap {
  position: relative;
  width: 100%;
  height: 500px;
}

#menu_wrap {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  width: 250px;
  margin: 10px 0 30px 10px;
  padding: 5px;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.7);
  z-index: 1;
  font-size: 12px;
  border-radius: 10px;
}

.bg_white {
  background: #fff;
}

#menu_wrap hr {
  display: block;
  height: 1px;
  border: 0;
  border-top: 2px solid #5F5F5F;
  margin: 3px 0;
}

#menu_wrap .option {
  text-align: center;
}

#menu_wrap .option p {
  margin: 10px 0;
}

#menu_wrap .option button {
  margin-left: 5px;
}

#placesList li {
  list-style: none;
}

#placesList .item {
  position: relative;
  border-bottom: 1px solid #888;
  overflow: hidden;
  cursor: pointer;
  min-height: 65px;
}

#placesList .item span {
  display: block;
  margin-top: 4px;
}

#placesList .item h5,
#placesList .item .info {
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

#placesList .item .info {
  padding: 10px 0 10px 55px;
}

#placesList .info .gray {
  color: #8a8a8a;
}

#placesList .info .jibun {
  padding-left: 26px;
  background: url(https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/places_jibun.png) no-repeat;
}

#placesList .info .tel {
  color: #009900;
}

#placesList .item .markerbg {
  float: left;
  position: absolute;
  width: 36px;
  height: 37px;
  margin: 10px 0 0 10px;
  background: url(https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png) no-repeat;
}

#placesList .item .marker_1 {
  background-position: 0 -10px;
}

#placesList .item .marker_2 {
  background-position: 0 -56px;
}

#placesList .item .marker_3 {
  background-position: 0 -102px
}

#placesList .item .marker_4 {
  background-position: 0 -148px;
}

#placesList .item .marker_5 {
  background-position: 0 -194px;
}

#placesList .item .marker_6 {
  background-position: 0 -240px;
}

#placesList .item .marker_7 {
  background-position: 0 -286px;
}

#placesList .item .marker_8 {
  background-position: 0 -332px;
}

#placesList .item .marker_9 {
  background-position: 0 -378px;
}

#placesList .item .marker_10 {
  background-position: 0 -423px;
}

#placesList .item .marker_11 {
  background-position: 0 -470px;
}

#placesList .item .marker_12 {
  background-position: 0 -516px;
}

#placesList .item .marker_13 {
  background-position: 0 -562px;
}

#placesList .item .marker_14 {
  background-position: 0 -608px;
}

#placesList .item .marker_15 {
  background-position: 0 -654px;
}

#pagination {
  margin: 10px auto;
  text-align: center;
}

#pagination a {
  display: inline-block;
  margin-right: 10px;
}

#pagination .on {
  font-weight: bold;
  cursor: default;
  color: #777;
}
</style>