<template>
  <div class="app-container">

    <div class="query-Form">
        <div class="form-title">
            <span>客户端发送详单</span>
        </div>
        <div class="form-body">
            <span>
                <span>客户端：{{hourInfo.clientDesc}}</span>
                <span style="margin-left: 10px;margin-right: 10px;">||</span>
                <span>运营商：{{hourInfo.providerName}}</span>
            </span>
            <span style="margin-left: 30px;">
                <span>时间范围:</span>
                <span>{{dateFormate1(beginDt)}} 到 {{dateFormate1(endDt)}}</span>
            </span>
        </div>
        <div class="div-btn" style="padding:10px">
            <el-button type="primary" size="mini" @click="$router.back(-1)">返回</el-button>
        </div>
    </div>
    <el-table
      :row-style="{height:'0px'}"
      :cell-style="{padding:'0px'}"
      v-loading="loading1"
      :data="dataList1"
      stripe
      border
      highlight-current-row
      @row-click="clientTableRowClick"
    >
      <el-table-column label="模板ID" align="left" prop="templateId" width="100"/>
      <el-table-column label="模板名称" align="left" prop="templateName"  width="300"/>
      <el-table-column label="电话数量" align="left" prop="phoneCount" width="100">
      </el-table-column>
      <el-table-column label="发送数量" align="left" prop="sendCount" width="100">
      </el-table-column>
      <el-table-column label="短信长度" align="left" prop="smsLen" width="100">
      </el-table-column>
      <el-table-column label="示例内容" align="left" prop="smsContent">
      </el-table-column>

    </el-table>
    <pagination
      v-show="total1 > 0"
      :total="total1"
      :page.sync="queryParams1.currentPage"
      :limit.sync="queryParams1.pageSize"
      @pagination="pageByClient"
    />
    <div v-if="dataList2 && dataList2.length">
      <el-table
        :row-style="{height:'0px'}"
        :cell-style="{padding:'0px'}"
        v-loading="loading2"
        :data="dataList2"
        stripe
        border
        @row-click="templateTableRowClick"
        highlight-current-row
      >
        <el-table-column label="模板名称" align="left" prop="templateName"  width="300"/>
        <el-table-column label="用户号码" align="left" prop="phoneNum" width="120">
        </el-table-column>
        <el-table-column label="数量" align="left" prop="phoneCount" width="120">
        </el-table-column>
        <el-table-column label="示例内容" align="left" prop="smsContent">
        </el-table-column>

      </el-table>
      <pagination
        v-show="total2 > 0"
        :total="total2"
        :page.sync="queryParams2.currentPage"
        :limit.sync="queryParams2.pageSize"
        @pagination="pageByTemplate"
      />
    </div>
    <div v-if="dataList3 && dataList3.length">
      <el-table
        :row-style="{height:'0px'}"
        :cell-style="{padding:'0px'}"
        v-loading="loading3"
        :data="dataList3"
        stripe
        border
      >
        <el-table-column label="日期" align="left" prop="insertDt"  width="200"/>
        <el-table-column label="用户号码" align="left" prop="phoneNum" width="200">
        </el-table-column>
        <el-table-column label="模板名称" align="left" prop="templateName" width="300">
        </el-table-column>
        <el-table-column label="短信内容" align="left" prop="smsContent">
        </el-table-column>
        <el-table-column label="是否发送" align="left" prop="smsContent" width="120">
          <template slot-scope="scope">{{scope.row.opResult + '' == '-1'?'失败':'成功'}}</template>
        </el-table-column>

      </el-table>
      <pagination
        v-show="total3 > 0"
        :total="total3"
        :page.sync="queryParams3.currentPage"
        :limit.sync="queryParams3.pageSize"
        @pagination="pageByPhone"
      />
    </div>
  </div>
</template>

<script>
  import {pageByClient,pageByTemplate, pageByPhone
  } from '@/api/sms/client_statisitc_hour_detail'
