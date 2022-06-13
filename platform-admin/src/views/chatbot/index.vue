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
      <el-form-item label="运营商" prop="carrierId">
        <el-select
          v-model="queryParams.params.carrierId"
          placeholder="请选择运营商"
          clearable
          size="small"
        >
          <el-option
            v-for="item in carrierList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
        >搜索
        </el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
        >重置
        </el-button
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
        >新增
        </el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="chatbotList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column label="机器人名称" align="center" prop="appName"/>
      <el-table-column label="机器人Id" align="center" prop="chatBotId"/>
      <el-table-column label="应用行业" align="center">
        <template slot-scope="scope">
          {{ scope.row.categoryIds|businessTypesName }}
        </template>
      </el-table-column>
      <el-table-column label="客户名称" align="center" prop="customerName"/>
      <el-table-column label="运营商" align="center" prop="carrierName"/>
      <el-table-column label="审核状态" align="center" prop="auditStatus" :formatter="chatbotAuditStatusFormat"/>
      <el-table-column
        label="创建时间"
        align="center"
        prop="insertDt"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.insertDt, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="上线操作" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <div v-if="scope.row.auditStatus>=0">
            <el-switch
              v-model="scope.row.auditStatus"
              :active-value="1"
              :inactive-value="0"
              @change="handleOnlieStatusChange(scope.row)"
              v-permission:EDIT="$route.meta.permission"
            ></el-switch>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="启用状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          ></el-switch>
        </template>
      </el-table-column>

      <el-table-column label="配置开发者" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="gotoDeveloperPage(scope.row)"
            v-permission:AUTH="$route.meta.permission"
          >
            配置开发者
          </el-button>
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
            :disabled="scope.row.auditStatus != 1"
            v-permission:EDIT="$route.meta.permission"
          >修改
          </el-button
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

    <!-- 添加或修改机器人信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" width="820px" top="4px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="160px"
               style="max-height: 740px;overflow-y: scroll;">
        <el-form-item label="选择应用" prop="appId">
          <el-cascader :props="props" v-model="cascaderModel" @change="handCasChange"
                       :disabled="isQueryById" :key="isResouceShow"></el-cascader>
        </el-form-item>
        <el-form-item label="行业类型" prop="categoryIds">
          <el-select v-model="categoryIds" @change="categoryIdsChange" placeholder="行业类型" multiple
                     :disabled="true" style="width:100%">
            <el-option v-for="item in businessTypes" :key="item.type" :value="item.type" :label="item.label"/>
          </el-select>
        </el-form-item>
        <el-form-item label="应用logo" prop="logoUrl">
          <!-- <el-input v-model="form.logoUrl" placeholder="请输入应用logo" /> -->
          <el-image :src="form.logoUrl" :preview-src-list="[form.logoUrl]" style="width:100px;height:100px"></el-image>
        </el-form-item>
        <el-form-item label="运营商id" prop="carrierId">
          <el-radio-group v-model="form.carrierId" @change="radioButtonChange" :disabled="isEdit">
            <el-radio

              v-model="form.carrierId"
              :label="item.id"
              v-for="(item,i) in carrierList"
              :disabled="form.appId==null"
            >{{ item.name }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <div v-if="form.carrierId == 2 || form.carrierId == 3">
          <el-form-item label="Chatbot ID" prop="chatBotId">
            <el-input :disabled="isEdit" v-model="form.chatBotId" placeholder="总长不得超过20位，格式为csp接入码+(扩展码)如：1065988(01)"/>
          </el-form-item>
          <el-form-item label="签名" prop="autograph">
            <el-input v-model="form.autograph"/>
          </el-form-item>
          <el-form-item label="提供者" prop="providerName">
            <el-input v-model="form.providerName"/>
          </el-form-item>
          <el-form-item label="是否显示提供者信息" prop="isShowProvider">
            <!--el-input v-model="form.isShowProvider" :disabled="true" /-->
            <el-checkbox v-model="form.isShowProvider">显示</el-checkbox>
          </el-form-item>
          <el-form-item label="短信端口号" prop="smsCode">
            <el-input :disabled="isEdit" v-model="form.smsCode" placeholder="Chatbot 短信端口号=chatBotId 中(接入号码+自定义号码)"/>
          </el-form-item>
          <el-form-item label="服务条款" prop="tosUrl">
            <el-input v-model="form.tosUrl"/>
          </el-form-item>
          <el-form-item label="Chatbot邮箱" prop="email">
            <el-input v-model="form.email"/>
          </el-form-item>
          <el-form-item label="Chatbot官网" prop="websiteUrl">
            <el-input v-model="form.websiteUrl"/>
          </el-form-item>
          <el-form-item label="服务电话" prop="phoneNum">
            <el-input v-model="form.phoneNum"/>
          </el-form-item>
          <el-form-item label="Chatbot办公地址" prop="address">
            <el-input v-model="form.address"/>
          </el-form-item>
          <el-form-item label="经纬度" prop="longitude">
            <el-input v-model="form.longitude" style="width:100px" placeholder="经度" :disabled="true"/>
            <el-input v-model="form.latitude" style="width:100px" placeholder="纬度" :disabled="true"/>
            <el-button
              class="button-new-tag"
              size="small"
              @click="handAddSugPosition"
            >+ 选择地图位置
            </el-button
            >
          </el-form-item>
          <el-form-item label="ip白名单" prop="whiteIps">
            <el-input v-model="form.whiteIps"/>
          </el-form-item>
        </div>

        <div v-if="form.carrierId == 1  || form.carrierId == 5 ">
          <el-form-item label="Chatbot ID" prop="chatBotId">
            <el-input :disabled="isEdit" v-model="form.chatBotId" placeholder="总长不得超过20位，格式为csp接入码+(扩展码)如：1065988(01)"/>
          </el-form-item>
          <el-form-item label="签名" prop="autograph">
            <el-input v-model="form.autograph"/>
          </el-form-item>
        </div>

        <div v-if="form.carrierId == 4">
          <el-form-item label="Chatbot ID" prop="chatBotId">
            <el-input :disabled="isEdit" v-model="form.chatBotId" placeholder="总长不得超过20位，格式为csp接入码+(扩展码)如：1065988(01)"/>
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="选择地址"
      :visible.sync="addAddressOpen"
      width="820px"
      append-to-body
      :close-on-click-modal="false"
    >
      <div>
        <Position :maker-position.sync="position" @position="loadPosition"/>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="okAddress">确 定</el-button>
        <el-button @click="cancelAddress">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listChatbot,
    getChatbot,
    updateChatbot,
    updateChatbotCm,
    updateChatbotCt,
    chatbotOnlieStatusUpdate,
    chatbotDelete,
    addChatbot,
    editChatbot
  } from "@/api/chatbot";
  import {getApplicationList, getApplication} from "@/api/application";
  import {customerGrades, getCustomerList} from "@/api/customer";
  import {
    dictAppStatus,
    industryType,
    dictChatbotStatus,
    dictChatbotAuditStatus,
  } from "@/assets/initdata";
  import {getCarrierList} from "@/api/carrier";
  import Position from "@/components/map/AMap";
  import {idCard, idCardValidity, phoneNumber, telephoneNumber, emailValue} from "@/utils/validate";

  export default {
    name: "Chatbot",
    components: {
      Position
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
        // 是否为编辑
        isEdit: false,

        // 总条数
        total: 0,
        cspEcNo: '',
        categoryIds: [],
        customerList: [],
        applicationList: [],
        carrierList: [],
        chatbotInfo: {},
        isResouceShow:1,
        // 机器人信息表格数据
        chatbotList: [],
        chatbotStatus: dictChatbotStatus(), //机器人启用状态
        chatbotAuditStatus: dictChatbotAuditStatus(), //机器人审核状态
        isQueryById: false,
        cascaderModel: [],
        appInfo: {},
        position: [121.5273285, 31.21515044],
        addAddressOpen: false,//选择地图
        props: {
          lazy: true,
          lazyLoad(node, resolve) {
            const {level} = node;
            if (level == 0) {
              getCustomerList().then((response) => {
                let _customerList = response.data;
                if (_customerList != null && _customerList.length > 0) {
                  let nodes = [];
                  _customerList.forEach(function (item, i) {
                    nodes[i] = {value: item.id, label: item.name, leaf: false};
                  })
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
                    nodes[i] = {value: item.id, label: item.name, leaf: true};
                  });
                  resolve(nodes);
                }
              });
            }
          }
        },
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        businessTypes: industryType(),
        // 查询参数
        queryParams: {
          currentPage: 1,
          pageSize: 10,
          params: {
            customerId: null,
            appId: null,
            carrierId: null,
          },
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          appId: [
            {required: true, message: "所属应用不能为空", trigger: "blur"},
          ],
          carrierId: [
            {required: true, message: "运营商id不能为空", trigger: "blur"},
          ],
          status: [{required: true, message: "状态不能为空", trigger: "blur"}],
          logicDel: [
            {required: true, message: "删除标志不能为空", trigger: "blur"},
          ],
          insertDt: [
            {required: true, message: "添加时间不能为空", trigger: "blur"},
          ],
          updateDt: [
            {required: true, message: "更新时间不能为空", trigger: "blur"},
          ],
          chatBotId: [
            {required: true, message: "chatbotId不可以为空", trigger: "blur"},
            {min: 4, max: 100, message: '长度在 4 到 100 个字符之间', trigger: 'blur'},
          ],
          autograph: [
            {required: true, message: "签名不可以为空", trigger: "blur"},
          ],
          providerName: [
            {required: true, message: "提供者姓名不可以为空", trigger: "blur"},
          ],
          tosUrl: [
            {required: true, message: "服务条款地址不可以为空", trigger: "blur"},
          ],
          email: [
            {required: true, message: "服务邮箱不可以为空", trigger: "blur"},
            {validator: emailValue, trigger: 'blur'}
          ],
          websiteUrl: [
            {required: true, message: "官网地址不可以为空", trigger: "blur"},
          ],
          phoneNum: [
            {required: true, message: "服务电话不可以为空", trigger: "blur"},
          ],
          address: [
            {required: true, message: "办公地址不可以为空", trigger: "blur"},
          ],
          longitude: [
            {required: true, message: "经纬度不可以为空", trigger: "blur"},
          ],
          whiteIps: [
            {required: true, message: "白名单不可以为空", trigger: "blur"},
          ],
          smsCode: [
            {required: true, message: "短信端口号不可以为空", trigger: "blur"},
          ]

        },
      };
    },
    created() {

      let _customerId = this.$route.query.customerId;
      let _appId = this.$route.query.appId;

      if (_customerId != null && _customerId != "" && _appId != null && _appId != "") {
        this.isQueryById = true;
        this.queryParams.params.customerId = _customerId;
        this.getApplicationListVV();
        this.queryParams.params.appId = _appId;

        this.cascaderModel = [_customerId, _appId];

        this.form.appId = _appId;
        this.form.customerId = _customerId;
      }

      this.getCustomerListVV();
      this.getCarrierListVV();
      this.getList();
    },
    methods: {
      /** 查询机器人信息列表 */
      getList() {
        this.loading = true;
        listChatbot(this.queryParams).then((response) => {
          this.chatbotList = response.data.items;
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
      getApplicationInfo(appId) {
        getApplication(appId).then(response => {
          this.appInfo = response.data;
          this.form.name = this.appInfo.name;

          console.log(this.appInfo);

          this.categoryIds = this.appInfo.categoryIds.split(',').map(Number);
          this.form.logoUrl = this.appInfo.logoUrl;
        })
      },
      customerListChange() {
        this.applicationList = [];
        this.queryParams.params.appId = null;
        this.getApplicationListVV();
      },
      handCasChange() {

        if (this.cascaderModel.length != 2) {
          return;

        }
        this.form.appId = this.cascaderModel[1];
        this.form.customerId = this.cascaderModel[0];

        this.getApplicationInfo(this.form.appId);

        //console.log(this.appInfo);
        //this.form=this.appInfo;
      },
      getCarrierListVV() {
        getCarrierList().then((response) => {
          this.carrierList = response.data;
        });
      },
      getCarrierNameById(id) {
        let _name = "";
        this.carrierList.forEach(function (item, v) {
          if (item.id + "" == id + "") {
            _name = item.name;
          }
        });
        return _name;
      },
      handleStatusChange(row) {
        let text = row.status + "" == "0" ? "停用" : "启用";
        this.$confirm(
          '确认要"' + text + '""' + row.appName + '"机器人吗?',
          "警告",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(function () {
            updateChatbot({id: row.id, status: row.status}).then((response) => {
              //this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          })
          .then(() => {
            //this.msgSuccess(text + "成功");
            this.getList();
          })
          .catch(function () {
            row.status = row.status === "0" ? "0" : "1";
          });
      },
      handleOnlieStatusChange(row) {
        let _that = this;
        let text = row.auditStatus + "" == "0" ? "下线" : "上线";
        _that.$confirm(
          '确认要"' + text + '""' + row.appName + '"机器人吗?',
          "警告",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(function () {
            chatbotOnlieStatusUpdate({id: row.id}).then((response) => {
              console.log("ok!");
              //this.open = false;
              _that.getList();
            });
          })
          .then((res) => {
            console.log("NO!!!!!!");
            //_that.getList();
          })
          .catch(function () {
            row.auditStatus = row.auditStatus === "0" ? "1" : "0";
          });
      },
      chatbotStatusFormat(row, column) {
        return this.selectDictLabel(this.chatbotStatus, row.status);
      },
      chatbotAuditStatusFormat(row, column) {
        return this.selectDictLabel(this.chatbotAuditStatus, row.auditStatus);
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
          carrierId: null,
          appId: this.form.appId,
          customerId: this.form.customerId,
          cspId: null,
          cspEcNo: null,
          chatBotId: null,
          accessNumber: null,
          smsCode: null
        };
        if (!this.isQueryById) {
          this.form.appId = null;
          this.form.customerId = null;
          this.cascaderModel = [];
        }
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.currentPage = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.queryParams = {
          params: {
            customerId: this.isQueryById ? this.queryParams.params.customerId : null,
            appId: this.isQueryById ? this.queryParams.params.appId : null,
            name: null
          }
        }
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
        this.isEdit = false;
        this.reset();
        if (this.isQueryById) {
          this.getApplicationInfo(this.queryParams.params.appId);
        }
        this.open = true;
        this.title = "添加机器人信息";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.isEdit = true;
        this.reset();
        const id = row.id || this.ids;
        getChatbot(id).then((response) => {
          this.cascaderModel = [];
          this.form = {
            ...response.data.application,
            ...response.data.chatbot
          };
          this.form.id = response.data.chatbot.id;
          this.form.carrierId = response.data.chatbot.carrierId;

          this.categoryIds = response.data.application.categoryIds.split(',').map(Number);

          this.cascaderModel[0] = response.data.application.customerId;
          this.cascaderModel[1] = response.data.chatbot.appId;

          this.form.logoUrl = response.data.application.logoUrl;
          this.open = true;
          this.title = "修改机器人信息";

          this.isResouceShow++;
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate((valid) => {
          if (valid) {
            let _data = {
              application: {
                ...this.form,
                id: this.form.appId,
                isShowProvider: this.form.isShowProvider ? 0 : 1,
                chatBotId: this.form.chatBotId
              },
              chatbot: {
                id: this.form.id,
                "appId": this.appInfo.id,
                smsCode: this.form.smsCode,
                'cspEcNo': this.form.cspEcNo,
                "carrierId": this.form.carrierId,
                "chatBotId": this.form.chatBotId,
              }
            };

            if (this.form.id != null) {//修改
              editChatbot(_data).then((response) => {
                this.open = false;
                this.getList();
              })
              /* if(this.getCarrierNameById(this.form.carrierId).indexOf('移动')>=0){
                updateChatbotCm(this.form).then((response)=>{
                  this.open = false;
                  this.getList();
                })
              }else if(this.getCarrierNameById(this.form.carrierId).indexOf('电信')>=0){
                updateChatbotCt(this.form).then((response)=>{
                  this.open = false;
                  this.getList();
                })
              } */
            } else {//新增

              // _data.application.chatBotId=this.form.chatBotId;

              addChatbot(_data).then((response) => {
                this.open = false;
                this.getList();
              })
              /* if(this.getCarrierNameById(this.form.carrierId).indexOf('移动')>=0){
                addChatbotCm(_data).then((response)=>{
                  this.open = false;
                  this.getList();
                })
              }else if(this.getCarrierNameById(this.form.carrierId).indexOf('电信')>=0){
                addChatbotCt(_data).then((response)=>{
                  this.open = false;
                  this.getList();
                })
              } */
            }
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$confirm(
          '是否确认删除机器人信息编号为"' + ids + '"的数据项?',
          "警告",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(function () {
            return chatbotDelete({id: ids});
          })
          .then(() => {
            this.getList();
            //this.msgSuccess("删除成功");
          });
      },
      radioButtonChange(id) {
        //this.form.name=this.appInfo.name;
      },
      getCarrierNameById(id) {
        let _name = '';
        this.carrierList.forEach(function (item, i) {
          if (item.id + '' == id + '') {
            _name = item.name;
          }
        });
        return _name;
      },
      gotoDeveloperPage(row) {
        this.$router.push({path: "/chatbot/developer/index", query: {id: row.id, carrierId: row.carrierId}});
      },
      handAddSugPosition() {
        this.addAddressOpen = true;
      },
      loadPosition(position) {
        this.form.longitude = position.lng.toFixed(3);
        this.form.latitude = position.lat.toFixed(3);
      },
      okAddress() {
        this.addAddressOpen = false;
      },
      cancelAddress() {
        this.addAddressOpen = false;
      },
      categoryIdsChange() {
        console.log(this.categoryIds);
      }
    },
    filters: {
      businessTypesName(value) {
        if (value == null) {
          return "";
        }
        let _btlist = industryType();
        let ids = value.split(",").map(Number);
        let _arry = _btlist.filter(row => value.indexOf(row.type) >= 0);

        let _name = "";
        _arry.forEach((item, i) => {
          _name += i > 0 ? "/" + item.label : item.label;
        })

        return _name;
      }
    }
  };
</script>
