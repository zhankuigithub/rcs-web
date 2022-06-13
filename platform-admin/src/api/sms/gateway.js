import request from '@/utils/sms-request'
 // 短信供应商管理
export function smsProviderList(data) {
    return request({
      url: '/smsWebSite/selectSmsProviderList',
      method: 'post',
      data: data
    })
}

//修改短信特服管理
export function smsProviderUpdate(data) {
  return request({
    url: '/smsWebSite/updateSmsProvider',
    method: 'put',
    data: data
  })
}

//供应商计费统计
export function smsProviderBillingStat(data) {
    return request({
      url: '/smsWebSite/selectSmsProviderBillingStat',
      method: 'post',
      data: data
    })
}
//短信特服管理
export function smsProviderConfigPage(data) {
    return request({
      url: '/smsProviderConfig/page',
      method: 'post',
      data: data
    })
}

//修改短信特服管理
export function smsProviderConfigUpdate(data) {
    return request({
      url: '/smsProviderConfig/updateNumConfig',
      method: 'put',
      data: data
    })
}
