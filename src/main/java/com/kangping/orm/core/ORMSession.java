package com.kangping.orm.core;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用于和数据库交互
 */
@Slf4j
public class ORMSession {

    private Connection connection;

    public ORMSession(Connection connection) {
        this.connection = connection;
    }

    /**
     * 保存对象
     * @param obj
     */
    public void save(Object obj) throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Mapper> mappers = ORMConfig.getMappers();
        for (Mapper mapper : mappers) {
            if (mapper.getClassName().equals(obj.getClass().getName())) {
                String insertSqlBefore = "insert into "+mapper.getTableName() + "(";
                String insertSqlafter = ")values(";
                Map<String, String> columnMapping = mapper.getColumnMapping();
                Set<String> proSet = columnMapping.keySet();
                Class<?> objClass = obj.getClass();
                for (String pro : proSet) {
                    insertSqlBefore+=columnMapping.get(pro)+",";
                    Field field = objClass.getDeclaredField(pro);
                    field.setAccessible(true);
                    Object f = field.get(obj);
                    if (f instanceof Date) {
                        Date date = (Date) f;

                        String format = simpleDateFormat.format(date);
                        insertSqlafter += "'" + format +"'"+",";
                    } else {
                        insertSqlafter += "'" + f.toString() +"'"+",";
                    }
                }
                String insertsql = insertSqlBefore.substring(0,insertSqlBefore.length()-1)+insertSqlafter.substring(0,insertSqlafter.length()-1)+")";
                log.info("miniORM insertsql:{}",insertsql);
                PreparedStatement preparedStatement = connection.prepareStatement(insertsql);
                preparedStatement.executeUpdate();
                break;
            }
        }


    }

    /**
     * 查询单个对象
     * @param clazz
     * @param id
     * @return
     */
    public Object findOne(Class clazz, Object id){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Mapper> mappers = ORMConfig.getMappers();
        for (Mapper mapper : mappers) {
            if (mapper.getClassName().equals(clazz.getName())) {
                Map<String, String> primaryKeyMapping = mapper.getPrimaryKeyMapping();
                Object[] objects = primaryKeyMapping.keySet().toArray();
                Map<String, String> columnMapping = mapper.getColumnMapping();
                Set<String> pros = columnMapping.keySet();
                try {
                    Object obj = clazz.newInstance();
                    String idColumn = primaryKeyMapping.get(objects[0].toString());
                    String findOneSql = "select * from "+mapper.getTableName() +" where "+idColumn +"= '"+id.toString() +"'";
                    log.info("miniORM findOneSql:{}",findOneSql);
                    PreparedStatement preparedStatement = connection.prepareStatement(findOneSql);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        for (String por : pros) {
                            Field field = clazz.getDeclaredField(por);
                            field.setAccessible(true);
                            //获取列
                            String col = columnMapping.get(por);
                            //从ResultSet 取对应列的值
                            Object object = resultSet.getObject(col);
                            Type type = field.getGenericType();
                            if (type.getTypeName().equals("java.lang.Integer")) {
                                Integer value = Integer.parseInt(object.toString());
                                field.set(obj,value);
                            } else if(type.getTypeName().equals("java.util.Date")){
                                Date date = simpleDateFormat.parse(object.toString());
                                field.set(obj,date);
                            }else {
                                field.set(obj,object);
                            }
                        }
                    }
                    return obj;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 删除对象
     * @param obj
     */
    public void delete(Object obj){
        List<Mapper> mappers = ORMConfig.getMappers();
        for (Mapper mapper : mappers) {
            if (mapper.getClassName().equals(obj.getClass().getName())) {
                Map<String, String> primaryKeyMapping = mapper.getPrimaryKeyMapping();
                Object[] objects = primaryKeyMapping.keySet().toArray();
                Class<?> aClass = obj.getClass();
                try {
                    Field field = aClass.getDeclaredField(objects[0].toString());
                    field.setAccessible(true);
                    Object o = field.get(obj);
                    String deleteSql = "delete from " + mapper.getTableName() +
                            " where " + primaryKeyMapping.get(objects[0]) +" = " + o.toString();

                    log.info("miniORM deleteSql:{}",deleteSql);
                    PreparedStatement preparedStatement = connection.prepareStatement(deleteSql);
                    preparedStatement.executeUpdate();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }



}
