import request from '@/utils/request'

// 查询设备台账列表
export function listDevice(query) {
  return request({
    url: '/iot/device/list',
    method: 'get',
    params: query
  })
}

// 查询设备台账详细
export function getDevice(deviceId) {
  return request({
    url: '/iot/device/' + deviceId,
    method: 'get'
  })
}

// 新增设备台账
export function addDevice(data) {
  return request({
    url: '/iot/device',
    method: 'post',
    data: data
  })
}

// 修改设备台账
export function updateDevice(data) {
  return request({
    url: '/iot/device',
    method: 'put',
    data: data
  })
}

// 删除设备台账
export function delDevice(deviceId) {
  return request({
    url: '/iot/device/' + deviceId,
    method: 'delete'
  })
}
