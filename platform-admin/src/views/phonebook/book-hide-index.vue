<template>
  <div class="app-container">

    <div class="filter-container">
      <el-form class="form" :model="queryParams" :inline="true"
               style="width:calc(100% - 180px);display: flex;flex-wrap: wrap;"
      >

        <el-form-item label="客户" prop="params.customerId">
          <el-select v-model="queryParams.params.customerId" placeholder="请选择客户" clearable size="small"
                     :disabled="isQueryById"
          >
            <el-option
              v-for="item in customerList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="手机号：" prop="phoneNum">
          <el-input v-model="queryParams.params.phoneNum" size="mini" placeholder="手机号" clearable></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="fetchList">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQueryParams">重置</el-button>
          <el-button style="margin-left: 10px" type="primary" class="button" size="mini" @click="downLoadExcel()">
            下载明细数据
          </el-button>
        </el-form-item>

      </el-form>
    </div>

    <div class="filter-container">
      <el-form class="form" :model="queryParams" :inline="true"
               style="width:calc(100% - 180px);display: flex;flex-wrap: wrap;"
      >
        <el-form-item>

          <el-button
            type="primary"
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-permission:ADD="$route.meta.permission"
          >新增
          </el-button>

          <el-button
            type="primary"
            icon="el-icon-plus"
            size="mini"
            @click="handleBatchAdd"
            v-permission:ADD="$route.meta.permission"
          >批量新增
          </el-button>

          <el-button
            type="primary"
            icon="el-icon-plus"
            size="mini"
            @click="importBatchAdd"
            v-permission:ADD="$route.meta.permission"
          >导入数据
          </el-button>

          <el-button
            type="danger"
            icon="el-icon-delete"
            size="mini"
            @click="submitDelete"
            v-permission:RM="$route.meta.permission"
          >批量删除
          </el-button>

          <el-popover
            style="margin-left: 10px"
            placement="bottom"
            width="400"
            trigger="click"
          >
            <el-form-item label="标签">
              <el-checkbox-group v-model="updateLabelIds">
                <el-checkbox v-for="item in labelList" :key="item.id + ''" :label="item.id + ''">
                  {{ item.name }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-button
              type="primary"
              size="mini"
              @click="submitBatchUpdLabel"
              v-permission:ADD="$route.meta.permission"
            >确定
            </el-button>

            <el-button slot="reference" type="primary" icon="el-icon-edit" size="mini"
                       v-permission:EDIT="$route.meta.permission"
            >批量修改用户标签
            </el-button>
          </el-popover>

        </el-form-item>

      </el-form>
    </div>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:10px"
              @selection-change="handleSelectionChange"
    >

      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="姓名" align="center" prop="name"/>
      <el-table-column label="手机号" align="center" prop="phoneNum"/>
      <el-table-column label="性别" align="center" prop="sex" :formatter="sexTypeFormat"/>
      <el-table-column label="标签" align="center" prop="labelNames"/>
      <el-table-column label="禁用的应用名单" align="center" prop="appBlacklistNames"/>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleEdit(scope.row)"
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
          </el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-badge style="margin-top: 20px;margin-left: 10px"
              v-for="item in labelMaps" :value="item.cnt"
    >
      <el-popover
        placement="bottom"
        width="200"
        trigger="hover"
      >

        <el-form class="form" :model="updLabelForm">
          <el-form-item label="id" prop="id" hidden>
            <el-input v-model="updLabelForm.id" :hidden="true"></el-input>
          </el-form-item>

          <el-form-item label="标签名" prop="name">
            <el-input v-model="updLabelForm.name"></el-input>
          </el-form-item>

          <el-button
            type="primary"
            size="mini"
            @click="updLabel"
            icon="el-icon-edit"
          >修改
          </el-button>

          <el-button
            type="primary"
            size="mini"
            @click="delLabel"
            icon="el-icon-delete"
          >删除
          </el-button>
        </el-form>

        <el-button @click="fetchListByLabel(item.id)" @mouseover.native="handleLabelEdit(item)" slot="reference" round>
          {{ item.name }}
        </el-button>
      </el-popover>
    </el-badge>

    <el-badge class="item" style="margin-top: 20px;margin-left: 10px">
      <el-popover
        placement="bottom"
        width="400"
        trigger="click"
      >

        <el-form class="form">
          <el-form-item label="请输入标签" prop="name">
            <el-input v-model="addLabelForm.name" placeholder=""/>
          </el-form-item>
          <el-button
            type="primary"
            size="mini"
            @click="addLabel"
            v-permission:ADD="$route.meta.permission"
          >确定
          </el-button>

        </el-form>
        <el-button slot="reference" type="success" icon="el-icon-plus"
                   v-permission:ADD="$route.meta.permission" round
        >添加标签
        </el-button>
      </el-popover>
    </el-badge>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchList"
    />

    <el-dialog :title="op_type === 'add'?'新增联系人':'修改联系人'" :visible.sync="addClientDialogOpen"
               :close-on-click-modal="false" width="80%" append-to-body
    >
      <el-form ref="form" :model="clientForm" :rules="rules" label-width="120px">
        <el-form-item label="id" prop="id" v-if="op_type === 'edit'" hidden>
          <el-input v-model="clientForm.id"/>
        </el-form-item>

        <el-form-item label="客户" prop="customerId">
          <el-select v-model="clientForm.customerId" placeholder="请选择客户" clearable size="small" :disabled="isQueryById"
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

    <el-dialog title="批量添加联系人" :visible.sync="batchAddClientDialogOpen"
               :close-on-click-modal="false" width="80%" append-to-body
    >
      <el-form ref="form" :model="batchForm" :rules="batchRules" label-width="120px">

        <el-form-item label="客户" prop="customerId">
          <el-select v-model="batchForm.customerId" placeholder="请选择客户" clearable size="small" :disabled="isQueryById"
          >
            <el-option
              v-for="item in customerList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="手机号码" prop="phones">
          <el-input type="textarea" :rows="5" placeholder="手动输入号码，每行一个号码"
                    v-model="batchForm.phoneNumStr"
                    @change="buildPhones"
          ></el-input>
        </el-form-item>
        <p v-if="noPhone.length !== 0" style="color: red; height: 10px; font-size: 12px;margin: 0;padding: 0;">
          号码{{ noPhone }}格式错误</p>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="batchForm.remark" placeholder="请输入备注"/>
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
        <el-button type="primary" @click="submitBatchForm" v-loading="submitLoading">确 定</el-button>
        <el-button @click="batchAddClientDialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="导入联系人" :visible.sync="importAddClientDialogOpen"
               :close-on-click-modal="false" width="80%" append-to-body
    >
      <el-form ref="form" :model="importForm" label-width="120px" enctype="multipart/form-data">

        <el-form-item label="客户" prop="customerId">
          <el-select v-model="importForm.customerId" placeholder="请选择客户" clearable size="small" :disabled="isQueryById"
          >
            <el-option
              v-for="item in customerList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="文件" prop="file">
          <el-button style="margin-right: 10px" type="primary" size="mini"><a href="https://rcs-fs.langma.cn/rcs/material%2F979189879454000.xlsx?key=bWF0ZXJpYWwvOTc5MTg5ODc5NDU0MDAwLnhsc3g%3D">下载模板文件</a></el-button>
          <input id="files" style="display:none" type="file" accept=".xlsx" @change="selectExcelFile"/>
          <button @click="selectFile">选择文件</button>
          <div v-if="importForm.fileName  && importForm.fileName.length">{{ importForm.fileName }}</div>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="importForm.remark" placeholder="请输入备注"/>
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
        <el-button type="primary" @click="submitImportForm" v-loading="submitLoading">确 定</el-button>
        <el-button @click="importAddClientDialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  page,
  addPhoneNumberBook,
  updatePhoneNumberBook,
  deletePhoneNumberBook,
  batchAdd,
  batchImportAdd
} from '@/api/phoneBook'
import { getCustomerList } from '@/api/customer'
import { groupLabels, listLabels, addLabels, updateLabels, deleteLabels, batchUpdLabelLabels } from '@/api/labels'
import { getApplicationList } from '@/api/application'
import { dictSexType } from '@/assets/initdata'
import FileSaver from 'file-saver'

