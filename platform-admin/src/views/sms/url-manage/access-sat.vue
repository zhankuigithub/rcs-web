<template>
  <div class="app-container">
      <el-form :model="params" ref="queryForm" :inline="true">
        <el-form-item label="日期范围：" prop="beginDt">
            <el-date-picker
            size="mini"
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            >
            </el-date-picker>
        </el-form-item>
        <el-form-item label="统计方式：" prop="statType">
            <el-select
                v-model="statType"
                placeholder="请选择统计方式"
                clearable
                size="mini"
            >
                <el-option
                key="1"
                :label="'按客户端统计'"
                value="1"
                ></el-option>
                <el-option
                key="2"
                :label="'按模板端统计'"
                value="2"
                ></el-option>
            </el-select>
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
        <el-table-column label="日期" align="left" prop="startDt" width="160"/>
        <el-table-column label="客户端" align="left" prop="client"/>
        <el-table-column v-if="params.params.statType + '' == '2'" label="模板" align="center" prop="template"/>
        <el-table-column label="短链接数量" align="center" prop="urlCnt"/>
        <el-table-column label="PV" align="center" prop="pv"/>
        <el-table-column label="UV" align="center" prop="uv" />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="params.currentPage"
      :limit.sync="params.pageSize"
      @pagination="fetchList"
    />
  </div>
</template>

<script>
  import {
    shortUrlAccessStat
  } from '@/api/sms/url-manage'

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
        dateRange:[],
        params: {
            "currentPage": 0,
            "pageSize": 20,
            "params": {
                "beginDt": "",
                "endDt": ""
            }
        },
        statType:'1',
        dataList: [],
      };
    },
    methods: {
        showTemplate(obj){
            console.log(obj)
            return false
        },
        fetchList(){
            this.loading = true;
            this.params.params.beginDt = this.dateFormate(this.dateRange[0]);
            this.params.params.endDt = this.dateFormate(this.dateRange[1]);
            this.params.params.statType = this.statType
            shortUrlAccessStat(this.params).then(res=>{
                this.dataList = res.data.items;
                this.total = res.data.total;
                this.loading = false;
            })
        },
        resetQueryParams() {
            this.statType = '1'
            let date = new Date();
            this.dateRange = [
                new Date(date.getFullYear(), date.getMonth(), date.getDate() - 7, 0, 0),
                new Date(),
            ];
            this.params = {
                "currentPage": 1,
                "pageSize": 20,
                "params": {
                    "beginDt": "",
                    "endDt": ""
                }
            };
            this.fetchList();
        },
        dateFormate(date){
            const dt =new Date(date)
            const y = dt.getFullYear()
            const m = (dt.getMonth() + 1 + '').padStart(2,0)
            const d = (dt.getDate() + '').padStart(2,0)
            const hh = (dt.getHours() + '').padStart(2,0)
            const mm = (dt.getMinutes() + '').padStart(2,0)
            const ss = (dt.getSeconds() + '').padStart(2,0)
            return `${y}-${m}-${d}`
        },
    }
  }
</script>

<style lang="scss" scoped>

</style>
