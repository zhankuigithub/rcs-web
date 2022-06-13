<template>
  <div class="app-container">
    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:10px">
      <el-table-column label="客户端ID" align="center" prop="clientId" width="100"/>
      <el-table-column label="短链接地址" align="left" prop="urlShort"/>
      <el-table-column label="长链接地址" align="left" prop="urlLong"/>
      <el-table-column label="生成时间" align="center" prop="insertDt" />
      <el-table-column label="使用时间" align="center" prop="logDt" />
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="params.currentPage"
      :limit.sync="params.pageSize"
      @pagination="fetchList"
    />
  </div>
</template>

<script>
  import {
    shortUrlUsedLog
  } from '@/api/sms/url-manage'

  export default {
    name: 'ClientManage',
    created(){
      this.fetchList()
    },
    data() {
      return {
        // 遮罩层
        loading: false,
        // 总条数
        total: 0,
        params: {
          "currentPage": 1,
          "pageSize": 20,
          "params": { }
        },
        dataList: [],
      };
    },
    methods: {
      fetchList(){
        this.loading = true;
        shortUrlUsedLog(this.params).then(res=>{
          this.dataList = res.data.items;
          this.total = res.data.total;
          this.loading = false;
        })
      }
    }
  }
</script>

<style lang="scss" scoped>

</style>
