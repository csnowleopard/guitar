package edu.umd.cs.guitar.replayer;

import junit.framework.TestCase;
import edu.umd.cs.guitar.replayer.*;

public class SitarReplayerMainTest extends TestCase {
	public SitarReplayerMainTest(String nm)
	{
		super(nm);
	}
	
	public void testMain()
	{
		String [] arg = System.getProperty("cmdargs").split(" ");
		SitarReplayerMain.main(arg);
	}
}
