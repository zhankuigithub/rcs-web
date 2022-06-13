import request from '@/utils/platform-request'

// 查询消息模板列表-分页
export function listMessageTemplate(data) {
  return request({
    url: '/manage/messageTemplate/page',
    method: 'post',
    data: data
  })
}

export function queryMessageTemplateList(query) {
  return request({
    url: '/manage/messageTemplate/list',
    method: 'get',
    params: query
  })
}

// 查询消息模板详细
export function getMessageTemplate(id) {
  return request({
    url: '/manage/messageTemplate/' + id,
    method: 'get'
  })
}

// 新增消息模板
export function addMessageTemplate(data) {
  return request({
    url: '/manage/messageTemplate/add',
    method: 'post',
    data: data
  })
}

// 修改消息模板
export function updateMessageTemplate(data) {
  return request({
    url: '/manage/messageTemplate/update',
    method: 'put',
    data: data
  })
}

// 删除消息模板
export function delMessageTemplate(id) {
  return request({
    url: '/manage/messageTemplate/' + id,
    method: 'delete'
  })
}

// 消息模板列表
export function itemsMessageTemplate(params) {
  return request({
    url: '/manage/messageTemplate/list',
    method: 'get',
    params
  })
}

// 添加动态消息模板
export function addDynamicMessageTemplate(data) {
  return request({
    url: '/manage/dynamicTemplate/add',
    method: 'post',
    data:data
  })
}
// 删除动态消息模板
export function deleteDynamicMessageTemplate(id) {
  return request({
    url: '/manage/dynamicTemplate/' + id,
    method: 'delete'
  })
}
// 编辑动态消息模板
export function updateDynamicMessageTemplate(data) {
  return request({
    url: '/manage/dynamicTemplate/edit',
    method: 'put',
    data:data
  })
}
// 查询消息模板详情
export function getDynamicMessageTemplate(id) {
  return request({
    url: '/manage/dynamicTemplate/item/' + id,
    method: 'get'
  })
}
// 查询动态消息模板列表-分页
export function pageDynamicMessageTemplate(data) {
  return request({
    url: '/manage/dynamicTemplate/page',
    method: 'post',
    data: data
  })
}
// 查询动态消息模板列表-不分页
export function listDynamicMessageTemplate(appId) {
  return request({
    url: '/manage/dynamicTemplate/list/' + appId,
    method: 'get'
  })
}
// 查询动态消息模板动态参数列表
export function dynamicTemplateGetAllKeys(id) {
  return request({
    url: '/manage/dynamicTemplate/getAllKeys/' + id,
    method: 'get',
  })
}

// 推送动态消息
export function dynamicTemplateSendMessage(data) {
  return request({
    url: '/manage/dynamicTemplate/sendMessage',
    method: 'post',
    data: data
  })
}
