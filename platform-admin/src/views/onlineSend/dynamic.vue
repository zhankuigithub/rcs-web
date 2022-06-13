<template>
  <section class="main-section">
    <aside class="left-task-form">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="150px" class="demo-ruleForm">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="选择客户" prop="customerId">
              <el-select v-model="ruleForm.customerId" placeholder="请选择客户" @change="switchApp" style="width: 100%;">
                <el-option v-for="item in customerList" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选择应用" prop="appId">
              <el-select v-model="ruleForm.appId" :disabled="ruleForm.customerId == null || ruleForm.customerId == ''"
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
        <el-form-item v-if="allParams && allParams.bodyKeys  && allParams.bodyKeys.length" label="消息主体参数" prop="templateId">
            <el-input v-for="(item,index) in allParams.bodyKeys" :key="index" :placeholder="'请输入' + item"
                    v-model="ruleForm.body[item]"  @input="inputBodyParams($event,key)"></el-input>
        </el-form-item>

        <el-form-item v-if="allParams && allParams.suggestionKeys  && allParams.suggestionKeys.length" label="浮动按钮参数" prop="templateId">
            <el-input v-for="(item,index) in allParams.suggestionKeys" :key="index" :placeholder="'请输入' + item"
                    v-model="ruleForm.suggestion[item]"  @input="inputSuggestionParams($event,key)"></el-input>
        </el-form-item>

        <el-form-item label="发送号码" prop="phoneNumStr">
          <el-input type="textarea" :rows="5" placeholder="手动输入号码，每行一个号码"
                    v-model="ruleForm.phoneNumStr"
                    @change="buildPhones" ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm')">发 送</el-button>
        </el-form-item>
      </el-form>
    </aside>
    <aside class="right-card-views" v-if="teamplate && JSON.stringify(teamplate) != '{}'">
      <MessageTemplatePreview
        
        :appId="teamplate.appId"
        :messageType="teamplate_payload.type"
        :textMsg="teamplate_payload.content"
        :floatingButtons="getFloatingButtonTags()"
        :cards="cards"
        :mediaResources="teamplate.materials != undefined && teamplate.materials.length > 0 ?teamplate.materials[teamplate.materials.length - 1]:{}"
        :position="teamplate_payload.content"
       :smsContent="teamplate_payload.smsContent">
      </MessageTemplatePreview>
    </aside>

  </section>
</template>

<script>
  import {
    getApplicationList
  } from "@/api/application";
  import {
    getCustomerList
  } from "@/api/customer";
  import {
    listDynamicMessageTemplate,
    getDynamicMessageTemplate,
    dynamicTemplateGetAllKeys,
    dynamicTemplateSendMessage
  } from '@/api/messageTemplate'
  import MessageTemplatePreview from "@/components/MsgTempPreview";
  import {parseTime} from '../../utils/ruoyi';
  export default {
    components: {
      MessageTemplatePreview
    },
    data() {
      return {
        noPhone: [],
        inputPhoneType: '1',
        customerList: [],
        applicationList: [],
        msgTeamplateList: [],
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
          phoneNumStr: [{
            required: true,
            message: "请填写发送手机号",
            trigger: "blur"
          }]
        },
        teamplate: {},
        teamplate_payload: {},
        allParams:{},
        cards:[]
      };
    },
    methods: {
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
            listDynamicMessageTemplate(appId).then((response) => {
            console.log(JSON.stringify(response));
            this.msgTeamplateList = response.data;
            });
        },
        submitPushMessage(data) {
            dynamicTemplateSendMessage(data).then((response) => {
                this.$notify.success({
                    title: '发送成功',
                    message: '发送任务已提交！'
                });
            });
        },
        submitForm(form) {
            let data = {...this.ruleForm}
            this.$refs["ruleForm"].validate(valid => {
                if (valid) {
                    this.submitPushMessage(data)
                }
            })
        },
        switchApp(id) {
            this.applicationList = [];
            this.msgTeamplateList = []
            this.getApplicationListVV(id);
        },
        loadAppTeamplate(appId) {
            this.itemsMessageTemplateVV(appId);
        },
        loadTeamplateDetail(teamplateId) {
            getDynamicMessageTemplate(teamplateId).then((response) => {
                this.teamplate = response.data;
                this.teamplate_payload = JSON.parse(this.teamplate.payload)
                if(this.teamplate_payload.type == 2|| this.teamplate_payload.type == 3){
                    this.cards =  this.teamplate_payload.content
                }
            });
            dynamicTemplateGetAllKeys(teamplateId).then((response) => {
                this.ruleForm.body = {}
                this.ruleForm.suggestion = {}
                this.allParams = response.data;
                console.log('all params ++++++++++++++++' + JSON.stringify(this.allParams))
            });
        },
        buildPhones() {
            let phones = this.ruleForm.phoneNumStr.split('\n')
            let pattam = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
            this.noPhone = phones.filter(phone => !pattam.test(phone))
            this.ruleForm.phones = phones;
        },
        getFloatingButtonTags(){
            var floatingButtonTags = []
            this.teamplate_payload.suggestions.forEach(element=>{
                if(element.reply){
                    floatingButtonTags.push(element.reply.displayText)
                }
                if(element.action){
                    floatingButtonTags.push(element.action.displayText)
                }
            })
            return floatingButtonTags
        },
        inputBodyParams(e,key){
            this.ruleForm.body[key] = e
            this.$nextTick(() => {
                this.$forceUpdate()
            })
        },
        inputSuggestionParams(e,key){
            this.ruleForm.suggestion[key] = e
            this.$nextTick(() => {
                this.$forceUpdate()
            })
        }
    },
    mounted() {
      this.getCustomerListVV();
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
