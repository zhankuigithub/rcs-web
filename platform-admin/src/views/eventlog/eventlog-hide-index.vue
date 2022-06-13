<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
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
        <el-button style="margin-left: 10px" type="primary" class="button" size="mini" @click="downLoadExcel()">
          下载明细数据
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:20px"
    >
      <el-table-column label="任务id" align="center" prop="task_id" width="200"/>
      <el-table-column label="任务名称" align="center" prop="name"/>
      <el-table-column label="发送号码" align="center" prop="phone_num"/>
      <el-table-column label="运营商" align="center" prop="carrier_name"/>
      <el-table-column label="发送时间" align="center" prop="update_dt" width="200"/>
      <el-table-column label="发送状态" align="center" prop="status" :formatter="sendStatusFormat"/>
      <el-table-column label="发送类型" align="center" prop="send_des"/>
      <el-table-column label="模板id" align="center" prop="message_template_id" width="200"/>
      <el-table-column label="模板名称" align="center" prop="message_template_name"/>
      <el-table-column label="模板类型" align="center" prop="message_template_type" :formatter="messageTypeFormat"/>
      <el-table-column label="回落配置" align="center" prop="message_template_back_type" :formatter="messageBackTypeFormat"/>
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
  detailPage
} from '@/api/logTaskSendEvent'

import {
  dictMessageType,
  dictMessageBackType,
  dictMessageStatusType
} from '@/assets/initdata'
import { page } from '@/api/phoneBook'
import FileSaver from 'file-saver'

export default {
  name: 'eventLogHideIndex',
  created() {
    let taskId = this.$route.query.id
    if (taskId != null && taskId !== '') {
      this.isQueryById = true
      this.queryParams.params.taskId = taskId
    }
    this.fetchList()
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      queryParams: {
        'currentPage': 1,
        'pageSize': 20,
        'params': {
          'taskId': '',
          'phoneNum': ''
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
      detailPage(this.queryParams).then(res => {
        this.dataList = res.data.items
        this.total = res.data.total
        this.loading = false
      })
    },

    handleQuery() {
      this.queryParams.currentPage = 1
      this.fetchList()
    },
    messageTypeFormat(row, column) {
      return this.selectDictLabel(this.messageTypeList, row.message_template_type)
    },
    messageBackTypeFormat(row, column) {
      return this.selectDictLabel(this.messageBackTypeList, row.message_template_back_type)
    },
    sendStatusFormat(row, column) {
      return this.selectDictLabel(this.statusTypeList, row.status)
    },
    downLoadExcel() {
      detailPage(this.queryParams, true).then((res) => {
        FileSaver.saveAs(
          new Blob([res.data], { type: 'application/octet-stream' }), '任务详情.xls'
        )
      })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
