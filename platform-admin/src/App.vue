<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>
<style>
  .top-right-btn{
    position: relative;
    float: right;
  }
  .el-row{
    position: relative;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
  }
  .el-upload__tip {
    font-size: 8px;
    color: #606266;
    margin-top: 7px;
    line-height: 14px;
  }
</style>
