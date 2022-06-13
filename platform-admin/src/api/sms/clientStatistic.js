import request from '@/utils/sms-request'


// 时报
export function hoursPage(data) {
  return request({
    url: '/clientStatistic/hoursPage',
    method: 'post',
    data: data
  })
}

// 日报
export function daysPage(data) {
  return request({
    url: '/clientStatistic/daysPage',
    method: 'post',
    data: data
  })
}

// 月报
export function monthsPage(data) {
  return request({
    url: '/clientStatistic/monthsPage',
    method: 'post',
    data: data
  })
}

// 客户端发送查询
export function sendLogPage(data) {
  return request({
    url: '/clientStatistic/sendLogPage',
    method: 'post',
    data: data
  })
}

// 短信发送量统计
export function smsSendPage(data) {
  return request({
    url: '/clientStatistic/smsSendPage',
    method: 'post',
    data: data
  })
}
