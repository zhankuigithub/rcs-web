<template>
    <div class="simulator-wrap">
        <div class="chat-head flex align-items-center">
            <img v-if="appInfo != null" class="chat-head-avatar" :src="appInfo.logoUrl"/>
            <span v-if="appInfo != null" class="chat-head-text">{{appInfo.name}}</span>
        </div>

        <div class="roll-cont">
            <div v-if="messageType + '' == '1' && textMsg.length > 0" class="msg-warp flex-row receivemsg-warp">
				        <img v-if="appInfo != null" class="chat-head-avatar" :src="appInfo.logoUrl"/>
                <div class="flex-row justify-content-start align-items-start flex-wrapper receive-txt-warp">
				        	<div class="text-msg receive-txt">
						        {{ textMsg }}
					        </div>
				        </div>
			      </div>
            <div v-if="(messageType + '' == '2' || messageType + '' == '3') && cards.length > 0  " class="msg-warp receivemsg-warp">
                <img v-if="appInfo != null && cards.length > 0" class="chat-head-avatar" :src="appInfo.logoUrl"/>
                <ul id="cardDomObj"  class="card-wrap-ul">
                    <li class="card-wrap-ul-li" v-for="(cardInfo,index) in cards" :key="index" :class="index == 0?'multi-card-item-first':''">
                        <div v-if="JSON.stringify(cardInfo.material) != '{}' || cardInfo.suggestions.length > 0 || cardInfo.title.length > 0 || cardInfo.description.length > 0" class="card-wrap" :class="cards.length == 1 ? 'single-card':'muti-card'">
                            <div :class="orientation + '' == '2'?  (imageAlignment + '' == '1'?  'card-horizontal':'card-horizontal-right'):'card-vertical'">
                              <div class="card-head">
                                <div class="media-wrap" :class="cardInfo.height == 3?'big':cardInfo.height == 1?'small':''" v-if="cardInfo.material" :style="cardInfo.material.type == 2 ? 'background-image: url(' +  cardInfo.material.thumbnailUrl + ');' : ''">
                                    <img v-if="cardInfo.material.type + '' == '1'" class="grid-item-img" :src="cardInfo.material.thumbnailUrl !== null ? cardInfo.material.thumbnailUrl:(cardInfo.material.fileUrl != undefined && cardInfo.material.fileUrl.length > 0) ? cardInfo.material.fileUrl:cardInfo.material.sourceUrl">
                                    <video v-if="cardInfo.material.type + '' == '3'" class="grid-item-img" :src="(cardInfo.material.fileUrl != undefined && cardInfo.material.fileUrl.length > 0) ? cardInfo.material.fileUrl:cardInfo.material.sourceUrl" controls="controls" :poster="cardInfo.material.thumbnailUrl"></video>
                                    <audio v-if="cardInfo.material.type + '' == '2'" class="grid-item-img audio" :src="(cardInfo.material.fileUrl != undefined && cardInfo.material.fileUrl.length > 0) ? cardInfo.material.fileUrl:cardInfo.material.sourceUrl" controls="controls"></audio>
                                </div>
                              </div>
                              <div class="card-cont-wrap">
                                  <div v-if="cardInfo.title + '' != '' || cardInfo.description + '' != ''">
                                      <div class="card-txt card-title">{{cardInfo.title}}</div>
                                      <div class="card-txt card-content">{{cardInfo.description}}</div>
                                  </div>
                                  <div class="card-buttons-wrap" style="width: 100%;">
                                    <div class="card-btn align-items-center justify-content-center" v-for="(buttonItem,index) in cardInfo.suggestions" :key="index" @click="onCardButtonClick(buttonItem,index)">
                                        <span>{{ getCardButtonDisplayText(buttonItem) }}</span>
                                    </div>
                                  </div>
                              </div>
                            </div>
                        </div>

                    </li>
                </ul>
            </div>
            <div v-if="messageType + '' == '4' && position !== null && position.length != '{}'" class="msg-warp receivemsg-warp">
				        <img v-if="appInfo != null" class="chat-head-avatar" :src="appInfo.logoUrl"/>
                <div class="flex-row justify-content-start align-items-start flex-wrapper pl">
					        <div class="justify-content-center align-items-start msg-image-wrap">
                    <el-amap zoom="15" style="width:320px;height:240px" :center="[position.lng,position.lat]">
                      <el-amap-marker :position="[position.lng,position.lat]" />
                    </el-amap>
					        </div>
			          </div>
		        </div>
            <div v-if="messageType + '' == '5' && mediaResources.type + '' == '1'" class="msg-warp receivemsg-warp">
				      <img v-if="appInfo != null" class="chat-head-avatar" :src="appInfo.logoUrl"/>
              <div class="flex-row justify-content-start align-items-start flex-wrapper">
					      <div class="justify-content-center align-items-start msg-image-wrap">
						      <img class="msg-image" :src="mediaResources.sourceUrl" />
					      </div>
				      </div>
			      </div>
            <div v-if="messageType + '' == '6' && mediaResources.type + '' == '2'" class="msg-warp receivemsg-warp">
				<img v-if="appInfo != null" class="chat-head-avatar" :src="appInfo.logoUrl"/>
                <div class="flex-row justify-content-start align-items-start flex-wrapper pl">
					<div class="justify-content-center align-items-start msg-image-wrap">
                        <audio controls>
                            <source :src="mediaResources.sourceUrl" type="audio/ogg" />
                            <source :src="mediaResources.sourceUrl" type="audio/mpeg" />
                            您的浏览器不支持 audio 元素。
                        </audio>
					</div>
				</div>
			</div>
            <div v-if="messageType + '' == '7' && mediaResources.type + '' == '3'" class="msg-warp receivemsg-warp">
				<img v-if="appInfo != null" class="chat-head-avatar" :src="appInfo.logoUrl"/>
                <div class="flex-row justify-content-start align-items-start flex-wrapper pl">
					<div class="justify-content-center align-items-start msg-image-wrap">
                        <video class="msg-image" controls :poster="mediaResources.thumbnailUrl">
                            <source :src="mediaResources.sourceUrl" type="video/mp4" />
                            <source :src="mediaResources.sourceUrl" type="video/ogg" />
                            您的浏览器不支持Video标签。
                        </video>
					</div>
				</div>
			</div>
          <div v-if="smsContent !=null">
             <span></span><span style="font-size: 12px;font-weight: bold;margin-left: 15%;color: red">短信回落内容：</span><br/>
             <span style="font-size: 12px;font-weight: bold;margin-left: 15%">{{smsContent}}</span>
          </div>
		</div>
        <div class="bottom-warp footer">
			<div id="floatingButton" class="float-menu-content flex-row">
          <div v-for="(item,index) in floatingButtons" :key="index" class="float-menu-item" :class="selectFloatBtnIndex + '' == index + '' ? 'active':''" @click="onFloatButtonClick(index)">
              {{ item }}
          </div>
			</div>
			<div class="buttom-menu-wrap flex">
			  <div class="config-menu-wrap flex-row align-items-center">
                <div class="switch-buttom-wrap flex align-items-center"><i class="el-icon-menu"></i></div>
            </div>
        </div>
		</div>
    </div>
