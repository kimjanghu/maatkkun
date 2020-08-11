<template>
  <div>
    <div v-for="(post, index) in userList" :key="`post_${post.postId}`">
      <br>
      <p class="user-post"><router-link class="user-post-hover" :to="{ name: constants.URL_TYPE.POST.DETAIL, params:{ id: post.postId }}">{{ post.title }}</router-link></p>
      <p class="user-post-name"><i class="far fa-user fa-lg" style="margin-right: 5px;"></i>{{ post.nickname }}</p>
      <br>
      <div class="heart">
        <div class="tag" v-for="(tag, index) in post.hashtag.split(',')" :key="`hash_${index}`">
          <p class="tag-btn">#{{ tag }}</p>
        </div>
        <i class="fas fa-heart fa-lg redheart" style="color: red;"></i><p style="margin-left: 5px;">{{ post.likes }}</p>
      </div>
      <hr v-if="index+1 < userList.length" style="border: 1px solid var(--third-color)">
    </div>
    <div v-if="!userList">
      <p class="empty-text">게시글이 없습니다.</p>
    </div>
  </div>
</template>

<script>
import constants from '@/lib/constants'

export default {
  name: 'UserList',
  props: {
    userList: {
      type: Array
    }
  },
  data() {
    return {
      constants
    }
  }
}
</script>

<style scoped>
.user-post {
  text-align: left;
  font-size: 30px;
  margin: 1rem 0 0 2rem;
}

.user-post-hover:hover {
  color: var(--fourth-color);
}

.user-post-name {
  font-size: 13px;
  text-align: left;
  margin: 1rem 0 0 2rem;
}

.heart {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin: 0 2rem 1rem 0;
}

.tag {
  display: flex;
}

.tag-btn {
  width: 65px;
  margin-right: 10px;
  background-color: var(--fifth-color);
  text-align: center;
  border-radius: 3px;
  color: #fff;
}

.empty-text {
  margin-top: 2rem;
}
</style>