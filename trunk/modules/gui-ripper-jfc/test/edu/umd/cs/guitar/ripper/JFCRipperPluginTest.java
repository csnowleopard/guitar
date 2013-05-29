package edu.umd.cs.guitar.ripper;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import edu.umd.cs.guitar.model.GObject;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.ripper.plugin.GRipperAfter;
import edu.umd.cs.guitar.ripper.plugin.GRipperAfterComponent;
import edu.umd.cs.guitar.ripper.plugin.GRipperBefore;
import edu.umd.cs.guitar.ripper.plugin.GRipperBeforeComponnent;

public class JFCRipperPluginTest {
	static Logger logger = Logger.getLogger("edu.umd.cs.guitar");
	
	@Before
	public void before(){
		BasicConfigurator.configure();
	}
	
	@Test
	public void testSimplePlugin() {
		
		JFCRipperMain.main(new String[]{
				"-c", "edu.umd.cs.guitar.demo.Project"
				,"-p", "edu.umd.cs.guitar.ripper.JfcSimplePlugin"
		});
	}
}


class JfcSimplePlugin implements GRipperBefore, GRipperBeforeComponnent, GRipperAfterComponent, GRipperAfter{

	@Override
	public void afterRipping() {
		System.out.println("* Done Ripping ");
	}

	@Override
	public void afterRippingComponnent(GObject component, GWindow window) {
		System.out.println("*** Done ripping component: " + component.getTitle());
	}

	@Override
	public void beforeRippingComponent(GObject component, GWindow window) {
		System.out.println("*** About to rip component: " + component.getTitle());
		
	}

	@Override
	public void beforeRipping() {
		System.out.println("* Start Ripping ");
	}
	
}