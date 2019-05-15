package com.kangping.orm.core;

import com.kangping.orm.utils.AnnotationUtil;
import com.kangping.orm.utils.Dom4jUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;

import java.beans.Encoder;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用于解析核心配置文件
 */

@Slf4j
@Getter
public class ORMConfig {

    /**
     * 核心配置文件名称
     */

    private static String configName = "miniORM.cfg.xml";

    /**
     * classpath 路径
     */
    private static String classpath;

    /**
     * 数据库源信息
     */
    private static Map<String,String> sourceProp;

    /**
     * 映射文件
     */
    private static Set<String>  mapperNames;

    /**
     *  实体类路径
     */
    private static String emptyLocation;

    /**
     * 解析出来的映射信息
     */
    private static List<Mapper> mappers;

    public static  Map<String,String> getSourceProp() {
        return sourceProp;
    }

    public static List<Mapper> getMappers() {
        return mappers;
    }


    static {
        //classpath 路径
         classpath = new Thread().getContextClassLoader().getResource("").getPath();
        if (classpath != null) {
            try {
                classpath = java.net.URLDecoder.decode(classpath, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                log.error("get classpath error ,{}",e);
            }

        }
        Document document = Dom4jUtil.getXMLByFilePath(classpath + configName);
        sourceProp = Dom4jUtil.Elements2Map(document, "property", "name");
        mapperNames = Dom4jUtil.Elements2Set(document,"mapping","resource");
        emptyLocation = Dom4jUtil.getPropValue(document,"entity","package");
    }

    public ORMConfig() {

        getMapper();

    }

    private void getMapper(){
          mappers = new ArrayList<Mapper>();
        //获取mapper 映射文件名称
        for (String mapperName:mapperNames) {
            //解析
            Document document = Dom4jUtil.getXMLByFilePath(classpath + mapperName);
            //类到全路径名
            String name = Dom4jUtil.getPropValue(document, "class", "name");
            String tableName = Dom4jUtil.getPropValue(document,"class","table");
            Map<String, String> elementsID2Map = Dom4jUtil.ElementsID2Map(document);
            Map<String, String> ColumnMapping = Dom4jUtil.Elements2Map(document);
            try {
                //封装成mapper对象
                Class<?> clazz = Class.forName(name);
                //类到名称
                String className = clazz.getName();
                Mapper mapper = new Mapper();
                mapper.setClassName(className);
                mapper.setTableName(tableName);
                mapper.setPrimaryKeyMapping(elementsID2Map);
                mapper.setColumnMapping(ColumnMapping);
                mappers.add(mapper);
            } catch (ClassNotFoundException e) {
                log.error("没有找到对应到类,{}",e);
                throw new RuntimeException(e);
            }
        }

        //获取
        Set<String> classNames = AnnotationUtil.getClassNameByPackage(emptyLocation);

        for (String name : classNames) {
            try {
                //封装成mapper对象
                Class<?> clazz = Class.forName(name);
                //类到名称
                String className = clazz.getName();
                String tableName = AnnotationUtil.getTableName(clazz);
                Map<String, String> idMapper = AnnotationUtil.getIdMapper(clazz);
                Map<String, String> propMapping = AnnotationUtil.getPropMapping(clazz);
                Mapper mapper = new Mapper();
                mapper.setClassName(className);
                mapper.setTableName(tableName);
                mapper.setPrimaryKeyMapping(idMapper);
                mapper.setColumnMapping(propMapping);
                mappers.add(mapper);
            } catch (ClassNotFoundException e) {
                log.error("没有找到对应到类,{}",e);
                throw new RuntimeException(e);
            }

        }

    }


}
