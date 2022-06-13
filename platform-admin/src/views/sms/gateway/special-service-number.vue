<template>
  <div class="app-container">
      <el-form :model="params" ref="queryForm" :inline="true">
        <el-form-item label="日期范围：" prop="beginDt">
           
        </el-form-item>
        <el-form-item label="统计方式：" prop="statType">
            
        </el-form-item>
        <el-form-item>
            <el-button
            type="primary"
            icon="el-icon-search"
            size="mini"
            @click="fetchList"
            >搜索
            </el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQueryParams"
            >重置</el-button
            >
        </el-form-item>
    </el-form>
    <el-table :row-style="{height:'0px'}"
              :cell-style="{padding:'0px'}" v-loading="loading" :data="dataList" stripe border style="margin-top:10px">
        <el-table-column label="供应商" align="left" prop="providerName"/>
        <el-table-column label="移动特服号" align="center" prop="chinaMobile"/>
        <el-table-column label="电信特服号" align="center" prop="chinaTelecomm"/>
        <el-table-column label="联通特服号" align="center" prop="chinaUnicomm"/>
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
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="供应商:" prop="provider">
          <el-input v-model="form.provider" disabled/>
        </el-form-item>
        <el-form-item label="移动特服号:" prop="chinaMobile">
          <el-input v-model="form.chinaMobile" placeholder="请输入"/>
        </el-form-item>
        <el-form-item label="电信特服号:" prop="chinaTelecomm">
          <el-input v-model="form.chinaTelecomm" placeholder="请输入"/>
        </el-form-item>
        <el-form-item label="联通特服号:" prop="chinaUnicomm">
          <el-input v-model="form.chinaUnicomm" placeholder="请输入"/>
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
    smsProviderConfigPage,
    smsProviderConfigUpdate
  } from '@/api/sms/gateway'

  export default {
    name: 'ClientManage',
    created(){
      this.resetQueryParams()
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
        dialogOpen:false,
        form:{
            "chinaMobile": "",
            "chinaTelecomm": "",
            "chinaUnicomm": "",
            "provider":"",
            "providerId": 0
        },
        submitLoading:false,
        rules: {
          chinaMobile: [
            {required: true, message: "请输入移动特服号", trigger: "blur"}
          ],
          chinaTelecomm: [
            {required: true, message: "请输入电信特服号", trigger: "blur"}
          ],
          chinaUnicomm: [
            {required: true, message: "请输入联通特服号", trigger: "blur"}
          ],
        },
      };
    },
    methods: {
        showTemplate(obj){
            console.log(obj)
            return false
        },
        fetchList(){
            this.loading = true;
            smsProviderConfigPage(this.params).then(res=>{
                this.dataList = res.data.items;
                this.total = res.data.total;
                this.loading = false;
            })
        },
        resetQueryParams() {
            this.params = {
                "currentPage": 1,
                "pageSize": 20,
                "params": {}
            };
            this.fetchList();
        },
        handleUpdate(item){
            this.form = {}
            this.form.chinaMobile = item.chinaMobile
            this.form.chinaTelecomm = item.chinaTelecomm
            this.form.chinaUnicomm = item.chinaUnicomm
            this.form.provider = item.providerName
            this.form.providerId = item.providerId
            this.dialogOpen = true
            this.resetForm("form")
        },
        submitForm(){
            this.$refs["form"].validate(valid => {
                if (valid) {
                    this.submitLoading = true
                    smsProviderConfigUpdate(this.form).then(res=>{
                        this.submitLoading = false
                        this.dialogOpen = false
                        this.fetchList()
                    })
                }
            });
        }

    }
  }
</script>

<style lang="scss" scoped>

</style>
