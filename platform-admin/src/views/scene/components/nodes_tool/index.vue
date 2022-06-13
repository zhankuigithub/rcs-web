<template>
    <div class="nodes-tool-container">
        <div class="node-item-wrap" v-for="(item,index) in nodes" :key="index">
            <div class="node-item" draggable 
             @dragstart="onDragstart($event,item)" @dragend="onDragend($event,item)">
                <span class="icon-box" :class="item.iconClass"><i class="iconfont" :class="item.iconType"></i></span>
                <span class="text-box">{{item.label}}</span>
            </div>
        </div>
    </div>
</template>
<script>
import data from "./nodes.js";
export default {
    name:'NodesTool',
    data(){
        return{
            nodes:data.nodes,
            eventData:null,
        }
    },

    created() {
        console.log(this.nodes)
      
    },
    methods:{
        onDragstart(event,item){
            event.dataTransfer.setData("text",JSON.stringify(item));
        },
        onDragend(event) {
            
        }
    }

}
</script>
<style scoped>
.nodes-tool-container{
    border-left: 1px solid #e6e9ed;
    width: 200px;
    height: 100%;
}
.node-item-wrap{
    width: 100%;
    display: flex;
    padding: 10px 0 10px 10px;
    flex-direction: column;
    justify-content: center;
}
.node-item{
    height: 32px;
    min-width: 80px;
    width: fit-content;
    line-height: 32px;
    text-align: center;
    background: #fff;
    border-radius: 51px;
}
.icon-box {
    display: inline-block;
    height: 32px;
    line-height: 32px;
    width: 32px;
    border-radius: 16px;
    position: relative;
    left: 0px;
}
.text-box {
    display: inline-block;
    height: 32px;
    min-width: 50px;
    padding: 0 10px;
    line-height: 32px;
    font-size: 14px;
    -moz-osx-font-smoothing: grayscale;
    -webkit-font-smoothing: antialiased;
}
</style>