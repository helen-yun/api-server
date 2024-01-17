package com.platfos.pongift.category.service;

import com.platfos.pongift.category.dao.GoodsCategoryDAO;
import com.platfos.pongift.category.model.GoodsCategory;
import com.platfos.pongift.category.model.GoodsCategoryChannel;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GoodsCategory Service
 * 상품 카테고리, 전시 카테고리 Join
 */
@Service
public class GoodsCategoryService {
    /** 상품(전시)카테고리 DAO **/
    @Autowired
    GoodsCategoryDAO dao;

    /**
     * 상품(전시)카테고리 리스트 조회
     * @param codeSt 코드 상태(Y/N)
     * @return
     */
    public List<GoodsCategory> getGoodsCategoryList(String codeSt){
        List<GoodsCategory> categories = dao.selectGoodsCategoryList(codeSt);
        for(GoodsCategory category : categories){
            category.setChannels(dao.selectGoodsCategoryChannelList(category.getGoodsCategory(), codeSt));
        }
        return categories;
    }

    /**
     * 상품(전시)카테고리 조회
     * @param goodsCategory 상품카테고리ID(기초코드 아이디)
     * @param codeSt 코드 상태(Y/N)
     * @return
     */
    public GoodsCategory getGoodsCategory(String goodsCategory, String codeSt){
        GoodsCategory category = dao.selectGoodsCategoryByGoodsCategory(goodsCategory, codeSt);
        if(category != null) category.setChannels(dao.selectGoodsCategoryChannelList(category.getGoodsCategory(), codeSt));
        return category;
    }

    /**
     * 상품(전시)카테고리 조회
     * @param goodsCategoryCd 상품카테고리코드(full code path)
     * @param codeSt 코드 상태(Y/N)
     * @return
     */
    public GoodsCategory getGoodsCategoryByGoodsCategoryCd(String goodsCategoryCd, String codeSt){
        GoodsCategory category = dao.selectGoodsCategoryByGoodsCategoryCd(goodsCategoryCd, codeSt);
        if(category != null) category.setChannels(dao.selectGoodsCategoryChannelList(category.getGoodsCategory(), codeSt));
        return category;
    }

    /**
     * 유통채널 구분 리스트 조회
     * @param goodsCategoryCd 상품카테고리코드(full code path)
     * @param codeSt 코드 상태(Y/N)
     * @return
     */
    public List<String> getChannelGbList(String goodsCategoryCd, String codeSt){
        return dao.selectChannelGbList(goodsCategoryCd, codeSt);
    }

    /**
     * 상품(전시)카테고리 조회(전시 카테고리 Join 안함)
     * @param goodsCategory 상품카테고리ID(기초코드 아이디)
     * @return
     */
    public GoodsCategory getGoodsCategoryCompriseWithoutExhibit(String goodsCategory){
        GoodsCategory category = dao.selectGoodsCategoryCompriseWithoutExhibit(goodsCategory);
        if(category != null) category.setChannels(dao.selectGoodsCategoryChannelList(category.getGoodsCategory(), null));
        return category;
    }
}
