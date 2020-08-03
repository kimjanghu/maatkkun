<template>
  <div>
    <h1 class="text-white">게시글 쓰기 페이지</h1>

    <div class="form-group d-flex">
      <label for="title">제목 </label>
      <input class="form-control" v-model="articleData.title" id="title" type="text" cols="10">
    </div>

    <div class="form-group d-flex">
      <label for="exampleFormControlSelect1">Category</label>
      <select class="form-control" id="exampleFormControlSelect1">
        <option>한식</option>
        <option>중식</option>
        <option>양식</option>
        <option>일식</option>
        <option>분식</option>
        <option>카페/디저트</option>
      </select>
    </div>

    <div class="form-group d-flex">d
      <label for="exampleFormControlTextarea1">내용 </label>
      <textarea class="form-control" id="exampleFormControlTextarea1" cols="50" rows="3"></textarea>
    </div>
    <div class="map_wrap">
      <div id="map" style="width:100%;height:100%;position:relative;overflow:hidden;"></div>

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
    <div id="restaurant" class="form-gruop d-flex">
      식당 {{placeDecision}}
    </div>

    <div class="form-group d-flex">
      <input type="file" id="file" @change="upload" class="inputfile"><label for="file" class="input-plus"></label>
      <div>
        <p>
          <img :src="newImgSrc">
        </p>
        <textarea placeholder="사진에 대한 설명을 적어주세요"></textarea>
        <button @click="upload">사진 저장</button>
        <button>사진 삭제</button>
      </div>
    </div>



    <div class="form-group d-flex">
      <Editor ref="toastuiEditor" />
      <button @click="createAction">저장하기</button>

    </div>
    <div>
      <button v-if="!isUpdate" @click="createArticle">작성하기</button>
      <button v-if="isUpdate" @click="updateArticle">수정하기</button>
      <button @click="detailArtic">상세목록 조회</button>
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


  const SERVER_URL = 'http://localhost:8080'
  export default {
    name: 'Create',
    components: {
      Editor,
    },
    data() {
      return {
        articleData: {
          title: null,
          content: null,
          created_at: null,

        },
        preArticleData: this.$route.params.articleData,
        newImgSrc: '',
        searchKeyword: "",
        markers: [],
        container: '',
        options: '',
        map: '',
        marker: '',
        ps: '',
        lat:"ss",
        lon:"dd",
        
        placeDecision: "Choose your restaurant",

      }
    },
    methods: {
      detailArtic(){
        axios.get(`${SERVER_URL}/articles/showArticle?postId=7`, {
          })
          .then(res => console.log(res))
          .catch(err => console.log(err));
      },
      createAction() {
          console.log("method : createAction");
          var content2= this.$refs.toastuiEditor.invoke('getHtml');
          
          axios.post(`${SERVER_URL}/photos/register`, {
              "src" : content2
          })
          .then(() => {
            
          })
          .catch(err => console.log(err));
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

        var keyword = document.getElementById('keyword').value;

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


          // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
          // LatLngBounds 객체에 좌표를 추가합니다
          bounds.extend(placePosition);
          var map = this.map;

          (function (marker, title, abc) {
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
            kakao.maps.event.addListener(marker, 'click', function (mouseEvent) {

              var a = confirm("이 식당이 맞습니까?")


              if (a) {
                console.log(abc.placeDecision)
                abc.placeDecision = title

                console.log(abc.lat)
                abc.lat=mouseEvent.latLng.getLat();
                console.log(abc.lat)
                abc.lot=mouseEvent.latlng.getLng();

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
                console.log(abc.placeDecision)
                abc.placeDecision = title

              }
            }

          })(marker, title, this);
          fragment.appendChild(itemEl);
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
        axios.post(SERVER_URL + '/articles/register/', this.articleData, config)
          .then(() => {
            this.$router.push({
              name: 'List'
            })
          })
          .catch(err => console.log(err.response))
      },
      updateArticle() {
        const config = {
          headers: {
            Authorization: `Token ${this.$cookies.get('auth-token')}`
          }
        }
        
        .put(SERVER_URL + '/articles/' + this.preArticleData.id + '/article_update_and_delete/', this.articleData,
            config)
          .then(res => console.log(res))
          .catch(err => console.log(err.response))
        this.$router.push({
          name: 'ArticleDetail',
          params: {
            articleData: this.articleData
          }
        })
      },
      upload(e) {
        let file = e.target.files
        let reader = new FileReader();
        reader.readAsDataURL(file[0]);
        reader.onload = e => {
          this.newImgSrc = e.target.result
        }
      }
    },
    computed: {
      isUpdate() {
        if (this.$route.params.articleData) {
          return true
        } else {
          return false
        }
      },
    },
    created() {
      // 수정하러 왔으면 이전 데이터로 덮어 씌움

      if (this.isUpdate) {
        this.articleData.id = this.preArticleData.id
        this.articleData.title = this.preArticleData.title
        this.articleData.content = this.preArticleData.content
        this.articleData.created_at = this.preArticleData.created_at
        this.articleData.article_comments = this.preArticleData.article_comments
        // this.articleData = this.preArticleData
      }

    },
    mounted() {
      if (window.kakao && window.kakao.maps) {

        this.initMap();
      } else {
        const script = document.createElement('script');
        /* global kakao */
        script.onload = () => kakao.maps.load(this.initMap);
        script.src = 'http://dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=caabdd0825a33e45f0ba6cdb6c0570af';
        document.head.appendChild(script);
      }
    }
  }
</script>

<style scoped>
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