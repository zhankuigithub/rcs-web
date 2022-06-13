<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="客户名称" prop="name">
        <el-input
          v-model="queryParams.params.name"
          placeholder="请输入客户名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="首次认证" prop="status">
        <el-select
          v-model="queryParams.params.status"
          placeholder="首次认证状态"
          clearable
          size="small"
        >
          <el-option
            v-for="(dict,index) in customerStatus"
            :key="index"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
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
      :data="customerList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="客户名称" align="center" prop="name"/>
      <el-table-column label="客户所在地" align="center">
        <template slot-scope="scope">
          {{scope.row.province}}/{{scope.row.city}}/{{scope.row.area}}
        </template>
      </el-table-column>
      <el-table-column label="首次认证" align="center">
        <template slot-scope="scope">
          <el-tooltip
            v-if="scope.row.status+'' != '0'&&scope.row.auditContent!=''"
            class="item"
            effect="dark"
            :content="scope.row.auditContent"
            placement="top-end"
          >
            <el-button
              size="mini"
              type="text"
              @click="handleStatus(scope.row)"
            >
              {{
              customerStatusFormat(scope.row, null)
              }}
            </el-button>
          </el-tooltip>
          <el-button
            size="mini"
            type="text"
            @click="handleStatus(scope.row)"
            v-else
          >
            {{
            customerStatusFormat(scope.row, null)
            }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="运营商认证状态" align="center">
        <template slot-scope="scope">
          <div v-for="(item, index) in scope.row.auditRecordList">
            <el-button
              size="mini"
              type="text"
              v-if="item.status != 2"
              @click="gotoAuditPage(scope.row)"
            >
              {{ item.carrierName }}-{{ customerStatusFormat(item, null) }}
            </el-button>
            <el-tooltip
              v-if="item.status == 2"
              class="item"
              effect="dark"
              :content="item.reviewData"
              placement="top-end"
            >
              <el-button
                size="mini"
                type="text"
                @click="gotoAuditPage(scope.row)"
              >
                {{ item.carrierName }}-{{
                customerStatusFormat(item, null)
                }}
              </el-button>
            </el-tooltip>
          </div>
          <div
            v-if="
              scope.row.auditRecordList == null ||
              scope.row.auditRecordList.length == 0
            "
          >
            <el-button
              size="mini"
              type="text"
              :disabled="scope.row.status != 1"
              @click="gotoAuditPage(scope.row)"

            >
              未认证
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="应用名称" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            v-if="scope.row.appList.length === 0"
            @click="gotoAppPage(scope.row)"
          >
            未创建
          </el-button>
          <el-button
            size="mini"
            type="text"
            v-for="(app,index) in scope.row.appList"
            :key="index"
            v-if="scope.row.appList.length !== 0"
            @click="gotoAppPage(scope.row,app.name)"
          >
            {{ app.name}} ({{auditStatus[app.status]}})
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
            icon="el-icon-search"
            @click="handleShowDetails(scope.row)"
            v-permission:VIEW="$route.meta.permission"
          >查看
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            :disabled="scope.row.status+''=='0'"
            v-permission:EDIT="$route.meta.permission"
          >修改
          </el-button>
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

    <!-- 添加或修改客户信息对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      :close-on-click-modal="false"
      width="820px"
      append-to-body
      top="4px"
    >
      <el-tabs v-model="tabsActiveName" @tab-click="handleTabsClick">
        <el-tab-pane label="客户" name="customer">客户</el-tab-pane>
        <el-tab-pane label="法人" name="legal">法人</el-tab-pane>
        <el-tab-pane label="联系人" name="contacts">联系人</el-tab-pane>
      </el-tabs>
      <el-form
        v-show="tabsActiveName == 'customer'"
        ref="customerForm"
        :model="customerForm"
        :rules="customerRules"
        label-width="120px"
      >
        <el-form-item label="客户名称" prop="name">
          <el-input v-model="customerForm.name" placeholder="请输入客户名称" :disabled="operType==operTypeEnum.audit"/>
        </el-form-item>
        <el-form-item label="所在地区" prop="area">
          <el-cascader
            size="large"
            :options="options"
            v-model="selectedOptions"
            @change="handleChange"
            style="width: 100%"
            :disabled="operType==operTypeEnum.audit"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="customerForm.address"
            placeholder="请输入客户详细地址"
            :disabled="operType==operTypeEnum.audit"
          />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="营业执照" prop="businessLicense" required>
              <file-upload v-if="operType!=operTypeEnum.audit"
                           ref="fileUploadLicense"
                           :config="uploadLic"
                           @on-resp="uploadLicense"
                           @on-change="handleChangeLicense"
                           @on-remove="()=>{customerForm.businessLicense=null;$refs.customerForm.validateField('businessLicense');$forceUpdate()}"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-image
              style="width: 100px; height: 100px"
              :src="customerForm.businessLicense"
              v-if="customerForm.businessLicense!=null"
              :preview-src-list="[customerForm.businessLicense]"
            ></el-image>
          </el-col>
        </el-row>
        <el-form-item label="行业类型" prop="category">
          <el-select
            v-model="customerForm.category"
            placeholder="选择客户所属行业类型"
            style="width: 100%"
            :disabled="operType==operTypeEnum.audit"
            @change="categoryChange"
          >
            <el-option
              v-for="(item,index) in businessTypes"
              :key="index"
              :value="item.type"
              :label="item.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="客户简介" prop="details">
          <el-input
            v-model="customerForm.details"
            placeholder="请输入企业简介"
            :disabled="operType==operTypeEnum.audit"
          />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="客户LOGO" prop="logoUrl">
              <file-upload
                ref="fileUpload"
                :config="uploadLg"
                @on-resp="uploadLogo"
                v-if="operType!=operTypeEnum.audit"
                @on-change="handleChangeLogo"
                @on-remove="()=>{customerForm.logoUrl=null;$refs.customerForm.validateField('logoUrl');$forceUpdate()}"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-image
              style="width: 100px; height: 100px"
              :src="customerForm.logoUrl"
              v-if="customerForm.logoUrl!=null"
              :preview-src-list="[customerForm.logoUrl]"
            ></el-image>
          </el-col>
        </el-row>

      </el-form>
      <el-form
        v-show="tabsActiveName == 'legal'"
        ref="legalPersonForm"
        :model="legalPersonForm"
        :rules="legalPersonRules"
        label-width="120px"
      >
        <el-form-item label="法人" prop="name">
          <el-input v-model="legalPersonForm.name" placeholder="请输入法人" :disabled="operType==operTypeEnum.audit"/>
        </el-form-item>
        <el-form-item label="法人身份证号" prop="idCardNo">
          <el-input
            v-model="legalPersonForm.idCardNo"
            placeholder="请输入法人身份证号"
            :disabled="operType==operTypeEnum.audit"
          />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="身份证正面" prop="idCardPostiveUrl">
              <file-upload
                ref="fileUploadIdCard1"
                :config="uploadCard1"
                @on-resp="uploadIdCard1"
                @on-change="handleChangeIdCard1"
                @on-remove="()=>{legalPersonForm.idCardPostiveUrl=null;$refs.legalPersonForm.validateField('idCardPostiveUrl');$forceUpdate()}"
                v-if="operType!=operTypeEnum.audit"
              />
              <el-image
                style="width: 100px; height: 100px"
                :src="legalPersonForm.idCardPostiveUrl"
                v-if="legalPersonForm.idCardPostiveUrl!=null"
                :preview-src-list="[legalPersonForm.idCardPostiveUrl]"
              ></el-image>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证反面" prop="idCardNegativeUrl">
              <file-upload
                ref="fileUploadIdCard2"
                :config="uploadCard2"
                @on-resp="uploadIdCard2"
                @on-change="handleChangeIdCard2"
                @on-remove="()=>{legalPersonForm.idCardNegativeUrl=null;$refs.legalPersonForm.validateField('idCardNegativeUrl');$forceUpdate()}"
                v-if="operType!=operTypeEnum.audit"
              />
              <el-image
                style="width: 100px; height: 100px"
                :src="legalPersonForm.idCardNegativeUrl"
                v-if="legalPersonForm.idCardNegativeUrl!=null"
                :preview-src-list="[legalPersonForm.idCardNegativeUrl]"
              ></el-image>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-form
        v-show="tabsActiveName == 'contacts'"
        ref="contactsForm"
        :model="contactsForm"
        :rules="contactsRules"
        label-width="120px"
      >
        <el-form-item label="联系人" prop="name">
          <el-input
            v-model="contactsForm.name"
            placeholder="请输入联系人名称"
            :disabled="operType==operTypeEnum.audit"
          />
        </el-form-item>
        <el-form-item label="联系人手机号" prop="phoneNum">
          <el-input
            v-model="contactsForm.phoneNum"
            placeholder="请输入联系人手机号"
            :disabled="operType==operTypeEnum.audit"
          />
        </el-form-item>
        <el-form-item label="联系人邮箱" prop="email">
          <el-input
            v-model="contactsForm.email"
            placeholder="请输入联系人邮箱"
            :disabled="operType==operTypeEnum.audit"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" v-if="operType!=operTypeEnum.audit" @click="submitForm">确 定</el-button>
        <el-button v-if="operType==operTypeEnum.audit&&customerForm.status+''=='0'" type="success"
                   @click="handFirstStatus"
        >审核
        </el-button
        >
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="初次审核"
      :visible.sync="aOpen"
      :close-on-click-modal="false"
      width="820px"
      append-to-body
    >
      <el-form
        ref="customerAutitForm"
        :model="customerAutitForm"
        :rules="customerAutitRules"
        label-width="120px"
      >
        <el-form-item label="认证状态" prop="status">
          <el-select
            v-model="customerAutitForm.status"
            placeholder="认证状态"
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
        <el-form-item label="审核内容" prop="auditContent">
          <el-input
            v-model="customerAutitForm.auditContent"
            :placeholder="auditPlaceholder"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="aSubmitForm">确定</el-button>
        <el-button @click="aCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listCustomer,
    getCustomer,
    delCustomer,
    addCustomer,
    updateCustomer,
    exportCustomer,
  } from "@/api/customer";
  import {regionData, CodeToText, TextToCode} from "element-china-area-data";
  import {
    customerGrades,
    industryType,
    dictCustomerStatus,
  } from "@/assets/initdata";
  import FileUpload from "@/components/file/upload";
  import {getToken} from "@/utils/auth";
  import {idCard, idCardValidity, phoneNumber, emailValue} from "@/utils/validate";

  export default {
    name: "Customer",
    components: {
      FileUpload,
    },
    data() {
      var validateFileBusinessLicense = (rule, value, callback) => {
        if (this.customerForm.businessLicense == null) {
          callback(new Error('请上传营业执照'));
        } else {
          callback();
        }
      };
      var validateFileIdCardPostiveUrl = (rule, value, callback) => {
        if (this.legalPersonForm.idCardPostiveUrl == null) {
          callback(new Error('请上传法人身份证正面'));
        } else {
          callback();
        }
      };
      var validateFileIdCardNegativeUrl = (rule, value, callback) => {
        if (this.legalPersonForm.idCardNegativeUrl == null) {
          callback(new Error('请上传法人身份证反面'));
        } else {
          callback();
        }
      };
      var validateFileAuditStatus = (rule, value, callback) => {
        if (value + '' == '0') {
          callback(new Error('当前为待审状态，请选择通过或者未通过状态才可以提交审核表单'));
        } else {
          callback();
        }
      };
      return {
        auditStatus: {0: '?', 1: '√', 2: '×'},
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
        // 客户信息表格数据
        customerList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        aOpen: false,
        operType: 1, //操作类型 1新增客户 2初审客户 3修改
        operTypeEnum: {
          add: 1,
          audit: 2,
          edit: 3
        },
        auditPlaceholder: "请输入审核内容",
        // 客户基本信息
        customerBaseInfo: {},
        options: regionData,
        selectedOptions: [],
        businessTypes: industryType(),
        ecGrades: customerGrades(),
        customerStatus: dictCustomerStatus(),
        tabsActiveName: "customer",

        // 查询参数
        queryParams: {
          currentPage: 1,
          pageSize: 10,
          params: {
            name: null,
            status: null,
          },
        },
        uploadLic: {
          action: process.env.VUE_APP_PLATFORM_BASE_API + "/manage/customer/uploadFile",
          name: "file",
          multiple: false,
          style: "button",
          files: [],
          accept: "image/png, image/jpeg",
          headers: {
            "ACCESS-TOKEN": getToken(),
          },
        },
        uploadLg: {
          action: process.env.VUE_APP_PLATFORM_BASE_API + "/manage/customer/uploadFile",
          name: "file",
          multiple: false,
          style: "button",
          files: [],
          accept: "image/png, image/jpeg",
          headers: {
            "ACCESS-TOKEN": getToken(),
          }
        },
        uploadCard1: {
          action: process.env.VUE_APP_PLATFORM_BASE_API + "/manage/customer/uploadFile",
          name: "file",
          multiple: false,
          style: "button",
          files: [],
          accept: "image/png, image/jpeg",
          headers: {
            "ACCESS-TOKEN": getToken(),
          }
        },
        uploadCard2: {
          action: process.env.VUE_APP_PLATFORM_BASE_API + "/manage/customer/uploadFile",
          name: "file",
          multiple: false,
          style: "button",
          files: [],
          accept: "image/png, image/jpeg",
          headers: {
            "ACCESS-TOKEN": getToken(),
          }
        },
        // 表单参数
        contactsForm: {},
        contractForm: {},
        customerForm: {},
        legalPersonForm: {},

        customerAutitForm: {},
        // 表单校验
        customerRules: {
          name: [
            {required: true, message: "客户名称不能为空", trigger: "blur"},
          ],
          address: [
            {required: true, message: "详细地址不可为空", trigger: "blur"},
          ],
          category: [
            {required: true, message: "行业类型不可以为空", trigger: "blur"},
          ],
          area: [
            {required: true, message: "请选择客户所在地区", trigger: "blur"},
          ],
          businessLicense: [
            //{ required: true, message: "请上传营业执照照片", trigger: "blur" },
            //validateFileBusinessLicense
            {validator: validateFileBusinessLicense, trigger: 'change'}
          ]
        },
        contactsRules: {
          name: [
            {required: true, message: "联系人姓名不可为空", trigger: "blur"},
          ],
          phoneNum: [
            {required: true, message: "联系人手机号不可为空", trigger: "blur"},
            {validator: phoneNumber, trigger: 'blur'}
          ],
          email: [
            {validator: emailValue, trigger: 'blur'}
          ]
        },
        contractRules: {},
        legalPersonRules: {
          name: [{required: true, message: "状态不能为空", trigger: "blur"}],
          idCardNo: [
            {required: true, message: "法人身份证号", trigger: "blur"},
            {validator: idCard, trigger: 'blur'},
            {validator: idCardValidity, trigger: 'blur'}
          ],
          idCardPostiveUrl: [
            //{ required: true, message: "请上传法人身份证正面照", trigger: "blur" },
            {validator: validateFileIdCardPostiveUrl, trigger: 'blur'}
          ],
          idCardNegativeUrl: [
            //{ required: true, message: "请上传法人身份证反面照", trigger: "blur" },
            {validator: validateFileIdCardNegativeUrl, trigger: 'blur'}
          ]
        },
        customerAutitRules: {
          status: [
            {required: true, message: "认证状态不能为空", trigger: "blur"},
            {validator: validateFileAuditStatus, trigger: 'blur'}
          ],
          auditContent: [
            {required: false, message: "请输入审核内容", trigger: "blur"},
          ],
        },
      };
    },
    created() {
      console.log(this.queryParams);
      this.getList();
    },
    methods: {
      /** 查询客户信息列表 */
      getList() {
        this.loading = true;
        listCustomer(this.queryParams).then((response) => {
          this.customerList = response.data.items;
          this.total = response.data.total;

          this.loading = false;
        });
      },
      customerStatusFormat(row, column) {
        return this.selectDictLabel(this.customerStatus, row.status);
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      aCancel() {
        this.aOpen = false;
        this.aReset();
      },
      // 表单重置
      reset() {
        this.contactsForm = {
          name: null,
          province: null,
          city: null,
          area: null,
          address: null,
          businessLicense: null,
          category: null,
          details: null,
          logoUrl: null,
        };
        this.selectedOptions = [];
        this.contractForm = {};
        this.customerForm = {
          name: null,
          idCardNo: null,
          idCardPostiveUrl: null,
          idCardNegativeUrl: null,
        };
        this.legalPersonForm = {
          name: null,
          phoneNum: null,
          email: null,
        };

        this.uploadLic.files = [];
        this.uploadLg.files = [];
        this.uploadCard1.files = [];
        this.uploadCard2.files = [];

        this.resetForm("contactsForm");
        this.resetForm("contractForm");
        this.resetForm("customerForm");
        this.resetForm("legalPersonForm");
      },

      aReset() {
        this.customerAutitForm = {
          status: null,
          auditContent: null,
        };
        this.resetForm("customerAuditForm");
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
            name: null,
            status: null
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
        this.reset();
        this.open = true;
        this.title = "添加客户信息";

        this.operType = 1;
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        getCustomer(row.id).then((response) => {
          this.contactsForm = response.data.contacts;
          this.contractForm = response.data.contract;
          this.customerForm = response.data.customer;
          this.legalPersonForm = response.data.legalPerson;

          if (this.customerForm.businessLicense != null && this.customerForm.businessLicense != "") {
            this.uploadLic.files = [
              {name: "license-01.jpg", url: this.customerForm.businessLicense}
            ];
          }

          this.customerForm.category = parseInt(this.customerForm.category);

          if (this.customerForm.logoUrl != null && this.customerForm.logoUrl != "") {
            this.uploadLg.files = [
              {name: "logo-01.jpg", url: this.customerForm.logoUrl}
            ]
          }

          if (this.legalPersonForm.idCardPostiveUrl != null && this.legalPersonForm.idCardPostiveUrl != "") {
            this.uploadCard1.files = [
              {name: "postive-01.jpg", url: this.legalPersonForm.idCardPostiveUrl}
            ]
          }

          if (this.legalPersonForm.idCardNegativeUrl != null && this.legalPersonForm.idCardNegativeUrl != "") {
            this.uploadCard2.files = [
              {name: "negative-01.jpg", url: this.legalPersonForm.idCardNegativeUrl}
            ]
          }

          //console.log(TextToCode(response.data.province));
          //this.selectedOptions=[TextToCode(response.data.province),TextToCode(response.data.city),TextToCode(response.data.area)],
          if (response.data.customer.province != null
            && response.data.customer.city != null
            && response.data.customer.area) {
            this.selectedOptions = [
              TextToCode[response.data.customer.province].code,
              TextToCode[response.data.customer.province][response.data.customer.city].code,
              TextToCode[response.data.customer.province][response.data.customer.city][response.data.customer.area].code
            ];

            this.customerForm.province = response.data.customer.province;
            this.customerForm.city = response.data.customer.city;
            this.customerForm.area = response.data.customer.area;
          }

          console.log(this.selectedOptions);

          this.open = true;
          this.title = "修改客户信息";
          this.operType = this.operTypeEnum.edit;

        });
      },

      handleStatus(row) {
        this.reset();
        //console.log(row);
        getCustomer(row.id).then((response) => {
          //console.log(response);
          this.contactsForm = response.data.contacts;
          this.contractForm = response.data.contract;

          this.customerForm = response.data.customer;
          this.customerForm.category = parseInt(this.customerForm.category);

          this.legalPersonForm = response.data.legalPerson;

          if (response.data.customer.province != null
            && response.data.customer.city != null
            && response.data.customer.area) {
            this.selectedOptions = [
              TextToCode[response.data.customer.province].code,
              TextToCode[response.data.customer.province][response.data.customer.city].code,
              TextToCode[response.data.customer.province][response.data.customer.city][response.data.customer.area].code
            ];

            this.customerForm.province = response.data.customer.province;
            this.customerForm.city = response.data.customer.city;
            this.customerForm.area = response.data.customer.area;
          }


          this.open = true;
          this.title = "客户户基本信息";

          this.operType = this.operTypeEnum.audit;
        });
      },
      handleChange(value) {
        console.log(value.length);
        if (value.length !== 3) {
          this.$message({
            type: "error",
            message: "请选择企业所在地",
          });
          return;
        }
        this.customerForm.province = CodeToText[value[0]];
        this.customerForm.city = CodeToText[value[1]];
        this.customerForm.area = CodeToText[value[2]];
      },
      /** 提交按钮 */
      submitForm() {
        let _this = this;
        _this.$refs["customerForm"].validate((valid1) => {
          if (valid1) {
            _this.$refs["legalPersonForm"].validate((valid2) => {
              if (valid2) {
                _this.$refs["contactsForm"].validate((valid3) => {
                  if (valid3) {
                    _this.submitData();
                  } else {
                    this.$message({
                      type: "error",
                      message: "联系人信息表单校验不通过",
                    });
                    this.tabsActiveName = "contacts";
                  }
                });
              } else {
                this.$message({
                  type: "error",
                  message: "法人信息表单校验不通过",
                });
                this.tabsActiveName = "legal";
              }
            });
          } else {
            this.$message({
              type: "error",
              message: "客户信息表单校验不通过",
            });
            this.tabsActiveName = "customer";
          }
        });
      },
      submitData() {
        if (this.customerForm.id != null) {
          let _customer = {...this.customerForm};
          _customer.status = 0;
          updateCustomer({
            contacts: this.contactsForm,
            contract: this.contractForm,
            customer: _customer,
            legalPerson: this.legalPersonForm,
          }).then((response) => {
            this.open = false;
            this.getList();
          });
        } else {
          addCustomer({
            contacts: this.contactsForm,
            contract: this.contractForm,
            customer: this.customerForm,
            legalPerson: this.legalPersonForm,
          }).then((response) => {
            //this.msgSuccess("新增成功");
            this.open = false;
            this.getList();
          });
        }
      },
      aSubmitForm() {
        this.$refs.customerAutitForm.validate(vali => {
          if (vali) {
            this.customerForm.status = this.customerAutitForm.status;
            this.customerForm.auditContent = this.customerAutitForm.auditContent;
            updateCustomer({
              contacts: this.contactsForm,
              contract: this.contractForm,
              customer: this.customerForm,
              legalPerson: this.legalPersonForm,
            }).then((response) => {
              this.open = false;
              this.aOpen = false;
              this.getList();
            });
          }
        })
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$confirm(
          '是否确认删除客户信息编号为"' + ids + '"的数据项?',
          "警告",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(function () {
            return delCustomer(ids);
          })
          .then(() => {
            this.getList();
            this.msgSuccess("删除成功");
          });
      },
      uploadLogo(resp) {
        if (resp.code !== 200) {
          this.$message({
            type: "error",
            message: "上传失败，请重新上传~",
          });
        } else {
          this.customerForm.logoUrl = resp.data;
          console.log(this.customerForm.logoUrl);
          this.$forceUpdate();
        }
      },
      handleChangeLogo(file, fileList) {
        if (file.status == 'success') {
          this.customerForm.logoUrl = file.response.data.url;
          if (fileList.length > 1) {
            fileList.splice(0, 1);
          }
        }
      },
      uploadLicense(resp) {
        if (resp.code !== 200) {
          this.$message({
            type: "error",
            message: "上传失败，请重新上传~",
          });
        } else {
          this.customerForm.businessLicense = resp.data;
          this.$refs.customerForm.validateField('businessLicense');
          this.$forceUpdate();
        }
      },
      handleChangeLicense(file, fileList) {
        if (file.status == 'success') {
          this.customerForm.businessLicense = file.response.data.url;
          if (fileList.length > 1) {
            fileList.splice(0, 1);
          }
          this.$refs.customerForm.validateField('businessLicense');
        }
      },
      uploadIdCard1(resp) {
        if (resp.code !== 200) {
          this.$message({
            type: "error",
            message: "上传失败，请重新上传~",
          });
        } else {
          this.legalPersonForm.idCardPostiveUrl = resp.data;
          this.$refs.legalPersonForm.validateField('idCardPostiveUrl');
          this.$forceUpdate();
        }
      },
      handleChangeIdCard1(file, fileList) {
        if (file.status == 'success') {
          this.legalPersonForm.idCardPostiveUrl = file.response.data.url;
          if (fileList.length > 1) {
            fileList.splice(0, 1);
          }
          this.$refs.legalPersonForm.validateField('idCardPostiveUrl');
        }
      },
      uploadIdCard2(resp) {
        if (resp.code !== 200) {
          this.$message({
            type: "error",
            message: "上传失败，请重新上传~",
          });
        } else {
          this.legalPersonForm.idCardNegativeUrl = resp.data;
          this.$refs.legalPersonForm.validateField('idCardNegativeUrl');
          this.$forceUpdate();
        }
      },
      handleChangeIdCard2(file, fileList) {
        if (file.status == 'success') {
          this.legalPersonForm.idCardNegativeUrl = file.response.data.url;
          if (fileList.length > 1) {
            fileList.splice(0, 1);
          }
          this.$refs.legalPersonForm.validateField('idCardNegativeUrl');
        }
      },
      handleShowDetails(row) {
        this.$router.push({path: "/customer/details", query: {id: row.id}});
      },
      gotoAuditPage(row) {
        this.$router.push({
          path: "/customer/audit/record",
          query: {id: row.id},
        });
      },
      gotoAppPage(row, appName = null) {
        if (appName == null && appName != "") {
          this.$router.push({path: "/app/index", query: {id: row.id}});
        } else {
          this.$router.push({path: "/app/index", query: {id: row.id, appName: appName}});
        }
      },
      handFirstStatus() {
        this.aReset();
        this.aOpen = true;

        this.customerAutitForm.status = this.customerForm.status + '' !== '0' ? this.customerForm.status + '' : '';
        this.customerAutitForm.auditContent = this.customerForm.auditContent;
        this.selectAuditStatus()
      },
      selectAuditStatus() {
        this.customerAutitRules.auditContent[0].required = this.customerAutitForm.status + '' == '2';

        if (this.customerAutitForm.status + '' == '1') {
          this.customerAutitForm.auditContent = "已审核通过";
        } else if (this.customerAutitForm.status + '' == '2') {
          this.customerAutitForm.auditContent = "";
          this.auditPlaceholder = "请输入不通过原因";
        } else {
          this.auditPlaceholder = "请输入审核内容";
        }

        //this.$refs.customerAuditForm.clearValidate();
        //this.$refs.customerAuditForm.validate();

      },
      handleTabsClick(tab, event) {
        console.log(tab, event);
      },
      removeUploadFile(file, fileList, type) {
        console.log(type);
      },
      categoryChange() {
        this.$refs.customerForm.validateField('category');
      }
    },
  };
</script>
