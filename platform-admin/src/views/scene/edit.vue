<template>
  <div class="container">
    <SceneTool class="scene-tool"  @onback="onBack"  @onsave="onSave" @onundo="onUnDo" @onredo="onReDo" @ondelete="onDelete"> </SceneTool>
    <div class="main">
        <NodesTool></NodesTool>
        <SceneMap ref="sceneMap" class="butterfly-main"  :data="sceneData" @onmapevent="onMapEvent" v-on:keyup.enter="onFocus"></SceneMap>
        <PropertyInspector 
          class="inspector" 
          :inspectorType="inspectorType" 
          :node="node" 
          :edge="edge"
          :sceneName="scene.name"
          :appId="scene.appId"
          @onInputSceneName="onInputSceneName"
          @onSelectApp="onSelectApp"
          @onSelectMsgTemplate="onSelectMsgTemplate"
          @onSelectScene="onSelectScene"
        />
    </div>
  </div>
</template>

<script>
import SceneTool from './components/scene_tool';
import NodesTool from './components/nodes_tool';
import SceneMap from './components/scene_map';
import PropertyInspector from './components/property';
import Node from "./components/scene_map/class/Node";
import Edge from "./components/scene_map/class/Edge";
import {sceneInfo,sceneModify} from "@/api/scene";
export default {
  name: 'App',
  components: {
    SceneTool,
    NodesTool,
    SceneMap,
    PropertyInspector
  },
  data() {
    return {
      inspectorType:'canvas',
      node:null,
      edge:null,
      scene:{},
      sceneData:{},

    };
  },
  created() {
    const sceneId = this.$route.query.id
    if(sceneId){
      sceneInfo(sceneId).then(response=>{
        if(response && response.data){
          console.log('__________!1')
          this.scene = response.data
          this.parseSceneData()
        }
      })
    }
  },

  methods:{
    
    parseSceneData(){
      var payload = JSON.parse(this.scene.payload)
      if(payload.nodes){
        payload.nodes.forEach(element => {
          element.Class = Node
        });
      }
      this.sceneData = payload
    },
    onBack(){
      this.$router.back(-1);
    },
    onSave(){
      var nodes = this.$refs.sceneMap.instance.nodes
      var nodesList = []
      nodes.forEach(element => {
        let edge = {
          "id": element.id,
          "label": element.options.label,
          "top": element.top,
          "left": element.left,
          "iconClass":element.options.iconClass,
          "iconType": element.options.iconType,
          "type": element.options.type,
          "endpoints":element.options.endpoints,
          "draggable": true,
          "data":element.options.data
        }
        nodesList.push(edge)
      });
      this.scene.nodes = nodesList
      var edges = this.$refs.sceneMap.instance.edges.map(item => item.options)
      var list = []
      edges.forEach(element => {
        let edge = {
          "source":"bottom",
          "target":"top",
          "sourceNode": element.sourceNode,
          "targetNode": element.targetNode,
          "type": "endpoint",
          "arrow": true,
          "arrowPosition": 0.8
        }
        list.push(edge)
      });
      this.scene.edges = list

      console.log(this.scene)
      sceneModify(this.scene).then(response=>{
        console.log(response)
        if(response.code == 200){
          this.$message({
              message:
                "场景配置成功！",
              type: "success",
            });
            this.$router.back(-1);
        }
      })
    },
    onUnDo(){
      this.$refs.sceneMap.instance.undo()
    },
    onReDo(){
      this.$refs.sceneMap.instance.redo()
    },
    onDelete(){
      if(this.node){
         console.log(this.node)
        this.$refs.sceneMap.instance.removeNode(this.node.id)
      }
      if(this.edge){
        this.$refs.sceneMap.instance.removeEdge(this.edge.id)
      }
    },


    onMapEvent(data){
      if(data.type == 'canvas:click'){
        this.inspectorType = 'canvas'
        this.node = null
        this.edge = null
      }
     
      if(data.type == 'node:click'){
        this.node = data.node
        this.inspectorType = 'node'
      }
      if(data.type == 'link:click'){
        this.edge = data.edge
        this.inspectorType = 'edge'
        this.node = null
      }
    },

    onInputSceneName(sceneName){
      this.scene.name = sceneName
      
    },
    onSelectApp(app){
      this.scene.appId = app.id   
    },
    onSelectMsgTemplate(msg){
      if(this.node){
        this.node.updateLabel(msg.name)
        this.node.options.data = msg.id
      }
      var data = this.$refs.sceneMap.instance.getNeighborNodesAndEdgesByLevel({node:this.node,type:'out',level:1})
      if(data){
        var nodes = data.nodes.filter(item => item.id !== this.node.id)
        this.$refs.sceneMap.instance.removeNodes(nodes.map(item => item.id))
      }
    
      var buttons = []
      if(msg.suggestions && msg.suggestions.length > 0){
        buttons = buttons.concat(msg.suggestions);
      }
      if(msg.type == 2 || msg.type == 3){
        console.log(msg.cards)
        msg.cards.forEach(card=>{
          if(card.suggestions && card.suggestions.length > 0){
            buttons = buttons.concat(card.suggestions)
          }
        })
      }
      console.log(buttons)
      for (var i=0; i < buttons.length; i++){ 
        var suggestion = buttons[i]
        if(suggestion.type != 1){
          continue
        }
        let node = {
          label: suggestion.displayText,
          bgColor: "",
          iconClass: "simple-yellow",
          iconType: "icon-anniu",
          type: 2,
          data: suggestion.id
        }
        node.top = this.node.top + 100
        node.left = this.node.left + i*120
        var msgNode = this.$refs.sceneMap.addNode(node)
        console.log(node)
        var edge = {
          source: 'bottom',
          target: 'top',
          sourceNode: this.node.id,
          targetNode: msgNode.id,
          type: 'endpoint',
          arrow: true,
          arrowPosition: 0.8,
          Class: Edge
        }
        this.$refs.sceneMap.instance.addEdge(edge)
      }
    },
    onSelectScene(scene){
      if(this.node){
        this.node.updateLabel(msg.name)
      }
    }


  }
}
</script>
<style scoped>
.container{
    width:100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
}
.main{
    width:100%;
    height: calc(100% - 42px);
    display: flex;
    flex-direction: row;
    margin-top: 42px;
}
</style>