package com.kangping.orm.core;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 映射模型
 */
@Setter
@Getter
public class Mapper {

    /**
     * 类名称
     */
    private String className;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 主键的映射信息
     */
    private Map<String,String> primaryKeyMapping;

    /**
     * 列的映射信息
     */
    private Map<String,String> columnMapping;



}
