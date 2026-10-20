import request from '@/utils/request'

export function listMedicationPlan(query) {
  return request({
    url: '/care/medicationPlan/list',
    method: 'get',
    params: query
  })
}

export function getMedicationPlan(planId) {
  return request({
    url: '/care/medicationPlan/' + planId,
    method: 'get'
  })
}

export function addMedicationPlan(data) {
  return request({
    url: '/care/medicationPlan',
    method: 'post',
    data
  })
}

export function updateMedicationPlan(data) {
  return request({
    url: '/care/medicationPlan',
    method: 'put',
    data
  })
}

export function delMedicationPlan(planId) {
  return request({
    url: '/care/medicationPlan/' + planId,
    method: 'delete'
  })
}
