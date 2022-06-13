<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="月份" prop="beginDt">
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
        <el-form-item label="客户端：" prop="clientId">
          <el-select
            v-model="queryParams.params.clientId"
            placeholder="请选择客户端"
            clearable
            size="mini"
          >
            <el-option
              v-for="item in clientListHasAll"
              :key="item.clientId"
              :label="item.clientDesc"
              :value="item.clientId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="运营商：" prop="providerId">
          <el-select
            v-model="queryParams.params.providerId"
            placeholder="请选择运营商"
            clearable
            size="mini"
          >
            <el-option
              v-for="item in providerListHasAll"
              :key="item.providerId"
              :label="item.providerName"
              :value="item.providerId"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
            <el-button
            type="primary"
            icon="el-icon-search"
            size="mini"
            @click="handleQuery"
            >搜索
            </el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQueryParams"
            >重置</el-button
            >
        </el-form-item>
    </el-form>
    <el-table
      :row-style="{height:'0px'}"
      :cell-style="{padding:'0px'}"
      v-loading="loading"
      :data="dataList"
      stripe
      border
      style="margin-top: 20px"
      highlight-current-row
    >
    <!-- 查询类型：1（1次1条）、2（1次2条）、3（1次3条）、4（1次4条）、5（1次5条）、6（大于5条）、7（成功量）、8（失败量）、9（发送总量） -->
      <el-table-column label="日期" align="center" prop="startDt" />
      <el-table-column label="客户端" align="center" prop="clientDesc" />
      <el-table-column label="运营商" align="center" prop="providerName" />
      <el-table-column label="发送总量" align="center" prop="dailyTotal">
          <template  scope="scope">
              <el-button size="mini" type="text"  @click="gotoDetail(scope.$index,9)"> {{scope.row.dailyTotal}} </el-button>
          </template>
      </el-table-column>
      <el-table-column label="成功量" align="center" prop="dailySuccess">
          <template slot-scope="scope">
              <el-button size="mini" type="text" @click="gotoDetail(scope.$index,7)">{{scope.row.dailySuccess}} </el-button>
          </template>
      </el-table-column>
      <el-table-column label="失败量" align="center" prop="dailyFail">
          <template slot-scope="scope">
              <el-button size="mini" type="text" @click="gotoDetail(scope.$index,8)">{{scope.row.dailyFail}} </el-button>
          </template>
      </el-table-column>
      <el-table-column label="1次1条" align="center" prop="once1">
          <template slot-scope="scope">
              <el-button size="mini" type="text" @click="gotoDetail(scope.$index,1)">{{scope.row.once1}} </el-button>
          </template>
      </el-table-column>
      <el-table-column label="1次2条" align="center" prop="once2">
          <template slot-scope="scope">
              <el-button size="mini" type="text"  @click="gotoDetail(scope.$index,2)">{{scope.row.once2}} </el-button>
          </template>
      </el-table-column>
      <el-table-column label="1次3条" align="center" prop="once3">
          <template slot-scope="scope">
              <el-button size="mini" type="text" @click="gotoDetail(scope.$index,3)">{{scope.row.once3}} </el-button>
          </template>
      </el-table-column>
      <el-table-column label="1次4条" align="center" prop="once4">
          <template slot-scope="scope">
              <el-button size="mini" type="text"  @click="gotoDetail(scope.$index,4)">{{scope.row.once4}} </el-button>
          </template>
      </el-table-column>
      <el-table-column label="1次5条" align="center" prop="once5">
          <template slot-scope="scope">
              <el-button size="mini" type="text" @click="gotoDetail(scope.$index,5)">{{scope.row.once5}} </el-button>
          </template>
      </el-table-column>
      <el-table-column label="大于5条" align="center" prop="onceThan5">
          <template slot-scope="scope">
              <el-button size="mini" type="text" @click="gotoDetail(scope.$index,6)">{{scope.row.onceThan5}} </el-button>
          </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchList"
    />
  </div>
</template>

<script>
import { daysPage } from "@/api/sms/clientStatistic";
import {
    getClientList
  } from '@/api/sms/template'
  import {
    getSmsProviderConfigList
  } from '@/api/sms/smsProviderConfig'
export default {
  name: "ClientStatisticHour",

  data() {
    return {
        clientListHasAll:[],
        providerListHasAll:[],
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      currentDate: "",
      queryParams: {
        "currentPage": 1,
        "pageSize": 10,
        "params": {
          "beginDt": "",
          "clientId": 0,
          "endDt": "",
          "phoneNum": "",
          "providerId": 0,
          "smsContent": ""
        }
      },
      dataList: [],
      dateRange: [],
    };
  },
  created() {
    getClientList().then(res=>{
        this.clientListHasAll = [{clientDesc:'全部',clientId:0},...res.data]
    })
    getSmsProviderConfigList().then(res=>{
        this.providerListHasAll = [{providerName:'全部',providerId:0},...res.data]
    })

    this.resetQueryParams();
  },
  methods: {
    fetchList() {
      this.loading = true;
      this.queryParams.params.beginDt = this.dateFormate(this.dateRange[0]);
      this.queryParams.params.endDt = this.dateFormate(this.dateRange[1]);
      daysPage(this.queryParams).then((res) => {
        this.dataList = res.data.items;
        this.total = res.data.total;
        this.loading = false;
      });
    },
    resetQueryParams() {
      let date = new Date();
      this.dateRange = [
        new Date(date.getFullYear(), date.getMonth(), date.getDate() - 7, 0, 0),
        new Date(),
      ];
      this.queryParams = {
        "currentPage": 1,
        "pageSize": 10,
        "params": {
          "beginDt": "",
          "clientId": 0,
          "endDt": "",
          "phoneNum": "",
          "providerId": 0,
          "smsContent": ""
        }
      };
      this.resetForm("queryForm");
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

    handleQuery() {
      this.fetchList();
    },
    gotoDetail(index,queryType){
        this.$router.push({
            path: "/sms/statistic/client-statistic-day-detail",
            query: {
                dayInfo:JSON.stringify(this.dataList[index]),
                queryType:queryType,
                beginDt: this.dataList[index].startDt,
                endDt: this.dataList[index].startDt,
            },
        });
    }
  },
};
</script>

<style lang="scss" scoped>
</style>
