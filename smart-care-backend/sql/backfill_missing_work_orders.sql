INSERT INTO biz_work_order (alert_id, elderly_id, elderly_name, process_record, status, create_time)
SELECT
    ar.alert_id,
    ar.elderly_id,
    ar.elderly_name,
    CONCAT(
        '系统补派工单，预警类型：',
        COALESCE(ar.alert_type, '未知类型'),
        '，预警描述：',
        COALESCE(ar.alert_desc, '待处置')
    ) AS process_record,
    '0' AS status,
    ar.create_time
FROM biz_alert_record ar
LEFT JOIN biz_work_order wo ON ar.alert_id = wo.alert_id
WHERE ar.alert_id IS NOT NULL
  AND ar.alert_id <> 0
  AND wo.order_id IS NULL;
