<template>
  <div class="card-grid-wrap" :class="isSelectCard ? 'space-around':''">
    <div
      class="rescources-card-wrap"
      v-for="(cardInfo,index) in dataList"
      :key="index"
      @click="selectCard(cardInfo)"

    >
      <div class="rescources-card-item-wrap" >
        <div
          class="media-wrap"
          :class="cardInfo.height == 3?'big':cardInfo.height == 1?'small':''"
          v-if="
            cardInfo.material != null &&
            JSON.stringify(cardInfo.material) != '{}'
          "
          :style="cardInfo.material.type == 2 ? 'background-image: url(' +  cardInfo.material.thumbnailUrl + ');' : ''"
        >
          <img
            v-if="cardInfo.material.type == 1"
            class="grid-item-img"
            :src="cardInfo.material.thumbnailUrl !== null ? cardInfo.material.thumbnailUrl:cardInfo.material.fileUrl "
          />
          <video
            v-if="cardInfo.material.type == 3"
            class="grid-item-img"
            :src="cardInfo.material.fileUrl"
            controls="controls"
            :poster="cardInfo.material.thumbnailUrl"
          ></video>
          <audio
            v-if="cardInfo.material.type == 2"
            class="grid-item-img audio"
            :src="cardInfo.material.fileUrl"
            controls="controls"
          ></audio>
        </div>
        <div class="card-cont-wrap">
          <div>
            <div class="card-title">{{ cardInfo.title }}</div>
            <div class="card-content">{{ cardInfo.description }}</div>
          </div>
          <div style="width: 100%">
            <div
              class="card-btn"
              v-for="(buttonItem, index) in cardInfo.suggestions"
              :key="index"
            >
              {{ buttonItem.displayText }}
            </div>
          </div>
        </div>
      </div>
      <div style="font-weight: bold;font-size: 10px;color: #7B7B7B;">{{cardInfo.appName}}</div>
      <div >
        <div class="audit-status-wrap" v-if="cardInfo.auditRecords && cardInfo.auditRecords.length > 0">
          <div
            class="audit-status"
            v-for="(statusItem, index) in cardInfo.auditRecords"
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

      <div class="card-op-warp" v-if="!isSelectCard">
          <el-button
            type="primary"
            size="small"
            icon="el-icon-edit"
            @click="handleEdit(cardInfo)"
            v-permission:EDIT="$route.meta.permission"
          ></el-button>
          <el-button
            type="danger"
            size="small"
            icon="el-icon-delete"
            @click="handleDelete(cardInfo)"
            v-permission:RM="$route.meta.permission"
          ></el-button>
        </div>
    </div>
     <div class="empty-text" v-if="dataList.length == 0">
      当前还未创建卡片图文素材，点击“新增卡片”丰富您的素材库吧~~
    </div>
  </div>
</template>


<script>
export default {
  name: "CardGridList",
  props: {
    dataList: {
      type: Array,
      default() {
        return [];
      },
    },
    isSelectCard:{

    }
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
    handleEdit(item){
        this.$emit('handleEdit', item)
    },
    handleDelete(item){
        this.$emit('handleDelete', item)
    },
    selectCard(item){
      this.$emit('select-success', item)
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
  flex: 1;
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
      height: 100%;
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
  .small{
    height: 150px;
  }
  .big{
    height: 250px;
  }
  .card-cont-wrap {
    width: 100%;
    display: flex;
    flex-direction: column;

    // height: 100%;
    width: 100%;
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
      margin-top: 10px;
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