</template>
<script>
import AMap from "@/components/map/AMap";
import { getApplication } from "@/api/application";
export default {
  components: { AMap },
  name: "MessageTemplatePreview",
  data() {
    return {
      appInfo: null,
      cardDomObj:null,
      floatingButton:null,
      selectFloatBtnIndex:''
    };
  },
  props: {
    appId: {
      type: String,
      default() {
        return "";
      },
    },
    messageType: {
    },
    textMsg: {
      type: String,
      default() {
        return "";
      },
    },
    floatingButtons: {
      type: Array,
      default() {
        return [];
      },
    },
    cards: {
      type: Array,
      default() {
        return [];
      },
    },
    mediaResources:{
        type: Object,
        default() {
            return {};
        },
    },
    position:{

    },
    orientation:{
        type: String,
        default() {
            return '1';
        },
    },
    imageAlignment:{
        type: String,
        default() {
            return '1';
        },
    },
    smsContent: {
      type: String,
      default() {
        return null;
      },
    },
    selectFloatButtonIndex:{
      type: String | Number,
      default() {
        return '';
      },
    }
  },
  watch: {
    appId(val) {
      this.getAppInfo(val);
    },
    floatingButtons(val){
        this.$nextTick(() => {
            this.floatingButtonScrollFunction();
        })
    },
    cards(val) {
        this.$nextTick(() => {
            this.scrollFunction();
        })
        console.log('cards' + JSON.stringify(this.cards))
    },
    position(val){
      console.log(val)
    },
    orientation(val){
      console.log('orientation' + val)
    },
    selectFloatButtonIndex(val){
        this.selectFloatBtnIndex = val
    }
  },
  created(){
      this.getAppInfo(this.appId)
  },
  methods: {
    getAppInfo(appId) {
        if(appId == undefined || appId.length == 0){
            return;
        }
        getApplication(appId).then((response) => {
            this.appInfo = response.data;
        });
    },
    scrollFunction () {
        this.cardDomObj = document.getElementById('cardDomObj') // 通过id获取要设置的div
        if(this.cardDomObj == null){
            return
        }
        if (this.cardDomObj.attachEvent) { // IE
            this.cardDomObj.attachEvent('onmousewheel', this.mouseScroll)
        } else if (this.cardDomObj.addEventListener) {
            this.cardDomObj.addEventListener('DOMMouseScroll', this.mouseScroll, false)
        }
        this.cardDomObj.onmousewheel = this.cardDomObj.onmousewheel = this.mouseScroll
    },
    mouseScroll(event) {
        // google 浏览器下
        let detail = event.wheelDelta || event.detail
        let moveForwardStep = -1
        let moveBackStep = 1
        let step = 0
        step = detail > 0 ? moveForwardStep * 100 : moveBackStep * 100
        event.preventDefault() // 阻止浏览器默认事件
        this.cardDomObj.scrollLeft = this.cardDomObj.scrollLeft + step
    },
    floatingButtonScrollFunction () {
        this.floatingButton = document.getElementById('floatingButton') // 通过id获取要设置的div
        if(this.floatingButton == null){
            return
        }
        if (this.floatingButton.attachEvent) { // IE
            this.floatingButton.attachEvent('onmousewheel', this.floatingButtonMouseScroll)
        } else if (this.floatingButton.addEventListener) {
            this.floatingButton.addEventListener('DOMMouseScroll', this.floatingButtonMouseScroll, false)
        }
        this.floatingButton.onmousewheel = this.floatingButton.onmousewheel = this.floatingButtonMouseScroll
    },
    floatingButtonMouseScroll(event){
        let detail = event.wheelDelta || event.detail
        let moveForwardStep = -1
        let moveBackStep = 1
        let step = 0
        step = detail > 0 ? moveForwardStep * 100 : moveBackStep * 100
        event.preventDefault() // 阻止浏览器默认事件
        this.floatingButton.scrollLeft = this.floatingButton.scrollLeft + step
    },
    onCardButtonClick(buttonItem,index) {
      this.$emit('select-card-btn', buttonItem,index)
    },
    onFloatButtonClick(index) {
      this.selectFloatBtnIndex = index
      this.$emit('on-select-floatbtn',index)
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
    }
  },
};
</script>

