<template>
  <div class="app-container">

    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
      <el-form-item label="电话" prop="userTel">
        <el-input
          v-model="queryParams.params.userTel"
          placeholder="请输入电话"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="姓名" prop="userName">
        <el-input
          v-model="queryParams.params.userName"
          placeholder="请输入姓名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="openId" prop="openId">
        <el-input
          v-model="queryParams.params.openId"
          placeholder="请输入openId"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="微信昵称" prop="openName">
        <el-input
          v-model="queryParams.params.openName"
          placeholder="请输入微信昵称"
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
        >添加预警人员
        </el-button>

      </el-form-item>
    </el-form>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:20px">
      <el-table-column label="电话" align="center" prop="userTel"/>
      <el-table-column label="姓名" align="center" prop="userName"/>
      <el-table-column label="openId" align="center" prop="openId"/>
      <el-table-column label="微信昵称" align="center" prop="openName"/>
      <el-table-column label="接收预警" align="center" prop="monitors" show-overflow-tooltip="true"/>
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

          <el-button
            size="mini"
            type="primary"
            v-if="scope.row.openId + '' == ''"
            v-permission:RM="$route.meta.permission"
            @click="bindWXDialogOpen = true"
          >
          绑定微信
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
    <el-dialog :title="op_type == 'add'?'新增':'修改'" :visible.sync="addClientDialogOpen"
               :close-on-click-modal="false" width="80%" append-to-body>
      <el-form ref="form" :model="clientForm" :rules="rules" label-width="120px">
        <el-form-item label="电话" prop="userTel">
          <el-input v-model="clientForm.userTel"/>
        </el-form-item>
        <el-form-item label="姓名" prop="userName">
          <el-input v-model="clientForm.userName" placeholder=""/>
        </el-form-item>
        <el-form-item label="openId" prop="openId">
          <el-input v-model="clientForm.openId" placeholder=""/>
        </el-form-item>
        <el-form-item label="微信昵称" prop="openName">
          <el-input v-model="clientForm.openName" placeholder=""/>
        </el-form-item>

        <el-form-item label="预警分类" prop="monitorIds">
          <el-checkbox-group v-model="selectMonitorIds">
            <el-checkbox v-for="item in monitorIdList" :key="item.monitorId + ''" :label="item.monitorId + ''">
              {{ item.monitorName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-loading="submitLoading">确 定</el-button>
        <el-button @click="addClientDialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 绑定微信 -->
    <el-dialog title="绑定微信" :visible.sync="bindWXDialogOpen"
               :close-on-click-modal="true" width="80%" append-to-body>
      <div style="display:flex;flex-direction: column;font-size:16px;line-height:30px">
        <span style="color:red;">1)、手机连接公司WIFI网络</span>
        <span>2)、关注“39健康俱乐部”公众号</span>
        <span>3)、用微信扫以下二维码，并点击推送的链接进行绑定</span>
        <img style="width:200px;height:200px" src="@/assets/images/sms-bind-wx.png" alt="">
        <span>4)、扫码后可以关闭此对话框</span>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  smsMonitorWechatInfoPage,
  addSmsMonitorWechatInfo,
  editSmsMonitorWechatInfo,
  deleteSmsMonitorWechatInfo
} from '@/api/sms/smsMonitorWechatInfo';

import {
  smsMonitorKeyInfoList
} from '@/api/sms/smsMonitorKeyInfo'

export default {
  name: 'smsMonitorWechatInfo',
  created() {
    this.fetchList();
    this.loadMonitorIdListList()
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
          "userTel": "",
          "userName": '',
          "openId": '',
          "openName": ''
        }
      },
      selectMonitorIds: [],
      monitorIdList: [],
      dataList: [],
      addClientDialogOpen: false,
      bindWXDialogOpen:false,
      clientForm: {
        "monitorIds": "",
        "openId": "",
        "openName": "",
        "userName": "",
        "userTel": ""
      },
      op_type: 'add',//'add' 'edit'
      submitLoading: false,
      rules: {
        userTel: [
          {required: true, message: "电话不能为空", trigger: "blur"}
        ],
        userName: [
          {required: true, message: "姓名不能为空", trigger: "blur"}
        ]
      },
    };
  },
  methods: {
    loadMonitorIdListList() {
      smsMonitorKeyInfoList().then((data) => {
        this.monitorIdList = data.data;
      });
    },
    fetchList() {
      this.loading = true;
      smsMonitorWechatInfoPage(this.queryParams).then(res => {
        this.dataList = res.data.items;
        this.total = res.data.total;
        this.loading = false;
      })
    },
    handleAdd() {
      this.clientForm = {
        "monitorIds": "",
        "openId": "",
        "openName": "",
        "userName": "",
        "userTel": ""
      }
      this.op_type = 'add'
      this.addClientDialogOpen = true
      this.resetForm("form")
    },
    handleEdit(item) {
      if (item.monitorIds != null && item.monitorIds != '') {
        this.selectMonitorIds = item.monitorIds.split(",");
      }
      this.clientForm = {
        "monitorIds": "",
        "openId": item.openId,
        "openName": item.openName,
        "userName": item.userName,
        "userTel": item.userTel
      }
      this.op_type = 'edit'
      this.addClientDialogOpen = true
      this.resetForm("form")
    },
    handleDelete(item) {
      let userName = item.userName;
      this.$confirm('是否删除人员"' + userName + '"?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteSmsMonitorWechatInfo(item.userTel).then((response => {
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
      if (this.selectMonitorIds.length > 0) {
        let idStr = "";
        this.selectMonitorIds.forEach((item, index) => {
          idStr += index > 0 ? "," + item : item;
        });
        this.clientForm.monitorIds = idStr;
      } else {
        this.clientForm.monitorIds = "";
      }

      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.op_type == 'add') {
            this.submitLoading = true
            addSmsMonitorWechatInfo(this.clientForm).then(res => {
              this.submitLoading = false
              this.addClientDialogOpen = false
              this.fetchList()
            })
          } else {
            this.submitLoading = true
            editSmsMonitorWechatInfo(this.clientForm).then(res => {
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

<style>
.el-tooltip__popper {

  max-width: 800px;

}
</style>
