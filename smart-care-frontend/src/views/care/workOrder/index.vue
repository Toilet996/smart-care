<template>
  <div class="app-container">
    <el-form
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      v-show="showSearch"
      label-width="88px"
    >
      <el-form-item label="关联预警ID" prop="alertId">
        <el-input
          v-model="queryParams.alertId"
          placeholder="请输入关联预警ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入工单ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="老人ID" prop="elderlyId">
        <el-input
          v-model="queryParams.elderlyId"
          placeholder="请输入老人ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="老人姓名" prop="elderlyName">
        <el-input
          v-model="queryParams.elderlyName"
          placeholder="请输入老人姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择工单状态" clearable>
          <el-option
            v-for="dict in biz_order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['care:workOrder:add']">
          新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate()"
          v-hasPermi="['care:workOrder:edit']"
        >
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete()"
          v-hasPermi="['care:workOrder:remove']"
        >
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['care:workOrder:export']"
        >
          导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="workOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="工单ID" align="center" prop="orderId" width="90" />
      <el-table-column label="关联预警ID" align="center" prop="alertId" width="110">
        <template #default="scope">
          <el-button v-if="scope.row.alertId" link type="danger" @click="handleViewAlertRecord(scope.row)">
            {{ scope.row.alertId }}
          </el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="老人ID" align="center" prop="elderlyId" width="90" />
      <el-table-column label="老人姓名" align="center" prop="elderlyName" min-width="120" />
      <el-table-column label="处置记录" align="center" prop="processRecord" min-width="220" show-overflow-tooltip />
      <el-table-column label="工单状态" align="center" prop="status" width="110">
        <template #default="scope">
          <dict-tag :options="biz_order_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="派单时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}:{s}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['care:workOrder:edit']"
          >
            修改
          </el-button>
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['care:workOrder:remove']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="560px" append-to-body>
      <el-form ref="workOrderRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="关联预警ID" prop="alertId">
          <el-input v-model.number="form.alertId" placeholder="请输入关联预警ID" />
        </el-form-item>
        <el-form-item label="老人ID" prop="elderlyId">
          <el-input v-model.number="form.elderlyId" placeholder="请输入老人ID" />
        </el-form-item>
        <el-form-item label="老人姓名" prop="elderlyName">
          <el-input v-model="form.elderlyName" placeholder="请输入老人姓名" />
        </el-form-item>
        <el-form-item label="处置记录" prop="processRecord">
          <el-input v-model="form.processRecord" type="textarea" placeholder="请输入处置记录" />
        </el-form-item>
        <el-form-item label="工单状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择工单状态">
            <el-option
              v-for="dict in biz_order_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
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

<script setup name="WorkOrder">
import { addWorkOrder, delWorkOrder, getWorkOrder, listWorkOrder, updateWorkOrder } from "@/api/care/workOrder"
import { parseTime } from "@/utils/ruoyi"

const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const { biz_order_status } = proxy.useDict("biz_order_status")

const workOrderList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderId: null,
    alertId: null,
    elderlyId: null,
    elderlyName: null,
    status: null
  },
  rules: {
    alertId: [{ required: true, message: "关联预警ID不能为空", trigger: "blur" }],
    elderlyId: [{ required: true, message: "老人ID不能为空", trigger: "blur" }],
    elderlyName: [{ required: true, message: "老人姓名不能为空", trigger: "blur" }],
    status: [{ required: true, message: "工单状态不能为空", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listWorkOrder(queryParams.value)
    .then((response) => {
      workOrderList.value = response.rows
      total.value = response.total
    })
    .finally(() => {
      loading.value = false
    })
}

function reset() {
  form.value = {
    orderId: null,
    alertId: null,
    elderlyId: null,
    elderlyName: null,
    processRecord: null,
    status: "0"
  }
  proxy.resetForm("workOrderRef")
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
  proxy.resetForm("queryRef")
  applyRouteQuery()
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.orderId)
  single.value = selection.length !== 1
  multiple.value = selection.length === 0
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增工单"
}

function handleUpdate(row) {
  const orderId = row?.orderId || ids.value[0]
  if (!orderId) {
    proxy.$modal.msgWarning("请选择需要修改的数据")
    return
  }
  reset()
  getWorkOrder(orderId).then((response) => {
    form.value = { ...response.data }
    open.value = true
    title.value = "修改工单"
  })
}

function submitForm() {
  proxy.$refs.workOrderRef.validate((valid) => {
    if (!valid) {
      return
    }
    const request = form.value.orderId ? updateWorkOrder(form.value) : addWorkOrder(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.orderId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const orderIds = row?.orderId ? [row.orderId] : ids.value
  if (!orderIds.length) {
    proxy.$modal.msgWarning("请先选择要删除的数据")
    return
  }
  proxy.$modal
    .confirm(`是否确认删除工单编号为“${orderIds.join(",")}”的数据项？`)
    .then(() => delWorkOrder(orderIds.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功")
      getList()
    })
    .catch(() => {})
}

function handleExport() {
  proxy.download("care/workOrder/export", { ...queryParams.value }, `workOrder_${new Date().getTime()}.xlsx`)
}

function handleViewAlertRecord(row) {
  if (!row?.alertId) {
    return
  }
  router.push({
    path: "/smartcare/alertRecord",
    query: {
      alertId: row.alertId,
      elderlyId: row.elderlyId,
      elderlyName: row.elderlyName
    }
  })
}

function applyRouteQuery() {
  queryParams.value.orderId = route.query.orderId ?? null
  queryParams.value.alertId = route.query.alertId ?? null
  queryParams.value.elderlyId = route.query.elderlyId ?? null
  queryParams.value.elderlyName = route.query.elderlyName ?? null
}

watch(
  () => route.query,
  () => {
    applyRouteQuery()
    getList()
  },
  { deep: true }
)

applyRouteQuery()
getList()
</script>
