import java.io.File;

import javax.swing.filechooser.FileFilter;


public class EFGFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {
		return arg0.getName().toLowerCase().endsWith(".efg");
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "GUITAR EFG File (.EFG)";
	}

}
