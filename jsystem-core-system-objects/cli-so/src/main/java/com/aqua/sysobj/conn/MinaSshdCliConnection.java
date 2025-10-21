package com.aqua.sysobj.conn;

import systemobject.terminal.Prompt;

import java.util.ArrayList;

public class MinaSshdCliConnection extends LinuxDefaultCliConnection {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public Prompt[] getPrompts() {
        ArrayList<Prompt> prompts = new ArrayList<Prompt>();
        Prompt p = new Prompt();
        p.setCommandEnd(true);
        p.setPrompt("# ");
        prompts.add(p);

        p = new Prompt();
        p.setCommandEnd(true);
        p.setPrompt("$ ");
        prompts.add(p);

        p = new Prompt();
        p.setPrompt("login: ");
        p.setStringToSend(getUser());
        prompts.add(p);

        p = new Prompt();
        p.setPrompt("Password: ");
        p.setStringToSend(getPassword());
        prompts.add(p);
        return prompts.toArray(new Prompt[prompts.size()]);
    }
}
