<template> 
  <div class="post">
    <div class="wrapB">
      <section class="post-list">
        <div v-for="(article, index) in articles.list" :key="article.id">
          <div class="post-card">
            <Card :article="article" />
            <div class="writer-wrap">
              <div class="comment-like-wrap">
                <i class="far fa-comment-alt fa-lg"></i><p style="margin: 0 5px;">{{ articles.comment[index] }}</p>
              </div>
              <Like :article="article" :likedposts="likedposts" />
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import '@/assets/css/post.css'
import { mapGetters } from 'vuex'
import constants from '@/lib/constants'
import Like from '@/components/common/Like.vue'
import Card from '@/components/common/Card.vue'


export default {
  name: 'PostList',
  components: {
    Like,
    Card
  },
  props: {
    articles: [Array, Object],
    likedposts: {
      type: Array
    }
  },
  data(){
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL
    }
  },
  computed:{
    ...mapGetters('userStore', ['isLoggedIn'])
  },
  watch: {},
  methods: {
  },
  created() {
  },
  mounted(){
  },
  updated() {
  },
};
</script>

<style scoped>

</style>