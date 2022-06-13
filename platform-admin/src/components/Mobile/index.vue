<template>
  <div class="simulator" :style="style">
    
  </div>
</template>

<script>
  export default {
    name: "MobileView",
    props: {
      teamplate: {
        type: Array,
        default: () => {
          return [{
            id: "",
            customerId: '',
            appId: '',
            name: "",
            title: "",
            description: "",
            items: [],
            materialId: '',
            material: {}
          }]
        }
      },
      styles: {
        type: Object,
        default: () => {
          return {
            "width": "350px",
            "height": "715px",
            "margin": "auto"
          }
        }
      }
    },
    data() {
      return {
        style: this.styles
      };
    },
    methods: {

    }
  };
</script>

<style lang="scss" scoped>
  .simulator {
    background: url("../../assets/images/iphone_backImg.png") no-repeat;
    background-size: 100% auto;
    position: relative;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;

    .rescources-card-wrap {
      width: 260px;
      position: relative;
      top: 128px;
      left: 40px;
      border-radius: 10px;
      padding: 10px;
      background: #f3f3f3;
      margin-bottom: 5px;
      display: flex;
      overflow-x: auto;
      .media-wrap {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 140px;
        .grid-item-img {
          width: 100%;
          max-height: 100%;
          object-fit: cover;
        }
      }
      .card-cont-wrap {
        width: 100%;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        // height: 100%;
        width: 100%;
        .card-title {
          line-height: 24px;
          font-weight: 600;
          font-size: 17px;
          color: #25292e;
          -webkit-line-clamp: 2;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        .card-content {
          overflow: hidden;
          font-size: 14px;
          color: #333333;
          text-overflow: ellipsis;
          -webkit-line-clamp: 4;
          display: -webkit-box;
          -webkit-box-orient: vertical;
        }
      }
    }
    .card-btn {
      display: flex;
      font-size: 16px;
      width: 100%;
      color: #25292e;
      margin-top: 4px;
      padding: 0;
      background: #fff;
      line-height: 38px;
      height: 38px;
      border-radius: 6px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      text-decoration: none;
      text-align: center;
      justify-content: center;
      margin-top: 10px;
    }
    .selectButton{
      background: rgb(36, 62, 206);
      color: white;
    }

    .right-info-bottm {
      display: flex;
      flex-flow: row-reverse;
    }

  }
</style>
