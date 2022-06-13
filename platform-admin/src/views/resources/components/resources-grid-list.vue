<template>
  <div class="card-grid-wrap" :class="isSelectCard ? 'space-around':''">
    <div
      class="rescources-card-wrap"
      v-for="(resource,index) in dataList"
      :key="index"
      @click="selectResources(resource)"
    >
      <div class="rescources-card-item-wrap">
        <div
          class="media-wrap"
          :style="resource.type == 2 ? 'background-image: url(' +  resource.thumbnailUrl + ');' : ''"
        >
          <img
            v-if="resource.type == 1"
            class="grid-item-img"
            :src="resource.sourceUrl"
          />
          <video
            v-if="resource.type == 3"
            class="grid-item-img"
            :src="resource.sourceUrl"
            controls="controls"
          ></video>
          <audio
            v-if="resource.type == 2"
            class="grid-item-img audio"
            :src="resource.sourceUrl"
            controls="controls"
          ></audio>
        </div>
        <div class="card-cont-wrap">
          <div>
            <div class="card-title" style="float: left">{{ resource.name }}</div>
            <div style="float:right;font-size: 10px;font-weight:bold;color: #7B7B7B;margin-right: 5px">{{ resource.appName }}</div>
          </div>
          <div style="width: 100%">
            <div
              class="card-btn"
              v-for="(buttonItem, index) in resource.suggestions"
              :key="index"
            >
              {{ buttonItem.displayText }}
            </div>
          </div>
        </div>
      </div>
      <div >
        <div class="audit-status-wrap" v-if="resource.auditRecords && resource.auditRecords.length > 0">
          <div
            class="audit-status"
            style="width:100%"
          >
            初审：<span :class="getAuditStatusTextClass(resource)">{{
              getAuditStatusText(resource)
            }}</span>
          </div>
          <div
            class="audit-status"
            v-for="(statusItem, index) in resource.auditRecords"
            :key="index"
          >
            {{ statusItem.carrierName + " - "
            }}<span :class="getAuditStatusTextClass(statusItem)">{{
              getAuditStatusText(statusItem)
            }}</span>&nbsp;&nbsp;&nbsp;<el-button type="info" round v-if="!statusItem.effect" size="mini" @click="rebuild(resource.id, statusItem.carrierId)" >过期重提</el-button>
          </div>
        </div>
        <div v-else class="audit-status">未提交</div>
      </div>
      <div class="card-from-warp">
        <el-tag
          type="success"
          effect="dark">
          {{resource.attribution == 1 ? "运营":"客户"}}
        </el-tag>
      </div>
      <div class="card-op-warp" v-if="!isSelectCard">
        <el-button
            type="primary"
            size="small"
            @click="handleCommitPrimaryAudit(resource)"
            v-permission:AUTH="$route.meta.permission"
            v-if="resource.status == 0"
          >初审</el-button>
          <el-button
            v-if="resource.status == 1"
            type="primary"
            size="small"
            @click="handleCommitAudit(resource)"
            v-permission:AUTH="$route.meta.permission"
          >提交审核</el-button>
          <el-button
            type="danger"
            size="small"
            icon="el-icon-delete"
            @click="handleDelete(resource)"
            v-permission:RM="$route.meta.permission"
          ></el-button>
        </div>
    </div>
    <div class="empty-text" v-if="dataList.length == 0">
      {{ getEmptyText() }}
    </div>
  </div>
</template>


<script>
export default {
  name: "ResourcesGridList",
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
    },
    isSelectCard:{

    }
  },
  watch:{
    dataList(val){
    }
  },
  data() {
    return {
    };
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
    selectAuditStatus() {
      this.autitRules.remark[0].required = this.autitForm.status + '' == '2';

      if (this.autitForm.status + '' == '1') {
        this.autitForm.remark = "已审核通过";
      } else if (this.autitForm.status + '' == '2') {
        this.autitForm.remark = "";
        this.auditPlaceholder = "请输入不通过原因";
      } else {
        this.auditPlaceholder = "请输入审核内容";
      }
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
    selectResources(item){
      this.$emit('select-success', item)
      this.visible = false;
    },
    rebuild(materialId, carrierId) {
      this.$emit('rebuild', materialId, carrierId)
    }
  },
};
</script>


<style lang="scss" scoped>
.card-grid-wrap {
  width: 100%;
  height: 100%;
  display: flex;
  flex-wrap: wrap;
}
.space-around{
  justify-content: space-around;
}
.rescources-card-wrap {
  position: relative;
  margin: 10px;
  width: 280px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: space-between;
  background: #fff;
}
.rescources-card-item-wrap {
  width: 100%;
  border-radius: 10px;
  padding: 10px;
  background: #f3f3f3;
  .media-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 200px;
    background-color: #fff;
    position: relative;
    background-size: cover;
    background-repeat: no-repeat;
    .grid-item-img {
      width: 100%;
      max-height: 100%;
      object-fit: cover;
    }
    .audio{
      width: 80%;
      position: absolute;
      bottom: 10px;
      left:10%;
      height: 30px;
    }
  }
  .card-cont-wrap {
    width: 100%;
    display: flex;
    flex-direction: column;

    // height: 100%;
    width: 100%;
    margin-top: 10px;
    .card-title {
      line-height: 24px;
      font-weight: 600;
      font-size: 17px;
      color: #25292e;
      -webkit-line-clamp: 2;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .card-content {
      overflow: hidden;
      font-size: 14px;
      color: #333333;
      text-overflow: ellipsis;
      -webkit-line-clamp: 4;
      display: -webkit-box;
      -webkit-box-orient: vertical;
    }
  }
}
.card-btn {
  display: flex;
  font-size: 16px;
  width: 100%;
  color: #25292e;
  margin-top: 4px;
  padding: 0;
  background: #fff;
  line-height: 38px;
  height: 38px;
  border-radius: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-decoration: none;
  text-align: center;
  justify-content: center;
  margin-top: 10px;
}
.audit-status-wrap {
  display: flex;
  padding: 10px 0;
  flex-direction: row;
  flex-wrap: wrap;
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
.card-op-warp{
    position: absolute;
    right: 10px;
    top: 10px;
}
.card-from-warp{
   position: absolute;
    left: 10px;
    top: 10px;
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
