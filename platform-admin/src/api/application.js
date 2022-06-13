import request from '@/utils/platform-request'

// 查询应用信息列表-分页
export function listApplication(data) {
  return request({
    url: '/manage/application/page',
    method: 'post',
    data: data
  })
}

//查询应用信息列表-不分页
export function getApplicationList(query) {
  return request({
    url: '/manage/application',
    method: 'get',
    params: query
  })
}

// 查询应用信息详细
export function getApplication(id) {
  return request({
    url: '/manage/application/info?appId=' + id,
    method: 'get'
  })
}

// 新增应用信息
export function addApplication(data) {
  return request({
    url: '/manage/application',
    method: 'post',
    data: data
  })
}

// 修改应用信息
export function updateApplication(data) {
  return request({
    url: '/manage/application',
    method: 'put',
    data: data
  })
}

// 审核应用信息
export function auditApplication(data) {
  return request({
    url: '/manage/application/audit',
    method: 'put',
    data: data
  })
}

// 删除应用信息
export function delApplication(id) {
  return request({
    url: '/manage/application/' + id,
    method: 'delete'
  })
}
