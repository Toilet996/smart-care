package com.ruoyi.smartcare.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class BizMessageNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long messageId;
    private String bizKey;
    private Long userId;
    private String roleKey;
    private String messageTitle;
    private String messageContent;
    private String messageType;
    private String messageLevel;
    private Long elderlyId;
    private String elderlyName;
    private Long alertId;
    private Long orderId;
    private String relatedRoute;
    private String readStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public String getBizKey() { return bizKey; }
    public void setBizKey(String bizKey) { this.bizKey = bizKey; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRoleKey() { return roleKey; }
    public void setRoleKey(String roleKey) { this.roleKey = roleKey; }
    public String getMessageTitle() { return messageTitle; }
    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getMessageLevel() { return messageLevel; }
    public void setMessageLevel(String messageLevel) { this.messageLevel = messageLevel; }
    public Long getElderlyId() { return elderlyId; }
    public void setElderlyId(Long elderlyId) { this.elderlyId = elderlyId; }
    public String getElderlyName() { return elderlyName; }
    public void setElderlyName(String elderlyName) { this.elderlyName = elderlyName; }
    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getRelatedRoute() { return relatedRoute; }
    public void setRelatedRoute(String relatedRoute) { this.relatedRoute = relatedRoute; }
    public String getReadStatus() { return readStatus; }
    public void setReadStatus(String readStatus) { this.readStatus = readStatus; }
    public Date getReadTime() { return readTime; }
    public void setReadTime(Date readTime) { this.readTime = readTime; }
}
