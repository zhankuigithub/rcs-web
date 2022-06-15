<template>
    <div class="button-form-wrap">
        <el-form
          :model="suggestionForm"
          ref="suggestionForm"
          :inline="true"
          :rules="suggestionRules"
          label-width="120px"
          style="padding:10px;display:flex;flex-direction: column;"
          label-position="left"
          size="small"
        >
            <el-form-item label="菜单名称" prop="displayText">
                <el-input
                placeholder="请输入菜单名称"
                v-model="suggestionForm.displayText"
                style="width:200px"
                />
            </el-form-item>
            <el-form-item label="菜单类型" prop="type">
                <el-select v-model="suggestionForm.type"
                        placeholder="选择菜单类型" @change="onButtonTypeChanged" style="width:200px">
                <el-option
                    v-for="item in buttonTypes"
                    :key="item.dictValue"
                    :value="item.dictValue"
                    :label="item.dictLabel"
                />
                </el-select>
            </el-form-item>

             <div v-if="suggestionForm.type == '1'">
                <el-form-item label="动作值">
                <el-input
                    placeholder="动作值" v-model="suggestionForm.customAction" style="width:200px">
                </el-input>
                </el-form-item>
            </div>
            <div v-if="suggestionForm.type == '2'" style="display:flex;flex-direction: column;">
                <el-form-item label="网页地址" prop="url">
                    <el-input
                        placeholder="请输入网页地址"
                        v-model="suggestionForm.url"
                        style="width:200px"
                    >
                    </el-input>
                </el-form-item>
                <el-form-item label="打开方式" prop="application">
                    <el-select v-model="suggestionForm.application" placeholder="请选择" style="width:200px">
                        <el-option label="内部" value="webview"></el-option>
                        <el-option label="网页" value="browser"></el-option>
                    </el-select>
                </el-form-item>
            </div>
            <div v-if="suggestionForm.type == '3'">
                <el-form-item label="电话号码" prop="phoneNumber">
                <el-input
                    placeholder="请输入电话号码"
                    v-model="suggestionForm.phoneNumber"
                    style="width:200px"
                >
                </el-input>
                </el-form-item>
            </div>
          <div v-if="suggestionForm.type == '4'" style="display:flex;flex-direction: column;">
            <el-form-item label="选择位置" prop="position">
              <el-input
                placeholder="经度"
                v-model="suggestionForm.longitude"
                :disabled="true"
                style="width:100px"
              >
              </el-input>
              <el-input
                placeholder="纬度"
                v-model="suggestionForm.latitude"
                :disabled="true"
                style="width:100px;margin-left:20px"
              >
              </el-input>
              <el-button
                class="button-new-tag"
                size="small"
                @click="selectAddressOpen = true"
                style="margin-left:20px"
              >+ 选择地图位置
              </el-button
              >
            </el-form-item>
          </div>
          <div v-if="suggestionForm.type == '5'" style="display:flex;flex-direction: column;">

            <el-form-item label="活动名称" prop="title">
              <el-input
                placeholder="请输入活动名称"
                v-model="suggestionForm.title"
                style="width:200px"
              >
              </el-input>
            </el-form-item>
            <el-form-item label="活动详情" prop="description">
              <el-input
                type="textarea"
                :rows="2"
                placeholder="请输入活动内容"
                v-model="suggestionForm.description"
                style="width:200px"
              >
              </el-input>
            </el-form-item>
            <el-form-item label="开始时间" prop="startTime">
                <el-date-picker
                    v-model="suggestionForm.startTime"
                    type="date"
                    placeholder="选择日期" style="width:200px">
                </el-date-picker>
            </el-form-item>
            <el-form-item label="结束时间" prop="endTime">
                <el-date-picker
                    v-model="suggestionForm.endTime"
                    type="date"
                    placeholder="选择日期" style="width:200px">
                </el-date-picker>
            </el-form-item>
          </div>
          <div style="text-align: right">
            <el-button @click="editButtonCancel">取 消</el-button>
            <el-button @click="deleteButton">删 除</el-button>
            <el-button type="primary" @click="saveButton">保存浮动菜单</el-button>
          </div>
        </el-form>
        <el-dialog
        title="选择地址"
        :visible.sync="selectAddressOpen"
        width="820px"
        append-to-body
        :close-on-click-modal="false"
        >
            <div>
                <Position :maker-position.sync="position" @position="loadPosition"/>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="selectAddressOpen = true">确 定</el-button>
                <el-button @click="selectAddressOpen = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import {materialList} from "@/api/resources";
