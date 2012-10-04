package org.richfaces.validator;

import java.io.IOException;
import java.text.MessageFormat;

import javax.faces.application.FacesMessage;

import org.ajax4jsf.javascript.ScriptString;
import org.ajax4jsf.javascript.ScriptStringBase;

public class CSVMessageObject extends ScriptStringBase implements ScriptString {

    private static final String MESSAGE_OBJECT = "if (RichFaces.csv) '{' RichFaces.csv.addMessage('{' ''{0}'': '{'detail:''{1}'',summary:''{2}'',severity:{3}'}' '}'); '}'";
    
    private FacesMessage facesMessage;
    private String messageId;
    
    public CSVMessageObject(String messageId, FacesMessage facesMessage) {
        this.messageId = messageId;
        this.facesMessage = facesMessage;
    }
    
    @Override
    public void appendScript(Appendable target) throws IOException {
        
        String summary = facesMessage.getSummary();
        String detail = facesMessage.getDetail();
        int severity = facesMessage.getSeverity().getOrdinal();
        
        String script = MessageFormat.format(MESSAGE_OBJECT, messageId, summary, detail, severity);
        
        target.append(script);
    }
}