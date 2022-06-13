<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
        <el-form-item label="应用名称" prop="name">
            <el-cascader
                :props="props"
                :value="selectAppInfo"
                style="width: 240px"
                @change="selectApp"
                size="mini">
            </el-cascader>
        </el-form-item>
      <el-form-item label="模板名称" prop="name">
        <el-input
          v-model="queryParams.params.name"
          placeholder="请输入模板名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模板类型" prop="type">
        <el-select v-model="queryParams.params.type" placeholder="请选择模板类型" clearable size="small">
          <el-option
            v-for="dict in messageTypeList"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
        >新增</el-button>
      </el-col>
      <right-toolbar @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="messageTemplateList">
      <el-table-column label="模板名称" align="center" prop="name" />
      <el-table-column label="模板类型" align="center" prop="type" :formatter="messageTypeFormat"  />
      <el-table-column label="所属应用" align="center" prop="appName" />
      <el-table-column label="素材审核状态" align="center">
        <template slot-scope="scope">
          <div v-for="(item,i) in scope.row.auditRecords" :key="i">
            <el-button
              size="mini"
              type="text"
            >{{item.carrier_name}}-{{autitStatusFormat(item)}}</el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="短信回落内容" align="center" prop="smsContent" />

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-permission:RM="$route.meta.permission"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { pageDynamicMessageTemplate, deleteDynamicMessageTemplate } from "@/api/messageTemplate";
import { dictMessageType,dictMessageTemplateMaterialAuditStatus} from '@/assets/initdata';
import { listApplication } from "@/api/application";
import { getCustomerList } from "@/api/customer";

export default {
  name: "MessageTemplate",
  data() {
    return {
      // 遮罩层
      loading: true,
     
      // 总条数
      total: 0,
      // 消息模板表格数据
      messageTemplateList: [],
      messageTypeList:dictMessageType(),
      autitStatusList:dictMessageTemplateMaterialAuditStatus(),
      props: {
        //级联选择器数据  客户名称  应用名称
        lazy: true,
        lazyLoad(node, resolve) {
          const { level } = node;
          console.log("node" + level);
          if (level == 0) {
            getCustomerList().then((response) => {
              var costomeList = response.data.map((item) => ({
                value: item.id,
                label: item.name,
                leaf: level >= 1,
              }));
              resolve(costomeList);
            });
          }
          if (level == 1) {
            listApplication({ params: { customerId: node.value } }).then(
              (response) => {
                var applicationList = response.data.items.map((item) => ({
                  value: item.id,
                  label: item.name,
                  leaf: true,
                }));
                resolve(applicationList);
              }
            );
          }
        },
      },
      selectAppInfo: [],
      // 查询参数
      queryParams: {
        currentPage: 0,
        pageSize: 10,
        params: {
          customerId: null,
          appId: null,
          name: null,
        },
      },
      
    };
  },
  created() {
    this.getList();
  },
  methods: {
      //选择应用
    selectApp(e) {
      this.selectAppInfo = e;
      this.queryParams.params.customerId = e[0];
      this.queryParams.params.appId = e[1];
      this.handleQuery();
    },
    /** 查询消息模板列表 */
    getList() {
      this.loading = true;
      pageDynamicMessageTemplate(this.queryParams).then(response => {
        this.messageTemplateList = response.data.items;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    messageTypeFormat(row,column){
      return this.selectDictLabel(this.messageTypeList,row.type);
    },
    autitStatusFormat(row,column){
      return this.selectDictLabel(this.autitStatusList,row.status)
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams={
        params:{
          customerId:null,
          appId:null,
          name:null,
          type:null
        }
      }
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      ///template/edit
      this.$router.push({path: '/template/dynamic/edit'});
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.$router.push({path: '/template/dynamic/edit',query:{template_id:row.id}});
    },
    /** 删除按钮操作 */
    handleDelete(row) {
        this.$confirm('是否确认删除模板名称为"' + row.name + '"的动态模板?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return deleteDynamicMessageTemplate(row.id);
        }).then(() => {
          this.getList();
        })
    }
  }
};
</script>
