<template>
  <div>
    <router-link :to="{ name: constants.URL_TYPE.POST.DETAIL, params: { id: article.postId } }">
      <img
        :src="article.content"
        :style="{backgroundImage:'url(https://www.ipcc.ch/site/assets/uploads/sites/3/2019/10/img-placeholder.png)'}"
        class="post-img"
      />

      <div class="contents">
        <h3>{{ article.title }}</h3>
        <br>
        <span class="date"><i class="far fa-user"></i> {{ article.nickname }}</span>
        <br>
        <span class="date"><i class="far fa-clock"></i> {{ article.createDate }}</span>
        <br>
        <span class="date"><i class="far fa-eye"></i> {{ article.hits }}</span>
        <br>
        <span class="date"><i class="fas fa-star" style="color: #F2E205;"></i> {{ article.starpoint }}</span>
        <br>
        <div class="tag" v-for="(tag, index) in article.hashtag.split(',')" :key="`hash_${index}`">
          <span class="tag-btn"># {{ tag }}</span>
        </div>
      </div>
    </router-link>
  </div>
</template>

<script>
import '@/assets/css/post.css'
import constants from '@/lib/constants'


export default {
  name: 'Card',
  components: {
  },
  props: {
    article: [Object, Array]
  },
  data() {
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL
    }
  },
  computed: {
  },
  methods: {
  },
  created() {
  },
  mounted() {
  },
}
</script>

<style>

</style>