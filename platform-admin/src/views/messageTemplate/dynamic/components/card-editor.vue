<template>
    <div class="card-edit-wrap clearfix">
      <div class="left-simulator-wrap">
        <div class="card-op-wrap">
          <el-form label-width="100px" ref="cardForm" :model="layout" style="display:flex">
            <el-form-item label="卡片布局" required prop="cardOrientation" style="width:220px">
              <el-select
                v-model="layout.cardOrientation"
                placeholder="请选择卡片布局"
                size="small"
              >
                <el-option
                  :key="'VERTICAL'"
                  :label="'垂直布局'"
                  :value="'VERTICAL'"
                ></el-option>
                <el-option
                  :key="'HORIZONTAL'"
                  :label="'水平布局'"
                  :value="'HORIZONTAL'"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="卡片宽度" required prop="cardWidth" style="width:200px">
              <el-select
                v-model="layout.cardWidth"
                placeholder="请选择卡片布局"
                size="small"
              >
                <el-option
                  :key="'SMALL_WIDTH'"
                  :label="'小'"
                  :value="'SMALL_WIDTH'"
                ></el-option>
                <el-option
                  :key="'MEDIUM_WIDTH'"
                  :label="'中'"
                  :value="'MEDIUM_WIDTH'"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item v-if="layout.cardOrientation == 'HORIZONTAL'" label="素材位置" required prop="imageAlignment" style="width:200px">
              <el-select
                v-model="layout.imageAlignment"
                placeholder="请选择卡片布局"
                size="small"
              >
                <el-option
                  :key="'LEFT'"
                  :label="'左'"
                  :value="'LEFT'"
                ></el-option>
                <el-option
                  :key="'RIGHT'"
                  :label="'右'"
                  :value="'RIGHT'"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <div style="text-align: right">
            <el-button @click="cancel">取 消</el-button>
            <el-button type="primary" @click="saveCard">保存卡片</el-button>
          </div>
        </div>
        <div class="card-grid-wrap">
          <div class="rescources-card-wrap" v-for="(cardInfo,index) in cards"
            :id="'cardWrap' + index"
            :key="index"
            :class="selectCardIndex + '' == index + '' ? 'select':''"
            @click="selectCard(index,cardInfo)">
            <div class="rescources-card-item-wrap"
              :class="layout.cardWidth == 'SMALL_WIDTH' ? 'smallWidth':''"
            >
              <div class="rescources-card-item-content-wrap" 
              :class="layout.cardOrientation == 'HORIZONTAL' ? layout.imageAlignment == 'LEFT' ? 'horizontal-left':'horizontal-right':''">
                <div class="media-wrap"
                  :class="cardInfo.height == 'TALL_HEIGHT'?'big':cardInfo.height == 'SHORT_HEIGHT'?'small':''"
                  v-if="cardInfo.material != null && JSON.stringify(cardInfo.material) != '{}'"
                  :style="cardInfo.material.type == 2 ? 'background-image: url(' +  cardInfo.material.thumbnailUrl + ');' : ''"
                  >
                    <img  v-if="cardInfo.material.type == 1"
                      class="grid-item-img"
                      :src="(cardInfo.material.thumbnailUrl !== null && cardInfo.material.thumbnailUrl != '' )? cardInfo.material.thumbnailUrl:cardInfo.material.sourceUrl "/>
                    <video v-if="cardInfo.material.type == 3"
                      class="grid-item-img"
                      :src="cardInfo.material.sourceUrl"
                      controls="controls"
                      :poster="cardInfo.material.thumbnailUrl"></video>
                    <audio v-if="cardInfo.material.type == 2"
                      class="grid-item-img audio"
                      :src="cardInfo.material.sourceUrl"
                      controls="controls"></audio>
                </div>
                <div class="card-cont-wrap">
                  <div class="card-title">{{ cardInfo.title }}</div>
                  <div class="card-content">{{ cardInfo.description }}</div>
                </div>  
              </div>
              <div style="width: 100%">
                <div class="card-btn"
                  v-for="(buttonItem, buttonIndex) in cardInfo.suggestions"
                  :key="buttonIndex"
                  :class="((selectCardIndex + '' == index + '') && (selectButonIndex + '' == buttonIndex + '')) ? 'select':''"
                  @click="onSelectCardButton(index,buttonIndex)"
                  @click.stop="">
                  {{ getCardButtonDisplayText(buttonItem) }}
                </div>
              </div>
               
            </div>

          </div>
          <div class="create-card" v-if="(cardType == 2 && cards.length < 1) || (cardType == 3 && cards.length < 4)" @click="addCardAction">
            <i class="el-icon-plus select-resource-plus">添加卡片</i>
          </div>
        </div>
      </div>
      <div class="right-info-wrap" style="text-align: left" v-if="tempCard && JSON.stringify(tempCard)">
        <el-form label-width="150px" ref="cardForm" :model="tempCard" :rules="cardRules">
          <el-form-item label="添加媒体" required prop="material">
            <div class="select-resource-wrap">
              <div
                class="select-resource-item"
                @click="selectResource('image')">
                <img
                  v-if="
                    tempCard.material != null &&
                    JSON.stringify(tempCard.material) != '{}' &&
                    tempCard.material.type == 1"
                  class="resource-file-wrap"
                  :src="tempCard.material.sourceUrl"/>
                <i v-else class="el-icon-plus select-resource-plus">添加图片</i>
              </div>
              <div
                class="select-resource-item"
                @click="selectResource('video')">
                <video
                  v-if="
                    tempCard.material != null &&
                    JSON.stringify(tempCard.material) != '{}' &&
                    tempCard.material.type == 3
                  "
                  class="resource-file-wrap"
                  autoplay
                  :src="tempCard.material.sourceUrl"
                ></video>
                <i v-else class="el-icon-plus select-resource-plus">添加视频</i>
              </div>
              <div
                class="select-resource-item"
                @click="selectResource('audio')"
                :style="
                  tempCard.material.type == 2
                    ? 'background-image: url(' +
                      tempCard.material.thumbnailUrl +
                      ');'
                    : ''
                ">
                <audio
                  class="resource-file-wrap audio"
                  v-if="
                    tempCard.material != null &&
                    JSON.stringify(tempCard.material) != '{}' &&
                    tempCard.material.type == 2
                  "
                  controls>
                  <source :src="tempCard.material.sourceUrl" type="audio/ogg"/>
                  <source
                    :src="tempCard.material.sourceUrl"
                    type="audio/mpeg"
                  />
                  您的浏览器不支持 audio 元素。
                </audio>
                <i v-else class="el-icon-plus select-resource-plus">添加音频</i>
              </div>
            </div>
            <div
              v-if="
                tempCard.material != null &&
                JSON.stringify(tempCard.material) != '{}'
              "
            >
              已选择{{
                tempCard.material.type == 1
                  ? "图片"
                  : tempCard.material.type == 3
                  ? "视频"
                  : "音频"
              }}
              : {{ tempCard.material.name }}
            </div>
            <span style="color: red">*注：以上媒体三选一</span>
          </el-form-item>
          <el-form-item label="卡片标题">
            <el-input
              v-model="tempCard.title"
              placeholder="请输入卡片标题"
              clearable
              size="small"
            ></el-input>
          </el-form-item>
          <el-form-item label="卡片摘要">
            <el-input
              v-model="tempCard.description"
              placeholder="请输入卡片摘要"
              clearable
              size="small"
            ></el-input>
          </el-form-item>
          <el-form-item label="卡片高度" >
            <el-radio-group v-model="tempCard.height" size="small">
              <el-radio label="TALL_HEIGHT" border>大</el-radio>
              <el-radio label="MEDIUM_HEIGHT" border>中</el-radio>
              <el-radio label="SHORT_HEIGHT" border>小</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="卡片按钮" v-if="(tempCard && tempCard.suggestions &&  tempCard.suggestions.length < 4) || (!tempCard || !tempCard.suggestions || tempCard.suggestions.length == 0) || tempButton">
            <el-button
            v-if="!tempButton"
            class="button-new-tag"
            size="small"
            @click="showAddSuggestion"
          >+ 添加卡片按钮
          </el-button>
          <button-form v-if="tempButton" :appId="appId" :button="tempButton" @on-save="onSaveCardButton" @on-cancel="onCancelSelectButton" @on-delete="onDeleteSelectButton"></button-form>
          </el-form-item>
          <div style="text-align: left">
            <el-button @click="editCardCancel">取 消</el-button>
            <el-button @click="deleteCard">删 除</el-button>
          </div>
        </el-form>
    </div>
    <ResourcesSelector
        ref="selector"
        @select-success="selectResourcesSuccess"
    ></ResourcesSelector>
  </div>
