package edu.umd.cs.guitar.model;

import java.lang.Integer.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;
import java.io.*;

import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;

public class IphCommServer {

	static ServerSocket iServerSocket;

	static Socket iSocket;

	static PrintWriter toIphone;
	static BufferedReader fromIphone;

	private static int time_out = 0;
	private static int port_num = 8081;	

	private static boolean connected = false;
	
	public static boolean setUpIServerSocket() {
		iServerSocket = null;
		try {
			iServerSocket = new ServerSocket(port_num);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port_num);
			return false;
		}
		System.out.println("Server set up successfully!");
		return true;
	}

	public static boolean waitForConnection() {
		// If server not set up, set up first
		if (iServerSocket == null) {
			setUpIServerSocket();
		} else {
			if (!iServerSocket.isBound()) {
				setUpIServerSocket();
			}
		}
		
		// Wait for connection, set up the connection
		iSocket = null;
		try {
			iSocket = iServerSocket.accept();
			iSocket.setSoTimeout(time_out);
			connected = true;
			System.out.println("Connection set up successfully!");
		} catch (IOException e) {
			System.err.println("Accept failed.");
			return false;
		}

		try {
			toIphone = new PrintWriter(iSocket.getOutputStream(), true);
			fromIphone= new BufferedReader(new InputStreamReader(iSocket.getInputStream()));
			System.out.println("IOStream initialized successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static String hear(){
		String inputLine;
		try {
			while ((inputLine = fromIphone.readLine()) != null) {
				System.out.println("Received response: " + inputLine);
				return inputLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setTimeOut(int timeout) {
		time_out = timeout;
	}
	
	public static int getTimeOut() {
		return time_out;
	}
	
	public static void setPortNum(int port) {
		port_num = port;
	}
	
	public static int getPortNum() {
		return port_num;
	}
	
	public static boolean isConnected() {
		return connected;
	}
	public static void request(String request) {
		if (isConnected() == true) {
			char[] buffer = request.toCharArray();
			System.out.println("Sending request: " + new String(buffer));
			toIphone.write(buffer);
			toIphone.flush();
		}
	}
	
	public static String requestAndHear(String request) {
		if (isConnected() == true) {
			request(request);
			return hear();
		} else {
			return null;
		}
	}
	
	public static void setPort(int port) {
		port_num = port;
	}
	
	public static void setPortAndTimeout(int port, int timeout) {
		port_num = port;
		time_out = timeout;
	}

	public static void close() {
		try {
			fromIphone.close();
			toIphone.close();
			iSocket.close();
			iServerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Request the GUIStructure in xml format from the Object-C Guitar, and fill in IphWindow list with WindowType
	 * @param windows
	 */
  /*
    Updated by CMSC435 04/25/2012
    Changed the communication flow of iGUITAR
      1. iGUITAR queries the app for number of windows
      2. iGUITAR then rips that number of windows and adds to list
    */
	public static void requestMainView(ArrayList<IphWindow> windows) {
		// Create a XML handler to parse the iPhone client's GUI response.
		XMLHandler xmlHandler = new XMLHandler();
		
		System.out.println("Did request");

		int numWindows = Integer.parseInt(requestAndHear(IphCommServerConstants.GET_NUM_WINDOWS));
		System.out.println("Num windows: " + numWindows);

    ByteArrayInputStream bs_root;
    bs_root = new ByteArrayInputStream(
      requestAndHear(IphCommServerConstants.GET_WINDOW_LIST).getBytes());
    GUIStructure guiWindow_root = (GUIStructure) xmlHandler.readObjFromFile(bs_root, GUIStructure.class);
    for (int i = 1; i < numWindows; i++) {
      // The xml handler needs an InputStream.
      ByteArrayInputStream bs;
      bs = new ByteArrayInputStream(requestAndHear("get new window").getBytes());
      
      // We can create the GUIStructure here using the xml handler.
      GUIStructure guiWindow = (GUIStructure) xmlHandler.readObjFromFile(bs, GUIStructure.class);
      
      // Pass the GUI to the new IphWindow object.
      windows.add(new IphWindow(guiWindow.getGUI().get(0), i + 1));
    }
    windows.add(new IphWindow(guiWindow_root.getGUI().get(0), 1));
	}
//	public static void getWindowProperties(Map<String, String> nameValueMap, String title) {
//		String xmlContent = requestAndHear(IphCommServerConstants.GET_WINDOW_PROPERTY_LIST + title);
//		XMLProcessor.parseProperties(nameValueMap, xmlContent);
//	}
}
