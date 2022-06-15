<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="2">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-permission:ADD="$route.meta.permission"
        >新增客户端
        </el-button>
      </el-col>
    </el-row>

    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:10px">
      <el-table-column label="客户端ID" align="center" prop="clientId" width="100"/>
      <el-table-column label="客户端名称" align="center" prop="clientDesc"/>
      <el-table-column label="绑定域名" align="center" prop="bindUrl"/>
      <el-table-column label="签名" align="center" prop="appendSign"/>
      <el-table-column label="客户端状态" align="center" prop="clientStatus" width="100">
        <template slot-scope="scope">
          {{scope.row.clientStatus == 1?'可用':'不可用'}}
        </template>
      </el-table-column>
      <el-table-column label="报警阈值" align="center" prop="warningNum" width="100"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width"  width="100">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleEdit(scope.row)"
            v-permission:EDIT="$route.meta.permission"
          >修改
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="params.currentPage"
      :limit.sync="params.pageSize"
      @pagination="fetchList"
    />
    <!-- 新增客户端 -->
    <el-dialog :title="op_type == 'add'?'新增客户端':'修改客户端'" :visible.sync="addClientDialogOpen" :close-on-click-modal="false" width="80%" append-to-body>
      <el-form ref="form" :model="clientForm" :rules="rules" label-width="120px">
        <el-form-item label="客户端编号" prop="clientId" v-if="op_type == 'edit'">
          <el-input v-model="clientForm.clientId" disabled/>
        </el-form-item>
        <el-form-item label="客户端名称" prop="clientDesc">
          <el-input v-model="clientForm.clientDesc" placeholder="请输入 例：IPTV正式"/>
        </el-form-item>
        <el-form-item label="模板签名" prop="appendSign">
          <el-input v-model="clientForm.appendSign" placeholder="请输入 例：朗玛信息"/>
        </el-form-item>
        <el-form-item label="绑定域名" prop="bindUrl">
          <el-input v-model="clientForm.bindUrl" placeholder="请输入 例：www.langma.cn"/>
        </el-form-item>
        <el-form-item label="客户端状态" prop="clientStatus">
          <el-radio-group v-model="clientForm.clientStatus">
            <el-radio :label="1">允许发送</el-radio>
            <el-radio :label="0">不允许发送</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="报警阈值" prop="warningNum" v-if="op_type == 'edit'">
          <el-input v-model="clientForm.warningNum" placeholder="请输入 例：www.langma.cn"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-loading="submitLoading">确 定</el-button>
        <el-button @click="addClientDialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    getClientPageList,
    addClient,
    updateClient
  } from '@/api/sms/template'

  export default {
    name: 'ClientManage',
    created(){
      this.fetchList()
    },
    data() {
      return {
        // 遮罩层
        loading: false,
        // 总条数
        total: 0,
        params: {
          "currentPage": 1,
          "pageSize": 20,
          "params": {
            "clientDesc": "",
            "clientId": ''
          }
        },
        dataList: [],
        addClientDialogOpen:false,
        clientForm:{
          "appendSign": "",
          "bindUrl": "",
          "clientDesc": "",
          "clientId": '',
          "clientStatus": 1,
          "warningNum": 0
        },
        op_type:'add',//'add' 'edit'
        submitLoading:false,
        rules: {
          appendSign: [
            {required: true, message: "模板签名不能为空", trigger: "blur"}
          ],
          bindUrl: [
            {required: true, message: "绑定域名不能为空", trigger: "blur"}
          ],
          clientDesc: [
            {required: true, message: "客户端名称不能为空", trigger: "blur"}
          ],
        },
      };
    },
    methods: {
      fetchList(){
        this.loading = true;
        getClientPageList(this.params).then(res=>{
          this.dataList = res.data.items;
          this.total = res.data.total;
          this.loading = false;
        })
      },
      handleAdd(){
        this.clientForm = {
          "appendSign": "",
          "bindUrl": "",
          "clientDesc": "",
          "clientId": '',
          "clientStatus": 1,
          "warningNum": 0
        }
        this.op_type = 'add'
        this.addClientDialogOpen = true
        this.resetForm("form")
      },
      handleEdit(item){
        this.clientForm = {
          "appendSign": item.appendSign,
          "bindUrl": item.bindUrl,
          "clientDesc": item.clientDesc,
          "clientId": item.clientId,
          "clientStatus": item.clientStatus,
          "warningNum": item.warningNum
        }
        this.op_type = 'edit'
        this.addClientDialogOpen = true
        this.resetForm("form")
      },
      submitForm(){
        this.$refs["form"].validate(valid => {
          if (valid) {
            if(this.op_type == 'add'){
              this.submitLoading = true
              addClient(this.clientForm).then(res=>{
                this.submitLoading = false
                this.addClientDialogOpen = false
                this.fetchList()
              })
            }else{
              this.submitLoading = true
              updateClient(this.clientForm).then(res=>{
                this.submitLoading = false
                this.addClientDialogOpen = false
                this.fetchList()
              })
            }
          }
        });

      }
    }
  }
</script>

<style lang="scss" scoped>

</style>
