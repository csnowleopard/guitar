import java.io.File;

import javax.swing.filechooser.FileFilter;


public class MTFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {
		return arg0.getName().toLowerCase().endsWith(".mt");
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Monkey Test File (.mt)";
	}

}
