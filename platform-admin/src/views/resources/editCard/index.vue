<template>
  <div class="app-container">
    <div style="padding: 20px 0; font-size: 14px; color: #333">
      请选择应用
      <el-cascader
        :props="props"
        :value="selectAppInfo"
        style="width: 240px"
        @change="selectApp"
        :disabled="!canSelectApp"
      ></el-cascader>
    </div>
    <div class="content">
      <div class="left-simulator-wrap">
        <MessageTemplatePreview
          :appId="cardInfo.appId"
          messageType="2"
          :cards="[cardInfo]"
          @select-card-btn="onButtonClick"
        >
        </MessageTemplatePreview>
      </div>
      <div class="right-info-wrap">
        <el-form label-width="150px" ref="cardForm" :model="cardInfo" :rules="cardRules">
          <el-form-item label="添加媒体" required prop="material">
            <div class="select-resource-wrap">
              <div
                class="select-resource-item"
                @click="selectResource('image')"
              >
                <img
                  v-if="
                    cardInfo.material != null &&
                    JSON.stringify(cardInfo.material) != '{}' &&
                    cardInfo.material.type == 1
                  "
                  class="resource-file-wrap"
                  :src="cardInfo.material.sourceUrl"
                />
                <i v-else class="el-icon-plus select-resource-plus">添加图片</i>
              </div>
              <div
                class="select-resource-item"
                @click="selectResource('video')"
              >
                <video
                  v-if="
                    cardInfo.material != null &&
                    JSON.stringify(cardInfo.material) != '{}' &&
                    cardInfo.material.type == 3
                  "
                  class="resource-file-wrap"
                  autoplay
                  :src="cardInfo.material.sourceUrl"
                ></video>
                <i v-else class="el-icon-plus select-resource-plus">添加视频</i>
              </div>
              <div
                class="select-resource-item"
                @click="selectResource('audio')"
                :style="
                  cardInfo.material.type == 2
                    ? 'background-image: url(' +
                      cardInfo.material.thumbnailUrl +
                      ');'
                    : ''
                "
              >
                <audio
                  class="resource-file-wrap audio"
                  v-if="
                    cardInfo.material != null &&
                    JSON.stringify(cardInfo.material) != '{}' &&
                    cardInfo.material.type == 2
                  "
                  controls
                >
                  <source :src="cardInfo.material.sourceUrl" type="audio/ogg"/>
                  <source
                    :src="cardInfo.material.sourceUrl"
                    type="audio/mpeg"
                  />
                  您的浏览器不支持 audio 元素。
                </audio>
                <i v-else class="el-icon-plus select-resource-plus">添加音频</i>
              </div>
            </div>
            <div
              v-if="
                cardInfo.material != null &&
                JSON.stringify(cardInfo.material) != '{}'
              "
            >
              已选择{{
                cardInfo.material.type == 1
                  ? "图片"
                  : cardInfo.material.type == 3
                  ? "视频"
                  : "音频"
              }}
              : {{ cardInfo.material.name }}
            </div>
            <span style="color: red">*注：以上媒体三选一</span>
          </el-form-item>
          <el-form-item label="素材名称" required prop="name">
            <el-input
              v-model="cardInfo.name"
              placeholder="请输入素材名称"
              clearable
              size="small"
            ></el-input>
          </el-form-item>
          <el-form-item label="卡片标题">
            <el-input
              v-model="cardInfo.title"
              placeholder="请输入卡片标题"
              clearable
              size="small"
            ></el-input>
          </el-form-item>
          <el-form-item label="卡片摘要">
            <el-input
              v-model="cardInfo.description"
              placeholder="请输入卡片摘要"
              clearable
              size="small"
            ></el-input>
          </el-form-item>
          <el-form-item label="卡片高度" >
            <el-radio-group v-model="cardInfo.height" size="small">
              <el-radio label="3" border>大</el-radio>
              <el-radio label="2" border>中</el-radio>
              <el-radio label="1" border>小</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="卡片按钮" v-if="cardInfo.suggestions.length < 4">
            <el-button
              clearable
              size="small"
              icon="el-icon-plus"
              @click="addMenuButtons"
            >添加按钮
            </el-button
            >
          </el-form-item>
          <el-form
            label-width="150px"
            ref="buttonForm"
            :model="tempButtonObj"
            :rules="buttonRules"
            v-if="
              tempButtonObj !== null && JSON.stringify(tempButtonObj) !== '{}'
            "
          >
            <el-form-item label="按钮名称" required prop="displayText">
              <el-input
                v-model="tempButtonObj.displayText"
                placeholder="请输入按钮名称"
                clearable
                size="small"
              ></el-input>
            </el-form-item>
            <el-form-item label="按钮类型" required prop="type">
              <el-select
                v-model="tempButtonObj.type"
                placeholder="请选择按钮类型"
                size="small"
                @change="onButtonTypeChanged"
              >
                <el-option label="建议回复" value="1"></el-option>
                <el-option label="打开网页" value="2"></el-option>
                <el-option label="拨打电话" value="3"></el-option>
                <el-option label="打开地图" value="4"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="tempButtonObj.type == 1"
              label="动作值"
            >
              <el-input
                placeholder="动作值"
                clearable
                size="small"
                v-model="tempButtonObj.postback.data"
              ></el-input>
            </el-form-item>
            <el-form-item
              v-if="tempButtonObj.type == 2"
              label="输入链接"
              required
              prop="urlAction.openUrl.url"
            >
            <el-select v-model="tempButtonObj.urlAction.openUrl.url" size="small" placeholder="请选择">
              <el-option
                v-for="item in webpageList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
            </el-form-item>

            <el-form-item
              v-if="tempButtonObj.type == 2"
              label="打开方式"
              required
              prop="urlAction.openUrl.application"
            >
              <el-select v-model="tempButtonObj.urlAction.openUrl.application" size="small" placeholder="请选择">
                <el-option
                  v-for="item in openTypes"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item
              v-if="tempButtonObj.type == 3"
              label="输入号码"
              required
              prop="dialerAction.dialPhoneNumber.phoneNumber"
            >
              <el-input
                placeholder="请输入号码"
                clearable
                size="small"
                maxlength="14"
                v-model="tempButtonObj.dialerAction.dialPhoneNumber.phoneNumber"
              ></el-input>
            </el-form-item>
            <el-form-item
              v-if="tempButtonObj.type == 4"
              label="地理位置（经纬度）"
              required
              prop="mapAction.showLocation.location"
            >
              <el-button
                icon="el-icon-map-location"
                size="small"
                @click="addAddressOpen = true"
              >地图选点
              </el-button
              >
              <div
                v-if="
                  tempButtonObj.mapAction.showLocation.location.latitude > 0 &&
                  tempButtonObj.mapAction.showLocation.location.longitude > 0
                "
              >
                {{
                  '(' + tempButtonObj.mapAction.showLocation.location.latitude +
                  "," +
                  tempButtonObj.mapAction.showLocation.location.longitude + ")"
                }}
              </div>
            </el-form-item>
            <div class="right-info-bottm">
              <el-button
                type="danger"
                icon="el-icon-delete"
                style="width: 150px; margin-left: 20px"
                @click="deleteButton"
              >删 除
              </el-button
              >
              <el-button
                type="primary"
                icon="el-icon-menu"
                style="width: 150px; margin-left: 20px"
                @click="saveButton"
              >保存按钮
              </el-button
              >
            </div>
          </el-form>
          <div
            class="card-button-wrap"
            v-if="
              tempButtonObj !== null && JSON.stringify(tempButtonObj) !== '{}'
            "
          ></div>
          <div
            class="right-info-bottm"
            v-if="JSON.stringify(tempButtonObj) == '{}'"
          >
            <el-button
              type="primary"
              icon="el-icon-menu"
              style="width: 150px; margin-left: 20px"
              @click="saveCardAction"
              v-loading="saveLoading"
            >保存卡片
            </el-button
            >
          </div>
        </el-form>
      </div>
    </div>
    <el-dialog
      title="选择地址"
      :visible.sync="addAddressOpen"
      width="820px"
      append-to-body
    >
      <div>
        <Position :maker-position.sync="position" @position="loadPosition"/>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addAddressOpen = false"
        >确 定
        </el-button
        >
        <el-button @click="addAddressOpen = false">取 消</el-button>
      </div>
    </el-dialog>
    <ResourcesSelector
      ref="selector"
      @select-success="selectResourcesSuccess"
    ></ResourcesSelector>
  </div>
