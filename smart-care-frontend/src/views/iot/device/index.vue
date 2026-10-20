<template>
  <div class="app-container">
    <el-form
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      v-show="showSearch"
      label-width="88px"
    >
      <el-form-item label="设备编号" prop="deviceId">
        <el-input
          v-model="queryParams.deviceId"
          placeholder="请输入设备编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备名称" prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备类型" prop="deviceType">
        <el-select v-model="queryParams.deviceType" placeholder="请选择设备类型" clearable>
          <el-option
            v-for="dict in deviceTypeOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="老人姓名" prop="elderlyName">
        <el-input
          v-model="queryParams.elderlyName"
          placeholder="请输入老人姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择设备状态" clearable>
          <el-option
            v-for="dict in biz_device_status"
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
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['iot:device:add']"
        >
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
          v-hasPermi="['iot:device:edit']"
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
          v-hasPermi="['iot:device:remove']"
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
          v-hasPermi="['iot:device:export']"
        >
          导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="设备编号" align="center" prop="deviceId" min-width="150" />
      <el-table-column label="设备名称" align="center" prop="deviceName" min-width="160" />
      <el-table-column label="设备类型" align="center" prop="deviceType" min-width="140">
        <template #default="scope">
          <dict-tag :options="deviceTypeOptions" :value="scope.row.deviceType" />
        </template>
      </el-table-column>
      <el-table-column label="老人ID" align="center" prop="elderlyId" width="100" />
      <el-table-column label="老人姓名" align="center" prop="elderlyName" min-width="120" />
      <el-table-column label="设备状态" align="center" prop="status" width="110">
        <template #default="scope">
          <dict-tag :options="biz_device_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="安装时间" align="center" prop="installTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.installTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['iot:device:edit']"
          >
            修改
          </el-button>
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['iot:device:remove']"
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
      <el-form ref="deviceRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="设备编号" prop="deviceId">
          <el-input
            v-model="form.deviceId"
            placeholder="不填写时由后端自动生成"
            :disabled="form.id !== null"
          />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="form.deviceType" placeholder="请选择设备类型">
            <el-option
              v-for="dict in deviceTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="老人ID" prop="elderlyId">
          <el-input v-model.number="form.elderlyId" placeholder="请输入老人ID" />
        </el-form-item>
        <el-form-item label="老人姓名" prop="elderlyName">
          <el-input v-model="form.elderlyName" placeholder="请输入老人姓名" />
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择设备状态">
            <el-option
              v-for="dict in biz_device_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="安装时间" prop="installTime">
          <el-date-picker
            v-model="form.installTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择安装时间"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Device">
import { computed } from "vue"
import { addDevice, delDevice, getDevice, listDevice, updateDevice } from "@/api/iot/device"
import { parseTime } from "@/utils/ruoyi"

const { proxy } = getCurrentInstance()
const route = useRoute()
const { biz_device_status, biz_device_type } = proxy.useDict("biz_device_status", "biz_device_type")
const deviceTypeOptions = computed(() => {
  const source = [...biz_device_type.value]
  ;[
    { label: "智能门磁", value: "door_sensor" },
    { label: "智能药盒", value: "pill_box" }
  ].forEach((item) => {
    if (!source.find((option) => option.value === item.value)) {
      source.push(item)
    }
  })
  return source
})

const deviceList = ref([])
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
    deviceId: null,
    deviceName: null,
    deviceType: null,
    elderlyName: null,
    status: null
  },
  rules: {
    deviceName: [{ required: true, message: "设备名称不能为空", trigger: "blur" }],
    deviceType: [{ required: true, message: "设备类型不能为空", trigger: "change" }],
    elderlyId: [{ required: true, message: "老人ID不能为空", trigger: "blur" }],
    elderlyName: [{ required: true, message: "老人姓名不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listDevice(queryParams.value)
    .then((response) => {
      deviceList.value = response.rows
      total.value = response.total
    })
    .finally(() => {
      loading.value = false
    })
}

function reset() {
  form.value = {
    id: null,
    deviceId: null,
    deviceName: null,
    deviceType: null,
    elderlyId: null,
    elderlyName: null,
    status: "0",
    installTime: null,
    remark: null
  }
  proxy.resetForm("deviceRef")
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
  ids.value = selection.map((item) => item.id)
  single.value = selection.length !== 1
  multiple.value = selection.length === 0
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增设备台账"
}

function handleUpdate(row) {
  reset()
  const currentId = row?.id ?? ids.value[0]
  if (!currentId) {
    proxy.$modal.msgWarning("请选择一条需要修改的数据")
    return
  }
  getDevice(currentId).then((response) => {
    form.value = { ...response.data }
    open.value = true
    title.value = "修改设备台账"
  })
}

function submitForm() {
  proxy.$refs.deviceRef.validate((valid) => {
    if (!valid) {
      return
    }
    const request = form.value.id !== null ? updateDevice(form.value) : addDevice(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.id !== null ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const targetIds = row?.id ? [row.id] : ids.value
  if (!targetIds.length) {
    proxy.$modal.msgWarning("请先选择要删除的数据")
    return
  }
  proxy.$modal
    .confirm(`是否确认删除设备台账编号为“${targetIds.join(",")}”的数据项？`)
    .then(() => delDevice(targetIds.join(",")))
    .then(() => {
      getList()
      proxy.$modal.msgSuccess("删除成功")
    })
    .catch(() => {})
}

function handleExport() {
  proxy.download(
    "iot/device/export",
    {
      ...queryParams.value
    },
    `device_${new Date().getTime()}.xlsx`
  )
}

function applyRouteQuery() {
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
