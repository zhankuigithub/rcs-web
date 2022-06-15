<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="月份" prop="beginDt">
        <el-date-picker
          size="mini"
          v-model="params.params.month"
          type="month"
          placeholder="选择月份"
          value-format="yyyy-MM"
          :picker-options="pickeroptions"
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
      </el-form-item>
    </el-form>
    <el-table
      :row-style="{ height: '0px' }"
      :cell-style="{ padding: '0px' }"
      v-loading="loading"
      :data="dataList"
      stripe
      border
      style="margin-top: 10px"
    >
      <el-table-column label="运营商ID" align="left" prop="providerId" />
      <el-table-column label="运营商" align="left" prop="providerName" />
      <el-table-column label="统计月份" align="left" prop="dt" />
      <el-table-column label="发送次数" align="left" prop="times" />
      <el-table-column label="发送总量" align="left" prop="sendCount" />
      <el-table-column label="发送成功总量" align="left" prop="successCount" />
      <el-table-column label="发送失败总量" align="left" prop="failCount" />
      <el-table-column label="总计费用" align="left" prop="totalMoney" />
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="params.currentPage"
      :limit.sync="params.pageSize"
      @pagination="fetchList"
    />
  </div>
</template>

<script>
import { smsProviderBillingStat } from "@/api/sms/gateway";

export default {
  name: "ClientManage",
  created() {
    let date = new Date();
    var month = date.getMonth() + 1;
    month = month > 10 ? month : "0" + month;
    this.currentDate = date.getFullYear() + "-" + month;
    this.fetchList();
  },
  data() {
    return {
      currentDate: "",
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      params: {
        currentPage: 1,
        pageSize: 20,
        params: {
          month: "",
        },
      },
      dataList: [],
      pickeroptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      },
    };
  },
  methods: {
    fetchList() {
      this.loading = true;
      if (!this.params.params.month || this.params.params.month == "") {
        this.params.params.month = this.currentDate;
      }
      smsProviderBillingStat(this.params).then((res) => {
        this.dataList = res.data.items;
        this.total = res.data.total;
        this.loading = false;
      });
    },
  },
};
</script>

<style lang="scss" scoped>
</style>
