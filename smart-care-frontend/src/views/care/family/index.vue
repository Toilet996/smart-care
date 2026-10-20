<template>
  <div class="app-container">
    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="家属姓名" prop="familyName">
        <el-input v-model="queryParams.familyName" placeholder="请输入家属姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="联系电话" prop="familyPhone">
        <el-input v-model="queryParams.familyPhone" placeholder="请输入联系电话" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="关联老人" prop="elderlyName">
        <el-input v-model="queryParams.elderlyName" placeholder="请输入老人姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="亲属关系" prop="relation">
        <el-select v-model="queryParams.relation" placeholder="请选择亲属关系" clearable>
          <el-option v-for="dict in biz_family_relation" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="紧急联系人" prop="isEmergency">
        <el-select v-model="queryParams.isEmergency" placeholder="请选择" clearable>
          <el-option v-for="dict in sys_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['care:family:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['care:family:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['care:family:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['care:family:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="familyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="关联ID" align="center" prop="id" width="88" />
      <el-table-column label="家属姓名" align="center" prop="familyName" min-width="120" />
      <el-table-column label="联系电话" align="center" prop="familyPhone" min-width="130" />
      <el-table-column label="家属用户ID" align="center" prop="userId" width="110" />
      <el-table-column label="默认关系" align="center" prop="relation" width="110">
        <template #default="{ row }">
          <dict-tag :options="biz_family_relation" :value="row.relation" />
        </template>
      </el-table-column>
      <el-table-column label="紧急联系人" align="center" width="110">
        <template #default="{ row }">
          <el-tag :type="isEmergency(row.isEmergency) ? 'danger' : 'info'">
            {{ emergencyLabel(row.isEmergency) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联老人数量" align="center" width="110">
        <template #default="{ row }">
          <el-tag type="info">{{ row.relatedElderlyCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联老人" align="center" min-width="180" show-overflow-tooltip>
        <template #default="{ row }">
          <el-button link type="primary" @click="goToElderly(row)">{{ row.elderlyNames || row.elderlyName || '-' }}</el-button>
        </template>
      </el-table-column>
      <el-table-column label="最近预警老人" align="center" min-width="140">
        <template #default="{ row }">
          <el-button v-if="row.latestAlertElderlyName" link type="danger" @click="goToLatestAlert(row)">
            {{ row.latestAlertElderlyName }}
          </el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="最近预警时间" align="center" min-width="170">
        <template #default="{ row }">
          <span>{{ parseTime(row.latestAlertTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="goToElderly(row)">老人</el-button>
          <el-button link type="danger" @click="goToLatestAlert(row)">预警</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['care:family:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(row)" v-hasPermi="['care:family:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="560px" append-to-body>
      <el-form ref="familyRef" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="关联老人" prop="elderlyId">
          <el-select v-model="form.elderlyId" placeholder="请选择老人" filterable style="width: 100%" @change="handleElderlySelect">
            <el-option v-for="item in elderlyOptions" :key="item.elderlyId" :label="`${item.elderlyName}（ID:${item.elderlyId}）`" :value="item.elderlyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="老人姓名" prop="elderlyName">
          <el-input v-model="form.elderlyName" placeholder="自动带出" readonly />
        </el-form-item>
        <el-form-item label="家属账号" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择家属账号" filterable style="width: 100%" @change="handleFamilyUserSelect">
            <el-option v-for="item in familyUserOptions" :key="item.userId" :label="`${item.nickName || item.userName}（ID:${item.userId}）`" :value="item.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="家属姓名" prop="familyName">
          <el-input v-model="form.familyName" placeholder="自动带出，可调整" />
        </el-form-item>
        <el-form-item label="家属电话" prop="familyPhone">
          <el-input v-model="form.familyPhone" placeholder="自动带出，可调整" />
        </el-form-item>
        <el-form-item label="亲属关系" prop="relation">
          <el-select v-model="form.relation" placeholder="请选择亲属关系" style="width: 100%">
            <el-option v-for="dict in biz_family_relation" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急联系人" prop="isEmergency">
          <el-select v-model="form.isEmergency" placeholder="请选择" style="width: 100%">
            <el-option v-for="dict in sys_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Family">
import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRouter } from 'vue-router'
import { addFamily, delFamily, getFamily, getFamilyOptions, listFamily, updateFamily } from '@/api/care/family'
import { parseTime } from '@/utils/ruoyi'

const { proxy } = getCurrentInstance()
const router = useRouter()
const { biz_family_relation, sys_yes_no } = proxy.useDict('biz_family_relation', 'sys_yes_no')

const familyList = ref([])
const familyUserOptions = ref([])
const elderlyOptions = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    elderlyName: null,
    userId: null,
    familyName: null,
    familyPhone: null,
    relation: null,
    isEmergency: null
  },
  rules: {
    elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }],
    elderlyName: [{ required: true, message: '老人姓名不能为空', trigger: 'blur' }],
    userId: [{ required: true, message: '请选择家属账号', trigger: 'change' }],
    familyName: [{ required: true, message: '家属姓名不能为空', trigger: 'blur' }],
    relation: [{ required: true, message: '请选择亲属关系', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listFamily(queryParams.value).then((response) => {
    familyList.value = response.rows
    total.value = response.total
  }).finally(() => {
    loading.value = false
  })
}

function loadOptions() {
  getFamilyOptions().then((response) => {
    familyUserOptions.value = response.data?.familyUsers || []
    elderlyOptions.value = response.data?.elderlyList || []
  })
}

function reset() {
  form.value = {
    id: null,
    elderlyId: null,
    elderlyName: null,
    userId: null,
    familyName: null,
    familyPhone: null,
    relation: null,
    isEmergency: 'N'
  }
  proxy.resetForm('familyRef')
}

function cancel() {
  open.value = false
  reset()
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id)
  single.value = selection.length !== 1
  multiple.value = selection.length === 0
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增家属关联'
}

function handleUpdate(row) {
  const id = row?.id || ids.value[0]
  if (!id) {
    proxy.$modal.msgWarning('请先选择要修改的数据')
    return
  }
  reset()
  getFamily(id).then((response) => {
    form.value = { ...response.data }
    open.value = true
    title.value = '修改家属关联'
  })
}

function handleFamilyUserSelect(userId) {
  const target = familyUserOptions.value.find((item) => item.userId === userId)
  if (!target) return
  form.value.familyName = target.nickName || target.userName || ''
  form.value.familyPhone = target.phonenumber || ''
}

function handleElderlySelect(elderlyId) {
  const target = elderlyOptions.value.find((item) => item.elderlyId === elderlyId)
  if (!target) return
  form.value.elderlyName = target.elderlyName || ''
}

function submitForm() {
  proxy.$refs.familyRef.validate((valid) => {
    if (!valid) return
    const request = form.value.id ? updateFamily(form.value) : addFamily(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.id ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const targetIds = row?.id ? [row.id] : ids.value
  if (!targetIds.length) {
    proxy.$modal.msgWarning('请先选择要删除的数据')
    return
  }
  proxy.$modal.confirm(`是否确认删除家属关联编号“${targetIds.join(',')}”的数据项？`)
    .then(() => delFamily(targetIds.join(',')))
    .then(() => {
      proxy.$modal.msgSuccess('删除成功')
      getList()
    })
    .catch(() => {})
}

function handleExport() {
  proxy.download('care/family/export', { ...queryParams.value }, `family_${new Date().getTime()}.xlsx`)
}

function isEmergency(value) {
  return value === 'Y' || value === '1'
}

function emergencyLabel(value) {
  return isEmergency(value) ? '是' : '否'
}

function goToElderly(row) {
  router.push({
    path: '/smartcare/elderly',
    query: { elderlyName: row.elderlyNames || row.elderlyName }
  })
}

function goToLatestAlert(row) {
  if (!row.latestAlertElderlyName) {
    proxy.$modal.msgWarning('该家属关联老人暂无预警记录')
    return
  }
  router.push({
    path: '/smartcare/alertRecord',
    query: { elderlyName: row.latestAlertElderlyName }
  })
}

reset()
loadOptions()
getList()
</script>
