package com.kangping.orm.core;




import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

/**
 * 用于创建session对象
 */
public class ORMSessionFactroy {


    private ORMConfig ormConfig;

    public ORMSessionFactroy(ORMConfig ormConfig){
        this.ormConfig = ormConfig;
    }

    public ORMSession createSession(){
        Connection connection = getConnection(ormConfig);
        ORMSession ormSession = new ORMSession(connection);
        return ormSession;
    }

    //获取数据库链接
    private Connection getConnection(ORMConfig ormConfig){

        Map<String, String> sourceProp = ORMConfig.getSourceProp();

        String url = sourceProp.get("connection.url");
        String driverClass = sourceProp.get("connection.driverClass");
        String username = sourceProp.get("connection.username");
        String password = sourceProp.get("connection.password");
        try {
            Class.forName(driverClass);
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(true);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

}
