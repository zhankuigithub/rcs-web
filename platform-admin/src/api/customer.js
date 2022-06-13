import request from '@/utils/platform-request'

// 查询客户信息列表-分页
export function listCustomer(data) {
  return request({
    url: '/manage/customer/page',
    method: 'post',
    data: data
  })
}
//查询客户信息列表-不分页
export function getCustomerList(query){
  return request({
    url:'/manage/customer',
    method:'get',
    params:query
  })
}

// 查询客户基本信息
export function getCustomer(id) {
  return request({
    url: '/manage/customer/' + id,
    method: 'get'
  })
}

// 新增客户信息
export function addCustomer(data) {
  return request({
    url: '/manage/customer',
    method: 'post',
    data: data
  })
}

// 修改客户信息
export function updateCustomer(data) {
  return request({
    url: '/manage/customer',
    method: 'put',
    data: data
  })
}

// 删除客户信息
export function delCustomer(id) {
  return request({
    url: '/system/customer/' + id,
    method: 'delete'
  })
}

export function submitAudit(data, carrierId){
  return request({
    url: '/manage/customer/submit/'+ carrierId,
    method: 'post',
    data: data
  })
}
