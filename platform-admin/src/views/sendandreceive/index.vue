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

      <el-form-item label="手机号" prop="phoneNum">
        <el-input
          v-model="queryParams.params.phoneNum"
          placeholder="请输入手机号"
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
      <el-table-column label="用户号码" align="center" prop="phone_num"/>
      <el-table-column label="运营商" align="center" prop="carrier_name"/>
      <el-table-column label="上行/下行" align="center" prop="send_type"/>
      <el-table-column label="发送状态" align="center" prop="status" :formatter="sendStatusFormat"/>
      <el-table-column label="消息类型" align="center" prop="" :formatter="sessionTypeFormat">

      </el-table-column>
      <el-table-column label="记录时间" align="center" prop="log_dt" width="180"/>
      <el-table-column label="客户/应用" align="center" width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.customer_name }} <br/>（{{ scope.row.app_name }}）</span>
        </template>
      </el-table-column>
      <el-table-column label="任务id" align="center" prop="task_id"/>
      <el-table-column label="任务名称" align="center" prop="task_name"/>
      <el-table-column label="模板id" align="center" prop="message_template_id"/>
      <el-table-column label="模板" align="center" prop="message_template_name"/>
      <el-table-column label="操作" align="center" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            @click="replyMessage(scope.row)"
            v-permission:EDIT="$route.meta.permission"
            :disabled="scope.row.send_type == '下行'"
          >回复消息
          </el-button>
          <el-button
            size="mini"
            type="text"

            @click="checkSession(scope.row)"
            v-permission:RM="$route.meta.permission"
          >查看会话
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

import { getCustomerList } from '@/api/customer'
import { getApplicationList } from '@/api/application'
import {
  dictMessageType,
  dictMessageBackType,
  dictMessageStatusType
} from '@/assets/initdata'
import { sendAndReceiveLog } from '@/api/statistical'
import SendReceivePreview from '@/components/SendReceivePreview'

export default {
  components: {
    SendReceivePreview
  },
  name: 'eventLogIndex',
  created() {
    this.getCustomerListVV()
    this.fetchList()
  },
  data() {
    return {
      template: {},
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
      statusTypeList: dictMessageStatusType()
    }
  },
  methods: {
    fetchList() {
      this.loading = true
      sendAndReceiveLog(this.queryParams).then(res => {
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
    messageTypeFormat(row, column) {
      return this.selectDictLabel(this.messageTypeList, row.message_template_type)
    },
    messageBackTypeFormat(row, column) {
      return this.selectDictLabel(this.messageBackTypeList, row.messageTemplateBackType)
    },
    sendStatusFormat(row, column) {
      return this.selectDictLabel(this.statusTypeList, row.status)
    },
    sessionTypeFormat(row, column) {
      let send_type = row.send_type
      if (row.content !== null && row.content !== undefined && row.content !== '') {
        let content = JSON.parse(row.content)
        if (send_type === '上行') {
          if (content.maapFile && content.maapFile.length) {
            let file = content.maapFile[0]
            if (file.contentType.indexOf('image') !== -1) {
              return '上行图片消息'
            }
            if (file.contentType.indexOf('audio') !== -1) {
              return '上行语音消息'
            }
            if (file.contentType.indexOf('video') !== -1) {
              return '上行视频消息'
            }
          }
          return '上行文本消息'
        } else {
          return this.selectDictLabel(this.messageTypeList, content.type)
        }
      }
    },
    replyMessage(row) {
      var parmas = {
        send_type: 'reply',
        phone_num: row.phone_num,
        app_id: row.app_id + '',
        carrier_id: row.carrier_id + '',
        customer_id: row.customer_id + ''
      }
      this.$router.push({ path: '/send/index', query: parmas })
    },
    checkSession(row) {
      var parmas = { phone_num: row.phone_num, app_id: row.app_id + '' }
      this.$router.push({ path: '/sendandreceive/session', query: parmas })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
