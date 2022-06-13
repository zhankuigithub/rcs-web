import Node from "../class/Node";
import endpoints from "./endpoints";
import Endpoint from "../class/Endpoint";
import randomstring from "randomstring";
export function randomString(length) {
  return randomstring.generate({
    length: length,
    charset: "alphabetic",
    capitalization: "lowercase"
  });
}

export class SceneNode {
  constructor(obj) {
    const timeInteval = new Date().getTime();
    this.id = randomString(5) + timeInteval;
    this.Class = Node;
    this.label = obj.label;
    this.top = obj.top;
    this.left = obj.left;
    this.iconClass = obj.iconClass;
    this.iconType = obj.iconType;
    this.type = obj.type
    this.data = obj.data
    // 0 开始 1 消息模板 2 建议回复 3场景 4关键词 5逻辑判断 6答案
    if(obj.type == 0){
      this.endpoints = [{
        id: `bottom`,
        color: obj.iconClass,
        orientation: [0, 1],
        pos: [0.5, 0],
        Class: Endpoint
      }]
    }else{
      this.endpoints = [
        {
          id: `top`,
          color: obj.iconClass,
          orientation: [0, -1],
          pos: [0.5, 0],
          Class: Endpoint
        },
        {
          id: `bottom`,
          color: obj.iconClass,
          orientation: [0, 1],
          pos: [0.5, 0],
          Class: Endpoint
        },
      
    ]
    }
  }
}




