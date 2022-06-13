<template>
  <div class="app-container">
    <div class="filter-container" >
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
        <el-form-item label="客户" prop="customerId">
          <el-select
            v-model="queryParams.params.customerId"
            placeholder="请选择客户"
            @change="customerListChange"
            
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
        </el-form-item>
        <el-form-item label="应用" prop="appId">
          <el-select
            v-model="queryParams.params.appId"
            placeholder="请选择应用"
            clearable
            size="small"
            @change="onSelectApp"
          >
            <el-option
              v-for="item in applicationList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div style="position: absolute; right: 30px">
          <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="medium"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
          >添加场景</el-button> 
      </div>
    </div>
    <div class="scene-list-wrap">

        <div class="scene-list-item-wrap" v-for="(item,index) in sceneList" :key="index">
            <div class="item-icon-wrap">
                <span class="iconfont icon-gongzuoliuchengtu" style="color:#40a9ff"></span>
            </div>
            <div class="item-content-wrap">
                <div class="item-top-warp">
                    <span style="font-size:16px;flex:1">{{item.name}}</span>
                    <div class="item-op-wrap">
                        <el-button type="primary" icon="el-icon-edit" circle size="mini" @click="handleEdit(item)"></el-button>
                        <el-button type="danger" icon="el-icon-delete" circle size="mini" @click="handleDelete(item)"></el-button>
                    </div>
                </div>
                <div>
                    <span style="font-size:14px">{{(item.updateDt == null || item.updateDt + "" == "") ? item.insertDt:item.updateDt}}</span>
                </div>
            </div>
        </div>

    </div>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchData"/>
  </div>
</template>
<script>
import "./components/scene_map/styles/iconfont.css";
  import {sceneList,deleteScene} from "@/api/scene";
  import { getApplicationList,getApplication } from "@/api/application";
  import { getCustomerList } from "@/api/customer";
  export default {
    data() {
      return {
        // 遮罩层
        loading: false,
        // 总条数
        total: 0,
        queryParams: {
          currentPage: 1,
          pageSize: 20,
          params: {
            appId: null,
            name: null,
          },
        },
        customerList: [],
        applicationList: [],
        sceneList:[]
      };
    },
    created() {
        this.getCustomerListVV();
        this.fetchData()
    },
    methods: {
      fetchData(){
        sceneList(this.queryParams).then(response=>{
          this.sceneList = response.data.items;
          this.total = response.data.total;
        })
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
      onSelectApp(){
          this.queryParams.params.currentPage = 1
          this.fetchData()
      },
      handleAdd(){
          this.$router.push({path: "/scene/edit"});
      },
      handleEdit(item){
          this.$router.push({path: "/scene/edit", query: {id:item.id}});
      },
      handleCopy(item){
          
      },
      handleDelete(item){
          this.$confirm("是否确定删除该场景", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "dangers",
          }).then(() => {
            deleteScene(item.id).then((response) => {
              if (response.code == 200) {
                this.$message({
                  message: "场景删除成功",
                  type: "success",
                });
                this.fetchData();
              }
            });
          }).catch(() => {});
      }

    },
  };
</script>
<style lang="less" scoped>
.el-form-item{
  margin-bottom: 0;
}
.scene-list-wrap{
    margin-top: 20px;
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: start;
}

.scene-list-item-wrap{
    
    display: flex;
    width: 300px;
    margin: 0 10px 5px 0;
    padding: 15px;
    position: relative;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    flex-direction: row;
}
.item-icon-wrap{   
    width: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
}
.item-content-wrap{
    width: 100%;
    display: flex;
    flex-direction: column;
}
.item-top-warp{
    width: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
}
.item-op-wrap{
    display: flex;
    flex-direction: row;
    align-items: right;
}
</style>
