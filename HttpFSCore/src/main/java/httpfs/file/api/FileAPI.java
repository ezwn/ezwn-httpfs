package httpfs.file.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas ENZWEILER
 */
public class FileAPI {

    public static String listFiles(final File root, final String path) throws FileNotFoundException, IOException {
        final File file = new File(root, path);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        if (!file.isDirectory()) {
            throw new RuntimeException("File is not a directory.");
        }

        if (!file.canRead()) {
            throw new RuntimeException("File is not readable.");
        }

        final List<FileDesc> descs = new ArrayList<>();

        for (File f : file.listFiles()) {
            descs.add(new FileDesc(f));
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(descs);
    }

    public static String getFileDesc(final File root, final String path) throws IOException {
        final File file = new File(root, path);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(new FileDesc(file));
    }
    
    public static String getFileContent(final File root, final String path) throws IOException {
        final File file = new File(root, path);

        if (!file.exists()) {
            System.err.println(file.getAbsolutePath() + " does not exist");
            throw new FileNotFoundException();
        }

        if (!file.isFile()) {
            throw new RuntimeException("File is not a file.");
        }

        if (!file.canRead()) {
            throw new RuntimeException("File is not readable.");
        }

        final StringBuilder stringBuilder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

            return stringBuilder.toString();
        } catch (IOException ex) {
            throw ex;
        } finally {
            reader.close();
        }

    }

    public static void setFileContent(final File root, final String path, final String content) throws IOException {
        final File file = new File(root, path);

        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.close();
    }

    public static FileEvent moveFile(final File root, final String oldPath, final String newPath) throws Exception {

        final File source = new File(root, oldPath);
        final File destination = new File(root, newPath);
        
        System.out.println(
                source.getAbsolutePath()
                + "\n    ->" + destination.getAbsolutePath()
        );
        
        boolean b = source.renameTo(destination);

        System.out.println("    [" + b + "]"
        );

        if (b) {
            return new FileEvent(false, "FILE_MOVED", destination);
        }

        throw new Exception();
    }
    
    public static FileEvent deleteFile(final File root, final String path) throws Exception {

        final File source = new File(root, path);
        
        boolean b = source.delete();

        if (b) {
            return new FileEvent(false, "FILE_DELETED", path);
        }

        throw new Exception();
    }
    
    public static FileEvent createFolder(final File root, final String path) throws Exception {

        final File source = new File(root, path);
        
        boolean b = source.mkdir();

        if (b) {
            return new FileEvent(false, "FODLER_CREATED", path);
        }

        throw new Exception();
    }
}
