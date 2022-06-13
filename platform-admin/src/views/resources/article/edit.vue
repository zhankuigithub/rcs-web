<template>
  <div class="container">
    <div class="editor_container" style="padding:60px">
      <el-cascader
        :props="cascaderProps"
        style="width: 100%"
        @change="selectApp"
        size="medium"
      ></el-cascader>

      <el-input size="medium" v-model="title" style="font-size:18px;margin-bottom:10px;margin-top:10px;" placeholder="请输入标题" type="text" show-word-limit maxlength="64" clearable></el-input>
      <Editor ref="editor" @valueChanged="valueChanged"></Editor>
    </div>
    <div class="footer">
      <!-- <el-button
        size="medium"
        icon="el-icon-view"
        @click="previewAction"
      >预览</el-button> -->
      <el-button
        size="medium"
        icon="el-icon-edit-outline"
        @click="saveAction"
        v-loading="loading"
        style="margin-right:50px"
      >保存</el-button>
    </div>
  </div>
</template>

<script>
import Editor from "@/components/editor/editor-tinymce";
import {getToken} from "@/utils/auth";
import {getCustomerList} from "@/api/customer";
import {getApplicationList} from "@/api/application";
import {addResources} from "@/api/resources";
export default {
  name: 'HelloWorld',
  components: {
    Editor
  },
  data() {
    return {
      title:'',
      content:'',
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
      customerId:'',
      appId:'',
      loading:false,
      articleId:''
    };
  },
  methods:{
    saveAction(){
      if(this.customerId + '' == '' || this.appId + '' == '' ){
        this.showTipMessage('请选择应用')
        return
      }
      if(this.title + '' == ''){
        this.showTipMessage('请输入标题')
        return
      }
      if(this.content + '' == ''){
        this.showTipMessage('请输入正文')
        return
      }
      var that = this
      var params= {
        fileUrl: '',
        originalFileUrl:'',
        originalThumbnailUrl:'',
        thumbnailUrl:'',
        appId: this.appId,
        customerId: this.customerId,
        fileName: this.title,
        type: 4,
        content:this.content
      }
      this.loading = true;
      addResources(params).then((response) => {
        that.loading = false;
        if (response.code == 200) {
          that.$message({
            message: "上传成功",
            type: "success",
          });
          that.$router.back(-1);
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
    },
    valueChanged(e){
      this.content = e
    },
    selectApp(e) {
      this.customerId = e[0];
      this.appId = e[1];
    },
    previewAction(){
      this.$refs.editor.preview()
    },
    showTipMessage(msg) {
      this.$message({
        message: msg,
        type: "warning",
      });
    },
  }

}
</script>
<style scoped>
.container{
  padding:60px;
  display:flex;
  justify-content: center;
  transform: translate(0,0);
}
.editor_container{
    width: 1000px;
    background-color: #fff;
    border: 1px solid #dbe1e8;
    border-radius: 2px;
}
.editor_header {
    position: relative;
    margin-bottom: 32px;
}
.footer{
  position: fixed;
  background-color: #fff;
  border: 1px solid #dbe1e8;
  width: 100%;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: right;
  padding: 20px;
}
</style>