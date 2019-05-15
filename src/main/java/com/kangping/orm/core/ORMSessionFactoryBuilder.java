package com.kangping.orm.core;

import java.sql.Connection;

/**
 * 用于创建工厂对象
 */
public class ORMSessionFactoryBuilder {

    private static ORMSessionFactroy ormSessionFactroy;

    private ORMSessionFactoryBuilder(){};


    public static synchronized ORMSessionFactroy build( ORMConfig ormConfig){
        if (ormSessionFactroy != null) {
            return ormSessionFactroy;
        }
        return new ORMSessionFactroy(ormConfig);
    }

}
