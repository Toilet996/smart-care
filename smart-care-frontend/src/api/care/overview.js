import request from '@/utils/request'

export function getOverviewData() {
  return request({
    url: '/care/overview/data',
    method: 'get'
  })
}
