import request from '@/utils/sms-request'
 // 短网址申请日志
 export function shortUrlApplyLog(data) {
    return request({
      url: '/webSiteManage/selectShortUrlApplyForLog',
      method: 'post',
      data: data
    })
}

// 短网址使用日志
export function shortUrlUsedLog(data) {
    return request({
      url: '/webSiteManage/selectShortUrlUsedLog',
      method: 'post',
      data: data
    })
}

// 短网址访问统计
export function shortUrlAccessStat(data) {
    return request({
      url: '/webSiteManage/selectShortUrlAccessStat',
      method: 'post',
      data: data
    })
}

// 短网址访问日志
export function shortUrlAccessLog(data) {
    return request({
      url: '/webSiteManage/selectShortUrlAccessLog',
      method: 'post',
      data: data
    })
}