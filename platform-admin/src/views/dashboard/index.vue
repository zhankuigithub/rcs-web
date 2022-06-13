<template>
  <div class="dashboard-container">
    <el-row :gutter="24">
      <el-col :span="6">
        <el-card class="box-card" shadow="hover">
          <h5 class="title"><span>个人信息</span></h5>
          <div class="info-item">
            <b>用户名</b>
            <span>{{user.name}}</span>
          </div>
          <div class="info-item">
            <b>账 户</b>
            <span>{{user.account}}</span>
          </div>
          <!-- <div class="info-item">
            <b>认 证</b>

            <span v-if="user.status === 0" >
              <i style="color: red;margin-right: 10px;"><b class="el-icon-medal"></b>未认证</i>
              <el-link type="primary" @click="$router.push('/customer/authorize')">立即认证?</el-link>
            </span>
            <span v-if="user.status === 1" >
                <i class="el-icon-medal" style="color: blue; margin-right: 10px;">审核中</i>
            </span>
             <span v-if="user.status === 2">
                <b class="el-icon-medal-1" style="color: orangered; margin-right: 10px;">认证通过</b>
            </span>
          </div> -->
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {
    mapGetters
  } from 'vuex'

  export default {
    name: 'Dashboard',
    computed: {
      ...mapGetters([
        'user',
        'roles'
      ])
    },
    methods: {
      getRoles(roles) {
        roles.foreEach(item => {
          if (item === 'user') {
            return '普通用户'
          }
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
  .dashboard {
    &-container {
      margin: 30px;
      font-size: 14px;

      & .box-card {
        & .title{
          margin: 8px 0 22px;
          font-size: 16px;
        }

        & .info-item {
          height: 32px;
          align-items: center;
          font-size: 14px;

          &>b {
            display: inline-block;
            width: 60px;
            color: #650061;
          }

          &>span {}
        }
      }
    }

  }
</style>
