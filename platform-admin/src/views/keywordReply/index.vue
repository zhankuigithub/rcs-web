<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="客户" prop="customerId">
        <el-select
          v-model="queryParams.params.customerId"
          placeholder="请选择客户"
          @change="customerListChange"
          :disabled="isQueryById"
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
          :disabled="isQueryById"
          clearable
          size="small"
        >
          <el-option
            v-for="item in applicationList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="规则名称" prop="name">
        <el-input
          v-model="queryParams.params.name"
          placeholder="请输入规则名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
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
          >新增</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="keywordReplyList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column label="规则名称" align="center" prop="name" />
      <el-table-column label="关键字" align="center" prop="ruleContent" />
      <el-table-column
        label="匹配模式"
        align="center"
        prop="type"
        :formatter="replyTypeFormat"
      />
      <el-table-column label="所属客户" align="center" prop="customerName" />
      <el-table-column label="所属应用" align="center" prop="appName" />
      <el-table-column label="回复模板" align="center" prop="messageName" />
      <el-table-column
        label="模板类型"
        align="center"
        prop="messageType"
        :formatter="messageTypeFormat"
      />
      <el-table-column
        label="更新时间"
        align="center"
        prop="updateDt"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateDt, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-permission:EDIT="$route.meta.permission"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-permission:RM="$route.meta.permission"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改关键字回复对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="860px"
      :close-on-click-modal="false"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="选择应用" prop="appId">
              <el-cascader
                :props="props"
                v-model="cascaderModel"
                @change="handCasChange"
                :disabled="isQueryById || operType == operTypeEmun.edit"
              ></el-cascader>
            </el-form-item>
            <el-form-item label="规则名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入配置名称" />
            </el-form-item>
            <el-form-item label="匹配模式" prop="type">
              <el-select
                v-model="form.type"
                placeholder="匹配模式"
                style="width: 100%"
              >
                <el-option
                  v-for="item in replyTypeList"
                  :key="item.dictValue"
                  :value="item.dictValue"
                  :label="item.dictLabel"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="关键字" prop="ruleContent">
              <el-input
                v-model="form.ruleContent"
                placeholder="请输入关键字或者正则表达式"
              />
            </el-form-item>
            <el-form-item label="回复内容" prop="replyId">
              <el-select
                v-model="form.replyId"
                @change="onReplyChange"
                placeholder="选择回复的模板"
                style="width: 100%"
              >
                <el-option
                  v-for="item in messageTemplateList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.name"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="排序权重" prop="weight">
              <el-input
                v-model="form.weight"
                placeholder="请输入排序权重，数字越大越靠前"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitForm">确 定</el-button>
              <el-button @click="cancel">取 消</el-button>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <MessageTemplatePreview
                v-if="teamplate"
                :appId="teamplate.appId"
                :messageType="teamplate.type"
                :textMsg="teamplate.payload"
                :floatingButtons="teamplate.suggestions != undefined ? teamplate.suggestions.map(item => item.displayText):[]"
                :cards="teamplate.cards"
                :mediaResources="teamplate.materials != undefined && teamplate.materials.length > 0 ?teamplate.materials[teamplate.materials.length - 1]:{}"
                :position="teamplate.payload">
            </MessageTemplatePreview>
          </el-col>
        </el-row>
      </el-form>
      <!-- <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div> -->
    </el-dialog>
  </div>
</template>

<script>
import {
  listKeywordReply,
  getKeywordReply,
  delKeywordReply,
  addKeywordReply,
  updateKeywordReply,
  exportKeywordReply,
} from "@/api/keywordReply";
import { dictReplayType, dictMessageType } from "@/assets/initdata";
import { getApplicationList } from "@/api/application";
import { getCustomerList } from "@/api/customer";
import { queryMessageTemplateList,getMessageTemplate } from "@/api/messageTemplate";
import MessageTemplatePreview from "@/components/MsgTempPreview";

