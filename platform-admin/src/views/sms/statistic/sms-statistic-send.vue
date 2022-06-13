<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      size="mini"
    >

      <el-form-item label="开始" prop="beginDt">
        <el-date-picker
          v-model="queryParams.params.beginDt"
          type="date"
          placeholder="选择开始时间"
          value-format="yyyy-MM-dd"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束" prop="endDt">
        <el-date-picker
          v-model="queryParams.params.endDt"
          type="date"
          placeholder="选择结束时间"
          value-format="yyyy-MM-dd"
        >
        </el-date-picker>
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
      <el-table-column label="日期" align="center" prop="statDt"/>
      <el-table-column label="电话号码" align="center" prop="phoneNum"/>
      <el-table-column label="客户端" align="center" prop="clientDesc"/>
      <el-table-column label="模板" align="center" prop="templateName"/>
      <el-table-column label="发送短信条数" align="center" prop="sendCount"/>
      <el-table-column label="最近一次发送时间" align="center" prop="lastSendDt"/>
      <el-table-column label="警告处理状态" align="center" prop="operationStatus">
        <template slot-scope="scope">
          {{scope.row.operationStatus == 1?'已处理':'未处理'}}
        </template>
      </el-table-column>
      <el-table-column label="警告处理人电话" align="center" prop="noticeTel"/>
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

import {smsSendPage} from '@/api/sms/clientStatistic'

export default {
  name: 'smsStatisticSend',
  created() {
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
          "endDt": "",
        }
      },
      dataList: [],
      addClientDialogOpen: false,
      op_type: 'add',//'add' 'edit'
      submitLoading: false,
    };
  },
  methods: {
    fetchList() {
      this.loading = true;
      smsSendPage(this.queryParams).then(res => {
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
      if (this.queryParams.params.beginDt == null || this.queryParams.params.endDt == null) {
        this.$message({
          message: "请选择时间范围",
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
