import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import VueDaumPostCode from 'vue-daum-postcode'
// use package
import VueCookies from 'vue-cookies'
import PrettyCheckbox from 'pretty-checkbox-vue'
import cors from 'cors'
import VueMq from 'vue-mq'
import ajax from 'vuejs-ajax'
import VueMeta from 'vue-meta'

Vue.use(VueMeta)
Vue.use(VueCookies)
Vue.use(ajax)
Vue.use(cors)
Vue.use(VueDaumPostCode)
Vue.use(PrettyCheckbox)
Vue.use(VueMq, {
  breakpoints: { // default breakpoints - customize this
    sm: 576,
    md: 768,
    lg: 960,
    xl: Infinity,
  }
})

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
