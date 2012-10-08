package sysobj;

import jsystem.framework.system.SystemObject;
import jsystem.framework.system.SystemObjectImpl;

public class Port extends SystemObjectImpl{
    private int portId;

    public Port(){
    	setLifeTime(SystemObject.TEST_LIFETIME);
    }
    public int getPortId() {
        return portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }
    
    public void close(){
    	super.close();
    	System.out.println(getName() + " device close");
    }
}
