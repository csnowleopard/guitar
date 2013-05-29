/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.umd.cs.guitar;

import com.android.monkeyrunner.MonkeyDevice;
import com.android.chimpchat.core.IChimpImage;
import com.android.chimpchat.core.IChimpDevice;
import com.android.monkeyrunner.recorder.ActionListModel;
import com.android.monkeyrunner.recorder.actions.Action;
import com.android.monkeyrunner.recorder.actions.DragAction;
import com.android.monkeyrunner.recorder.actions.DragAction.Direction;
import com.android.monkeyrunner.recorder.actions.PressAction;
import com.android.monkeyrunner.recorder.actions.TouchAction;
import com.android.monkeyrunner.recorder.actions.TypeAction;
import com.android.monkeyrunner.recorder.actions.WaitAction;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.umd.cs.guitar.exception.ApplicationConnectException;
import edu.umd.cs.guitar.exception.RipperStateException;
import edu.umd.cs.guitar.model.*;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.ripper.ADRRipperConfiguration;
import edu.umd.cs.guitar.ripper.ADRRipperMonitor;
import edu.umd.cs.guitar.model.GWindow;

import java.lang.reflect.Type;


class BooleanResultReader extends MultiLineReceiver {
	private final boolean[] mResult;

	public BooleanResultReader(boolean[] result) {
		mResult = result;
	}

	@Override
	public void processNewLines(String[] strings) {
		if (strings.length > 0) {
			Pattern pattern = Pattern.compile(".*?\\([0-9]{8} ([0-9]{8}).*");
			Matcher matcher = pattern.matcher(strings[0]);
			if (matcher.matches()) {
				if (Integer.parseInt(matcher.group(1)) == 1) {
					mResult[0] = true;
				}
			}
		}
	}

	public boolean isCancelled() {
		return false;
	}
}

/**
 * MainFrame for MonkeyRecorder.
 */
public class ADRCaptureFrame extends JFrame {

	String MAIN_CLASS;
	int port = 10737;
	private int x, y;
	/*We might need this later*/
	//Set<GWindow> opened_window = new TreeSet<GWindow>(new MyComparator<GWindow>());
	Set<GWindow> opened_window = new TreeSet<GWindow>();
	private static final Logger LOG =
			Logger.getLogger(ADRCaptureFrame.class.getName());

	private final IChimpDevice device;

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel display = null;
	private JScrollPane historyPanel = null;
	private JPanel actionPanel = null;
	private JButton waitButton = null;
	private JButton pressButton = null;
	private JButton typeButton = null;
	private JButton flingButton = null;
	private JButton exportActionButton = null;

	private JButton refreshButton = null;

	private JButton stopButton = null;
	private JButton startButton = null;
	private boolean captureStarted = false;

	private BufferedImage currentImage;  //  @jve:decl-index=0:
	private BufferedImage scaledImage = new BufferedImage(320, 480,
			BufferedImage.TYPE_INT_ARGB);  //  @jve:decl-index=0:

	private JList historyList;
	private ActionListModel actionListModel;


	private AndroidDebugBridge adb;
	private IDevice currentDevice;
	private String adbCommand;

	protected ADRRipperConfiguration configuration = new ADRRipperConfiguration();
	protected final ADRCaptureMain ripperMain = new ADRCaptureMain(configuration);

