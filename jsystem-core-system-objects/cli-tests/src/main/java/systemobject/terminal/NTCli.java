/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;

public class NTCli extends Cli {

	Prompt prompt;
    Prompt userPrompt;
    Prompt passwordPrompt;
    Prompt domainPrompt;
    
    /**
     * Create a systemobject.terminal.Cli object
     *
     * @param terminal The terminal to use, can be systemobject.terminal.Telnet or systemobject.terminal.RS232
     * @throws java.io.IOException
     */
    public NTCli(Terminal terminal) throws IOException {
        super(terminal);
        prompt = new Prompt(">", false);
        prompt.setCommandEnd(true);
        userPrompt = new Prompt("Login username:", false);
        userPrompt.setStringToSend("guy_arieli");
        userPrompt.setAddEnter(true);
        passwordPrompt  = new Prompt("Login password:", false);
        passwordPrompt.setStringToSend("nights");
        passwordPrompt.setAddEnter(true);
        domainPrompt = new Prompt("Domain name:", false);
        domainPrompt.setStringToSend("WORKGROUP");
        domainPrompt.setAddEnter(true);

        terminal.addPrompt(prompt);
        terminal.addPrompt(userPrompt);
        terminal.addPrompt(passwordPrompt);
        terminal.addPrompt(domainPrompt);
    }

}
