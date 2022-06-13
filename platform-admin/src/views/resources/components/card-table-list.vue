<template>
  <div>
    <el-table
      style="margin-top: 20px"
      :data="dataList"
      header-row-class-name="el-table-header"
      border
      v-if="dataList.length > 0"
    >
      <el-table-column label="卡片名称" align="center" prop="name" />
      <el-table-column label="所属应用" align="center" prop="appName" />
      <el-table-column label="所属客户" align="center" prop="customerName" />
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
                }}<span :class="getAuditStatusTextClass(statusItem)">{{
                  getAuditStatusText(statusItem)
                }}</span>
              </div>
            </div>
            <div v-else class="audit-status">未提交</div>
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
          ></el-button>
          <el-button
            type="danger"
            size="small"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-permission:RM="$route.meta.permission"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="empty-text" v-else>
      当前还未创建卡片图文素材，点击“新增卡片”丰富您的素材库吧~~
    </div>
  </div>
</template>


<script>
export default {
  name: "CardTableList",
  props: {
    dataList: {
      type: Array,
      default() {
        return [];
      },
    },
  },
  data() {
    return {};
  },
  methods: {
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
    handleEdit(item) {
      this.$emit("handleEdit", item);
    },
    handleDelete(item) {
      this.$emit("handleDelete", item);
    },
  },
};
</script>


<style lang="scss" scoped>
.audit-status-wrap {
  display: flex;
  padding: 20px;
  flex-direction: row;
  justify-content: center;
  .audit-status {
    color: #333;
    margin: 5px;
    font-size: 14px;
    font-weight: 500;
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
.empty-text{
    width: 100%;
    height: 100%;
    padding: 20px;
    color: #333;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
