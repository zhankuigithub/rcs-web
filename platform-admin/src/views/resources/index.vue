<template>
  <div class="app-container">
    <div class="filter-container">
      <div style="padding: 10px">
        应用名称
        <el-cascader
          :props="props"
          :value="selectAppInfo"
          style="width: 240px"
          @change="selectApp"
          size="mini"
        ></el-cascader>
      </div>
    </div>
    <div class="filter-container" style="margin-top: 30px">
      <el-radio-group
        size="medium"
        v-model="resourcesType"
        @change="changeResourcesType"
      >
        <el-radio-button label="card">卡片库</el-radio-button>
        <el-radio-button label="image">图片</el-radio-button>
        <el-radio-button label="video">视频</el-radio-button>
        <el-radio-button label="audio">音频</el-radio-button>
        <el-radio-button label="article">文章</el-radio-button>
        <el-radio-button label="webpage">网页</el-radio-button>
      </el-radio-group>
      <div style="padding: 10px; position: absolute; right: 30px">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
          >{{ getAddButtonTitle() }}</el-button
        >
      </div>
    </div>
    <div class="filter-container" style="margin-top: 10px; font-size: 20px">
      {{ getListTitle() }}
      <el-input
        v-if="resourcesType != 'card'"
        placeholder="请输入名称搜索"
        clearable
        size="small"
        @keyup.enter.native="handleQuery"
        style="width: 200px"
        v-model="queryParams.params.name"
      >
        <el-button slot="append" icon="el-icon-search" @click="handleQuery"></el-button>

      </el-input>
      <el-button v-if="(resourcesType == 'card' || resourcesType == 'image' || resourcesType == 'video' || resourcesType == 'audio') && listType == 'table' && multipleSelection.length > 0" @click="handleMultipleCommitAudit" style="margin-left:20px">批量提交审核</el-button>
      <div v-if="resourcesType == 'card' || resourcesType == 'image' || resourcesType == 'video' || resourcesType == 'audio'" style="padding: 10px; position: absolute; right: 30px">
        <el-button
          size="small"
          :icon="listType == 'table' ? 'el-icon-s-grid' : 'el-icon-s-operation'"
          @click="changeListType"
          type="info"
        ></el-button>
      </div>
    </div>
    <CardGridList
      v-loading="loading"
      v-if="resourcesType == 'card' && listType == 'grid'"
      :dataList="dataList"
      @handleEdit="handleEdit"
      @handleDelete="handleDelete"
    ></CardGridList>
    <CardTableList
      v-loading="loading"
      v-if="resourcesType == 'card' && listType == 'table'"
      :dataList="dataList"
      @handleEdit="handleEdit"
      @handleDelete="handleDelete"
    ></CardTableList>
    <ResourcesGridList
      v-if="(resourcesType == 'image' || resourcesType == 'video' || resourcesType == 'audio') && listType == 'grid'"
      :dataList="dataList"
      :resourcesType="resourcesType"
      @handleCommitAudit="handleCommitAudit"
      @handleDelete="handleDelete"
      @handleCommitPrimaryAudit="handleCommitPrimaryAudit"
      @rebuild="rebuild"
    ></ResourcesGridList>
    <ResourcesTableList
      v-loading="loading"
      v-if="(resourcesType == 'image' || resourcesType == 'video' || resourcesType == 'audio') && listType == 'table'"
      :dataList="dataList"
      :resourcesType="resourcesType"
      @multipleSelectionChanged="multipleSelectionChanged"
      @handleCommitAudit="handleCommitAudit"
      @handleDelete="handleDelete"
    ></ResourcesTableList>
    <ArctileTableList
      v-loading="loading"
      v-if="resourcesType == 'article'"
      :dataList="dataList"
      :resourcesType="resourcesType"
      @handleCommitAudit="handleCommitAudit"
      @handleDelete="handleDelete"
    ></ArctileTableList>
    <WebpageTableList
      v-loading="loading"
      v-if="resourcesType == 'webpage'"
      :dataList="dataList"
      :resourcesType="resourcesType"
      @handleCommitAudit="handleCommitAudit"
      @handleDelete="handleDelete"
    ></WebpageTableList>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
      style="z-index:999999"
    />
    <ResourcesUpload
      ref="uploader"
      @upload-success="handleQuery"
    ></ResourcesUpload>
    <AddWebpage
      ref="ref_webpage"
      @upload-success="handleQuery"
    ></AddWebpage>

    <el-dialog
      title="请选择素材需要提交审核的Chatbot"
      :visible.sync="multipleAuditDialogVisible"
      width="30%"
      center
      :close-on-click-modal="false"
    >
      <el-checkbox-group v-model="selectCarrierIDs">
        <el-checkbox
          v-for="carrier in carrierList"
          :label="carrier.id + ''"
          :key="carrier.id + ''"
          >{{ carrier.name }}</el-checkbox
        >
      </el-checkbox-group>
      <span slot="footer" class="dialog-footer">
        <el-button @click="multipleAuditDialogVisible = false">取 消</el-button>
        <el-button :disabled="selectCarrierIDs.length == 0" v-loading="auditLoading" type="primary" @click="handleMultipleSureCommitAudit">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="请选择本素材需要提交审核的Chatbot"
      :visible.sync="auditDialogVisible"
      width="30%"
      center
      :close-on-click-modal="false"
    >
      <el-checkbox-group v-model="selectCarrierIDs">
        <el-checkbox
          v-for="audit in tempAuditResources.auditRecords"
          :label="audit.carrierId + ''"
          :key="audit.carrierId + ''"
          :disabled="audit.status == 0 || audit.status == 1"
          >{{ audit.carrierName }}</el-checkbox
        >
      </el-checkbox-group>
      <span slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取 消</el-button>
        <el-button :disabled="selectCarrierIDs.length == 0" v-loading="auditLoading" type="primary" @click="handleSureCommitAudit">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="素材初审"
      :visible.sync="auditPrimaryDialogVisible"
      :close-on-click-modal="false"
      width="820px"
      append-to-body
    >
      <el-form
        ref="autitForm"
        :model="autitForm"
        :rules="autitRules"
        label-width="120px"
      >
        <el-form-item label="审核状态" prop="status">
          <el-select
            v-model="autitForm.status"
            placeholder="审核状态"
            clearable
            size="small"
            @change="selectAuditStatus"
          >
            <el-option
              key="1"
              label="已审核通过"
              value="1"
            />
            <el-option
              key="2"
              label="审核未通过"
              value="2"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审核内容" prop="remark">
          <el-input
            v-model="autitForm.remark"
            :placeholder="auditPlaceholder"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleSureCommitPrimaryAudit" v-loading="auditLoading">确定</el-button>
        <el-button @click="auditPrimaryDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCustomerList } from "@/api/customer";
