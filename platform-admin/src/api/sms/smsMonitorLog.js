import request from '@/utils/sms-request'

export function smsMonitorLogPage(data) {
  return request({
    url: '/smsMonitorLog/page',
    method: 'post',
    data: data
  })
}
