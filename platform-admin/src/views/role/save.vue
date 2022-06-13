<template>
  <el-dialog :title="dialogConfig.title" :visible.sync="dialogConfig.visible" :before-close="handleClose"
             :width="dialogConfig.width">
    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
      <el-row>
        <el-col :span="24">
          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="ruleForm.roleName" maxlength="50" placeholder="输入角色名称..." style="width:100%"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="角色编码" prop="code">
            <el-input v-model="ruleForm.code" maxlength="50" placeholder="输入角色编码..." style="width:100%"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="角色状态" prop="status">
            <el-select v-model="ruleForm.status" placeholder="选择状态" style="width:100%">
              <el-option v-for="item in dialogConfig.status" :key="item.label" :label="item.label" :value="item.value"/>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="24">
          <el-form-item label="运营商" prop="carrierIds">
            <el-checkbox-group v-model="selectCarrierIDs">
              <el-checkbox v-for="item in dialogConfig.carrierList" :key="item.id + ''" :label="item.id + ''">
                {{item.name}}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="文本短信客户" prop="clientIds">
            <el-checkbox-group v-model="selectClientIds">
              <el-checkbox v-for="item in dialogConfig.clientList" :key="item.clientId + ''" :label="item.clientId + ''">
                {{item.clientDesc}}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>

    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="resetForm('ruleForm')" v-if="dialogConfig.isAdd" class="el-icon-refresh-left"> 重 置</el-button>
      <el-button type="primary" v-if="dialogConfig.isAdd" @click="submitForm('ruleForm')" class="el-icon-position">
        创 建
      </el-button>
      <el-button type="primary" v-else @click="submitForm('ruleForm')" class="el-icon-position"> 保 存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {
    save,
    edit
  } from '@/api/authRole'

  export default {
    props: {
      dialogConfig: {
        type: Object,
        default: () => {
        },
      },
    },
    data() {
      return {
        ruleForm: {},
        selectCarrierIDs: [],
        selectClientIds: [],
        rules: {
          roleName: [{
            required: true,
            message: "请填写角色名称~",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "请选择是否启用~",
            trigger: "blur"
          }],
          code: [{
            required: true,
            message: "请填写角色编码~",
            trigger: "blur"
          }]
        }
      };
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.dialogConfig.isAdd ?
              this.submitNewData(formName) :
              this.submitEditData(formName);
          } else {
            console.log("error submit!!");
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
        this.selectCarrierIDs = [];
      },
      submitNewData(formName) {
        if (this.selectCarrierIDs.length > 0) {
          let idStr = "";
          this.selectCarrierIDs.forEach((item, index) => {
            idStr += index > 0 ? "," + item : item;
          });
          this.ruleForm.carrierIds = idStr;
        } else {
          this.ruleForm.carrierIds = "";
        }
        save(this.ruleForm).then(resp => {
          if (resp.code === 200) {
            this.$emit('refreshTable')
            this.dialogConfig.visible = false
            this.$notify({
              title: "成功",
              message: "添加记录成功",
              type: "success",
            });
            this.resetForm(formName)
          } else {
            this.$message({
              type: "warning",
              message: resp.msg,
            });
          }
        })
      },
      submitEditData(formName) {
        if (this.selectCarrierIDs.length > 0) {
          let idStr = "";
          this.selectCarrierIDs.forEach((item, index) => {
            idStr += index > 0 ? "," + item : item;
          });
          this.ruleForm.carrierIds = idStr;
        } else {
          this.ruleForm.carrierIds = "";
        }
        if (this.selectClientIds.length > 0) {
          let idStr = "";
          this.selectClientIds.forEach((item, index) => {
            idStr += index > 0 ? "," + item : item;
          });
          this.ruleForm.clientIds = idStr;
        } else {
          this.ruleForm.clientIds = "";
        }
        edit(this.ruleForm).then(resp => {
          if (resp.code === 200) {
            this.$emit('refreshTable')
            this.dialogConfig.visible = false
            this.$notify({
              title: "成功",
              message: "修改记录成功",
              type: "success",
            });
            this.resetForm(formName)
          } else {
            this.$message({
              type: "warning",
              message: resp.msg,
            });
          }
        })
      },
      handleClose(done) {
        this.resetForm('ruleForm')
        done()
      },
      initData(data) {
        this.ruleForm = Object.assign({},data);
        if (this.ruleForm.carrierIds != null && this.ruleForm.carrierIds != '') {
          this.selectCarrierIDs = this.ruleForm.carrierIds.split(",");
        }
        if (this.ruleForm.clientIds != null && this.ruleForm.clientIds != '') {
          this.selectClientIds = this.ruleForm.clientIds.split(",");
        }
      }
    }
  };
</script>
