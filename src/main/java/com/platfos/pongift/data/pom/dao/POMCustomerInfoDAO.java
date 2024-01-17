package com.platfos.pongift.data.pom.dao;

import com.platfos.pongift.data.pom.model.POMCustomerInfo;
import com.platfos.pongift.data.pom.model.POMTradeGift;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface POMCustomerInfoDAO {
    POMCustomerInfo selectPOMCustomerInfoByProductId(@Param("productId") String productId);
    long insertPOMCustomerInfo(POMCustomerInfo pomCustomerInfo);
    int updatePOMCustomerInfo(@Param("customerId") String customerId,
                              @Param("buyerNm") String buyerNm, @Param("buyerPhone") String buyerPhone,
                              @Param("receiverNm") String receiverNm, @Param("receiverPhone") String receiverPhone);
}
