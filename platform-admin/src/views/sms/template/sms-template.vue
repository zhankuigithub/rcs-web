<template>
  <div class="app-container">
    <div class="filter-container" >
      <el-form class="form" :model="params" :inline="true" label-width="100px" style="width:calc(100% - 180px);display: flex;flex-wrap: wrap;">
        <el-form-item label="客户端：" prop="clientId">
          <el-select
            v-model="params.params.clientId"
            placeholder="请选择客户端："
            clearable
            size="mini"
          >
            <el-option
              v-for="item in clientList"
              :key="item.clientId"
              :label="item.clientDesc"
              :value="item.clientId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="模板编号：" prop="templateId">
            <el-input v-model="params.params.templateId" size="mini" placeholder="模板编号" clearable></el-input>
        </el-form-item>
        <el-form-item label="模板名称：" prop="templateName">
            <el-input v-model="params.params.templateName" size="mini" placeholder="模板名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="模板内容：" prop="templateContent">
            <el-input v-model="params.params.templateContent" size="mini" placeholder="模板内容" clearable></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="fetchList">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQueryParams">重置</el-button>
        </el-form-item>
      </el-form>
      <div style="padding: 10px; position: absolute; right: 30px">
          <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
          >添加短信模板</el-button>
      </div>
    </div>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:20px">
      <el-table-column label="模板编号" align="left" prop="templateId" width="100"/>
      <el-table-column label="客户端编号" align="left" prop="clientId" width="100"/>
      <el-table-column label="模板名称" align="left" prop="templateName"/>
      <el-table-column label="短信模板" align="left" prop="templateContent"/>
      <el-table-column label="短链接配置" align="left" prop="web_flag" >
        <template slot-scope="scope">
          {{scope.row.webFlag == 1?'使用':'不使用'}}
        </template>
      </el-table-column>
      <el-table-column label="网页标题" align="left" prop="webTitle"/>
      <el-table-column label="网页模板" align="left" prop="webHtml" width="320px">
        <template slot-scope="scope">
           <iframe class="web-content" :srcdoc="scope.row.webHtml" frameborder="0"></iframe>
        </template>
      </el-table-column>
<!--      <el-table-column label="网页文件" align="left" prop="htmlFile" >-->
<!--        <template slot-scope="scope">-->
<!--            <a :href="'https://sms.langma.cn/preview/' + scope.row.htmlFile + '/' + scope.row.clientId">{{scope.row.htmlFile}}</a>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="操作" align="left" class-name="small-padding fixed-width" >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleEdit(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >编辑
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDel(scope.row)"
            v-permission:EDIT="$route.meta.permission"
            style="color:red"
          >
          删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="params.currentPage"
      :limit.sync="params.pageSize"
      @pagination="fetchList"
    />
  </div>
</template>

<script>
  import {
    getClientList,
    getSmsTemplateList,
    deleteSmsTemplate
  } from '@/api/sms/template'
import Editor from "@/components/editor/editor-tinymce";
  export default {
    name: 'SmsTemplateManage',
    components: {
        Editor
    },
    data() {
      return {
        // 遮罩层
        loading: false,
        // 总条数
        total: 0,
        params: {
            "currentPage": 1,
            "pageSize": 10,
            "params": {
                "clientId": '',
                "opId": '',
                "templateContent": "",
                "templateId": '',
                "templateName": ""
            }
        },
        clientList:[],
        dataList: []
      };
    },
    created(){
      this.fetchList()
      getClientList().then(res=>{
          this.clientList = res.data
      })
    },
    watch:{
        '$route':"fetchList" //需要处理数据的方法
    },
    methods: {
      fetchList(){
        this.loading = true;
        getSmsTemplateList(this.params).then(res=>{
          this.dataList = res.data.items;
          this.total = res.data.total;
          this.loading = false;
        })
      },
      resetQueryParams(){
          this.params = {
                "currentPage": 1,
                "pageSize": 10,
                "params": {
                    "clientId": '',
                    "opId": '',
                    "templateContent": "",
                    "templateId": '',
                    "templateName": ""
                }
            }
            this.fetchList()
      },
      handleAdd(){
        this.$router.push({
          path: "/sms/template/sms-template-editor",
          query: {},
        });
      },
      handleEdit(item){
        this.$router.push({
          path: "/sms/template/sms-template-editor",
          query: {templateId:item.templateId},
        });
      },
      handleDel(item){
          this.$confirm('是否确认删除模板编号为"' + item.templateId + '"的消息模板?', "警告", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(function () {
                return deleteSmsTemplate(item.templateId);
            }).then(() => {
                this.fetchList();
                this.msgSuccess("删除成功");
            })
      },
      submitForm(){
        this.$refs["form"].validate(valid => {
          if (valid) {
            if(this.op_type == 'add'){
              this.submitLoading = true
              addClient(this.editForm).then(res=>{
                this.submitLoading = false
                this.addClientDialogOpen = false
                this.fetchList()
              })
            }else{
              this.submitLoading = true
              updateClient(this.editForm).then(res=>{
                this.submitLoading = false
                this.addClientDialogOpen = false
                this.fetchList()
              })
            }
          }
        });

      }
    }
  }
</script>

<style lang="less" scoped>
.cell {
    -webkit-line-clamp: 1;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 23px;
    padding-right: 10px;
}
.web-content{
  width: 320px;
  height:200px
}

</style>
