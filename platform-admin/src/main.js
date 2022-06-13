import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import '@/icons' // icon
import '@/permission' // permission control
import VueAMap from 'vue-amap';

import { parseTime, resetForm, addDateRange, selectDictLabel, selectDictLabels, download, handleTree } from "@/utils/ruoyi";

// 全局方法挂载
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.download = download
Vue.prototype.handleTree = handleTree

import Pagination from "@/components/Pagination";
// 自定义表格工具扩展
import RightToolbar from "@/components/RightToolbar";

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */

// set ElementUI lang to EN
// Vue.use(ElementUI, { locale })
// 如果想要中文版 element-ui，按如下方式声明

// 全局组件挂载
Vue.component('Pagination', Pagination)
Vue.component('RightToolbar', RightToolbar)

Vue.use(ElementUI)
Vue.use(VueAMap);

Vue.config.productionTip = false

Vue.directive('permission', {
  bind: function (el, binding, vnode) {
    if(binding.value && binding.value.indexOf(binding.arg) < 0){
      el.style.display = 'none';
    }
  }
})
import 'butterfly-dag/dist/index.css';
import "./views/scene/components/scene_map/styles/index.less";


VueAMap.initAMapApiLoader({
  // 高德的key
  key: '804d71712fdd874c99a17a6fc2c09eca',
  // 插件集合
  plugin: ['AMap.Autocomplete', 'AMap.Geocoder','AMap.PlaceSearch',  'AMap.ToolBar'],
  // 高德 sdk 版本，默认为 1.4.4
  v: '1.4.4'
});

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
