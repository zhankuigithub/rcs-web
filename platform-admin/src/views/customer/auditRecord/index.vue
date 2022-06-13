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
          >新增</el-button
        >
        <span style="color:red">*注意：每个运营商只能新增一个客户，如果你提交多次，以最后一次为准！</span>
      </el-col>

      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="customerAuditRecordList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column label="客户名称" align="center" prop="customerName" />
      <el-table-column label="运营商名称" align="center" prop="carrierName" />
      <el-table-column label="NCSP客户识别码" align="center" prop="cspEcNo" />
      <el-table-column
        label="审核状态"
        align="center"
        prop="status"
        :formatter="customerStatusFormat"
      />
      <el-table-column label="审核内容" align="center" prop="reviewData" />
      <el-table-column
        label="添加时间"
        align="center"
        prop="insertDt"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.insertDt, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
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
        <!-- <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="submitAudit(scope.row)"
            >提交认证</el-button
          >
        </template> -->
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
    >
      <el-tabs v-model="tabsActiveName" @tab-click="handleTabsClick">
        <el-tab-pane label="运营商" name="carrier">运营商</el-tab-pane>
        <el-tab-pane label="客户" name="customer">客户</el-tab-pane>
        <el-tab-pane label="法人" name="legal">法人</el-tab-pane>
        <el-tab-pane label="联系人" name="contacts">联系人</el-tab-pane>
        <el-tab-pane label="合同" name="contract">合同</el-tab-pane>
      </el-tabs>
      <el-form
        v-show="tabsActiveName == 'carrier'"
        ref="carrierForm"
        :model="carrierForm"
        :rules="carrierRules"
        label-width="120px"
      >
        <el-form-item label="运营商" prop="carrierId">
          <el-select
            v-model="carrierForm.carrierId"
            placeholder="请选择运营商"
            clearable
            size="small"
            @change="()=>{$refs.carrierForm.validateField('carrierId')}"
          >
            <el-option
              v-for="item in carrierList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-form
        v-show="tabsActiveName == 'customer'"
        ref="customerForm"
        :model="customerForm"
        :rules="customerRules"
        label-width="120px"
      >
        <el-form-item label="客户名称" prop="name">
          <el-input
            v-model="customerForm.name"
            placeholder="请输入客户名称"
            :disabled="true"
          />
        </el-form-item>
        <el-form-item label="所在地区" prop="name">
          <el-input
            :value="
              customerForm.province +
              '/' +
              customerForm.city +
              '/' +
              customerForm.area
            "
            :disabled="true"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="customerForm.address"
            placeholder="请输入客户详细地址"
            :disabled="true"
          />
        </el-form-item>
        <el-form-item label="营业执照" prop="businessLicense" required>
          <el-image
            style="width: 100px; height: 100px"
            :src="customerForm.businessLicense"
            :preview-src-list="[customerForm.businessLicense]"
          ></el-image>
        </el-form-item>
        <el-form-item label="行业类型" prop="category">
          <el-select
            v-model="customerForm.category"
            placeholder="选择客户所属行业类型"
            style="width: 100%"
            :disabled="true"
          >
            <el-option
              v-for="item in businessTypes"
              :key="item.type"
              :value="item.type"
              :label="item.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="客户简介" prop="details">
          <el-input
            v-model="customerForm.details"
            placeholder="请输入企业简介"
            :disabled="true"
          />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="客户LOGO" prop="logoUrl">
              <file-upload
                ref="fileUpload"
                :config="uploadLo"
                @on-resp="uploadLogoUrl"
                @on-change="handleChangeLogoUrl"
                @on-remove="()=>{customerForm.logoUrl=null;$refs.customerForm.validateField('logoUrl');$forceUpdate()}"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-image
              style="width: 100px; height: 100px"
              :src="customerForm.logoUrl"
              v-show="customerForm.logoUrl"
              :preview-src-list="[customerForm.logoUrl]"
            ></el-image>
          </el-col>
        </el-row>
        <el-form-item label="客户办公电话" prop="phoneNum">
          <el-input
            v-model="customerForm.phoneNum"
            placeholder="请输入办公电话"
          />
        </el-form-item>
        <el-form-item label="客户等级" prop="grade">
          <el-select
            v-model="customerForm.grade"
            placeholder="请选择客户等级"
            clearable
            size="small"
          >
            <el-option
              v-for="item in customerGradeList"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-form
        v-show="tabsActiveName == 'legal'"
        ref="legalPersonForm"
        :model="legalPersonForm"
        :rules="legalPersonRules"
        label-width="120px"
      >
        <el-form-item label="法人" prop="name">
          <el-input
            v-model="legalPersonForm.name"
            placeholder="请输入法人"
            :disabled="true"
          />
        </el-form-item>
        <el-form-item label="法人身份证号" prop="idCardNo">
          <el-input
            v-model="legalPersonForm.idCardNo"
            placeholder="请输入法人身份证号"
            :disabled="true"
          />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="身份证正面">
              <el-image
                style="width: 100px; height: 100px"
                :src="legalPersonForm.idCardPostiveUrl"
                :preview-src-list="[legalPersonForm.idCardPostiveUrl]"
              ></el-image>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证反面">
              <el-image
                style="width: 100px; height: 100px"
                :src="legalPersonForm.idCardNegativeUrl"
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
            :disabled="true"
          />
        </el-form-item>
        <el-form-item label="联系人手机号" prop="phoneNum">
          <el-input
            v-model="contactsForm.phoneNum"
            placeholder="请输入联系人手机号"
            :disabled="true"
          />
        </el-form-item>
        <el-form-item label="联系人邮箱" prop="email">
          <el-input
            v-model="contactsForm.email"
            placeholder="请输入联系人邮箱"
          />
        </el-form-item>
        <el-form-item label="联系人身份证号" prop="idCardNo">
          <el-input
            v-model="contactsForm.idCardNo"
            placeholder="请输入联系人身份证号"
          />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="身份证正面" prop="cardUrlA">
              <file-upload
                ref="fileUploadCardUrlA"
                :config="uploadCa1"
                @on-resp="uploadIdCard1"
                @on-change="handleChangeIdCard1"
                @on-remove="
                  () => {
                    contactsForm.cardUrlA = null;
                    $refs.contactsForm.validateField('cardUrlA');
                    $forceUpdate()
                  }
                "
              />
              <el-image
                style="width: 100px; height: 100px"
                :src="contactsForm.cardUrlA"
                v-show="contactsForm.cardUrlA != null"
                :preview-src-list="[contactsForm.cardUrlA]"
              ></el-image>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证反面" prop="cardUrlB">
              <file-upload
                ref="fileUploadCardUrlB"
                :config="uploadCa2"
                @on-resp="uploadIdCard2"
                @on-change="handleChangeIdCard2"
                @on-remove="
                  () => {
                    contactsForm.cardUrlB = null;
                    $refs.contactsForm.validateField('cardUrlB');
                    $forceUpdate()
                  }
                "
              />
              <el-image
                style="width: 100px; height: 100px"
                :src="contactsForm.cardUrlB"
                v-show="contactsForm.cardUrlB != null"
                :preview-src-list="[contactsForm.cardUrlB]"
              ></el-image>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-form
        v-show="tabsActiveName == 'contract'"
        ref="contractForm"
        :model="contractForm"
        :rules="contractRules"
        label-width="120px"
      >
        <el-form-item label="合同编号" prop="contractNo">
          <el-input
            v-model="contractForm.contractNo"
            placeholder="请输入合同编号"
          />
        </el-form-item>
        <el-form-item label="合同名称" prop="name">
          <el-input v-model="contractForm.name" placeholder="请输入合同名称" />
        </el-form-item>
        <el-form-item label="合同生效日期" prop="effectiveDt">
          <el-date-picker
            v-model="contractForm.effectiveDt"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="合同失效日期" prop="expireDt">
          <el-date-picker
            v-model="contractForm.expireDt"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="合同是否续签" prop="renewStatus">
          <el-select
            v-model="contractForm.renewStatus"
            placeholder="请选择合同是否续签"
            clearable
            size="small"
            @change="renewStatusChange"
          >
            <el-option label="否" :value="2" :key="2" />
            <el-option label="是" :value="1" :key="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="合同续签日期" prop="renewDt" :required="renewDtRequired">
          <el-date-picker
            v-model="contractForm.renewDt"
            type="date"
            placeholder="选择日期"
            @change="renewDtChange"
            value-format="yyyy-MM-dd HH:mm:ss"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="合同附件" prop="accessoryUrl">
          <file-upload
            ref="fileUploadAccessoryUrl"
            :config="uploadAc"
            @on-resp="uploadAccessory"
            @on-change="handleChangeAccessory"
            @on-remove="
              () => {
                contractForm.accessoryUrl = null;
                $refs.contractForm.validateField('accessoryUrl');
              }
            "
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listCustomerAuditRecord,
} from "@/api/customerAuditRecord";
import {
  getCustomerList,
  getCustomer,
  updateCustomer,
  submitAudit
} from "@/api/customer";
import { regionData, CodeToText } from "element-china-area-data";
import {
  customerGrades,
  industryType,
  dictCustomerStatus,
} from "@/assets/initdata";
import FileUpload from "@/components/file/upload";
import { getToken } from "@/utils/auth";
import { getCarrierList } from "@/api/carrier";
import { idCard,idCardValidity,phoneNumber,telephoneNumber,emailValue } from "@/utils/validate";