<style lang="scss" scoped>
.simulator-wrap {
  width: 375px;
  height: 781px;
  background: url("../../assets/images/simulator.png") no-repeat;
  background-size: 100% auto;
  position: relative;
  padding: 34px 20px;
}
.chat-head-avatar {
    width: 30px;
    height: 30px;
    margin: 0 10px 0 0;
    border-radius: 50%;
  }
.chat-head {
  // z-index: 1000;
  width: 100% ;
  background: #fff;
  padding: 10px;
  height: 50px;


}
.chat-head-text {
    font-size: 10px;
    color: #25292e;
    font-weight: 600;
  }
.bottom-warp {
  position: absolute;
  width: calc(100% - 40px);
  bottom: 34px;
  background: #fff;
  min-height: 50px;
}
.footer {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
.buttom-menu-wrap {
  .switch-buttom-wrap {
    height: 54px;
    flex: 0 0 50px;
    display: flex;
    align-self: flex-end;
    justify-content: center;
  }
  .config-menu-wrap {
    height: 54px;
    display: flex;
    flex: 1;
    .switch-buttom-wrap-img {
      width: 28px;
      height: 28px;
    }
    .menu-wrap {
      height: 100%;
      display: flex;
      align-items: center;
      flex: 1;
      .menu-item {
        height: 100%;
        color: #25292e;
        justify-content: center;
        position: relative;
        flex: 1;
        :before {
          content: "";
          position: absolute;
          left: 0;
          top: 50%;
          width: 1px;
          height: 40%;
          -webkit-transform: translateY(-50%) scaleX(0.5);
          transform: translateY(-50%) scaleX(0.5);
          background: #dddee0;
        }
      }
    }
  }
}
.flex {
  display: flex;
}
.flex-row {
  display: flex;
  flex-direction: row;
}
.flex-column {
  display: flex;
  flex-direction: column;
}
.align-items-center {
  display: flex;
  align-items: center;
}
.align-items-start {
  align-items: flex-start;
}
.justify-content-start {
  justify-content: flex-start;
}

.justify-content-end {
  justify-content: flex-end;
}
.justify-content-center {
  justify-content: center;
}
body{ margin:0;padding:0;}


//消息列表
.roll-cont {
    height: calc(100% - 150px);
    position: relative;
    width: 100%;
}

.msg-warp{
    padding: 5px 10px;
}
.text-msg{
    font-size: 17px;
	line-height: 24px;
    word-break: break-all;
    color: #25292e;
	text-align: left;
	padding: 8px 12px;
}
//接收的文本消息
.receivemsg-warp{
    display: flex;
    flex-direction: row;
	.receive-txt-warp{
		padding-right: 32px;
	}
	.receive-txt{
		background: #f0f0f0;
		border-radius: 1px 14px 14px 14px;
	}
}
//单卡片消息
.card-txt{
    background-color: #f3f3f3;
    width: 100%;
    font-size: 17px;
    color: #25292e;
    line-height: 20px;
    text-align: left;
    margin: 4px 0 4px;
    word-break: break-all;
}
.card-btn {
    display: flex;

    width: 100%;
    margin-top: 4px;
    padding: 0 4px;
    background: #fff;
    line-height: 36px;
    height: 36px;
    border-radius: 11px;
    overflow: hidden;

    span{
      text-align: center;
      width: 100%;
      font-size: 16px;
      color: #25292e;
      -webkit-line-clamp: 1;
			-webkit-box-orient: vertical;
			overflow: hidden;
			text-overflow: ellipsis;
    };
}
.card-wrap{
    border-radius: 10px;
    background-color: #f3f3f3;
    overflow: hidden;
    box-sizing: border-box;
    height: 100%;
    padding: 10px;
    .card-vertical{
      display: flex;
      flex-direction: column;
    }
    .card-horizontal{
      display: flex;
      flex-direction: row;
      .card-head{
        width: 50%;
      }
      .card-cont-wrap{
        width: 50%;
        margin: 0 0 0 10px;
      }
      .card-buttons-wrap{
        margin-top: 10px;
      }
    }

    .card-horizontal-right{
      display: flex;
      flex-direction: row-reverse;
      .card-head{
        width: 50%;
      }
      .card-cont-wrap{
        width: 50%;
        margin: 0 10px 0 0;
      }
      .card-buttons-wrap{
        margin-top: 10px;
      }
    }



	.card-head {
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
	}
	.card-cont-wrap {
	    width: 100%;
	    display: flex;
	    flex-direction: column;
		// height: 100%;
        margin-top: 10px;
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
		}
		.card-content{
		    overflow: hidden;
		    font-size: 14px;
		    text-overflow: ellipsis;
		    -webkit-line-clamp: 4;
		    display: -webkit-box;
		    -webkit-box-orient: vertical;
		}
	}
}
.card-wrap-ul{
    padding: 0;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    .card-wrap-ul-li{
        margin-right: 15px;
        display: inline-block;
        vertical-align: top;
        .single-card{
            width: 260px;
        }
        .muti-card{
            width: 230px;
        }
    }
}



//图片
.msg-image-wrap{
	border-radius: 11px 11px 11px 11px;
	background-color: #f3f3f3;
	overflow: hidden;
	box-sizing: border-box;
	height: 100%;
    width: 260px;
	.msg-image{
		width: 100%;
		max-height: 400px;
        object-fit: cover;
	}
}

// 地步菜单
.float-menu-content {
  width: 100%;
  padding: 0 12px;
  display: flex;
  flex-wrap: nowrap;
  white-space: nowrap;
  overflow: hidden;;
  position: relative;
  text-align: left;
}
.float-menu-item {
  font-size: 16px;
  line-height: 16px;
  padding: 10px;
  background: #fff;
  color: #5a5b5c;
  border: 1px solid #d9d9d9;
  border-radius: 15px;
  margin: 0 4px;
}
.active{
  border: 1px solid #1a1aa0;
  color: #1a1aa0;
}
</style>
