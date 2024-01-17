package com.platfos.pongift.goods.dao;

import com.platfos.pongift.goods.model.Goods;
import com.platfos.pongift.goods.model.GoodsSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoodsDAO {
    /** 상품 리스트 페이지 정보 **/
    int selectGoodsListTotalPage(GoodsSearchCondition condition);
    /** 상품 리스트 조회 **/
    List<Goods> selectGoodsList(GoodsSearchCondition condition);
    /** 상품 정보 조회 **/
    Goods selectGoodsByAgencyIdAndGoodsId(@Param("agencyId") String agencyId, @Param("goodsId") String goodsId, @Param("accessFl") String accessFl);
    /** 상품 정보 조회(상품권 원장) **/
    Goods selectGoodsByLedgerId(@Param("ledgerId") String ledgerId, @Param("agencyId") String agencyId, @Param("accessFl") String accessFl, @Param("tradeTp") String tradeTp);
    /** 상품 매장 일치 여부 조회 **/
    long countGoodsIdAndStoreId(@Param("goodsId") String goodsId, @Param("storeId") String storeId);
}
