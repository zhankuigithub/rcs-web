<template>
  <div class="app-container">
    <el-form :model="form" :rules="rules" ref="form" label-width="120px" class="devset">
      <!-- <el-form-item label="开发者token" prop="token">
        <el-input v-model="form.token" placeholder="请输入开发者token" maxlength="200"></el-input>
      </el-form-item> -->
      <el-form-item label="协议类型" prop="protocol">
        <el-select v-model="form.protocol"
          placeholder="请选择协议类型" clearable style="width: 100%;">
          <el-option key="http" label="http" value="1" />
          <el-option key="https" label="https" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="回调URL" prop="notifyUrl">
        <el-input v-model="form.notifyUrl" placeholder="请输入回调URL" maxlength="200"></el-input>
      </el-form-item>
      <el-form-item label="是否启用接口" prop="enable">
        <!-- <el-input v-model="form.enable" placeholder="请输入回调URL" maxlength="200"></el-input> -->
        <el-switch
          v-model="form.enable"
          :active-value="1"
          :inactive-value="0"
          active-color="#13ce66"
          inactive-color="#ff4949"
          active-text="开启"
          inactive-text="关闭"
          >
        </el-switch>
      </el-form-item>
      <el-form-item label="开发者id" prop="appId">
        <!-- {{form.appId}} -->
        <el-input v-model="form.appId" placeholder="请输入开发者id" maxlength="50"></el-input>
      </el-form-item>
      <el-form-item label="开发者密匙" prop="appId">
        <!-- {{form.appKey}} -->
        <el-input v-model="form.appKey" placeholder="请输入开发者密匙" maxlength="100"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">确认保存</el-button>
        <el-button type="text" @click="returnListPage">返回列表</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
import { listChatbotDeveloper, getChatbotDeveloper, delChatbotDeveloper, addChatbotDeveloper, updateChatbotDeveloper } from "@/api/chatbotDeveloper";
import { getApplicationList,getApplication } from "@/api/application";
import { getCustomerList } from "@/api/customer";

export default {
  name: "ChatbotDeveloper",
  components: {
  },
  data() {
    return {
      chatBotId:null,
      form:{
        appId: '',
        appKey: ''
      },
      rules:{

      }
    };
  },
  created() {
     let _chatbotId=this.$route.query.id;
     if(_chatbotId!=null&&_chatbotId!=""){
       this.chatBotId=_chatbotId;
     }

     this.getDevInfo();

  },
  methods: {
    submitForm(){
      let _data={...this.form};
      _data.chatBotId=this.chatBotId;
      this.$refs["form"].validate((valid) => {
        if(valid){
          if (_data.id != null) {
            updateChatbotDeveloper(_data).then(res=>{
              this.$notify.success({
                title: '操作成功',
                message: '您已成功修改了开发者信息！'
              });
            });
          }else{
            addChatbotDeveloper(_data, this.$route.query.carrierId).then(res=>{
              this.$notify.success({
                title: '操作成功',
                message: '您已成功添加了开发者信息！'
              });
            });
          }
        }
      })
    },
    reset(){
      this.form={
        appId: null,
        appKey: null,
        chatBotId: null,
        enable: null,
        notifyUrl: null,
        protocol: null
      }
    },
    getDevInfo(){
      let _param={
        currentPage: 1,
        pageSize: 500,
        params: {
          chatBotId: this.chatBotId,
        }
      };
      listChatbotDeveloper(_param).then(res=>{
        if(res.data.items!=null&&res.data.items.length>0){
          this.form=res.data.items[0];
        }else{
          this.reset;
        }
      })
    },
    returnListPage(){
      this.$router.go(-1);
    }
  }
};
</script>

<style lang="scss" scoped="scoped">
  .el-form-item{
    max-width: 420px;
  }
</style>
