package systemobject.terminal;

/* License
 *
 * Copyright 1994-2004 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistribution of source code must retain the above copyright notice,
 *	this list of conditions and the following disclaimer.
 *
 *  * Redistribution in binary form must reproduce the above copyright notice,
 *	this list of conditions and the following disclaimer in the
 *	documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

//package net.mpowers.telnet;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
* A minimal telnet protocol filter that handles only
* TERMINAL_TYPE and NAWS (window size) options.
*/
public class TelnetInputStream extends InOutInputStream
{
    private OutputStream output;
    private int width, height;
    private String terminal;

    // used for replies
    private final byte[] reply  = { IAC, (byte) 0, (byte) 0 };

    /**
    * Constructor specifying the input stream to filter,
    * and the output stream with which the telnet protocol
    * is negotiated.
    */
    public TelnetInputStream(
        InputStream inInput, OutputStream inOutput )
    {
        this( inInput, inOutput, 0, 0, null );
    }

    /**
    * Constructor specifying the input stream to filter,
    * the output stream with which the telnet protocol
    * is negotiated, and terminal emulation settings.
    */
    public TelnetInputStream(
        InputStream inInput, OutputStream inOutput,
        int inWidth, int inHeight, String inTermType )
    {
    	setInputStream(inInput);
        output = inOutput;
        width = inWidth;
        height = inHeight;
        terminal = inTermType;
        if ( terminal == null ) terminal = "dumb";
    }

    public int read() throws IOException
    {
        byte b;

        b = (byte) in.read();
        if ( b != IAC ) return b; // not an IAC, skip.

        b = (byte) in.read();
        if ( b == IAC ) return b; // two IACs isn't.

        if ( b != SB ) // handle command
        {
            switch ( b )
            {
                // basic commands
                case   GA:
                case  NOP:
                case  DAT:
                case  BRK:
                case   IP:
                case   AO:
                case  AYT:
                case   EC:
                case   EL:
                    // not implemented: ignore for now
                    /*System.err.println(
                        "Ignored command: " + b + " : " + reply[2] );*/
                	if(available() > 0){
                        return read();
                	} else {
                		return -1;
                	}

                // option prefixes
                case   DO:
                case DONT:
                case WILL:
                case WONT:
                    // read next byte to determine option
                    reply[2] = (byte) in.read();
                    switch ( reply[2] )
                    {
                        case TERMINAL_TYPE:
                            // do allow terminal type subnegotiation
                            if ( b == DO )
                            {
                                reply[1] = WILL;
                                write( reply );
                                break;
                            }

                        case WINDOW_SIZE:
                            // do allow and reply with window size
                            if ( b == DO && width > 0 && height > 0 )
                            {
                                reply[1] = WILL;
                                write( reply );
                                reply[1] = SB;
                                write( reply );
                                byte[] bytes = new byte[6];
                                bytes[0] = (byte) (width >> 8);
                                bytes[1] = (byte) (width & 0xff);
                                bytes[2] = (byte) (height >> 8);
                                bytes[3] = (byte) (height & 0xff);
                                bytes[4] = IAC;
                                bytes[5] = SE;
                                write( bytes );
                                break;
                            }

                        default:
                            // unsupported option: refuse and break
                            /*System.err.println(
                                "Unsupported option: " + b + " : " + reply[2] );*/
                            reply[1] = WONT;
                            if ( b == WILL )
                            {
                                //reply[1] = DONT;
                                reply[1] = DO;
                            }
                            write( reply );
                            break;
                    }
                    break;

                default:
                    // unsupported option: suppress and exit
                    //System.err.println( "Unsupported command: " + b );
            }
        }
        else // handle begin-sub
        {
            b = (byte) in.read();
            reply[2] = b;

            switch ( b )
            {
                case TERMINAL_TYPE:
                    if ( (b = (byte) in.read()) != TERMINAL_SEND ) return b;
                    if ( (b = (byte) in.read()) != IAC ) return b;
                    if ( (b = (byte) in.read()) != SE ) return b;
                    reply[1] = SB;
                    write( reply );
                    char[] c = terminal.toCharArray();
                    byte[] bytes = new byte[c.length+3];
                    int i = 0;
                    bytes[i++] = TERMINAL_IS;
                    for ( ; i < c.length+1; i++ ) bytes[i] = (byte) c[i-1];
                    bytes[i++] = IAC;
                    bytes[i++] = SE;
                    write( bytes );
                    break;
                default:
                    reply[1] = WONT;
                    write( reply );
            }
        }
        if(available() > 0){
            return read();
        } else {
        	return -1;
        }
    }

    public void close() throws IOException
    {
        in.close();
    }

    private void write( byte inByte ) throws IOException
    {
        output.write( inByte );
        output.flush();
    }

    private void write( byte[] inBytes ) throws IOException
    {
        output.write( inBytes );
        output.flush();
    }
    public int available() throws IOException {
        return in.available();
    }

    // iac commands
    private final static byte SE   = (byte) 240; // -16
    private final static byte NOP  = (byte) 241;
    private final static byte DAT  = (byte) 242;
    private final static byte BRK  = (byte) 243;
    private final static byte IP   = (byte) 244;
    private final static byte AO   = (byte) 245;
    private final static byte AYT  = (byte) 246;
    private final static byte EC   = (byte) 247;
    private final static byte EL   = (byte) 248;
    private final static byte GA   = (byte) 249;
    private final static byte SB   = (byte) 250; // -6
    private final static byte WILL = (byte) 251; // -5
    private final static byte WONT = (byte) 252; // -4
    private final static byte DO   = (byte) 253; // -3
    private final static byte DONT = (byte) 254; // -2
    private final static byte IAC  = (byte) 255; // -1

    // options
    private final static byte TRANSMIT_BINARY = (byte) 0;
    private final static byte ECHO = (byte) 1;
    private final static byte SUPPRESS_GO_AHEAD = (byte) 3;
    private final static byte STATUS = (byte) 5;
    private final static byte TIMING_MARK = (byte) 6;
    private final static byte TERMINAL_TYPE = (byte) 24;
    private final static byte END_OF_RECORD = (byte) 25;
    private final static byte WINDOW_SIZE = (byte) 31;

    // used with END_OF_RECORD
    private final static byte EOR  = (byte) 239;

    // used with TERMINAL_TYPE
    private final static byte TERMINAL_IS = (byte) 0;
    private final static byte TERMINAL_SEND = (byte) 1;

}