<template>
  <div>
    <br><h2>댓글</h2><br>
    <div class="comment" v-for="comment in comments" :key="`comment_${comment.commentid}`">
      <router-link
        class="comment-username" 
        :to="{ name:constants.URL_TYPE.USER.MYPAGE, params:{ id: comment.userid }}"
      >{{ comment.nickname }}</router-link>
      <p class="comment-time">{{ comment.createDate }}</p>

      <div v-if="checkComment.isComment && comment.commentid === checkComment.commentId">
        <CreateComment :checkComment="checkComment" @change-comment="updateComment"/>
      </div>
      <div v-else>
        <p class="comment-content">{{ comment.content }}</p>
      </div>
      
      <div class="rightbuttons" v-if="!checkComment.isComment && +commentForm.userid === comment.userid">
        <button class="create-button" style="margin-right:2px;" @click="changeIsComment(comment)">수정</button>
        <button class="create-button" @click="deleteComment(comment.commentid)">삭제</button>
      </div><br>
    </div>
    <hr><br>
    <CreateComment v-if="isLoggedIn" @submit-comment="createComment" />
    
  </div>
</template>

<script>
import constants from '@/lib/constants.js'
import CreateComment from './CreateComment.vue'
import axios from 'axios'
import { mapGetters } from 'vuex'

export default {
  name: 'Comment',
  components: {
    CreateComment
  },
  props: {
    article: {
      type: Object
    }
  },
  data () {
    return {
      constants,
      SERVER_URL: process.env.VUE_APP_API_URL,
      commentForm: {
        content: '',
        postid: this.article.postId,
        userid: this.$cookies.get(`auth-token`)
      },
      comments: null,
      checkComment: {
        isComment: false,
        commentId: null,
        commentValue: null,
      },
      updateCommentForm: {
        content: '',
        postid: '',
        userid: '',
        commentid: '',
        parentid: '',
        nickname: ''
      }
    }
  },
  computed: {
    ...mapGetters('userStore', ['isLoggedIn'])
  },
  methods: {
    changeIsComment(comment) {
      this.checkComment.isComment = !this.checkComment.isComment
      this.checkComment.commentId  = comment.commentid
      this.checkComment.commentValue = comment.content
      this.updateCommentForm.postid = comment.postid
      this.updateCommentForm.parentid = comment.parentid
      this.updateCommentForm.nickname = comment.nickname
      this.updateCommentForm.userid = comment.userid
      this.updateCommentForm.commentid = comment.commentid
    },
    createComment(content) {
      this.commentForm.content = content
      axios.post(`${this.SERVER_URL}/comments/register`, this.commentForm)
        .then(() => {
          this.getCommentList()
        })
        .catch(err => console.log(err.response.data))
    },
    getCommentList() {
      axios.get(`${this.SERVER_URL}/comments/list/${this.article.postId}`)
        .then((res) => {
          this.comments = res.data
        })
        .catch(err => console.log(err.response.data))
    },
    updateComment(content) {
      this.updateCommentForm.content = content
      axios.put(`${this.SERVER_URL}/comments/modify`, this.updateCommentForm)
        .then(() => {
          this.checkComment.isComment = false
          this.checkComment.commentId = null
          this.checkComment.commentValue = null
          this.getCommentList()
        })
        .catch(err => console.log(err))
    },
    deleteComment(deleteCommentId) {
      axios.delete(`${this.SERVER_URL}/comments/dropComment?commentId=${deleteCommentId}`)
        .then(() => {
          alert("댓글이 삭제되었습니다.")
          this.getCommentList()
        })
        .catch(err => alert(err))
    }
  },
  created() {
    this.getCommentList()
  }
}
</script>

<style scoped>
.create-button {
  font-size: 1em;
  font-weight: 400;
  cursor: pointer;
  border: none;
  border-radius: 10px;
  color: black;
  padding: 5px 7px;
}
.comment {
  position: relative;
}
.rightbuttons{
  position: absolute;
  top: 0;
  right: 0;
}
.comment-username:hover {
  color: var(--primary-color)
}

.comment-time {
  font-size: 11px;
  opacity: 0.5;
}

.comment-content {
  margin: 1rem 0 0 5px;
}
</style>