export default {
  name: "ClientStatisticHourDetail",

  data() {
    return {
      hourInfo:{},
      beginDt:'',
      endDt:'',
      queryType:9,
      loading1: false,
      total1: 0,
      queryParams1: {
        "currentPage": 1,
        "pageSize": 10,
        "params": {
          "beginDt": "",
          "clientId": "",
          "endDt": "",
          "phoneNum": "",
          "providerId": "",
          "queryType": "",
          "templateId": ""
        }
      },
      loading2: false,
      total2: 0,
      queryParams2: {
        "currentPage": 1,
        "pageSize": 10,
        "params": {
          "beginDt": "",
          "clientId": "",
          "endDt": "",
          "phoneNum": "",
          "providerId": "",
          "queryType": "",
          "templateId": ""
        }
      },
      loading3: false,
      total3: 0,
      queryParams3: {
        "currentPage": 1,
        "pageSize": 10,
        "params": {
          "beginDt": "",
          "clientId": "",
          "endDt": "",
          "phoneNum": "",
          "providerId": "",
          "queryType": "",
          "templateId": ""
        }
      },
      dataList1:[],
      dataList2:[],
      dataList3:[]

    };
  },
  created() {
    let query = this.$route.query;
    if(query){
      this.hourInfo = JSON.parse(query.hourInfo)
      this.beginDt = query.beginDt
      this.endDt = query.endDt
      this.queryType = parseInt(query.queryType)
    }
    this.pageByClient()
  },
  methods: {
    dateFormate1(date){
        const dt =new Date(date)
        const y = dt.getFullYear()
        const m = (dt.getMonth() + 1 + '').padStart(2,0)
        const d = (dt.getDate() + '').padStart(2,0)
        const hh = (dt.getHours() + '').padStart(2,0)
        const mm = (dt.getMinutes() + '').padStart(2,0)
        const ss = (dt.getSeconds() + '').padStart(2,0)
        return `${y}年${m}月${d}日 ${hh}时${mm}分${ss}秒`
    },
    pageByClient(){
      this.dataList2 = []
      this.dataList3 = []
      this.loading1 = true
      this.queryParams1.params.beginDt = this.beginDt
      this.queryParams1.params.endDt = this.endDt
      this.queryParams1.params.clientId = this.hourInfo.clientId
      this.queryParams1.params.providerId = this.hourInfo.providerId
      this.queryParams1.params.queryType = this.queryType
      pageByClient(this.queryParams1).then(res=>{
        this.dataList1 = res.data.items;
        this.total1 = res.data.total;
        this.loading1 = false;
      })
    },
    pageByTemplate(){
      this.loading2 = true
      this.dataList3 = []
      this.queryParams2.params.beginDt = this.beginDt
      this.queryParams2.params.endDt = this.endDt
      this.queryParams2.params.clientId = this.hourInfo.clientId
      this.queryParams2.params.providerId = this.hourInfo.providerId
      this.queryParams2.params.queryType = this.queryType
      pageByTemplate(this.queryParams2).then(res=>{
        this.dataList2 = res.data.items;
        this.total2 = res.data.total;
        this.loading2 = false;
      })
    },
    pageByPhone(){
      this.loading3 = true
      this.queryParams3.params.beginDt = this.beginDt
      this.queryParams3.params.endDt = this.endDt
      this.queryParams3.params.clientId = this.hourInfo.clientId
      this.queryParams3.params.providerId = this.hourInfo.providerId
      this.queryParams3.params.queryType = this.queryType
      pageByPhone(this.queryParams3).then(res=>{
        this.dataList3 = res.data.items;
        this.total3 = res.data.total;
        this.loading3 = false;
      })
    },

    clientTableRowClick(row, column, event){
      this.dataList2 = []
      this.dataList3 = []
      this.queryParams2.params.templateId = row.templateId
      this.queryParams3.params.templateId = row.templateId
      this.pageByTemplate()
    },
    templateTableRowClick(row, column, event){
      this.dataList3 = []
      this.queryParams3.params.phoneNum = row.phoneNum
      this.pageByPhone()
    }



  },
};
</script>

<style lang="scss" scoped>
.form-title{
    font-size: 16px;
    font-weight: bold;
    background-color: rgb(227, 230, 230);
    padding-left: 6px;
    padding-top: 3px;
    padding-bottom: 3px;
}
.form-body{
    font-size: 14px;
    padding-left: 6px;
    padding-top: 10px;
    padding-bottom: 10px;
}
</style>
