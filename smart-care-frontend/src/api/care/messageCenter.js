import request from '@/utils/request'

export function listMessage(query) {
  return request({
    url: '/care/message/list',
    method: 'get',
    params: query
  })
}

export function getMessage(messageId) {
  return request({
    url: '/care/message/' + messageId,
    method: 'get'
  })
}

export function getMessageStats() {
  return request({
    url: '/care/message/stats',
    method: 'get'
  })
}

export function markMessageRead(messageId) {
  return request({
    url: '/care/message/' + messageId + '/read',
    method: 'post'
  })
}

export function markAllMessageRead() {
  return request({
    url: '/care/message/readAll',
    method: 'post'
  })
}
