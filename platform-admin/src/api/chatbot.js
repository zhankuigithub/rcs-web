import request from '@/utils/platform-request'

// 查询机器人信息列表-分页
export function listChatbot(data) {
  return request({
    url: '/manage/chatbot/page',
    method: 'post',
    data: data
  })
}

// 查询机器人信息详细
export function getChatbot(id) {
  return request({
    url: '/manage/chatbot/' + id,
    method: 'get'
  })
}

// 新增机器人信息
export function addChatbot(data) {
  return request({
    url: '/manage/chatbot/addChatbot',
    method: 'post',
    data: data
  })
}

// 变更机器人信息
export function editChatbot(data) {
  return request({
    url: '/manage/chatbot/editChatbot',
    method: 'post',
    data: data
  })
}

///manage/chatbot
export function updateChatbot(data) {
  return request({
    url: '/manage/chatbot',
    method: 'put',
    data: data
  })
}

// 修改机器人信息-移动
export function updateChatbotCm(data) {
  return request({
    url: '/manage/chatbot/cm',
    method: 'put',
    data: data
  })
}

// 修改机器人信息-电信
export function updateChatbotCt(data) {
  return request({
    url: '/manage/chatbot/ct',
    method: 'put',
    data: data
  })
}

//上下线机器人
export function chatbotOnlieStatusUpdate(data){
  return request({
    url:'/manage/chatbot/online',
    method:'put',
    params:data
  })
}

//删除机器人
export function chatbotDelete(data){
  return request({
    url:'/manage/chatbot',
    method:'delete',
    query:data
  })
}
