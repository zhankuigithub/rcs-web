<template>
  <section class="main-section">
    <aside class="left-task-form">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="选择客户" prop="customerId">
              <el-select v-model="ruleForm.customerId" :disabled="sendType === 'reply'" placeholder="请选择客户" @change="switchApp" style="width: 100%;">
                <el-option v-for="item in customerList" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选择应用" prop="appId">
              <el-select v-model="ruleForm.appId" :disabled="sendType === 'reply' || ruleForm.customerId == null || ruleForm.customerId == ''"
                         placeholder="请选择应用" clearable @change="loadAppTeamplate" style="width: 100%;">
                <el-option v-for="item in applicationList" :key="item.id" :label="item.name"
                           :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="选择模板" prop="templateId">
          <el-select v-model="ruleForm.templateId" placeholder="请选择发送消息模板" clearable @change="loadTeamplateDetail"
                     :disabled="ruleForm.appId == null || ruleForm.appId == ''" style="width: 100%;">
            <el-option v-for="item in msgTeamplateList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <span v-for="item in teamplate.auditRecords" :key="item.id">
            {{ item.carrierName }} -
            <span :class="getAuditStatusTextClass(item)" style="margin-right: 10px">
            {{ auditStatus[item.status] }}
            </span>
          </span>
        </el-form-item>
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="ruleForm.name" placeholder="请输入本批次发送号码的任务名称" maxlength="30"></el-input>
        </el-form-item>
        <el-form-item label="发送号码" prop="phoneNumStr" >
          <el-radio v-model="inputPhoneType" label="1" :disabled="sendType === 'reply'">手动输入</el-radio>
          <el-radio v-model="inputPhoneType" label="2" @change="loadLabel" :disabled="sendType === 'reply'">通讯录</el-radio>
          <el-radio v-model="inputPhoneType" label="3" @change="openExcel"  :disabled="sendType === 'reply'">文件导入</el-radio>
          <input v-if="inputPhoneType === '3'" id="files" style="display:none" type="file" accept=".xlsx" @change="selectExcelFile"/>
          <button v-if="inputPhoneType === '3'" @click="selectFile">选择文件</button>
          <el-input :disabled="sendType === 'reply'" type="textarea" :rows="5" placeholder="手动输入号码，每行一个号码"
                    v-model="ruleForm.phoneNumStr"
                    @change="buildPhones"></el-input>

          <p v-if="noPhone.length !== 0" style="color: red; height: 10px; font-size: 12px;margin: 0;padding: 0;">
            号码{{noPhone}}格式错误</p>
        </el-form-item>
        <el-form-item label="发送时间" prop="scheduled">
          <input type="hidden" v-model="ruleForm.scheduled"/>
          <el-radio v-model="sendTimeType" label="1">立即发送</el-radio>
          <el-radio v-model="sendTimeType" label="2">定时发送</el-radio>
        </el-form-item>
        <el-form-item label="" v-if="sendTimeType+''=='2'" prop="sendDt">
                <el-date-picker
                  v-model="ruleForm.sendDt"
                  type="datetime"
                  placeholder="选择日期时间"
                  align="right"
                  :picker-options="pickerOptions">
                </el-date-picker>
              </el-form-item>
        <el-form-item label="运营商" prop="carrierIds">
          <el-select v-model="ruleForm.carrierIds" :disabled="sendType == 'reply'" placeholder="请选择运营商" clearable multiple style="width: 100%;">
            <el-option v-for="item in carrierList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" :rows="5" v-model="ruleForm.remark" placeholder="请填写备注" maxlength="100"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm')">发 送</el-button>
        </el-form-item>
      </el-form>
    </aside>
    <aside class="right-card-views">
      <MessageTemplatePreview
        v-if="teamplate"
        :appId="teamplate.appId"
        :messageType="teamplate.type"
        :textMsg="teamplate.payload"
        :floatingButtons="teamplate.suggestions != undefined ? teamplate.suggestions.map(item => item.displayText):[]"
        :cards="teamplate.cards"
        :mediaResources="teamplate.materials != undefined && teamplate.materials.length > 0 ?teamplate.materials[teamplate.materials.length - 1]:{}"
        :position="teamplate.payload"
        :smsContent="teamplate.smsContent"
        :backType="teamplate.backType">
      </MessageTemplatePreview>
    </aside>

    <el-dialog title="通讯录" :visible.sync="addClientDialogOpen"
               :close-on-click-modal="false" width="20%" append-to-body
    >

      <el-table :row-style="{height:'0px'}"
                :cell-style="{padding:'0px'}"  :data="labelList" stripe border
                @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="标签名称" align="center" prop="name"/>
        <el-table-column label="号码数量" align="center" prop="cnt"/>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmPhones">确 定</el-button>
        <el-button @click="addClientDialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </section>
