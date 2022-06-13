<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
      <el-form-item label="客户" prop="params.customerId">
        <el-select v-model="queryParams.params.customerId" placeholder="请选择客户" clearable size="small"
                   @change="customerListChange"
        >
          <el-option
            v-for="item in customerList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="应用" prop="appId">
        <el-select
          v-model="queryParams.params.appId"
          placeholder="请选择应用"
          clearable
          size="small"
        >
          <el-option
            v-for="item in applicationList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="任务id" prop="id">
        <el-input
          v-model="queryParams.params.id"
          placeholder="请输入任务id"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="模板id" prop="messageTemplateId">
        <el-input
          v-model="queryParams.params.messageTemplateId"
          placeholder="请输入模板id"
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
          @click="handleQuery"
        >搜索
        </el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:20px"
    >
      <el-table-column label="任务id" align="center" prop="id" width="200">
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="gotoDetailPage(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >{{scope.row.id}}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="任务名称" align="center" prop="name"/>
      <el-table-column label="应用" align="center" width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.customerName }} <br/> {{ scope.row.appName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="模板" align="center" width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.messageTemplateId }} <br/> {{ scope.row.messageTemplateName }}</span>   <span style="font-size: 10px;color: blue">（{{messageTypeFormat(scope.row.messageTemplateType)}}）</span>
        </template>
      </el-table-column>
      <el-table-column label="回落配置" align="center" prop="messageTemplateBackType" :formatter="messageBackTypeFormat"/>
      <el-table-column label="发送时间" align="center" prop="sendDt" width="200"/>
      <el-table-column label="发送状态" align="center" prop="sendStatus" :formatter="sendStatusFormat"/>
      <el-table-column label="号码总量" align="center" prop="cnt"/>
      <el-table-column label="运营商" align="center" prop="carrierName" width="200"/>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" width="300">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.sendStatus === 1 || scope.row.sendStatus === 3"
            size="mini"
            type="danger"
            @click="doCancel(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >取消
          </el-button>

          <el-button
            v-if="scope.row.sendStatus === 1"
            size="mini"
            type="warning"
            @click="doActivateOrSuspend(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >暂停
          </el-button>

          <el-button
            v-if="scope.row.sendStatus === 3"
            size="mini"
            type="success"
            @click="doActivateOrSuspend(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >激活
          </el-button>

        </template>
      </el-table-column>
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
  taskPage,
  cancel,
  activateOrSuspend
} from '@/api/sendTask'
import { getCustomerList } from '@/api/customer'
import { getApplicationList } from '@/api/application'
import {
  dictMessageType,
  dictMessageBackType,
  dictEventStatusType
} from '@/assets/initdata'
import { deleteByCid } from '@/api/phoneBook'

export default {
  name: 'eventLogIndex',
  created() {
    this.getCustomerListVV()
    this.fetchList()
  },
  data() {
    return {
      customerList: [],
      applicationList: [],
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      queryParams: {
        'currentPage': 1,
        'pageSize': 20,
        'params': {
          'alarmLog': '',
          'userName': '',
          'userTel': ''
        }
      },
      dataList: [],
      messageTypeList: dictMessageType(),
      messageBackTypeList: dictMessageBackType(),
      statusTypeList: dictEventStatusType()
    }
  },
  methods: {
    fetchList() {
      this.loading = true
      taskPage(this.queryParams).then(res => {
        this.dataList = res.data.items
        this.total = res.data.total
        this.loading = false
      })
    },
    resetQuery() {
      this.queryParams = {
        params: {
          name: null,
          status: null
        }
      }
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleQuery() {
      this.queryParams.currentPage = 1
      this.fetchList()
    },
    getCustomerListVV() {
      getCustomerList({ isAll: true }).then(response => {
        this.customerList = response.data
      })
    },
    getApplicationListVV() {
      if (this.queryParams.params.customerId == null || this.queryParams.params.customerId === '') {
        return
      }
      getApplicationList({
        customerId: this.queryParams.params.customerId,
        isAll: true
      }).then((response) => {
        this.applicationList = response.data
      })
    },
    customerListChange() {
      this.applicationList = []
      this.queryParams.params.appId = null
      this.getApplicationListVV()
    },
    messageTypeFormat(type) {
      return this.selectDictLabel(this.messageTypeList, type)
    },
    messageBackTypeFormat(row, column) {
      return this.selectDictLabel(this.messageBackTypeList, row.messageTemplateBackType)
    },
    sendStatusFormat(row, column) {
      return this.selectDictLabel(this.statusTypeList, row.sendStatus)
    },
    doCancel(item) {
      this.$confirm('是否取消任务：' + item.name, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        cancel({ 'taskId': item.id }).then((res) => {
          if (res.code === 200) {
            this.fetchList()
          }
        })
      })
    },
    doActivateOrSuspend(item) {
      var sendStatus = item.sendStatus
      var message = ''
      if (sendStatus === 3) {
        message = '是否激活任务：' + item.name + '？'
      } else if (sendStatus === 1) {
        message = '是否取消任务：' + item.name + '？'
      }

      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        activateOrSuspend({ 'taskId': item.id }).then((res) => {
          if (res.code === 200) {
            this.fetchList()
          }
        })
      })
    },
    gotoDetailPage(row) {
      this.$router.push({
        path: '/eventlog/index/detail',
        query: { id: row.id }
      })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
