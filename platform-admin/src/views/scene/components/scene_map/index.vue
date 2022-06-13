<template>
  <div id="canvas" class="butterfly-layout" @dragenter="onDragEnter" @dragover="onDragOver" @drop="onDrop" ></div>
</template>
<script>
import init from "./utils/render";
import { SceneNode } from "./utils/index";
import "./styles/index.less";
import "./styles/iconfont.css";
export default {
  name: "SceneMap",
  props: {
    data: {
      type: Object
    }
  },
  watch: {
    data(val) {
      this.instance.redraw(val)
    },
  },
  data() {
    return {
      instance: {}
    };
  },
  mounted() {
    this.instance = this.init(this.data);
  },
  methods: {
    init,
    onDragEnter(event){
        event.preventDefault();
    },
    onDragOver(event){
        event.preventDefault();
    },
    onDrop(event){
        event.preventDefault();
        var node =  JSON.parse(event.dataTransfer.getData('text'));
        if(node.type == 2){
          return
        }
        var location = this.instance.terminal2canvas([event.clientY, event.clientX]);
        var offset = this.instance.getOffset()
        node.top = location[0] + offset[0]
        node.left = location[1] + offset[1]
        this.addNode(node)
    },
    addNode(nodeDict){
      var node = new SceneNode(nodeDict)
      this.instance.addNode(node);
      return node
    }
  }
};
</script>