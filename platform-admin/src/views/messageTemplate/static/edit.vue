<template>
  <div class="app-container clearfix">
    <div class="left-simulator-wrap">
      <MessageTemplatePreview
        :appId="appId"
        :messageType="form.type"
        :textMsg="form.payload"
        :floatingButtons="suggestionTags"
        :cards="cardList"
        :mediaResources="mediaResources"
        :position="msgPosition"
        :orientation="msgOrientation"
        :imageAlignment="imageAlignment"
        :backType="backType"
      >
      </MessageTemplatePreview>
    </div>
    <div class="right-info-wrap">
      <div>
        <el-form
          :model="form"
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
              :disabled="isQueryById"
            ></el-cascader>
          </el-form-item>
          <el-form-item label="模板名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入模板名称"/>
          </el-form-item>
          <el-form-item label="模板类型" prop="type">
            <el-select
              @change="formTypeChange"
              v-model="form.type"
              placeholder="选择消息类型"
              :disabled="isQueryById"
            >
              <el-option
                v-for="item in messageTypeList"
                :key="item.dictValue"
                :value="item.dictValue"
                :label="item.dictLabel"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="回落配置" prop="backType">
            <el-radio-group v-model="backType">
              <el-radio label="0">不回落</el-radio>
              <el-radio label="1">Chatbot H5</el-radio>
              <el-radio label="2">文本短信</el-radio>
              <el-radio label="3">UP 1.0</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item v-if="backType!=='0'" label="短信回落" prop="smsContent">
            <el-input
              type="textarea"
              :rows="3"
              placeholder="请输入短信回落"
              v-model="form.smsContent"
            >
            </el-input>
          </el-form-item>
          <div v-if="messageTypeFormat(form.type) == '纯文本'">
            <el-form-item label="文本内容" prop="payload">
              <el-input
                type="textarea"
                :rows="3"
                placeholder="请输入文本内容"
                v-model="form.payload"
              >
              </el-input>
            </el-form-item>
          </div>
          <div v-if="messageTypeFormat(form.type) == '单卡片'">
            <el-form-item label="选择卡片" prop="singleCard">
              <el-tag
                v-for="card in selectedCards"
                :key="card.name"
                closable
                :type="card.id"
                @close="delCardList(card)"
              >
                {{ card.name }}
              </el-tag>
              <div
                class="el-upload el-upload--picture-card"
                @click="handAddSingleCard"
                v-if="selectedCards.length <= 0"
              >
                <i class="el-icon-plus"></i>
              </div>
            </el-form-item>
            <el-form-item label="卡片布局" prop="msgOrientation">
              <el-radio-group v-model="msgOrientation" size="small">
                <el-radio label="1" border>垂直布局</el-radio>
                <el-radio label="2" border>水平布局</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="msgOrientation == '2'" label="素材位置" prop="imageAlignment">
              <el-radio-group v-model="imageAlignment" size="small">
                <el-radio label="1" border>居左</el-radio>
                <el-radio label="2" border>居右</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
          <div v-if="messageTypeFormat(form.type) == '多卡片'">
            <el-form-item label="选择卡片" prop="multipleCard" style="min-height: 120px;">
              <el-tag
                v-for="card in selectedCards"
                :key="card.name"
                closable
                :type="card.id"
                @close="delCardList(card)"
              >
                {{ card.name }}
              </el-tag>
              <div
                class="el-upload el-upload--picture-card"
                @click="handAddMultipleCard"
              >
                <i class="el-icon-plus"></i>
              </div>
            </el-form-item>
          </div>
          <div
            v-if="messageTypeFormat(form.type) == '地理信息'"
            style="width: 720px"
          >
            <el-form-item label="选择位置" prop="msgPosition">
              <span v-if="this.msgPosition" class="msg-position">
                {{ this.msgPosition | msgPositionFormat }}
              </span>
              <el-button
                class="button-new-tag"
                size="small"
                @click="handCleanMsgPosition"
                v-if="this.msgPosition"
              >x 清空
              </el-button
              >
              <el-button
                class="button-new-tag"
                size="small"
                @click="handAddMsgPosition"
              >+ 选择地图位置
              </el-button
              >
            </el-form-item>
          </div>
          <div v-if="messageTypeFormat(form.type) == '图片'">
            <el-form-item label="选择图片" prop="msgPicture">
              <div v-if="this.msgPicture">
                <el-image
                  style="width: 100px; height: 100px"
                  :src="msgPicture.url"
                ></el-image>
              </div>
              <div v-if="this.msgPicture">
                <el-button
                  class="button-new-tag"
                  size="small"
                  @click="handCleanMsgMaterial(selectTypeEnum.picture)"
                >x 清空
                </el-button
                >
              </div>
              <div
                class="el-upload el-upload--picture-card"
                @click="handAddMaterial(selectTypeEnum.picture)"
              >
                <i class="el-icon-plus"></i>
              </div>
            </el-form-item>
          </div>
          <div v-if="messageTypeFormat(form.type) == '视频'">
            <el-form-item label="选择视频" prop="msgVideo">
              <div v-if="this.msgVideo">
                <video width="320" height="240" controls>
                  <source :src="this.msgVideo.url" type="video/mp4"/>
                  <source :src="this.msgVideo.url" type="video/ogg"/>
                  您的浏览器不支持Video标签。
                </video>
              </div>
              <div v-if="this.msgVideo">
                <el-button
                  class="button-new-tag"
                  size="small"
                  @click="handCleanMsgMaterial(selectTypeEnum.video)"
                >x 清空
                </el-button
                >
              </div>
              <div
                class="el-upload el-upload--picture-card"
                @click="handAddMaterial(selectTypeEnum.video)"
              >
                <i class="el-icon-plus"></i>
              </div>
            </el-form-item>
          </div>
          <div v-if="messageTypeFormat(form.type) == '音频'">
            <el-form-item label="选择音频" prop="msgAudio">
              <div v-if="this.msgAudio">
                <audio controls>
                  <source :src="msgAudio.url" type="audio/ogg"/>
                  <source :src="msgAudio.url" type="audio/mpeg"/>
                  您的浏览器不支持 audio 元素。
                </audio>
              </div>
              <div v-if="this.msgAudio">
                <el-button
                  class="button-new-tag"
                  size="small"
                  @click="handCleanMsgMaterial(selectTypeEnum.audio)"
                >x 清空
                </el-button
                >
              </div>
              <div
                class="el-upload el-upload--picture-card"
                @click="handAddMaterial(selectTypeEnum.audio)"
              >
                <i class="el-icon-plus"></i>
              </div>
            </el-form-item>
          </div>
          <el-form-item label="浮动菜单">
            <el-tag
              :key="tag"
              v-for="tag in suggestionTags"
              closable
              :disable-transitions="false"
              @close="handleClose(tag)"
              @click="suggestionEdit(tag)"
            >
              {{ tag }}
            </el-tag>
            <el-button
              class="button-new-tag"
              size="small"
              @click="showAddSuggestion"
            >+ 新建菜单
            </el-button
            >
          </el-form-item>
        </el-form>
      </div>
      <div
        v-if="editSugFlag"
        style="padding: 32px;width: 620px;border:1px solid #dacece"
      >
        <el-form
          :model="suggestionForm"
          ref="suggestionForm"
          :inline="true"
          :rules="suggestionRules"
          label-width="120px"
          style="padding:10px;"
          label-position="top"
          size="small"
        >
          <el-form-item label="菜单名称" prop="displayText">
            <el-input
              placeholder="请输入菜单名称"
              v-model="suggestionForm.displayText"
            />
          </el-form-item>
          <el-form-item label="菜单类型" prop="type">
            <el-select v-model="suggestionForm.type" @change="()=>{$refs.suggestionForm.validateField('type');}"
                       placeholder="选择菜单类型">
              <el-option
                v-for="item in suggestionTypeList"
                :key="item.dictValue"
                :value="item.dictValue"
                :label="item.dictLabel"
              />
            </el-select>
          </el-form-item>
          <div v-if="suggestionTypeFormat(suggestionForm.type) == '建议回复'">
            <el-form-item label="动作值">
              <el-input
                placeholder="动作值"
                v-model="suggestionForm.postback"
              >
              </el-input>
            </el-form-item>
          </div>
          <div v-if="suggestionTypeFormat(suggestionForm.type) == '跳转网页'">
            <el-form-item label="网页地址" prop="url">
              <el-select v-model="suggestionForm.url" placeholder="请选择">
                <el-option
                  v-for="item in webpageList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="打开方式" prop="application">
              <el-select v-model="suggestionForm.application" placeholder="请选择">
                <el-option label="内部" value="webview"></el-option>
                <el-option label="网页" value="browser"></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div v-if="suggestionTypeFormat(suggestionForm.type) == '拨打电话'">
            <el-form-item label="电话号码" prop="phoneNumber">
              <el-input
                placeholder="请输入电话号码"
                v-model="suggestionForm.phoneNumber"
              >
              </el-input>
            </el-form-item>
          </div>
          <div v-if="suggestionTypeFormat(suggestionForm.type) == '位置信息'">
            <el-form-item label="选择位置" prop="position">
              <!--span v-if="sugPosition != null">{{
                sugPosition | positionFormat
              }}</span-->
              <el-input
                placeholder="经度"
                v-model="suggestionForm.longitude"
                :disabled="true"
                style="width: 100px"
              >
              </el-input>
              <el-input
                placeholder="纬度"
                v-model="suggestionForm.latitude"
                :disabled="true"
                style="width: 100px"
              >
              </el-input>
              <el-button
                class="button-new-tag"
                size="small"
                @click="handAddSugPosition"
              >+ 选择地图位置
              </el-button
              >
            </el-form-item>
          </div>
          <div v-if="suggestionTypeFormat(suggestionForm.type) == '活动计划'">
            <el-form-item label="活动时间" prop="activityRange">
              <el-date-picker
                v-model="suggestionForm.activityRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                value-format="yyyy-MM-ddTHH:mm:ssZ"
              >
              </el-date-picker>
            </el-form-item>
            <el-form-item label="活动名称" prop="title">
              <el-input
                placeholder="请输入活动名称"
                v-model="suggestionForm.title"
              >
              </el-input>
            </el-form-item>
            <el-form-item label="活动详情" prop="description">
              <el-input
                type="textarea"
                :rows="2"
                placeholder="请输入活动内容"
                v-model="suggestionForm.description"
              >
              </el-input>
            </el-form-item>
          </div>
          <div style="text-align: right">
            <el-button type="primary" @click="okAddSug">保存浮动菜单</el-button>
            <el-button @click="cancelAddSug">取 消</el-button>
          </div>
        </el-form>
      </div>
      <div style="margin-top: 40px; padding-left: 120px">
        <el-button type="primary" @click="submitForm">保存消息模板</el-button>
        <el-button @click="returnListPage">返回列表</el-button>
      </div>
    </div>
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

    <CardSelector
      ref="selectorCard"
      @select-success="handleSelectorCard"
    ></CardSelector>
    <MaterialSelector
      ref="selector"
      @select-success="handleSelectorMaterial"
    ></MaterialSelector>
  </div>
