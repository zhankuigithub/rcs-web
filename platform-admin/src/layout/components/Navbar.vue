<template>
  <div class="navbar">
    <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb class="breadcrumb-container" />

    <div class="right-menu">
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <img v-if="user.avatar && user.avatar.length" :src="user.avatar" class="user-avatar" >
          <img v-else src="@/assets/images/default.png" class="user-avatar" >
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <el-dropdown-item >
            <el-avatar v-if="user.avatar && user.avatar.length" size="small" :src="user.avatar" style="vertical-align: middle;"></el-avatar>
            <el-avatar v-else size="small" src="@/assets/images/default.png" style="vertical-align: middle;"></el-avatar>
            {{user.name}}
          </el-dropdown-item>
          <router-link to="/">
            <el-dropdown-item divided>
              <span class="el-icon-s-home"></span>  首页
            </el-dropdown-item>
          </router-link>
          <a  href="/admin/">
            <el-dropdown-item><span class="el-icon-user"></span> 个人资料</el-dropdown-item>
          </a>
          <el-dropdown-item  @click.native="changePassword"><span class="el-icon-suitcase" ></span> 修改密码</el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;" class="el-icon-s-promotion"> 退出</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <!-- 修改密码弹框 -->
    <el-dialog title="修改密码" :visible.sync="passwordDialogVisible" width="50%">
      <el-form ref="passwordForm" class="account-form" :model="passwordForm" :rules="passwordRules" label-width="180px"
               size="medium">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" maxlength="18" placeholder="请输入原始密码!" v-model="passwordForm.oldPassword"></el-input>
        </el-form-item>
        <el-form-item label="新密码"  prop="password">
          <el-input type="password" maxlength="18" placeholder="请输入新密码!" v-model="passwordForm.password"></el-input>
        </el-form-item>
        <el-form-item v-if="passwordForm.password!=''">
          <password-strength v-model="passwordForm.password"></password-strength>
        </el-form-item>
        <el-form-item label="重复新密码"  prop="passwordAgain">
          <el-input type="password" maxlength="18" placeholder="请再次输入新密码!" v-model="passwordForm.passwordAgain"></el-input>
        </el-form-item>
        <el-input v-if="false" v-model="passwordForm.id"></el-input>
        <el-form-item class="align-right">
          <el-button @click="e => passwordDialogVisible = false">取消</el-button>
          <el-button @click="submitForm">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import {isPassword, passwordStrengthCK} from '@/utils/validate'

import {
  updatePassWord
} from "@/api/user";
import {Encrypt} from "@/utils/aes"

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  data() {
    return {
      passwordDialogVisible: false,
      passwordForm: {
        "id": "",
        "oldPassword": "",
        "password": "",
        "passwordAgain": ""
      }
    };
  },

  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'user',
      'roles'
    ]),
    passwordRules: function () {
      return {
        oldPassword: [{
          message: '原始密码不能为空',
          trigger: "blur",
          required: true
        }],
        password: [{required: true, trigger: 'blur', validator: this.validatePassword}],
        passwordAgain: [{
          validator: this.verifyPasswordAgain,
          trigger: "blur",
          required: true
        }]
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    },
    changePassword() {
      this.passwordForm = {
        "id": "",
        "oldPassword": "",
        "password": "",
        "passwordAgain": ""
      }
      if(this.$refs["passwordForm"]){
        this.$refs["passwordForm"].resetFields();
      }
      this.passwordDialogVisible = true;
    },
    /** 修改密码 */
    submitForm() {
      this.$refs["passwordForm"].validate((valid) => {
        if (valid) {
          this.passwordForm.id = this.user.id
          let formdata = {
            'id': this.passwordForm.id,
            'oldPassword': Encrypt(this.passwordForm.oldPassword),
            'password': Encrypt(this.passwordForm.password),
            'passwordAgain': Encrypt(this.passwordForm.passwordAgain)
          };
          updatePassWord(formdata).then(res => {
            if (res.code == 200 && res.data == true) {
              this.logout();
            }
          });
        }
      });
    },
    validatePassword(rule, value, callback){
      if (value === undefined || value === null || value === '') {
        callback(new Error('请输入密码'))
        return
      }

      if (!isPassword(value)) {
        callback(new Error('密码不能少于6位'))
      }else if(passwordStrengthCK(value)<2){
        callback(new Error('密码强度不足'))
      } else {
        callback()
      }
    },
    verifyPassword(rule, code, callback) {
      if (!code) {
        callback(new Error('请输入新密码！'))
        return;
      }
      if (!isPassword(code)) {
        callback(new Error('新密码不能小于6位！'))
        return;
      }
      if (this.passwordForm.oldPassword === code) {
        callback(new Error('新密码和原始密码不能相同！'))
        return;
      }
      callback()
    },
    verifyPasswordAgain(rule, code, callback) {
      if (!code) {
        callback(new Error('请再次输入新密码！'))
        return;
      }
      if (!isPassword(code)) {
        callback(new Error('新密码不能小于6位！'))
        return;
      }
      if (this.passwordForm.password !== this.passwordForm.passwordAgain) {
        callback(new Error('两次输入的密码不一致！'))
        return;
      }
      callback()
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
