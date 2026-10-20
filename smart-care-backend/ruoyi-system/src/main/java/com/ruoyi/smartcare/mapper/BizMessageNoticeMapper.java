package com.ruoyi.smartcare.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.smartcare.domain.BizMessageNotice;

public interface BizMessageNoticeMapper
{
    BizMessageNotice selectBizMessageNoticeById(Long messageId);

    List<BizMessageNotice> selectBizMessageNoticeList(BizMessageNotice bizMessageNotice);

    int insertIgnoreBizMessageNotice(BizMessageNotice bizMessageNotice);

    int updateBizMessageNoticeRead(@Param("messageId") Long messageId, @Param("userId") Long userId);

    int updateAllBizMessageNoticeRead(Long userId);

    List<Map<String, Object>> selectMessageStats(Long userId);

    List<Long> selectUserIdsByRoleKeys(@Param("roleKeys") List<String> roleKeys);
}
