import request from '@/utils/sms-request'

// 运营商列表
export function getSmsProviderConfigList() {
  return request({
    url: '/smsProviderConfig/list',
    method: 'get'
  })
}