</template>
<script>
import {materialList} from "@/api/resources";
import {dictSuggestionType} from "@/assets/initdata";
import Position from "@/components/map/AMap";
import ButtonForm from './button-form.vue'
import ResourcesSelector from "../../../resources/components/resources-selector";
export default {
    name: "SuggestionButtonForm",
    components: {
      ButtonForm,
      Position,
      ResourcesSelector
    },
    watch:{
        
    },
    props:{
        appId:{
            type:String | Number,
            default:''
        },
        customerId:{
            type:String | Number,
            default:''
        },
        cardType:{
            type:String | Number,
            default:''
        },
        cards:{
            type:Array,
            default () {
                return []
            }
        },
        layout:{
          type:Object,
          default () {
              return {
              "cardWidth":"MEDIUM_WIDTH",
              "cardOrientation":"VERTICAL",
              "imageAlignment":"LEFT"
            }
          }
        }
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
        return {
            selectCardIndex:'',
            tempCard:null,
            material:{},
            visible:false,
            cardRules: {
                name: [{validator: validateCardName, trigger: ["blur", "change"]}],
                material: [
                {validator: validateMaterial, trigger: ["blur", "change"]}
                ]
            },
            cardButtons:[],
            selectButonIndex:'',
            tempButton:null,
            
        };
    },
    created() {
      
    },
    methods:{
      //卡片按钮 相关处理
      //添加卡片按钮
      showAddSuggestion(){
        this.tempButton = {}
      },
      getCardButtonDisplayText(button){
        if(button.displayText){
          return button.displayText
        }
        if(button.reply && button.reply.displayText){
          return button.reply.displayText
        }
        if(button.action && button.action.displayText){
          return button.action.displayText
        }
        return ""
      },
      onSelectCardButton(index,buttonIndex){
        this.selectCard(index,this.cards[index])
        this.selectButonIndex = buttonIndex
        this.tempButton = this.cardButtons[buttonIndex]
      },
      onCancelSelectButton(){
        this.tempButton = ""
        this.selectButonIndex = ''
      },
      onDeleteSelectButton(){
        this.tempButton = ""
        this.cardButtons.splice(this.selectButonIndex,1)
        this.selectButonIndex = ''
      },
      onSaveCardButton(button){
        if(this.selectButonIndex + '' != ''){
          this.cardButtons[this.selectButonIndex] = button
        }else{
          this.cardButtons.push(button)
        }
        this.tempButton = ""
        this.selectButonIndex = ''
      },
      //选择素材
      selectResource(selectType) {
        if (this.customerId == null || this.appId == null) {
          this.$message({
            message: "请先选择应用",
            type: "warning",
          });
          return;
        }
        this.$refs.selector.open({
          customerId: this.customerId,
          appId: this.appId,
          resourcesType: selectType,
        });
      },
      selectResourcesSuccess(item){
        this.tempCard.material = item
        this.tempCard.file = item.id
        this.$forceUpdate()
      },
      //卡片操作
      addCardAction(){
        var card = {
          "title": "",
          "description": "",
          "height": "MEDIUM_HEIGHT",
          "suggestions": [],
          "file": "",
          "material" : ""
      	}
        this.cards.push(card)
        this.selectCard(this.cards.length - 1,this.cards[this.cards.length - 1])
        this.$nextTick(() => {
          const cardID = document.querySelector('#cardWrap' + this.selectCardIndex)
          cardID.scrollIntoView({ behavior:"smooth", block: "start", inline: "nearest"})
        })
      },
      selectCard(index,card){
        this.selectCardIndex = index
        this.tempCard = card
        this.cardButtons = card.suggestions

        this.selectButonIndex = ''
        this.tempButton = null

        const cardID = document.querySelector('#cardWrap' + this.selectCardIndex)
        cardID.scrollIntoView({ behavior:"smooth", block: "start", inline: "nearest"})
      },
      editCardCancel(){
        this.selectCardIndex = ''
        this.tempCard = null
        this.cardButtons = []
      },
      deleteCard(){
        this.cards.splice(this.selectCardIndex,1)
        this.selectCardIndex = ''
        this.tempCard = null
        this.cardButtons = []
      },
      cancel(){
        this.$emit('onCancel')
      },
      saveCard(){
        this.cards.forEach((element,index) => {
          if(!element.file || element.file == ""){
            this.$message({
              message: "第" + (index + 1) + "张卡片，未添加媒体文件",
              type: "warning",
            });
            return
          }
          if(!element.title || element.title == ""){
            this.$message({
              message: "第" + (index + 1) + "张卡片，未添加卡片标题",
              type: "warning",
            });
            return
          }
          if(!element.description || element.description == ""){
            this.$message({
              message: "第" + (index + 1) + "张卡片，未添加卡片摘要",
              type: "warning",
            });
            return
          }
          
        });
        this.selectCardIndex = ''
        this.tempCard = null
        this.cardButtons = []
        this.selectButonIndex = ''
        this.tempButton = ""
        this.$emit('onCompleteEditCards',this.cards,this.layout)
      }
    }
}
</script>
<style lang="less" scoped>
.card-edit-wrap {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #fff;
  margin: 0 auto;
  display: flex;
  flex-direction: row;
  padding: 30px;
  overflow-x: auto;
}