import { listApplication } from "@/api/application";
import {getCarrierList} from "@/api/carrier";
import { listResources, deleteResources,sendToCarrier,auditMaterial,rebuildMaterial } from "@/api/resources";
import { cardDelete, listCard } from "@/api/card";
import AddWebpage from "./components/add-webpage";
import ResourcesUpload from "./components/upload-resources";
import CardGridList from "./components/card-grid-list";
import CardTableList from "./components/card-table-list";
import ResourcesTableList from "./components/resources-table-list";
import ResourcesGridList from "./components/resources-grid-list";
import ArctileTableList from "./components/arctile-list";
import WebpageTableList from "./components/webpage-list";

export default {
  name: "Customer",
  components: {
    AddWebpage,
    ResourcesUpload,
    CardGridList,
    CardTableList,
    ResourcesTableList,
    ResourcesGridList,
    ArctileTableList,
    WebpageTableList

  },
  data() {
    return {
      // 遮罩层
      loading: true,
      resourcesType: "card",
      listType: "grid", //grid table
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
      // 总条数
      total: 0,
      // 资源列表表格数据
      dataList: [],
      queryParams: {
        currentPage: 1,
        pageSize: 10,
        params: {
          appId: 0,
          customerId: 0,
          name: "",
          type: 1, //type:1图片，2音频，3视频
        },
      },
      multipleSelection:[],
      carrierList:[],
      multipleAuditDialogVisible:false,
      auditDialogVisible: false,
      auditPrimaryDialogVisible:false,
      tempAuditResources: {},
      selectCarrierIDs:[],
      auditLoading:false,
      auditPlaceholder: "请输入审核内容",
      autitRules: {
        status: [
          {required: true, message: "请选择审核状态", trigger: "blur"}
        ],
        remark: [
          {required: true, message: "请输入审核内容", trigger: "blur"}
        ]
      },
      autitForm: {}
    };
  },
  created() {
    let _customerId = this.$route.query.customerId;
    let _appId = this.$route.query.appId;
    this.selectAppInfo = [_customerId, _appId];
    this.queryParams.params.customerId = _customerId;
    this.queryParams.params.appId = _appId;
    this.getList();
    this.getCarrierList()
  },
  methods: {
    getCarrierList(){
      getCarrierList().then((response) => {
          this.carrierList = response.data;
        });
    },
    multipleSelectionChanged(val){
      this.multipleSelection = val
    },
    //选择应用
    selectApp(e) {
      this.selectAppInfo = e;
      this.queryParams.params.customerId = e[0];
      this.queryParams.params.appId = e[1];
      this.handleQuery();
    },
    //选择素材类型
    changeResourcesType(e) {
      this.handleQuery();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.dataList = []
      this.queryParams.currentPage = 1;
      this.getList();
    },
    /** 新增按钮操作 */
    handleAdd() {
      if (this.resourcesType == "card") {
        this.$router.push({
          path: "/resources/editCard",
          query: {
            appId: this.selectAppInfo[1],
            customerId: this.selectAppInfo[0],
          },
        });
      }else if (this.resourcesType == "article") {
        this.$router.push({
          path: "/resources/article/edit",
          query: {
            appId: this.selectAppInfo[1],
            customerId: this.selectAppInfo[0],
          },
        });
      }else if (this.resourcesType == "webpage") {

          this.$nextTick(() => {
          this.$refs.ref_webpage.open({
            customerId: this.selectAppInfo[0],
            appId: this.selectAppInfo[1]
          });


        });

      } else {
        this.$nextTick(() => {
          this.$refs.uploader.open({
            customerId: this.selectAppInfo[0],
            appId: this.selectAppInfo[1],
            resourcesType: this.resourcesType,
          });
        });
      }
    },
    getList() {
      this.loading = true;
      if (this.resourcesType == "card") {
        listCard(this.queryParams).then((response) => {
          this.dataList = response.data.items;
          this.total = response.data.total;
          this.loading = false;
        });
      } else {
        if (this.resourcesType == "image") {
          this.queryParams.params.type = 1;
        }
        if (this.resourcesType == "audio") {
          this.queryParams.params.type = 2;
        }
        if (this.resourcesType == "video") {
          this.queryParams.params.type = 3;
        }
        if (this.resourcesType == "article") {
          this.queryParams.params.type = 4;
        }
        if (this.resourcesType == "webpage") {
          this.queryParams.params.type = 5;
        }
        listResources(this.queryParams).then((response) => {
          this.dataList = response.data.items;
          this.total = response.data.total;
          this.loading = false;
        });
      }
    },
    changeListType() {
      if (this.listType == "grid") {
        this.listType = "table";
      } else {
        this.listType = "grid";
      }
    },
    getListTitle() {
      if (this.resourcesType == "card") {
        return "卡片（共" + this.total + "张）";
      }
      if (this.resourcesType == "image") {
        return "图片素材（共" + this.total + "张）";
      }
      if (this.resourcesType == "audio") {
        return "音频素材（共" + this.total + "条）";
      }
      if (this.resourcesType == "video") {
        return "视频素材（共" + this.total + "条）";
      }
      if (this.resourcesType == "article") {
        return "文章（共" + this.total + "篇）";
      }
      if (this.resourcesType == "webpage") {
        return "网页（共" + this.total + "条）";
      }
    },
    getAddButtonTitle() {
      if (this.resourcesType == "card") {
        return "新增卡片";
      }
      if (this.resourcesType == "image") {
        return "上传图片";
      }
      if (this.resourcesType == "audio") {
        return "上传音频";
      }
      if (this.resourcesType == "video") {
        return "上传视频";
      }
      if (this.resourcesType == "article") {
        return "添加文章";
      }
      if (this.resourcesType == "webpage") {
        return "新增网页";
      }
    },
    handleEdit(item) {
      this.$router.push({
        path: "/resources/editCard",
        query: {
          cardId: item.id,
          appId: item.appId,
          customerId: item.customerId,
        },
      });
    },
    handleDelete(item) {
      var msg = "";
      if (this.resourcesType == "card") {
        msg = "点击确定删除该卡片，“" + item.name + "”";
      }
      if (this.resourcesType == "image") {
        msg = "点击确定删除该图片，“" + item.name + "”";
      }
      if (this.resourcesType == "video") {
        msg = "点击确定删除该视频，“" + item.name + "”";
      }
      if (this.resourcesType == "audio") {
        msg = "点击确定删除该音频，“" + item.name + "”";
      }
      if (this.resourcesType == "article") {
        msg = "点击确定删除该文章，“" + item.name + "”";
      }
      if (this.resourcesType == "webpage") {
        msg = "点击确定删除该网页，“" + item.name + "”";
      }
      this.$confirm(msg, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "dangers",
      })
        .then(() => {
          if (this.resourcesType == "card") {
            cardDelete(item.id).then((response) => {
              if (response.code == 200) {
                this.$message({
                  message: "卡片删除成功",
                  type: "success",
                });
                this.handleQuery();
              }
            });
          } else {
            deleteResources(item.id).then((response) => {
              if (response.code == 200) {
                this.$message({
                  message: "素材删除成功",
                  type: "success",
                });
                this.handleQuery();
              }
            });
          }
        })
        .catch(() => {});
    },
    handleMultipleCommitAudit(){
      this.selectCarrierIDs = [];
      this.multipleAuditDialogVisible = true
    },
    handleCommitAudit(item) {
      this.selectCarrierIDs = [];
      this.tempAuditResources = item;
      this.auditDialogVisible = true;
    },
    handleCommitPrimaryAudit(item){
      this.aReset()
      this.tempAuditResources = item;
      this.auditPrimaryDialogVisible = true;
    },
    aReset() {
        this.autitForm = {
          status: null,
          remark: null
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
    handleSureCommitAudit(){
      console.log(JSON.stringify(this.tempAuditResources));
      this.auditLoading = true;
      sendToCarrier({
        "carrierIds": this.selectCarrierIDs,
        "ids": [this.tempAuditResources.id],
        "uploadMode": 2
      }).then((response) => {
        console.log(JSON.stringify(response))
        if(response.code == 200){
          this.$message({
            message: "提交审核成功",
            type: "success",
          });
          this.dataList = []
          this.getList();
        }
        this.auditLoading = false;
        this.auditDialogVisible = false;
      }).catch((err) =>{
        console.log(JSON.stringify(err))
        this.auditLoading = false;
        this.auditDialogVisible = false;
      });
    },
    handleMultipleSureCommitAudit(){
      console.log(JSON.stringify(this.multipleSelection));
      this.auditLoading = true;
      sendToCarrier({
        "carrierIds": this.selectCarrierIDs,
        "ids": this.multipleSelection.map(item => item.id),
        "uploadMode": 1
      }).then((response) => {
        console.log(JSON.stringify(response))
        if(response.code == 200){
          this.$message({
            message: "提交审核成功",
            type: "success",
          });
          this.dataList = []
          this.getList();
        }
        this.auditLoading = false;
        this.multipleSelection = []
        this.multipleAuditDialogVisible = false;
      }).catch((err) =>{
        console.log(JSON.stringify(err))
        this.auditLoading = false;
        this.multipleAuditDialogVisible = false;
      });
    },
    handleSureCommitPrimaryAudit(){
      console.log(JSON.stringify(this.tempAuditResources));
      this.$refs["autitForm"].validate(valid => {
          if (valid) {
            let _data = {...this.autitForm};
            _data.id = this.tempAuditResources.id
            this.auditLoading = true;
            auditMaterial(_data).then((response) => {
              console.log(JSON.stringify(response))
              if(response.code == 200){
                this.$message({
                  message: "审核成功",
                  type: "success",
                });
                this.dataList = []
                this.getList();
              }
              this.auditLoading = false;
              this.auditPrimaryDialogVisible = false;
            }).catch((err) =>{
              console.log(JSON.stringify(err))
              this.auditLoading = false;
              this.auditPrimaryDialogVisible = false;
            });
          }
        });
    },
    rebuild(materialId, carrierId) {
      this.$confirm("确定重新提交此素材？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        rebuildMaterial({"materialId":materialId, "carrierId":carrierId}).then((response) => {
          if(response.code == 200) {
            this.$message({
              message: "重构成功",
              type: "success",
            });
            this.dataList = []
            this.getList();
          }
        });
      }).catch(() => {});
    }
  },
};
</script>
<style lang="less" scoped>
</style>
