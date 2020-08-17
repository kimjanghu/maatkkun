<template>
  <div>
    <div class="post-list-link">
      <div class="main-link" :class="{ active: listStatus.isRecentList }">
        <router-link :to="{ name: constants.URL_TYPE.POST.MAIN }"><i class="far fa-clock fa-lg post-icon"></i>최신순</router-link>
      </div>
      <div class="main-link" :class="{ active: listStatus.isLikeList }">
        <router-link :to="{ name: constants.URL_TYPE.POST.LIKE }"><i class="far fa-heart fa-lg post-icon"></i>좋아요</router-link>
      </div>
      <div class="main-link" :class="{ active: listStatus.isHitList }">
        <router-link :to="{ name: constants.URL_TYPE.POST.VIEWS }"><i class="fas fa-fire-alt fa-lg post-icon"></i>조회순</router-link>
      </div>
      <div class="main-link" :class="{ active: listStatus.isStarList }">
        <router-link :to="{ name: constants.URL_TYPE.POST.STAR }"><i class="far fa-star fa-lg post-icon"></i>별점순</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import constants from '@/lib/constants.js'

export default {
  name: 'PostNavbar',
  props: {
    listStatus: {
      type: Object
    }
  },
  data() {
    return {
      constants,
      isRecentList: false,
      isLikeList: true,
      isHitList: true,
      isStarList: true
    }
  },
}
</script>

<style scoped>
.post-list-link {
  margin-top: 1rem;
  display: flex;
}

.main-link {
  cursor: pointer;
  margin-top: 1rem;
  padding: 10px;
  border-bottom: 2px solid var(--secondary-color);
}

.main-link.active {
  border: none;
  opacity: 0.2;
}

.post-icon {
  margin-right: 5px;
}
</style>