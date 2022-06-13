import Node from "../scene_map/class/Node.js";
import endpoints from "../scene_map/utils/endpoints";

//type 0 开始 1 消息模板 2 建议回复 3场景 4关键词 5逻辑判断 6答案
export default {
  nodes: [{
        label: "开始",
        bgColor: "simple-blue",
        iconClass: "green",
        iconType: "icon-kaishi",
        type: 0,
        data:''
    },
    {
        label: "消息模板",
        bgColor: "simple-blue",
        iconClass: "purple",
        iconType: "icon-xiaoximoban",
        type: 1,
        data:''
    },
    {
        label: "建议回复",
        bgColor: "",
        iconClass: "simple-yellow",
        iconType: "icon-anniu",
        type: 2,
        data:''
    },{
        label: "场景",
        bgColor: "",
        iconClass: "deep-blue",
        iconType: "icon-changjing",
        type: 3
    },{
        label: "关键词",
        bgColor: "",
        iconClass: "green",
        iconType: "icon-guanjianci",
        type: 4,
        data:''
    },{
        label: "逻辑判断",
        bgColor: "",
        iconClass: "deep-green",
        iconType: "icon-hongfatiaojian",
        type: 5,
        data:''
    },{
        label: "条件",
        bgColor: "",
        iconClass: "simple-green",
        iconType: "icon-daan",
        type: 6,
        data:''
    }
  ]
};