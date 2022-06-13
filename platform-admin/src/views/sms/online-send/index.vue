<template>
  <div class="app-container">
    <div class="content-box" style="width: 1200px">
        <div style="width:50%">
            <el-form ref="form" :model="sendForm" label-width="120px" size="mini">
                <el-form-item label="选择短信模板">
                    <el-cascader
                        :props="props"
                        :value="selectTemplateInfo"
                        style="width: 400px"
                        @change="onSelectTemplate"
                    ></el-cascader>
                </el-form-item>
                <el-form-item label="电话号码">
                    <el-input
                        type="textarea"
                        v-model="sendForm.phones"
                        placeholder='请输入电话号码,多个手机号请以","隔开！'
                    />
                </el-form-item>
                <el-form-item label="占位符参数" v-if="templateParams && templateParams.length">
                    <el-form-item :label="key" v-for="(key,index) in templateParams" :key="index" style="margin-top:30px">
                        <el-input
                            :value="sendForm.params[key]"
                            :placeholder="'请输入占位符参数' + key"
                            @input="inputParams($event,key)"
                        />
                    </el-form-item>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitForm" size="mini" v-loading="submitLoading">立即提交</el-button>
                <el-button @click="$router.back(-1)" size="mini">返 回</el-button>
            </div>
        </div>
        <div style="width:50%;margin-left:60px;display:flex;flex-direction: column;" v-if="selectTemplateInfo && selectTemplateInfo.length">
            <h2>选择模板预览</h2>
            <span style="font-size:18px;font-weight:500">
                {{selectTemplate.templateName}}
            </span>
            <span style="font-size:16px;margin-top:20px">
                {{selectTemplate.templateContent}}
            </span >
            <iframe style="font-size:16px;margin-top:20px" :srcdoc="selectTemplate.webHtml" frameborder="0"></iframe>
        </div>
    </div>
  </div>
</template>

<script>
import { getClientList,getSmsClientTemplateList,getTemplateAllKeys,sendSms } from "@/api/sms/template";
import Editor from "@/components/editor/editor-tinymce";
export default {
  name: "SmsTemplateManage",
  components: {
    Editor,
  },
  data() {
    return {
        submitLoading: false,
        templateList:[],
        props: {
            lazy: true,
            lazyLoad(node, resolve) {
            const {level} = node;
            if (level == 0) {
                getClientList().then((response) => {
                    var costomeList = response.data.map((item) => ({
                        value: item.clientId,
                        label: item.clientDesc,
                        leaf: level >= 1,
                    }));
                    resolve(costomeList);
                });
            }
            if (level == 1) {
                var that = this
                getSmsClientTemplateList(node.value).then((response) => {
                    that.templateList = response.data
                    var templateList = response.data.map((item) => ({
                        value: item.templateId,
                        label: item.templateName,
                        leaf: true,
                    }));
                    resolve(templateList);
                });
            }
            },
        },
        sendForm:{
            "clientId": '',
            "params": {},
            "phones": "",
            "templateId": ''
        },
        selectTemplate:{},
        templateParams:[],
        selectTemplateInfo:[],



    };
  },
  created() {
    let query = this.$route.query;
    if(query){

    }
  },
  methods: {
    onSelectTemplate(e){
        console.log("select template:" + JSON.stringify(e));
        this.selectTemplateInfo = e;
        this.sendForm.clientId = e[0];
        this.sendForm.templateId = e[1];
        this.sendForm.params = {}
        this.templateParams = []
        getTemplateAllKeys(this.sendForm.templateId).then((response) => {
            if(response.code == 200){
                this.templateParams = response.data
                this.templateParams.forEach(element => {
                    this.sendForm.params[element] = ""
                });
            }
        });
        getSmsClientTemplateList(this.sendForm.clientId).then((response) => {
            this.templateList = response.data
            this.templateList.forEach(element =>{
                if(element.templateId == this.sendForm.templateId){
                    this.selectTemplate = element
                }
            })
        });

    },
    inputParams(e,key){
       this.sendForm.params[key] = e
        this.$nextTick(() => {
             this.$forceUpdate()
             console.log(JSON.stringify(this.sendForm))
        })

    },

    submitForm() {
        var from = {
            "clientId": this.sendForm.clientId,
            "params": this.sendForm.params,
            "phones": this.sendForm.phones.split(","),
            "templateId": this.sendForm.templateId
        }
        if(from.clientId + '' == '' || from.templateId + '' == ''){
            this.$message('请选择消息模板')
            return
        }
        if(from.phones + '' == ''){
            this.$message('请输入手机号码')
            return
        }
        if(from.params){
            for (var key in from.params) {
                if(from.params[key] + '' == ''){
                    this.$message('请填写完整的占位符参数')
                    return
                }
            }
        }

        this.submitLoading = true
        sendSms(from).then((response) => {
            this.submitLoading = false
            if(response.code == 200){
                this.$message('发送成功')
                this.sendForm = {
                    "clientId": '',
                    "params": {},
                    "phones": "",
                    "templateId": ''
                }
            }else{
                this.$message(response.msg)
            }

        });

    }
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
    .content-box{
        display: flex;
        flex-direction: row;
    }
</style>
