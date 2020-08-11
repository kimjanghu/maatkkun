<template>
  <div>
    <div v-if="isLoggedIn" class="post-like-wrap">
      <i ref="postId" v-if="included" @click="change(); checkLike(article);"
        class="fas fa-heart fa-lg animated delay-1s redheart" style="color: red;"></i>
      <i ref="postId" v-if="!included" @click="change(); checkLike(article);"
        class="far fa-heart fa-lg animated infinite bounce delay-1s blankheart" style="color: gray;"></i>
      <p style="margin-left: 5px;">{{ article.likes }} likes</p>
    </div>
    <div v-else class="post-like-wrap">
      <i class="fas fa-heart fa-lg redheart" style="color: red;"></i>
      <p style="margin-left: 5px;">{{ article.likes }} likes</p>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import axios from 'axios'

export default {
  name: 'Like',
  props: {
    article: {
      type: Object
    }
  },
  data() {
    return {
      SERVER_URL: process.env.VUE_APP_API_URL,
      included: true,
      likedposts: '',
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  methods: {
    includes() {
      if (this.article.postId in this.likedposts) {
        this.included = true;
      } else {
        this.included = false;
      }
    },
    change() {
      var cl = this.$refs["postId"].getAttribute('class');
      if (cl === "fas fa-heart fa-lg animated delay-1s" || cl === "fas fa-heart fa-lg animated delay-1s redheart") {
        this.$refs["postId"].setAttribute("class", "far fa-heart fa-lg animated infinite bounce delay-1s");
        this.$refs["postId"].setAttribute('style', "color:gray");
        this.article.likes -= 1

      } else {
          this.$refs["postId"].setAttribute("class", "fas fa-heart fa-lg animated delay-1s");
          this.$refs["postId"].setAttribute('style',"color:red");
        this.article.likes += 1
      }
    },
    checkLike(post) {
      post.userid = this.$cookies.get('auth-token')
      // console.log(post.userid)
      axios.post(`${this.SERVER_URL}/articles/like`, post)
        .then(() => {
          // console.log(res)
          axios.post(`${this.SERVER_URL}/accounts/userDetail`, {
              "uid": this.$cookies.get('auth-token')
            })
            .then((res) => {
              console.log(res)
            })
        })
    },
  },
  created() {
    if (this.$cookies.get('auth-token')) {
      axios.post(`${this.SERVER_URL}/accounts/userDetail`, {
        "uid": this.$cookies.get('auth-token')
      })
      .then((res) => {
        const liked_list = res.data.likedpost.split(',').map(i => parseInt(i))
        const result = liked_list.slice(0, -1)
        this.likedposts = result
        if (this.likedposts.includes(this.article.postId)) {
          this.included = true;
        } else {
          this.included = false;
        }
      })
    }
  }
}
</script>

<style scoped>
.post-like-wrap {
  margin: 1rem 0;
  display: flex;
  align-items: center;
}
</style>