</template>

<script>
import MessageTemplatePreview from "@/components/MsgTempPreview";
import ResourcesSelector from "../components/resources-selector";
import {getCustomerList} from "@/api/customer";
import {getApplicationList} from "@/api/application";
import {saveCard, cardDetail} from "@/api/card";
import {materialList} from "@/api/resources";

import Position from "@/components/map/AMap";

export default {
  components: {
    ResourcesSelector,
    MessageTemplatePreview,
    Position,
  },
  data() {
    const validateCardName = (rule, value, callback) => {
      if (value === null || value === "") {
        callback(new Error("请输入卡片名称"));
      } else {
        callback();
      }
    };
    const validateMaterial = (rule, value, callback) => {
      if (value === null || value === "" || JSON.stringify(value) == '{}') {
        callback(new Error("请选择媒体"));
      } else {
        callback();
      }
    };
    const validateButtonName = (rule, value, callback) => {
      if (value === null || value === "") {
        callback(new Error("请输入按钮名称"));
      } else {
        callback();
      }
    };
    const validateUrl = (rule, value, callback) => {
      if (this.tempButtonObj.urlAction.openUrl.url.length == 0) {
        callback(new Error("请输入打开的网址"));
      } else {
        callback();
        return
        var strRegex =
          "^((https|http|ftp|rtsp|mms)?://)" +
          "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" + //ftp的user@
          "(([0-9]{1,3}.){3}[0-9]{1,3}" + // IP形式的URL- 199.194.52.184
          "|" + // 允许IP和DOMAIN（域名）
          "([0-9a-z_!~*'()-]+.)*" + // 域名- www.
          "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]." + // 二级域名
          "[a-z]{2,6})" + // first level domain- .com or .museum
          "(:[0-9]{1,4})?" + // 端口- :80
          "((/?)|" + // a slash isn't required if there is no file name
          "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        var re = new RegExp(strRegex);
        //re.test()
        if (re.test(this.tempButtonObj.urlAction.openUrl.url)) {
          callback();
        } else {
          callback(new Error("您输入的网址无效"));
        }
      }
    };
    const validateUrl1 = (rule, value, callback) => {
      if (this.tempButtonObj.urlAction.openUrl.url.length == 0) {
        callback(new Error("请输入打开的网址"));
      } else {
        callback();
      }
    };
    const validatePhoneNumber = (rule, value, callback) => {
      var tel = /^0\d{2,3}-?\d{7,8}$/;
      var phone = /^(((13[0-9]{1})|(15[0-9]{1})|(16[0-9]{1})|(17[3-8]{1})|(18[0-9]{1})|(19[0-9]{1})|(14[5-7]{1}))+\d{8})$/;
      if (value.length == 11) {
        //手机号码
        if (phone.test(value)) {
          callback();
          return true;
        }
      } else if (value.length == 13 && value.indexOf("-") != -1) {
        //电话号码
        if (tel.test(value)) {
          callback();
          return true;
        }
      }
      callback(new Error("请输入有效的电话号码"));
    };
    const validatePhoneNumber1 = (rule, value, callback) => {
      if (value.length < 1) {
        callback(new Error("请输入电话号码"));
      } else {
        callback();
      }
    };
    const validateLocation = (rule, value, callback) => {
      console.log(value);
      if (value.latitude.length == 0 || value.longitude.length == 0) {
        callback(new Error("请在地图上选择地理位置"));
      } else {
        callback();
      }
    };
    return {
      selectAppInfo: [],
      props: {
        lazy: true,
        lazyLoad(node, resolve) {
          const {level} = node;
          if (level == 0) {
            getCustomerList().then((response) => {
              var costomeList = response.data.map((item) => ({
                value: item.id,
                label: item.name,
                leaf: level >= 1,
              }));
              resolve(costomeList);
            });
          }
          if (level == 1) {
            getApplicationList({customerId: node.value}).then((response) => {
              var applicationList = response.data.map((item) => ({
                value: item.id,
                label: item.name,
                leaf: true,
              }));
              resolve(applicationList);
            });
          }
        },
      },
      commitLoading: false, //提交按钮加载
      cardInfo: {
        id: "",
        customerId: "",
        appId: "",
        name: "",
        title: "",
        description: "",
        suggestions: [],
        materialId: "",
        material: {},
        height:'2'
      },
      position: [121.5273285, 31.21515044, '贵阳'],
      selectResources: null,
      tempButtonObj: {},
      tempButtonIndex: -1,
      saveLoading: false,
      canSelectApp: true,
      addAddressOpen: false,
      cardRules: {
        name: [{validator: validateCardName, trigger: ["blur", "change"]}],
        material: [
          {validator: validateMaterial, trigger: ["blur", "change"]}
        ]
      },
      buttonRules: {
        displayText: [{validator: validateButtonName, trigger: ["blur", "change"]}],
        urlAction: {
          openUrl: {
            url: [
              {validator: validateUrl, trigger: "blur"},
              {validator: validateUrl1, trigger: "change"},
            ],
          },
        },
        dialerAction: {
          dialPhoneNumber: {
            phoneNumber: [
              {validator: validatePhoneNumber, trigger: "blur"},
              {validator: validatePhoneNumber1, trigger: "change"},
            ],
          },
        },
        mapAction: {
          showLocation: {
            location: [{validator: validateLocation, trigger: "change"}],
          },
        },
      },
      webpageList:[],
      openTypes: [
        {id: 'webview', name: '内部'},
        {id: 'browser', name: '网页'},
      ]
    };
  },
  created() {
    var _customerId = this.$route.query.customerId;
    var _appId = this.$route.query.appId;
    let cardId = this.$route.query.cardId;

    if (_appId !== undefined && _customerId !== undefined) {
      this.selectAppInfo = [_customerId, _appId];
      this.cardInfo.customerId = _customerId;
      this.cardInfo.appId = _appId;
    }

    if (cardId !== undefined && cardId.length !== 0) {
      this.selectAppInfo = [_customerId, _appId];
      cardDetail(cardId).then((response) => {
        if (response.code == 200) {
          this.cardInfo = response.data;
          this.cardInfo.height = this.cardInfo.height + '';
          this.canSelectApp = false;
          if (this.cardInfo.suggestions !== undefined) {
            this.cardInfo.suggestions.forEach((element) => {
              element.type = element.type + "";
              if (element.urlAction == undefined) {
                element.urlAction = {
                  openUrl: {
                    url: "",
                  },
                };
              }
              if (element.dialerAction == undefined) {
                element.dialerAction = {
                  dialPhoneNumber: {
                    phoneNumber: "",
                  },
                };
              }
              if (element.mapAction == undefined) {
                element.mapAction = {
                  showLocation: {
                    fallbackUrl: "",
                    location: {
                      label: "",
                      latitude: "",
                      longitude: "",
                    },
                  },
                };
              }
              // 格式化数据
              if (element.type == '1') {
                let json = JSON.parse(element.postback.data);
                element.postback.data = json.action;
              }
            });
          }
        }
      });
      this.updateMaterialList(_appId ? _appId : '')
    }
  },
  methods: {
    //选择应用
    selectApp(e) {
      this.selectAppInfo = e;
      this.cardInfo.customerId = e[0];
      this.cardInfo.appId = e[1];
      this.updateMaterialList(this.cardInfo.appId)
    },
    updateMaterialList(appId){
      materialList({appId:appId,types:'4,5'}).then((response) => {
        this.webpageList = response.data
      });
    },
    //选择素材
    selectResource(type) {
      if (
        this.selectAppInfo[0] === undefined ||
        this.selectAppInfo[0].length === 0 ||
        this.selectAppInfo[1] === undefined ||
        this.selectAppInfo[1].length === 0
      ) {
        this.$message({
          message: "请先选择应用",
          type: "warning",
        });
        return;
      }
      this.$nextTick(() => {
        this.$refs.selector.open({
          customerId: this.selectAppInfo[0],
          appId: this.selectAppInfo[1],
          resourcesType: type,
        });
      });
    },
    selectResourcesSuccess(item) {
      this.cardInfo.material = item;
      this.cardInfo.materialId = item.id;
      this.cardInfo.thumbId = item.thumbId;
      this.$refs.cardForm.validateField('material');
    },
    addMenuButtons() {
      this.tempButtonIndex = -1;
      var buttonItem = {
        displayText: "",
        type: "1",
        urlAction: {
          openUrl: {
            url: "",
          },
        },
        postback: {
          data: "",
        },
        dialerAction: {
          dialPhoneNumber: {
            phoneNumber: "",
          },
        },
        mapAction: {
          showLocation: {
            fallbackUrl: "",
            location: {
              label: "",
              latitude: "",
              longitude: "",
            },
          },
        },
        material: {},
      };
      this.tempButtonObj = buttonItem;
    },
    onButtonTypeChanged() {
      this.$refs.buttonForm.clearValidate();
    },
    onButtonClick(buttonItem, index) {
      this.tempButtonObj = buttonItem;
      if (typeof this.tempButtonObj.postback === "undefined" || this.tempButtonObj.postback === null) {
        this.tempButtonObj.postback = {
          data: "",
        }
      }
      this.tempButtonIndex = index;
    },
    deleteButton() {
      this.tempButtonObj = {};
      if (this.tempButtonIndex != -1) {
        this.cardInfo.suggestions.splice(this.tempButtonIndex, 1);
        this.tempButtonIndex = -1;
      }
    },
    loadPosition(position) {
      console.log(JSON.stringify(position));
      this.position = position;
      this.tempButtonObj.mapAction.showLocation.location.latitude =
        position.lat;
      this.tempButtonObj.mapAction.showLocation.location.longitude =
        position.lng;
      this.tempButtonObj.mapAction.showLocation.location.label =
        position.address;
    },
    saveButton() {
      this.$refs.buttonForm.validate((valid) => {
        if (valid) {
          if (this.tempButtonObj.type == 4) {
            this.tempButtonObj.mapAction.showLocation.fallbackUrl =
              "https://www.google.com/maps/@" +
              this.tempButtonObj.mapAction.showLocation.location.latitude +
              "," +
              this.tempButtonObj.mapAction.showLocation.location.longitude +
              ",15z";
          }
          if (this.tempButtonObj.type == "1") {

            // if (this.tempButtonObj.postback.data.length == 0) {
            //   this.tempButtonObj.postback.data = this.tempButtonObj.displayText;
            // }

          }
          if (this.tempButtonIndex == -1) {
            this.cardInfo.suggestions.push(this.tempButtonObj);
          } else {
            this.cardInfo.suggestions[
              this.tempButtonIndex
              ] = this.tempButtonObj;
          }

          this.tempButtonIndex = -1;
          this.tempButtonObj = {};
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    saveCardAction() {
      if (
        this.cardInfo.customerId === undefined ||
        this.cardInfo.customerId.length === 0 ||
        this.cardInfo.appId === undefined ||
        this.cardInfo.appId.length === 0
      ) {
        this.$message({
          message: "请先选择应用",
          type: "warning",
        });
        return;
      }
      var that = this;
      this.$refs.cardForm.validate((valid) => {
        if (valid) {
          that.saveLoadding = true;
          saveCard(this.cardInfo).then((response) => {
            if (response.code == 200) {
              that.$message({
                message: "卡片保存成功",
                type: "success",
              });
              that.$router.back(-1);
            }
          });
        }
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.content {
  display: flex;
  flex-direction: row;
}

.select-resource-wrap {
  display: flex;
  flex-direction: row;
  position: relative;

  .select-resource-item {
    width: 148px;
    height: 148px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    margin: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    background-size: cover;
    background-repeat: no-repeat;

    .resource-file-wrap {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .audio {
      width: 80%;
      bottom: 10px;
      left: 10%;
      height: 30px;
    }
  }
}

.right-info-wrap {
  position: relative;
  float: left;
  padding: 10px;
  flex: 1;
  max-width: 700px;
  min-height: 710px;
  margin-left: 20px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;

  .right-info-bottm {
    display: flex;
    flex-flow: row-reverse;
  }
}
</style>
