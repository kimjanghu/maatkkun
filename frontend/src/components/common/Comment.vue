<template>
  <div>
  <CreateComment @submit-comment="createComment" />
    <br><h2>댓글</h2><br>
    <div class="comment" v-for="comment in comments" :key="`comment_${comment.commentid}`">
      <router-link
        class="comment-username" 
        :to="{ name:constants.URL_TYPE.USER.MYPAGE, params:{ id: comment.userid }}"
      >{{ comment.nickname }}</router-link> | {{ comment.createDate }}<br>

      <div v-if="checkComment.isComment && comment.commentid === checkComment.commentId">
        <CreateComment :checkComment="checkComment" @change-comment="updateComment"/>
      </div>
      <div v-else>
        <br>{{ comment.content }}<br>
      </div>
      
      <div class="rightbuttons" v-if="!checkComment.isComment && +commentForm.userid === comment.userid">
        <button class="create_button" style="margin-right:2px;" @click="changeIsComment(comment)">수정</button>
        <button class="create_button" @click="deleteComment(comment.commentid)">삭제</button>
      </div><br>
    </div>
    <hr><br>
    
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
.create_button {
  border: none;
  font-size: 1em;
  font-weight: 400;
  cursor: pointer;
  border: none;
  border-radius: 10px;
  color: #000000;
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
</style>