</template>

<script>
  import {getApplicationList} from "@/api/application";
  import {getCustomerList} from "@/api/customer";
  import {materialList} from "@/api/resources";
  import {
    customerGrades,
    industryType,
    dictCustomerStatus,
    dictMessageType,
    dictSuggestionType,
  } from "@/assets/initdata";
  import {
    listMessageTemplate,
    getMessageTemplate,
    addMessageTemplate,
    updateMessageTemplate,
  } from "@/api/messageTemplate";
  import Position from "@/components/map/AMap";
  import CardSelector from "../../resources/components/card-selector";
  import MaterialSelector from "../../resources/components/resources-selector";
  import MessageTemplatePreview from "@/components/MsgTempPreview";
  import {TimeSelect} from 'element-ui';
  import {idCard, idCardValidity, phoneNumber, emailValue, urlString} from "@/utils/validate";

  export default {
    name: "messageTemplateEdit",
    data() {
      var validateFileAppId = (rule, value, callback) => {
        if (this.appId == null) {
          callback(new Error('请选择应用'));
        } else {
          callback();
        }
      };
      var validateFileSingleCard = (rule, value, callback) => {
        if (this.selectedCards.length <= 0) {
          callback(new Error('请选择一张卡片'));
        } else {
          callback();
        }
      };
      var validateFileMultipleCard = (rule, value, callback) => {
        if (this.selectedCards.length < 2) {
          callback(new Error('请至少选择两张卡片'));
        } else {
          callback();
        }
      };
      var validateFileMsgPosition = (rule, value, callback) => {
        if (this.msgPosition == null) {
          callback(new Error('请在地图上选择地理位置'));
        } else {
          callback();
        }
      };
      var validateFilePosition = (rule, value, callback) => {
        if (this.sugPosition == null) {
          callback(new Error('请在地图上选择地理位置'));
        } else {
          callback();
        }
      };
      var validateFieldMsgPicture = (rule, value, callback) => {
        if (this.msgPicture == null) {
          callback(new Error('请选择一张图片'));
        } else {
          callback();
        }
      };
      var validateFieldMsgVideo = (rule, value, callback) => {
        if (this.msgVideo == null) {
          callback(new Error('请选择一个视频'));
        } else {
          callback();
        }
      };
      var validateFieldMsgAudio = (rule, value, callback) => {
        if (this.msgAudio == null) {
          callback(new Error('请选择一个音频'));
        } else {
          callback();
        }
      };
      return {
        isQueryById: false,
        customerId: null,
        appId: null,
        sugIds: "",
        cascaderModel: [],
        messageTypeList: dictMessageType(),
        suggestionTypeList: dictSuggestionType(),
        applicationList: [],
        selectAppInfo: {},
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
        suggestionTags: [],
        suggestionList: [],
        editIndex: null, //修改浮动菜单的位置序号
        inputVisible: false,
        inputValue: "",
        singleCardId: null,
        sugPosition: null,
        sugOperType: 1, //浮动菜单操作类型：1添加，2修改
        msgPosition: null,
        cardList: [],
        editSugFlag: false,
        addAddressOpen: false,
        position: [121.5273285, 31.21515044],
        positionType: 1, //位置信息类型：1消息，2浮动菜单
        positionTypeEnum: {
          messageTemplate: 1,
          suggestion: 2,
        },
        formOpType: 1,
        formOpTypeEnum: {
          //表单操作类型
          add: 1,
          edit: 2,
        },
        selectType: 1, //选择类型：1单卡，2多卡，3图片，4视频，5音频
        selectTypeEnum: {
          singleCard: 1,
          multipleCard: 2,
          picture: 3,
          video: 4,
          audio: 5,
        },
        form: {},
        suggestionForm: {},
        rules: {
          appId: [
            //{ required: true, message: "所属应用不能为空", trigger: "blur" },
            {validator: validateFileAppId, trigger: 'blur'}
          ],
          type: [{required: true, message: "请选择消息内容", trigger: "blur"}],
          name: [
            {required: true, message: "模板名称不可为空", trigger: "blur"},
          ],
          payload: [
            {required: true, message: "请输入相应的内容", trigger: "blur"},
          ],
          singleCard: [
            {validator: validateFileSingleCard, trigger: 'blur'}
          ],
          multipleCard: [
            {validator: validateFileMultipleCard, trigger: 'blur'}
          ],
          msgPosition: [
            {validator: validateFileMsgPosition, trigger: 'blur'}
          ],
          msgPicture: [
            {validator: validateFieldMsgPicture, trigger: 'blur'}
          ],
          msgVideo: [
            {validator: validateFieldMsgVideo, trigger: 'blur'}
          ],
          msgAudio: [
            {validator: validateFieldMsgAudio, trigger: 'blur'}
          ]
        },
        suggestionRules: {
          displayText: [
            {required: true, message: "请输入菜单名称", trigger: "blur"},
          ],
          type: [
            {required: true, message: "请选择菜单类型", trigger: "blur"},
          ],
          url: [
            {required: true, message: "请选择跳转网页地址", trigger: "blur"}
          ],
          application: [
            {required: true, message: "请选择打开方式", trigger: "blur"}
          ],
          position: [
            {validator: validateFilePosition, trigger: 'blur'}
          ],
          phoneNumber: [
            {required: true, message: "请输入电话号码", trigger: "blur"},
            //{ validator: phoneNumber, trigger: 'blur' }
          ],
          activityRange: [
            {required: true, message: "请选择活动时间范围", trigger: "blur"},
          ],
          title: [
            {required: true, message: "请输入活动名称", trigger: "blur"},
          ],
          description: [
            {required: true, message: "请输入活动详情", trigger: "blur"},
          ]
        },
        messageTemplateId: null,
        messageTemplate: null,
        selectedCards: [],
        msgPicture: null,
        msgVideo: null,
        msgAudio: null,
        mediaResources: {},
        messageTemplateInfo: null,
        msgOrientation:'1',//1垂直  2 水平
        imageAlignment:'1',//素材位置  1 左 2右
        backType:'0',
        webpageList:[]
      };
    },
    created() {
      let query = this.$route.query;

      let _customerId = this.$route.query.customerId;
      let _appId = this.$route.query.appId;
      let _messageTemplateId = this.$route.query.id;

      if (query.oper == "add") {
        this.formOpType = this.formOpTypeEnum.add;
      } else if (query.oper == "edit") {
        this.formOpType = this.formOpTypeEnum.edit;

        if (_customerId == null || _appId == null || _messageTemplateId == null) {
          this.$message({
            message: "加载出错，参数缺失！",
            type: "warning",
          });

          return;
        }

        this.appId = _appId;
        this.customerId = _customerId;
        this.cascaderModel = [_customerId, _appId];
        this.isQueryById = true;

        getMessageTemplate(_messageTemplateId).then((res) => {
          this.messageTemplateInfo = res.data;
          this.msgOrientation = this.messageTemplateInfo.orientation + '';
          this.imageAlignment =  this.messageTemplateInfo.imageAlignment + '';
          this.backType =  this.messageTemplateInfo.backType + '';
          this.sugIds = res.data.sugIds;
          this.setEditForm();
        });
      }
      if(this.appId && this.appId + '' != ''){
        this.updateMaterialList(this.appId)
      }
    },
    components: {
      Position,
      CardSelector,
      MaterialSelector,
      MessageTemplatePreview,
    },
    methods: {
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
        this.updateMaterialList(this.appId)

      },
      updateMaterialList(appId){
        materialList({appId:appId,types:'4,5'}).then((response) => {
          this.webpageList = response.data
        });
      },
      messageTypeFormat(key) {
        return this.selectDictLabel(this.messageTypeList, key);
      },
      handleClose(tag) {
        var index = this.suggestionTags.indexOf(tag)
        this.suggestionTags.splice(index, 1);
        this.suggestionList.splice(index)
      },
      showAddSuggestion() {
        this.resetSuggestionForm();
        this.sugOperType = this.formOpTypeEnum.add;
        //this.editSugFlag = !this.editSugFlag;
        this.editSugFlag = true;
      },
      handleInputConfirm() {
        let inputValue = this.inputValue;
        if (inputValue) {
          this.dynamicTags.push(inputValue);
        }
        this.inputVisible = false;
        this.inputValue = "";
      },
      suggestionTypeFormat(type) {
        return this.selectDictLabel(
          this.suggestionTypeList,
          type
        );
      },
      handAddSingleCard() {
        if (this.customerId == null || this.appId == null) {
          this.$message({
            message: "请先选择应用",
            type: "warning",
          });

          return;
        }

        this.selectType = this.selectTypeEnum.singleCard;

        this.$refs.selectorCard.open({
          customerId: this.customerId,
          appId: this.appId,
        });
      },
      handAddMultipleCard() {
        if (this.customerId == null || this.appId == null) {
          this.$message({
            message: "请先选择应用",
            type: "warning",
          });

          return;
        }
        this.selectType = this.selectTypeEnum.multipleCard;

        this.$refs.selectorCard.open({
          customerId: this.customerId,
          appId: this.appId,
        });
      },
      handleSelectorMaterial(item) {
        console.log(item);
        this.mediaResources = item;
        let _obj = {id: item.id, url: item.sourceUrl, thumbId: item.thumbId};
        if (this.selectType == this.selectTypeEnum.picture) {
          this.msgPicture = _obj;
          this.$refs.form.validateField("msgPicture");
        } else if (this.selectType == this.selectTypeEnum.video) {
          this.msgVideo = _obj;
          this.$refs.form.validateField("msgVideo");
        } else if (this.selectType == this.selectTypeEnum.audio) {
          this.msgAudio = _obj;
          this.$refs.form.validateField("msgAudio");
        }
      },
      loadPosition(position) {
        console.log(JSON.stringify(position));
        if (this.positionType == this.positionTypeEnum.messageTemplate) {
          this.msgPosition = position;
          this.$refs.form.validateField("msgPosition");
        } else if (this.positionType == this.positionTypeEnum.suggestion) {
          this.sugPosition = position;
          this.suggestionForm.longitude = position.lng;
          this.suggestionForm.latitude = position.lat;
          this.$refs.suggestionForm.validateField("position");
        }
      },
      //选择卡片
      handleSelectorCard(item) {
        console.log(item);
        if (this.selectType == this.selectTypeEnum.singleCard) {
          let _cards = [{id: item.id, name: item.name}];
          this.selectedCards = _cards;
          this.cardList = [item];
          this.$refs.form.validateField("singleCard");
        } else if (this.selectType == this.selectTypeEnum.multipleCard) {
          let _isSelected = false;
          this.selectedCards.forEach(function (_item, i) {
            if (_item.id + "" == item.id + "") {
              _isSelected = true;
            }
          });

          if (_isSelected) {
            this.$message({
              message: "不可以重复选择卡片！",
              type: "warning",
            });
            return;
          }

          let _cards = {
            id: item.id,
            name: item.name,
          };
          this.selectedCards.push(_cards);
          this.cardList.push(item);

          this.$refs.form.validateField("multipleCard");
        }
      },
      handAddMsgPosition() {
        this.positionType = this.positionTypeEnum.messageTemplate;
        this.addAddressOpen = true;
      },
      handCleanMsgPosition() {
        this.msgPosition = null;
        this.$refs.form.validateField("msgPosition");
      },
      handAddSugPosition() {
        this.positionType = this.positionTypeEnum.suggestion;
        this.addAddressOpen = true;
      },
      suggestionEdit(tag) {
        this.editSugFlag = true;
        this.sugOperType = this.formOpTypeEnum.edit;
        this.editIndex = this.suggestionTags.indexOf(tag);
        this.suggestionForm = this.suggestionList[this.editIndex];
      },
      okAddress() {
        this.addAddressOpen = false;
      },
      cancelAddress() {
        this.addAddressOpen = false;
      },
      okAddSug() {

        let _this = this;
        _this.$refs["suggestionForm"].validate((valid) => {
          if (valid) {
            if (_this.suggestionForm.displayText == null) {
              return;
            }
            let _i = _this.suggestionTags.indexOf(_this.suggestionForm.displayText);
            if (_i >= 0 && _this.sugOperType == _this.formOpTypeEnum.add) {
              return;
            }

            if (_this.sugOperType == _this.formOpTypeEnum.add) {
              _this.suggestionTags.push(_this.suggestionForm.displayText);
              let _form = {..._this.suggestionForm};
              _this.suggestionList.push(_form);
            } else if (_this.sugOperType == _this.formOpTypeEnum.edit) {
              _this.suggestionTags[_this.editIndex] = _this.suggestionForm.displayText;
              _this.suggestionList[_this.editIndex] = {..._this.suggestionForm};
            }

            _this.editSugFlag = !_this.editSugFlag;
          } else {

          }
        });
      },
      cancelAddSug() {
        this.editSugFlag = !this.editSugFlag;
      },
      submitForm() {
        let _this = this;
        _this.$refs["form"].validate((valid1) => {
          if (valid1) {
            let data = _this.getSubData();
            if (data == null) {
              return;
            }
            if (_this.formOpType == _this.formOpTypeEnum.add) {
              addMessageTemplate(data).then((res) => {
                //console.log(res);
                _this.$notify.success({
                  title: "添加成功",
                  message: "恭喜您已成功添加一条消息模板",
                });

                _this.$router.go(-1);
              });
            } else if (_this.formOpType == _this.formOpTypeEnum.edit) {
              updateMessageTemplate(data).then((res) => {
                console.log(res);

                _this.$notify.success({
                  title: "修改成功",
                  message: "恭喜您已成功更新了一条消息模板",
                });

                _this.$router.go(-1);
              });
            }
          } else {
            _this.$message({
              type: "error",
              message: "消息表单校验不通过",
            });
          }
        });
      },
      cancelAddress() {
      },
      getSuggestionFromListByName(name) {
        let obj = null;
        this.suggestionList.forEach(function (item, i) {
          let _name = "";
          if (item.reply != null) {
            _name = item.reply.displayText;
          } else if (item.action != null) {
            _name = item.action.displayText;
          } else {
            _name = "";
          }
          if (_name == name) {
            obj = this.suggestionList[i];
          }
        });
        return obj;
      },
      resetSuggestionForm() {
        this.suggestionForm = {
          displayText: null,
          type: null,
          url: null,
          phoneNumber: null,
          activityRange: null,
          title: null,
          description: null,
          postback: null,
        };
      },
      getSubData() {
        let _obj = {};

        if (this.formOpType == this.formOpTypeEnum.add) {
          _obj = {
            appId: this.appId,
            name: this.form.name,
            payload: "",
            type: parseInt(this.form.type),
            suggestions: [],
          };
        } else if (this.formOpType == this.formOpTypeEnum.edit) {
          _obj = {
            id: this.messageTemplateInfo.id,
            sugIds: this.sugIds,
            appId: this.appId,
            name: this.form.name,
            payload: "",
            type: parseInt(this.form.type),
            suggestions: [],
          };
        }
        //payload格式化
        if (this.messageTypeFormat(this.form.type) == "纯文本") {
          _obj.payload = this.form.payload;
        } else if (this.messageTypeFormat(this.form.type) == "单卡片") {
          if (this.selectedCards.length <= 0) {
            this.$message({
              message: "请选择一个卡片再提交",
              type: "warning",
            });
            return null;
          }
          _obj.payload = this.selectedCards[0].id;
        } else if (this.messageTypeFormat(this.form.type) == "多卡片") {
          if (this.selectedCards.length < 2) {
            this.$message({
              message: "请至少选择两张卡片",
              type: "warning",
            });
            return null;
          }
          _obj.payload = "";
          this.selectedCards.forEach(function (item, i) {
            _obj.payload += i > 0 ? "," + item.id : item.id;
          });
        } else if (this.messageTypeFormat(this.form.type) == "地理信息") {
          if (this.msgPosition == null) {
            this.$message({
              message: "请在地图上选择地理位置",
              type: "warning",
            });
            return null;
          }
          let _val =
            "geo:" +
            this.msgPosition.lat +
            "," +
            this.msgPosition.lng +
            ";crs=gcj02;rcs-l=" +
            this.msgPosition.address;
          _obj.payload = _val;
        } else if (this.messageTypeFormat(this.form.type) == "图片") {
          if (this.msgPicture == null) {
            this.$message({
              message: "请选择图片",
              type: "warning",
            });
            return null;
          }
          if (this.msgPicture.thumbId != undefined && this.msgPicture.thumbId.length !== 0) {
            _obj.payload = this.msgPicture.thumbId + ',' + this.msgPicture.id;
          } else {
            _obj.payload = this.msgPicture.id;
          }
        } else if (this.messageTypeFormat(this.form.type) == "视频") {
          if (this.msgVideo == null) {
            this.$message({
              message: "请选择视频",
              type: "warning",
            });
            return null;
          }
          if (this.msgVideo.thumbId != undefined && this.msgVideo.thumbId.length !== 0) {
            _obj.payload = this.msgVideo.thumbId + ',' + this.msgVideo.id;
          } else {
            _obj.payload = this.msgVideo.id;
          }
        } else if (this.messageTypeFormat(this.form.type) == "音频") {
          //msgAudio
          if (this.msgAudio == null) {
            this.$message({
              message: "请选择音频",
              type: "warning",
            });
            return null;
          }
          _obj.payload = this.msgAudio.id;
          if (this.msgAudio.thumbId != undefined && this.msgAudio.thumbId.length !== 0) {
            _obj.payload = this.msgAudio.thumbId + ',' + this.msgAudio.id;
          } else {
            _obj.payload = this.msgAudio.id;
          }
        }

        let _this = this;

        //suggestions格式化
        this.suggestionList.forEach(function (item, i) {
          if (_this.suggestionTypeFormat(item.type) == "建议回复") {
            let _sug = {
              reply: {
                displayText: item.displayText,
                postback: {
                  data: item.postback && item.postback.length > 0 ? item.postback : "",
                },
              },
            };

            if (_this.formOpType == _this.formOpTypeEnum.edit) {
              _sug = _this.getEditSug(_sug, item);
            }

            _obj.suggestions.push(_sug);
          } else if (_this.suggestionTypeFormat(item.type) == "跳转网页") {
            let _sug = {
              action: {
                urlAction: {
                  openUrl: {
                    url: item.url,
                    application: item.application
                  },
                },
                displayText: item.displayText,
                postback: {
                  data: item.displayText,
                },
              },
            };

            if (_this.formOpType == _this.formOpTypeEnum.edit) {
              _sug = _this.getEditSug(_sug, item);
            }

            _obj.suggestions.push(_sug);
          } else if (_this.suggestionTypeFormat(item.type) == "拨打电话") {
            let _sug = {
              action: {
                dialerAction: {
                  dialPhoneNumber: {
                    phoneNumber: item.phoneNumber,
                  },
                },
                displayText: item.displayText,
                postback: {
                  data: item.displayText,
                },
              },
            };

            if (_this.formOpType == _this.formOpTypeEnum.edit) {
              _sug = _this.getEditSug(_sug, item);
            }

            _obj.suggestions.push(_sug);
          } else if (_this.suggestionTypeFormat(item.type) == "位置信息") {
            let _url = "https://www.google.com/maps/@" +
              item.latitude +
              "," +
              item.longitude +
              ",15z";
            let _sug = {
              action: {
                mapAction: {
                  showLocation: {
                    location: {
                      latitude: item.latitude,
                      longitude: item.longitude,
                      label: item.displayText,
                    },
                    fallbackUrl: _url,
                  },
                },
                displayText: item.displayText,
                postback: {
                  data: item.displayText,
                },
              },
            };

            if (_this.formOpType == _this.formOpTypeEnum.edit) {
              _sug = _this.getEditSug(_sug, item);
            }

            _obj.suggestions.push(_sug);
          } else if (_this.suggestionTypeFormat(item.type) == "活动计划") {
            let _range = item.activityRange;
            if (_range == null || _range.length != 2) {
              this.$message({
                message: "活动时间段不可以为空",
                type: "warning",
              });
              return null;
            }
            let _sug = {
              action: {
                calendarAction: {
                  createCalendarEvent: {
                    startTime: _range[0],
                    endTime: _range[1],
                    title: item.title,
                    description: item.description,
                  },
                },
                displayText: item.displayText,
                postback: {
                  data: item.displayText,
                },
              },
            };
            //如果是编辑操作的，则需要获取编辑接口格式的浮动菜单格式
            if (_this.formOpType == _this.formOpTypeEnum.edit) {
              _sug = _this.getEditSug(_sug, item);
            }

            _obj.suggestions.push(_sug);
          }
        });
        _obj.orientation = this.msgOrientation;
        _obj.imageAlignment = this.imageAlignment;
        _obj.backType = this.backType;
        _obj.smsContent = (this.backType === '0') ? '' : this.form.smsContent;
        return _obj;
      },
      getEditSug(sug, item) {
        let pl = JSON.stringify(sug);
        let obj = {};

        if (item.id != null) {
          obj.id = item.id;
        }

        if (item.appId != null) {
          obj.appId = item.appId;
        }

        obj.type = item.type;
        obj.displayText = item.displayText;

        obj.payload = pl;

        return obj;
      },
      returnListPage() {
        this.$router.go(-1);
      },
      delCardList(card) {
        let _index = -1;
        this.selectedCards.forEach(function (item, i) {
          if (item.id + "" == card.id + "") {
            _index = i;
          }
        });

        if (_index >= 0) {
          this.selectedCards.splice(_index, 1);
          this.cardList.splice(_index, 1);
        } else {
          this.$message({
            message: "警告:未找到需要删除的对象",
            type: "warning",
          });
        }

        if (this.selectType == this.selectTypeEnum.singleCard) {
          this.$refs.form.validateField("singleCard");
        } else if (this.selectType == this.selectTypeEnum.multipleCard) {
          this.$refs.form.validateField("multipleCard");
        }
      },
      formTypeChange() {
        this.selectedCards = [];
        this.cardList = [];
        this.msgPosition = null;
        this.msgPicture = null;
        this.msgVideo = null;
        this.msgAudio = null;
        this.mediaResources = {};
        this.msgOrientation = "1";
        this.imageAlignment = "1";
        this.$refs.form.validateField("type");
      },
      handAddMaterial(selectType) {
        if (this.customerId == null || this.appId == null) {
          this.$message({
            message: "请先选择应用",
            type: "warning",
          });

          return;
        }

        if (selectType != this.selectTypeEnum.video && selectType != this.selectTypeEnum.picture && selectType != this.selectTypeEnum.audio) {
          this.$message({
            message: "素材类型不正确",
            type: "warning",
          });

          return;
        }

        this.selectType = selectType;

        let _typeName = '';
        if (selectType == this.selectTypeEnum.picture) {
          _typeName = 'image';
        } else if (selectType == this.selectTypeEnum.video) {
          _typeName = 'video';
        } else if (selectType == this.selectTypeEnum.audio) {
          _typeName = 'audio'
        }

        this.$refs.selector.open({
          customerId: this.customerId,
          appId: this.appId,
          resourcesType: _typeName,
        });
      },
      handCleanMsgMaterial(selectType) {
        if (selectType == this.selectTypeEnum.picture) {
          this.msgPicture = null;
          this.$refs.form.validateField("msgPicture");
        } else if (selectType == this.selectTypeEnum.video) {
          this.msgVideo = null;
          this.$refs.form.validateField("msgVideo");
        } else if (selectType == this.selectTypeEnum.audio) {
          this.msgAudio = null;
          this.$refs.form.validateField("msgAudio");
        }
      },
      setEditForm() {
        if (this.messageTemplateInfo == null) {
          this.$message({
            message: "未获取到待修改的模板信息！",
            type: "warning",
          });
          return;
        }
        this.form = this.messageTemplateInfo;
        this.form.type = this.form.type + "";
        this.appId = this.form.appId;

        //设置消息模板表单
        if (this.messageTypeFormat(this.form.type) == "纯文本") {
          //this.form.payload=this.messageTemplateInfo.payload;
        } else if (
          this.messageTypeFormat(this.form.type) == "单卡片" ||
          this.messageTypeFormat(this.form.type) == "多卡片"
        ) {
          this.selectedCards = this.form.cards;
          this.cardList = [...this.selectedCards];
        } else if (this.messageTypeFormat(this.form.type) == "地理信息") {
          //geo:120.978355,31.141584;crs=gcj02;rcs-l=江苏省苏州市昆山市淀山湖镇环湖大道淀山湖风景区
          let _content = this.form.payload;
          let _nodes = _content.split(";");
          if (_nodes.length != 3) {
            this.$message({
              message: "地理信息格式异常！",
              type: "warning",
            });
            return;
          }
          let _naNodes = _nodes[0].split(",");
          /**
           * let _val =
           "geo:" +
           this.msgPosition.lng +
           "," +
           this.msgPosition.lat +
           ";crs=gcj02;rcs-l=" +
           this.msgPosition.address;
           */

          this.msgPosition = {
            lng: _naNodes[0].replace(/geo:/, ""),
            lat: _naNodes[1],
            address: _nodes[2].replace(/rcs-l=/, ""),
          };
        } else if (this.messageTypeFormat(this.form.type) == "图片") {
          this.msgPicture = {
            id: this.form.materials[this.form.materials.length - 1].id,
            url: this.form.materials[this.form.materials.length - 1].sourceUrl,
          };
        } else if (this.messageTypeFormat(this.form.type) == "视频") {
          this.msgVideo = {
            id: this.form.materials[this.form.materials.length - 1].id,
            url: this.form.materials[this.form.materials.length - 1].sourceUrl,
          };
        } else if (this.messageTypeFormat(this.form.type) == "音频") {
          this.msgAudio = {
            id: this.form.materials[this.form.materials.length - 1].id,
            url: this.form.materials[this.form.materials.length - 1].sourceUrl,
          };
        }
        if (this.form.materials != undefined && this.form.materials.length > 0) {
          this.mediaResources = this.form.materials[this.form.materials.length - 1];
        }

        //返显 suggestions
        if (this.form.suggestions == null || this.form.suggestions.length <= 0) {
          return;
        }

        let _sugsList = this.form.suggestions;

        let _this = this;
        _sugsList.forEach(function (item, i) {
          _this.suggestionTags.push(item.displayText);

          let _sug = {};

          if (_this.suggestionTypeFormat(item.type) == "建议回复") {
            let pl = JSON.parse(item.payload);
            let data = JSON.parse(pl.reply.postback.data);
            _sug = {
              id: item.id,
              appId: item.appId,
              displayText: item.displayText,
              type: item.type + "",
              postback: data.action
            };
          } else if (_this.suggestionTypeFormat(item.type) == "跳转网页") {
            let pl = JSON.parse(item.payload);
            _sug = {
              id: item.id,
              appId: item.appId,
              displayText: item.displayText,
              type: item.type + "",
              url: pl.action.urlAction.openUrl.url,
              application: pl.action.urlAction.openUrl.application,
            };
          } else if (_this.suggestionTypeFormat(item.type) == "拨打电话") {
            let pl = JSON.parse(item.payload);
            _sug = {
              id: item.id,
              appId: item.appId,
              displayText: item.displayText,
              type: item.type + "",
              phoneNumber: pl.action.dialerAction.dialPhoneNumber.phoneNumber,
            };
          } else if (_this.suggestionTypeFormat(item.type) == "位置信息") {
            let pl = JSON.parse(item.payload);

            _sug = {
              id: item.id,
              appId: item.appId,
              displayText: item.displayText,
              type: item.type + "",
              longitude: pl.action.mapAction.showLocation.longitude,
              latitude: pl.action.mapAction.showLocation.latitude,
            };
          } else if (_this.suggestionTypeFormat(item.type) == "活动计划") {
            let pl = JSON.parse(item.payload);

            _sug = {
              id: item.id,
              appId: item.appId,
              displayText: item.displayText,
              type: item.type + "",
              activityRange: [
                pl.action.calendarAction.createCalendarEvent.startTime,
                pl.action.calendarAction.createCalendarEvent.endTime,
              ],
              title: pl.action.calendarAction.createCalendarEvent.title,
              description:
              pl.action.calendarAction.createCalendarEvent.description,
            };
          }

          _this.suggestionList.push(_sug);
        });
      },
    },
    filters: {
      positionFormat: function (position) {
        let _val = position.lng + "," + position.lat;
        return _val;
      },
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
  };
</script>
<style lang="scss" scoped>
  .app-container {
    margin: 0 auto;
    display: flex;
    flex-direction: row;
  }

  .left-simulator-wrap {
    float: left;

    .simulator {
      width: 350px;
      height: 715px;
      background: url("../../../assets/images/iphone_backImg.png") no-repeat;
      background-size: 100% auto;
      position: relative;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;
    }
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
