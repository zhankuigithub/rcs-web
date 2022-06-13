<template>
  <div class="container">
    <div class="header">融合通信运营管理平台</div>

    <div class="form-warp" v-loading="loading">
      <el-form class="form" ref="form" :model="params" :rules="loginRules" auto-complete="on"
               label-position="left">
        <el-form-item prop="account">
          <el-input class="login-container-input" v-model="params.account" placeholder="请输入手机号码" type="text"
                    auto-complete="on">
            <span slot="prefix" class="svg-container">
              <svg-icon icon-class="user"/>
            </span>
          </el-input>
        </el-form-item>
        <div class="code-box" >
          <el-form-item prop="code">
            <el-input v-model="params.code" placeholder="短信验证码" type="text"
                      onkeyup="this.value = this.value.replace(/[^\d.]/g,'');" maxlength="4">
              <span slot="prefix" class="svg-container">
                <svg-icon icon-class="yanzhengma"/>
              </span>
            </el-input>
          </el-form-item>
          <span class="code-img">
            <el-button @click.native.prevent="getCode" type="success" plain
                       :disabled="isDisabled" >{{
                isDisabled ? countText.count + 's后获取' : countText.click
              }}</el-button>
          </span>
        </div>
        <el-form-item prop="password">
          <el-input class="login-container-input" v-model="params.password" placeholder="请输入新密码" :key="passwordType"
                    :type="passwordType" auto-complete="on">
            <span slot="prefix" class="svg-container">
              <svg-icon icon-class="password"/>
            </span>
          </el-input>
        </el-form-item>
        <el-form-item label="验证" prop="isLock">
          <slider-verify-code v-model="params.isLock" @change="handlerLock"></slider-verify-code>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="margin-top:20px; width:100%;"
            @click.native.prevent="resetPassword" >
            重置密码
          </el-button>
          <el-button type="text" style="margin-top:20px; width:100%;"
            @click.native.prevent="login" >
            登录
          </el-button>
        </el-form-item>
        
      </el-form>
    </div>
  </div>
</template>
<script>
import sliderVerifyCode from '@/components/slider/slider-verify-code.vue';
import {
  resetPassword,
  sendResetPasswordCode
} from "@/api/user";
import {Encrypt} from "@/utils/aes"
export default {
  components: {
    'slider-verify-code': sliderVerifyCode
  },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (value === null || value === '') {
        callback(new Error('请输入手机号码'))
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
    const checkStatus = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("请拖动滑块完成验证"));
      } else {
        callback();
      }
    };
    const validateCode = (rule, value, callback) => {
      if (value.length < 1) {
        callback(new Error('请输入验证码'))
      } else {
        callback()
      }
    }
    return {
      params: {
        account: '',
        password: '',
        code: '',
        isLock:false
      },
      isDisabled:false,
      loginRules: {
        account: [{required: true, trigger: 'blur', validator: validateUsername}],
        password: [{required: true, trigger: 'blur', validator: validatePassword}],
        code: [{required: true, trigger: 'blur', validator: validateCode}],
        isLock: [
          {validator: checkStatus, trigger: 'blur'},
        ],
      },
      loading: false,
      passwordType: 'password',
      countText: {
        count: "59",
        click: "获取短信"
      }
    }
  },
  created() {
    
  },
  methods:{
    handlerLock(data) {
      if (data) {
        // this.$refs.form.validateField('isLock');
      }
    },
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
      if (this.params.account == null || this.params.account == '') {
        this.$refs.form.validateField('account');
        return;
      }
      this.loading = true;
      sendResetPasswordCode({"account": this.params.account}).then((res) => {
        this.loading = false;
      }).catch(() => {
        this.loading = false
      });
    },
    resetPassword(){
      
      this.$refs.form.validate(valid => {
        if(valid){
          let data = {
              "account" : this.params.account,
              "password" : Encrypt(this.params.password),
              "code" : this.params.code,
          }
          this.loading = true;
          resetPassword(data).then((res) => {
            this.$message({
              message: '重置密码成功',
              type: "success",
            });
            this.$router.back(-1);
            this.loading = false;
          }).catch(() => {
            this.loading = false
          });
        }
      })
    },
    login(){
      this.$router.back(-1);
    }

  }
}
</script>

<style lang="less" scoped>
.container{
  min-height: 100vh;
}
.header{
  font-size: 44px;
  background: #2b2f3a;
  padding: 16px 32px;
  font-weight: 500;
  color: white;
  
}
.form-warp{
  flex: 1;
  height: calc(100vh - 82px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.form{
  width: 600px;
  border-radius: 10px;
  padding: 60px;
  box-shadow: 0 2px 8px 0 rgba(7, 17, 27, 0.06);
}
// 样式
  .code-box {
    width:  100%;
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
</style>