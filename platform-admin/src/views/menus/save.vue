<template>
  <el-dialog :title="dialogConfig.title" :visible.sync="dialogConfig.visible" :width="dialogConfig.width" :close-on-click-modal = "false" @close='closeDialog'>
    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm" size="small">
      <el-row>
        <el-col :span="24">
          <el-form-item label="菜单标题" prop="title">
            <el-input v-model="ruleForm.title" maxlength="50" placeholder="输入菜单标题(50字以内)..." style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="菜单路由" prop="path">
            <el-input v-model="ruleForm.path" maxlength="50" placeholder="输入菜单路由..." style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="父级组件" prop="pid">
            <el-select :disabled="true" v-model="ruleForm.pid" placeholder="选择父级组件..." style="width:100%" clearable @change="ruleForm.operate = operateAry.join(',')">
              <el-option v-for="item in parents" :key="item.id" :label="item.title" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="页面组件" prop="component">
            <el-input v-model="ruleForm.component" maxlength="50" placeholder="输入views页面组件(路径不包含views目录,如/views/xxx/xx 应填写/xxx/xx)..." style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="菜单图标" prop="icon">
            <el-input v-model="ruleForm.icon" maxlength="50" placeholder="输入菜单图标..." style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="页面操作" prop="operate">
            <input type="hidden" v-model="ruleForm.operate" />
            <el-select v-model="operateAry" placeholder="选择页面操作..." multiple  clearable style="width:100%" @change="ruleForm.operate = operateAry.join(',')">
              <el-option v-for="item in operates" :key="item.label" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="菜单排序" prop="sort">
            <el-input v-model="ruleForm.sort" placeholder="输入菜单排序编号..."/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="隐藏菜单" prop="hidden">
            <el-switch v-model="ruleForm.hidden" active-value="1" inactive-value="0">
            </el-switch>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="是否启用" prop="status">
            <el-select v-model="ruleForm.status" placeholder="选择状态" style="width:100%">
              <el-option v-for="item in dialogConfig.status" :key="item.label" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="resetForm('ruleForm')" v-if="dialogConfig.isAdd" class="el-icon-refresh-left" size="small"> 重 置</el-button>
      <el-button type="primary" v-if="dialogConfig.isAdd" @click="submitForm('ruleForm')" class="el-icon-position" size="small">
        创 建</el-button>
      <el-button type="primary" v-else @click="submitForm('ruleForm')" class="el-icon-position" size="small"> 保 存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {operateItems} from '@/assets/initdata'
  import {
    save,
    edit,
    getMenuList,
  } from '@/api/authMenus'
  export default {
    props: {
      dialogConfig: {
        type: Object,
        default: () => {},
      },
    },
    data() {
      return {
        ruleForm: {},
        operateAry: [],
        parents: [],
        operates: operateItems,
        rules: {
          title: [{
            required: true,
            message: "请填写菜单标题~",
            trigger: "blur"
          }],
          path: [{
            required: true,
            message: "请填写页面路由~",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "请选择是否启用~",
            trigger: "blur"
          }],
          sort: [{
            required: true,
            message: "请填写菜单排序号~",
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
        if(this.$refs[formName]){
          this.$refs[formName].resetFields();
        }
      },
      submitNewData(formName) {
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

      initData(data, isAdd) {
        if (isAdd) {
          if (data.id != null) {
            this.ruleForm = Object.assign({},{pid : data.id})
          }
        } else {
          this.ruleForm = Object.assign({},data)
          if (data.hidden != null) {
            this.ruleForm.hidden = data.hidden+""; // el-switch 只能使用string类型进行赋值生效
          }
          if(data.operate != null){
            this.operateAry = data.operate.split(',')
          }
        }
        this.loadMenuList({})
      },
      loadMenuList(params){
        getMenuList(params).then(resp => {
          this.parents = resp.data
        })
      },
      closeDialog() {
        this.$refs['ruleForm'].resetFields();
      }
    }
  };
</script>
