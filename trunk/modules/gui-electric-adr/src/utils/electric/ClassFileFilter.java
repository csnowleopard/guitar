package utils.electric;

import java.io.File;
import javax.swing.filechooser.*;


/* ImageFilter.java is used by FileChooserDemo2.java. */
public class ClassFileFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = f.getName().substring(f.getName().lastIndexOf('.'));
        if (extension != null) {
            if (extension.equals(".class")){
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Just Java Class files";
    }
}