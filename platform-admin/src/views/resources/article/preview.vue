<template>
    <div class="container">
        <div class="content_wrap">
            <h3 class="title">{{detail.name}}</h3>
            <div class="suptitle_wrap">
                <span class="app_name">{{detail.appName}}</span>
                <span class="update_date">{{detail.insertDt}}</span>
            </div>
            <div class="content" v-html="detail.content"></div>
        </div>
    </div>
</template>
<script>
import {materialDetail} from '@/api/resources'
export default {
    data() {
        return {
            preview_id:'',
            detail:{},
        
        };
    },
    created() {
        let query = this.$route.query;
        this.preview_id  = query.preview_id
        materialDetail(this.preview_id).then((res) => {
          this.detail = res.data;
        });
    }
}
</script>
<style scoped>
.container{
    display: flex;
    justify-content: center;
}
.content_wrap{
    padding: 16px;
    width: 100%;
}
.title{
    font-family: PingFangSC-Medium;
    font-size: 24px;
    color: #242424;
    font-weight: 700;
    margin-bottom: 12px;
    line-height: 33px;
}
.suptitle_wrap{
    margin-bottom: 11px;
    font-size: 13px;
}
.app_name{
    color: #224a8f;
    cursor: pointer;
}
.update_date{
    color: rgba(26,21,21,.26);
    margin-left: 24px;
}
@media screen and (min-width: 750px){
    .content_wrap{
        width: 750px;
    } 
}



</style>