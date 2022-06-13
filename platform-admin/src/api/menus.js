import request from '@/utils/platform-request'

export function page(pageParam) {
  return request({
    url: '/manage/menuGroups/page',
    method: 'POST',
    data: pageParam
  })
}

export function save(data) {
  return request({
    url: '/manage/menuGroups',
    method: 'POST',
    data: data
  })
}

export function detail(data) {
  return request({
    url: '/manage/menuGroups/menus',
    method: 'get',
    params: data
  })
}

export function deleteMenu(data) {
  return request({
    url: '/manage/menuGroups',
    method: 'DELETE',
    params: data
  })
}

export function submitAudit(data) {
  return request({
    url: '/manage/menuGroups/submitAudit',
    method: 'POST',
    data: data
  })
}

export function getAppCurrentMenu(data) {
  return request({
    url: '/manage/menuGroups/getCurrentUsed',
    method: 'get',
    params: data
  })
}




