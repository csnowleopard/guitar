package edu.umd.cs.guitar.ripper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.umd.cs.guitar.event.ADREventHandler;
import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.model.ADRActivity;
import edu.umd.cs.guitar.model.ADRApplication;
import edu.umd.cs.guitar.model.ADRComponent;
import edu.umd.cs.guitar.model.ADRConstants;
import edu.umd.cs.guitar.model.ADRWindow;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.util.GUITARLog;

public class ADRRipperMonitor extends GRipperMonitor {

	ADRRipperConfiguration CONFIG = null;

	volatile LinkedList<ADRActivity> tempOpenedWinStack = new LinkedList<ADRActivity>();
	volatile LinkedList<ADRActivity> tempClosedWinStack = new LinkedList<ADRActivity>();

	public ADRRipperMonitor(ADRRipperConfiguration config) {
		this.CONFIG = config;
	}
	
	ADRApplication app;

	@Override
	public void setUp() {
		EventManager em = EventManager.getInstance();

		for (Class<? extends ADREventHandler> event : ADRConstants.DEFAULT_SUPPORTED_EVENTS) {
			try {
				em.registerEvent(event.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		app = new ADRApplication(CONFIG.MAIN_CLASS, CONFIG.port);
		app.connect();
	}

	@Override
	public void cleanUp() {
		app.disconnect();
	}
	
	@Override
	public List<GWindow> getRootWindows() {
		return new ArrayList<GWindow>(app.getRootWindows());
	}

	@Override
	public boolean isNewWindowOpened() {
		return (tempOpenedWinStack.size() > 0);
	}

	@Override
	public boolean isWindowClosed() {
		return (tempClosedWinStack.size() > 0);
	}

	@Override
	public LinkedList<GWindow> getOpenedWindowCache() {
		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();

		for (ADRActivity window : tempOpenedWinStack) {
			GWindow gWindow = new ADRWindow(window);
			if (gWindow.isValid())
				retWindows.addLast(gWindow);
		}
		return retWindows;
	}

	@Override
	public void resetWindowCache() {
		this.tempOpenedWinStack.clear();
		this.tempClosedWinStack.clear();
	}

	@Override
	public void closeWindow(GWindow window) {
		app.closeWindow(window);
	}

	@Override
	public boolean isIgnoredWindow(GWindow window) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void expandGUI(GComponent component) {
		if (component == null)
			return;

		GUITARLog.log.info("Expanding *" + component.getTitle() + "*...");
		
		Set<GWindow> gRootWindows = app.getRootWindows();
		
		String sWindowTitle = ((ADRComponent)component).getWindow().getTitle();
		String list[] = sWindowTitle.split("\\.");
		
		for (GWindow l : gRootWindows) {
			if (!((ADRWindow)l).getTitle().equals(sWindowTitle)) {
				//return;
				app.gotoWindow(sWindowTitle, list[list.length-1]);
			}
		}
		
		app.expandGUI(component, tempClosedWinStack, tempOpenedWinStack);
	}

	@Override
	boolean isExpandable(GComponent component, GWindow window) {
		return ((ADRComponent)component).isExpandable();
	}

	@Override
	LinkedList<GWindow> getClosedWindowCache() {
		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();

		for (ADRActivity window : tempClosedWinStack) {
			ADRWindow gWindow = new ADRWindow(window);
			if (gWindow.isValid())
				retWindows.addLast(gWindow);
		}
		return retWindows;
	}

}