</template>

<script>
  import {
    getCarrierList
  } from "@/api/carrier";
  import {
    getApplicationList
  } from "@/api/application";
  import {
    getCustomerList
  } from "@/api/customer";
  import {
    pushMessage
  } from '@/api/sendTask'
  import {
    itemsMessageTemplate,
    getMessageTemplate
  } from '@/api/messageTemplate'
  import MessageTemplatePreview from "@/components/MsgTempPreview";
  import {parseTime} from '../../utils/ruoyi';

  import {
    getPhonesByLabel,
    readExcel
  } from '@/api/phoneBook'

  import {
    groupLabels
  } from '@/api/labels'

  export default {
    components: {
      MessageTemplatePreview
    },
    data() {
      return {
        addClientDialogOpen: false,
        openExcelDialog: false,
        labelList: [],
        auditStatus: {0: '待审核', 1: '已审核', 2: '未通过', 3: '未提交'},
        noPhone: [],
        inputPhoneType: '1',
        sendTimeType: '1',
        carrierList: [],
        customerList: [],
        applicationList: [],
        msgTeamplateList: [],
        sendType:'',
        ruleForm: {},
        rules: {
          customerId: [{
            required: true,
            message: "请选择客户",
            trigger: "blur"
          }],
          appId: [{
            required: true,
            message: "请选择应用",
            trigger: "blur"
          }],
          carrierIds: [{
            required: true,
            message: "请选择运营商",
            trigger: "blur"
          }],
          templateId: [{
            required: true,
            message: "请选择消息模板",
            trigger: "blur"
          }],
          name: [{
            required: true,
            message: "请填写任务名称",
            trigger: "blur"
          }],
          // phoneNumStr: [{
          //   required: true,
          //   message: "请填写发送手机号",
          //   trigger: "blur"
          // }],
          sendDt:[
          {
            required: true,
            message: "请选择定时发送的日期和时间",
            trigger: "blur",
          },
        ]
        },
        teamplate: {},
      };
    },
    created() {
      this.getCarrierListVV();
      this.getCustomerListVV();
      let _customerId = this.$route.query.customer_id;
      let _appId = this.$route.query.app_id;
      let _phoneNum = this.$route.query.phone_num;
      let _carrierId = this.$route.query.carrier_id;
      let sendType = this.$route.query.send_type;
      if(sendType == 'reply'){
        this.sendType = sendType
        this.getApplicationListVV(_customerId);
        this.loadAppTeamplate(_appId)
        this.ruleForm.customerId = _customerId + "";
        this.ruleForm.appId = _appId + "";
        this.ruleForm.carrierIds = [_carrierId + ""];
        this.ruleForm.phoneNumStr = _phoneNum;
        this.buildPhones()
      }

    },
    methods: {
      getCarrierListVV() {
        getCarrierList().then((response) => {
          this.carrierList = response.data;
        });
      },
      getApplicationListVV(id) {
        getApplicationList({
          customerId: id,
          isAll: true,
        }).then((response) => {
          this.applicationList = response.data;
        });
      },
      getCustomerListVV() {
        getCustomerList().then((response) => {
          this.customerList = response.data;
        });
      },
      itemsMessageTemplateVV(appId) {
        itemsMessageTemplate({
          appId
        }).then((response) => {
          this.msgTeamplateList = response.data;
        });
      },
      submitPushMessage(data) {
        pushMessage(data).then((response) => {
          this.$notify.success({
            title: '发送成功',
            message: '发送任务已提交！'
          });
         // this.$refs['ruleForm'].resetFields();
        });
      },
      submitForm(form) {
        let ctime = parseTime(new Date(),null);
        let data = {...this.ruleForm}

        if(this.sendTimeType+""=="1"){
          data.sendDt = ctime;
        }else if(this.sendTimeType+""=="2"){
          data.sendDt = parseTime(data.sendDt,null);
        }

        this.$refs["ruleForm"].validate(valid => {
          if (valid) {
            this.submitPushMessage(data)
          }
        })
      },
      switchApp(id) {
        this.applicationList = [];
        this.getApplicationListVV(id);
      },
      loadAppTeamplate(appId) {
        this.itemsMessageTemplateVV(appId);
      },
      loadTeamplateDetail(teamplateId) {
        getMessageTemplate(teamplateId).then((response) => {
          this.teamplate = response.data;
        });
      },
      buildPhones() {
        let phones = this.ruleForm.phoneNumStr.split('\n')
        let pattam = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
        this.noPhone = phones.filter(phone => !pattam.test(phone))
        this.ruleForm.userPhones = phones;
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
        return "never";
      },
      loadLabel() {
        this.resetForm('form');
        if (this.ruleForm.customerId !== '' && this.ruleForm.customerId !== null && this.ruleForm.customerId !== undefined) {
          this.addClientDialogOpen = true
          groupLabels(this.ruleForm.customerId).then(response => {
            this.labelList = response.data
          })
        }
      },
      handleSelectionChange(selection) {
        this.selectionIds = selection.map((item) => item.id)
      },
      confirmPhones() {
        if (this.selectionIds == null || this.selectionIds.length === 0) {
          this.$message('请至少选择一行记录')
          return
        }
        let ids = {"ids": this.selectionIds};
        getPhonesByLabel(ids).then((res) => {
          let phs = res.data;
          this.ruleForm.phoneNumStr = "";

          for (let i = 0; i < phs.length; i++) {
            var item = phs[i];

            if (i !== phs.length-1) {
              this.ruleForm.phoneNumStr += item + "\n";
            } else {
              this.ruleForm.phoneNumStr += item
            }
          }
          this.buildPhones();
          this.addClientDialogOpen = false;
        });
      },
      selectExcelFile(res) {
        if (res.target.files && res.target.files.length) {
          let selectFile = res.target.files[0]
          let formData = new FormData()
          formData.append('file', selectFile)
          readExcel(formData).then((res)=>{

            let phs = res.data.data;
            this.ruleForm.phoneNumStr = "";

            for (let i = 0; i < phs.length; i++) {
              var item = phs[i];

              if (i !== phs.length-1) {
                this.ruleForm.phoneNumStr += item + "\n";
              } else {
                this.ruleForm.phoneNumStr += item
              }
            }
            this.buildPhones();
          });
        }
      },
      selectFile() {
        document.getElementById('files').click()
      },
    },
    mounted() {
    },

  }
</script>
<style lang="scss" scoped="scoped">
  .main-section {
    display: flex;
    padding: 30px 15px 10px;

    .left-task-form {
      order: -1;
      width: 500px;
    }

    .right-card-views {
      order: 1;
      margin-left: 50px;
    }
  }

  .waitting {
    color: #ff6600;
  }

  .audit {
    color: #008000;
  }

  .reject {
    color: #ff0000;
  }

  .never{
    color: #1f2d3d;
  }

</style>
