package com.platfos.pongift.data.sim.dao;

import com.platfos.pongift.data.sim.model.SIMCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface SIMDynamicDAO {
    @Cacheable(value="colCache", key="#tableName")
    HashMap<String, String> selectPrimaryKeyColumnNameByTableName(@Param("tableName") String tableName);
    int countByTableNameAndColumn(@Param("tableName") String tableName, @Param("columnName") String columnName
            , @Param("columnValue") String columnValue);
    HashMap<String, String> selectByTableNameAndColumnAndSelectColumnName(@Param("tableName") String tableName, @Param("columnName") String columnName
            , @Param("columnValue") String columnValue, @Param("selectColumnName") String selectColumnName);
    HashMap<String, Object> selectHierarchicalInfo(@Param("levels") int[] levels, @Param("separator") String separator, @Param("tableName") String tableName, @Param("columnName") String columnName
            , @Param("columnValue") String columnValue, @Param("parentColumnName") String parentColumnName);
    List<HashMap<String, Object>> selectReverseHierarchicalPaths(@Param("levels") int[] levels, @Param("separator") String separator, @Param("tableName") String tableName, @Param("columnName") String columnName
            , @Param("columnValue") String columnValue, @Param("parentColumnName") String parentColumnName);
}
