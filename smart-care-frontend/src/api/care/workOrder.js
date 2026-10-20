import request from '@/utils/request'

// 查询处置工单列表
export function listWorkOrder(query) {
  return request({
    url: '/care/workOrder/list',
    method: 'get',
    params: query
  })
}

// 查询处置工单详细
export function getWorkOrder(orderId) {
  return request({
    url: '/care/workOrder/' + orderId,
    method: 'get'
  })
}

// 新增处置工单
export function addWorkOrder(data) {
  return request({
    url: '/care/workOrder',
    method: 'post',
    data: data
  })
}

// 修改处置工单
export function updateWorkOrder(data) {
  return request({
    url: '/care/workOrder',
    method: 'put',
    data: data
  })
}

// 删除处置工单
export function delWorkOrder(orderId) {
  return request({
    url: '/care/workOrder/' + orderId,
    method: 'delete'
  })
}
