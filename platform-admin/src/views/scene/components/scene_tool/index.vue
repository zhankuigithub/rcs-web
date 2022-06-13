<template>
    <div class="tool-container">
        <div class="tool-title-wrap">
            <span class="icon-box iconfont icon-fanhui" @click="onBackClick"></span>
            场景编辑
        </div>
        <div>
            <span class="icon-box iconfont icon-chexiaozuo" @click="onUnDo"></span>
            <span class="icon-box iconfont icon-chexiaoyou" @click="onRedo"></span>
            |
            <span class="icon-box iconfont icon-shanchu" @click="onDelete"></span>
            <span class="icon-box iconfont icon-baocun" @click="onSave"></span>
        </div>
    </div>
</template>
<script>
import "../scene_map/styles/iconfont.css";
export default {
    name:"SceneTool",
    created(){
        this.$nextTick(()=>{
            document.onkeydown=(e)=>{
                console.log(e)
                if(e.code == 'Backspace' || e.code == 'Delete'){
                   console.log('11111111111')
                }
            }
        })
    },
    methods:{
        onUnDo(){
            this.$emit('onundo')
        },
        onRedo(){
            this.$emit('onredo')
        },
        onDelete(){
            this.$emit('ondelete')
        },
        onSave(){
            this.$emit('onsave')
        },
        onBackClick(){
            this.$emit('onback')
        }

    }

}
</script>
<style lang="less" scoped>
.tool-container{
    text-align: center;
    box-sizing: border-box;
    padding: 8px 0;
    width: 100%;
    border: 1px solid #e9e9e9;
    height: 42px;
    z-index: 3;
    box-shadow: 0 8px 12px 0 rgb(0 52 107 / 4%);
    position: fixed;
    background: #fff;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
}
.icon-box {
    display: inline-block;
    height: 32px;
    line-height: 32px;
    width: 32px;
    border-radius: 16px;
    position: relative;
    left: 0px;
    .iconfont {
      color: #fff;
    }
  }
  .tool-title-wrap{
      position: absolute;
      left: 0;
  }
</style>