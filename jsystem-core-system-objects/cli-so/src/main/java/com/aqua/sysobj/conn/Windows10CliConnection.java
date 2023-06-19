package com.aqua.sysobj.conn;

/*
 * Copyright 2005-2020 Ignis Software Tools Ltd. All rights reserved.
 */


import java.util.ArrayList;
import java.util.List;

import systemobject.terminal.*;

/**
 * SSH CliConnection for Windows 10 machine
 * Protocol is SSH.
 * Default port is 22.
 *
 * @author  Mohammed ali Abu deeb
 */
public class Windows10CliConnection extends CliConnectionImpl {

    private static final String NEW_LINE_SUFFIX = "\n";
    private static final String REDIRECT_STDERR_SUFFIX = " 2>&1" + NEW_LINE_SUFFIX;

    /**
     * If set to true the stderr will be redirected to the output
     */
    private boolean redirectStderr = true;

    public Windows10CliConnection() {
        //disable vt100 filter no need for it because we are reading raw without pseudo terminal escape sequences
        setVt100Filter(false);
        // leading enters and redirecting stderr
        setEnterString();
        setLeadingEnter(true);
        setDump(true);
        setUseTelnetInputStream(false);
        setProtocol("ssh");
        setPort(22);
        //Important to disable the Sudo Terminal
        setEnableSudoTerminal(false);

    }

    private void setEnterString() {
        if (redirectStderr){
            setEnterStr(REDIRECT_STDERR_SUFFIX);
        } else {
            setEnterStr(NEW_LINE_SUFFIX);
        }
    }

    public Windows10CliConnection(String host, String user, String password) {
        this();
        setUser(user);
        setPassword(password);
        setHost(host);
    }

    @Override
    public Position[] getPositions() {
        return null;
    }

    @Override
    public Prompt[] getPrompts() {
        final List<Prompt> prompts = new ArrayList<>();
        Prompt p = new Prompt();
        p.setPrompt(">");
        p.setCommandEnd(true);
        p.setDontWaitForScrollEnd(false);
        prompts.add(p);
        return prompts.toArray(new Prompt[prompts.size()]);
    }


    /**
     * Is stderr also redirected to the output
     * @return
     */
    public boolean isRedirectStderr() {
        return redirectStderr;
    }

    /**
     * If set to true, will redirect the strerr stream to the output
     * @param  redirectStderr
     */
    public void setRedirectStderr(boolean redirectStderr) {
        this.redirectStderr = redirectStderr;
        setEnterString();
    }
}
