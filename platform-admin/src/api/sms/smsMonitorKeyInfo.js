import request from '@/utils/sms-request'


export function smsMonitorKeyInfoList() {
  return request({
    url: '/smsMonitorKeyInfo/list',
    method: 'get'
  })
}

export function smsMonitorKeyInfoPage(data) {
  return request({
    url: '/smsMonitorKeyInfo/page',
    method: 'post',
    data: data
  })
}

// 新增
export function addSmsMonitorKeyInfo(data) {
  return request({
    url: '/smsMonitorKeyInfo/add',
    method: 'post',
    data: data
  })
}
// 修改
export function editSmsMonitorKeyInfo(data) {
  return request({
    url: '/smsMonitorKeyInfo/update',
    method: 'put',
    data: data
  })
}
// 删除
export function deleteSmsMonitorKeyInfo(data) {
  return request({
    url: '/smsMonitorKeyInfo/delete',
    method: 'delete',
    data: data
  })
}
