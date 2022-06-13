<template>
  <section class="main-section">
    <aside class="left-task-form">
      <div class="app-container clearfix">
        <div v-if="isCheckCurrentDetail">
          <el-radio-group
            size="medium"
            v-model="currentMenuIndex"
            @change="changeCurrentChatbot"
            style="margin-bottom: 20px"
          >
            <el-radio-button :label="index" :value="index" v-for="(item,index) in currentUseMenus" :key="index">
              {{item.carrierName}}
            </el-radio-button>
          </el-radio-group>
          <div v-if="isCheckCurrentDetail && currentUseMenus.length == 0">
            当前没有正在生效的菜单
          </div>
        </div>
        <div class="content" v-if="(isCheckCurrentDetail && currentUseMenus.length > 0) || !isCheckCurrentDetail">
          <div class="left-simulator-wrap">
            <div class="simulator">
              <div class="bottom-menu-wrap clearfix">
                <div class="menu-wrap">
                  <div class="menu-icon-wrap"><i class="el-icon-menu"></i></div>
                  <div class="menu-item-wrap">
                    <div
                      class="menu-item"
                      :class="
                    index == selectMenuIndex && selectSubMenuIndex < 0
                      ? 'active'
                      : ''
                  "
                      v-for="(item, index) in menu.menuList"
                      :key="index"
                    >
                      <div class="menu-item-title" @click="selectMenu(item, index)">
                        <div>{{ item.displayText }}</div>
                      </div>
                      <div
                        class="config-submenu-wrap"
                        v-if="
                      selectMenuIndex == index &&
                      menu.menuList[selectMenuIndex].menuType == 1
                    "
                      >
                        <div class="submenu-wrap">
                          <div
                            v-if="
                          menu.menuList[selectMenuIndex].submenus.length < 5 &&
                          !isCheckDetail
                        "
                            class="menu-item"
                            @click="addSubMenu"
                          >
                            <div class="addMenuBtn">
                              <i class="el-icon-plus"></i>
                            </div>
                          </div>
                          <div
                            class="menu-item"
                            :class="index1 == selectSubMenuIndex ? 'active' : ''"
                            v-for="(subMenu, index1) in menu.menuList[
                          selectMenuIndex
                        ].submenus"
                            :key="index1"
                          >
                            <div
                              class="menu-item-title"
                              @click="selectSubMenu(subMenu, index1)"
                            >
                              {{ subMenu.displayText }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div
                      v-if="menu.menuList.length < 3 && !isCheckDetail"
                      class="menu-item"
                      @click="addMenu"
                    >
                      <div class="addMenuBtn"><i class="el-icon-plus"></i></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="right-info-wrap">
            <el-form
              style="display: flex; flex-direction: column"
              label-position="top"
              :model="menu"
              ref="queryForm"
              :inline="true"
              label-width="100px"
            >
              <el-form-item label="请选择应用" prop="appInfo" required>
                <el-cascader
                  :props="props"
                  :value="menu.appInfo"
                  style="width: 240px"
                  @change="selectApp"
                  :disabled="isEdit || isCheckDetail"
                ></el-cascader>
              </el-form-item>
              <el-form-item label="基础菜单名称" prop="menuName" required>
                <el-input
                  v-model="menu.menuName"
                  placeholder="请输入基础菜单名称"
                  clearable
                  size="small"
                  :disabled="!isJump & (isEdit | isCheckDetail)"
                ></el-input>
              </el-form-item>

              <el-form-item
                v-if="JSON.stringify(tempMenuObj) !== '{}'"
                style="margin-top: 20px"
                label="菜单名称"
                required
              >
                <el-input
                  v-model="tempMenuObj.displayText"
                  placeholder="请输入菜单名称"
                  clearable
                  size="small"
                  :disabled="isCheckDetail"
                ></el-input>
              </el-form-item>
              <el-form-item
                v-if="JSON.stringify(tempMenuObj) !== '{}'"
                label="菜单类型"
                required
              >
                <el-radio-group
                  v-model="tempMenuObj.menuType"
                  :disabled="isCheckDetail"
                >
                  <el-radio v-if="tempMenuObj.isSubMenu == false" :label="1"
                  >菜单
                  </el-radio
                  >
                  <el-radio :label="2">建议回复</el-radio>
                  <el-radio :label="3">打开网页</el-radio>
                  <el-radio :label="4">拨打电话</el-radio>
                  <el-radio :label="5">打开地图</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item v-if="tempMenuObj.menuType == 2" label="选择模板" prop="templateId">
                <el-select v-model="tempMenuObj.templateId" placeholder="请选择发送消息模板" clearable
                           @change="loadTeamplateDetail" style="width: 100%;">
                  <el-option v-for="item in msgTeamplateList" :key="item.id" :label="item.name"
                             :value="item.id"></el-option>
                </el-select>
              </el-form-item>

              <el-form-item
                v-if="tempMenuObj.menuType == 2"
                label="动作值"
              >
                <el-input
                  placeholder="动作值"
                  clearable
                  size="small"
                  v-model="tempMenuObj.postback"
                  :disabled="isCheckDetail"
                ></el-input>
              </el-form-item>
              <el-form-item
                v-if="tempMenuObj.menuType == 3"
                label="网页地址"
                required
              >
                <el-input
                  v-model="tempMenuObj.url"
                  placeholder="请输入打开网页地址"
                  clearable
                  size="small"
                  :disabled="isCheckDetail"
                ></el-input>
              </el-form-item>
              <el-form-item
                v-if="tempMenuObj.menuType == 4"
                label="电话号码"
                required
              >
                <el-input
                  v-model="tempMenuObj.phoneNumber"
                  placeholder="请输入电话号码"
                  clearable
                  size="small"
                  :disabled="isCheckDetail"
                ></el-input>
              </el-form-item>
              <el-form-item label="选择位置" prop="position" v-if="tempMenuObj.menuType == 5">
                <el-input
                  placeholder="经度"
                  v-model="tempMenuObj.longitude"
                  :disabled="true"
                  style="width: 100px"
                >
                </el-input>
                <el-input
                  placeholder="纬度"
                  v-model="tempMenuObj.latitude"
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
            </el-form>
            <div class="right-info-bottm" v-if="!isCheckCurrentDetail">
              <el-button
                v-if="!isCheckDetail"
                type="danger"
                icon="el-icon-delete"
                style="width: 150px; margin-left: 20px"
                @click="deleteMenu"
              >删除该菜单
              </el-button
              >
              <el-button
                v-if="!isCheckDetail"
                type="primary"
                icon="el-icon-menu"
                style="width: 150px; margin-left: 20px"
                @click="saveAction"
                v-loading="saveLoading"
              >保存
              </el-button
              >
              <el-button
                v-if="isCheckDetail"
                type="primary"
                icon="el-icon-edit"
                style="width: 150px; margin-left: 20px"
                @click="handleEdit"
                v-permission:EDIT="$route.meta.permission"
              >编辑
              </el-button
              >
              <el-button
                v-if="
              isCheckDetail &&
              (tempMenu.auditRecords == undefined ||
                tempMenu.auditRecords.length == 0)
            "
                type="primary"
                style="width: 150px; margin-left: 20px"
                @click="handleCommitAudit"
                v-loading="commitLoading"
                v-permission:AUTH="$route.meta.permission"
              >提交
              </el-button
              >
            </div>
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

        <el-dialog
          title="点击确定后，本固定菜单将提交以下Chatbot对应的运营商进行审核！"
          :visible.sync="auditDialogVisible"
          :close-on-click-modal="false"
          width="40%"
          center
        >
          <el-checkbox-group v-model="selectCarrierIDs">
            <el-checkbox
              v-for="audit in carrierList"
              :label="audit.id + ''"
              :key="audit.id + ''"
              :disabled="audit.status == 0 || audit.status == 1"
            >{{ audit.name }}
            </el-checkbox
            >
          </el-checkbox-group>
          <span slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取 消</el-button>
        <el-button
          :disabled="selectCarrierIDs.length == 0"
          v-loading="commitLoading"
          type="primary"
          @click="handleSureCommitAudit"
        >确 定</el-button
        >
      </span>
        </el-dialog>
      </div>
    </aside>

    <aside class="right-card-views" v-if="tempMenuObj.menuType == 2 && tempMenuObj.templateId">
      <MessageTemplatePreview
        v-if="teamplate"
        :appId="teamplate.appId"
        :messageType="teamplate.type"
        :textMsg="teamplate.payload"
        :floatingButtons="teamplate.suggestions != undefined ? teamplate.suggestions.map(item => item.displayText):[]"
        :cards="teamplate.cards"
        :mediaResources="teamplate.materials != undefined && teamplate.materials.length > 0 ?teamplate.materials[teamplate.materials.length - 1]:{}"
        :position="teamplate.payload">
      </MessageTemplatePreview>
    </aside>
  </section>
</template>

<script>
  import {getCustomerList} from "@/api/customer";
  import {getApplicationList} from "@/api/application";
  import {getAppCarrierList} from "@/api/carrier";
  import {save, detail, submitAudit, getAppCurrentMenu} from "@/api/menus";
  import {
    itemsMessageTemplate,
    getMessageTemplate
  } from '@/api/messageTemplate'
  import MessageTemplatePreview from "@/components/MsgTempPreview";
  import Position from "@/components/map/AMap";

  export default {
    components: {
      MessageTemplatePreview,
      Position
    },
    data() {
      return {
        isEdit: false,
        isCheckDetail: false,
        isCheckCurrentDetail: false, //是否为查看当前生效的菜单
        isJump: false, //是否为从应用跳转过来
        msgTeamplateList: [],
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
        selectMenuIndex: -1, //当前选中的一级菜单
        selectSubMenuIndex: -1, //当前选中的二级菜单
        menu: {
          menuList: [],
          menuName: "",
          appInfo: "",
          id: "",
        },
        tempMenuObj: {},
        saveLoading: false,
        checkMenu: false, //查看当前菜单配置的对话框
        appMenuInfo: {}, //app 原始的menu数据结构
        tempMenu: {}, //弹出对话框使用的menu结构.
        commitLoading: false, //提交按钮加载
        carrierList: [],
        selectCarrierIDs: [],
        auditDialogVisible: false,
        currentUseMenus: [],
        currentMenuIndex: 0,
        teamplate: {},
        addAddressOpen: false,
        position: [121.5273285, 31.21515044],
      };
    },
    created() {
      let _customerId = this.$route.query.customerId;
      let appId = this.$route.query.appId;
      let _isCheckDetail = this.$route.query.checkDetail;

      let _isJump = this.$route.query.isJump;
      if (_isJump != undefined) {
        this.isJump = true;
      } else {
        this.isJump = false;
      }

      if (_isCheckDetail != undefined) {
        this.isCheckDetail = true;
      } else {
        this.isCheckDetail = false;
      }
      let _isCheckCurrentDetail = this.$route.query.checkCurrentDetail;
      if (_isCheckCurrentDetail != undefined) {
        this.isCheckCurrentDetail = true;
      } else {
        this.isCheckCurrentDetail = false;
      }
      if (
        _customerId != undefined &&
        _customerId != "" &&
        appId != "" &&
        appId != undefined
      ) {
        this.isEdit = true;
        this.menu.appInfo = [_customerId, appId];

        if (this.isJump) { //从应用跳转过来的
          return;
        }

        if (this.isCheckCurrentDetail) {
          this.fetchAppCurrentMenu();
        } else {
          this.getMenuDetail();
        }
        this.itemsMessageTemplateVV(appId);
      }
    },
    methods: {
      //选择应用
      selectApp(e) {
        console.log("select app:" + JSON.stringify(e));
        this.menu.appInfo = e;
        this.getMenuDetail();
        this.itemsMessageTemplateVV(this.menu.appInfo[1]);
      },
      getMenuDetail() {
        detail({appId: this.menu.appInfo[1]}).then((response) => {
          this.appMenuInfo = response.data;
          var tempMenu = {
            menuList: [],
            menuName: "",
            appInfo: "",
            id: "",
          };
          if (this.appMenuInfo) {
            tempMenu.id = this.appMenuInfo.id;
            tempMenu.menuName = this.appMenuInfo.displayText;
            tempMenu.menuList = this.getMenuList(this.appMenuInfo.menu, 1);
            tempMenu.appInfo = [
              this.appMenuInfo.customerId,
              this.appMenuInfo.appId,
            ];
            if (this.appMenuInfo.auditRecords) {
              tempMenu.auditRecords = this.appMenuInfo.auditRecords;
            }
          }
          this.menu = tempMenu;
        });
      },
      fetchAppCurrentMenu() {
        getAppCurrentMenu({appId: this.menu.appInfo[1]}).then((response) => {
          this.currentUseMenus = response.data;
          this.currentMenuIndex = 0;
          this.selectCurrentAppMenu();
        });
      },
      changeCurrentChatbot(e) {
        console.log(e)
        this.selectCurrentAppMenu();
      },
      selectCurrentAppMenu() {
        if (this.currentUseMenus.length > 0) {
          this.appMenuInfo = this.currentUseMenus[this.currentMenuIndex].menus;
          var tempMenu = {
            menuList: [],
            menuName: "",
            appInfo: this.menu.appInfo,
            id: "",
          };
          if (this.appMenuInfo) {
            tempMenu.id = this.appMenuInfo.id;
            tempMenu.menuName = this.currentUseMenus[this.currentMenuIndex].menuName;
            tempMenu.menuList = this.getMenuList(this.appMenuInfo.menu, 1);
            if (this.appMenuInfo.auditRecords) {
              tempMenu.auditRecords = this.appMenuInfo.auditRecords;
            }
          }
          this.menu = tempMenu;
        }
      },
      getMenuList(menus, level) {
        var menuList = [];
        if (JSON.stringify(menus) == "{}") {
          return menuList;
        }
        menus.entries.forEach((element) => {
          var menu = {};
          if (level == 2) {
            menu.isSubMenu = true;
          } else {
            menu.isSubMenu = false;
          }
          menu.submenus = [];
          if (element.menu != undefined) {
            menu.displayText = element.menu.displayText;
            menu.submenus = this.getMenuList(element.menu, 2);
            menu.menuType = 1;
          }
          if (element.action != undefined) {
            menu.displayText = element.action.displayText;
            if (element.action.urlAction !== undefined) {
              menu.menuType = 3;
              menu.url = element.action.urlAction.openUrl.url;
            }
            if (element.action.dialerAction != undefined) {
              menu.menuType = 4;
              menu.phoneNumber =
                element.action.dialerAction.dialPhoneNumber.phoneNumber;
            }
            if (element.action.mapAction != undefined) {
              menu.menuType = 5;
              menu.latitude =
                element.action.mapAction.showLocation.location.latitude;
              menu.longitude =
                element.action.mapAction.showLocation.location.longitude;
            }
          }
          if (element.reply != undefined) {
            let json = JSON.parse(element.reply.postback.data);
            menu.displayText = element.reply.displayText;
            menu.menuType = 2;

            menu.postback = json.action != undefined ? json.action : "";
            menu.templateId = json.mid != undefined ? json.mid : "";

          }
          menuList.push(menu);
        });
        return menuList;
      },

      //选择一级菜单
      selectMenu(menu, index) {
        this.loadTeamplateDetailByMenu(menu);
        this.selectSubMenuIndex = -1;
        this.selectMenuIndex = index;
        this.tempMenuObj = menu;
      },
      //添加一级菜单
      addMenu() {
        this.tempMenuObj = {
          displayText: "菜单" + (this.menu.menuList.length + 1),
          submenus: [],
          menuType: 1,
          isSubMenu: false,
        };
        this.menu.menuList.push(this.tempMenuObj);
        this.selectMenuIndex = this.menu.menuList.length - 1;
      },
      //添加二级菜单
      addSubMenu() {
        var tempSubMenuObj = {
          displayText:
            "子菜单" +
            (this.menu.menuList[this.selectMenuIndex].submenus.length + 1),
          menuType: 2,
          isSubMenu: true,
        };
        this.menu.menuList[this.selectMenuIndex].submenus.unshift(tempSubMenuObj);
        this.tempMenuObj = tempSubMenuObj;
        this.selectSubMenuIndex = 0;
      },
      //添加二级菜单
      selectSubMenu(menu, index) {
        this.loadTeamplateDetailByMenu(menu);
        this.tempMenuObj = menu;
        this.selectSubMenuIndex = index;
      },

      //保存菜单
      saveAction() {
        if (this.menu.appInfo.length == 0) {
          this.showTipMessage("请选择应用");
          return;
        }
        if (this.menu.menuName.length == 0) {
          this.showTipMessage("请输入菜单名称");
          return;
        }
        if (this.menu.menuList.length == 0) {
          this.showTipMessage("请添加菜单");
          return;
        }
        console.log("提交参数 ---------->" + JSON.stringify(this.menu.appInfo));
        if (this.getMenuEntries(this.menu.menuList, 1) == undefined) {
          return;
        }
        var params = {
          appId: this.menu.appInfo[1],
          customerId: this.menu.appInfo[0],
          displayText: this.menu.menuName,
          id: this.menu.id,
          menu: {
            entries: this.getMenuEntries(this.menu.menuList, 1),
          },
        };
        console.log("提交参数 ---------->" + JSON.stringify(params));
        this.saveLoading = true;
        save(params).then((response) => {
          this.saveLoading = false;
          console.log("save menu--------->" + JSON.stringify(response));
          if (response.code == 200) {
            this.$message({
              message:
                "配置的底部固定菜单已保存成功，请等待提交运营商审核，可在应用管理页面查看审核结果！",
              type: "success",
            });
            this.$router.back(-1);
          }
        });
      },
      getMenuEntries(menuList, level) {
        var levelStr = "一级菜单";
        if (level == 2) {
          levelStr = "二级菜单";
        }
        var entries = [];
        menuList.forEach((item, index) => {
          var menu = {
            action: undefined,
            reply: undefined,
            menu: undefined,
          };
          if (item.displayText.length == 0) {
            this.showTipMessage(
              levelStr + "，第" + (index + 1) + "个菜单，菜单名为空"
            );
            return undefined;
          }
          if (item.menuType == 1) {
            menu.menu = {
              entries: this.getMenuEntries(item.submenus, 2),
              displayText: item.displayText,
            };
            entries.push(menu);
          }
          if (item.menuType == 2) {
            // 构建data的json字符串
            let json = {};
            json.type = "menu";
            json.id = "0";// 暂时赋值为0
            if (item.templateId && item.templateId.length > 0) {
                json.mid = item.templateId
            }

            if (item.postback && item.postback.length > 0) {
                json.action = item.postback;
            }

            menu.reply = {
              displayText: item.displayText,
              postback: {
                data: JSON.stringify(json)
              },
            };
            entries.push(menu);
          }
          if (item.menuType == 3) {
            if (!item.url || item.url.length == 0) {
              this.showTipMessage(
                levelStr + "，第" + (index + 1) + "个菜单，打开网页链接为空"
              );
              return undefined;
            } else {
              menu.action = {
                displayText: item.displayText,
                urlAction: {
                  openUrl: {
                    url: item.url,
                    application: "webview",
                  },
                },
                postback: {
                  data: item.displayText,
                },
              };
              entries.push(menu);
            }
          }
          if (item.menuType == 4) {
            if (!item.phoneNumber || item.phoneNumber.length == 0) {
              this.showTipMessage(
                levelStr + "，第" + (index + 1) + "个菜单，电话号码为空"
              );
            } else {
              menu.action = {
                displayText: item.displayText,
                dialerAction: {
                  dialPhoneNumber: {
                    phoneNumber: item.phoneNumber,
                  },
                },
                postback: {
                  data: item.displayText,
                },
              };
              entries.push(menu);
            }
          }
          if (item.menuType == 5) {
            if (
              !item.latitude ||
              !item.longitude ||
              item.latitude.length == 0 ||
              item.longitude.length == 0
            ) {
              this.showTipMessage(
                levelStr + "，第" + (index + 1) + "个菜单，经、纬度为空"
              );
              return undefined;
            } else {
              menu.action = {
                displayText: item.displayText,
                mapAction: {
                  showLocation: {
                    fallbackUrl:
                      "https://www.google.com/maps/@" +
                      item.latitude +
                      "," +
                      item.longitude +
                      ",15z",
                    location: {
                      label: "Googleplex",
                      latitude: item.latitude,
                      longitude: item.longitude,
                    },
                  },
                },
                postback: {
                  data: item.displayText,
                },
              };

              entries.push(menu);
            }
          }
        });
        return entries;
      },
      showTipMessage(msg) {
        this.$alert(msg, "提示", {
          confirmButtonText: "确定",
        });
      },
      //删除菜单
      deleteMenu() {
        if (this.tempMenuObj.isSubMenu) {
          //删除子菜单
          this.menu.menuList[this.selectMenuIndex].submenus.splice(
            this.selectSubMenuIndex,
            1
          );
          this.selectSubMenuIndex--;
          if (this.selectSubMenuIndex >= 0) {
            this.tempMenuObj = this.menu.menuList[this.selectMenuIndex].submenus[
              this.selectSubMenuIndex
              ];
          } else {
            if (this.menu.menuList[this.selectMenuIndex].submenus.length > 0) {
              this.selectSubMenuIndex = 0;
            } else {
              this.selectSubMenuIndex = -1;
              this.tempMenuObj = this.menu.menuList[this.selectMenuIndex];
            }
          }
        } else {
          //删除一级菜单
          this.$confirm(
            "点击“确定”后“" +
            this.menu.menuList[this.selectMenuIndex].displayText +
            "”菜单及其子菜单下”的所有设置内容将被删除",
            "提示",
            {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning",
            }
          )
            .then(() => {
              this.menu.menuList.splice(this.selectMenuIndex, 1);
              this.selectMenuIndex--;
              if (this.selectMenuIndex >= 0) {
                this.tempMenuObj = this.menu.menuList[this.selectMenuIndex];
              } else {
                if (this.menu.menuList.length > 0) {
                  this.selectMenuIndex = 0;
                  this.tempMenuObj = this.menu.menuList[this.selectMenuIndex];
                } else {
                  this.selectMenuIndex = -1;
                  this.tempMenuObj = {};
                }
              }
            })
            .catch(() => {
            });
        }
      },
      handleEdit() {
        this.isCheckDetail = false;
        this.isEdit = true;
      },
      handleCommitAudit() {
        this.commitLoading = true;
        this.selectCarrierIDs = [];
        getAppCarrierList(this.menu.appInfo[0]).then((response) => {
          console.log("getAppCarrierList--------->" + JSON.stringify(response));
          this.commitLoading = false;
          this.carrierList = response.data;
          if (this.carrierList.length == 0) {
            this.$confirm(
              "对不起，当前本应用下暂未开通任一运营商的Chatbot，固定菜单无法提交审核！请先去开通Chatbot吧！",
              "提示",
              {
                confirmButtonText: "确定",
                type: "warning",
                showCancelButton: false,
              }
            );
            return;
          }
          this.carrierList = response.data;
          this.auditDialogVisible = true;
        });
      },
      handleSureCommitAudit() {
        this.commitLoading = true;
        submitAudit({
          appId: this.menu.appInfo[1],
          carrierIds: this.selectCarrierIDs,
        })
          .then((response) => {
            if (response.code == 200) {
              this.$message({
                message:
                  "配置的底部固定菜单已提交运营商审核，可在应用管理页面查看审核结果！",
                type: "success",
              });
              this.$router.back(-1);
            }
            this.commitLoading = false;
            this.auditDialogVisible = false;
          })
          .catch((err) => {
            this.commitLoading = false;
            this.auditDialogVisible = false;
          });
      },
      handleCheckMenu() {
        this.tempMenu = {
          menuList: [],
          menuName: "",
          appInfo: "",
          id: "",
          auditRecords: [],
        };
        if (this.appMenuInfo) {
          this.tempMenu.id = this.appMenuInfo.id;
          this.tempMenu.menuName = this.appMenuInfo.displayText;
          this.tempMenu.menuList = this.getMenuList(this.appMenuInfo.menu, 1);
          this.tempMenu.appInfo = [
            this.appMenuInfo.customerId,
            this.appMenuInfo.appId,
          ];
          if (this.appMenuInfo.auditRecords) {
            this.tempMenu.auditRecords = JSON.parse(
              this.appMenuInfo.auditRecords
            );
          }
        }
        this.checkMenu = true;
      },
      getAuditStatusText(audit) {
        if (audit.status == 0) {
          return "待审核";
        }
        if (audit.status == 1) {
          return "已审核";
        }
        if (audit.status == 2) {
          return "未通过";
        }
        return "未提交";
      },
      getAuditStatusTextClass(audit) {
        if (audit.status == 0) {
          return "waitting";
        }
        if (audit.status == 1) {
          return "audit";
        }
        if (audit.status == 2) {
          return "reject";
        }
        return "";
      },
      itemsMessageTemplateVV(appId) {
        itemsMessageTemplate({
          appId
        }).then((response) => {
          this.msgTeamplateList = response.data;
        });
      },
      loadTeamplateDetail(teamplateId) {
        if (teamplateId) {
          getMessageTemplate(teamplateId).then((response) => {
            this.teamplate = response.data;
          });
        }
      },
      loadTeamplateDetailByMenu(menu) {
        if (menu.menuType == 2) {
          if (menu.templateId) {
            this.loadTeamplateDetail(menu.templateId);
          } else {
            this.teamplate = {}
          }
        } else {
          this.teamplate = {}
        }
      },
      handAddSugPosition() {
       // this.positionType = this.positionTypeEnum.suggestion;
        this.addAddressOpen = true;
      },
      loadPosition(position) {
          this.tempMenuObj.longitude = position.lng;
          this.tempMenuObj.latitude = position.lat;
      },
      okAddress() {
        this.addAddressOpen = false;
      },
      cancelAddress() {
        this.addAddressOpen = false;
      },
    },
  };
</script>
<style lang="scss" scoped>
  .app-container {
    margin: 0 auto;
    display: flex;
    flex-direction: column;
  }

  .content {
    margin: 0 auto;
    display: flex;
    flex-direction: row;
  }

  // .clearfix{*zoom:1;}
  // .clearfix::after{content: "";display: table; clear: both;}
  .left-simulator-wrap {
    .simulator {
      width: 350px;
      height: 715px;
      background: url("../../assets/images/iphone_backImg.png") no-repeat;
      background-size: 100% auto;
      position: relative;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;

      .bottom-menu-wrap {
        position: absolute;
        bottom: 88px;
        left: 25px;
        width: 300px;

        .menu-wrap {
          display: flex;
          width: 100%;
          height: 40px;

          .menu-icon-wrap {
            height: 40px;
            width: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
          }

          .menu-item-wrap {
            display: flex;
            align-items: center;
            flex: 1;
            flex-direction: row;
            justify-content: center;

            .menu-item {
              flex: 1;
              position: relative;

              .menu-item-title {
                display: flex;
                color: #25292e;
                justify-content: center;
                position: relative;
                align-items: center;
                height: 40px;

                ::before {
                  content: "";
                  position: absolute;
                  left: 0;
                  width: 1px;
                  top: 20px;
                  height: 20px;
                  -webkit-transform: translateY(-50%) scaleX(0.5);
                  transform: translateY(-50%) scaleX(0.5);
                  background: #dddee0;
                }
              }

              .config-submenu-wrap {
                position: absolute;
                bottom: 42px;
                width: 100%;
                display: flex;
                justify-content: center;

                .submenu-wrap {
                  width: 100%;
                  max-width: 100px;
                  background: #fff;
                  box-shadow: 0 0.05rem 0.14rem rgb(0 0 0 / 14%);
                }
              }
            }

            .active {
              border: 1px solid #2bb673;
            }

            .addMenuBtn {
              display: flex;
              color: #25292e;
              justify-content: center;
              position: relative;
              align-items: center;
              height: 40px;
              flex: 1;
            }
          }
        }
      }
    }
  }

  .right-info-wrap {
    position: relative;
    float: left;
    width: 600px;
    padding: 10px;
    height: 710px;
    margin-left: 20px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;

    .right-info-bottm {
      display: flex;
      flex-flow: row-reverse;
    }
  }

  .audit-status-wrap {
    display: flex;
    padding: 20px;
    flex-direction: row;

    .audit-status {
      color: #333;
      font: 16px;

      .waitting {
        color: #ff6600;
      }

      .audit {
        color: #008000;
      }

      .reject {
        color: #ff0000;
      }
    }
  }

  .main-section {
    display: flex;
    padding: 30px 15px 10px;

    .left-task-form {
      order: -1;
      width: 500px;
    }

    .right-card-views {
      order: 1;
      margin-left: 450px;
    }
  }
</style>
