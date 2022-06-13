/**
 * 客户等级
 */
export const customerGrades = () => {
  return [{
    id: 0,
    label: '金牌级'
  },
    {
      id: 1,
      label: '银牌级'
    },
    {
      id: 2,
      label: '铜牌级'
    }, {
      id: 3,
      label: '标准级'
    }
  ]
}

/**
 * 行业类型
 */
export const industryType = () => {
  return [{
    type: 1,
    label: 'IT 服务'
  },
    {
      type: 2,
      label: '制造业'
    },
    {
      type: 3,
      label: '批发/零售'
    },
    {
      type: 4,
      label: '生活服务'
    },
    {
      type: 5,
      label: '文化/体育/娱乐业'
    },
    {
      type: 6,
      label: '建筑/房地产'
    },
    {
      type: 7,
      label: '教育'
    },
    {
      type: 8,
      label: '运输/物流/仓储'
    },
    {
      type: 9,
      label: '医疗'
    },
    {
      type: 10,
      label: '政府'
    },
    {
      type: 11,
      label: '金融'
    },
    {
      type: 12,
      label: '能源/采矿'
    },
    {
      type: 13,
      label: '农、林、牧、渔业'
    },
    {
      type: 14,
      label: '电力、热力、燃气及水生产和供应业'
    },
    {
      type: 15,
      label: '计算机软件/硬件/信息服务'
    },
    {
      type: 16,
      label: '互联网和相关服务'
    },
    {
      type: 17,
      label: '机械/电子'
    },
    {
      type: 18,
      label: '服装/纺织'
    },
    {
      type: 19,
      label: '汽车'
    },
    {
      type: 20,
      label: '金属制品'
    },
    {
      type: 21,
      label: '食品/饮料'
    },
    {
      type: 22,
      label: '家具/家纺'
    },
    {
      type: 23,
      label: '重工制造'
    },
    {
      type: 24,
      label: '家电/数码'
    },
    {
      type: 25,
      label: '橡胶/塑料'
    },
    {
      type: 26,
      label: '日用品/化妆品'
    },
    {
      type: 27,
      label: '其他行业'
    }
  ]
}

/**
 * 客户审核状态字典
 */
export const dictCustomerStatus = () => {
  return [{
    dictValue: '0',
    dictLabel: '待审核'
  }, {
    dictValue: '1',
    dictLabel: '已审核通过'
  }, {
    dictValue: '2',
    dictLabel: '审核未通过'
  }
  ]
}

/**
 * 应用审核状态字典
 */
export const dictAppStatus = () => {
  return [{
    dictValue: '0',
    dictLabel: '待审核'
  }, {
    dictValue: '1',
    dictLabel: '已审核通过'
  }, {
    dictValue: '2',
    dictLabel: '审核未通过'
  }
  ]
}

/**
 * 机器人启用状态
 */
export const dictChatbotStatus = () => {
  return [{
    dictValue: '0',
    dictLabel: '未启用'
  }, {
    dictValue: '1',
    dictLabel: '已启用'
  }
  ]
}

/**
 * 机器人审核状态
 */
export const dictChatbotAuditStatus = () => {
  return [{
    dictValue: '-2',
    dictLabel: '未通过'
  }, {
    dictValue: '-1',
    dictLabel: '审核中'
  },
    {
      dictValue: '0',
      dictLabel: '已下线'
    },
    {
      dictValue: '1',
      dictLabel: '已上线'
    }
  ]
}

/**
 *消息类型字典
 */
export const dictMessageType = () => {
  return [
    {
      dictValue: '1',
      dictLabel: '纯文本'
    },
    {
      dictValue: '2',
      dictLabel: '单卡片'
    },
    {
      dictValue: '3',
      dictLabel: '多卡片'
    },
    {
      dictValue: '4',
      dictLabel: '地理信息'
    },
    {
      dictValue: '5',
      dictLabel: '图片'
    },
    {
      dictValue: '6',
      dictLabel: '音频'
    },
    {
      dictValue: '7',
      dictLabel: '视频'
    }
  ]
}

