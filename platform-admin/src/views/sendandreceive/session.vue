<template>
  <div class="container">
    <div class="appinfo-box">
      <span class="name">客户名称：{{ appInfo.customerName }}</span>
      <span style="margin:0 10px" class="name">应用名称：{{ appInfo.name }}</span>
    </div>
    <div class="content">
      <div class="roll-cont" id="followData">
        <div v-for="(msg,index) in chatList" :key="index" class="m-item">
          <div class="header" style="padding:0 16px" :class="isUpstreamMsg(msg) ? 'sendmsg-header':'receivemsg-header'">
            <span class="name">{{ isUpstreamMsg(msg) ? msg.phone_num : appInfo.name }}</span>
            <span style="margin:0 10px" class="date">{{ msg.log_dt }}</span>
          </div>
          <!-- 文本消息 -->
          <div v-if="isTextMsg(msg)"
               class="msg-warp flex-row align-items-center"
               :class="isUpstreamMsg(msg) ? 'sendmsg-warp':'receivemsg-warp'"
          >
            <div class="flex-row justify-content-start align-items-start flex-wrapper"
                 :class="isUpstreamMsg(msg) ? '':'receive-txt-warp'"
            >
              <div class="text-msg" :class="isUpstreamMsg(msg) ? 'send-txt':'receive-txt'">
                {{ getMsgText(msg) }}
              </div>
            </div>
          </div>

          <!-- 下行卡片消息 -->
          <div v-if="!isUpstreamMsg(msg) && (getMsgContent(msg).type == 2 || getMsgContent(msg).type == 3)"
               class="msg-warp receivemsg-warp"
          >
            <div class="d-flex flex-row justify-content-start align-items-start flex-wrapper">
              <div class="gridlist-inline-demo clear-mar" :class="getMsgContent(msg).type == 3 ? 'multiCard':''">
                <div class="multi-card-item" v-for="(msgContent,index) in getMsgContent(msg).content"
                     :key="index" :class="index == 0?'multi-card-item-first':''"
                >
                  <div class="flex-row justify-content-start align-items-start flex-wrapper">
                    <div class="flex-column justify-content-center align-items-start card-wrap">
                      <div class="card-head">
                        <div class="media-wrap" v-if="msgContent.file">
                          <video class="single-card-head" v-if="getMediaType(msgContent.file.contentType) == 'video'"
                                 :src="msgContent.file.showUrl" :poster="msgContent.thumb ? msgContent.thumb.showUrl:''"
                                 controls autoplay
                          ></video>
                          <audio class="single-card-head" v-if="getMediaType(msgContent.file.contentType) == 'audio'"
                                 :src="msgContent.file.showUrl" controls
                          ></audio>
                          <img class="single-card-head" v-if="getMediaType(msgContent.file.contentType) == 'image'"
                               :src="msgContent.thumb ? msgContent.thumb.showUrl:msgContent.file.showUrl"
                               @click="predivImage(msgContent.file.showUrl)" mode="scaleToFill"
                          />
                        </div>
                      </div>
                      <div class="card-cont-wrap">
                        <div>
                          <div class="card-txt card-title">{{ msgContent.title }}</div>
                          <div class="card-txt card-content">{{ msgContent.description }}</div>
                        </div>
                        <div style="width: 100%;">
                          <div class="card-btn align-items-center justify-content-center"
                               v-for="(buttonItem,index) in msgContent.suggestions" :key="index"
                               @click="onMenuClick(buttonItem)"
                          >
                            {{ buttonItem.reply ? buttonItem.reply.displayText : buttonItem.action.displayText }}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 下行媒体消息 -->
          <div
            v-if="!isUpstreamMsg(msg) && (getMsgContent(msg).type == 5 || getMsgContent(msg).type == 6 || getMsgContent(msg).type == 7)"
            class="msg-warp receivemsg-warp"
          >
            <div class="flex-row justify-content-start align-items-start flex-wrapper pl">
              <div class="justify-content-center align-items-start msg-image-wrap"
                   v-for="(msgContent,index) in getMsgContent(msg).content" :key="index"
              >
                <video v-if="getMediaType(msgContent.file.contentType) == 'video'" :src="msgContent.file.showUrl"
                       :poster="msgContent.thumb ? msgContent.thumb.showUrl:''" class="msg-image" controls autoplay
                ></video>
                <img v-if="getMediaType(msgContent.file.contentType) == 'image'" class="msg-image"
                     :src="msgContent.thumb ? msgContent.thumb.showUrl:msgContent.file.showUrl"
                     @click="predivImage(msgContent.file.showUrl)" mode="aspectFit"
                />
                <audio class="single-card-head" v-if="getMediaType(msgContent.file.contentType) == 'audio'"
                       :src="msgContent.file.showUrl" controls
                ></audio>
              </div>
            </div>
          </div>
          <!-- 上行行媒体消息 -->
          <div v-if="isUpstreamMsg(msg) && !isTextMsg(msg)"
               class="msg-warp sendmsg-warp"
          >
            <div class="flex-row justify-content-start align-items-start flex-wrapper pl">
              <div class="justify-content-center align-items-start msg-image-wrap"
                   v-for="(file,index) in getMsgContent(msg).maapFile" :key="index"
              >
                <video v-if="getMediaType(file.contentType) == 'video'" class="msg-image" :src="file.murl"
                       controls autoplay
                ></video>
                <img v-if="getMediaType(file.contentType) == 'image'" class="msg-image"
                     :src="file.murl"
                />
                <audio class="single-card-head" v-if="getMediaType(file.contentType) == 'audio'"
                       :src="file.murl" controls
                ></audio>
              </div>
            </div>
          </div>
          <div class="footer" style="padding:0 16px" :class="isUpstreamMsg(msg) ? 'sendmsg-footer':'receivemsg-footer'">
            <span class="status">{{ selectDictLabel(statusTypeList, msg.status) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {
  getApplication
} from '@/api/application'
import {
  dictMessageStatusType
} from '@/assets/initdata'
import { chatLog } from '@/api/statistical'

export default {
  data() {
    return {
      chatList: [],
      noMoreData: false,
      appInfo: {},
      params: {
        'currentPage': 1,
        'pageSize': 10,
        'params': {
          'phone_num': '',
          'app_id': ''
        }
      },
      statusTypeList: dictMessageStatusType()
    }
  },
  created() {
    this.params.params.appId = this.$route.query.app_id
    this.params.params.phoneNum = this.$route.query.phone_num
    this.getAppInfo()
    this.getSessionList()
  },
  mounted() {
    this.bindEventListener()
  },
  methods: {
    // mounted 中需要加载的方法，给父元素绑定 scroll 事件
    bindEventListener() {
      // 用 $nextTick ，确保页面 dom 元素加载完毕
      this.$nextTick(() => {
        const el = document.getElementById('followData')
        if (!el) return
        // 用 lodash 做节流处理，防止滚动到底部时多次触发加载数据方法，scrollHandler 方法在下方给出
        el.addEventListener('scroll', _.throttle(this.scrollHandler, 200))
      })
    },
    // 滚动分页加载
    scrollHandler() {
      const divHeight = document.getElementById('followData').offsetHeight
      const nScrollHeight = document.getElementById('followData').scrollHeight
      const nScrollTop = document.getElementById('followData').scrollTop
      if (nScrollTop + divHeight + 1 >= nScrollHeight) {
        this.params.currentPage = parseInt(this.params.currentPage + 1)
        if (this.noMoreData) {
          return false
        }
        this.getSessionList() // 请求数据，getData 方法在下方给出
      }
    },
    getAppInfo() {
      getApplication(this.params.params.appId).then(response => {
        this.appInfo = response.data
      })
    },
    getSessionList() {
      this.noMoreData = false
      chatLog(this.params).then(response => {
        if (response.data.items.length < this.params.pageSize) {
          this.noMoreData = true
        }
        if (this.params.currentPage == 1) {
          this.chatList = response.data.items
        } else {
          this.chatList.push(...response.data.items)
        }
      })
    },
    getMsgContent(msg) {
      if (msg.content) {
        return JSON.parse(msg.content)
      }
      return {}
    },
    isUpstreamMsg: function(msg) {
      if (msg.send_type == '1') {
        return true
      }
      return false
    },
    isTextMsg(msg) {
      if (this.isUpstreamMsg(msg)) {
        if (this.getMsgContent(msg).maapFile == null) {
          return true
        }
      } else {
        if (this.getMsgContent(msg).type == 1) {
          return true
        }
      }
      return false
    },
    getMsgText(msg) {
      if (this.isUpstreamMsg(msg)) {
        let contentType = this.getMsgContent(msg).contentType
        if (contentType === 'application/vnd.gsma.rcs-ft-http+xml' || contentType === 'text/plain;charset=UTF-8') {
          return this.getMsgContent(msg).bodyText
        } else {
          let bodyText = JSON.parse(this.getMsgContent(msg).bodyText)
          return bodyText.response.reply.displayText
        }
      } else {
        return this.getMsgContent(msg).content
      }
    },
    getMediaType(contentType) {
      console.log(contentType)
      if (contentType == 'image/jpg' || contentType == 'image/jpeg' || contentType == 'image/png') {
        return 'image'
      }
      if (contentType == 'video/mp4') {
        return 'video'
      }
      if (contentType == 'audio/mp3' || contentType == 'audio/mpeg' || contentType == 'audio/amr') {
        return 'audio'
      }
      return ''
    }
  }
}
</script>
<style lang="less" scoped>
.appinfo-box {
  padding: 16px;
  font-size: 16px;
}

//消息列表
.roll-cont {
  position: absolute;
  left: 80px;
  top: 100px;
  width: 400px;
  height: 728px;
  margin: 10px;
  overflow-x: hidden;
  box-shadow: 2px 4px 10px 2px rgba(0, 0, 0, 0.64);
  backdrop-filter: blur(10px);
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

.m-item {
  margin: 24px 0;
}

.header {
  align-items: center;
  display: flex;

  .name {
    font-size: 16px;
    font-weight: 500;
    line-height: 14px;
  }

  .date {
    font-size: 14px;
    font-weight: 400;
    line-height: 14px;
  }
}

.sendmsg-header {
  flex-direction: row-reverse;
  justify-content: right;
}

.sendmsg-footer {
  text-align: end;
}

.status {
  font-size: 14px;
  font-weight: 400;
  line-height: 14px;
}


.msg-warp {
  margin: 8px 0;
}

.pl {
  padding-left: 16px;
}

.text-msg {
  font-size: 16px;
  line-height: 24px;
  word-break: break-all;
  color: #25292e;
  text-align: left;
  padding: 8px 12px;
}

//发送的文本消息
.sendmsg-warp {
  padding: 0 16px 0 62px;
  justify-content: flex-end;

  .send-txt {
    background: #d6e9ff;
    border-radius: 14px 1px 14px 14px;
  }
}

//接收的文本消息
.receivemsg-warp {
  .receive-txt-warp {
    padding-left: 16px;
    padding-right: 62px;
  }

  .receive-txt {
    background: #f0f0f0;
    border-radius: 1px 14px 14px 14px;
  }
}

//单卡片消息
.card-txt {
  background-color: #f3f3f3;
  width: 100%;
  font-size: 16px;
  color: #25292e;
  line-height: 20px;
  text-align: left;
  margin: 4px 0 4px;
  word-break: break-all;
  white-space: pre-line;
}

.card-btn {
  display: flex;
  font-size: 14px;
  width: 100%;
  color: #25292e;
  margin-top: 4px;
  margin-bottom: 8px;
  padding: 0;
  background: #fff;
  line-height: 40px;
  height: 40px;
  border-radius: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-decoration: none;
}

.card-wrap {
  border-radius: 6px;
  background-color: #f3f3f3;
  overflow: hidden;
  box-sizing: border-box;
  height: 100%;

  .card-head {
    width: 100%;


  }


}

.card-cont-wrap {
  width: 300px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 4px 12px 4px 12px;
  height: 100%;

  .card-title {
    line-height: 24px;
    font-weight: 600;
    font-size: 16px;
    color: #25292e;
    -webkit-line-clamp: 2;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .card-content {
    overflow: hidden;
    font-size: 14px;
    text-overflow: ellipsis;
    -webkit-line-clamp: 4;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    white-space: pre-line;
  }

}

.media-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 300px;
  height: 162px;

  .single-card-head {
    object-fit: fill;
    width: 100%;
    height: 100%;
    overflow: hidden;
    position: relative;
  }
}

//多卡片
.gridlist-inline-demo {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  padding: 0;
}

.mu-grid-list {
  display: -webkit-box;
  display: -webkit-flex;
  display: -ms-flexbox;
  display: flex;
  -webkit-flex-wrap: wrap;
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
}

.multi-card-item-first {
  padding-left: 18px;
}

.multiCard {
  .multi-card-item {
    padding: 4px;
    width: 100%;
    margin-right: auto;
    margin-left: auto;
    height: auto;
  }

  .multi-card-item-first {
    padding-left: 18px;
  }

  .media-wrap {
    width: 90px;
    height: 84px;
  }

  .card-cont-wrap {
    width: 90px;
  }
}

//图片
.msg-image-wrap {
  border-radius: 6px;
  background-color: #f3f3f3;
  overflow: hidden;
  box-sizing: border-box;
  height: 100%;

  .msg-image {
    max-width: 270px;
    max-height: 400px;
  }
}
</style>
