<template>
  <div class="app-container">
      <el-form :model="params" ref="queryForm" :inline="true">
        <el-form-item label="日期范围：" prop="beginDt">
            <el-date-picker
            size="mini"
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            >
            </el-date-picker>
        </el-form-item>
        <el-form-item>
            <el-button
            type="primary"
            icon="el-icon-search"
            size="mini"
            @click="fetchList"
            >搜索
            </el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQueryParams"
            >重置</el-button
            >
        </el-form-item>
    </el-form>
    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:10px">
        <el-table-column label="短链接ID" align="center" prop="urlId" width="100"/>
        <el-table-column label="短链接" align="left" prop="urlShort"/>
        <el-table-column label="客户端ID" align="center" prop="clientId" width="100"/>
        <el-table-column label="模板ID" align="center" prop="templateId" width="100"/>
        <el-table-column label="cookie_guid" align="center" prop="cookieGuid" />
        <el-table-column label="访问时间" align="center" prop="logDt" />
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
    shortUrlAccessLog
  } from '@/api/sms/url-manage'

  export default {
    name: 'ClientManage',
    created(){
      this.resetQueryParams()
    },
    data() {
      return {
        // 遮罩层
        loading: false,
        // 总条数
        total: 0,
        dateRange:[],
        params: {
            "currentPage": 0,
            "pageSize": 20,
            "params": {
                "beginDt": "",
                "endDt": ""
            }
        },
        dataList: [],
      };
    },
    methods: {
        fetchList(){
            this.loading = true;
            this.params.params.beginDt = this.dateFormate(this.dateRange[0]);
            this.params.params.endDt = this.dateFormate(this.dateRange[1]);
            shortUrlAccessLog(this.params).then(res=>{
                this.dataList = res.data.items;
                this.total = res.data.total;
                this.loading = false;
            })
        },
        resetQueryParams() {
            let date = new Date();
            this.dateRange = [
                new Date(date.getFullYear(), date.getMonth(), date.getDate() - 7, 0, 0),
                new Date(),
            ];
            this.params = {
                "currentPage": 1,
                "pageSize": 20,
                "params": {
                    "beginDt": "",
                    "endDt": ""
                }
            };
            this.fetchList();
        },
        dateFormate(date){
            const dt =new Date(date)
            const y = dt.getFullYear()
            const m = (dt.getMonth() + 1 + '').padStart(2,0)
            const d = (dt.getDate() + '').padStart(2,0)
            const hh = (dt.getHours() + '').padStart(2,0)
            const mm = (dt.getMinutes() + '').padStart(2,0)
            const ss = (dt.getSeconds() + '').padStart(2,0)
            return `${y}-${m}-${d}`
        },
    }
  }
</script>

<style lang="scss" scoped>

</style>
