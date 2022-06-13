<template>
    <div class="property-container">
        <div class="scene_detail_wrap" v-if="type == 'canvas'">
            <div class="title">场景</div>
            <div class="detail_conatiner">
                <el-form  ref="form" class="scene_name_form">
                    <el-form-item label="场景名称">
                        <el-input
                            v-model="sceneName"
                            placeholder="请输入场景名称"
                            clearable
                            size="small"
                            @input="onInputSceneName"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="chatbot">
                        <el-select v-model="selectAppId" placeholder="请选择chatBot" size="small" style="width:100%" @change="onSelectApp">
                            <el-option
                                v-for="item in applicationList"
                                :key="item.id + ''"
                                :label="item.name"
                                :value="item.id + ''"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <div class="scene_detail_wrap" v-if="type == 'node'">
            <div class="title">组件信息</div>
            <div class="detail_conatiner">
                <el-form ref="form" class="scene_name_form">
                    <el-form-item :label="getNodeTypeName()" prop="name">
                        <!-- //type 0 开始 1 消息模板 2 建议回复 3场景 4关键词 5逻辑判断 6条件 -->
                        <el-select v-if="node.options.type == 1" v-model="selectMsgId" placeholder="请选择消息模板" clearable @change="onSelectMsgTemplate" size="small" style="width:100%">
                            <el-option v-for="item in messageTemplateList" :key="item.id + ''" :label="item.name" :value="item.id + ''"></el-option>
                        </el-select>
                        
                        <el-select v-if="node.options.type == 3" v-model="selectSceneId" placeholder="请选择场景" clearable @change="onSelectScene" size="small" style="width:100%">
                            <el-option v-for="item in sceneList" :key="item.id" :label="item.name" :value="item.id"></el-option>
                        </el-select>
                        <el-input
                            v-if="node.options.type == 4"
                            v-model="node.options.label"
                            placeholder="请输入关键词"
                            clearable
                            size="small"
                            @input="onInputNodeLabel"
                        ></el-input>
                        <el-input
                            v-if="node.options.type == 5"
                            v-model="node.options.label"
                            placeholder="请输入判断条件"
                            clearable
                            size="small"
                            @input="onInputNodeLabel"
                        ></el-input>
                        <el-input
                            v-if="node.options.type == 6"
                            v-model="node.options.label"
                            placeholder="请输入条件"
                            clearable
                            size="small"
                            @input="onInputNodeLabel"
                        ></el-input>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <div class="scene_detail_wrap" v-if="type == 'edge'">
            <div class="title">连线</div>
            <div class="scene_name">
                <el-form ref="form" class="scene_name_form">
                    <el-form-item label="内容" prop="name">
                        <el-input
                            v-model="name"
                            placeholder="请输入连线内容"
                            clearable
                            size="small"
                            @input="onInputEdgeLabel"
                        ></el-input>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>
<script>
import {
    listApplication
} from "@/api/application";
import { listMessageTemplate} from "@/api/messageTemplate";
export default {
    name:'PropertyInspector',
    props: {
        sceneName: {
            type: String,
            default() {
                return '';
            },
        },
        appId: {
            type: String,
            default() {
                return '';
            },
        },
        inspectorType: {
            type: String,
            default() {
                return 'canvas';
            },
        },
        node:{
            type: Object,
            default() {
                return 0;
            },
        },
        edge:{
            type: Object,
            default() {
                return 0;
            },
        }
    },
    watch: {
        node(val){
            this.selectMsgId = ''
        },
        inspectorType(val) {
            console.log(val)
            this.type = val
        },
        appId(val){
            this.selectAppId = val
            this.getMsgTemplateList()
        }
    },
    data(){
        return{
            type:'canvas',
            applicationList: [],
            messageTemplateList: [],
            selectAppId:this.appId,
            sceneList:[],
            selectMsgId:''
        }
    },
    created(){
        this.getAppList()
    },
    methods:{
        getNodeTypeName(){
            if(this.node && this.node.options){
                if(this.node.options.type == 0){
                    return '开始'
                }
                if(this.node.options.type == 1){
                    return '消息模板'
                }
                if(this.node.options.type == 2){
                    return '建议回复'
                }
                if(this.node.options.type == 3){
                    return '场景'
                }
                if(this.node.options.type == 4){
                    return '关键词'
                }
                if(this.node.options.type == 5){
                    return '逻辑判断'
                }
                if(this.node.options.type == 6){
                    return '条件'
                }
            }
            return ''
        },
        onInputSceneName(e){
            this.$emit('onInputSceneName',e)
        },
        onSelectApp(e){
            console.log(e)
            this.selectAppId = e + '';
            var app = {}
            this.applicationList.forEach(item=>{
                if(item.id == e){
                    app = item
                }
            })
            this.$emit('onSelectApp',app)
            this.getMsgTemplateList()
        },
        onSelectMsgTemplate(e){
            this.selectMsgId = e
            var msg = {}
            this.messageTemplateList.forEach(item=>{
                if(item.id == e){
                     console.log(e)
                    msg = item
                }
            })
            console.log(msg)
        
            this.$emit('onSelectMsgTemplate',msg)
        },
        onSelectScene(e){
            this.selectSceneId = e
            console.log(e)
            var scene = {}
            this.sceneList.forEach(item=>{
                if(item.id == e){
                    scene = item
                }
            })
            this.$emit('onSelectScene',scene)
        },
        onInputNodeLabel(e){
            this.node.updateLabel(e)
            this.$emit('onInputNodeLabel',e)
        },
        onInputEdgeLabel(e){
            
            this.edge.updateLabel(e)
            this.$emit('onInputEdgeLabel',e)
        },
        /** 查询应用信息列表 */
        getAppList() {
            this.loading = true;
            listApplication({
                currentPage: 1,
                pageSize: 10000,
                params: {name: null, customerId: null}
            }).then(response => {
                this.applicationList = response.data.items;
            });
        },
        getMsgTemplateList(){
            listMessageTemplate({
                currentPage: 0,
                pageSize: 1000,
                params: {
                    appId: this.selectAppId,
                }
            }).then(response => {
                this.messageTemplateList = response.data.items;
            });
        }

    }
}
</script>
<style lang="less" scoped>
.property-container{
    border-left: 1px solid #e6e9ed;
    width: 300px;
    height: 100%;
    position: relative;
}
.scene_detail_wrap{
    .title{
        height: 32px;
        border-top: 1px solid #dce3e8;
        border-bottom: 1px solid #dce3e8;
        background: #ebeef2;
        color: #000;
        line-height: 28px;
        padding-left: 12px;
    } 
    .detail_conatiner{
        padding: 8px;
    }
}
</style>