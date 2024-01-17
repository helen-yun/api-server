package com.platfos.pongift.data.pom.dao;

import com.platfos.pongift.data.pom.model.POMDispatchInfo;
import com.platfos.pongift.data.pom.model.POMTradeGift;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface POMDispatchInfoDAO {
    int selectResendCountByProductId(@Param("productId") String productId);
    int selectExpiredNoticeCountByProductId(@Param("productId") String productId);
    long insertPOMDispatchInfo(POMDispatchInfo pomDispatchInfo);
}
