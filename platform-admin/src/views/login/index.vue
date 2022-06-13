<template>
  <div class="login-container">

    <div class="login-container-form">
      <div class="login-container-title">融合通信运营管理平台</div>
      <div>
        <el-tabs v-model="activeName" @tab-click="handleClick" tabindex="-1">
            <el-tab-pane label="密码登录" name="first" tabindex="-1"></el-tab-pane>
            <el-tab-pane label="短信验证码登录" name="second" tabindex="-1"></el-tab-pane>
        </el-tabs>
      </div>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on"
               label-position="left">
        <el-form-item prop="account">
          <el-input class="login-container-input" v-model="loginForm.account" placeholder="登录手机号码" type="text"
                    tabindex="1" auto-complete="on">
            <!-- <i  class="el-input__icon el-icon-date"></i> -->
            <span slot="prefix" class="svg-container">
              <svg-icon icon-class="user"/>
            </span>
          </el-input>
        </el-form-item>
        <el-form-item prop="password" v-if="activeName=='first'">
          <el-input class="login-container-input" v-model="loginForm.password" placeholder="登录密码" :key="passwordType"
                    :type="passwordType" tabindex="2" auto-complete="on">
            <!-- <i  class="el-input__icon el-icon-date"></i> -->
            <span slot="prefix" class="svg-container">
              <svg-icon icon-class="password"/>
            </span>
          </el-input>
          <el-button type="text" class="forget-password" @click="forgetPassword">
            忘记密码?
          </el-button>
        </el-form-item>
        <el-form-item prop="code" v-if="activeName=='first' || (activeName=='second' && !smsCodeSuccess)">
          <el-input class="login-container-input" @keyup.enter.native="handleLogin" v-model="loginForm.code" placeholder="图形验证码" type="text" tabindex="3">
            <span slot="prefix" class="svg-container">
              <svg-icon icon-class="yanzhengma"/>
            </span>
            <img slot="suffix" style="height:35px;vertical-align: middle;" :src=" codeUrl + timestamp"
                 @click="timestamp = new Date().getTime()">
          </el-input>
        </el-form-item>
        <div class="code-box" v-if="activeName=='second'">
          <el-form-item prop="smsCode">
            <el-input v-model="loginForm.smsCode" placeholder="短信验证码" type="text"
                      onkeyup="this.value = this.value.replace(/[^\d.]/g,'');" @keyup.enter.native="handleLogin" maxlength="4" tabindex="5">
              <span slot="prefix" class="svg-container">
                <svg-icon icon-class="yanzhengma"/>
              </span>
            </el-input>
          </el-form-item>
          <span class="code-img">
            <el-button :loading="loading02" @click.native.prevent="getCode" type="success" plain
                       :disabled="isDisabled"  tabindex="4">{{
                isDisabled ? countText.count + 's后获取' : countText.click
              }}</el-button>
          </span>
        </div>
        <el-button :loading="loading01" type="primary" style="margin-top:20px; width:100%;"
                   @click.native.prevent="handleLogin" tabindex="6">
          <i class="el-icon-s-promotion"> </i>
          点击登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script>
