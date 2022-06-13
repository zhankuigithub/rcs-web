<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      size="mini"
    >

      <el-form-item label="月份" prop="beginDt">
        <el-date-picker
          v-model="queryParams.params.beginDt"
          type="month"
          placeholder="选择月份"
          value-format="yyyy-MM"
          :picker-options="pickeroptions"
        >
        </el-date-picker>
      </el-form-item>

      <el-form-item label="客户端" prop="clientId">
        <el-select v-model="queryParams.params.clientId" placeholder="请选择客户端" clearable >
          <el-option
            v-for="item in clientList"
            :key="item.clientId"
            :label="item.clientDesc"
            :value="item.clientId"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="运营商" prop="providerId">
        <el-select v-model="queryParams.params.providerId" placeholder="请选择运营商" clearable >
          <el-option
            v-for="item in providerList"
            :key="item.providerId"
            :label="item.providerName"
            :value="item.providerId"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="手机号" prop="phoneNum">
        <el-input
          v-model="queryParams.params.phoneNum"
          placeholder="请输入手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="短信内容" prop="smsContent">
        <el-input
          v-model="queryParams.params.smsContent"
          placeholder="请输入短信内容"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery">搜索
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:20px">
      <el-table-column label="日期" align="left" prop="insertDt"/>
      <el-table-column label="电话号码" align="left" prop="phoneNum"/>
      <el-table-column label="客户端" align="left" prop="clientDesc"/>
      <el-table-column label="运营商" align="left" prop="providerName"/>
      <el-table-column label="短信内容" align="left" prop="smsContent"/>
      <el-table-column label="发送状态" align="left" prop="sendStatus"/>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchList"
    />
  </div>
</template>

<script>

import {sendLogPage} from '@/api/sms/clientStatistic'
import {getClientList} from '@/api/sms/template'
import {getSmsProviderConfigList} from "@/api/sms/smsProviderConfig";

export default {
  name: 'clientStatisticSend',
  created() {
    let date = new Date()
    var month = date.getMonth() + 1;
    month = month > 10 ? month : "0" + month;
    this.currentDate =  date.getFullYear() + '-' + month
    this.queryParams.params.beginDt = this.currentDate
    this.fetchList();
    this.loadList();
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      clientList: [], // 客户端列表
      providerList: [], // 运营商列表
      queryParams: {
        "currentPage": 1,
        "pageSize": 20,
        "params": {
          "beginDt": "",
          "clientId": "",
          "endDt": "",
          "phoneNum": "",
          "providerId": "",
          "smsContent": "",
        }
      },
      dataList: [],
      addClientDialogOpen: false,
      op_type: 'add',//'add' 'edit'
      submitLoading: false,
      pickeroptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      }
    };
  },
  methods: {
    loadList() {
      getClientList().then((data) => {
        this.clientList = data.data;
      });
      getSmsProviderConfigList().then((data) => {
        this.providerList = data.data;
      });
    },
    fetchList() {
      this.loading = true;
      sendLogPage(this.queryParams).then(res => {
        this.dataList = res.data.items;
        this.total = res.data.total;
        this.loading = false;
      })
    },
    resetQuery() {
      this.queryParams = {
        params: {
          name: null,
          status: null
        }
      }
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleQuery() {
      if (this.queryParams.params.beginDt == null || this.queryParams.params.beginDt === '') {
        this.$message({
          message: "请选择月份",
          type: "warning",
        });
        return
      }
      this.queryParams.currentPage = 1;
      this.fetchList();
    },
  }
}
</script>

<style lang="scss" scoped>

</style>
