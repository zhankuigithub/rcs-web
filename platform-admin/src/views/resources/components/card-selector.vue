<template>
  <el-dialog :title="dialogTitle" :visible.sync="visible" :close-on-click-modal="false" @close="closeDialog" width="60%">
    <div class="filter-container">
      <div style="padding: 10px">
        <el-input
          placeholder="请输入卡片名称"
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
    </div>
    <div class="content-wrap">
      <CardGridList :dataList="dataList" isSelectCard="true" @select-success="selectCard"></CardGridList>
    </div>
   <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </el-dialog>
</template>
<script>
import { listCard } from "@/api/card";
import CardGridList from "./card-grid-list";
export default {
  name: "CardSelector",
  components:{
    CardGridList
  },
  data() {
    return {
      dialogTitle: "选择卡片",
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
          name: ""
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
      listCard(this.queryParams).then((response) => {
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
          name: ""
        },
      }
    },

    selectCard(item){
      console.log('22323')
      this.$emit('select-success', item)
      this.visible = false;
    }
  },
};
</script>
