<template>
  <el-dialog title="消息模板" :visible.sync="dialogVisible" width="65%" :before-close="handleClose">
    <section class="mobile-items-wrapper">
      <MobileView :card="cardInfo" :style="mobileStyle" class="mobile" />
      <MobileView :card="cardInfo" :style="mobileStyle" class="mobile" />
      <MobileView :card="cardInfo" :style="mobileStyle" class="mobile" />
    </section>
  </el-dialog>
</template>

<script>
  import MobileView from '@/components/Mobile/index'
  export default {
    name: 'MsgTeamplate',
    components: {
      MobileView
    },
    data() {
      return {
        dialogVisible: false,
        cardInfo: {
          id: "",
          customerId: '',
          appId: '',
          name: "",
          title: "",
          description: "",
          items: [],
          materialId: '',
          material: {}
        },
        mobileStyle: {
          "width": "290px",
          "height": "600px",
          "margin": "auto"
        }
      };
    },
    methods: {
      handleClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {});
      }
    }
  };
</script>

<style lang="scss" scoped>
  .mobile-items-wrapper{
    display: flex;
  }

</style>