export default {
  name: "CustomerAuditRecord",
  components: {
    FileUpload,
  },
  data() {
    var validateFileCardUrlA = (rule, value, callback) => {
      if (this.contactsForm.cardUrlA == null) {
        callback(new Error("请上传联系人身份证正面"));
      } else {
        callback();
      }
    };
    var validateFileCardUrlB = (rule, value, callback) => {
      if (this.contactsForm.cardUrlB == null) {
        callback(new Error("请上传联系人身份证反面"));
      } else {
        callback();
      }
    };
    var validateFileAccessoryUrl = (rule, value, callback) => {
      if (this.contractForm.accessoryUrl == null) {
        callback(new Error("请上传合同附件"));
      } else {
        callback();
      }
    };
    var validateRenewDt = (rule, value, callback) => {
      if (this.renewDtRequired&&this.contractForm.renewDt == null) {
        callback(new Error("选择续签日期"));
      } else {
        callback();
      }
    };
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
      // 客户审核记录表格数据
      customerAuditRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      renewDtRequired:false,
      customerList: [],
      carrierList: [],
      isQueryById: false,
      businessTypes: industryType(),
      customerGradeList: customerGrades(),
      tabsActiveName: "carrier",
      // 查询参数
      queryParams: {
        currentPage: 1,
        pageSize: 10,
        params: {
          customerId: null,
          carrierId: null,
        },
      },
      customerStatus: dictCustomerStatus(),
      customerInfo: {},
      uploadLo: {
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
      uploadAc: {
        action: process.env.VUE_APP_PLATFORM_BASE_API + "/manage/customer/uploadFile",
        name: "file",
        multiple: false,
        style: "button",
        files: [],
        accept: "image/png, application/pdf, image/jpeg",
        headers: {
          "ACCESS-TOKEN": getToken(),
        },
      },
      uploadCa1: {
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
      uploadCa2: {
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
      // 表单参数
      //form: {},
      contactsForm: {},
      contractForm: {},
      customerForm: {},
      legalPersonForm: {},
      carrierForm: {},
      // 表单校验
      carrierRules: {
        carrierId: [
          { required: true, message: "请选择运营商", trigger: "blur" },
        ],
      },
      customerRules: {
        name: [
          { required: true, message: "客户名称不能为空", trigger: "blur" },
        ],
        address: [
          { required: true, message: "详细地址不可为空", trigger: "blur" },
        ],
        category: [
          { required: true, message: "行业类型不可以为空", trigger: "blur" },
        ],
        phoneNum:[
          {validator:telephoneNumber,trigger:"blur"}
        ]
      },
      legalPersonRules: {
        name: [
          { required: true, message: "法人姓名不可为空", trigger: "blur" },
        ],
        idCardNo: [
          { required: true, message: "法人身份证号", trigger: "blur" },
          { validator: idCard, trigger: "blur" },
          { validator: idCardValidity, trigger: "blur" },
        ],
      },
      contactsRules: {
        name: [
          { required: true, message: "联系人姓名不可为空", trigger: "blur" },
        ],
        phoneNum: [
          { required: true, message: "联系人手机号不可为空", trigger: "blur" },
          { validator: phoneNumber, trigger: "blur" },
        ],
        idCardNo: [
          {
            required: true,
            message: "联系人身份证号不可为空",
            trigger: "blur",
          },
          { validator: idCard, trigger: "blur" },
          { validator: idCardValidity, trigger: "blur" },
        ],
        cardUrlA: [
          /*
          {
            required: true,
            message: "请上传联系人身份证正面",
            trigger: "blur",
          },*/
          { validator: validateFileCardUrlA, trigger: "blur" },
        ],
        cardUrlB: [
          /*
          {
            required: true,
            message: "请上传联系人身份证反面",
            trigger: "blur",
          },*/
          { validator: validateFileCardUrlB, trigger: "blur" },
        ],
      },
      contractRules: {
        contractNo: [
          { required: true, message: "合同编号不可为空", trigger: "blur" },
        ],
        name: [
          { required: true, message: "合同名称不可为空", trigger: "blur" },
        ],
        effectiveDt: [
          { required: true, message: "合同有效期不可为空", trigger: "blur" },
        ],
        expireDt: [
          { required: true, message: "合同失效期不可为空", trigger: "blur" },
        ],
        renewStatus: [
          { required: true, message: "请选择是否续签", trigger: "blur" },
        ],
        renewDt:[
          {validator:validateRenewDt,trigger:"blur"}
        ],
        accessoryUrl: [
          { required: true, message: "请上传合同附件", trigger: "blur" },
          { validator: validateFileAccessoryUrl, trigger: "blur" },
        ],
      },
    };
  },
  created() {
    //this.customerId = this.$route.query.id+"";
    let _customerId = this.$route.query.id;

    if (_customerId != null && _customerId != "") {
      this.isQueryById = true;
      this.queryParams.params.customerId = _customerId;
    }

    this.getCustomerListVV();
    this.getCarrierListVV();
    this.getList();
  },
  methods: {
    /** 查询客户审核记录列表 */
    getList() {
      this.loading = true;

      listCustomerAuditRecord(this.queryParams).then((response) => {
        this.customerAuditRecordList = response.data.items;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    getCustomerListVV() {
      getCustomerList({ isAll: true }).then((response) => {
        this.customerList = response.data;
      });
    },
    getCarrierListVV() {
      getCarrierList(null).then((response) => {
        this.carrierList = response.data;
      });
    },
    getCustomerInfo() {
      getCustomer(this.queryParams.params.customerId).then((response) => {
        this.customerInfo = {...response.data};

        this.contactsForm = {...this.customerInfo.contacts};

        console.log(this.contactsForm.cardUrl);
        //this.contactsForm.cardUrlA=
        if (this.contactsForm.cardUrl != null) {
          if (this.contactsForm.cardUrl.length > 0) {
            this.contactsForm.cardUrlA = this.contactsForm.cardUrl[0];
            this.uploadCa1.files=[
              {name:"card01.jpg",url:this.contactsForm.cardUrlA}
            ];
          }
          if (this.contactsForm.cardUrl.length > 1) {
            this.contactsForm.cardUrlB = this.contactsForm.cardUrl[1];
            this.uploadCa2.files=[
              {name:"card02.jpg",url:this.contactsForm.cardUrlAB}
            ];
          }
        }

        if (this.customerInfo.contract != null) {
          this.contractForm = this.customerInfo.contract;
          this.uploadAc.files=[
            {name:"ac01.jpg",url:this.contractForm.accessoryUrl}
            ]
        }

        //uploadLo
        if(this.customerInfo.customer!=null){
          this.uploadLo.files=[
            {name:"logo.jpg",url:this.customerInfo.customer.logoUrl}
          ]
        }

        this.customerForm = {...this.customerInfo.customer};
        this.customerForm.category = parseInt(this.customerForm.category);
        this.legalPersonForm = {...this.customerInfo.legalPerson};
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
        cardUrlA: null,
        cardUrlB: null,
      };
      this.contractForm = {
        contractNo: null,
        name: null,
        effectiveDt: null,
        expireDt: null,
        renewStatus: null,
        renewDt: null,
        accessoryUrl: null,
      };
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
      this.resetForm("contactsForm");
      this.resetForm("contractForm");
      this.resetForm("customerForm");
      this.resetForm("legalPersonForm");
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
          carrierId: null,
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
      this.title = "下发运营商审核";

      this.getCustomerInfo();
    },
    /** 提交按钮 */
    submitForm() {
      let _this = this;
      _this.$refs["carrierForm"].validate((valid0) => {
        if (valid0) {
          _this.$refs["customerForm"].validate((valid1) => {
            if (valid1) {
              _this.$refs["legalPersonForm"].validate((valid2) => {
                if (valid2) {
                  _this.$refs["contactsForm"].validate((valid3) => {
                    if (valid3) {
                      //_this.submitData();
                      _this.$refs["contractForm"].validate((valid4) => {
                        if (valid4) {
                          _this.submitData();
                        } else {
                          this.$message({
                            type: "error",
                            message: "合同信息表单校验不通过",
                          });
                          this.tabsActiveName = "contract";
                        }
                      });
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
        } else {
          this.$message({
            type: "error",
            message: "请先选择运营商",
          });
          this.tabsActiveName = "carrier";
        }
      });
    },
    submitData() {
      let _this = this;
      let _contacts = { ...this.contactsForm };
      _contacts.cardUrl = [_contacts.cardUrlA, _contacts.cardUrlB];
      let data  = {
          contacts: _contacts,
          contract: this.contractForm,
          customer: this.customerForm,
          legalPerson: this.legalPersonForm,
       }
      submitAudit(data, this.carrierForm.carrierId).then((response) => {
        this.open = false;
        this.$notify({
          type: "info",
          title: "操作提示",
          message: "认证请求提交成功~",
        });
        setTimeout(this.getList(),3000)
      });
    },
    handleTabsClick(tab, event) {
      console.log(tab, event);
    },
    uploadLogoUrl(resp) {
      if (resp.code !== 200) {
        this.$message({
          type: "error",
          message: "上传失败，请重新上传~",
        });
      } else {
        this.customerForm.logoUrl = resp.data;
        this.$forceUpdate();
      }
    },
    handleChangeLogoUrl(file,fileList){
      if(file.status == 'success'){
        this.customerForm.logoUrl  = file.response.data.url;
        if(fileList.length > 1){
          fileList.splice(0,1);
        }
      }
    },
    uploadAccessory(resp) {
      if (resp.code !== 200) {
        this.$message({
          type: "error",
          message: "上传失败，请重新上传~",
        });
      } else {
        this.contractForm.accessoryUrl = resp.data;
        this.$refs.contractForm.validateField("accessoryUrl");
        this.$forceUpdate();
      }
    },
    handleChangeAccessory(file,fileList){
      if(file.status == 'success'){
        this.contractForm.accessoryUrl  = file.response.data.url;
        if(fileList.length > 1){
          fileList.splice(0,1);
        }
        this.$refs.contractForm.validateField("accessoryUrl");
      }
    },
    uploadIdCard1(resp) {
      if (resp.code !== 200) {
        this.$message({
          type: "error",
          message: "上传失败，请重新上传~",
        });
      } else {
        this.contactsForm.cardUrlA = resp.data;
        this.$refs.contactsForm.validateField("cardUrlA");
        this.$forceUpdate();
      }
    },
    handleChangeIdCard1(file,fileList){
      if(file.status == 'success'){
        this.contactsForm.cardUrlA  = file.response.data.url;
        if(fileList.length > 1){
          fileList.splice(0,1);
        }
        this.$refs.contactsForm.validateField("cardUrlA");
      }
    },
    uploadIdCard2(resp) {
      if (resp.code !== 200) {
        this.$message({
          type: "error",
          message: "上传失败，请重新上传~",
        });
      } else {
        this.contactsForm.cardUrlB = resp.data;
        this.$refs.contactsForm.validateField("cardUrlB");
        this.$forceUpdate();
      }
    },
    handleChangeIdCard2(file,fileList){
      if(file.status == 'success'){
        this.contactsForm.cardUrlB = file.response.data.url;
        if(fileList.length > 1){
          fileList.splice(0,1);
        }
        this.$refs.contactsForm.validateField("cardUrlB");
      }
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
    renewStatusChange() {
      this.$refs.contractForm.validateField("renewStatus");
      if(this.contractForm.renewStatus+''=='1'){
        this.renewDtRequired=true;
      }else{
        this.renewDtRequired=false;
      }
      this.contractForm.renewDt=null;
      this.$refs.contactsForm.validateField("renewDt");
    },
    renewDtChange(){
      this.$refs.contactsForm.validateField("renewDt");
    }
  },
};
</script>
