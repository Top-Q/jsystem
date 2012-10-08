/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;

/**
 *  HTML javadoc view. 
 *  Javadoc content is displayed in JEditorPane pane using HTMLEditorKit.
 *
 */
public class HTMLJavaDocView extends JEditorPane implements JavaDocView {
    
    private static final long serialVersionUID = 1L;
	
    private HTMLEditorKit htmlKit;
    
    /** Creates a new instance of HTMLJavaDocView */
    public HTMLJavaDocView(Color bgColor) {
        setEditable(false);
        setBGColor(bgColor);
        setMargin(new Insets(0,3,3,3));
    }

    /** Sets the javadoc content as HTML document */
    public void setContent( String doc) {
		if (doc.indexOf("<br>") == -1){
			doc = doc.replaceAll("\n","<br>");
		}
		final String content = doc;
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                Reader in = new StringReader(""+content+"");//NOI18N                
                try{
                    Document doc = getDocument();
                    doc.remove(0, doc.getLength());
                    getEditorKit().read(in, getDocument(),0);  //!!! still too expensive to be called from AWT
                    setCaretPosition(0);
                    scrollRectToVisible(new Rectangle(0,0,0,0));            
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }catch(BadLocationException ble){
                    ble.printStackTrace();
                }
            }
        });
    }
    
    /** Sets javadoc background color */
    public void setBGColor(Color bgColor) {
        setBackground(bgColor);
    }
    
    protected EditorKit createDefaultEditorKit() {
        if (htmlKit == null){
            htmlKit= new HTMLEditorKit ();
            setEditorKit(htmlKit);

          
            if (htmlKit.getStyleSheet().getStyleSheets() != null)
                return htmlKit;
            
            javax.swing.text.html.StyleSheet css = new javax.swing.text.html.StyleSheet();
            java.awt.Font f = /*new javax.swing.JTextArea().*/ getFont();
            css.addRule(new StringBuffer("body { font-size: ").append(f.getSize()) // NOI18N
                        .append("; font-family: ").append(f.getName()).append("; }").toString()); // NOI18N
            css.addStyleSheet(htmlKit.getStyleSheet());
            htmlKit.setStyleSheet(css);
        }
        return htmlKit;
    }
}