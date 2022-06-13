<template>
  <el-dialog :title="dialogConfig.title" :visible.sync="dialogConfig.visible" :before-close="handleClose" :close-on-click-modal="false" :width="dialogConfig.width">
    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
      <el-row>
        <el-col :span="12">
          <el-form-item label="用户名" prop="name">
            <el-input placeholder="请填写管理员姓名" maxlength="20" v-model="ruleForm.name"></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input placeholder="请填写11位手机号" maxlength="20" v-model="ruleForm.phone"></el-input>
          </el-form-item>
          <el-form-item label="角色" prop="roleIds">
            <el-select  multiple v-model="ruleForm.roleIds" placeholder="选择管理员角色" style="width:100%">
              <el-option v-for="item in roles" :key="item.id" :label="item.roleName" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="头像">
            <file-upload ref="fileUpload" :config="fileConfig" @on-resp="fileItems" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="账号" prop="account">
            <el-input placeholder="请填写登录账号" maxlength="20" v-model="ruleForm.account"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="密码" prop="password" :required="dialogConfig.isAdd" v-if="dialogConfig.isAdd || hasSuperAdminRole">
            <el-input placeholder="请填写登录密码" maxlength="20" v-model="ruleForm.password"  show-password></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="ruleForm.status" placeholder="选择状态" style="width:100%">
              <el-option v-for="item in dialogConfig.status" :key="item.label" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="resetForm('ruleForm')" v-if="dialogConfig.isAdd" class="el-icon-refresh-left"> 重 置</el-button>
      <el-button type="primary" v-if="dialogConfig.isAdd" @click="submitForm('ruleForm')" class="el-icon-position">
        创 建</el-button>
      <el-button type="primary" v-else @click="submitForm('ruleForm')" class="el-icon-position"> 保 存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {
    save,
    edit
  } from '@/api/authAdmin'
  import {getRoles} from '@/api/authRole'
  import {
    getToken
  } from '@/utils/auth'
  import FileUpload from '@/components/file/upload'
  import { mapGetters } from 'vuex'
  import {Encrypt} from "@/utils/aes"
  export default {
    components: {
      FileUpload
    },
    props: {
      dialogConfig: {
        type: Object,
        default: ()=>{},
      },
    },
    computed: {
      ruleForm() {
        this.fileConfig.files = this.dialogConfig.files;
        return this.dialogConfig.formData
      },
      ...mapGetters([
        'user',
        'userRoles'
      ])
    },
    data() {
      return {
        roles: [],
        fileConfig: {
          action: process.env.VUE_APP_ADMIN_BASE_API + '/manage/admin/uploadFile',
          name: 'file',
          files: [],
          accept: 'image/png, image/jpeg, image/png',
          headers: {
            "ACCESS-TOKEN": getToken()
          },
          limit: 1
        },
        rules: {
          account: [{
            required: true,
            message: "请输入用户名~",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "请选择是否启用~",
            trigger: "blur"
          }],
          roleIds: [{
            required: true,
            message: "请分配用户角色~",
            trigger: "blur"
          }],
          password: [{
            message: "请输入密码~",
            trigger: "blur"
          }]
        },
        hasSuperAdminRole:false
      };
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.dialogConfig.isAdd ?
              this.submitNewData() :
              this.submitEditData();
            this.dialogConfig == { ...this.dialogConfig.default
            }
          } else {
            console.log("error submit!!");
            return false;
          }
        });
      },
      resetForm(formName) {
        this.fileConfig.files = [];
        this.$refs[formName].resetFields();
      },
      submitNewData() {
        let data = this.ruleForm;
        data.password = Encrypt(this.ruleForm.password)
        save(data).then(resp => {
          if (resp.code === 200) {
            this.$emit('refreshTable')
            this.dialogConfig.visible = false
            this.$notify({
              title: "成功",
              message: "添加记录成功",
              type: "success",
            });
          } else {
            this.$message({
              type: "warning",
              message: resp.msg,
            });
          }
        })
      },
      submitEditData() {
        let data = this.ruleForm;
        data.password = Encrypt(this.ruleForm.password)
        edit(data).then(resp => {
          if (resp.code === 200) {
            this.$emit('refreshTable')
            this.dialogConfig.visible = false
            this.$notify({
              title: "成功",
              message: "修改记录成功",
              type: "success",
            });
          } else {
            this.$message({
              type: "warning",
              message: resp.msg,
            });
          }
        })
      },
      loadRoles(){
        getRoles().then(resp => {
          this.roles = resp.data
        })
      },
      fileItems(response) {
        if (response.code === 200) {
          this.ruleForm.avatar = response.data
        }else{
           this.$message.error(response.msg);
        }
      },
      handleClose(done) {
        this.fileConfig.files.splice(0,  this.fileConfig.files.length)
        done()
      }
    },
    mounted() {
      this.loadRoles()
      this.hasSuperAdminRole = false
      if(this.user && this.user.roles){
        this.user.roles.forEach(element => {
           if(element.code == 'SUPER_ADMIN'){
             this.hasSuperAdminRole = true
           }
        });
      }

    }

  };
</script>
