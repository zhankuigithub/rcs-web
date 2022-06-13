<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.params.customerId" placeholder="请选择客户" :disabled="isQueryById" clearable
                   size="small">
          <el-option
            v-for="item in customerList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="应用名称" prop="name">
        <el-input
          v-model="queryParams.params.name"
          placeholder="请输入应用名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
        >新增
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="applicationList" @selection-change="handleSelectionChange">
      <el-table-column label="应用名称" align="center" prop="name"/>
      <el-table-column label="应用行业" align="center">
        <template slot-scope="scope">
          {{scope.row.categoryIds|businessTypesName}}
        </template>
      </el-table-column>
      <el-table-column label="LOGO" align="center" prop="logoUrl">
        <template slot-scope="scope">
          <el-image style="width: 42px; height: 42px" :src="scope.row.logoUrl"
                    :preview-src-list="[scope.row.logoUrl]"></el-image>
        </template>
      </el-table-column>
      <el-table-column label="归属客户" align="center" prop="customerName"/>
      <el-table-column label="应用审核" align="center">
        <template slot-scope="scope">
          <el-tooltip
            v-if="scope.row.status+'' != '0'&&scope.row.remark!=''"
            class="item"
            effect="dark"
            :content="scope.row.remark"
            placement="top-end"
          >
            <el-button
              size="mini"
              type="text"
              @click="handleAutitUpdate(scope.row)"
            >
              {{appStatusFormat(scope.row,null)}}
            </el-button>
          </el-tooltip>
          <el-button
            size="mini"
            type="text"
            @click="handleAutitUpdate(scope.row)"
            v-else
          >
            {{appStatusFormat(scope.row,null)}}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="机器人" align="center">
        <template slot-scope="scope">
          <div v-if="scope.row.chatbotItems.length==0">
            <el-button
              size="mini"
              type="text"
              @click="gotoChatbotPage(scope.row)"
            >未开通
            </el-button>
          </div>
          <div v-for="(item,key) in scope.row.chatbotItems">
            <el-button
              size="mini"
              type="text"
              @click="gotoChatbotPage(scope.row)"
            >{{item.carrierName}}-{{chatbotAuditStatusFormat(item)}}
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="固定菜单" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            @click="gotoMenuPage(scope.row)"
          >
            {{scope.row.menuAuditTag ? '已添加':'未添加'}}
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="添加素材" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="gotoMaterialPage(scope.row)"
          >添加素材
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            :disabled="scope.row.status +'' == '0'"
            v-permission:EDIT="$route.meta.permission"
          >修改
          </el-button>
          <!-- <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.currentPage"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改应用信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" width="820px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户"
                     :disabled="isQueryById||operType==operTypeEnum.autit||operType==operTypeEnum.edit" clearable
                     size="small">
            <el-option
              v-for="item in customerList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="应用名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入应用名称" :disabled="operType==operTypeEnum.autit"/>
        </el-form-item>
        <el-form-item label="应用介绍" prop="details">
          <el-input v-model="form.details" type="textarea" placeholder="请输入应用介绍"
                    :disabled="operType==operTypeEnum.autit"/>
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="应用logo" prop="logoUrl">
              <!-- <el-input v-model="form.logoUrl" placeholder="请输入应用logo" /> -->
              <div v-if="operType!=operTypeEnum.autit">
                <file-upload
                  ref="fileUpload"
                  :config="uploadLg"
                  @on-resp="uploadLogo"
                  @on-change="handUploadLogo"
                  @on-remove="()=>{form.logoUrl=null;$refs.form.validateField('logoUrl');$forceUpdate();}"
                />
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <div v-if="form.logoUrl!=null">
              <el-image style="width: 100px; height: 100px" :src="form.logoUrl"></el-image>
            </div>
          </el-col>
        </el-row>

        <el-form-item label="行业类型" prop="categoryIds">
          <el-select v-model="form.categoryIds" placeholder="行业类型" multiple style="width:100%"
                     @change="categoryIdsChange" :disabled="operType==operTypeEnum.autit" :multiple-limit="3">
            <el-option v-for="item in businessTypes" :key="item.type" :value="item.type" :label="item.label"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="operType!=operTypeEnum.autit">确 定</el-button>
        <el-button type="primary" @click="showAutitForm" v-if="operType==operTypeEnum.autit&&form.status+''=='0'">审核
        </el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <el-dialog
      title="审核应用"
      :visible.sync="aOpen"
      :close-on-click-modal="false"
      width="820px"
      append-to-body
    >
      <el-form
        ref="autitForm"
        :model="autitForm"
        :rules="autitRules"
        label-width="120px"
      >
        <el-form-item label="审核状态" prop="status">
          <el-select
            v-model="autitForm.status"
            placeholder="审核状态"
            clearable
            size="small"
            @change="selectAuditStatus"
          >
            <el-option
              key="1"
              label="已审核通过"
              value="1"
            />
            <el-option
              key="2"
              label="审核未通过"
              value="2"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审核内容" prop="remark">
          <el-input
            v-model="autitForm.remark"
            :placeholder="auditPlaceholder"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="aSubmitForm">确定</el-button>
        <el-button @click="aCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listApplication,
    getApplication,
    delApplication,
    addApplication,
    updateApplication,
    exportApplication,
    auditApplication
  } from "@/api/application";
  import {customerGrades, getCustomerList} from "@/api/customer";
  import {dictAppStatus, industryType, dictChatbotAuditStatus} from '@/assets/initdata';
  import FileUpload from "@/components/file/upload";
  import {getToken} from "@/utils/auth";

  export default {
    name: "Application",
    components: {
      FileUpload
    },
    data() {
      var validateFileLogoUrl = (rule, value, callback) => {
        if (this.form.logoUrl == null) {
          callback(new Error('请上传应用LOGO'));
        } else {
          callback();
        }
      };
      return {
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 应用信息表格数据
        applicationList: [],
        // 弹出层标题
        title: "",
        uploadLg: {
          action: process.env.VUE_APP_PLATFORM_BASE_API + "/manage/customer/uploadFile",
          name: "file",
          multiple: true,
          style: "button",
          files: [],
          accept: "image/png, image/jpeg",
          headers: {
            "ACCESS-TOKEN": getToken(),
          }
        },
        // 是否显示弹出层
        open: false,
        aOpen: false,
        customerList: [],
        isQueryById: false,
        businessTypes: industryType(),
        appStatus: dictAppStatus(),
        autitStatus: dictAppStatus(),
        operType: 1,//1 添加 2修改 3审核
        operTypeEnum: {
          add: 1,
          edit: 2,
          autit: 3
        },
        auditPlaceholder: "请输入审核内容",
        // 查询参数
        queryParams: {
          currentPage: 1,
          pageSize: 10,
          params: {
            name: null,
            customerId: null
          }
        },
        // 表单参数
        form: {},
        autitForm: {},
        // 表单校验
        rules: {
          customerId: [
            {required: true, message: "客户id不能为空", trigger: "blur"}
          ],
          name: [
            {required: true, message: "应用名称不能为空", trigger: "blur"}
          ],
          details: [
            {required: true, message: "应用介绍不能为空", trigger: "blur"}
          ],
          logoUrl: [
            {required: true, message: "应用LOGO不能为空", trigger: "blur"},
            {validator: validateFileLogoUrl, trigger: 'blur'}
          ],
          categoryIds: [
            {required: true, message: "行业类型不能为空", trigger: "blur"}
          ]

        },
        autitRules: {
          status: [
            {required: true, message: "请选择审核状态", trigger: "blur"}
          ],
          remark: [
            {required: true, message: "请输入审核内容", trigger: "blur"}
          ]
        },
        chatbotAuditStatus: dictChatbotAuditStatus(), //机器人审核状态
      };
    },
    created() {
      let _customerId = this.$route.query.id;
      let _appName = this.$route.query.appName;

      if (_customerId != null && _customerId != "") {
        this.isQueryById = true;
        this.queryParams.params.customerId = _customerId;
        this.form.customerId = _customerId;
      }

      if (_appName != null) {
        this.queryParams.params.name = _appName;
      }

      this.getCustomerListVV();
      this.getList();
    },
    methods: {
      /** 查询应用信息列表 */
      getList() {
        this.loading = true;
        listApplication(this.queryParams).then(response => {
          /*
          this.applicationList = response.rows;
          this.total = response.total;
          this.loading = false;*/
          this.applicationList = response.data.items;
          this.total = response.data.total;

          this.loading = false;
        });
      },
      getCustomerListVV() {
        getCustomerList({isAll: true}).then(response => {
          this.customerList = response.data;
        })
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          id: null,
          customerId: null,
          name: null,
          details: null,
          logoUrl: null,
          autograph: null,
          categoryIds: null,
          providerName: null,
          isShowProvider: null,
          tosUrl: null,
          email: null,
          websiteUrl: null,
          phoneNum: null,
          address: null,
          longitude: null,
          latitude: null,
          whiteIps: null,
          remark: null,
          status: 0,
          logicDel: null,
          insertDt: null,
          updateDt: null
        };
        this.uploadLg.files = [];
        this.resetForm("form");
      },
      aReset() {
        this.autitForm = {
          status: null,
          remark: null
        }
      },
      showAutitForm() {
        this.aOpen = true;
      },
      aSubmitForm() {
        this.$refs["autitForm"].validate(valid => {
          if (valid) {
            let _data = {...this.form};
            //_data.categoryIds=_data.categoryIds.join(',');
            /*
            let _data={
              id:this.form.id,
              status:this.autitForm.status,
              remark:this.autitForm.remark
            }*/
            _data.categoryIds = _data.categoryIds.join(',');
            _data.status = this.autitForm.status;
            _data.remark = this.autitForm.remark;

            auditApplication(_data).then(response => {
              this.aOpen = false;
              this.open = false;
              this.getList();
            });
          }
        });
      },
      aCancel() {
        this.aOpen = false;
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.currentPage = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.queryParams = {
          params: {
            customerId: this.isQueryById ? this.queryParams.params.customerId : null,
            name: null
          }
        }
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.operType = this.operTypeEnum.add;

        this.reset();

        let _customerId = this.$route.query.id;

        if (_customerId != null && _customerId != "") {
          this.isQueryById = true;
          this.form.customerId = _customerId;
        }

        this.open = true;
        this.title = "添加应用信息";
      },
      chatbotAuditStatusFormat(row, column) {
        return this.selectDictLabel(this.chatbotAuditStatus, row.auditStatus);
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.operType = this.operTypeEnum.edit;
        this.reset();
        const id = row.id || this.ids
        getApplication(id).then(response => {
          //this.form = response.data;
          let _data = response.data;
          _data.categoryIds = _data.categoryIds.split(',');

          _data.categoryIds.forEach(function (item, i) {
            _data.categoryIds[i] = parseInt(item);
          });

          if (_data.logoUrl != null && _data.logoUrl != "") {
            this.uploadLg.files = [
              {name: "logo-01.jpg", url: _data.logoUrl}
            ];
          }

          this.form = {..._data};

          this.open = true;
          this.title = "修改应用信息";
        });
      },
      handleAutitUpdate(row) {
        this.operType = this.operTypeEnum.autit;
        this.reset();
        this.aReset();
        const id = row.id || this.ids;
        getApplication(id).then(response => {
          //this.form = response.data;
          let _data = response.data;
          _data.categoryIds = _data.categoryIds.split(',');

          _data.categoryIds.forEach(function (item, i) {
            _data.categoryIds[i] = parseInt(item);
          });


          this.form = {..._data};

          this.open = true;
          this.title = "审核应用信息";
        });
      },
      selectAuditStatus() {
        this.autitRules.remark[0].required = this.autitForm.status + '' == '2';

        if (this.autitForm.status + '' == '1') {
          this.autitForm.remark = "已审核通过";
        } else if (this.autitForm.status + '' == '2') {
          this.autitForm.remark = "";
          this.auditPlaceholder = "请输入不通过原因";
        } else {
          this.auditPlaceholder = "请输入审核内容";
        }
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              let _data = {...this.form};
              _data.categoryIds = _data.categoryIds.join(',');
              _data.status = 0;
              updateApplication(_data).then(response => {
                this.open = false;
                this.getList();
              });
            } else {
              let _data = {...this.form};
              _data.categoryIds = _data.categoryIds.join(',');
              addApplication(_data).then(response => {
                this.open = false;
                this.getList();
              });
            }
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$confirm('是否确认删除应用信息编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return delApplication(ids);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
      },
      appStatusFormat(row, column) {
        return this.selectDictLabel(this.appStatus, row.status);
      },
      gotoChatbotPage(row) {
        //console.log(row); chatbot/index
        this.$router.push({path: '/chatbot/index', query: {customerId: row.customerId, appId: row.id}});
      },
      gotoMenuPage(row) {
        //console.log(row);
        this.$router.push({path: '/menu/index', query: {customerId: row.customerId, appId: row.id, isJump: true}});
      },
      gotoMaterialPage(row) {
        //console.log(row);
        this.$router.push({path: '/resources/index', query: {customerId: row.customerId, appId: row.id}});
      },
      uploadLogo(resp) {
        if (resp.code !== 200) {
          this.$message({
            type: "error",
            message: "上传失败，请重新上传~",
          });
        } else {
          this.form.logoUrl = resp.data;
          this.$refs.form.validateField('logoUrl');
          this.$forceUpdate();
        }
      },
      handUploadLogo(file, fileList) {
        if (file.status == 'success') {
          this.form.logoUrl = file.response.data.url;
          if (fileList.length > 1) {
            fileList.splice(0, 1);
          }
          this.$refs.form.validateField('logoUrl');
        }
      },
      categoryIdsChange() {
        this.$refs.form.validateField('categoryIds');
      },
      getLogoUrl() {
        if (this.form.logoUrl == null) {
          return "";
        } else {
          return require(this.form.logoUrl);
        }
      }
    },
    filters: {
      businessTypesName(value) {
        let _btlist = industryType();
        /*
        let _name="";
        _btlist.forEach(function(v,i){
          if(v.type+""==value+""){
            _name=v.label;
          }
        });*/

        let ids = value.split(",").map(Number);
        let _arry = _btlist.filter(row => value.indexOf(row.type) >= 0);

        let _name = "";
        _arry.forEach((item, i) => {
          _name += i > 0 ? "/" + item.label : item.label;
        })

        return _name;
      }
    }
  };
</script>
