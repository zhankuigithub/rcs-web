import request from '@/utils/sms-request'


// 按客户端查
export function pageByClient(data) {
  return request({
    url: '/clientStatisticDaysDetail/pageByClient',
    method: 'post',
    data: data
  })
}
// 按短信模板查
export function pageByTemplate(data) {
    return request({
      url: '/clientStatisticDaysDetail/pageByTemplate',
      method: 'post',
      data: data
    })
  }
// 按电话号码查
export function pageByPhone(data) {
    return request({
      url: '/clientStatisticDaysDetail/pageByPhone',
      method: 'post',
      data: data
    })
  }