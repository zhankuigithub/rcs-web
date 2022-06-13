import request from '@/utils/admin-request'


export function roleAuthorize(data) {
  return request({
    url: '/manage/rolePermission/authorize',
    method: 'POST',
    data
  })
}

export function getMenus() {
  return request({
    url: '/manage/rolePermission/menus',
    method: 'get'
  })
}
