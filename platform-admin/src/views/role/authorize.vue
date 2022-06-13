<template>
  <el-dialog :title="config.title" :visible.sync="config.visible" :width="config.width">
    <el-form :model="ruleForm" ref="ruleForm" label-width="100px" class="demo-ruleForm">
      <el-row>
        <el-col :span="24">
          <el-form-item label="授权角色">
            <el-input :value="ruleForm.roleName" style="width: 100%;" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="选择菜单">
            <div >
              <el-input
                placeholder="输入关键字进行过滤"
                v-model="filterText">
              </el-input>
              <div style="max-height: 400px; margin-top: 5px; overflow: overlay;">
                <el-tree :data="menusTree.tree" :default-checked-keys="menusTree.checked" show-checkbox default-expand-all node-key="id"
                  ref="tree" highlight-current :props="defaultProps" :filter-node-method="filterNode">
                </el-tree>
              </div>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm('ruleForm')" class="el-icon-position"> 授 权</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {
    getMenus
  } from '@/api/authMenus'

  import {
    roleAuthorize
  } from '@/api/rolePermisssion'

  export default {
    props: {
      config: {
        type: Object
      },
    },
    data() {
      return {
        filterText: '',
        ruleForm: {},
        menusTree: {},
        authForm: [],
        defaultProps: {
          children: 'children',
          label: 'label'
        }
      };
    },
    methods: {
      submitForm(formName) {
        let keys = this.$refs.tree.getCheckedKeys().concat(this.$refs.tree.getHalfCheckedKeys())
        if(keys.length <= 0){
          this.$message({
            type:"error",
            message: '请选择授权菜单'
          })
          return;
        }
        keys.sort();
        keys.forEach(arg =>{
          if(arg.indexOf('-') > 0){
            let btn = arg.split('-')
            let permission =  this.authForm.find((item) => item.menusId === btn[0])
            permission.permissions === null ? permission.permissions = btn[1] : permission.permissions += ","+btn[1]
          }else{
            this.authForm.push({roleId: this.ruleForm.id, menusId: arg, permissions: null})
          }
        })
        roleAuthorize(this.authForm).then(resp => {
          this.$notify({
            title: "成功",
            message: "授权成功",
            type: "success",
          });
          this.authForm = []
          this.config.visible  = false
          this.refreshRoute()
        })
      },
      async loadMenus(params) {
        await getMenus(params).then(resp => {
          this.menusTree = resp.data
        })
      },
      async refreshRoute(){
        const accessRoutes = await this.$store.dispatch('permission/generateRoutes',  null)
        // dynamically add accessible routes
        this.$router.addRoutes(accessRoutes)
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      },
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },
      initData(row) {
        this.ruleForm = row
        this.loadMenus({
          roleId: row.id
        })
      }
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    }
  };
</script>
