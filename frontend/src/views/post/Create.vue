<template>
  <div class="main-wrapper" :class="{ active: isTemporaryModal }" >
    <button class="create-btn" @click.prevent="changeTemoporaryModal(); getSubArticles();">임시저장 목록 가져오기</button>
    <br><br><br>
    <table class="table table-bordered">
      <tbody>
        <tr>
          <th><h3>제목</h3></th>
          <td><input style="padding: 10px 1rem;" type="text" v-model="articleData.title" placeholder="제목을 입력하세요. " name="subject" /></td>
        </tr>
      </tbody>
    </table>

    <br>

    <div v-show="isTemporaryModal" class="tmp-list">
      <div class="tmp-list-title">
        <i @click.prevent="changeTemoporaryModal()" class="fas fa-times fa-lg close"></i>
        <p >임시저장 목록</p>
      </div>
      <hr>
      <div class="tmp-list-content">
        <div v-for="subarticle in subarticles" :key="subarticle.id" @click="detailPage(subarticle)">
          <div v-if="+subarticle.userid === +articleData.userid" >
            <div class="tmp-list-info">
              <p class="tmp-list-info-title" @click="detailPage(subarticle)">{{ subarticle.title }}</p>
              <p>{{ subarticle.createDate }} <i @click="deleteTmpArticle(subarticle.postId)" style="margin-left: 10px;" class="far fa-trash-alt"></i></p>
            </div>
            <hr>
          </div>
        </div>
      </div>
    </div>

    <div>
      <Editor ref="toastuiEditor" :initialValue="articleData.content" initialEditType="wysiwyg"/>
    </div>

    <br>
    <button class="create-btn" @click.prevent="changeModal">
      음식점 위치가 어디인가요?
    </button>
    <br>
    <br>

    <div v-show="isModal" class="modal-container" id="modal">
      <div class="modal">
        <br>
        <h2 class="modaltitle">역삼동 음식점만 가능합니다!</h2>
        <br>
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
      {{articleData.address}}
    </div>
    <div id="restaurant_name" class="form-group d-flex mt-3">
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
      <input type="checkbox" id="Cafe" value="카페" v-model="hashtags">
      <label for="Cafe">카페</label>
      <input type="checkbox" id="Soju" value="술집" v-model="hashtags">
      <label for="Soju">술집</label>
    </div>

    <br>
    <h2>평점을 남겨주세요!</h2>
    <div class="star-point">
      <p class="star-title">맛</p>
      <star-rating v-model="articleData.taste" :increment="0.5" :max-rating="5" active-color="#F2E205"
        :rounded-corners="true" :star-size="20">
      </star-rating>
    </div>
    <div class="star-point">
      <p class="star-title">분위기</p>
      <star-rating v-model="articleData.atmosphere" :increment="0.5" :max-rating="5" active-color="#F2E205"
        :rounded-corners="true" :star-size="20">
      </star-rating>
    </div>
    <div class="star-point">
      <p class="star-title">가격</p>
      <star-rating v-model="articleData.price" :increment="0.5" :max-rating="5" active-color="#F2E205"
        :rounded-corners="true" :star-size="20">
      </star-rating>
    </div>
    <br><br>
    <div class="twobuttons">
      <button class="create-btn" style="margin-right:2px;" v-if="!isUpdate || isTemporary"
        @click="createArticle">작성하기</button>
      <button class="create-btn" v-if="!isUpdate && !isTemporary" @click="goTemporary">임시저장</button>
      <button class="create-btn" v-if="isUpdate" @click="updateArticle">수정하기</button>
    </div>

    <div v-show="isCreateLoading" class="create-loading">
      <Loading />
    </div>
  </div>

</template>

<script>
import axios from 'axios'
import { Editor } from '@toast-ui/vue-editor'
import SERVER from '@/api/drf'
import constants from '@/lib/constants'
import StarRating from 'vue-star-rating'
import Loading from '@/components/common/Loading.vue'
import '@/assets/css/modal.css'
import '@/assets/css/checkbox.css'
import 'codemirror/lib/codemirror.css';
import '@toast-ui/editor/dist/toastui-editor.css';


