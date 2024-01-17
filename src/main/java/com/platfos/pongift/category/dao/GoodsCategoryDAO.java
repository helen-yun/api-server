package com.platfos.pongift.category.dao;

import com.platfos.pongift.category.model.GoodsCategory;
import com.platfos.pongift.category.model.GoodsCategoryChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GoodsCategory DAO
 * 상품 카테고리, 전시 카테고리 Join
 */
@Mapper
@Repository
public interface GoodsCategoryDAO {
    /**
     * 상품카테고리 리스트 조회
     * @param codeSt 코드 상태(Y/N)
     * @return
     */
    List<GoodsCategory> selectGoodsCategoryList(@Param("codeSt") String codeSt);

    /**
     * 상품카테고리 조회
     * @param goodsCategory 상품카테고리ID(기초코드 아이디)
     * @param codeSt 코드 상태(Y/N)
     * @return
     */
    GoodsCategory selectGoodsCategoryByGoodsCategory(@Param("goodsCategory") String goodsCategory, @Param("codeSt") String codeSt);

    /**
     * 상품카테고리 조회
     * @param goodsCategoryCd 상품카테고리코드(full code path)
     * @param codeSt 코드 상태(Y/N)
     * @return
     */
    GoodsCategory selectGoodsCategoryByGoodsCategoryCd(@Param("goodsCategoryCd") String goodsCategoryCd, @Param("codeSt") String codeSt);

    /**
     * 전시카테고리 & 유통채널 정보 조회
     * @param goodsCategory 상품카테고리ID(기초코드 아이디)
     * @param codeSt 코드 상태(Y/N)
     * @return
     */
    List<GoodsCategoryChannel> selectGoodsCategoryChannelList(@Param("goodsCategory") String goodsCategory, @Param("codeSt") String codeSt);

    /**
     * 유통채널 구분 코드 리스트 조회
     * @param goodsCategoryCd 상품카테고리코드(full code path)
     * @param codeSt 코드 상태(Y/N)
     * @return
     */
    List<String> selectChannelGbList(@Param("goodsCategoryCd") String goodsCategoryCd, @Param("codeSt") String codeSt);

    /**
     * 상품카테고리 조회(전시 카테고리 Join 안함)
     * @param goodsCategory 상품카테고리ID(기초코드 아이디)
     * @return
     */
    GoodsCategory selectGoodsCategoryCompriseWithoutExhibit(@Param("goodsCategory") String goodsCategory);
}
