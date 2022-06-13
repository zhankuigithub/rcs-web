import request from '@/utils/platform-request'

// 查询关键字回复列表
export function listKeywordReply(data) {
  return request({
    url: '/manage/keywordReplyConfig/page',
    method: 'post',
    data: data
  })
}

// 查询关键字回复详细
export function getKeywordReply(id) {
  return request({
    url: '/manage/keywordReplyConfig/' + id,
    method: 'get'
  })
}

// 新增关键字回复
export function addKeywordReply(data) {
  return request({
    url: '/manage/keywordReplyConfig/add',
    method: 'post',
    data: data
  })
}

// 修改关键字回复
export function updateKeywordReply(data) {
  return request({
    url: '/manage/keywordReplyConfig/update',
    method: 'put',
    data: data
  })
}

// 删除关键字回复
export function delKeywordReply(id) {
  return request({
    url: '/manage/keywordReplyConfig/' + id,
    method: 'delete'
  })
}
