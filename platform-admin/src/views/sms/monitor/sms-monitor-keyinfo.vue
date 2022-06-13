<template>
  <div class="app-container">

    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
      <el-form-item label="名称" prop="monitorName">
        <el-input
          v-model="queryParams.params.monitorName"
          placeholder="请输入名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="关键字" prop="monitorKey">
        <el-input
          v-model="queryParams.params.monitorKey"
          placeholder="请输入关键字"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="queryParams.params.remark"
          placeholder="请输入备注"
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
          @click="handleQuery">搜索
        </el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
        >添加预警分类
        </el-button>

      </el-form-item>
    </el-form>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:20px">
      <el-table-column label="预警分类ID" align="center" prop="monitorId" width="100"/>
      <el-table-column label="预警名称" align="center" prop="monitorName"/>
      <el-table-column label="预警关键字" align="center" prop="monitorKey"/>
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
    <!-- 新增客户端 -->
    <el-dialog :title="op_type == 'add'?'新增客户端':'修改客户端'" :visible.sync="addClientDialogOpen"
               :close-on-click-modal="false" width="80%" append-to-body>
      <el-form ref="form" :model="clientForm" :rules="rules" label-width="120px">
        <el-form-item label="预警编号" prop="monitorId" v-if="op_type == 'edit'">
          <el-input v-model="clientForm.monitorId" disabled/>
        </el-form-item>
        <el-form-item label="预警名称" prop="monitorName">
          <el-input v-model="clientForm.monitorName" placeholder=""/>
        </el-form-item>
        <el-form-item label="预警关键字" prop="monitorKey">
          <el-input v-model="clientForm.monitorKey" placeholder=""/>
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
  smsMonitorKeyInfoPage,
  addSmsMonitorKeyInfo,
  editSmsMonitorKeyInfo,
  deleteSmsMonitorKeyInfo
} from '@/api/sms/smsMonitorKeyInfo'
import {delApplication} from "@/api/application";

export default {
  name: 'smsMonitorKeyinfo',
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
        "currentPage": 1,
        "pageSize": 20,
        "params": {
          "monitorId": 0,
          "monitorKey": '',
          "monitorName": '',
          "remark": ''
        }
      },
      dataList: [],
      addClientDialogOpen: false,
      clientForm: {
        "monitorId": 0,
        "monitorKey": "",
        "monitorName": "",
        "remark": ""
      },
      op_type: 'add',//'add' 'edit'
      submitLoading: false,
      rules: {
        monitorName: [
          {required: true, message: "预警名称不能为空", trigger: "blur"}
        ],
        monitorKey: [
          {required: true, message: "预警关键字不能为空", trigger: "blur"}
        ],
        remark: [
          {required: true, message: "备注不能为空", trigger: "blur"}
        ],
      },
    };
  },
  methods: {
    fetchList() {
      this.loading = true;
      smsMonitorKeyInfoPage(this.queryParams).then(res => {
        this.dataList = res.data.items;
        this.total = res.data.total;
        this.loading = false;
      })
    },
    handleAdd() {
      this.clientForm = {
        "monitorId": 0,
        "monitorKey": "",
        "monitorName": "",
        "remark": ''
      }
      this.op_type = 'add'
      this.addClientDialogOpen = true
      this.resetForm("form")
    },
    handleEdit(item) {
      this.clientForm = {
        "monitorId": item.monitorId,
        "monitorKey": item.monitorKey,
        "monitorName": item.monitorName,
        "remark": item.remark
      }
      this.op_type = 'edit'
      this.addClientDialogOpen = true
      this.resetForm("form")
    },
    handleDelete(item) {
      let monitorId = item.monitorId;
      this.$confirm('是否确认删除编号为"' + monitorId + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteSmsMonitorKeyInfo(item).then((response => {
          if (response.code === 200) {
            this.$message({
              message: "删除成功",
              type: "success",
            });
            this.fetchList()
          }
        }));
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.op_type == 'add') {
            this.submitLoading = true
            addSmsMonitorKeyInfo(this.clientForm).then(res => {
              this.submitLoading = false
              this.addClientDialogOpen = false
              this.fetchList()
            })
          } else {
            this.submitLoading = true
            editSmsMonitorKeyInfo(this.clientForm).then(res => {
              this.submitLoading = false
              this.addClientDialogOpen = false
              this.fetchList()
            })
          }
        }
      });
    },
    resetQuery() {
      this.queryParams = {
        params: {
          name: null,
          status: null
        }
      }
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleQuery() {
      this.queryParams.currentPage = 1;
      this.fetchList();
    },
  }
}
</script>

<style lang="scss" scoped>

</style>
