<template>
  <div>
    <el-table
      style="margin-top: 20px"
      :data="dataList"
      header-row-class-name="el-table-header"
      border
      v-if="dataList.length > 0"
    >
      <el-table-column label="网页名称" align="center" prop="name" />
      <el-table-column label="网页地址" align="center" >
        <template slot-scope="scope">
          <a :href="scope.row.originalUrl">{{scope.row.originalUrl}}</a>
        </template>
      </el-table-column>
      <el-table-column label="所属应用" align="center" prop="appName" />
      <el-table-column label="所属客户" align="center" prop="customerName" />
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <!-- <el-button
          v-if="scope.row.status == 0"
            type="primary"
            size="small"
            @click="handleCommitPrimaryAudit(scope.row)"
          v-permission:AUTH="$route.meta.permission"
          >初审</el-button>
          <el-button
          v-if="scope.row.status == 1"
            type="primary"
            size="small"
            @click="handleCommitAudit(scope.row)"
          v-permission:AUTH="$route.meta.permission"
          >提交审核</el-button> -->
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
      当前还未网页，点击“添加网页”添加吧~~
    </div>
  </div>
</template>


<script>
export default {
  name: "WebpageTableList",
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
    getContent(val){
      console.log(val)
      if(val + '' != '' && val != undefined){
        var value = val.replace(/(\n)/g, "");  
        value = value.replace(/(\t)/g, "");  
        value = value.replace(/(\r)/g, "");  
        value = value.replace(/<\/?[^>]*>/g, "");  
        value = value.replace(/\s*/g, "");
        return value
      }
      return ''
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
    handleCommitPrimaryAudit(item){
      this.$emit('handleCommitPrimaryAudit', item)
    },
    handleCommitAudit(item){
        this.$emit('handleCommitAudit', item)
    },
    handleDelete(item){
        this.$emit('handleDelete', item)
    }
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
