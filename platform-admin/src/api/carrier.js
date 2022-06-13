import request from '@/utils/platform-request'

// 查询运营商信息列表-分页
export function listCarrier(data) {
  return request({
    url: '/manage/carrier/page',
    method: 'post',
    data: data
  })
}

// 查询运营商信息列表-不分页
export function getCarrierList(data) {
  return request({
    url: '/manage/carrier/items',
    method: 'post',
    data: data
  })
}

// 查询App 开通的运营商列表
export function getAppCarrierList(appId) {
  return request({
    url: '/manage/carrier/' + appId,
    method: 'post'
  })
}