export default {
  name: 'PhoneBookDetailIndex',
  created() {
    let _customerId = this.$route.query.id
    if (_customerId != null && _customerId !== '') {
      this.isQueryById = true
      this.queryParams.params.customerId = _customerId
    }
    this.getCustomerListVV()
    this.fetchList()
  },
  data() {
    return {
      loading: false,
      isQueryById: false,
      customerList: [],
      applicationList: [],
      labelList: [],
      selectLabelIds: [],
      selectApplicationIds: [],
      updateLabelIds: [],
      total: 0,
      labelCnt: '',
      queryParams: {
        currentPage: 1,
        pageSize: 10,
        params: {
          customerId: '',
          phoneNum: '',
          labelId: ''
        }
      },
      dataList: [],
      addClientDialogOpen: false,
      batchAddClientDialogOpen: false,
      importAddClientDialogOpen: false,
      submitLoading: false,
      op_type: 'add',//'add' 'edit'
      rules: {
        customerId: [
          { required: true, message: '客户id不能为空', trigger: 'blur' }
        ],
        phoneNum: [
          { required: true, message: '手机号不能为空', trigger: 'blur' }
        ]
      },
      batchRules: {
        customerId: [
          { required: true, message: '客户id不能为空', trigger: 'blur' }
        ],
        phones: [
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
      batchForm: {
        'customerId': '',
        'phones': [],
        'remark': '',
        'labelIds': [],
        'applicationIds': []
      },
      addLabelForm: {
        'customerId': '',
        'name': ''
      },
      labelMaps: [],
      selectionIds: [],
      updLabelForm: {
        'id': '',
        'name': ''
      },
      sexTypeList: dictSexType(),
      noPhone: [],
      importForm: {
        'fileName': ''
      }
    }
  },
  methods: {
    fetchListByLabel(id) {
      this.queryParams.params.labelId = id
      this.loading = true
      page(this.queryParams).then(res => {
        this.dataList = res.data.items
        this.total = res.data.total
        this.labelCnt = '全部用户' + '（' + res.data.total + '）'
        this.loading = false
      })
    },
    fetchList() {
      this.loading = true
      this.queryParams.params.labelId = ''
      page(this.queryParams).then(res => {
        this.dataList = res.data.items
        this.total = res.data.total
        this.labelCnt = '全部用户' + '（' + res.data.total + '）'
        this.loading = false
      })
      listLabels(this.queryParams.params.customerId).then(response => {
        this.labelList = response.data
      })

      groupLabels(this.queryParams.params.customerId).then(response => {
        this.labelMaps = response.data
      })

    },
    resetQueryParams() {
      this.queryParams = {
        params: {
          customerId: this.isQueryById
            ? this.queryParams.params.customerId
            : null,
          carrierId: null
        }
      }
      this.fetchList()
    },
    getCustomerListVV() {
      getCustomerList({ isAll: true }).then(response => {
        this.customerList = response.data
      })

      getApplicationList({ 'customerId': this.queryParams.params.customerId }).then(response => {
        this.applicationList = response.data
      })
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
    submitBatchForm() {
      this.batchForm.labelIds = this.selectLabelIds
      this.batchForm.applicationIds = this.selectApplicationIds

      this.$refs['form'].validate(valid => {
        if (valid) {
          this.submitLoading = true
          batchAdd(this.batchForm).then(res => {
            if (res.msg !== null && res.msg !== '') {
              this.$message.warning(res.msg)
            }
            this.submitLoading = false
            this.batchAddClientDialogOpen = false
            this.fetchList()
          })
        }
      })
    },
    submitImportForm() {
      this.importForm.labelIds = this.selectLabelIds
      this.importForm.applicationIds = this.selectApplicationIds

      this.$refs['form'].validate(valid => {
        if (valid) {
          this.submitLoading = true

          let formData = new FormData()
          formData.append('customerId', this.importForm.customerId)
          formData.append('remark', this.importForm.remark)
          formData.append('labelIds', this.selectLabelIds)
          formData.append('applicationIds', this.selectApplicationIds)
          formData.append('file', this.importForm.file)

          batchImportAdd(formData).then(res => {
            res = res.data
            if (res.msg !== null && res.msg !== '') {
              this.$message.warning(res.msg)
            }
            this.submitLoading = false
            this.importAddClientDialogOpen = false
            this.fetchList()
          })
        }
      })
    },
    selectExcelFile(res) {
      if (res.target.files && res.target.files.length) {
        let selectFile = res.target.files[0]
        this.importForm.file = selectFile
        this.importForm.fileName = selectFile.name
      }
    },
    selectFile() {
      document.getElementById('files').click()
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
    handleBatchAdd() {
      this.batchForm = {
        'customerId': this.queryParams.params.customerId,
        'phones': [],
        'remark': '',
        'labelIds': [],
        'applicationIds': []
      }
      this.batchAddClientDialogOpen = true
      this.resetForm('form')
    },
    importBatchAdd() {
      this.importForm = {
        'customerId': this.queryParams.params.customerId,
        'file': null,
        'remark': '',
        'labelIds': [],
        'applicationIds': []
      }
      this.importAddClientDialogOpen = true
      this.resetForm('form')
    },
    submitBatchUpdLabel() {
      if (this.selectionIds == null || this.selectionIds.length === 0) {
        this.$message('请至少选择一行记录')
        return
      }

      batchUpdLabelLabels({ 'phoneIds': this.selectionIds, 'labelIds': this.updateLabelIds }).then((res) => {
        if (res.data) {
          this.fetchList()
          this.updateLabelIds = []
        }
      })
    },
    submitDelete() {
      if (this.selectionIds == null || this.selectionIds.length === 0) {
        this.$message('请至少选择一行记录')
        return
      }

      this.$confirm('此操作将永久删除数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.selectionIds.forEach(item => {
          deletePhoneNumberBook(item).then((res) => {
            if (!res.data) {
              this.$message('删除失败：' + item)
            }
          })
        })
        this.fetchList()
        this.updateLabelIds = []
      })
    },
    handleEdit(item) {
      this.selectLabelIds = []
      if (item.labelIds != null && item.labelIds.length > 0) {
        this.selectLabelIds = item.labelIds.map(String)
      }

      this.selectApplicationIds = []
      if (item.appBlacklistIds != null && item.appBlacklistIds.length > 0) {
        this.selectApplicationIds = item.appBlacklistIds.map(String)
      }

      this.clientForm = {
        'id': item.id,
        'customerId': item.customerId,
        'name': item.name,
        'phoneNum': item.phoneNum,
        'sex': item.sex,
        'remark': item.remark
      }
      this.op_type = 'edit'
      this.addClientDialogOpen = true
      this.resetForm('form')
    },
    handleDelete(item) {
      this.$confirm('此操作将永久删除数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deletePhoneNumberBook(item.id).then((res) => {
          if (res.data) {
            this.fetchList()
          }
        })
      })
    },
    handleSelectionChange(selection) {
      this.selectionIds = selection.map((item) => item.id)
    },
    addLabel() {
      this.addLabelForm.customerId = this.queryParams.params.customerId
      addLabels(this.addLabelForm).then((res) => {
        this.submitLoading = false
        if (res.data) {
          this.fetchList()
        }
      })
    },
    handleLabelEdit(item) {
      this.updLabelForm = {
        'id': item.id,
        'name': item.name
      }
    },
    updLabel() {
      this.updLabelForm.customerId = this.queryParams.params.customerId
      updateLabels(this.updLabelForm).then((res) => {
        this.submitLoading = false
        if (res.data) {
          this.fetchList()
        }
      })
    },
    delLabel() {
      this.$confirm('是否确认删除标签：' + this.updLabelForm.name + '?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteLabels(this.updLabelForm.id).then(res => {
          if (res.data) {
            this.fetchList()
          }
        })
      })
    },
    sexTypeFormat(row, column) {
      return this.selectDictLabel(this.sexTypeList, row.sex)
    },
    buildPhones() {
      if (this.batchForm.phoneNumStr !== '' && this.batchForm.phoneNumStr !== null) {
        let phones = this.batchForm.phoneNumStr.split('\n')
        let pattam = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/
        this.noPhone = phones.filter(phone => !pattam.test(phone))
        this.batchForm.phones = phones
      }
    },
    downLoadExcel() {
      page(this.queryParams, true).then((res) => {
        FileSaver.saveAs(
          new Blob([res.data], { type: 'application/octet-stream' }), '通讯录.xls'
        )
      })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
