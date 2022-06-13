<template>
  <el-dialog title="新增网页" :visible.sync="visible" :close-on-click-modal="false" width="650px"
             @close="closeDialog" append-to-body>
    <el-form ref="uploadForm" label-position="left" label-width="120px" :model="uploadForm" :rules="uploadRules">
      <el-form-item
        label="应用名称"
        required
        prop="cascaderValue"
      >
        <el-cascader
          :props="cascaderProps"
          style="width: 240px"
          @change="selectApp"
          size="mini"
          v-model="uploadForm.cascaderValue"
          :disabled="!canSelectApp"
        ></el-cascader>
      </el-form-item>
      <el-form-item label="网页名称" required prop="fileName">
        <el-input
          v-model="uploadForm.fileName"
          placeholder="请输入网页名称"
          clearable
          size="small"
        ></el-input>
      </el-form-item>
      <el-form-item label="网页地址" required prop="originalFileUrl">
        <el-input v-model="uploadForm.originalFileUrl" placeholder="请输入网页地址"
                  size="small"></el-input>
      </el-form-item>
      
    </el-form>
    <div class="dialog-footer">
      <el-button size="mini" @click="visible = false">取 消</el-button>
      <el-button
        v-loading="loading"
        size="mini"
        type="primary"
        @click="handleCommit"
      >确 定
      </el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import {getCustomerList} from "@/api/customer";
import {getApplicationList} from "@/api/application";
import {addResources} from "@/api/resources";
import {idCard, idCardValidity, phoneNumber, emailValue, urlString} from "@/utils/validate";

  export default {
    name: "AddWebpage",
    data() {
      const validateSelectApp = (rule, value, callback) => {
        if (this.uploadForm.customerId == undefined ||
          this.uploadForm.customerId.length == 0 ||
          this.uploadForm.appId == undefined ||
          this.uploadForm.appId.length == 0) {
          callback(new Error('请先选择应用'))
        } else {
          callback()
        }
      }
      const validateFileName = (rule, value, callback) => {
        if (this.uploadForm.fileName == undefined ||
          this.uploadForm.fileName.length == 0) {
          callback(new Error('请输入网页名称'))
        } else {
          callback()
        }
      }
      return {
        uploadForm: {
          cascaderValue: [],
          appId: '',
          customerId: '',
          fileName: '',
          fileUrl: '',
          thumbnailUrl: '',
          type: '5',
          originalFileUrl: '',
          originalThumbnailUrl: ''
        },
        visible: false,
        canSelectApp: true,
        //级联选择器数据  客户名称  应用名称
        //级联选择器数据  客户名称  应用名称
        cascaderProps: {
          lazy: true,
          lazyLoad(node, resolve) {
            const {level} = node;
            console.log("node" + level);
            if (level == 0) {
              getCustomerList().then((response) => {
                console.log("CustomerList--------->" + JSON.stringify(response));
                var costomeList = response.data.map((item) => ({
                  value: item.id,
                  label: item.name,
                  leaf: level >= 1,
                }));
                resolve(costomeList);
              });
            }
            if (level == 1) {
              getApplicationList({customerId: node.value}).then(
                (response) => {
                  console.log(
                    "CustomerList--------->" + JSON.stringify(response)
                  );
                  var applicationList = response.data.map((item) => ({
                    value: item.id,
                    label: item.name,
                    leaf: true,
                  }));
                  resolve(applicationList);
                }
              );
            }
          },
        },
        
        // uploadTip: '',
        // loading: false,
        uploadRules: {
          cascaderValue: [{required: true, trigger: 'change', validator: validateSelectApp}],
          originalFileUrl: [{required: true, message: "请输入跳转网页地址", trigger: "blur"},
            {validator: urlString, trigger: 'blur'}],
          fileName: [{required: true, trigger: ['blur', 'change'], validator: validateFileName}]
        },
      };
    },
    methods: {
      open(query) {
        this.uploadForm.appId = query.appId;
        this.uploadForm.customerId = query.customerId;
        if (this.uploadForm.customerId == undefined ||
          this.uploadForm.customerId.length == 0 ||
          this.uploadForm.appId == undefined ||
          this.uploadForm.appId.length == 0) {
          this.canSelectApp = true;
        } else {
          this.uploadForm.cascaderValue = [this.uploadForm.customerId, this.uploadForm.appId];
          this.canSelectApp = false;
        }
        this.visible = true;
      },
      //选择应用
      selectApp(e) {
        this.uploadForm.customerId = e[0];
        this.uploadForm.appId = e[1];
      },
      //对话框操作
      handleCancel() {
        this.visible = false;
      },
      handleCommit() {
        this.$refs.uploadForm.validate((valid, obj) => {
          console.log(obj)
          if (valid) {
            this.loading = true;
            var that = this;
            addResources(this.uploadForm).then((response) => {
              that.loading = false;
              if (response.code == 200) {
                that.$message({
                  message: "上传成功",
                  type: "success",
                });
                this.$emit('upload-success')
                that.visible = false;
              } else {
                that.$message({
                  message: response.msg | "服务器错误",
                  type: "warning",
                });
              }
            }).catch((error) => {
              that.loading = false;
              console.log(error)
            });
          } else {
            console.log('error submit!!')
            return false
          }
        })
      },
      showTipMessage(msg) {
        this.$message({
          message: msg,
          type: "warning",
        });
      },

      closeDialog() {
        this.autoSourceUrl = '',
          this.autoThumbUrl = '',
          this.uploadForm = {
            cascaderValue: [],
            appId: '',
            customerId: '',
            fileName: '',
            fileUrl: '',
            thumbnailUrl: '',
            type: 5,
            originalFileUrl: '',
            originalThumbnailUrl: '',
          }
        this.$refs.uploadForm.clearValidate();
      }
    },
  };
</script>
<style lang="scss" scoped>

  .dialog-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: row;
  }
</style>
