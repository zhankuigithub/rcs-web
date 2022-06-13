import request from '@/utils/platform-request'

// 查询机器人开发者信息列表
export function listChatbotDeveloper(data) {
  return request({
    url: '/manage/chatbotDeveloper/page',
    method: 'post',
    data: data
  })
}

// 查询机器人开发者信息详细
export function getChatbotDeveloper(id) {
  return request({
    url: '/manage/chatbotDeveloper/' + id,
    method: 'get'
  })
}

// 新增机器人开发者信息
export function addChatbotDeveloper(data, carrierId) {
  return request({
    url: '/manage/chatbotDeveloper/' + carrierId,
    method: 'post',
    data: data
  })
}

// 修改机器人开发者信息
export function updateChatbotDeveloper(data) {
  return request({
    url: '/manage/chatbotDeveloper',
    method: 'put',
    data: data
  })
}
