package com.platfos.pongift.data.ssm.dao;

import com.platfos.pongift.data.ssm.model.SSMGiftCardHistory;
import com.platfos.pongift.data.ssm.model.SSMGoodsInfo;
import com.platfos.pongift.data.ssm.model.SSMSendHistory;
import com.platfos.pongift.data.ssm.model.SSMSendInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SSMDirectSendGiftCardDAO {
    List<SSMSendInfo> selectSendInfoList(SSMSendInfo sendInfoParam);

    List<SSMSendInfo> selectGoodsSendInfoList(SSMSendInfo sendInfoParam);

    SSMSendInfo selectSendGoodsByGoodsId(String goodsId, String accessFl);

    void updateGiftCardNo(SSMSendInfo sendInfo);

    long insertGiftCardExpiryHistory(SSMGiftCardHistory giftCardHistory);

    long insertGiftCardSendHistory(SSMSendHistory giftCardSendHistory);

    List<SSMGiftCardHistory> selectGiftCardHistoryList(String sendId);
}