import {dictSuggestionType} from "@/assets/initdata";
import Position from "@/components/map/AMap";
export default {
    name: "SuggestionButtonForm",
    components: {
      Position,
    },
    props:{
        appId:{
            type:String | Number,
            default:''
        },

        buttonType:{//0 浮动按钮  1 卡片按钮
            type:String | Number,
            default:0
        },
        button:{
            type:Object,
            default () {
                return {}
            }
        }
    },
    watch:{
        appId : {
			handler(val, oldval) {
				materialList({appId:val,types:'4,5'}).then((response) => {
                    this.webpageList = response.data
                });
			},
			immediate: true,
			deep: true
		},
        button:{
            handler(val, oldval) {
				this.configButtonForm()
			},
			immediate: true,
			deep: true
        }
    },

    data() {
        var validateFilePosition = (rule, value, callback) => {
            if (this.sugPosition == null) {
            callback(new Error('请在地图上选择地理位置'));
            } else {
            callback();
            }
        };
        return {
            buttonTypes:dictSuggestionType(),
            validateFilePosition,
            suggestionForm: {
                type:"1"
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
                ],
                title: [
                    {required: true, message: "请输入活动名称", trigger: "blur"},
                ],
                description: [
                    {required: true, message: "请输入活动详情", trigger: "blur"},
                ],
                startTime: [
                    {required: true, message: "请输入活动开始时间", trigger: "blur"},
                ],
                endTime: [
                    {required: true, message: "请输入活动结束时间", trigger: "blur"},
                ],
            },
            position: [121.5273285, 31.21515044],
            webpageList:[],//打开网页的时候 获取网页
            selectAddressOpen: false
        };
    },
    mounted() {
        if(this.appId){
            // materialList({appId:this.appId,types:'4,5'}).then((response) => {
            //     this.webpageList = response.data
            // });
        }

    },
    methods:{
        onButtonTypeChanged(buttonType){
            this.$refs.suggestionForm.validateField('type')
        },
        configButtonForm(){
            var form = {
                type:"1"
            }
            if(this.$refs["suggestionForm"]){
                this.$refs["suggestionForm"].resetFields();
            }
            if(this.button.reply){
                form.type = '1'
                form.displayText = this.button.reply.displayText
                let data = JSON.parse(this.button.reply.postback.data);
                form.customAction = data.action;
            }
            if(this.button.action){
                form.displayText = this.button.action.displayText
                if(this.button.action.urlAction){
                    form.type = '2'
                    form.url = this.button.action.urlAction.openUrl.url
                    form.application = this.button.action.urlAction.openUrl.application
                }
                if(this.button.action.dialerAction){
                    form.type = '3'
                    form.phoneNumber = this.button.action.dialerAction.dialPhoneNumber.phoneNumber
                }
                if(this.button.action.mapAction){
                    form.type = '4'
                    form.latitude = this.button.action.mapAction.showLocation.location.latitude
                    form.longitude = this.button.action.mapAction.showLocation.location.longitude
                    this.position = [this.suggestionForm.latitude, this.suggestionForm.longitude]
                }
                if(this.button.action.calendarAction){
                    form.type = '4'
                    form.startTime = this.button.action.calendarAction.createCalendarEvent.startTime
                    form.endTime = this.button.action.calendarAction.createCalendarEvent.endTime
                    form.title = this.button.action.calendarAction.createCalendarEvent.title
                    form.description = this.button.action.calendarAction.createCalendarEvent.description
                }
            }
            this.suggestionForm = Object.assign({},form)
        },
        loadPosition(position) {
            console.log(JSON.stringify(position));
            this.selectAddressOpen = false
            this.sugPosition = position;
            this.suggestionForm.longitude = position.lng;
            this.suggestionForm.latitude = position.lat;
            this.$refs.suggestionForm.validateField("position");
        },
        saveButton(){
            let _this = this;
            _this.$refs["suggestionForm"].validate((valid) => {
                if (valid) {
                    var button = {
                        "reply":{
                            "displayText":"Yes",
                            "postback":{
                                "data":"set_by_chatbot_reply_yes"
                            }
                        }
                    }
                    if(this.suggestionForm.type == '1'){
                        button = {
                            "reply":{
                                "displayText":this.suggestionForm.displayText,
                                "postback":{
                                    "data":"{\"type\":\"sug\",\"id\":\"0\",\"action\":\""+this.suggestionForm.customAction+"\"}"
                                }
                            }
                        }
                    }
                    if(this.suggestionForm.type == '2'){
                        button = {
                            "action":{
                                "urlAction":{
                                    "openUrl":{
                                        "url":this.suggestionForm.url,
                                        "application": this.suggestionForm.application
                                    }
                                },
                                "displayText":this.suggestionForm.displayText,
                                "postback":{
                                    "data":this.suggestionForm.displayText
                                }
                            }
                        }
                    }
                    if(this.suggestionForm.type == '3'){
                        button = {
                            action: {
                                dialerAction: {
                                    dialPhoneNumber: {
                                        phoneNumber: this.suggestionForm.phoneNumber,
                                    },
                                },
                                displayText: this.suggestionForm.displayText,
                                postback: {
                                    data: this.suggestionForm.displayText,
                                },
                            },
                        }
                    }
                    if(this.suggestionForm.type == '4'){
                        let _url = "https://www.google.com/maps/@" + this.suggestionForm.latitude + "," + this.suggestionForm.longitude + ",15z";
                        button = {
                            action: {
                                mapAction: {
                                    showLocation: {
                                        location: {
                                            latitude: this.suggestionForm.latitude,
                                            longitude: this.suggestionForm.longitude,
                                            label: this.suggestionForm.displayText,
                                        },
                                        fallbackUrl: _url,
                                    },
                                },
                                displayText: this.suggestionForm.displayText,
                                postback: {
                                    data: this.suggestionForm.displayText,
                                },
                            },
                        }
                    }
                    if(this.suggestionForm.type == '5'){
                        button = {
                            action: {
                                calendarAction: {
                                    createCalendarEvent: {
                                        startTime: this.suggestionForm.startTime,
                                        endTime: this.suggestionForm.endTime,
                                        title: this.suggestionForm.title,
                                        description: this.suggestionForm.description,
                                    },
                                },
                                displayText: this.suggestionForm.displayText,
                                postback: {
                                    data: this.suggestionForm.displayText,
                                },
                            },
                        }
                    }
                    this.$emit('on-save',button)
                }
            });
        },
        deleteButton(){
            this.$emit('on-delete')
        },
        editButtonCancel(){
            this.$emit('on-cancel')
        }
    }
}
</script>
