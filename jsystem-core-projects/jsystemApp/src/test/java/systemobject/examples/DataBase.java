/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.examples;

import java.sql.Connection;
import java.sql.SQLException;

import jsystem.framework.system.SystemObjectImpl;

public class DataBase extends SystemObjectImpl{
    public static final String USERS_TABLE = "users";
    public static final String USER_FIELD = "user";
    public static final String PASSWORD_FIELD = "password";

    private String dbServerName = null;
    private String user = null;
    private String password = null;
    private Connection connection = null;

    public void init() throws Exception{
        super.init();
        dbServerName = sut.getValue(getXPath() + "/dbServerName/text()");
        user = sut.getValue(getXPath() + "/user/text()");
        password = sut.getValue(getXPath() + "/password/text()");
        initConnection();
    }

    public void assertShouldBeFoundInTable(String tableName, String fieldName, String value){
        // implementation
        // ...
    }

    public void deleteFieldEntry(String tableName, String fieldName, String value){
        // implementation
        // ...
    }
    
    private void initConnection() throws SQLException{
        // init connection implementation
        // ....
    }
    
}
