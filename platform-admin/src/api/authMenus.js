import request from '@/utils/admin-request'

export function page(pageParam) {
  return request({
    url: '/manage/menus/defaultPage',
    method: 'POST',
    data: pageParam
  })
}

export function save(data) {
  return request({
    url: '/manage/menus/add',
    method: 'POST',
    data
  })
}

export function edit(data) {
  return request({
    url: '/manage/menus/edit',
    method: 'PUT',
    data
  })
}

export function remove(id) {
  return request({
    url: '/manage/menus/'+id,
    method: 'DELETE'
  })
}

export function getMenus(params) {
  return request({
    url: '/manage/menus/items',
    method: 'get',
    params
  })
}

export function getMenuList(params) {
  return request({
    url: '/manage/menus/list',
    method: 'get',
    params
  })
}

export function getTree(params) {
  return request({
    url: '/manage/menus/tree/'+params,
    method: 'get'
  })
}
