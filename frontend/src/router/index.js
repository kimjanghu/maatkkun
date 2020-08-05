import Vue from 'vue'
import VueRouter from 'vue-router'

import constants from '@/lib/constants'

// 유저
import Join from '@/views/user/Join.vue'
import Login from '@/views/user/Login.vue'
import Logout from '@/views/user/Logout.vue'
import Mypage from '@/views/user/MyPage.vue'
import EditUserInfo from '@/views/user/EditUserInfo.vue'

//testPage
import testPage from '@/views/user/Test.vue'

// 포스트
import List from '@/views/post/List.vue'
import Create from '@/views/post/Create.vue'
import Detail from '@/views/post/Detail.vue'
import Temporary from '@/views/post/Temporary.vue'
import Recommend from '@/views/post/Recommend.vue'

// 에러
import Errorpage from '@/components/Error.vue'
import Notfound from '@/components/Notfound.vue'

Vue.use(VueRouter)

const routes = [
  // Main
  {
    path: '/',
    name: constants.URL_TYPE.POST.MAIN,
    component: List
  },
  // Signup
  {
    path: '/user/join',
    name: constants.URL_TYPE.USER.JOIN,
    component: Join
  },
  // Login
  { 
    path: '/user/login',
    name: constants.URL_TYPE.USER.LOGIN,
    component: Login
  },
  // Logout
  {
    path: '/user/logout',
    name: constants.URL_TYPE.USER.LOGOUT,
    component: Logout
  },
  // Edit user info
  {
    path: '/user/edit',
    name: constants.URL_TYPE.USER.EDIT,
    component: EditUserInfo
  },
  // My page
  {
    path: '/user/mypage/:id',
    name: constants.URL_TYPE.USER.MYPAGE,
    component: Mypage
  },
  // Create post
  {
    path: '/post/create',
    name: constants.URL_TYPE.POST.CREATE,
    component: Create
  },
  // Detail post
  {
    path: '/post/detail/:id',
    name: constants.URL_TYPE.POST.DETAIL,
    component: Detail
  },
  // Temporary post
  {
    path: '/post/temporary',
    name: constants.URL_TYPE.POST.TEMPORARY,
    component: Temporary
  },
  {
    path: '/post/recommend',
    name: constants.URL_TYPE.POST.RECOMMEND,
    component: Recommend
  },
  // My page
  {
    path: '/user/mypage',
    name: constants.URL_TYPE.USER.MYPAGE,
    component: Mypage
  },
  // Error
  {
    path: '/error',
    name: constants.ERROR.FRONT_ERROR,
    component: Errorpage
  },
  // Test page
  {
    path: '/user/test',
    name: constants.URL_TYPE.TEST,
    component: testPage
  },
  // Page not found
  {
    path: '*',
    name: constants.NOT_FOUND.PAGE_NOT_FOUND,
    component: Notfound
  },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router