<template>
  <div>
    <span v-if="checkComment">
      <input @keypress.enter.prevent="changeInput" id="content" :value="checkComment.commentValue" @input="newValue=$event.target.value">
      <button class="create_button" @click.prevent="changeInput" type="submit">수정</button>
    </span>
    <span style="width:100% text-align:center;" v-else>
      <input style="border-radius:5px;width:89%" @keypress.enter.prevent="onInput" id="content" v-model="commentValue" placeholder="댓글을 입력해주세요">
      <button style="margin-left:5px; width:10%" class="create_button" @click.prevent="onInput" type="submit">제출</button>
      
    </span>
  </div>
</template>

<script>
export default {
  name: 'CreateComment',
  data() {
    return {
      commentValue: '',
      newValue: ''
    }
  },
  props: {
    checkComment: {
      type: Object
    }
  },
  methods: {
    onInput() {
      // console.log(e.target.value)
      // console.log(this.newValue)
      this.$emit('submit-comment', this.commentValue)
      this.commentValue = null
    },
    changeInput() {
      this.$emit('change-comment', this.newValue)
    }
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

</style>