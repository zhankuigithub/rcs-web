<template>
  <div>
    <el-table
      style="margin-top: 20px"
      v-loading="loading"
      :data="dataList"
      header-row-class-name="el-table-header"
      border
      v-if="dataList.length > 0"
      @selection-change="handleSelectionChange">
      <el-table-column
        type="selection"
        width="40">
      </el-table-column>
      <el-table-column
        :label="getTableNameColumnTitle()"
        align="center"
        prop="name"
      />
      <el-table-column v-if="resourcesType != 'card'" :label="getTableContentColumnTitle()" align="center">
        <template slot-scope="scope">
          <div class="resource-content-wrap">
            <img v-if="scope.row.type == 1"
              class="resource-content-img"
              :src="scope.row.sourceUrl"
              alt=""
            />
            <video v-if="scope.row.type == 3" class="resource-content-img" :src="scope.row.sourceUrl" controls="controls"></video>
            <audio style="height:30px" v-if="scope.row.type == 2" class="resource-content-img" :src="scope.row.sourceUrl" controls="controls"></audio>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="缩略图" align="center">
        <template slot-scope="scope">
          <div class="resource-content-wrap">
            <img
              class="resource-content-img"
              :src="scope.row.thumbnailUrl"
              alt=""
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="所属应用" align="center" prop="appName" />
      <el-table-column label="所属客户" align="center" prop="customerName" />
      <el-table-column label="素材来源" align="center">
        <template slot-scope="scope">
          <el-tag
            type="success"
            effect="dark">
            {{scope.row.attribution == 1 ? "运营":"客户"}}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="运营商审核状态" align="center">
        <template slot-scope="scope">
          <div class="audit-status-wrap">
              <div v-if="scope.row.auditRecords && scope.row.auditRecords.length > 0">
                  <div class="audit-status" v-for="(statusItem,index) in scope.row.auditRecords" :key="index">
                      {{statusItem.carrierName + ' - ' }}<span :class="getAuditStatusTextClass(statusItem)">{{getAuditStatusText(statusItem)}}</span>
                  </div>
              </div>
              <div v-else class="audit-status"> 未提交 </div>
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
          >提交审核</el-button>
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
      {{getEmptyText()}}
    </div>
  </div>
</template>


<script>
export default {
  name: "ResourcesTableList",
  props: {
    dataList: {
      type: Array,
      default() {
        return [];
      },
    },
    resourcesType:{
        type:String,
        default() {
            return 'image';
        },
    }
  },
  watch:{
    dataList(val){
      this.$emit('multipleSelectionChanged',val)
    }
  },
  data() {
    return {
      multipleSelection:[]
    };
  },
  methods: {
    handleSelectionChange(val){
      this.multipleSelection = val;
      this.$emit('multipleSelectionChanged',this.multipleSelection)
    },
    getAuditStatusText(audit) {
      if (audit.status == 0) {
        return "待审核";
      }
      if (audit.status == 1) {
        return "已审核";
      }
      if (audit.status == 2) {
        return "未通过" + '-' + audit.remark;
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
    handleCommitPrimaryAudit(item){
      this.$emit('handleCommitPrimaryAudit', item)
    },
    handleCommitAudit(item){
        this.$emit('handleCommitAudit', item)
    },
    handleDelete(item){
        this.$emit('handleDelete', item)
    },
    getEmptyText() {
      if (this.resourcesType == "image") {
        return "当前还未创建图片素材，点击“上传图片”丰富您的素材库吧~~";
      }
      if (this.resourcesType == "audio") {
        return "当前还未创建音频素材，点击“上传音频”丰富您的素材库吧~~";
      }
      if (this.resourcesType == "video") {
        return "当前还未创建视频素材，点击“上传视频”丰富您的素材库吧~~";
      }
    },
    getTableNameColumnTitle() {
      if (this.resourcesType == "image") {
        return "图片名称";
      }
      if (this.resourcesType == "audio") {
        return "音频名称";
      }
      if (this.resourcesType == "video") {
        return "视频名称";
      }
    },
    getTableContentColumnTitle() {
      if (this.resourcesType == "image") {
        return "图片内容";
      }
      if (this.resourcesType == "audio") {
        return "音频内容";
      }
      if (this.resourcesType == "video") {
        return "视频内容";
      }
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
.resource-content-wrap {
  .resource-content-img {
    max-width: 80%;
    max-height: 200px;
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
