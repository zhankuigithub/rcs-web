<template>
  <div class="app-container clearfix">
    <div class="left-simulator-wrap">
      <MessageTemplatePreview
        :appId="appId"
        :messageType="templateForm.type"
        :textMsg="templateForm.content"
        :smsContent="templateForm.smsContent"
        :floatingButtons="getFloatingButtonTags()"
        :selectFloatButtonIndex="selectFloatingButonIndex"
        :mediaResources="mediaResources"
        :cards="cardList"
        :position="msgPosition"
        @on-select-floatbtn="onSelectFloationgButton"
      >
      </MessageTemplatePreview>
    </div>
    <div class="right-info-wrap">
      <el-form
        :model="templateForm"
        ref="form"
        :inline="true"
        :rules="rules"
        label-width="120px" size="small"
      >
        <el-form-item label="选择应用" prop="appId" >
          <el-cascader
            :props="props"
            v-model="cascaderModel"
            @change="handCasChange"
          ></el-cascader>
        </el-form-item>
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="templateForm.name" placeholder="请输入模板名称"/>
        </el-form-item>
        <el-form-item label="短信回落" prop="smsContent">
          <el-input
            type="textarea"
            :rows="3"
            placeholder="请输入短信回落"
            v-model="templateForm.smsContent"></el-input>
        </el-form-item>
        <el-form-item label="模板类型" prop="type">
          <el-select
            @change="formTypeChange"
            v-model="templateForm.type"
            placeholder="选择模板类型">
            <el-option
              v-for="item in messageTypeList"
              :key="item.dictValue"
              :value="item.dictValue"
              :label="item.dictLabel"/>
          </el-select>
        </el-form-item>
        <div v-if="messageTypeFormat(templateForm.type) == '纯文本'">
          <el-form-item label="文本内容" prop="content">
            <el-input
              type="textarea"
              :rows="3"
              placeholder="请输入文本内容"
              v-model="templateForm.content">
            </el-input>
          </el-form-item>
        </div>
        <div v-if="messageTypeFormat(templateForm.type) == '多卡片' || messageTypeFormat(templateForm.type) == '单卡片'" >
          <el-form-item label="编辑卡片" style="min-height: 120px;">
              <div
                class="el-upload el-upload--picture-card"
                @click="handEditCard">
                <i class="el-icon-edit"></i>
              </div>
          </el-form-item>
        </div>
        <div v-if="messageTypeFormat(templateForm.type) == '地理信息'">
          <el-form-item label="选择位置" prop="content">
            <span v-if="templateForm.content" class="msg-position">
              {{ templateForm.content }}
            </span>
            <el-button
              class="button-new-tag"
              size="small"
              @click="handCleanMsgPosition"
              v-if="templateForm.content">x 清空</el-button>
            <el-button
              class="button-new-tag"
              size="small"
              @click="addAddressOpen = true"
            >+ 选择地图位置
            </el-button>
          </el-form-item>
        </div>
        <div v-if="messageTypeFormat(templateForm.type) == '图片'">
          <el-form-item label="选择图片" prop="content">
            <div v-if="this.mediaResources && JSON.stringify(this.mediaResources) != '{}'">
              <el-image
                style="width: 100px; height: 100px"
                :src="mediaResources.sourceUrl"
              ></el-image>
            </div>
            <div v-if="this.mediaResources && JSON.stringify(this.mediaResources) != '{}'">
              <el-button
                class="button-new-tag"
                size="small"
                @click="handCleanMsgMaterial(1)"
              >x 清空
              </el-button >
            </div>
            <div
              class="el-upload el-upload--picture-card"
              @click="handAddMaterial(1)"
            >
              <i class="el-icon-plus"></i>
            </div>
            <el-input
              placeholder="请输入图片参数"
              v-model="templateForm.content">
            </el-input>
          </el-form-item>
        </div>
        <div v-if="messageTypeFormat(templateForm.type) == '视频'">
          <el-form-item label="选择视频" prop="msgVideo">
            <div v-if="this.mediaResources && JSON.stringify(this.mediaResources) != '{}'">
              <video width="320" height="240" controls>
                <source :src="this.mediaResources.sourceUrl" type="video/mp4"/>
                <source :src="this.mediaResources.sourceUrl" type="video/ogg"/>
                您的浏览器不支持Video标签。
              </video>
            </div>
            <div v-if="this.mediaResources && JSON.stringify(this.mediaResources) != '{}'">
              <el-button
                class="button-new-tag"
                size="small"
                @click="handCleanMsgMaterial(2)"
              >x 清空
              </el-button
              >
            </div>
            <div
              class="el-upload el-upload--picture-card"
              @click="handAddMaterial(2)"
            >
              <i class="el-icon-plus"></i>
            </div>
            <el-input
              placeholder="请输入视频参数"
              v-model="templateForm.content">
            </el-input>
          </el-form-item>
        </div>
        <div v-if="messageTypeFormat(templateForm.type) == '音频'">
          <el-form-item label="选择音频" prop="msgAudio">
            <div v-if="this.mediaResources && JSON.stringify(this.mediaResources) != '{}' ">
              <audio controls>
                <source :src="mediaResources.sourceUrl" type="audio/ogg"/>
                <source :src="mediaResources.sourceUrl" type="audio/mpeg"/>
                您的浏览器不支持 audio 元素。
              </audio>
            </div>
            <div v-if="this.mediaResources && JSON.stringify(this.mediaResources) != '{}'">
              <el-button
                class="button-new-tag"
                size="small"
                @click="handCleanMsgMaterial(3)"
              >x 清空
              </el-button
              >
            </div>
            <div
              class="el-upload el-upload--picture-card"
              @click="handAddMaterial(3)"
            >
              <i class="el-icon-plus"></i>
            </div>
            <el-input
              placeholder="请输入音频参数"
              v-model="templateForm.content">
            </el-input>
          </el-form-item>
        </div>
        <el-form-item label="浮动菜单">
          <el-button
            v-if="!tempButton"
            class="button-new-tag"
            size="small"
            @click="showAddSuggestion"
          >+ 添加浮动菜单
          </el-button>
          <button-form v-if="tempButton" :appId="appId" :button="tempButton" @on-save="onSaveFloatingButton" @on-cancel="onCancelFloatingButton" @on-delete="onDeleteFloatingButton"></button-form>
        </el-form-item>
      </el-form>
      <div style="margin-top: 40px; padding-left: 120px">
        <el-button type="primary" @click="submitForm">保存消息模板</el-button>
        <el-button @click="returnListPage">返回列表</el-button>
      </div>
    </div>
    <MaterialSelector
      ref="selector"
      @select-success="handleSelectorMaterial"
    ></MaterialSelector>
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
        <el-button type="primary" @click="addAddressOpen = false">确 定</el-button>
        <el-button @click="addAddressOpen = false">取 消</el-button>
      </div>
    </el-dialog>
    <card-editor @onCompleteEditCards="completeEditCards" @onCancel="showCardEditor = false" v-if="showCardEditor" :appId="appId" :customerId="customerId" :cardType="templateForm.type" :cards="cardList" :layout="layout"></card-editor>
  </div>
