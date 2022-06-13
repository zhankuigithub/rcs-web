<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="客户" prop="customerId">
        <el-select
          v-model="queryParams.params.customerId"
          placeholder="请选择客户"
          @change="customerListChange"
          :disabled="isQueryById"
          clearable
          size="small"
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
          :disabled="isQueryById"
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
      <el-form-item label="模板id" prop="id">
        <el-input
          v-model="queryParams.params.id"
          placeholder="请输入模板id"
          onkeyup="this.value = this.value.replace(/[^\d.]/g,'');"
          maxlength="19"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模板名称" prop="name">
        <el-input
          v-model="queryParams.params.name"
          placeholder="请输入模板名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模板类型" prop="type">
        <el-select v-model="queryParams.params.type" placeholder="请选择模板类型" clearable size="small">
          <el-option
            v-for="dict in messageTypeList"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
        >新增
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="messageTemplateList" @selection-change="handleSelectionChange">
      <el-table-column label="模板id" align="center" prop="id"/>
      <el-table-column label="模板名称" align="center" prop="name"/>
      <el-table-column label="模板类型" align="center" prop="type" :formatter="messageTypeFormat"/>
      <el-table-column label="所属客户" align="center" prop="customerName"/>
      <el-table-column label="所属应用" align="center" prop="appName"/>
      <el-table-column label="素材审核状态" align="center">
        <template slot-scope="scope">

          <div v-for="(item,i) in scope.row.auditRecords">
            <el-button
              size="mini"
              type="text"
              @click="handGotoMaterial(scope.row)"
            >{{ item.carrierName }}-{{ autitStatusFormat(item) }}
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="回落配置" align="center" prop="backType" :formatter="messageBackTypeFormat"/>
      <el-table-column label="短信回落内容" align="center" prop="smsContent"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-permission:RM="$route.meta.permission"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import {
  listMessageTemplate,
  getMessageTemplate,
  delMessageTemplate,
  addMessageTemplate,
  updateMessageTemplate
} from '@/api/messageTemplate'
import {
  customerGrades,
  industryType,
  dictCustomerStatus,
  dictMessageType,
  dictMessageBackType,
  dictMessageTemplateMaterialAuditStatus
} from '@/assets/initdata'
import { getApplicationList, getApplication } from '@/api/application'
import { getCustomerList } from '@/api/customer'

export default {
  name: 'MessageTemplate',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 消息模板表格数据
      messageTemplateList: [],
      messageTypeList: dictMessageType(),
      messageBackTypeList: dictMessageBackType(),
      autitStatusList: dictMessageTemplateMaterialAuditStatus(),
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      customerList: [],
      applicationList: [],
      isQueryById: false,
      // 查询参数
      queryParams: {
        currentPage: 0,
        pageSize: 10,
        params: {
          customerId: null,
          appId: null,
          name: null
        }
      },
      // 表单校验
      rules: {
        appId: [
          { required: true, message: '所属应用不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '模板名称不能为空', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '模板类型不能为空', trigger: 'change' }
        ],
        sugGroupId: [
          { required: true, message: '建议回复菜单id不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '状态不能为空', trigger: 'blur' }
        ],
        logicDel: [
          { required: true, message: '逻辑删除标志 0未删除 1已删除不能为空', trigger: 'blur' }
        ],
        insertDt: [
          { required: true, message: '添加时间不能为空', trigger: 'blur' }
        ],
        updateDt: [
          { required: true, message: '更新时间不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getCustomerListVV()
    this.getList()
  },
  methods: {
    /** 查询消息模板列表 */
    getList() {
      this.loading = true
      listMessageTemplate(this.queryParams).then(response => {
        this.messageTemplateList = response.data.items
        this.total = response.data.total
        this.loading = false
      })
    },
    getCustomerListVV() {
      getCustomerList().then((response) => {
        this.customerList = response.data
      })
    },
    getApplicationListVV() {
      if (
        this.queryParams.params.customerId == null ||
        this.queryParams.params.customerId == ''
      ) {
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
      return this.selectDictLabel(this.messageTypeList, row.type)
    },
    messageBackTypeFormat(row, column) {
      return this.selectDictLabel(this.messageBackTypeList, row.backType)
    },
    autitStatusFormat(row, column) {
      return this.selectDictLabel(this.autitStatusList, row.status)
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        params: {
          customerId: this.isQueryById ? this.queryParams.params.customerId : null,
          appId: this.isQueryById ? this.queryParams.params.appId : null,
          name: null,
          type: null
        }
      }
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      ///template/edit
      this.$router.push({ path: '/template/static/edit', query: { oper: 'add' } })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.$router.push({
        path: '/template/static/edit',
        query: { oper: 'edit', customerId: row.customerId, appId: row.appId, id: row.id }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$confirm('是否确认删除消息模板名称为"' + row.name + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(function() {
        return delMessageTemplate(ids)
      }).then(() => {
        this.getList()
      })
    },
    handGotoMaterial(row) {
      console.log(row)
    }
  }
}
</script>
