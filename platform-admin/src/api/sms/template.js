import request from '@/utils/sms-request'

  // 客户端列表
  export function getClientList(data) {
    return request({
      url: '/clientManage/list',
      method: 'get',
      data: data
    })
  }
  // 分页获取客户端列表
  export function getClientPageList(data) {
    return request({
      url: '/clientManage/page',
      method: 'post',
      data: data
    })
  }
  // 新增客户端
  export function addClient(data) {
    return request({
      url: '/clientManage/add',
      method: 'post',
      data: data
    })
  }
  // 修改客户端
  export function updateClient(data) {
    return request({
      url: '/clientManage/update',
      method: 'put',
      data: data
    })
  }
  //获取短信模板列表
  export function getSmsTemplateList(data) {
    return request({
      url: '/smsTemplateConfigInfo/page',
      method: 'post',
      data: data
    })
  }
  //获取短信模板详情
  export function getSmsTemplateDetail(id) {
    return request({
      url: '/smsTemplateConfigInfo/item/' + id,
      method: 'get'
    })
  }
  
  //新增短信模板
  export function addSmsTemplate(data) {
    return request({
      url: '/smsTemplateConfigInfo/add',
      method: 'post',
      data: data
    })
  }
  //修改短信模板
  export function updateSmsTemplate(data) {
    return request({
      url: '/smsTemplateConfigInfo/update',
      method: 'put',
      data: data
    })
  }
  //删除短信模板
  export function deleteSmsTemplate(data) {
    return request({
      url: '/smsTemplateConfigInfo/delete/' + data,
      method: 'delete',
      data: data
    })
  }

  //获取短信模板
  export function getSmsClientTemplateList(data) {
    return request({
      url: '/smsTemplateConfigInfo/getAllSmsTemplate/' + data,
      method: 'get',
      data: data
    })
  }
  //获取短信模板下所有参数
  export function getTemplateAllKeys(data) {
    return request({
      url: '/smsTemplateConfigInfo/getAllKeys/' + data,
      method: 'get',
      data: data
    })
  }

  //获取短信模板调用日志
  export function getSmsTemplateLogList(data) {
    return request({
      url: '/smsTemplateConfigInfo/sendLogPage',
      method: 'post',
      data: data
    })
  }
  //发送短信
  export function sendSms(data) {
    return request({
      url: '/smsTemplateConfigInfo/sendSms',
      method: 'post',
      data: data
    })
  }




  