import {
  loginBySmsCode,
  sendSmsCode
} from "@/api/user";
import {Encrypt} from "@/utils/aes"
export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (value === null || value === '') {
        callback(new Error('请输入登录账户名'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length == 0) {
        callback(new Error('请输入登录密码'))
      } else if (value.length < 6) {
        callback(new Error('登录密码错误'))
      } else {
        callback()
      }
    }
    const validateCode = (rule, value, callback) => {
      if (this.activeName == "first" && value.length < 1) {
        callback(new Error('请输入验证码'))
      } else {
        callback()
      }
    }
    const validateSmsCode = (rule, value, callback) => {
      if (value.length < 1) {
        callback(new Error('请输入验证码'))
      } else {
        callback()
      }
    }
    return {
      loginForm: {
        account: '',
        password: '',
        code: '',
        smsCode: ''
      },
      loginRules: {
        account: [{required: true, trigger: 'blur', validator: validateUsername}],
        password: [{required: true, trigger: 'blur', validator: validatePassword}],
        code: [{required: true, trigger: 'blur', validator: validateCode}],
        smsCode: [{required: true, trigger: 'blur', validator: validateSmsCode}]
      },
      loading01: false,
      loading02: false,
      passwordType: 'password',
      redirect: undefined,
      codeUrl: process.env.VUE_APP_ADMIN_BASE_API + '/captcha/',
      timestamp: '',
      activeName: "first",
      smsCodeSuccess: false,
      isDisabled: false,
      countText: {
        count: "59",
        click: "获取短信"
      }
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.timestamp = new Date().getTime()
  },
  methods: {
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading01 = true

          if (this.activeName === "first") {
            let data = {
              "account" : this.loginForm.account,
              "password" : Encrypt(this.loginForm.password),
              "code" : this.loginForm.code,
            }
            this.$store.dispatch('user/login', data).then(() => {
              this.$router.push({path: this.redirect || '/'})
              this.loading01 = false
            }).catch(() => {
              this.timestamp = new Date().getTime()
              this.loading01 = false
            })
          } else {
            this.$store.dispatch('user/smsLogin', this.loginForm).then(() => {
              this.$router.push({path: this.redirect || '/'})
              this.loading01 = false
            }).catch(() => {
              this.timestamp = new Date().getTime()
              this.loading01 = false
            })
          }
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    handleClick(tab, event) {
      this.timestamp = new Date().getTime()
      console.log(tab, event);
    },
    // methods 中写方法
    // 倒计时
    countTime() {
      const TIME_COUNT = 60; //倒计时60秒
      if (!this.timer) {
        this.countText.count = TIME_COUNT;
        this.isDisabled = true;
        this.timer = setInterval(() => {
          if (this.countText.count > 0 && this.countText.count <= TIME_COUNT) {
            this.countText.count--;
          } else {
            this.isDisabled = false;
            clearInterval(this.timer);
            this.timer = null;
            this.smsCodeSuccess = false;
          }
        }, 1000);
      }
    },
    // 点击获取短信验证码
    getCode() {
      if (this.loginForm.account == null || this.loginForm.account == '') {
        this.$refs.loginForm.validateField('account');
        return;
      }
      this.loading02 = true;
      sendSmsCode(this.loginForm).then((res) => {
        this.countTime();
        this.smsCodeSuccess = true;
        this.loading02 = false;
      }).catch(() => {
        this.loading02 = false
      });
    },
    forgetPassword(){
      this.$router.push('/forget-password')
    }
  }
}
</script>

<style lang="scss">
.login-container {
  width: 100%;
  height: 100vh;
  background: url('~@/assets/images/login_background.png');
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: flex-end;

  &-form {
    width: 380px;
    height: 520px;
    padding: 4vh;
    margin-right: 10%;
    margin-left: 20px;
    background: url('~@/assets/images/login_form.png');
    background-size: 100% 100%;
    border-radius: 10px;
    box-shadow: 0 2px 8px 0 rgba(7, 17, 27, 0.06);
  }

  &-hello {
    font-size: 32px;
    color: #fff;
  }

  &-title {
    margin-bottom: 30px;
    font-size: 20px;
    color: #fff;
  }

  &-tips {
    position: fixed;
    width: 100%;
    height: 40px;
    color: rgba(255, 255, 255, 0.856);
    text-align: center;
  }

  &-input {
    margin-top: 10px;
    height: 35px;
  }

  // 样式
  .code-box {
    display: flex;
    font-size: 14px;
    padding-top: 10px;
  }

  .code-img {
    flex: 4;
    height: 40px;
    line-height: 40px;
    display: inline-block;
    border-radius: 4px;
    box-sizing: border-box;
    outline: 0;
    margin-left: 10px;
    overflow: hidden;
  }

  .el-tabs__item {
    padding: 0 20px;
    height: 40px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    line-height: 40px;
    display: inline-block;
    list-style: none;
    font-size: 14px;
    font-weight: 500;
    color: #fff;
    position: relative;
  }
  .forget-password{
    float: right;
    font-weight: 400;
    font-style: normal;
    font-size: 14px;
    color: #0FAEE8;
  }
}

</style>
