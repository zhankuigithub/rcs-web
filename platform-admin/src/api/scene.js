import request from '@/utils/platform-request'

export function sceneModify(pageParam) {
  return request({
    url: '/manage/scene/modify',
    method: 'POST',
    data: pageParam
  })
}
export function sceneInfo(id) {
    return request({
      url: '/manage/scene/info/' + id,
      method: 'GET'
    })
  }
export function deleteScene(id) {
  return request({
    url: '/manage/scene/delete/' + id,
    method: 'DELETE'
  })
}
export function sceneList(data) {
  return request({
    url: '/manage/scene/page',
    method: 'POST',
    data:data
  })
}

export function setSceneEnable(data) {
  return request({
    url: '/manage/scene/openOrClose',
    method: 'POST',
    data:data
  })
}
