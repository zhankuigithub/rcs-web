<template>
  <el-dialog :title="dialogTitle" :visible.sync="visible" :close-on-click-modal="false" width="650px"
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
      <el-form-item :label="'上传' + dialogTitle" prop="fileUrl">
        <el-upload
          ref="fileUpload"
          :headers="headers"
          :accept="accept"
          :action="action"
          :before-upload="beforeUploadFile"
          :on-change="handleChange"
          :on-remove="handleFileRemove"
          :on-success="handleSuccessOfSource"
          drag
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">{{ uploadTip }}</div>
        </el-upload>

        <img
          v-if="this.isShow && this.resourcesType == 'image'"
          class="auto-image"
          :src="this.autoSourceUrl"
        />
        <audio
          v-if="this.isShow && this.resourcesType == 'audio'"
          class="auto-image audio"
          :src="this.autoSourceUrl"
          controls="controls"
        ></audio>
        <video
          v-if="this.isShow && this.resourcesType == 'video'"
          class="auto-video"
          :src="this.autoSourceUrl"
          controls="controls"
        ></video>

      </el-form-item>

      <el-form-item :label="'输入' + dialogTitle + '地址'" prop="originalFileUrl">
        <el-input v-model="uploadForm.originalFileUrl" :placeholder="'请输入' + dialogTitle + '地址'"
                  size="small"></el-input>
      </el-form-item>
      <el-form-item label="上传缩略图">
        <el-upload
          ref="thumbnailFileUpload"
          :headers="headers"
          accept="image/png,image/jpeg, image/jpg"
          :action="action"
          :before-upload="beforeUploadThumbnail"
          :on-change="handleThumbnailChange"
          :on-remove="handleThumbnailFileRemove"
          :on-success="handleSuccessOfThumb"
          drag
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">只能上传jpg/jpeg/png文件，且不超过200k</div>
        </el-upload>
        <img
          v-if="this.isShow"
          class="auto-image"
          :src="this.autoThumbUrl"
        />
      </el-form-item>
      <el-form-item label="输入缩略图地址" v-if="this.resourcesType !== 'image'">
        <el-input v-model="uploadForm.originalThumbnailUrl" placeholder="输入文件地址" size="small"></el-input>
      </el-form-item>
      <el-form-item label="素材名称" required prop="fileName">
        <el-input
          v-model="uploadForm.fileName"
          placeholder="请输入素材名称"
          clearable
          size="small"
        ></el-input>
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
  import {getToken} from "@/utils/auth";
  import {getCustomerList} from "@/api/customer";
  import {getApplicationList} from "@/api/application";
  import {addResources} from "@/api/resources";

  export default {
    name: "ResourcesUpload",
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
      const validateFile = (rule, value, callback) => {

        if (this.uploadForm.originalFileUrl + '' !== '' || value + '' !== '') {
          this.$refs.uploadForm.clearValidate('originalFileUrl')
          return callback()

        } else {
          return callback(new Error('请上传文件或等待文件上传成功'))
        }
      }
      const validateOriginalFileUrl = (rule, value, callback) => {
        if (value + '' !== '' || this.uploadForm.fileUrl + '' !== '') {
          this.$refs.uploadForm.clearValidate('fileUrl')
          return callback()
        } else {
          return callback(new Error('请输入文件地址'))
        }
      }
      const validateOriginalFileUrl1 = (rule, value, callback) => {
        if (value !== '') {
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
          if (re.test(value)) {
            callback();
          } else {
            callback(new Error("您输入的文件地址无效"));
          }
        } else {
          if (this.uploadForm.fileUrl + '' !== '') {
            return callback()
          } else {
            return callback(new Error('请输入文件地址'))
          }
        }
      }

      const validateFileName = (rule, value, callback) => {
        if (this.uploadForm.fileName == undefined ||
          this.uploadForm.fileName.length == 0) {
          callback(new Error('请输入素材名称'))
        } else {
          callback()
        }
      }
      return {
        autoSourceUrl: '',
        autoThumbUrl: '',
        isShow: false,
        dialogTitle: "图片",
        visible: false,
        canSelectApp: true,
        resourcesType: "image", //上传素材类型  image video audio
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
        //上传组件参数
        accept: "image/png,image/jpeg, image/jpg",
        headers: {"ACCESS-TOKEN": getToken()},
        action: process.env.VUE_APP_PLATFORM_BASE_API + "/manage/material/uploadMaterial",
        uploadTip: '',
        loading: false,
        uploadForm: {
          cascaderValue: [],
          appId: '',
          customerId: '',
          fileName: '',
          fileUrl: '',
          thumbnailUrl: '',
          type: '',
          originalFileUrl: '',
          originalThumbnailUrl: ''
        },
        uploadRules: {
          cascaderValue: [{required: true, trigger: 'change', validator: validateSelectApp}],
          fileUrl: [{trigger: 'blur', validator: validateFile}],
          originalFileUrl: [{trigger: 'change', validator: validateOriginalFileUrl},
            {trigger: 'blur', validator: validateOriginalFileUrl}],
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
        this.resourcesType = query.resourcesType;
        if (this.resourcesType == "image") {
          this.accept = "image/png,image/jpeg, image/jpg";
          this.dialogTitle = "图片";
          this.uploadTip = '只能上传jpg/jpeg/png文件，且不超过2M';
          this.uploadForm.type = 1;
        }
        if (this.resourcesType == "audio") {
          this.accept = "audio/amr,audio/mp3";
          this.dialogTitle = "音频";
          this.uploadTip = '只能上传amr/mp3文件，且不超过5M';
          this.uploadForm.type = 2;
        }
        if (this.resourcesType == "video") {
          this.accept = "video/mp4";
          this.dialogTitle = "视频";
          this.uploadTip = '只能上传mp4文件，且不超过10M';
          this.uploadForm.type = 3;
        }
        this.visible = true;
      },
      //选择应用
      selectApp(e) {
        this.uploadForm.customerId = e[0];
        this.uploadForm.appId = e[1];
      },

      //上传文件之前校验
      beforeUploadFile(file) {
        var fileLimitSize = 0;
        if (this.resourcesType == 'image') {
          fileLimitSize = 2;
        } else if (this.resourcesType == 'audio') {
          fileLimitSize = 5;
        } else if (this.resourcesType == 'video') {
          fileLimitSize = 10;
        }
        const fileSize = file.size / 1024 / 1024;
        if (fileSize > fileLimitSize) {
          this.$message.error('上传' + this.dialogTitle + '大小不能超过' + fileLimitSize + 'MB!');
        }
        return fileSize < fileLimitSize;
      },
      //删除已上传文件时
      handleFileRemove(file, fileList) {
        if (file.status == 'success') {
          this.uploadForm.fileUrl = '';
          this.$refs.uploadForm.validateField('fileUrl');
        }
      },
      //文件上传 文件列表发生变化
      handleChange(file, fileList) {
        if (file.status == 'success') {
          this.uploadForm.fileUrl = file.response.data.url;
          if (fileList.length > 1) {
            fileList.splice(0, 1);
          }
          this.$refs.uploadForm.validateField('fileUrl');
        }
      },
      handleSuccessOfSource(response) {
        if (response.code == 200) {
          this.isShow = true;
          let data = response.data;
          this.autoSourceUrl = data.url;
        }
      },
      handleSuccessOfThumb(response) {
        if (response.code == 200) {
          this.isShow = true;
          let data = response.data;
          this.autoThumbUrl = data.url;
        }
      },
      //上传缩略图
      beforeUploadThumbnail(file) {
        const fileSize = file.size / 1024;
        if (fileSize >= 200) {
          this.$message.error('上传缩略图大小不能超过200kb!');
        }
        return fileSize < 200;
      },
      handleThumbnailChange(file, fileList) {
        if (file.status == 'success') {
          this.uploadForm.thumbnailUrl = file.response.data.url;
          if (fileList.length > 1) {
            fileList.splice(0, 1);
          }
        }
      },
      handleThumbnailFileRemove(file, fileList) {
        if (file.status == 'success') {
          this.uploadForm.thumbnailUrl = '';
        }
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
          this.isShow = false,
          this.uploadForm = {
            cascaderValue: [],
            appId: '',
            customerId: '',
            fileName: '',
            fileUrl: '',
            thumbnailUrl: '',
            type: '',
            originalFileUrl: '',
            originalThumbnailUrl: '',
          }
        this.$refs.uploadForm.clearValidate();
        this.$refs.fileUpload.clearFiles();
        this.$refs.thumbnailFileUpload.clearFiles();
      }
    },
  };
</script>
<style lang="scss" scoped>
  .resource-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 178px;
    height: 178px;

  }

  .uploader-plus-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }

  .resource-file-wrap {
    width: 178px;
    height: 178px;
    display: block;
  }

  .resource-uploader .el-upload:hover {
    border-color: #409eff;
  }

  .dialog-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: row;
  }

  .auto-image {
    max-height: 100px;
    width: auto;
  }

  .auto-video {
    max-width: 120px;
    height: auto;
  }

  .audio {
    width: 80%;
    position: absolute;
    height: 20px;
  }
</style>
