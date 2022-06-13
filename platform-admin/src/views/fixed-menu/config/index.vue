<template>
    <div class="app-container  clearfix">
        <simulator-menu></simulator-menu>
    </div>
</template>

<script>
import SimulatorMenu from '../component/simulator-menu'
import { save,detail,submitAudit } from "@/api/menus";
export default {
    components:{
        SimulatorMenu
    },
    data(){
        return{
            customerId:'',
            appId:'',
        }
    },
    created(){
        this.customerId = this.$route.query.customerId;
        this.appId = this.$route.query.appId;
        if (this.customerId != undefined && this.customerId != "" && this.appId != undefined && this.appId != "" ) {
            
        }
    },
}
</script>

<style lang="scss" scoped>

</style>