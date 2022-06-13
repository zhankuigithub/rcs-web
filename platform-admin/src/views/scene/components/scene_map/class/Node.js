import data from '@/views/scene/mockData';
import { Node } from 'butterfly-dag';
import $ from 'jquery';

class BaseNode extends Node {
  constructor(opts) {
    super(opts);
    this.options = opts;
  }
  draw = opts => {
    let classed = ["pipeline-base-node"];
    if (this.options.bgColor) {
      classed.push(`path-bg-${this.options.bgColor}`);
    }
    if (this.options.className) {
      classed.push(`path-bg-${this.options.className}`);
    }
    let container = $(`<div class="${classed.join(" ")}"></div>`)
      .attr("id", opts.id)
      .css("top", opts.top + "px")
      .css("left", opts.left + "px");

    this._createTypeIcon(container);
    this._createText(container);
    this._createIcon(container[0]);
    console.log(container)
    return container[0];
  };
  updateLabel(label) {
    this.label = label;
    this.options.label = label;
    this.labelDom.remove();
    this.label = label;
    this.labelDom = $('<span class="text-box"></span>')
      .text(label).appendTo(this.dom);
  }
  _createIcon(dom = this.dom) {
    let logoContainer = $('<div class="logo-toolbar"></div>');
    const boxContainer = $('<div class="box-container"></div>');
    boxContainer.append(
      $(
        '<div class="box"  name="close"><i class="iconfont icon-guanbi"></i></div>'
      )
    );
    boxContainer.append(
      $(
        '<div class="box" name="next"><i class="iconfont icon-webicon310"></i></div>'
      )
    );
    let that = this;
    boxContainer.on("click", ".box", function(event) {
      event.stopPropagation();
      that.clickTools($(this).attr("name"));
      that.toggleFocus();
    });
    logoContainer.append(boxContainer);
    $(dom).append(logoContainer);
  }
  _createTypeIcon(dom = this.dom) {
    const iconContainer = $(
      `<span class="icon-box ${this.options.iconClass}"></span>`
    )[0];
    const icon = $(`<i class="iconfont ${this.options.iconType}"></i>`)[0];

    iconContainer.append(icon);
    $(dom).append(iconContainer);
  }

  _createText(dom = this.dom) {
    this.labelDom = $('<span class="text-box"></span>')
      .text(this.options.label).appendTo(dom);
  }
  toggleClass(clasName) {
    $(this.dom).toggleClass(clasName);
  }
  toggleFocus() {
    
  }
  focus = () => {
    $(this.dom)
      .find(".logo-toolbar")
      .addClass("show");
  };
  unFocus = () => {
    $(this.dom)
      .find(".logo-toolbar")
      .removeClass("show");
  };
  clickTools(name) {
    this.emit("events", {
      type: "node:click:tools",
      node: this,
      name
    });
  }
}

export default BaseNode;
