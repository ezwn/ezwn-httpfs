
package httpfs.file.api;

import java.io.File;

/**
 * @author Nicolas ENZWEILER
 */
public class FileDesc {

    private String name;
    private boolean directory;
    private boolean file;
    private boolean exists;
    
    public FileDesc(File file) {
        // System.out.println("FileDesc:" + file.getName());
        this.name = file.getName();
        this.directory = file.isDirectory();
        this.file = file.isFile();
        this.exists = file.exists();
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the directory
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    /**
     * @return the file
     */
    public boolean isFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(boolean file) {
        this.file = file;
    }

    /**
     * @return the exists
     */
    public boolean isExists() {
        return exists;
    }

    /**
     * @param exists the exists to set
     */
    public void setExists(boolean exists) {
        this.exists = exists;
    }
    
}
