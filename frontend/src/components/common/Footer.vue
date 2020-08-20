<template>
  <div class="footer">
    <p class="copyright">Copyright <i class="far fa-copyright"> 2020</i> MAATKKUN. All Rights Reserved.</p>
  </div>
</template>

<script>
export default {
  name: 'Footer'
}
</script>

<style>
.footer {
  margin-top: 100px;
  height: 150px;
  background-color: rgba(36, 35, 38, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
}

.copyright {
  font-family: 'Noto Sans KR', sans-serif;
  color: #fff;
}
</style>