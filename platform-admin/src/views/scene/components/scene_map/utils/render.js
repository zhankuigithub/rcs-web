import pipelineEvents from "./events";
import Canvas from "../class/DragCanvas.js";
export default function(defaultData) {
  let root = document.getElementById("canvas");
  let canvas = new Canvas({
    root: root,
    disLinkable: true, // 可删除连线
    linkable: true, // 可连线
    draggable: true, // 可拖动
    zoomable: true, // 可放大
    moveable: true, // 可平移
    autoFixCanvas: {
      //节点拖动或连线拖动到画布边缘时，画布自动延展
      enable: true,
      autoMovePadding: [20, 20, 20, 20] //触发自动延展的画布内边距
    },
    layout: {
      type: "drageLayout",
      options: {
        rankdir: "LR",
        align: "DR",
        nodesep: 6,
        ranksep: 31,
        controlPoints: false
      }
    },
    theme: {
      edge: {
        type: 'AdvancedBezier',
        arrow: true,
        arrowPosition:0.8
      }
    }
  });
  canvas.setMinimap(true,{})
  canvas.draw(defaultData, () => {
    canvas.setGirdMode(true, {
      isAdsorb: false,         // 是否自动吸附,默认关闭
      theme: {
        shapeType: 'line',     // 展示的类型，支持line & circle
        gap: 10,               // 网格间隙
        backgroud: '#252525',     // 网格背景颜色
        lineWidth:0.1,
        circleColor: 'rgba(0, 0, 0, 0.3)'    // 圆点颜色
      }
    });
  });
  canvas.on("events", data => {
    let { type } = data;
    this.$emit('onmapevent',data)
    let f = pipelineEvents[type];
    if (f) {
      f.bind(this)(data);
    }
  });
  return canvas;
}
