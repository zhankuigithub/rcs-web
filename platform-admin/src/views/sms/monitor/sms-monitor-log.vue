<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
      <el-form-item label="管理员电话" prop="userTel">
        <el-input
          v-model="queryParams.params.userTel"
          placeholder="请输入管理员电话"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="管理员名称" prop="userName">
        <el-input
          v-model="queryParams.params.userName"
          placeholder="请输入管理员名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="	预警内容" prop="alarmLog">
        <el-input
          v-model="queryParams.params.alarmLog"
          placeholder="请输入预警内容"
          clearable
          size="small"
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
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:20px">
      <el-table-column label="管理员电话" align="center" prop="userTel"/>
      <el-table-column label="管理员名称" align="center" prop="userName"/>
      <el-table-column label="预警内容" align="center" prop="alarmLog"/>
      <el-table-column label="预警时间" align="center" prop="logDt"/>
      <el-table-column label="发送结果" align="center" prop="remark"/>
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
  smsMonitorLogPage
} from '@/api/sms/smsMonitorLog'

export default {
  name: 'smsMonitorLog',
  created() {
    this.fetchList();
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      queryParams: {
        "currentPage": 1,
        "pageSize": 20,
        "params": {
          "alarmLog": "",
          "userName": '',
          "userTel": ''
        }
      },
      selectMonitorIds: [],
      monitorIdList: [],
      dataList: [],
      addClientDialogOpen: false,
      clientForm: {
        "monitorIds": "",
        "openId": "",
        "openName": "",
        "userName": "",
        "userTel": ""
      },
      op_type: 'add',//'add' 'edit'
      submitLoading: false,
    };
  },
  methods: {
    fetchList() {
      this.loading = true;
      smsMonitorLogPage(this.queryParams).then(res => {
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
      this.queryParams.currentPage = 1;
      this.fetchList();
    },
  }
}
</script>

<style lang="scss" scoped>

</style>
