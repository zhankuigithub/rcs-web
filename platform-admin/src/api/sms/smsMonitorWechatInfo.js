import request from '@/utils/sms-request'

export function smsMonitorWechatInfoPage(data) {
  return request({
    url: '/smsMonitorWechatInfo/page',
    method: 'post',
    data: data
  })
}

// 新增
export function addSmsMonitorWechatInfo(data) {
  return request({
    url: '/smsMonitorWechatInfo/add',
    method: 'post',
    data: data
  })
}

// 修改
export function editSmsMonitorWechatInfo(data) {
  return request({
    url: '/smsMonitorWechatInfo/update',
    method: 'put',
    data: data
  })
}

// 删除
export function deleteSmsMonitorWechatInfo(data) {
  return request({
    url: '/smsMonitorWechatInfo/delete/' + data,
    method: 'delete'
  })
}
