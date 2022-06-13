<template>
  <div class="app-container">

    <el-form class="form" :model="queryParams" :inline="true"
             style="width:calc(100% - 180px);display: flex;flex-wrap: wrap;">

      <el-form-item label="" prop="word">
        <el-input v-model="queryParams.params.word" size="mini" placeholder="敏感词" clearable></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="fetchList">搜索</el-button>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
        >新增敏感词
        </el-button>
      </el-form-item>

      <el-form-item>
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          @click="submitDelete"
          v-permission:RM="$route.meta.permission"
        >批量删除
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:10px"
              @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="敏感词" align="center" prop="word"/>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-popover
            placement="bottom"
            width="300"
            trigger="click"
          >

            <el-form class="form" :model="updWordForm">
              <el-form-item label="id" prop="id" hidden>
                <el-input v-model="updWordForm.id" :hidden="true"></el-input>
              </el-form-item>

              <el-form-item label="敏感词" prop="name">
                <el-input v-model="updWordForm.word"></el-input>
              </el-form-item>
              <el-form-item label="备注" prop="remark">
                <el-input v-model="updWordForm.remark"></el-input>
              </el-form-item>
              <el-button
                type="primary"
                size="mini"
                @click="updWord"
              >确定
              </el-button>
            </el-form>
            <el-button size="mini" type="text" slot="reference" icon="el-icon-edit"
                       v-permission:EDIT="$route.meta.permission" @click="handleEdit(scope.row)"
            >修改
            </el-button>
          </el-popover>

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
      @pagination="fetchList"
    />
    <!-- 新增 -->
    <el-dialog title="新增敏感词" :visible.sync="addClientDialogOpen"
               :close-on-click-modal="false" width="80%" append-to-body
    >
      <el-form ref="form" :model="clientForm" :rules="rules" label-width="120px">
        <el-form-item label="敏感词" prop="words">
          <el-input type="textarea" :rows="5" placeholder="请输入敏感词（每行一个，每个词20个字以内）"
                    v-model="clientForm.wordstr"
                    @change="buildWords"
          ></el-input>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="clientForm.remark" placeholder=""/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-loading="submitLoading">确 定</el-button>
        <el-button @click="addClientDialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  page,
  addSensitiveWords,
  checkSensitiveWords,
  updateSensitiveWords,
  deleteSensitiveWords
} from '@/api/sensitive'
import { deletePhoneNumberBook } from '@/api/phoneBook'

export default {
  name: 'ClientManage',
  created() {
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
          'word': ''
        }
      },
      selectionIds: [],
      dataList: [],
      addClientDialogOpen: false,
      clientForm: {},
      updWordForm: {
        'id': 0,
        'word': '',
        'remark': ''
      },
      submitLoading: false,
      rules: {
        appendSign: [
          { required: true, message: '模板签名不能为空', trigger: 'blur' }
        ],
        bindUrl: [
          { required: true, message: '绑定域名不能为空', trigger: 'blur' }
        ],
        clientDesc: [
          { required: true, message: '客户端名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    fetchList() {
      this.loading = true
      page(this.queryParams).then(res => {
        this.dataList = res.data.items
        this.total = res.data.total
        this.loading = false
      })
    },
    handleAdd() {
      this.clientForm = {
        'words': [],
        'remark': ''
      }
      this.addClientDialogOpen = true
      this.resetForm('form')
    },
    handleEdit(item) {
      console.log(item)
      this.updWordForm = {
        'id': item.id,
        'word': item.word,
        'remark': item.remark
      }
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          addSensitiveWords(this.clientForm).then(res => {
            this.submitLoading = false
            this.addClientDialogOpen = false
            this.fetchList()
          })
        }
      })
    },
    handleSelectionChange(selection) {
      this.selectionIds = selection.map((item) => item.id)
    },
    buildWords() {
      this.clientForm.words = this.clientForm.wordstr.split('\n')
    },
    updWord() {
      updateSensitiveWords(this.updWordForm).then((res) => {
        if (res.code === 200 && res.data) {
          this.fetchList()
        } else {
          this.$message(res.msg)
        }
      })
    },
    handleDelete(item) {
      let ids = { 'ids': [item.id] }
      this.$confirm('此操作将永久删除数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteSensitiveWords(ids).then((res) => {
          if (res.data) {
            this.fetchList()
          }
        })
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
        deleteSensitiveWords({ 'ids': this.selectionIds }).then((res) => {
          if (res.data) {
            this.fetchList()
            this.selectionIds = []
          }
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
