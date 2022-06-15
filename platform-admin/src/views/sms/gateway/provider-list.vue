<template>
  <div class="app-container">
    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:10px">
      <el-table-column label="供应商类型" align="left" prop="typeDesc"/>
      <el-table-column label="短信供应商" align="left" prop="providerName"/>
      <el-table-column label="更新时间" align="left" prop="lastUpdDt"/>
      <el-table-column label="操作" align="center">
            <template slot-scope="scope">
                <el-button
                    size="mini"
                    type="text"
                    icon="el-icon-edit"
                    @click="handleUpdate(scope.row)"
                >修改</el-button>
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
    <!-- 编辑 -->
    <el-dialog title="编辑" :visible.sync="dialogOpen" :close-on-click-modal="false" width="80%" append-to-body>
      <el-form ref="form" :model="form" label-width="120px">
        <el-form-item label="供应商类型:" prop="typeDesc">
          <el-input v-model="form.typeDesc" disabled/>
        </el-form-item>
        <el-form-item label="供应商:" prop="providerId" style="width:100%">
          <el-select
            v-model="form.providerId"
            placeholder="请选择供应商"
            style="width:100%"
          >
            <el-option
              v-for="item in providerList"
              :key="item.providerId"
              :label="item.providerName"
              :value="item.providerId"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-loading="submitLoading">确 定</el-button>
        <el-button @click="dialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    smsProviderList,
    smsProviderUpdate
  } from '@/api/sms/gateway'
  import {
    getSmsProviderConfigList
  } from '@/api/sms/smsProviderConfig'

  export default {
    name: 'ClientManage',
    created(){
        this.fetchList()
        getSmsProviderConfigList().then(res=>{
            this.providerList = res.data
        })
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
          "params": {}
        },
        dataList: [],
        providerList:[],
        dialogOpen:false,
        form:{
            "providerId": "",
            "smsClientId": 0,
            "smsType": 0,
            "telType": 0,
            "typeDesc": ""
        },
        submitLoading:false,
      };
    },
    methods: {
        fetchList(){
            this.loading = true;
            smsProviderList(this.params).then(res=>{
                this.dataList = res.data.items;
                this.total = res.data.total;
                this.loading = false;
            })
        },
        handleUpdate(item){
            let form = {
                providerId:item.providerId,
                smsClientId:item.smsClientId,
                smsType : item.smsType,
                telType : item.telType,
                typeDesc : item.typeDesc
            }
            this.form = Object.assign({},form)
            this.dialogOpen = true
            this.resetForm("form")
        },
        submitForm(){
          this.submitLoading = true
          smsProviderUpdate(this.form).then(res=>{
              this.submitLoading = false
              this.dialogOpen = false
              this.fetchList()
          })
        }
    }
  }
</script>

<style lang="scss" scoped>

</style>
