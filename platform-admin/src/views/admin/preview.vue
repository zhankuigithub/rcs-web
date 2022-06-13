<template>
  <el-dialog title="详情" :visible.sync="config.open" :close-on-click-modal="false" width="40%" center>
    <el-form status-icon label-width="100px" class="demo-ruleForm">
      <el-form-item label="头像">
        <el-image v-if="viewData.avatar && viewData.avatar.length" :src="viewData.avatar" style="width: 40px; height: 40px; border-radius: 8px;" />
        <img v-else src="@/assets/images/default.png" style="width: 40px; height: 40px; border-radius: 8px;" />
      </el-form-item>
      <el-form-item label="姓名">
        <span>{{viewData.name}}</span>
      </el-form-item>
      <el-form-item label="账号">
        <span>{{viewData.account}}</span>
      </el-form-item>
      <el-form-item label="角色">
        <el-tag v-for="role in viewData.roles" :key="role.id" style="margin-right: 8px;">
          <i class="el-icon-s-flag"></i> {{role.roleName}}
        </el-tag>
      </el-form-item>
      <el-form-item label="状态">
        <el-tag type="success" v-if="viewData.status === 0">正常</el-tag>
        <el-tag type="danger" v-else>异常</el-tag>
      </el-form-item>
      <el-form-item label="手机号">
        <span>{{viewData.phone}}</span>
      </el-form-item>
      <el-form-item label="注册时间">
        <span>{{viewData.insertDt}}</span>
      </el-form-item>
      <el-form-item label="修改时间" v-if="viewData.updateDt && viewData.updateDt.length">
        <span>{{viewData.updateDt}}</span>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
  import {
    detail
  } from '@/api/authAdmin'
  export default {
    props: {
      config: {
        type: Object,
        default () {
          return {
            open: false
          }
        }
      }
    },
    data() {
      return {
        viewData: {}
      };
    },
    methods: {
      initData(data) {
        detail(data).then(resp => {
          this.viewData  = resp.data
        })
      }
    }
  };
</script>
