package edu.umd.cs.guitar.helper;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.*;

import edu.umd.cs.guitar.gui.InputException;
import edu.umd.cs.guitar.parser.XMLParser;

/**
 * This class imports all necessary files into working directory. <br/>
 * GUI Structure and EFG can be accessed via getGuiFile() and getEfgFile() respectively <br />
 * All screen shots can be accessed by getScreenshots() which is list of BufferedImage <br />
 * Test cases directory can be accessed via getTestCaseDir() <br />
 * 
 * @author Sigmund Gorski and Muhammad Ashraf Ishak
 *
 */
public class FileImporter {

	private File guiFile = null;
	private File efgFile = null;
	private File testCaseDir = null;
	private File screenshotDir = null;
	public ArrayList<BufferedImage> screenShots = null;
	public HashMap <String, BufferedImage> nameImage = null;
	private static final boolean TESTING = false;
	
	/**
	 * Get the exported GUI File
	 * @return GUI in File type
	 */
	public File getGuiFile() {
		return guiFile;
	}
	
	/**
	 * Get the exported EFG file
	 * @return EFG in File type
	 */
	public File getEfgFile() {
		return efgFile;
	}
	
	/**
	 * Get exported test case directory
	 * @return 
	 */
	public File getTestCaseDir() {
		return testCaseDir;
	}
	
	/**
	 * Get all screenshots in BufferedImage
	 * @return
	 */
	public ArrayList<BufferedImage> getScreenShots() {
		return screenShots;
	}
	
	/**
	 * This creates an XMLParser object using the information read in by this object.
	 * 
	 * @return an XMLParser object
	 */
	public XMLParser getNewXMLParser(){
		return new XMLParser(guiFile, efgFile, testCaseDir);
	}
	
	/**
	 * This is the constructor for the FileImporter that takes in a single argument, the directory
	 * in which all the files are stored. It then verifies the files are there properly reads them stores them.
	 * 
	 * @param workingDirPath the path of the directory where the files are stored
	 * @throws InputException an exception thrown where there is a fatal error in that files required for normal
	 * cannot be found in the directory given
	 */
	public FileImporter(String workingDirPath) throws InputException{
		nameImage = new HashMap<String, BufferedImage>();
		screenShots = new ArrayList<BufferedImage>();
		File workDir = new File(workingDirPath);
		//foundTest, validTest, foundEFG, foundGUI, foundScreenShots, validScreen, oneScreen
		boolean verify[] = new boolean[] {false, true, false, false, false, true, false};
		if(TESTING){
			fileImporterTesting(workDir, verify);
		}else{
			for(File sel : workDir.listFiles()){
				if(sel.isDirectory()){
					if(sel.getName().equals("testcases")){
						verify[0]= true;
						this.testCaseDir = sel;
						for(File name : sel.listFiles()){
							if(!name.isDirectory()){
								if(!name.getName().endsWith(".tst")){
									verify[1] = false;
									break;
								}
							}
						}
					}else if(sel.getName().equals("screenshots")){
						verify[4] = true;
						this.screenshotDir = sel;
						for(File file : sel.listFiles()){
							if(!file.isDirectory()){
								BufferedImage bi;
								try {
									bi = ImageIO.read(file);
									screenShots.add(bi);
									nameImage.put(file.getName(),bi);
									verify[6] = true;
								} catch (IOException e) {
									verify[5] = false; 
								}
							}
						}
					}
				}else if(sel.getName().endsWith(".EFG")){
					verify[2] = true;
					this.efgFile = sel;
				}else if(sel.getName().endsWith(".GUI")){
					verify[3] = true;
					this.guiFile = sel;
				}
			}
		}
		errorReporting(verify);
	}
	
	/**
	 * This used solely for testing purposes and is basically a copy of the one
	 * above except it copies the files and loads the copies.
	 * 
	 * @param workDir the path of the directory where the files are stored
	 * @param verify an array used to record errors found
	 */
	private void fileImporterTesting(File workDir, boolean[] verify){
		File newDir = new File(new File(workDir.getAbsolutePath()).getParent() + File.separator + workDir.getName() + "_copy");
		File testDir = new File(newDir.getAbsolutePath() + File.separator + "testcases");
		File screenDir = new File(newDir.getAbsolutePath() + File.separator + "screenshots");
		if(newDir.exists()){
			deleteDir(newDir);
		}
		newDir.mkdir();
		testDir.mkdir();
		screenDir.mkdir();
		this.testCaseDir = testDir;
		this.screenshotDir = screenDir;
		for(File sel : workDir.listFiles()){
			if(sel.isDirectory()){
				if(sel.getName().equals("testcases")){
					verify[0]= true;
					for(File name : sel.listFiles()){
						if(!name.isDirectory()){
							if(!name.getName().endsWith(".tst")){
								verify[1] = false;
							}else{
								copyFile(name, testDir.getAbsolutePath());
							}
						}
					}
				}else if(sel.getName().equals("screenshots")){
					verify[4] = true;
					for(File file : sel.listFiles()){
						if(!file.isDirectory()){
							copyImage(file, screenDir.getAbsolutePath(), verify);
						}
					}
				}
			}else if(sel.getName().endsWith(".EFG")){
				verify[2] = true;
				this.efgFile = copyFile(sel,newDir.getAbsolutePath());
			}else if(sel.getName().endsWith(".GUI")){
				verify[3] = true;
				this.guiFile = copyFile(sel, newDir.getAbsolutePath());
				this.guiFile = sel;
			}
		}
	}
	
