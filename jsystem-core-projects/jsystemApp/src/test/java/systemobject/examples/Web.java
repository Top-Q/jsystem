/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.examples;

import jsystem.framework.system.SystemObjectImpl;

public class Web extends SystemObjectImpl{
    
	private String url = null;

    public void init() throws Exception{
        super.init();
        url = sut.getValue(getXPath() + "/url/text()");
    }

    public void addUser(String user, String password){
        // implementation
        // ...
    }
    public void removeUser(String user, String passowrd){
        // implementation
        // ...
    }

}
