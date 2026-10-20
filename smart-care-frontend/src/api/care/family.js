import request from '@/utils/request'

// 查询家属关联列表
export function listFamily(query) {
  return request({
    url: '/care/family/list',
    method: 'get',
    params: query
  })
}

// 查询家属关联详细
export function getFamily(id) {
  return request({
    url: '/care/family/' + id,
    method: 'get'
  })
}

export function getFamilyOptions() {
  return request({
    url: '/care/family/options',
    method: 'get'
  })
}

// 新增家属关联
export function addFamily(data) {
  return request({
    url: '/care/family',
    method: 'post',
    data: data
  })
}

// 修改家属关联
export function updateFamily(data) {
  return request({
    url: '/care/family',
    method: 'put',
    data: data
  })
}

// 删除家属关联
export function delFamily(id) {
  return request({
    url: '/care/family/' + id,
    method: 'delete'
  })
}
