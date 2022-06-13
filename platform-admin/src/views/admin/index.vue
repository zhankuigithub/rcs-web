<template>
  <main class="page">
    <section class="search-wrap">
      <el-row :gutter="24">
        <el-col :span="5">
          <el-input size="small" v-model="pageParams.params.account" placeholder="登录账号" clearable></el-input>
        </el-col>
        <el-col :span="5">
          <el-input size="small" v-model="pageParams.params.name" placeholder="管理员姓名" clearable></el-input>
        </el-col>
        <el-col :span="5">
          <el-select size="small" v-model="pageParams.params.status" placeholder="选择状态" style="width:100%">
            <el-option v-for="item in status" :key="'admin_'+item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-input size="small" v-model="pageParams.params.phone" placeholder="联系方式" clearable></el-input>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="pageParams.params = {}">重置</el-button>
          <el-button size="small" v-permission:VIEW="$route.meta.permission" type="primary" @click="loadPage">查询</el-button>
        </el-col>
      </el-row>
    </section>
    <section>
      <article class="toolbar">
        <div class="left">
          <!-- <el-button v-if="account === 'admin'" type="primary" class="el-icon-push" @click="swatchAddDialogCfg"> 添加管理员</el-button> -->
          <el-button size="small" v-permission:ADD="$route.meta.permission" type="primary" class="el-icon-push" @click="swatchAddDialogCfg"> 添加管理员</el-button>
        </div>
        <div class="right">
          <el-tooltip class="item" effect="dark" content="刷新数据" placement="bottom">
            <el-link type="primary" style="font-size: 18px" :underline="false" class="el-icon-refresh" @click="loadPage" />
          </el-tooltip>
          <el-link type="primary" style="font-size: 18px" :underline="false" class="el-icon-full-screen" disabled />
          <el-link type="primary" style="font-size: 18px" :underline="false" class="el-icon-printer" disabled />
        </div>
      </article>
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column label="序号" type="index" width="80" align="center" />
        <el-table-column prop="account" label="账号" align="center" />
        <el-table-column prop="name" label="姓名" align="center" />
        <el-table-column label="头像" width="120" align="center">
          <template slot-scope="{row}">
            <el-image v-if="row.avatar && row.avatar.length" :src="row.avatar" style="width: 40px; height: 40px; border-radius: 8px;" />
            <img v-else src="@/assets/images/default.png" style="width: 40px; height: 40px; border-radius: 8px;" />
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系方式" align="center" />
        <el-table-column label="状态" width="150" align="center">
          <template slot-scope="{row}">
            <el-tag type="info" v-if="row.status === 0"><span class="el-icon-success" style="color: #409EFF;"></span> 正
              常</el-tag>
            <el-tag type="danger" v-else><span class="el-icon-warning" style="color: #fd5e21;"></span> 锁 定</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="insertDt" label="创建时间" width="200" align="center" />
        <el-table-column prop="updateDt" label="更新时间" width="200" align="center" />
        <el-table-column fixed="right" label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button v-permission:VIEW="$route.meta.permission" @click="handlePreview(scope.row)" type="text" size="small">
              <svg-icon icon-class="eye-open" /> 预览</el-button>
            <el-button v-permission:EDIT="$route.meta.permission" @click="swatchEditDialogCfg(scope.row)" type="text" size="small"> <i class="el-icon-edit"></i>
              编辑</el-button>
            <el-button v-permission:RM="$route.meta.permission" @click="handleDelete(scope.row)" type="text" size="small"><i class="el-icon-delete"></i>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <article class="pagination">
        <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[20, 30, 40, 50]" :current-page.sync="pageParams.currentPage" :page-size="pageParams.pageSize"
          :total="pageParams.total">
        </el-pagination>
      </article>
    </section>

    <template>
      <preview-dialog ref="preview" :config="preview" />
      <save-dialog ref="saveDialog" :dialog-config="dialogConfig" @refreshTable="loadPage" />
    </template>
  </main>
</template>

<script>
  import {
    page,
    remove
  } from '@/api/authAdmin'
  import PreviewDialog from './preview.vue'
  import SaveDialog from "./save";
  export default {

    components: {
      PreviewDialog,
      SaveDialog
    },
    data() {
      return {
        loading: false,
        status: [{
            value: '',
            label: "全部"
          },
          {
            value: 0,
            label: "正常"
          },
          {
            value: 1,
            label: "锁定"
          }
        ],
        preview: {
          open: false
        },
        dialogConfig: {
          title: "添加管理员",
          visible: false,
          width: "50%",
          isAdd: true,
          formData: {
            name: "",
            description: "",
          },
          files: [],
          groups: [],
          status: this.status
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
        if(row.avatar && row.avatar.length){
          this.dialogConfig.files = [{
            url: row.avatar
          }]
        }
        this.dialogConfig.title = "修改管理员";
        this.dialogConfig.isAdd = false;
        this.dialogConfig.formData = row;
        this.dialogConfig.visible = !this.dialogConfig.visible;
      },
      swatchAddDialogCfg() {
        this.dialogConfig.title = "添加管理员";
        this.dialogConfig.isAdd = true;
        this.dialogConfig.formData = {}
        this.dialogConfig.visible = !this.dialogConfig.visible;
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
        this.preview.open = true
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
      this.dialogConfig.status = this.status.filter(item => item.value !== '')
      this.loadPage()
    }
  };
</script>

<style lang="scss" scoped>
  @import '../../styles/page.scss'
</style>