	/**
	 * This method is used to process the errors found when trying to load the necessary files.
	 * 
	 * @param arr the array that stores weather of not errors occurred
	 * @throws InputException the exception thrown if any fatal error is detected
	 */
	private void errorReporting(boolean[] arr) throws InputException{
		if(!arr[2]){
			System.out.println("Error: No EFG file found");
			throw new InputException("Error: No EFG file found!\nThe program will now exit.");
		}
		if(!arr[3]){
			System.out.println("Error: No GUI file found");
			throw new InputException("Error: No GUI file found!\nThe program will now exit.");
		}
		if(!arr[4]){
			System.out.println("Error: No \"screenshots\" directory found.");
			throw new InputException("Error: No \"screenshots\" directory found!\nThe program will now exit.");
		}
		if(!arr[0]){
			System.out.println("Error: No \"testcases\" directory found.");
			throw new InputException("Error: No \"testcases\" directory found!\nThe program will now exit.");
		}
		if(!arr[1]){
			System.out.println("Warning: Some files were ingored in the \"testcases\" directory because they did not have the extension \".tst\"");
		}
		if(!arr[5]){
			System.out.println("Warning: Some files were ingored in the \"screenshots\" directory because they were not valid image files.");
		}
		if(!arr[6]){
			System.out.println("Warning: No test case files found in the \"testcases\" directory.");
		}
	}
	
	/**
	 * This is used to delete completely any directory and all the files and sub directories in it.
	 * 
	 * @param dir the directory to delete
	 * @return if it was successful
	 */
	public static boolean deleteDir(File dir) {
		if(dir.isDirectory()) {
			for(File children : dir.listFiles()){
				boolean success = deleteDir(children);
				if(!success){
					return false;
				}
			}
		}
		return dir.delete();
	}
	
	/**
	 * Given a list of file names for test cases, the methods deletes all files in the list if
	 * they exist.
	 * 
	 * @param testCase array of test case files to delete
	 * @return a boolean array indicating successful deletion of each file in the list
	 */
	public boolean[] deleteTestCaseFiles(ArrayList<String> testCase){
		boolean[] ret = new boolean[testCase.size()];
		for(int i = 0; i < ret.length; i++){
			ret[i] = false;
		}
		int counter = 0;
		for(File test: testCaseDir.listFiles()){
			if(testCase.contains(test.getName())){
				test.delete();
				ret[testCase.indexOf(test.getName())] = true;
				counter++;
			}
			if(counter >= ret.length){
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Given a string the method will delete all test cases whose file names
	 * start with that string.
	 * 
	 * @param start the start of the file name
	 * @return names of all the files deleted
	 */
	public ArrayList<String> deleteTestCaseFiles(String start){
		ArrayList<String> ret = new ArrayList<String>();
		for(File test : testCaseDir.listFiles()){
			if(test.getName().startsWith(start)){
				ret.add(test.getName());
				test.delete();
			}
		}
		return ret;
	}
	
	/**
	 * Makes a copy of a file to a destination directory. The name remains the same.
	 * 
	 * @param source the file to be copied
	 * @param destDir the new directory path of the copied file
	 * @return the file copied
	 */
	private File copyFile (File source, String destDir){
		File result = new File(destDir + File.separator + source.getName());
		try {
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(result);
			byte[] buf = new byte[4096];
			int len;
			while((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			out.flush();
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
		} catch (IOException e) {
			System.out.println("IO Problem");
		}
		return result;
	}
	
	/**
	 * Copies an image file to a new directory keeping the same name.
	 * 
	 * @param source the file to be copied
	 * @param destDir the new directory path of the copied file
	 * @param verify an array used for verification purposes
	 */
	private void copyImage (File source, String destDir, boolean[] verify){
		File destF = new File(destDir + File.separator + source.getName());
		try {
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(destF);
			byte[] buf = new byte[4096];
			int len;
			while((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			out.flush();
			in.close();
			out.close();
			
			BufferedImage bi = ImageIO.read(destF);
			if (screenShots == null){
				screenShots = new ArrayList<BufferedImage>();
			}
			screenShots.add(bi);
			nameImage.put(destF.getName(), bi);
			verify[6] = true;
		} catch (FileNotFoundException e) {
			verify[5] = false;
		} catch (IOException e) {
			verify[5] = false;
		}
		
	}
	
	public void setGuiFile(File guiFile) {
		this.guiFile = guiFile;
	}

	public void setEfgFile(File efgFile) {
		this.efgFile = efgFile;
	}

	public void setTestCaseDir(File testCaseDir) {
		this.testCaseDir = testCaseDir;
	}

	public void setScreenshotDir(File screenshotDir) {
		this.screenshotDir = screenshotDir;
	}

	public File getScreenshotDir() {
		return screenshotDir;
	}

	public void setScreenShots(ArrayList<BufferedImage> screenShots) {
		this.screenShots = screenShots;
	}
}