/*
*关键字回复匹配类型
*/
export const dictReplayType = () => {
  return [
    {
      dictValue: '1',
      dictLabel: '完全匹配'
    },
    {
      dictValue: '2',
      dictLabel: '模糊匹配（包含）'
    },
    {
      dictValue: '3',
      dictLabel: '正则匹配'
    },
    {
      dictValue: '4',
      dictLabel: '兜底消息'
    }
  ]
}

/**
 * 悬浮菜单类型
 */
export const dictSuggestionType = () => {
  return [
    {
      dictValue: '1',
      dictLabel: '建议回复'
    },
    {
      dictValue: '2',
      dictLabel: '跳转网页'
    },
    {
      dictValue: '3',
      dictLabel: '拨打电话'
    },
    {
      dictValue: '4',
      dictLabel: '位置信息'
    },
    {
      dictValue: '5',
      dictLabel: '活动计划'
    }
  ]
}

export const dictMessageTemplateMaterialAuditStatus = () => {
  return [
    {
      dictValue: '0',
      dictLabel: '未审核'
    },
    {
      dictValue: '1',
      dictLabel: '已审核'
    },
    {
      dictValue: '2',
      dictLabel: '未通过'
    },
    {
      dictValue: '3',
      dictLabel: '未提交'
    }
  ]
}

export const operateItems = [{
  value: 'ADD',
  label: '新增'
}, {
  value: 'EDIT',
  label: '修改'
}, {
  value: 'RM',
  label: '删除'
}, {
  value: 'VIEW',
  label: '查看'
}, {
  value: 'AUTH',
  label: '授权'
}, {
  value: 'IMPORT',
  label: '导入'
}, {
  value: 'EXPORT',
  label: '导出'
}]

export const dictMessageBackType = () => {
  return [
    {
      dictValue: '0',
      dictLabel: '不回落'
    },
    {
      dictValue: '1',
      dictLabel: 'Chatbot H5'
    },
    {
      dictValue: '2',
      dictLabel: '文本短信'
    },
    {
      dictValue: '3',
      dictLabel: 'UP 1.0'
    }
  ]
}

export const dictEventStatusType = () => {
  return [
    {
      dictValue: '1',
      dictLabel: '待发送'
    },
    {
      dictValue: '2',
      dictLabel: '已取消'
    },
    {
      dictValue: '3',
      dictLabel: '暂停'
    },
    {
      dictValue: '4',
      dictLabel: '已完成'
    }
  ]
}
// 1 已发送 2发送失败 3消息已送达 4已阅读 5已转短 6已撤回 7机器人未开通 8机器人未审核 9素材未审核 10用户未登录 404 未知错误通用码
export const dictMessageStatusType = () => {
  return [
    {
      dictValue: '0',
      dictLabel: '未发送'
    },
    {
      dictValue: '1',
      dictLabel: '已发送'
    },
    {
      dictValue: '2',
      dictLabel: '发送失败'
    },
    {
      dictValue: '3',
      dictLabel: '消息已送达'
    },
    {
      dictValue: '4',
      dictLabel: '已阅读'
    },
    {
      dictValue: '5',
      dictLabel: '已转短'
    },
    {
      dictValue: '6',
      dictLabel: '已撤回'
    },
    {
      dictValue: '7',
      dictLabel: '机器人未开通'
    },
    {
      dictValue: '8',
      dictLabel: '机器人未审核'
    },
    {
      dictValue: '9',
      dictLabel: '素材未审核'
    },
    {
      dictValue: '10',
      dictLabel: '用户未登录'
    },
    {
      dictValue: '404',
      dictLabel: '未知错误'
    }
  ]
}

export const dictSexType = () => {
  return [
    {
      dictValue: '0',
      dictLabel: '未定义'
    },
    {
      dictValue: '1',
      dictLabel: '男'
    },
    {
      dictValue: '2',
      dictLabel: '女'
    }
  ]
}
