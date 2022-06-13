<template>
  <main class="page">
    <section class="search-wrap">
      <el-form :inline="true">
        <el-form-item label="是否显示隐藏菜单">
          <el-switch @change="loadPage" v-model="isShowHidden" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
    </section>
    <section>
      <el-table :row-style="{height:'0px'}"
                :cell-style="{padding:'0px'}"
                row-key="id" :data="tableData" v-loading="loading" border stripe :tree-props="{children: 'children', hasChildren: 'hasChildren'}" >
        <el-table-column prop="id" align="center" label="id" width="150"> </el-table-column>
        <el-table-column prop="sort" align="center" label="顺序" width="150"> </el-table-column>
        <el-table-column prop="title" align="center" label="标题"> </el-table-column>
        <el-table-column prop="path" align="center" label="路由"> </el-table-column>
        <el-table-column prop="component" align="center" label="组件"> </el-table-column>
        <el-table-column align="center" label="隐藏" width="80">
           <template slot-scope="{row}"> {{row.hidden == 0 ? '显示' : '隐藏'}}</template>
        </el-table-column>
        <el-table-column align="center" label="图标" width="80">
          <template slot-scope="{row}"> <span :class="row.icon"></span> </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="250" align="center">
          <template slot-scope="scope">
            <el-button v-permission:ADD="$route.meta.permission" @click="swatchAddDialogCfg(scope.row)" type="text" size="small">
              <i class="el-icon-plus"></i> 添加子菜单
            </el-button>
            <el-button v-permission:EDIT="$route.meta.permission" @click="swatchEditDialogCfg(scope.row)" type="text" size="small">
              <i class="el-icon-edit"></i> 编辑
            </el-button>
            <el-button v-permission:RM="$route.meta.permission" @click="handleDelete(scope.row)" type="text" size="small">
              <i class="el-icon-delete"></i> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <template>
      <save-dialog ref="saveDialog" :dialog-config="dialogConfig" @refreshTable="loadPage" />
    </template>
  </main>
</template>

<script>
  import {
    page,
    getTree,
    remove
  } from '@/api/authMenus'
  import {
    getGroups
  } from '@/api/userAccountInfo'
  import SaveDialog from "./save";
  export default {
    components: {
      SaveDialog
    },
    data() {
      return {
        loading: false,
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
          status: []
        },
        tableData: [],
        isShowHidden: "1"
      };
    },
    methods: {
      loadPage() {
        this.loading = true
        getTree(this.isShowHidden).then((resp) => {
          if (resp.code === 200) {
            this.tableData = resp.data
          }
        }).finally(() => {
          this.loading = false
        })
      },
      swatchEditDialogCfg(row) {
        this.dialogConfig.title = "修改记录";
        this.dialogConfig.isAdd = false;
        this.$refs.saveDialog.initData(row, false)
        this.dialogConfig.visible = !this.dialogConfig.visible;
      },
      swatchAddDialogCfg(row) {
        this.dialogConfig.files=[]
        this.dialogConfig.title = "新增记录";
        this.dialogConfig.isAdd = true;
        this.$refs.saveDialog.initData(row, true)
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
        this.preview.visible = true
      },
      handleAuthorize(row) {
        this.authorize.visible = true
        this.$refs.authorize.initData(row)
      },
    },
    mounted() {
      this.dialogConfig.status = this.status.filter(item => item.value >= 0)
      this.loadPage()
    }
  };
</script>

<style lang="scss" scoped>
  @import '../../styles/page.scss';
</style>
