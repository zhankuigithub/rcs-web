<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :sm="6" :lg="6">
        客户信息<br />
        <hr />
        <h3>Hi~ {{ customerInfo.customer.name }}</h3>
        {{ customerStatusFormat(customerInfo.customer.status) }}-
        <el-button
              v-if="customerInfo.customer.status+'' == '0'"
              type="text"
              @click="handBaseDetails"
              >去审核</el-button
            >
        <el-tooltip
              v-else-if="customerInfo.customer.status+'' != '0'&&customerInfo.customer.auditContent!=''"
              class="item"
              effect="dark"
              :content="customerInfo.customer.auditContent"
              placement="top-end"
            >
            <el-button
              type="text"
              @click="handBaseDetails"
              >查看详情</el-button
            >
        </el-tooltip>
        <el-button
              v-else
              type="text"
              @click="handBaseDetails"
              >查看详情</el-button
        >
      </el-col>
      <el-col :sm="6" :lg="6">
        <div class="main-bt" @click="handSendMsg">
          <i class="el-icon-message"></i>
          <span>消息发送</span>
        </div>
      </el-col>
      <el-col :sm="6" :lg="6">
        <div class="main-bt" @click="handMaterial">
          <i class="el-icon-picture-outline"></i>
          <span>素材管理</span>
        </div>
      </el-col>
      <el-col :sm="6" :lg="6">
        <div class="main-bt" @click="handTemplate">
          <i class="el-icon-c-scale-to-original"></i>
          <span>模板管理</span>
        </div>
      </el-col>
    </el-row>

    <el-table
      style="margin-top: 20px"
      :data="applicationList"
      header-row-class-name="el-table-header"
      border
    >
      <el-table-column label="应用名称" align="center" prop="name" />
      <el-table-column label="机器人开通" align="center" prop="name">
        <template slot-scope="scope">
          <div class="audit-status-wrap">
            <div
              v-if="scope.row.chatbotItems && scope.row.chatbotItems.length > 0"
            >
              <div
                class="audit-status"
                v-for="(statusItem, index) in scope.row.chatbotItems"
                :key="index"
              >
                {{ statusItem.carrierName + " - "
                }}<span :class="getAuditStatusTextClass(statusItem)">{{
                  getAuditStatusText(statusItem)
                }}</span>
              </div>
            </div>
            <div v-else class="audit-status">未提交</div>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-row :gutter="20">
      <el-col :sm="24" :lg="24"> </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :sm="8" :lg="8">
        <div class="main-tt">
          <span class="mt-title">今日发送总数（条）</span>
          <span class="mt-content">0</span>
        </div>
      </el-col>
      <el-col :sm="8" :lg="8">
        <div class="main-tt">
          <span class="mt-title">今日上行总数（条）</span>
          <span class="mt-content">0</span>
        </div>
      </el-col>
      <el-col :sm="8" :lg="8">
        <div class="main-tt">
          <span class="mt-title">今日上行人数（人）</span>
          <span class="mt-content">0</span>
        </div>
      </el-col>
    </el-row>

    <el-dialog
      title="客户详情"
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
        <el-tab-pane label="合同" name="contract" v-if="customerInfo.contract!=null">合同</el-tab-pane>
      </el-tabs>
      <el-form
        v-show="tabsActiveName == 'customer'"
        ref="customerForm"
        label-width="120px"
      >
        <el-form-item label="客户名称" prop="name">
          {{customerInfo.customer.name}}
        </el-form-item>
        <el-form-item label="所在地区" prop="area">
          {{customerInfo.customer.province}}/{{customerInfo.customer.city}}/{{customerInfo.customer.area}}
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          {{customerInfo.customer.address}}
        </el-form-item>
        <el-form-item label="营业执照" prop="businessLicense">
          <el-image
              style="width: 100px; height: 100px"
              :src="customerInfo.customer.businessLicense"
              :preview-src-list="[customerInfo.customer.businessLicense]"
              ></el-image>
        </el-form-item>

        <el-form-item label="行业类型" prop="category">
          {{customerInfo.customer.category|businessTypesName}}
        </el-form-item>
        <el-form-item label="客户简介" prop="details">
          {{customerInfo.customer.details==null?"":customerInfo.customer.details}}
        </el-form-item>
        <el-form-item label="客户LOGO" prop="logoUrl">
          <el-image
              style="width: 100px; height: 100px"
              :src="customerInfo.customer.logoUrl"
              :preview-src-list="[customerInfo.customer.logoUrl]"
              ></el-image>
        </el-form-item>
      </el-form>
      <el-form
        v-show="tabsActiveName == 'legal'"
        ref="legalPersonForm"
        label-width="120px"
      >
        <el-form-item label="法人" prop="name">
          {{customerInfo.legalPerson.name}}
        </el-form-item>
        <el-form-item label="法人身份证号" prop="idCardNo">
          {{customerInfo.legalPerson.idCardNo}}
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="身份证正面" prop="idCardPostiveUrl">
              <el-image
              style="width: 100px; height: 100px"
              :src="customerInfo.legalPerson.idCardPostiveUrl"
              :preview-src-list="[customerInfo.legalPerson.idCardPostiveUrl]"
              ></el-image>
        </el-form-item>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证反面" prop="idCardNegativeUrl">
              <el-image
              style="width: 100px; height: 100px"
              :src="customerInfo.legalPerson.idCardNegativeUrl"
              :preview-src-list="[customerInfo.legalPerson.idCardNegativeUrl]"
              ></el-image>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-form
        v-show="tabsActiveName == 'contacts'"
        ref="contactsForm"
        label-width="120px"
      >
        <el-form-item label="联系人" prop="name">
          {{customerInfo.contacts.name}}
        </el-form-item>
        <el-form-item label="联系人手机号" prop="phoneNum">
          {{customerInfo.contacts.phoneNum}}
        </el-form-item>
        <el-form-item label="联系人邮箱" prop="email">
          {{customerInfo.contacts.email}}
        </el-form-item>
      </el-form>
      <el-form
        v-show="tabsActiveName == 'contract'"
        ref="contractForm"
        label-width="120px"
        v-if="customerInfo.contract!=null"
      >
        <el-form-item label="合同编号" prop="contractNo">
          {{customerInfo.contract.contractNo}}
        </el-form-item>
        <el-form-item label="合同名称" prop="name">
          {{customerInfo.contract.name}}
        </el-form-item>
        <el-form-item label="合同生效日期" prop="effectiveDt">
          {{customerInfo.contract.effectiveDt}}
        </el-form-item>
        <el-form-item label="合同失效日期" prop="expireDt">
          {{customerInfo.contract.expireDt}}
        </el-form-item>
        <el-form-item label="合同是否续签" prop="renewStatus">
          {{customerInfo.contract.renewStatus+''=='0'?'否':'是'}}
        </el-form-item>
        <el-form-item label="合同续签日期" prop="renewDt">
          {{customerInfo.contract.renewDt}}
        </el-form-item>
        <el-form-item label="合同附件" prop="accessoryUrl">
          <a target="_blank" :href="customerInfo.contract.accessoryUrl">附件，点击下载</a>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="customerInfo.customer.status+'' == '0'" type="success" @click="handFirstStatus"
          >审核</el-button
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
        <el-button @click="aCancel">取消</el-button>
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
import { listApplication } from "@/api/application";
import {
  customerGrades,
  industryType,
  dictCustomerStatus,
} from "@/assets/initdata";
export default {
  name: "customerDetails",
  data() {
    var validateFileAuditStatus=(rule,value,callback)=>{
      if(value+''=='0'){
        callback(new Error('当前为待审状态，请选择通过或者未通过状态才可以提交审核表单'));
      }else{
        callback();
      }
    };
    return {
      open:false,
      customerId: null,
      customerPhoneNum: "",
      customerInfo: {
        contacts: {},
        contract: {},
        customer: {
          name: "",
          phoneNum: "",
          status: null,
        },
        legalPerson: {},
      },
      industryTypeList:industryType(),
      customerStatus: dictCustomerStatus(),
      auditPlaceholder:"请输入审核内容",
      applicationList: [],
      tabsActiveName:"customer",
      customerAutitForm:{},
      aOpen:false,
      customerAutitRules:{
        status: [
          { required: true, message: "认证状态不能为空", trigger: "blur" },
          {validator:validateFileAuditStatus,trigger:'blur'}
        ],
        auditContent: [
          { required: true, message: "请输入审核内容", trigger: "blur" },
        ],
      }
    };
  },
  created() {
    let _customerId = this.$route.query.id;
    if (_customerId != null && _customerId != "") {
      this.customerId = _customerId;
    }

    this.getCustomerInfo();
    this.fetchAppList();
  },
  methods: {
    aCancel() {
      this.aOpen = false;
      this.aReset();
    },
    aReset() {
      this.customerAutitForm = {
        status: null,
        auditContent: null,
      };
      this.resetForm("customerAuditForm");
    },
    getCustomerInfo() {
      if (this.customerId == null) {
        return;
      }
      getCustomer(this.customerId).then((response) => {
        this.customerInfo.contacts = response.data.contacts;
        this.customerInfo.contract = response.data.contract;
        this.customerInfo.customer = response.data.customer;
        this.customerInfo.legalPerson = response.data.legalPerson;

        this.customerAutitForm.status=this.customerInfo.customer.status;
        this.customerAutitForm.auditContent=this.customerInfo.customer.auditContent;
      });
    },
    fetchAppList() {
      listApplication({ params: { customerId: this.customerId } }).then(
        (response) => {
          this.applicationList = response.data.items;
        }
      );
    },
    customerStatusFormat(status) {
      return this.selectDictLabel(this.customerStatus, status);
    },
    handBaseDetails() {
      //console.log(this.customerId);
      if(this.customerId==null){
        return;
      }

      getCustomer(this.customerId).then((response) => {
        this.customerInfo.contacts = response.data.contacts;
        this.customerInfo.contract = response.data.contract;
        this.customerInfo.customer = response.data.customer;
        this.customerInfo.legalPerson = response.data.legalPerson;

        this.customerAutitForm.status=this.customerInfo.customer.status;
        this.customerAutitForm.auditContent=this.customerInfo.customer.auditContent;

        this.open=true;
      });
    },
    handSendMsg() {
      this.$router.push({
        path: "/send/index",
        query: { id: this.customerId },
      });
    },
    handMaterial() {
      this.$router.push({
        path: "/resources/index",
        query: { id: this.customerId },
      });
    },
    handTemplate() {
      this.$router.push({
        path: "/template/index",
        query: { id: this.customerId },
      });
    },
    getAuditStatusText(audit) {
      if (audit.status == 0) {
        return "待审核";
      }
      if (audit.status == 1) {
        return "已审核";
      }
      if (audit.status == 2) {
        return "未通过";
      }
      return "未提交";
    },
    getAuditStatusTextClass(audit) {
      if (audit.status == 0) {
        return "waitting";
      }
      if (audit.status == 1) {
        return "audit";
      }
      if (audit.status == 2) {
        return "reject";
      }
      return "";
    },
    handleTabsClick(tab, event){

    },
    handFirstStatus(){
      this.aReset();
      this.aOpen = true;

      //this.customerAutitForm.status = this.customerInfo.customer.status + "";
      this.customerAutitForm.status = this.customerForm.status + '' !== '0' ? this.customerForm.status + '' : '';
      this.customerAutitForm.auditContent = this.customerInfo.customer.auditContent;

      this.selectAuditStatus()
    },
    selectAuditStatus(){
      this.customerAutitRules.auditContent[0].required = this.customerAutitForm.status + '' == '2';

      if(this.customerAutitForm.status+''=='1'){
        this.customerAutitForm.auditContent="已审核通过";
      }else if(this.customerAutitForm.status+''=='2'){
        this.customerAutitForm.auditContent="";
        this.auditPlaceholder="请输入不通过原因";
      }else{
        this.auditPlaceholder="请输入审核内容";
      }
      
      //this.$refs.customerAuditForm.clearValidate();

    },
    cancel(){
      this.open=false;
    },
    aSubmitForm() {
      let _this =this;
      _this.$refs["customerAutitForm"].validate((valid) => {
        if(valid){
          let _data= _this.customerInfo;
          _data.customer.status = _this.customerAutitForm.status;
          _data.customer.auditContent = _this.customerAutitForm.auditContent;
          
          updateCustomer(_data).then((response) => {
            _this.aOpen = false;
            _this.open = false;
          });
        }
      });
      
    },
    
  },
  filters:{
    businessTypesName(value){
      
      if(value==null){
        return;
      }

      let _btlist=industryType();

      let _arry = _btlist.filter(row => value.indexOf(row.type) >= 0  );

      let _name="";
      _arry.forEach((item,i)=>{
        _name+=i>0?"/"+item.label:item.label;
      })

      return _name;
    }
  }
  
};
</script>
<style lang="scss" scoped>
.main-bt {
  text-align: center;
  font-size: 16px;
  height: 120px;
  padding-top: 40px;
  cursor: pointer;
}
.main-bt i {
  display: block;
  font-size: 60px;
  color: cornflowerblue;
}

.main-tt {
  text-align: center;
  padding: 50px 0px;
}
.main-tt span {
  display: block;
  margin-top: 20px;
}
.main-tt .mt-title {
  font-size: 14px;
}
.main-tt .mt-content {
  font-size: 24px;
}

table th,
table td {
  text-align: center;
}
.audit-status-wrap {
  display: flex;
  padding: 20px;
  flex-direction: row;
  justify-content: center;
  .audit-status {
    color: #333;
    font: 14px;
    .waitting {
      color: #ff6600;
    }
    .audit {
      color: #008000;
    }
    .reject {
      color: #ff0000;
    }
  }
}
</style>