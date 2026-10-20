import request from '@/utils/request'

export function getFamilyPortalData(params) {
  return request({
    url: '/care/familyPortal/data',
    method: 'get',
    params
  })
}

export function listFamilyPortalContacts(params) {
  return request({
    url: '/care/familyPortal/contacts',
    method: 'get',
    params
  })
}

export function addFamilyPortalContact(data, params) {
  return request({
    url: '/care/familyPortal/contacts',
    method: 'post',
    params,
    data
  })
}

export function updateFamilyPortalContact(data, params) {
  return request({
    url: '/care/familyPortal/contacts',
    method: 'put',
    params,
    data
  })
}

export function deleteFamilyPortalContact(ids, params) {
  return request({
    url: `/care/familyPortal/contacts/${ids}`,
    method: 'delete',
    params
  })
}

export function triggerFamilyPortalSos(data, params) {
  return request({
    url: '/care/familyPortal/sos',
    method: 'post',
    params,
    data
  })
}
