<template>
  <div class="app-container">
    <div class="filter-container">
      <div style="padding: 10px">
        客户
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
      </div>
      <div style="padding: 10px">
        应用
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
      </div>

      <div style="padding: 10px">
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
        >搜索
        </el-button
        >
      </div>
      <div style="padding: 10px; position: absolute; right: 30px">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="medium"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
        >新增固定菜单
        </el-button
        >
      </div>
    </div>
    <el-table
      v-loading="loading"
      :data="dataList"
      header-row-class-name="el-table-header"
      empty-text="暂无固定菜单"
    >
      <el-table-column label="基础菜单名称" align="center" prop="name"/>
      <el-table-column label="所属应用" align="center" prop="appName"/>
      <el-table-column label="所属客户" align="center" prop="customerName"/>
      <el-table-column label="运营商审核状态" align="center">
        <template slot-scope="scope">
          <div class="audit-status-wrap">
            <div
              v-if="scope.row.auditRecords && scope.row.auditRecords.length > 0"
            >
              <div
                class="audit-status"
                v-for="(statusItem, index) in scope.row.auditRecords"
                :key="index"
              >
                {{ statusItem.carrierName + " - "
                }}<span
                :class="getAuditStatusTextClass(statusItem)"
                @click="handleDetail(scope.row)"
              >{{ getAuditStatusText(statusItem) }}</span
              >
              </div>
            </div>
            <el-button
              v-else
              size="mini"
              type="text"
              @click="handleDetail(scope.row)"
            >未提交
            </el-button
            >
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="small"
            icon="el-icon-edit"
            @click="handleEdit(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >修改</el-button>

          <el-button
            type="primary"
            size="small"
            icon="el-icon-search"
            @click="handleCurrentDetail(scope.row)"
            v-permission:VIEW="$route.meta.permission"
          >查看生效菜单
          </el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>
<script>
  import {page, deleteMenu} from "@/api/menus";
  import {getApplicationList, getApplication} from "@/api/application";
  import {customerGrades, getCustomerList} from "@/api/customer";

  export default {
    data() {
      return {
        // 遮罩层
        loading: false,
        // 总条数
        total: 0,
        appName: "",
        isQueryById: false,
        queryParams: {
          currentPage: 1,
          pageSize: 10,
          params: {
            customerId: null,
            appId: null,
            isJump: null,
          },
        },
        dataList: [],
        deleteLoading: false,
        customerList: [],
        applicationList: [],
      };
    },
    created() {
      this.getCustomerListVV();

      let _customerId = this.$route.query.customerId;
      let _appId = this.$route.query.appId;
      let _isJump = this.$route.query.isJump;

      if (
        _customerId != null &&
        _customerId != "" &&
        _appId != null &&
        _appId != ""
      ) {
        this.isQueryById = true;
        this.queryParams.params.customerId = _customerId;
        this.queryParams.params.isJump = _isJump;
        this.getApplicationListVV();
        this.queryParams.params.appId = _appId;
      }
      console.log(this.queryParams);
      this.getList();
    },
    methods: {
      /** 查询信息列表 */
      getList() {
        this.loading = true;
        page(this.queryParams).then((response) => {
          console.log("res-------------->" + JSON.stringify(response));
          this.dataList = response.data.items;
          this.total = response.data.total;
          this.loading = false;
        });
      },
      getCustomerListVV() {
        getCustomerList().then((response) => {
          this.customerList = response.data;
        });
      },
      getApplicationListVV() {
        if (
          this.queryParams.params.customerId == null ||
          this.queryParams.params.customerId == ""
        ) {
          return;
        }
        getApplicationList({
          customerId: this.queryParams.params.customerId,
          isAll: true,
        }).then((response) => {
          this.applicationList = response.data;
        });
      },
      customerListChange() {
        this.applicationList = [];
        this.queryParams.params.appId = null;
        this.getApplicationListVV();
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.currentPage = 1;
        this.getList();
      },
      /** 新增按钮操作 */
      handleAdd() {
        let _query = {};
        if (this.isQueryById) {
          _query = {
            customerId: this.queryParams.params.customerId,
            appId: this.queryParams.params.appId,
            isJump: this.queryParams.params.isJump
          };
        }
        this.$router.push({path: "/menu/config", query: _query});
      },
      handleDelete(item) {
        this.$confirm(
          "如删除本菜单，其对应机器人中的固定菜单也将被删除, 是否继续?",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(() => {
            this.deleteLoading = true;
            deleteMenu({id: item.id}).then((response) => {
              console.log("res-------------->" + JSON.stringify(response));
              this.handleQuery();
              this.deleteLoading = false;
            });
          })
          .catch(() => {
          });
      },
      handleDetail(item) {
        this.$router.push({
          path: "/menu/config",
          query: {
            appId: item.appId + "",
            customerId: item.customerId,
            checkDetail: true,
          },
        });
      },
      handleCurrentDetail(item) {
        this.$router.push({
          path: "/menu/config",
          query: {
            appId: item.appId + "",
            customerId: item.customerId,
            checkDetail: true,
            checkCurrentDetail: true,
          },
        });
      },
      handleEdit(item) {
        var that = this;
        var showDialog = false;
        item.auditRecords.forEach((element) => {
          if (element.status == 1) {
            showDialog = true;
          }
        });
        if (showDialog) {
          this.$confirm(
            "本固定菜单已由运营商审核通过，如进行修改，将重新提交审核，是否继续？",
            "提示",
            {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning",
            }
          )
            .then(() => {
              that.gotoDetailForEdit(item);
            })
            .catch(() => {
            });
        } else {
          that.gotoDetailForEdit(item);
        }
      },
      gotoDetailForEdit(item) {
        this.$router.push({
          path: "/menu/config",
          query: {appId: item.appId + "", customerId: item.customerId},
        });
      },
      getAuditStatusText(audit) {
        if (audit.status == 0) {
          return "待审核";
        }
        if (audit.status == 1) {
          return "已审核";
        }
        if (audit.status == 2) {
          return "未通过";
        }
        return "未提交";
      },
      getAuditStatusTextClass(audit) {
        if (audit.status == 0) {
          return "waitting";
        }
        if (audit.status == 1) {
          return "audit";
        }
        if (audit.status == 2) {
          return "reject";
        }
        return "";
      },
    },
  };
</script>

<style lang="scss">
  .audit-status-wrap {
    display: flex;
    flex-direction: column;

    .audit-status {
      color: #333;
      font: 16px;

      .waitting {
        color: #ff6600;
      }

      .audit {
        color: #008000;
      }

      .reject {
        color: #ff0000;
      }
    }
  }
</style>