.left-simulator-wrap {
  float: left;
  width: 60%;
}
.create-card{
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #d9d9d9;
  position: relative;
  margin: 10px;
  width: 280px;
  height: 280px;
  display: flex;
}
.create-card:hover{
  border: 1px dashed #1a1aa0;
  color: #1a1aa0;
}

.right-info-wrap {
  position: relative;
  float: left;
  width: 600px;
  padding: 10px;
  margin-left: 20px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;

}


.card-grid-wrap {
  width: 100%;
  display: -webkit-box;
  flex-wrap: nowrap;
  overflow-x: scroll;
}
.rescources-card-wrap {
  margin: 10px;
  
  display: flex;
  flex-direction: column;
  background: #fff;
}
.select{
  border: 1px solid #1a1aa0;
}

.rescources-card-item-wrap {
  width: 320px;
  border-radius: 10px;
  padding: 10px;
  background: #f3f3f3;
  flex: 1;
  .rescources-card-item-content-wrap{
    display: flex;
    flex-direction: column;
  }
  .horizontal-left{
    display: flex;
    flex-direction: row;
    .card-cont-wrap{
      margin: 10px;
    }
  }
  .horizontal-right{
    display: flex;
    flex-direction: row-reverse;
    .card-cont-wrap{
      margin: 10px;
    }
  }
  .media-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 200px;
    background-color: #fff;
    position: relative;
    background-size: cover;
    background-repeat: no-repeat;
    .grid-item-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    .audio{
      width: 80%;
      position: absolute;
      bottom: 10px;
      left:10%;
      height: 30px;
    }
    
  }
  .small{
    height: 150px;
  }
  .big{
    height: 250px;
  }
  .card-cont-wrap {
    // width: 100%;
    display: flex;
    flex-direction: column;

    // height: 100%;
    width: 100%;
    .card-title {
      line-height: 24px;
      font-weight: 600;
      font-size: 17px;
      color: #25292e;
      -webkit-line-clamp: 2;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-top: 10px;
    }
    .card-content {
      overflow: hidden;
      font-size: 14px;
      color: #333333;
      text-overflow: ellipsis;
      -webkit-line-clamp: 4;
      display: -webkit-box;
      -webkit-box-orient: vertical;
    }
  }
}
.smallWidth{
   width: 280px;
}
.card-btn {
  display: flex;
  font-size: 16px;
  width: 100%;
  color: #25292e;
  margin-top: 4px;
  padding: 0;
  background: #fff;
  line-height: 38px;
  height: 38px;
  border-radius: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-decoration: none;
  text-align: center;
  justify-content: center;
  margin-top: 10px;
}
.card-op-warp{
    position: absolute;
    right: 10px;
    top: 10px;
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
</style>