<template>
    <div class="simulator-wrap">
        <img class="simulator-wrap-bg" src="@/assets/images/simulator.png" alt="">
        <div class="buttom-menu-wrap flex">
			<div class="config-menu-wrap flex-row align-items-center">
                <div class="switch-buttom-wrap flex align-items-center"><i class="el-icon-menu"></i></div>
				<div class="menu-wrap flex-row align-items-center" gutter="16">
					<div class="menu-item flex-row align-items-center" v-for="(item,index) in menu.entries" :key="index" @click="onMenuClick(item,index)">
						<span class="menu-item-text">{{ item.menu !== undefined ? item.menu.displayText:item.reply.displayText }}</span>
					</div>
				</div>
			</div>
		</div>
    </div>
</template>
<script>
export default {
    name: "SimulatorMenu",
    props:{
        menu: {
            type: Object,
            default() {
                return {};
            },
        },
        isEditMode:{
            type:Boolean,
            default(){
                return false;
            }
        }
    },
    methods:{
        onMenuClick(){
            
        }
    }
}
</script>

<style lang="scss" scoped>
.simulator-wrap{
    width: 375px;
    height: 781px;
    position: relative;
}
.simulator-wrap-bg{
    width: 100%;
    height: 100%;
    position: absolute;
}
.buttom-menu-wrap{
    width: calc(100% - 36px);
    min-height: 50px;
    position: absolute;
    bottom: 34px;
    left: 18px;
	.switch-buttom-wrap{
		height: 54px;
		z-index: 101;
		flex: 0 0 50px;
		display: flex;
		align-self: flex-end;
		justify-content: center;
	}
	.config-menu-wrap{
		height: 54px;
		display: flex;
		flex: 1;
        .switch-buttom-wrap-img{
			width: 28px;
			height: 28px;
		}
		.menu-wrap{
			height: 100%;
			display: flex;
			align-items: center;
			flex: 1;
			.menu-item{
				height: 100%;
				color: #25292e;
				justify-content: center;
				position: relative;
				flex: 1;
				:before {
				    content: "";
					position: absolute;
				    left: 0;
					top: 50%;
				    width: 1px;
				    height: 40%;
				    -webkit-transform: translateY(-50%) scaleX(.5);
				    transform: translateY(-50%) scaleX(.5);
				    background: #dddee0;
				}
			}
		}
	}
}
.flex {
	display: flex;
}
.flex-row {
    display: flex;
    flex-direction: row;
}
.flex-column{
    display: flex;
	flex-direction: column;
}
.align-items-center{
	display: flex;
	align-items: center;
}
.align-items-start {
	align-items: flex-start;
}
.justify-content-start {
    justify-content: flex-start;
}

.justify-content-end {
    justify-content: flex-end;
}
.justify-content-center{
	justify-content: center;
}
</style>
















