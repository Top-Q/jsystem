package sysobj;

import jsystem.framework.system.SystemObjectImpl;

public class Telnet extends SystemObjectImpl{
    private int index = -1;
    private boolean dummy = false;
    private long timeout = 20000;
    private String[] errors = null;
    String text =
            "C:\\>dir\r\n" +
            "Volume in drive C has no label.\r\n" +
            "Volume Serial Number is 24B7-9F23\r\n" +
            "Directory of C:\\\r\n" +
            "03/05/2004  10:17 PM                 0 AUTOEXEC.BAT\r\n" +
            "03/05/2004  10:17 PM                 0 CONFIG.SYS\r\n" +
            "10/01/2004  09:51 PM    <DIR>          Documents and Settings\r\n" +
            "09/16/2004  09:38 PM    <DIR>          j2sdk1.4.2_04\r\n" +
            "04/23/2004  03:22 PM    <DIR>          My Downloads\r\n" +
            "10/02/2004  10:28 PM    <DIR>          Program Files\r\n" +
            "09/12/2004  08:30 AM    <DIR>          vslick\r\n" +
            "10/05/2004  04:04 PM    <DIR>          WINDOWS\r\n" +
            "03/05/2004  10:46 PM    <DIR>          WinRAR\r\n" +
            "09/18/2004  11:27 PM    <DIR>          work\r\n" +
            "               2 File(s)              0 bytes\r\n" +
            "               8 Dir(s)   4,305,276,928 bytes free\r\n" +
            "C:\\>";


    public void init() throws Exception {
        super.init();
        System.out.println("Telnet was init");
    }

    public void close() {
    }
    public void dirCommand(){
        setTestAgainsObject(text);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }
}
