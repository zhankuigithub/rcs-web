<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
      <el-form-item label="月份" prop="beginDt">
        <el-date-picker
          size="mini"
          v-model="queryParams.params.beginDt"
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
          @click="handleQuery">搜索
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:20px">
      <el-table-column label="客户端id" align="center" prop="clientId"/>
      <el-table-column label="客户端名称" align="center" prop="clientDesc"/>
      <el-table-column label="统计月份" align="center" prop="month"/>
      <el-table-column label="发送总量" align="center" prop="cnt"/>
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
import {
  monthsPage
} from '@/api/sms/clientStatistic'

export default {
  name: 'clientStatisticMonth',
  created() {
    let date = new Date()
    var month = date.getMonth() + 1;
    month = month > 10 ? month : "0" + month;
    this.currentDate =  date.getFullYear() + '-' + month
    this.queryParams.params.beginDt = this.currentDate
    this.fetchList();
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      currentDate:'',
      queryParams: {
        "currentPage": 1,
        "pageSize": 20,
        "params": {
          "beginDt": ''
        }
      },
      selectMonitorIds: [],
      monitorIdList: [],
      dataList: [],
      addClientDialogOpen: false,
      op_type: 'add',//'add' 'edit'
      submitLoading: false,
      pickeroptions:{
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      }
    };
  },
  methods: {
    fetchList() {
      this.loading = true;
      monthsPage(this.queryParams).then(res => {
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
      if (this.queryParams.params.beginDt==null || this.queryParams.params.beginDt=== '') {
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
