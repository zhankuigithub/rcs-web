<template>
  <el-dialog :title="dialogTitle" :visible.sync="visible" :close-on-click-modal="false" @close="closeDialog" width="60%">
    <div class="filter-container">
      <div style="padding: 10px">
        <el-input
          placeholder="请输入 素材名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
          style="width: 200px"
          v-model="queryParams.params.name"
        ></el-input>
      </div>
      <div style="padding: 10px">
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
      </div>
      <div style="padding: 10px; position: absolute; right: 30px">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          >{{ getAddButtonTitle() }}</el-button
        >
      </div>
    </div>
    <ResourcesGridList
      :dataList="dataList"
      :resourcesType="resourcesType"
      :isSelectCard="true"
      @select-success="selectResources"
    ></ResourcesGridList>
   <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <ResourcesUpload
      ref="uploader"
      @upload-success="handleQuery"
    ></ResourcesUpload>
  </el-dialog>
</template>
<script>
import { listResources } from "@/api/resources";
import ResourcesUpload from "./upload-resources";
import ResourcesGridList from "./resources-grid-list";
export default {
  name: "ResourcesSelector",
  components: {
    ResourcesUpload,
    ResourcesGridList
  },
  data() {
    return {
      dialogTitle: "图片",
      visible: false,
      resourcesType:'image',
      // 总条数
      total: 0,
      // 资源列表表格数据
      dataList: [],
      queryParams: {
        currentPage: 0,
        pageSize: 10,
        params: {
          appId: 0,
          customerId: 0,
          name: "",
          type: 1, //type:1图片，2音频，3视频
          isThumb: 0
        },
      },
      loading:false,
    };
  },
  methods: {
    //打开选择素材的对话框
    open(query) {
      this.resourcesType = query.resourcesType;
      this.queryParams.params.appId = query.appId;
      this.queryParams.params.customerId = query.customerId;
      if (this.resourcesType == "image") {
        this.dialogTitle = "图片素材库";
      }
      if (this.resourcesType == "audio") {
        this.dialogTitle = "音频素材库";
      }
      if (this.resourcesType == "video") {
        this.dialogTitle = "视频素材库";
      }
      this.handleQuery();
      this.visible = true;
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.currentPage = 1;
      this.getList();
    },
    getList() {
      this.loading = true;
      if (this.resourcesType == "image") {
        this.queryParams.params.type = 1;
      }
      if (this.resourcesType == "audio") {
        this.queryParams.params.type = 2;
      }
      if (this.resourcesType == "video") {
        this.queryParams.params.type = 3;
      }
      listResources(this.queryParams).then((response) => {
        this.dataList = response.data.items;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    closeDialog(){
      this.dataList = [];
      this.queryParams = {
        currentPage: 0,
        pageSize: 10,
        params: {
          appId: 0,
          customerId: 0,
          name: "",
          type: 1, //type:1图片，2音频，3视频
          isThumb:0
        },
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$refs.uploader.open({
        customerId:  this.queryParams.params.customerId,
        appId:  this.queryParams.params.appId,
        resourcesType: this.resourcesType,
      });
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
    getAddButtonTitle() {
      if (this.resourcesType == "image") {
        return "上传图片";
      }
      if (this.resourcesType == "audio") {
        return "上传音频";
      }
      if (this.resourcesType == "video") {
        return "上传视频";
      }
    },

    selectResources(item){
      this.$emit('select-success', item)
      this.visible = false;
    }
  },
};
</script>
<style lang="scss" scoped>
.grid-item-wrap {
    border-radius: 10px;
    min-height: 36px;
    background: #fff;
    box-shadow: 0 10px 28px rgb(0 0 0 / 14%);
    margin-top: 20px;
  }
  .grid-item-img{
    width: 100%;
    max-height: 160px;
    object-fit: cover;
  }
</style>