</template>

<script>
import { getApplicationList } from "@/api/application";
import { getCustomerList } from "@/api/customer";
import { materialDetail } from "@/api/resources";
import { dictMessageType, dictSuggestionType } from "@/assets/initdata";
import {
  addDynamicMessageTemplate,
  updateDynamicMessageTemplate,
  getDynamicMessageTemplate,
} from "@/api/messageTemplate";
import Position from "@/components/map/AMap";
import ButtonForm from './components/button-form.vue'
import CardEditor from './components/card-editor.vue'
import MaterialSelector from "../../resources/components/resources-selector";
import MessageTemplatePreview from "@/components/MsgTempPreview";
export default {
  name: "DynamicMessageTemplateEdit",
  components: {
    Position,
    CardEditor,
    MaterialSelector,
    MessageTemplatePreview,
    ButtonForm
  },
  data() {
    var validateFileAppId = (rule, value, callback) => {
      if (this.appId == null) {
        callback(new Error('请选择应用'));
      } else {
        callback();
      }
    };
    var validateFileContent = (rule, value, callback) => {
      console.log('---------------1' + JSON.stringify(this.templateForm))
      if(this.templateForm.type + '' == '1' && !this.templateForm.content.length){
        callback(new Error('请输入文本内容'));
      }else if(this.templateForm.type + '' == '2' && this.cardList.length == 0){
        callback(new Error('请添加卡片'));
      }else if(this.templateForm.type + '' == '3' && this.cardList.length < 2){
        callback(new Error('请至少添加2张卡片'));
      }else if(this.templateForm.type + '' == '4' && !this.templateForm.content.length){
        callback(new Error('请选择地理位置信息'));
      }else if(this.templateForm.type + '' == '5' && (this.mediaResources == null || JSON.stringify(this.mediaResources) == "{}")){
        callback(new Error('请选择一张图片'));
      }else if(this.templateForm.type + '' == '6' && (this.mediaResources == null || JSON.stringify(this.mediaResources) == "{}")){
        callback(new Error('请选择一个音频'));
      }else if(this.templateForm.type + '' == '7' && (this.mediaResources == null || JSON.stringify(this.mediaResources) == "{}")){
        callback(new Error('请选择一个视频'));
      }else{
        callback();
      }
    };


    return {
      appId:'',
      selectAppInfo:{},
      customerId:'',
      template_id: "",
      template_info:{},
      template_payload:{},
      messageTypeList:dictMessageType(),
      rules: {
        appId: [
          //{ required: true, message: "所属应用不能为空", trigger: "blur" },
          {validator: validateFileAppId, trigger: 'blur'}
        ],
        type: [{required: true, message: "请选择模板类型", trigger: "blur"}],
        name: [
          {required: true, message: "模板名称不可为空", trigger: "blur"},
        ],
        content: [
          {required: true,validator: validateFileContent, trigger: 'blur'}
        ],
      },
      templateForm:{},
      cascaderModel: [],
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
                  nodes[i] = {value: item.id, label: item.name, leaf: true};
                });
                resolve(nodes);
              } else {
                resolve([]);
              }
              this.applicationList = _applicationList;
            });
          }
        },
      },
      mediaResources:null,
      floatingButtons:[],
      selectFloatingButonIndex:'',
      tempButton:null,
      showCardEditor:false,
      cardList:[],
      layout:{
        "cardWidth":"MEDIUM_WIDTH",
        "cardOrientation":"VERTICAL",
        "imageAlignment":"RIGHT"
      },
      addAddressOpen:false,
      position: [121.5273285, 31.21515044],
      msgPosition:null
    };
  },
  filters: {
    msgPositionFormat: function (position) {
      let _val =
        "geo:" +
        position.lng +
        "," +
        position.lat +
        ";crs=gcj02;rcs-l=" +
        position.address;
      return _val;
    },
  },
  created() {
    this.template_id = this.$route.query.template_id;
    if(this.template_id && this.template_id + '' !=  ''){
      getDynamicMessageTemplate(this.template_id).then(res =>{
        console.log(JSON.stringify(res))
        if(res && res.code == 200){
          this.template_info = res.data
          this.template_payload = JSON.parse(this.template_info.payload)
          this.cascaderModel = [this.template_info.customerId,this.template_info.appId]
          this.appId = this.template_info.appId
          this.customerId = this.template_info.customerId
          this.floatingButtons = this.template_payload.suggestions ? this.template_payload.suggestions:[]

          var form = {}
          form.appId = this.template_info.appId
          form.name = this.template_info.name
          form.smsContent = this.template_payload.smsContent
          form.type = this.template_info.type + ""

          if(this.template_payload.type == 1 ||  this.template_payload.type == 4 || this.template_payload.type == 5 || this.template_payload.type == 6 || this.template_payload.type == 7 ){
            form.content = this.template_payload.content
            if(this.template_payload.type == 4){
              let _content = form.content;
              let _nodes = _content.split(";");
              if (_nodes.length != 3) {
                this.$message({
                  message: "地理信息格式异常！",
                  type: "warning",
                });
                return;
              }
              let _naNodes = _nodes[0].split(",");
              this.msgPosition = {
                lng: _naNodes[0].replace(/geo:/, ""),
                lat: _naNodes[1],
                address: _nodes[2].replace(/rcs-l=/, ""),
              };
            }
          }
          if(this.template_payload.type == 5 || this.template_payload.type == 6 || this.template_payload.type == 7){
            materialDetail(this.template_payload.content).then(res=>{
              this.mediaResources = res.data
            })
          }
          if(this.template_payload.type == 2 || this.template_payload.type == 3){
            this.cardList = this.template_payload.content
            this.layout = this.template_payload.layout
            this.cardList.forEach(element=>{
              materialDetail(element.file).then(res=>{
                element.material = res.data
              })
            })
          }
          this.templateForm = Object.assign({},form)
        }

      })
    }
  },

  methods: {
    returnListPage(){
      this.$router.go(-1);
    },
    handCasChange() {
      if (this.cascaderModel.length != 2) {
        return;
      }
      this.customerId = this.cascaderModel[0];
      this.appId = this.cascaderModel[1];

      this.applicationList.forEach((element) => {
        if (element.id == this.appId) {
          this.selectAppInfo = element;
        }
      });
      this.$refs.form.validateField('appId');
    },
    messageTypeFormat(key) {
      return this.selectDictLabel(this.messageTypeList, key);
    },
    formTypeChange() {
      this.cardList = [];
      this.msgPosition = null;

      this.mediaResources = {};
      var form = Object.assign({},this.templateForm)
      form.content = ""
      this.templateForm = form
    },
    //编辑卡片
    handEditCard(){
      if (this.customerId == null || this.appId == null) {
        this.$message({
          message: "请先选择应用",
          type: "warning",
        });
        return;
      }
      this.showCardEditor = true
    },
    completeEditCards(cards,layout){
      this.cardList = cards
      this.layout = layout
      this.showCardEditor = false
    },

    //添加浮动菜单按钮
    showAddSuggestion(){
      if(!this.cascaderModel || this.cascaderModel.length != 2){
        this.$message({
          message: "请先选择应用",
          type: "warning",
        });
        return
      }
      this.tempButton = {}
    },

    //浮动菜单按钮 相关处理
    onSelectFloationgButton(index){
      this.selectFloatingButonIndex = index
      this.tempButton = this.floatingButtons[index]
    },
    onCancelFloatingButton(){
      this.tempButton = ""
      this.selectFloatingButonIndex = ''
    },
    onDeleteFloatingButton(){
      this.tempButton = ""
      this.floatingButtons.splice(this.selectFloatingButonIndex,1)
      this.selectFloatingButonIndex = ''
    },
    onSaveFloatingButton(button){
      if(this.selectFloatingButonIndex + '' != ''){
        this.floatingButtons[this.selectFloatingButonIndex] = button
      }else{
        this.floatingButtons.push(button)
      }
      this.tempButton = ""
      this.selectFloatingButonIndex = ''
    },
    getFloatingButtonTags(){
      var floatingButtonTags = []
      this.floatingButtons.forEach(element=>{
        if(element.reply){
          floatingButtonTags.push(element.reply.displayText)
        }
        if(element.action){
          floatingButtonTags.push(element.action.displayText)
        }
      })
      return floatingButtonTags
    },
    //地理位置消息 保存
    handCleanMsgPosition() {
      this.msgPosition = null;
      var form = Object.assign({},this.templateForm)
      form.content = ""
      this.templateForm = form
      this.$refs.form.validateField("content");
    },
    loadPosition(position) {
        console.log(JSON.stringify(position));
        this.msgPosition = position;
        this.templateForm.content = "geo:" +
        position.lng +
        "," +
        position.lat +
        ";crs=gcj02;rcs-l=" +
        position.address;
        this.$refs.form.validateField("content");
    },

    //选择素材
    handAddMaterial(selectType) {
      if (this.customerId == null || this.appId == null) {
        this.$message({
          message: "请先选择应用",
          type: "warning",
        });
        return;
      }
      let _typeName = '';
      if (selectType == 1) {
        _typeName = 'image';
      } else if (selectType == 2) {
        _typeName = 'video';
      } else if (selectType == 3) {
        _typeName = 'audio'
      }

      this.$refs.selector.open({
        customerId: this.customerId,
        appId: this.appId,
        resourcesType: _typeName,
      });
    },
    handleSelectorMaterial(item){
      this.mediaResources = item
      this.templateForm.content = item.id
      this.$forceUpdate()
    },
    handCleanMsgMaterial(){
      this.mediaResources = {}
      var form = Object.assign({},this.templateForm)
      form.content = ""
      this.templateForm = form
      this.$forceUpdate()
    },
    //保存消息模板
    submitForm(){
      this.$refs["form"].validate((valid1) => {
        if (valid1) {

          var data = {
            "appId": this.appId,

            "name": this.templateForm.name,
            "payload": "",
            "smsContent": this.templateForm.smsContent,
            "type": this.templateForm.type
          }
          var payload = {}
          payload.suggestions = this.floatingButtons
          payload.type = this.templateForm.type
          payload.smsContent = this.templateForm.smsContent
          if(this.templateForm.type == 1 ||  this.templateForm.type == 4 || this.templateForm.type == 5
          || this.templateForm.type == 6 || this.templateForm.type == 7 ){
            payload.content = this.templateForm.content
          }else{
            payload.content = this.cardList
            payload.layout = this.layout
          }
          data.payload = JSON.stringify(payload)

          if(this.template_id && this.template_id.length){
            data.id =  this.template_id
            updateDynamicMessageTemplate(data).then(res=>{
               this.$notify.success({
                  title: "修改成功",
                  message: "恭喜您已成功更新了一条动态消息模板",
                });
                this.$router.go(-1);
            })
          }else{
            addDynamicMessageTemplate(data).then(res=>{
              this.$notify.success({
                  title: "添加成功",
                  message: "恭喜您已成功添加了一条动态消息模板",
                });
                this.$router.go(-1);
            })
          }

        } else {
          _this.$message({
            type: "error",
            message: "消息表单校验不通过",
          });
        }
      });
    }
  },
};
</script>
<style lang="scss" scoped>
.app-container {
  margin: 0 auto;
  display: flex;
  flex-direction: row;
}
.bottom-warp .switch-buttom-wrap {
  z-index: 0;
}

.left-simulator-wrap {
  float: left;
}

.right-info-wrap {
  position: relative;
  float: left;
  width: 100%;
  padding: 10px;
  margin-left: 20px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;

  .el-form--inline .el-form-item {
    display: block;
    margin-right: 10px;
    vertical-align: top;
  }

  .right-info-bottm {
    display: flex;
    flex-flow: row-reverse;
  }
}

.el-tag + .el-tag {
  margin-left: 10px;
}

.button-new-tag {
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.input-new-tag {
  width: 90px;
  vertical-align: bottom;
}

.msg-position {
  display: inline-block;
  max-width: 200px;
  font-size: 12px;
  line-height: 21px;
  color: cadetblue;
  background: black;
  padding: 10px;
  border-radius: 5px;
  word-break: break-all;
}

.el-upload--picture-card {
  width: 62px;
  height: 62px;
  line-height: 68px;
}
</style>