export default {
  name: 'Create',
  components: {
    Editor,
    StarRating,
    Loading
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
        userid: null,
        url: '',
        placename: '',
        price: 0,
        taste: 0,
        atmosphere: 0,
      },
      hashtags: [],
      preArticleData: this.$route.params.articleData,
      newImgSrc: '',
      searchKeyword: '',
      markers: [],
      container: '',
      options: '',
      map: '',
      marker: '',
      ps: '',
      isCreateLoading: false,
      placeDecision: "Choose your restaurant",
      isModal: false,
      isTemporaryModal: false,
      subarticles: [],
      coordinate: ''
    }
  },
  methods: {
    deleteTmpArticle(postId) {
      const check = confirm('임시저장 글을 삭제하시겠습니까?')
      if (check) {
        axios.delete(`${this.SERVER_URL}/subarticles/dropSubarticle?postId=${postId}`)
        .then(() => {
          this.$router.go(-1)
          alert('임시저장 글이 삭제되었습니다.')
        })
      } else {
        this.$router.go(0)
      }
    },
    detailPage(one) {
      axios.get(`${this.SERVER_URL}/subarticles/detail/${one.postId}`)
        .then((res) => {
          this.$router.push({
            name: constants.URL_TYPE.POST.TEMPORARY,
            params: {
              articleData: res.data
            }
          })
        })
        .catch(err => console.log(err))
    },
    getSubArticles() {
      const user = parseInt(this.$cookies.get('auth-token'))
      axios.get(`${this.SERVER_URL}/subarticles/list/${user}`)
        .then((res) => {
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
    createAction() {
      const content = this.$refs.toastuiEditor.invoke('getHtml');
      this.articleData.content = content;
    },
    createActionTwo() {
      const contentTwo = this.$refs.toastuiEditor.invoke('getHtml'); 
      this.articleData.content = contentTwo;
      document.getElementsByClassName('te-ok-button')[1].addEventListener('click',
        alert('png형식의 파일은 지원하지 않습니다.'))
      },
    initMap() {
      const container = document.getElementById('map')
      const mapOption = {
        center: new kakao.maps.LatLng(37.500649, 127.036530),
        level: 5
      }
      this.map = new kakao.maps.Map(container, mapOption) //마커추가하려면 객체를 아래와 같이 하나 만든다. 
      this.marker = new kakao.maps.Marker({
        position: this.map.getCenter()
      })
      this.marker.setMap(this.map);
      // 다각형을 구성하는 좌표 배열입니다. 이 좌표들을 이어서 다각형을 표시합니다
      const polygonPath = [
        new kakao.maps.LatLng(37.504432, 127.0222243),
        new kakao.maps.LatLng(37.510435, 127.043856),
        new kakao.maps.LatLng(37.496263, 127.052837),
        new kakao.maps.LatLng(37.489680, 127.031581),
      ]
      // 지도에 표시할 다각형을 생성합니다
      const polygon = new kakao.maps.Polygon({
        path: polygonPath, // 그려질 다각형의 좌표 배열입니다
        strokeWeight: 3, // 선의 두께입니다
        strokeColor: "#D96523", // 선의 색깔입니다
        strokeOpacity: 0.8, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
        strokeStyle: 'solid', // 선의 스타일입니다
        fillColor: '#fff', // 채우기 색깔입니다
        fillOpacity: 0.1 // 채우기 불투명도 입니다
      });
      // 지도에 다각형을 표시합니다
      polygon.setMap(this.map);
      this.ps = new kakao.maps.services.Places()
    },
    // 키워드 검색을 요청하는 함수입니다
    searchPlaces() {
      let keyword = "역삼동" + document.getElementById('keyword').value;
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
        // 정상적으로 검색이 완료됐으면 검색 목록과 마커를 표출합니다
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
        var lat = places[i].y;
        var lon = places[i].x;
        if (address.includes("역삼동")) {
          // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해 LatLngBounds 객체에 좌표를 추가합니다
          bounds.extend(placePosition);
          var map = this.map;
          (function (marker, title, abc, address, placeurl, lat, lon) {
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
                abc.articleData.lat = lat;
                abc.articleData.lon = lon;
                abc.articleData.address = address;
                abc.articleData.url = placeurl;
                abc.articleData.placename = title;
                abc.isModal = !abc.isModal;
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
                abc.articleData.lat = lat;
                abc.articleData.lon = lon;
                abc.articleData.address = address;
                abc.articleData.url = placeurl;
                abc.articleData.placename = title;
                abc.isModal = !abc.isModal;
              }
            }
          })(marker, title, this, address, placeurl, lat, lon);
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
        })
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
      this.createAction()
      if (!this.articleData.title) {
        alert('제목은 필수 항목입니다.')
      } else if (!this.articleData.content) {
        alert('내용을 입력해주세요')
      } else if (!this.articleData.lat || !this.articleData.lon) {
        alert('음식점 좌표는 필수 항목입니다. 지도에서 검색해주세요.')
      } else if (!this.articleData.taste) {
        alert('맛 별점을 체크해주세요.')
      } else if (!this.articleData.atmosphere) {
        alert('분위기 별점을 체크해주세요.')
      } else if (!this.articleData.price) {
        alert('가격 별점을 체크해주세요.')
      } else {
        const check = confirm('글을 제출하시겠습니까?')
        if (check) {
          this.isCreateLoading = true
          this.articleData.hashtag = this.hashtags.join(',')
          axios.post(this.SERVER_URL + '/articles/register/', this.articleData)
            .then(() => {
              if (this.isTemporary) {
                axios.delete(`${this.SERVER_URL}/subarticles/dropSubarticle?postId=${this.preArticleData.postId}`)
              }
              alert('작성이 완료되었습니다.')
              this.isCreateLoading = false
              window.document.location.href = '/home'
            })
            .catch(err => {
              alert('사진 용량을 줄여주세요.')
              this.isCreateLoading = false
              console.log(err.response)
            })
        }
      }
    },
    updateArticle() {
      this.createAction()
      if (!this.articleData.title) {
        alert('제목은 필수 항목입니다.')
      } else if (!this.articleData.content) {
        alert('내용을 입력해주세요.')
      } else if (!this.articleData.lat || !this.articleData.lon) {
        alert('음식점 좌표는 필수 항목입니다. 지도에서 검색해주세요.')
      } else if (!this.articleData.taste) {
        alert('맛 별점을 체크해주세요.')
      } else if (!this.articleData.atmosphere) {
        alert('분위기 별점을 체크해주세요.')
      } else if (!this.articleData.price) {
        alert('가격 별점을 체크해주세요.')
      } else {
        const check = confirm('글을 제출하시겠습니까?')
        if (check) {
          this.articleData.hashtag = this.hashtags.join(',')
          axios.put(`${this.SERVER_URL}${SERVER.ROUTES.update}`, this.articleData)
            .then(() => {
              this.$router.push({
                name: constants.URL_TYPE.POST.DETAIL,
                params: {
                  id: this.articleData.postId
                }
              })
            })
        }
      }
    },
    goTemporary() {
      this.articleData.hashtag = this.hashtags.join(",")
      this.createAction();
      axios.post(this.SERVER_URL + '/subarticles/register/', this.articleData)
        .then(() => {
          this.$router.push('/home')
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
    this.articleData.userid = this.$cookies.get('auth-token')
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
      this.articleData.price = this.preArticleData.price
      this.articleData.atmosphere = this.preArticleData.atmosphere
      this.articleData.taste = this.preArticleData.taste
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
      const modal = document.getElementById("modal")
      e.target === modal ? (this.isModal = false) : false
    })
  },
  updated() {
    if (this.isModal) {
      this.initMap();
      (function (abc) {
        kakao.maps.event.addListener(abc.map, 'center_changed', function () {
          // 지도의 중심좌표를 얻어옵니다 
          var latlng = abc.map.getCenter();
          if (latlng.getLat() >= 37.51 || latlng.getLat() <= 37.4 || latlng.getLng() <= 127.01 || latlng
            .getLng() >= 127.06) {
            alert("역삼동을 벗어났습니다.")
            abc.map.setCenter(new kakao.maps.LatLng(37.500649, 127.036530))
          }
        });
      })(this)
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

.main-wrapper.active {
  overflow: hidden;
  max-height: 90vh;
}

.create-btn {
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

.tmp-list {
  position: fixed;
  z-index: 1;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
  border: 1px solid #000;
  border-radius: 10px;
  width: 50%;
  height: 60%;
  overflow: auto;
}

.close {
  cursor: pointer;
  position: absolute;
  top: 20px;
  right: 20px;
}

.tmp-list-title {
  margin: 1rem 0;
}

.tmp-list-title p {
  text-align: center;
  font-size: 20px;
}

.tmp-list hr {
  border: 1px solid rgba(0, 0, 0, 0.4);
  width: 97%;
  margin: 0 auto;
}

.tmp-list-content div hr {
  border: 1px solid rgba(0, 0, 0, 0.2);
  width: 95%;
  margin: 0 auto;
}

.tmp-list-info {
  margin: 13px 1rem;
  display: flex;
  justify-content: space-between;
}

.tmp-list-info p {
  margin: 0 2rem;
}

.tmp-list-info-title:hover {
  cursor: pointer;
  color: var(--primary-color);
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
}

#mainWrapper>table {
  font: 100%;
  width: 100%;

  border-collapse: collapse;
}

#demo-table>caption {
  text-align: center;
  font-weight: bold;
  font-size: 150%;
  padding-bottom: 10px;
  border-bottom: .1em solid #000;
  margin-top: 10px;

}

#demo-table thead {
  background-color: #FFD732;
}
/* basic shared rules */
#demo-table th,
#demo-table td {
  text-align: center;
  padding-right: .5em;
}

#demo-table tr {
  border: 1px solid black;
  height: 30px;
}

#demo-table th {
  font-weight: bold;
  padding-left: .5em;
}

.modaltitle {
  text-align: center;

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

.star-point {
  display: flex; 
  align-items:center;
}

.star-title {
  padding-top: 7px;
  margin-right: 5px;
}

.create-loading {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@media(min-width:560px) {
  .main-wrapper {
    position: relative;
    align-items: center;
    width: 60%;
    margin: 0 auto;
  }
}
</style>