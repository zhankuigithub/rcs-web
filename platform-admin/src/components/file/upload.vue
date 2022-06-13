<template>
  <section>
    <el-upload :action="config.action" :name="config.name" :headers="config.headers" :list-type=" config.style === 'button' ? '': 'picture-card'"
      :auto-upload="config.autoUpload" :accept="config.accept" :limit="config.limit" :file-list="config.files"
      :on-success="handleSuccess" :on-remove="handleRemove" :before-upload="beforeAvatarUpload" :on-change="handleChange">
      <template v-if="config.style === 'button'">
        <el-button size="small" type="primary">点击上传</el-button>
        <div slot="tip" class="el-upload__tip">只能上传{{config.accept}}文件，且不超过 {{config.maxSize || 2}}M</div>
      </template>
      <template v-else>
        <i slot="default" class="el-icon-plus"></i>
        <div slot="file" slot-scope="{file}">
          <img class="el-upload-list__item-thumbnail" :src="file.url" alt="">
          <span class="el-upload-list__item-actions">
            <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
              <i class="el-icon-zoom-in"></i>
            </span>
            <span v-if="!disabled" class="el-upload-list__item-delete" @click="handleDownload(file)">
              <i class="el-icon-download"></i>
            </span>
            <span v-if="!disabled" class="el-upload-list__item-delete" @click="handleRemove(file)">
              <i class="el-icon-delete"></i>
            </span>
          </span>
        </div>
      </template>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </section>
</template>

<script>
  export default {
    name: 'FileUpload',
    props: {
      config: {
        type: Object,
        default () {
          return {
            listType: "picture-card",
            autoUpload: false,
            accept: '',
            limit: 20,
            action: "#"
          }
        }
      }
    },
    data() {
      return {
        dialogImageUrl: '',
        dialogVisible: false,
        disabled: false,
        files: []
      };
    },
    methods: {
      handleRemove(file) {
        this.config.files.splice(this.config.files.findIndex(e => e.name === file.name), 1)
      },
      handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      },
      handleDownload(file) {
        console.log(file);
      },
      handleSuccess(response, file, fileList) {
        this.$emit('on-resp', response)
      },
      //文件上传 文件列表发生变化
      handleChange(file,fileList){
        if(file.status == 'success'){
          if(fileList.length > 1){
            fileList.splice(0,1);
          }
        }
      },
      handleRemove(file, fileList){
        this.$emit('on-remove',file,fileList);
      },
      beforeAvatarUpload(file) {
        const isOK = (!this.config.accept || this.config.accept == '' || this.config.accept === null) ||
          this.config.accept.indexOf(file.type) >= 0;
        let maxSize = this.config.maxSize | 2
        const isLimit = file.size / 1024 / 1024 < maxSize;

        if (!isOK) {
          this.$message.error('只支持' + this.config.accept + '格式文件上传');
        }
        if (!isLimit) {
          this.$message.error('上传文件最大支持' + maxSize + 'M');
        }
        return isOK && isLimit;
      },
      clear(){
        this.config.files = []
      }
    }
  }
</script>
