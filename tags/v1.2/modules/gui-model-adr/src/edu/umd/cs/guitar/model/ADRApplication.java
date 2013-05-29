package edu.umd.cs.guitar.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.umd.cs.guitar.exception.ApplicationConnectException;

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

class MyComparator<T> implements Comparator<T>{
    public int compare(T o1, T o2){ 
        ADRWindow s1 = (ADRWindow)o1;
        ADRWindow s2 = (ADRWindow)o2;
        
        return s1.window.title.compareTo(s2.window.title);	
    }
}

public class ADRApplication extends GApplication {
	String MAIN_CLASS;
	int port;
	Set<GWindow> opened_window = new TreeSet<GWindow>(new MyComparator<GWindow>());

	public ADRApplication(String main_class, int port) {
		super();
		MAIN_CLASS = main_class;
		this.port = port;
	}

	AndroidDebugBridge adb;
	IDevice currentDevice;
	String adbCommand;

	void RunADBCommand() throws Exception {
		boolean[] result = new boolean[1];

		if (currentDevice.isOnline()) {
			currentDevice.executeShellCommand(adbCommand, new BooleanResultReader(result));
		}
	}

	void WaitForDevice() throws Exception {
		while (currentDevice == null) {
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

	@Override
	public void connect() throws ApplicationConnectException {
		AndroidDebugBridge.init(false);
		adb = AndroidDebugBridge.createBridge("adb", true);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			do {
				System.out.println("==> Waiting until the emulator prepared");
				WaitForDevice();

				System.out.println("==> Starting AUT");
				adbCommand = "am startservice -n edu.umd.cs.guitar/.Server -e AUT " + MAIN_CLASS;
				RunADBCommand();
			} while (!isConnedcted());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldn't connect to ADR server");
		}
	}

	@Override
	public void connect(String[] args) throws ApplicationConnectException {
		connect();
	}

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

	@Override
	public Set<GWindow> getAllWindow() {
		return opened_window;
	}
	
	public Set<GWindow> getRootWindows() {
		Set<GWindow> retWindowSet = new TreeSet<GWindow>();

		Socket socket = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		String line;

		try {
			System.out.println("==> getRootWindows");

			socket = new Socket();
			socket.connect(new InetSocketAddress("127.0.0.1", port));

			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

			out.write("getRootWindows");
			out.newLine();
			out.flush();
			while(!in.ready());
			line = in.readLine();

			GWindow gWindow = new ADRWindow(line);
			retWindowSet.add(gWindow);
			opened_window.add(gWindow);
		} catch (IOException ex) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			retWindowSet = getAllWindow();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return retWindowSet;
	}

	public Set<GWindow> gotoWindow(String sWindowTitleFull, String sWindowTitle) {
		Set<GWindow> retWindowSet = new TreeSet<GWindow>();

		Socket socket = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		String line;

		try {
			System.out.println("==> GotoWindow");

			socket = new Socket();
			socket.connect(new InetSocketAddress("127.0.0.1", port));

			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

			out.write("goBackTo");
			out.newLine();
			out.write(sWindowTitle);
			out.newLine();
			out.flush();
			while(!in.ready());
			line = in.readLine();

			GWindow gWindow = new ADRWindow(line);
			if (gWindow.getTitle().equals(sWindowTitleFull))
				retWindowSet.add(gWindow);
		} catch (IOException ex) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			retWindowSet = getAllWindow();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return retWindowSet;
	}

	public void closeWindow(GWindow window) {
		Socket socket = null;
		BufferedWriter out = null;
		BufferedReader in = null;

		try {
			System.out.println("==> CloseWindow");

			socket = new Socket();
			socket.connect(new InetSocketAddress("127.0.0.1", port));

			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

			out.write("back");
			out.newLine();
			out.flush();

			while(!in.ready());
			in.readLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void expandGUI(GComponent component,
			LinkedList<ADRActivity> tempClosedWinStack,
			LinkedList<ADRActivity> tempOpenedWinStack) {
		Socket socket = null;
		BufferedWriter out = null;
		BufferedReader in = null;
		String line;
		ADRActivity closed, opened;
		ADRWindow closed_w, opened_w;

		try {
			System.out.println("==> expandGUI");

			socket = new Socket();
			socket.connect(new InetSocketAddress("127.0.0.1", port));

			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

			out.write("click");
			out.newLine();
			out.write(((ADRComponent)component).getTitle());
			out.newLine();
			out.flush();

			while(!in.ready());

			line = in.readLine();
			Type vlst = new TypeToken<ADRActivity>() {}.getType();		
			Gson gson = new Gson();
			closed = gson.fromJson(line, vlst);
			closed_w = new ADRWindow(line);

			line = in.readLine();
			opened = gson.fromJson(line, vlst);
			opened_w = new ADRWindow(line);
			
			if (closed.id != opened.id) {
				tempClosedWinStack.add(closed);

				if (!opened_window.contains(opened_w)) {
					tempOpenedWinStack.add(opened);
					opened_window.add(opened_w);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
