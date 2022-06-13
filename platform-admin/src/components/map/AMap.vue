<template>
  <div class="amap-page-container">
    <el-amap-search-box class="search-box" :search-option="searchOption" :on-search-result="onSearchResult" />
    <div class="toolbar">
      [ 经度：{{ amap.lng }} 维度：{{ amap.lat }} 位置：{{ amap.address }} ]
    </div>
    <el-amap vid="amapDemo2" :center="mapCenter" :zoom="zoom" class="amap-demo" :events="events" :plugin="plugin">
      <el-amap-marker v-for="(marker,index) in markers" :key="index" :position="marker.position" />
    </el-amap>
  </div>
</template>

<script>
  export default {
    name: 'AmapPage',
    props: {
      makerPosition: {
        type: Array,
        default: () => {
          return []
        }
      }
    },
    data: function() {
      const self = this
      return {
        zoom: 12,
        center: [0, 0],
        amap: {
          address: '',
          lng: 0,
          lat: 0,
        },
        markers: [{
          position: this.makerPosition
        }],
        searchOption: {
          city: '上海',
          citylimit: false
        },
        mapCenter: [121.59996, 31.197646],
        plugin: [{
          pName: 'ToolBar',
          events: {
            init(instance) {}
          }
        }],
        events: {
          click(e) {
            const {
              lng,
              lat
            } = e.lnglat
            self.amap.lng = lng
            self.amap.lat = lat
            self.markers[0].position = [lng, lat]

            // 这里通过高德 SDK 完成。
            var geocoder = new AMap.Geocoder({
              radius: 1000,
              extensions: 'all'
            })
            geocoder.getAddress([lng, lat], function(status, result) {
              if (status === 'complete' && result.info === 'OK') {
                if (result && result.regeocode) {
                  self.amap.address = result.regeocode.formattedAddress
                  self.$emit("position", self.amap)
                  self.$nextTick()
                }
              }
            })
          }
        },
      }
    },
    created() {
      this.mapCenter = this.makerPosition
      this.amap.lng = this.makerPosition[0]
      this.amap.lat = this.makerPosition[1]
    },
    methods: {
      onSearchResult(pois) {
        console.log(pois);
        let latSum = 0
        let lngSum = 0
        if (pois.length > 0) {
          pois.forEach(poi => {
            const {
              lng,
              lat
            } = poi
            lngSum += lng
            latSum += lat
          })
          const center = {
            lng: lngSum / pois.length,
            lat: latSum / pois.length
          }
          this.mapCenter = [center.lng, center.lat]
        }
      },
      select() {
        this.$emit('update:makerPosition', this.markers[0].position)
      }
    }

  }
</script>

<style lang="scss" scoped>
  .amap-demo {
    height: 300px;
  }

  .search-box {
    z-index: 999;
    position: absolute;
    right: 5px;
    top: 45px;
    box-shadow: 0 0 40px rgba(0, 0, 0, .2);
    border-radius: 6px;
    overflow: hidden;
  }

  .amap-page-container {
    position: relative;
  }
</style>
