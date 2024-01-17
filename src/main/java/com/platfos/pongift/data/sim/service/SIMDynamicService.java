package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.data.sim.dao.SIMDynamicDAO;
import com.platfos.pongift.definition.SIMIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class SIMDynamicService {
    @Autowired
    SIMDynamicDAO dao;

    public HashMap<String, String> selectPrimaryKeyColumnNameByTableName(SIMIndex simIndex){
        return dao.selectPrimaryKeyColumnNameByTableName(simIndex.getTbl());
    }
    public HashMap<String, String> selectByTableNameAndColumnAndSelectColumnName(String tableName, String columnName, String columnValue, String selectColumnName){
        return dao.selectByTableNameAndColumnAndSelectColumnName(tableName, columnName, columnValue, selectColumnName);
    }
    public int countByTableNameAndColumn(String tableName, String columnName, String columnValue){
        return dao.countByTableNameAndColumn(tableName, columnName, columnValue);
    }
    public HashMap<String, Object> selectHierarchicalInfo(int maxLevel, String separator, SIMIndex simIndex, String columnValue, String parentColumnName){
        HashMap<String, String> info = selectPrimaryKeyColumnNameByTableName(simIndex);
        if(info != null && (info.containsKey("tbl") & info.containsKey("col"))) return selectHierarchicalInfo(maxLevel, separator, info.get("tbl"), info.get("col"), columnValue, parentColumnName);
        return null;
    }
    public HashMap<String, Object> selectHierarchicalInfo(int maxLevel, String separator, String tableName, String columnName, String columnValue, String parentColumnName){
        return dao.selectHierarchicalInfo(new int[maxLevel], separator, tableName, columnName, columnValue, parentColumnName);
    }
    public List<HashMap<String, Object>> selectReverseHierarchicalPaths(int maxLevel, String separator, SIMIndex simIndex, String columnValue, String parentColumnName){
        HashMap<String, String> info = selectPrimaryKeyColumnNameByTableName(simIndex);
        if(info != null && (info.containsKey("tbl") & info.containsKey("col"))) return selectReverseHierarchicalPaths(maxLevel, separator, info.get("tbl"), info.get("col"), columnValue, parentColumnName);
        return null;
    }
    public List<HashMap<String, Object>> selectReverseHierarchicalPaths(int maxLevel, String separator, String tableName, String columnName, String columnValue, String parentColumnName){
        return dao.selectReverseHierarchicalPaths(new int[maxLevel], separator, tableName, columnName, columnValue, parentColumnName);
    }
    public boolean check(SIMIndex simIndex, String columnValue){
        HashMap<String, String> info = selectPrimaryKeyColumnNameByTableName(simIndex);
        int cnt = 0;
        if(info != null && (info.containsKey("tbl") & info.containsKey("col"))) cnt = countByTableNameAndColumn(info.get("tbl"), info.get("col"), columnValue);
        return (cnt>0);
    }
}
