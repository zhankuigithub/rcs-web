<template>
  <div class="app-container">

    <div class="content-box">
      <el-form ref="form" :model="editForm" :rules="rules" label-width="120px">
        <el-form-item label="客户端编号" prop="clientId">
          <el-select
            v-model="editForm.clientId"
            placeholder="请选择客户端"
            clearable
          >
            <el-option
              v-for="item in clientList"
              :key="item.clientId"
              :label="item.clientDesc"
              :value="item.clientId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="模板名称" prop="templateName">
          <el-input
            v-model="editForm.templateName"
            placeholder="请输入模板名称"
          />
        </el-form-item>
        <el-form-item label="模板内容" prop="templateContent">
          <el-input
            type="textarea"
            v-model="editForm.templateContent"
            placeholder="请输入内容：例如：您正在进行贵健康注册，验证码{code}。{minute}内有效，请及时完成注册流程。"
          />
        </el-form-item>
        <el-form-item label="短连接配置" prop="webFlag">
          <el-radio-group v-model="editForm.webFlag">
            <el-radio :label="1">使用</el-radio>
            <el-radio :label="0">不使用</el-radio>
          </el-radio-group>
        </el-form-item>
        <div class="web-content-box" v-if="editForm.webFlag + '' == '1'">
            <el-form-item label="网页标题" prop="webTitle">
                <el-input v-model="editForm.webTitle" placeholder="请输入网页标题"/>
            </el-form-item>
            <el-form-item label="网页内容" prop="webHtml">
                <iframe v-if="editForm.fileName  && editForm.fileName.length" class="web-content" :srcdoc="editForm.webHtml" frameborder="0"></iframe>
                <Editor v-else :value="editForm.webHtml"  @valueChanged="valueChanged"></Editor>
            </el-form-item>
            <el-form-item label="选择网页" prop="file">
                <input id="files" style="display:none" type="file" accept=".html" @change="selectHtmlFile" />
                <button @click="selectFile">选择上传文件</button>
                <div v-if="editForm.fileName  && editForm.fileName.length">{{editForm.fileName}}
                  <el-button @click="deleteFile">删除</el-button>
                </div>
            </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" size="mini" v-loading="submitLoading">立即提交</el-button>
        <el-button @click="$router.back(-1)" size="mini">返 回</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getClientList,getSmsTemplateDetail,addSmsTemplate,updateSmsTemplate } from "@/api/sms/template";
import Editor from "@/components/editor/editor-tinymce";
  import {getToken} from "@/utils/auth";
export default {
  name: "SmsTemplateManage",
  components: {
    Editor,
  },
  data() {
    return {
      clientList: [],
      dataList: [],
      submitLoading: false,
      url:process.env.VUE_APP_SMS_BASE_API + '/rcs-sms/smsTemplateConfigInfo/upload',
      editForm: {
        clientId: "",
        templateContent: "",
        templateId: "",
        templateName: "",
        webFlag: 0,
        webHtml: "",
        webTitle: "",
        fileName: ""
      },
      headers: {
            "ACCESS-TOKEN": getToken(),
          },
      files:[],
      rules: {
        clientId: [
          {required: true, message: "请选择客户端", trigger: "blur"}
        ],
        templateName: [
          {required: true, message: "请输入模板名称", trigger: "blur"}
        ]
      },
    };
  },
  created() {
    let query = this.$route.query;
    if(query && JSON.stringify(query) != '{}'){
      let templateId = query.templateId
      if(templateId){
        getSmsTemplateDetail(templateId).then(res=>{
          let data = res.data
          this.editForm = data
          if(this.editForm.htmlFile){
            this.editForm.fileName = this.editForm.htmlFile
          }
        })
      }
    }
    getClientList().then((res) => {
      this.clientList = res.data;
    });
  },
  methods: {
    valueChanged(e){
      this.editForm.webHtml = e
    },
    deleteFile(){
      this.editForm.fileName = ''
      this.editForm.webHtml = ''
      var file = document.getElementById('files');
      file.value = null;
    },
    selectFile(){
      document.getElementById('files').click();
    },
    selectHtmlFile(res){
      console.log(res)
      if(res.target.files && res.target.files.length){
        const selectedFile = res.target.files[0];
        this.editForm.fileName = selectedFile.name
        var reader = new FileReader();
        reader.readAsText(selectedFile);
        var that = this
        reader.onload = function() {
          console.log(this.result)
          that.editForm.webHtml = this.result
        }
      }
    },

    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
            if(this.editForm.templateId){
                updateSmsTemplate(this.editForm).then(res=>{
                    if(res.code == 200){
                        this.$message('修改成功')
                        this.$router.back(-1)
                    }else{
                        this.$message(res.msg)
                    }
                })
            }else{
                addSmsTemplate(this.editForm).then(res=>{
                    if(res.code == 200){
                        this.$message('添加成功')
                        this.$router.back(-1)
                    }else{
                        this.$message(res.msg)
                    }
                })
            }

        }
      });
    },
    // 成功上传文件
    upFile(res, file) {
        if (res.code == 200) {
            this.editForm.fileName = res.data
        }
    },
  },
};
</script>

<style lang="less" scoped>
    .dialog-footer {
        margin-top: 30px;
        display: flex;
        align-items: center;
        justify-content: right;
        flex-direction: row;
    }
    .web-content-box{
        padding: 30px;
        border: solid 1px #bbb;
    }
    .web-content{
      width: 100%;
      height: 320px;
    }
</style>
