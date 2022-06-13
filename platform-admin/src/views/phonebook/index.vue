<template>
  <div class="app-container">

    <div class="filter-container">
      <el-form class="form" :model="queryParams" :inline="true"
               style="width:calc(100% - 180px);display: flex;flex-wrap: wrap;"
      >

        <el-form-item label="客户" prop="params.customerId">
          <el-select v-model="queryParams.params.customerId" placeholder="请选择客户" clearable size="small"
          >
            <el-option
              v-for="item in customerList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="fetchList">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQueryParams">重置</el-button>
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd"
                     v-permission:ADD="$route.meta.permission"
          >新增
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table v-loading="loading" :data="dataList" stripe border style="margin-top:10px"
    >
      <el-table-column label="客户" align="center" prop="customerName"/>
      <el-table-column label="通讯录号码总数" align="center" prop="cnt"/>
      <el-table-column label="移动号码数" align="center" prop="cntCm"/>
      <el-table-column label="联通号码数" align="center" prop="cntCu"/>
      <el-table-column label="电信号码数" align="center" prop="cntCt"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="gotoDetailPage(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="deleteAllPhone(scope.row.customerId)"
            v-permission:RM="$route.meta.permission"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :title="op_type === 'add'?'新增联系人':'修改联系人'" :visible.sync="addClientDialogOpen"
               :close-on-click-modal="false" width="80%" append-to-body
    >
      <el-form ref="form" :model="clientForm" :rules="rules" label-width="120px">
        <el-form-item label="id" prop="id" v-if="op_type === 'edit'" hidden>
          <el-input v-model="clientForm.id"/>
        </el-form-item>

        <el-form-item label="客户" prop="customerId">
          <el-select v-model="clientForm.customerId" placeholder="请选择客户" clearable size="small"
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

        <el-form-item label="手机号" prop="phoneNum">
          <el-input v-model="clientForm.phoneNum" placeholder="请输入手机号"/>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="clientForm.name" placeholder="请输入姓名"/>
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="clientForm.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="clientForm.remark" placeholder="请输入备注"/>
        </el-form-item>

        <el-form-item label="标签" prop="labelIds">
          <el-checkbox-group v-model="selectLabelIds">
            <el-checkbox v-for="item in labelList" :key="item.id + ''" :label="item.id + ''">
              {{ item.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="黑名单应用" prop="applicationIds">
          <el-checkbox-group v-model="selectApplicationIds">
            <el-checkbox v-for="item in applicationList" :key="item.id + ''" :label="item.id + ''">
              {{ item.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-loading="submitLoading">确 定</el-button>
        <el-button @click="addClientDialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>

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
  pageByCid,
  page,
  addPhoneNumberBook,
  updatePhoneNumberBook,
  deletePhoneNumberBook,
  deleteByCid
} from '@/api/phoneBook'
import { getCustomerList } from '@/api/customer'
import { getApplicationList } from '@/api/application'
import { listLabels } from '@/api/labels'

export default {
  name: 'PhoneBookIndex',
  created() {
    this.getCustomerListVV()
    this.fetchList()
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      customerList: [],
      // 总条数
      total: 0,
      queryParams: {
        currentPage: 1,
        pageSize: 10,
        params: {
          customerId: ''
        }
      },
      dataList: [],
      addClientDialogOpen: false,
      submitLoading: false,
      rules: {
        customerId: [
          { required: true, message: '客户id不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '姓名不能为空', trigger: 'blur' }
        ],
        phoneNum: [
          { required: true, message: '手机号不能为空', trigger: 'blur' }
        ]
      },
      clientForm: {
        'customerId': '',
        'name': '',
        'phoneNum': '',
        'sex': 1,
        'remark': '',
        'labelIds': [],
        'applicationIds': []
      },
      selectLabelIds: [],
      selectApplicationIds: [],
      applicationList: [],
      labelList: []
    }
  },
  methods: {
    fetchList() {
      this.loading = true
      pageByCid(this.queryParams).then(res => {
        this.dataList = res.data.items
        this.total = res.data.total
        this.loading = false
      })
    },
    resetQueryParams() {
      this.queryParams = {
        currentPage: 1,
        pageSize: 10,
        params: {
          customerId: ''
        }
      }
      this.fetchList()
    },
    gotoDetailPage(row) {
      this.$router.push({
        path: '/phonebook/index/detail',
        query: { id: row.customerId }
      })
    },
    getCustomerListVV() {
      getCustomerList({ isAll: true }).then(response => {
        this.customerList = response.data
      })
    },
    deleteAllPhone(id) {
      this.$confirm('此操作将永久删除数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteByCid(id).then((res) => {
          if (res.data) {
            this.fetchList()
          }
        })
      })
    },
    handleAdd() {
      this.clientForm = {
        'customerId': this.queryParams.params.customerId,
        'name': '',
        'phoneNum': '',
        'sex': 1,
        'remark': '',
        'labelIds': [],
        'applicationIds': []
      }
      this.op_type = 'add'
      this.addClientDialogOpen = true
      this.resetForm('form')
    },
    customerListChange() {
      if (this.clientForm.customerId !== '' && this.clientForm.customerId !== null) {
        getApplicationList({ 'customerId': this.clientForm.customerId }).then(response => {
          this.applicationList = response.data
        })

        listLabels(this.clientForm.customerId).then(response => {
          this.labelList = response.data
        })
      }
    },
    submitForm() {
      this.clientForm.labelIds = this.selectLabelIds
      this.clientForm.applicationIds = this.selectApplicationIds

      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.op_type === 'add') {
            this.submitLoading = true
            addPhoneNumberBook(this.clientForm).then(res => {
              this.submitLoading = false
              this.addClientDialogOpen = false
              this.fetchList()
            })
          } else {
            this.submitLoading = true
            updatePhoneNumberBook(this.clientForm).then(res => {
              this.submitLoading = false
              this.addClientDialogOpen = false
              this.fetchList()
            })
          }
        }
      })
    },
  }
}
</script>

<style lang="scss" scoped>

</style>
