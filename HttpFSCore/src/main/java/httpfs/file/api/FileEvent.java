
package httpfs.file.api;

/**
 *
 * @author Nicolas ENZWEILER
 */
public class FileEvent {

    private boolean exception;
    private String type;
    private Object subject;
    
    public FileEvent() {
    }

    public FileEvent(boolean exception, String type, Object subject) {
        this.exception = exception;
        this.type = type;
        this.subject = subject;
    }
        
    /**
     * @return the exception
     */
    public boolean isException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(boolean exception) {
        this.exception = exception;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the subject
     */
    public Object getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(Object subject) {
        this.subject = subject;
    }
   
}
