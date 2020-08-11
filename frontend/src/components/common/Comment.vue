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
    <CreateComment @submit-comment="createComment" />
    
  </div>
</template>

<script>
import constants from '@/lib/constants.js'
import CreateComment from './CreateComment.vue'
import axios from 'axios'

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
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      this.commentForm.content = content
      axios.post(`${this.SERVER_URL}/comments/register`, this.commentForm, config)
        .then(() => {
          // console.log(res.data)
          this.getCommentList()
        })
        .catch(err => console.log(err.response.data))
    },
    getCommentList() {
      axios.get(`${this.SERVER_URL}/comments/list/${this.article.postId}`)
        .then((res) => {
          // console.log(res)
          this.comments = res.data
          // console.log(this.comments)
        })
        .catch(err => console.log(err.response.data))
    },
    updateComment(content) {
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      this.updateCommentForm.content = content
      console.log(this.updateCommentForm)
      axios.put(`${this.SERVER_URL}/comments/modify`, this.updateCommentForm, config)
        .then(() => {
          // console.log(res.data)
          this.checkComment.isComment = false
          this.checkComment.commentId = null
          this.checkComment.commentValue = null
          this.getCommentList()
        })
        .catch(err => {
          // console.log(this.commentForm)
          console.log(err)
        })
    },
    deleteComment(deleteCommentId) {
      const config = {
        headers: {
          Authorization: `Token ${this.$cookies.get(`auth-token`)}`
        }
      }
      axios.delete(`${this.SERVER_URL}/comments/dropComment?commentId=${deleteCommentId}`, config)
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
  color: #fff;
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