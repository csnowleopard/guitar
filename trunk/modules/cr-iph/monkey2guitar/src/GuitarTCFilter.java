import java.io.File;

import javax.swing.filechooser.FileFilter;


public class GuitarTCFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {
		return arg0.getName().toLowerCase().endsWith(".tst");
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "GUITAR Testcase File (.tst)";
	}

}
