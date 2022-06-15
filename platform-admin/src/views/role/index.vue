<template>
  <main class="page">
    <section class="search-wrap">
      <el-row :gutter="24">
        <el-col :span="5">
          <el-input size="small" v-model="pageParams.params.code" placeholder="输入角色编码搜索"></el-input>
        </el-col>
        <el-col :span="5">
          <el-input size="small" v-model="pageParams.params.roleName" placeholder="输入角色名称搜索"></el-input>
        </el-col>
        <el-col :span="5">
          <el-select size="small" v-model="pageParams.params.status" placeholder="选择状态" style="width:100%">
            <el-option v-for="item in status" :key="item.label" :label="item.label" :value="item.value"/>
          </el-select>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="pageParams.params = {}">重置</el-button>
          <el-button size="small" type="primary" @click="loadPage">查询</el-button>
        </el-col>
      </el-row>
    </section>
    <section>
      <article class="toolbar">
        <div class="left">
          <el-button size="small" type="primary" v-permission:ADD="$route.meta.permission" class="el-icon-push"
                     @click="swatchAddDialogCfg"> 创建角色
          </el-button>
        </div>
        <div class="right">
          <el-tooltip class="item" effect="dark" content="刷新数据" placement="bottom">
            <el-link type="primary" style="font-size: 18px" :underline="false" class="el-icon-refresh"
                     @click="loadPage"/>
          </el-tooltip>
          <el-link type="primary" style="font-size: 18px" :underline="false" class="el-icon-full-screen" disabled/>
          <el-link type="primary" style="font-size: 18px" :underline="false" class="el-icon-printer" disabled/>
        </div>
      </article>
      <el-table :row-style="{height:'0px'}"
                :cell-style="{padding:'0px'}" :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column label="序号" type="index" width="80" align="center"/>
        <el-table-column prop="code" label="角色编码"></el-table-column>
        <el-table-column prop="roleName" label="角色名称"></el-table-column>
        <el-table-column prop="carrierIds" label="权限">
          <template slot-scope="{row}">{{ formatData(row.carrierIds) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="150" align="center">
          <template slot-scope="{row}">{{ row.status === 0 ? '启用' : '停用' }}</template>
        </el-table-column>
        <el-table-column prop="insertDt" label="创建时间" width="200" align="center"/>
        <el-table-column prop="updateDt" label="更新时间" width="200" align="center"/>
        <el-table-column fixed="right" label="操作" width="300" align="center">
          <template slot-scope="scope">
            <el-button v-permission:AUTH="$route.meta.permission" @click="handleAuthorize(scope.row)" type="text"
                       size="small"><i class="el-icon-s-check"></i> 授权
            </el-button>
            <el-button v-permission:EDIT="$route.meta.permission" @click="swatchEditDialogCfg(scope.row)" type="text"
                       size="small"><i class="el-icon-edit"></i>
              编辑
            </el-button>
            <el-button v-permission:RM="$route.meta.permission" @click="handleDelete(scope.row)" type="text"
                       size="small"><i class="el-icon-delete"></i>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <article class="pagination">
        <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                       layout="total, sizes, prev, pager, next, jumper"
                       :page-sizes="[20, 30, 40, 50]" :current-page.sync="pageParams.currentPage"
                       :page-size="pageParams.pageSize"
                       :total="pageParams.total">
        </el-pagination>
      </article>
    </section>

    <template>
      <authorize-dialog ref="authorize" :config="authorize"/>
      <save-dialog ref="saveDialog" :dialog-config="dialogConfig" @refreshTable="loadPage"/>
    </template>
  </main>
</template>

<script>
import {
  page,
  remove
} from '@/api/authRole'
import {
    getClientList
  } from '@/api/sms/template'
import {
  getGroups
} from '@/api/userAccountInfo'
import AuthorizeDialog from './authorize.vue'
import SaveDialog from "./save";

export default {
  components: {
    AuthorizeDialog,
    SaveDialog
  },
  data() {
    return {
      loading: false,
      carrierJson: {1: '中国移动', 2: '中国联通', 3: '中国电信', 4: '朗玛信息', 5: '广西移动'},
      status: [{
        label: "全部"
      },
        {
          value: 0,
          label: "启用"
        },
        {
          value: 1,
          label: "停用"
        }
      ],
      authorize: {
        title: '授权',
        visible: false,
      },
      dialogConfig: {
        title: "创建角色",
        visible: false,
        width: "50%",
        isAdd: true,
        types: [],
        status: [],
        carrierList: [
          {id: 1, name: '中国移动'},
          {id: 2, name: '中国联通'},
          {id: 3, name: '中国电信'},
          {id: 4, name: '朗玛信息'},
          {id: 5, name: '广西移动'},
        ],
        clientList:[

        ]
      },
      pageParams: {
        currentPage: 1,
        pageSize: 20,
        total: null,
        params: {}
      },
      tableData: []
    };
  },
  methods: {
    loadPage() {
      this.loading = true
      page(this.pageParams).then((resp) => {
        if (resp.code === 200) {
          this.tableData = resp.data.items
          this.pageParams.total = resp.data.total
        }
      }).finally(() => {
        this.loading = false
      })
    },
    swatchEditDialogCfg(row) {
      this.dialogConfig.title = "修改记录";
      this.dialogConfig.isAdd = false;
      this.$refs.saveDialog.initData(row)
      this.dialogConfig.visible = !this.dialogConfig.visible;
    },
    swatchAddDialogCfg() {
      this.dialogConfig.title = "新增记录";
      this.dialogConfig.isAdd = true;
      this.$refs.saveDialog.initData({})
      this.dialogConfig.visible = !this.dialogConfig.visible;
    },
    formatData(carrierIds) {
      if (carrierIds != null && carrierIds != '') {
        let array = carrierIds.split(',');
        let msg = "";
        array.forEach(item => {
          msg += this.carrierJson[item] + " ";
        })
        return msg;
      }
      return "";
    },
    handleDelete(row) {
      const this_ = this
      this.$confirm("此操作将永久删除数据, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        this_.doRemove(row.id)
      })
    },
    doRemove(id) {
      remove(id).then((resp) => {
        if (resp.code === 200) {
          this.loadPage()
          this.$notify({
            title: "成功",
            message: "删除成功",
            type: "success",
          });
        } else {
          this.$message({
            type: "warning",
            message: resp.msg,
          });
        }
      });
    },
    handlePreview(row) {
      this.$refs.preview.initData(row)
      this.preview.visible = true
    },
    handleAuthorize(row) {
      this.authorize.visible = true
      this.$refs.authorize.initData(row)
    },
    handleSizeChange(pageSize) {
      this.pageParams.pageSize = pageSize
      this.loadPage()
    },
    handleCurrentChange(currentPage) {
      this.pageParams.currentPage = currentPage
      this.loadPage()
    },
  },
  mounted() {
    this.dialogConfig.status = this.status.filter(item => item.value >= 0)
    this.loadPage()
  }
};
</script>

<style lang="scss" scoped>
@import '../../styles/page.scss'
</style>