export default {
  name: "KeywordReply",
  components: {
    MessageTemplatePreview
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 关键字回复表格数据
      keywordReplyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      operType: 1,
      operTypeEmun: {
        add: 1,
        edit: 2,
      },
      teamplate:{},
      customerList: [],
      applicationList: [],
      isQueryById: false,
      replyTypeList: dictReplayType(),
      messageTypeList: dictMessageType(),
      messageTemplateList: [],
      cascaderModel: [],
      // 查询参数
      queryParams: {
        currentPage: 1,
        pageSize: 10,
        params: {
          customerId: null,
          appId: null,
          name: null,
        },
      },
      props: {
        lazy: true,
        lazyLoad(node, resolve) {
          const { level } = node;
          if (level == 0) {
            getCustomerList().then((response) => {
              let _customerList = response.data;
              if (_customerList != null && _customerList.length > 0) {
                let nodes = [];
                _customerList.forEach(function (item, i) {
                  nodes[i] = { value: item.id, label: item.name, leaf: false };
                });
                resolve(nodes);
              }
            });
          } else {
            getApplicationList({
              customerId: node.value,
              isAll: true,
            }).then((response) => {
              let _applicationList = response.data;
              if (_applicationList != null && _applicationList.length > 0) {
                let nodes = [];
                _applicationList.forEach(function (item, i) {
                  nodes[i] = { value: item.id, label: item.name, leaf: true };
                });
                resolve(nodes);
              }
            });
          }
        },
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        appId: [
          { required: true, message: "所属应用不能为空", trigger: "blur" },
        ],
        name: [
          { required: true, message: "配置名称不能为空", trigger: "blur" },
        ],
        type: [
          { required: true, message: "匹配类型不能为空", trigger: "change" },
        ],
        replyId: [
          { required: true, message: "回复内容不能为空", trigger: "blur" },
        ],
        ruleContent: [
          { required: true, message: "关键字不可为空", trigger: "blur" },
        ],
        weight: [
          { required: true, message: "排序权重不可为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    let _customerId = this.$route.query.customerId;
    let _appId = this.$route.query.appId;

    if (
      _customerId != null &&
      _customerId != "" &&
      _appId != null &&
      _appId != ""
    ) {
      this.isQueryById = true;

      this.queryParams.params.customerId = _customerId;
      this.queryParams.params.appId = _appId;

      this.cascaderModel[0] = _customerId;
      this.cascaderModel[1] = _appId;

      this.form.appId = _appId;
    }

    this.getCustomerListVV();
    this.getList();
  },
  methods: {
    /** 查询关键字回复列表 */
    getList() {
      this.loading = true;
      listKeywordReply(this.queryParams).then((response) => {
        this.keywordReplyList = response.data.items;
        this.total = response.data.total;
        this.loading = false;
      });
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
    replyTypeFormat(row, column) {
      return this.selectDictLabel(this.replyTypeList, row.type);
    },
    messageTypeFormat(row, column) {
      return this.selectDictLabel(this.messageTypeList, row.messageType);
    },
    getMessageTemplateList() {
      this.messageTemplateList = [];

      if (this.form.appId == null) {
        return;
      }

      queryMessageTemplateList({ appId: this.form.appId }).then((res) => {
        this.messageTemplateList = res.data;
      });
    },
    handCasChange() {
      if (this.cascaderModel.length != 2) {
        return;
      }
      this.form.appId = this.cascaderModel[1];
      this.getMessageTemplateList();
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        appId: this.form.appId == null ? null : this.form.appId,
        name: null,
        type: null,
        ruleContent: null,
        replyId: null,
        status: 0,
        weight: 99,
        logicDel: null,
        insertDt: null,
        updateDt: null,
      };
      if (!this.isQueryById) {
        this.form.appId = null;
        this.cascaderModel = [];
      }
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        params: {
          customerId: this.isQueryById
            ? this.queryParams.params.customerId
            : null,
          appId: this.isQueryById ? this.queryParams.params.appId : null,
          name: null,
        },
      };
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加关键字回复";
      this.operType = this.operTypeEmun.add;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getKeywordReply(id).then((response) => {
        this.form = response.data;
        this.form.type = this.form.type + "";
        this.form.replyId = this.form.replyId;

        if(this.form.replyId!=null){
          getMessageTemplate(this.form.replyId).then((res)=>{
            console.log(res);
            this.teamplate=res.data;
          });
        }

        this.cascaderModel = [response.data.customerId, response.data.appId];

        this.getMessageTemplateList();

        this.open = true;
        this.title = "修改关键字回复";
        this.operType = this.operTypeEmun.edit;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateKeywordReply(this.form).then((response) => {
              this.open = false;
              this.getList();
            });
          } else {
            addKeywordReply(this.form).then((response) => {
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm(
        '是否确认删除关键字回复编号为"' + ids + '"的数据项?',
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(function () {
          return delKeywordReply(ids);
        })
        .then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        });
    },
    onReplyChange(){
      this.teamplate={};
      if(this.form.replyId==null){
        return;
      }
      getMessageTemplate(this.form.replyId).then((response)=>{
        console.log(response);
        this.teamplate=response.data;
      })
    }
  },
};
</script>