	private final Timer refreshTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			refreshDisplay();  //  @jve:decl-index=0:
		}
	});

	/**
	 * This is the default constructor
	 */

	public ADRCaptureFrame(IChimpDevice device) {
		this.device = device;
		initialize();
	}

	private void initialize() {
		this.setSize(400, 600);
		this.setContentPane(getJContentPane());
		this.setTitle("ADR-Capture-Tool");

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				refreshDisplay();
			}});
		refreshTimer.start();
	}


	void RunADBCommand() throws Exception {
		boolean[] result = new boolean[1];

		if (currentDevice.isOnline()) {
			currentDevice.executeShellCommand(adbCommand, new BooleanResultReader(result));
		}
	}

	void WaitForDevice() throws Exception {
		while (currentDevice == null) {
			if(adb == null){
				System.out.println("Error==> Android Debug Bridge is null");
			}
			for (IDevice device : adb.getDevices()) {
				if (device.getAvdName() != null && device.getAvdName().equals("ADRGuitarTest")) {
					currentDevice = device;
				}
			}
		}

		while (!currentDevice.isOnline())
			Thread.sleep(1000);

		currentDevice.createForward(port, port);
	}

	boolean isConnedcted() {
		Socket socket = null;

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		socket = new Socket();

		try {
			socket.connect(new InetSocketAddress("127.0.0.1", port));
		} catch (IOException e) {
			return false;
		}

		try {
			socket.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public void setMainClass(String S){
		MAIN_CLASS = S;
	}

	public void setPort(int portNum){
		port = portNum;
	}

	public void connect() throws ApplicationConnectException {
		//		System.out.println("connect to ADB");
		//		AndroidDebugBridge.init(false);
		//		adb = AndroidDebugBridge.createBridge("adb", true);
		//		try {
		//			Thread.sleep(2000);
		//		} catch (InterruptedException e1) {
		//			e1.printStackTrace();
		//		}
		adb = AndroidDebugBridge.getBridge();
		try {
			do {
				System.out.println("==> Waiting until the emulator prepared");
				WaitForDevice();

				System.out.println("==> Starting AUT");
				System.out.println("==> Using MAIN_CLASS `" + MAIN_CLASS + "'");
				adbCommand = "am startservice -n edu.umd.cs.guitar/.Server -e AUT " + MAIN_CLASS;
				RunADBCommand();
			} while (!isConnedcted());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldn't connect to ADR server");
		}
	}
	/*
	@Override
	public void connect(String[] args) throws ApplicationConnectException {
		connect();
	}
	 */

	public void disconnect() {
		Socket socket = null;
		BufferedWriter out = null;

		try {
			System.out.println("==> cleanUp");

			socket = new Socket();
			socket.connect(new InetSocketAddress("127.0.0.1", port));

			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			out.write("finish");
			out.newLine();
			out.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}



	private void refreshDisplay() {
		IChimpImage snapshot = device.takeSnapshot();
		currentImage = snapshot.createBufferedImage();
		Graphics2D g = scaledImage.createGraphics();
		g.drawImage(currentImage, 0, 0,
				scaledImage.getWidth(), scaledImage.getHeight(),
				null);
		g.dispose();

		display.setIcon(new ImageIcon(scaledImage));

		pack();
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			display = new JLabel();
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(display, BorderLayout.CENTER);
			jContentPane.add(getHistoryPanel(), BorderLayout.EAST);
			jContentPane.add(getActionPanel(), BorderLayout.NORTH);

			display.setPreferredSize(new Dimension(320, 480));

			display.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					touch(event);
				}
			});
		}
		return jContentPane;
	}

	/**
	 * This method initializes historyPanel
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getHistoryPanel() {
		if (historyPanel == null) {
			historyPanel = new JScrollPane();
			historyPanel.getViewport().setView(getHistoryList());
		}
		return historyPanel;
	}

	private JList getHistoryList() {
		if (historyList == null) {
			actionListModel = new ActionListModel();
			historyList = new JList(actionListModel);
		}
		return historyList;
	}

	/**
	 * This method initializes actionPanel
	 *
	 * @return javax.swing.JPanel
	 */

	private JPanel getActionPanel() {
		if (actionPanel == null) {
			actionPanel = new JPanel();
			actionPanel.setLayout(new BoxLayout(getActionPanel(), BoxLayout.X_AXIS));
			actionPanel.add(getWaitButton(), null);
			actionPanel.add(getPressButton(), null);
			actionPanel.add(getTypeButton(), null);
			actionPanel.add(getFlingButton(), null);
			actionPanel.add(getExportActionButton(), null);
			actionPanel.add(getRefreshButton(), null);
			actionPanel.add(getStartButton(), null);
			actionPanel.add(getStopButton(),null);
		}
		return actionPanel;
	}

	/**
	 * This method initializes waitButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getWaitButton() {
		if (waitButton == null) {
			waitButton = new JButton();
			waitButton.setText("Wait");
			waitButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String howLongStr = JOptionPane.showInputDialog("How many seconds to wait?");
					if (howLongStr != null) {
						float howLong = Float.parseFloat(howLongStr);
						addAction(new WaitAction(howLong));
					}
				}
			});
		}
		return waitButton;
	}

	/**
	 * This method initializes pressButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPressButton() {
		if (pressButton == null) {
			pressButton = new JButton();
			pressButton.setText("Press a Button");
			pressButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JPanel panel = new JPanel();
					JLabel text = new JLabel("What button to press?");
					JComboBox keys = new JComboBox(PressAction.KEYS);
					keys.setEditable(true);
					JComboBox direction = new JComboBox(PressAction.DOWNUP_FLAG_MAP.values().toArray());
					panel.add(text);
					panel.add(keys);
					panel.add(direction);

					int result = JOptionPane.showConfirmDialog(null, panel, "Input", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION) {
						// Look up the "flag" value for the press choice
						Map<String, String> lookupMap = PressAction.DOWNUP_FLAG_MAP.inverse();
						String flag = lookupMap.get(direction.getSelectedItem());
						addAction(new PressAction((String) keys.getSelectedItem(), flag));
					}
				}
			});
		}
		return pressButton;
	}

	/**
	 * This method initializes typeButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTypeButton() {
		if (typeButton == null) {
			typeButton = new JButton();
			typeButton.setText("Type Something");
			typeButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String whatToType = JOptionPane.showInputDialog("What to type?");
					if (whatToType != null) {
						addAction(new TypeAction(whatToType));
					}
				}
			});
		}
		return typeButton;
	}

	/**
	 * This method initializes flingButton
	 *
	 * @return javax.swing.JButton
	 */

	private JButton getFlingButton() {
		if (flingButton == null) {
			flingButton = new JButton();
			flingButton.setText("Fling");
			flingButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
					panel.add(new JLabel("Which Direction to fling?"));
					JComboBox directionChooser = new JComboBox(DragAction.Direction.getNames());
					panel.add(directionChooser);
					panel.add(new JLabel("How long to drag (in ms)?"));
					JTextField ms = new JTextField();
					ms.setText("1000");
					panel.add(ms);
					panel.add(new JLabel("How many steps to do it in?"));
					JTextField steps = new JTextField();
					steps.setText("10");
					panel.add(steps);



					int result = JOptionPane.showConfirmDialog(null, panel, "Input", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION) {
						DragAction.Direction dir =
								DragAction.Direction.valueOf((String) directionChooser.getSelectedItem());
						long millis = Long.parseLong(ms.getText());
						int numSteps = Integer.parseInt(steps.getText());

						addAction(newFlingAction(dir, numSteps, millis));
					}
				}
			});
		}
		return flingButton;
	}


	private DragAction newFlingAction(Direction dir, int numSteps, long millis) {
		int width = Integer.parseInt(device.getProperty("display.width"));
		int height = Integer.parseInt(device.getProperty("display.height"));

		// Adjust the w/h to a pct of the total size, so we don't hit things on the "outside"
		width = (int) (width * 0.8f);
		height = (int) (height * 0.8f);
		int minW = (int) (width * 0.2f);
		int minH = (int) (height * 0.2f);

		int midWidth = width / 2;
		int midHeight = height / 2;

		int startx = minW;
		int starty = minH;
		int endx = minW;
		int endy = minH;

		switch (dir) {
		case NORTH:
			startx = endx = midWidth;
			starty = height;
			break;
		case SOUTH:
			startx = endx = midWidth;
			endy = height;
			break;
		case EAST:
			starty = endy = midHeight;
			endx = width;
			break;
		case WEST:
			starty = endy = midHeight;
			startx = width;
			break;
		}

		return new DragAction(dir, startx, starty, endx, endy, numSteps, millis);
	}

	/**
	 * This method initializes exportActionButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getExportActionButton() {
		if (exportActionButton == null) {
			exportActionButton = new JButton();
			exportActionButton.setText("Export Actions");
			exportActionButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					JFileChooser fc = new JFileChooser();
					if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						try {
							actionListModel.export(fc.getSelectedFile());
						} catch (FileNotFoundException e) {
							LOG.log(Level.SEVERE, "Unable to save file", e);
						}
					}
				}
			});
		}
		return exportActionButton;
	}

	/**
	 * This method initializes refreshButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.setText("Refresh Display");
			refreshButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					refreshDisplay();
				}
			});
		}
		return refreshButton;
	}

	private JButton getStartButton() {
		if (startButton == null) {
			startButton = new JButton();
			startButton.setText("Start Capture");
			startButton.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(!captureStarted){
						captureStarted = true;
						System.out.println("Capture mode started");
					}
				}
			});
		}
		return startButton;
	}

	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setText("Stop Capture");
			stopButton.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(captureStarted){
						captureStarted = false;
						ArrayList<GComponent> components = ripperMain.testCaseComponent;
						ripperMain.terminate();
						System.out.println("Capture mode terminated");
						//Do something with components
					} else {
						//do nothing
					}
				}
			});
		}
		return stopButton;
	}

	private void touch(MouseEvent event) {
		x = event.getX();
		y = event.getY();

		// Since we scaled the image down, our x/y are scaled as well.
		double scalex = ((double) currentImage.getWidth()) / ((double) scaledImage.getWidth());
		double scaley = ((double) currentImage.getHeight()) / ((double) scaledImage.getHeight());

		x = (int) (x * scalex);
		y = (int) (y * scaley);

		capComponent(x,y);
		//rip components



		//send the x and y to the emulator,
		//the emulator will change things
		//check if a new window is in opened
		switch (event.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			addAction(new TouchAction(x, y, MonkeyDevice.DOWN_AND_UP));
			break;
		case MouseEvent.MOUSE_PRESSED:
			addAction(new TouchAction(x, y, MonkeyDevice.DOWN));
			break;
		case MouseEvent.MOUSE_RELEASED:
			addAction(new TouchAction(x, y, MonkeyDevice.UP));
			break;
		}
		/*String sUUID = null;
			try {
				sUUID = captureImage(gWindow.getContainer());
			} catch (AWTException e) {
				// Ignore AWT exceptions sUUID is null
			} catch (IOException e) {
				throw e;
			}
		 */
	}


	public void capComponent(int x, int y) {
		Socket socket = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		String line;
		ADRActivity act;
		ADRWindow closed_w;
		ADRComponent ret;

		try {
			System.out.println("==> capComponent");
			System.out.println("0");
			socket = new Socket();
			System.out.println("1");
			socket.connect(new InetSocketAddress("127.0.0.1", port));
			System.out.println("2");
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			System.out.println("3");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			System.out.println("4");
			out.write("getViewAtPoint");
			System.out.println("5");
			out.newLine();
			System.out.println("6");
			out.write(x + "," + y);
			System.out.println("7");
			out.newLine();
			out.flush();
			System.out.println("before");
			while(!in.ready());
			line = in.readLine();
			//debug statement
			System.out.println("after" + "\n" + line);
			if(line.equals("Exception thrown in getViewAtPoint")){
				System.out.println("==> Error in getViewAtPoint");
			}
			String Buff = "";
			while((line = in.readLine()) != null){
				Buff = Buff + line;
			}
			System.out.println("View: " + Buff);
			/*
			 * Should receive activity,
			 * Should then recieve a view at that point
			 * Should then reciend End*/

			//This should create the window for the activity

			
			//WAS USED PREVIOUSLY
			//GWindow gWindow = new ADRWindow(line);
			GWindow gWindow = new ADRWindow(Buff);
			
			/*Type vlst = new TypeToken<ADRActivity>() {}.getType();		
			Gson gson = new Gson();
			act = gson.fromJson(line, vlst);
			closed_w = new ADRWindow(line);*/

			//GComponent comp = gWindow.getContainer();

			//This will create ADRView that we get from AUTInstrument
			Type vlst = new TypeToken<ADRView>() {}.getType();		
			Gson gson = new Gson();
			ADRView	view = gson.fromJson(line, vlst);	
			ret = new ADRComponent(view, gWindow); 

			// Rip the component created
			System.out.println("captureStarted" + captureStarted);
			if(captureStarted){
				ripperMain.execute(ret);
			}
			//			final ADRCaptureTool adrcapt = new ADRCaptureTool(); 
			//			try {
			//				GUIType guiComp = adrcapt.ripWindow(gWindow, ret);
			//				adrcapt.dGUIStructure.getGUI().add(guiComp);
			//				if (adrcapt.idGenerator == null) {
			//					throw new RipperStateException();
			//				} else {
			//					adrcapt.idGenerator.generateID(adrcapt.dGUIStructure);
			//				}
			//			} catch (Exception e) {
			//				e.printStackTrace();
			//			}


		} catch (IOException ex) {
			System.out.println("10");
			try {
				System.out.println("11");
				Thread.sleep(500);
				System.out.println("12");
			} catch (InterruptedException e1) {
				System.out.println("13");
				e1.printStackTrace();
			}
			//retWindowSet = getAllWindow();
		} finally {
			System.out.println("14");
			try {
				System.out.println("15");
				if (out != null) {
					System.out.println("16");
					out.close();
				}
				if (in != null) {
					System.out.println("17");
					in.close();
				}
				socket.close();
			} catch (IOException ex) {
				System.out.println("18");
				ex.printStackTrace();
			}
		}
		//return gWindow;
	}

	public void addAction(Action a) {
		actionListModel.add(a);
		try {
			a.execute(device);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Unable to execute action!", e);
		}
	}
}
