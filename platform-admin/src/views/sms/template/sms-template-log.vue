<template>
  <div class="app-container">
    <div class="filter-container" >
      <el-form class="form" :model="params" :inline="true" label-width="100px" style="width:calc(100% - 180px);display: flex;flex-wrap: wrap;">
        <el-form-item label="客户端：" prop="clientId">
          <el-select
            v-model="params.params.clientId"
            placeholder="请选择客户端："
            clearable
            size="mini"
          >
            <el-option
              v-for="item in clientList"
              :key="item.clientId"
              :label="item.clientDesc"
              :value="item.clientId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号：" prop="templateId">
            <el-input v-model="params.params.mobile" size="mini" placeholder="手机号" clearable></el-input>
        </el-form-item>
        <el-form-item label="消息内容：" prop="msgContent">
            <el-input v-model="params.params.msgContent" size="mini" placeholder="消息内容" clearable></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="fetchList">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQueryParams">重置</el-button>
        </el-form-item>
      </el-form>
      <div style="padding: 10px; position: absolute; right: 30px">
          <el-button
          type="primary"
          icon="el-icon-plus"
          size="medium"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
          >添加短信模板</el-button>
      </div>
    </div>
    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border >
        <el-table-column label="日期" align="left" prop="insertDt" />
        <el-table-column label="电话号码" align="left" prop="mobile" />
        <el-table-column label="客户端" align="left" prop="clientDesc"/>
        <el-table-column label="模板名称" align="left" prop="templateName"/>
        <el-table-column label="短信内容" align="left" prop="msgContent"/>
        <el-table-column label="短网址" align="left" prop="urlShort"/>
        <el-table-column label="发送状态" align="left">
         <template slot-scope="{row}">{{ sendStatusJson[row.sendStatus]}}</template>
       </el-table-column>
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
    getClientList,
    getSmsTemplateLogList
  } from '@/api/sms/template'
import Editor from "@/components/editor/editor-tinymce";
  export default {
    name: 'SmsTemplateManage',
    components: {
        Editor
    },
    data() {
      return {
        sendStatusJson: {1: '成功', 2: '失败', 0:'待发送'},
        // 遮罩层
        loading: false,
        // 总条数
        total: 0,
        params: {
            "currentPage": 1,
            "pageSize": 10,
            "params": {
                "clientId": "",
                "mobile": "",
                "msgContent": ""
            }
        },
        clientList:[],
        dataList: []
      };
    },
    created(){
      this.fetchList()
      getClientList().then(res=>{
          this.clientList = res.data
      })
    },
    watch:{
        '$route':"fetchList" //需要处理数据的方法
    },
    methods: {
      fetchList(){
        this.loading = true;
        getSmsTemplateLogList(this.params).then(res=>{
          this.dataList = res.data.items;
          this.total = res.data.total;
          this.loading = false;
        })
      },
      resetQueryParams(){
          this.params = {
                "currentPage": 1,
                "pageSize": 10,
                "params": {
                    "clientId": "",
                    "mobile": "",
                    "msgContent": ""
                }
            }
            this.fetchList()
      },
      afterPage() {
        let currentPage = this.params.currentPage;
        if (currentPage === 1) {
          this.$message({
            type: "warning",
            message: "当前已经是第一页",
          });
          return;
        }
        this.loading = true;
        this.params.currentPage = currentPage -1;
        getSmsTemplateLogList(this.params).then(res=>{
          this.dataList = res.data.items;
          this.total = res.data.total;
          this.loading = false;
        })
      },
      nextPage() {
        this.loading = true;
        let currentPage = this.params.currentPage;
        this.params.currentPage = currentPage +1;
        getSmsTemplateLogList(this.params).then(res=>{
          this.dataList = res.data.items;
          this.total = res.data.total;
          this.loading = false;
        })
      }
    }
  }
</script>

<style lang="less" scoped>
.cell {
    -webkit-line-clamp: 1;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 23px;
    padding-right: 10px;
}

</style>
