/*	
 *  Copyright (c) 2011-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.event;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SitarConstants;
import edu.umd.cs.guitar.model.swtwidgets.SitarWidget;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicReference;

import edu.umd.cs.guitar.util.GUITARLog;


/**
 * The default action supported by most {@code SitarWidget}s.
 * {@code SitarDefaultAction} simulates a user interacting with a widget by
 * notifying all event listeners registered on the widget.
 * 
 * @author Gabe Gorelick
 * 
 * @see SitarWidget#getEventList()
 */
public class SitarDefaultAction extends SitarAction {

	private int sync_flag;
	private int close_flag;
	private int open_flag;
	private Robot robot;
		
	/**
	 * Execute all events that the given widget is listening for.
	 * 
	 * @param gComponent
	 *            the component to perform this action on
	 *            
	 * @see Widget#isListening(int)
	 */

	public SitarDefaultAction() {
		super();
		try
		{
			this.robot = new Robot();
		}
		catch ( AWTException ae )
		{
			ae.printStackTrace( );
		}
	}


    private void type(CharSequence characters) { 
        int length = characters.length(); 
        for (int i = 0; i < length; i++) { 
                char character = characters.charAt(i); 
                type(character); 
        } 
    }	

    private void type(char character) { 
        switch (character) { 
        case 'a': doType(KeyEvent.VK_A); break; 
        case 'b': doType(KeyEvent.VK_B); break; 
        case 'c': doType(KeyEvent.VK_C); break; 
        case 'd': doType(KeyEvent.VK_D); break; 
        case 'e': doType(KeyEvent.VK_E); break; 
        case 'f': doType(KeyEvent.VK_F); break; 
        case 'g': doType(KeyEvent.VK_G); break; 
        case 'h': doType(KeyEvent.VK_H); break; 
        case 'i': doType(KeyEvent.VK_I); break; 
        case 'j': doType(KeyEvent.VK_J); break; 
        case 'k': doType(KeyEvent.VK_K); break; 
        case 'l': doType(KeyEvent.VK_L); break; 
        case 'm': doType(KeyEvent.VK_M); break; 
        case 'n': doType(KeyEvent.VK_N); break; 
        case 'o': doType(KeyEvent.VK_O); break; 
        case 'p': doType(KeyEvent.VK_P); break; 
        case 'q': doType(KeyEvent.VK_Q); break; 
        case 'r': doType(KeyEvent.VK_R); break; 
        case 's': doType(KeyEvent.VK_S); break; 
        case 't': doType(KeyEvent.VK_T); break; 
        case 'u': doType(KeyEvent.VK_U); break; 
        case 'v': doType(KeyEvent.VK_V); break; 
        case 'w': doType(KeyEvent.VK_W); break; 
        case 'x': doType(KeyEvent.VK_X); break; 
        case 'y': doType(KeyEvent.VK_Y); break; 
        case 'z': doType(KeyEvent.VK_Z); break; 
        case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break; 
        case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break; 
        case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break; 
        case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break; 
        case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break; 
        case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break; 
        case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break; 
        case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break; 
        case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break; 
        case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break; 
        case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break; 
        case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break; 
        case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break; 
        case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break; 
        case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break; 
        case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break; 
        case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break; 
        case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break; 
        case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break; 
        case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break; 
        case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break; 
        case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break; 
        case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break; 
        case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break; 
        case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break; 
        case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break; 
        case '`': doType(KeyEvent.VK_BACK_QUOTE); break; 
        case '0': doType(KeyEvent.VK_0); break; 
        case '1': doType(KeyEvent.VK_1); break; 
        case '2': doType(KeyEvent.VK_2); break; 
        case '3': doType(KeyEvent.VK_3); break; 
        case '4': doType(KeyEvent.VK_4); break; 
        case '5': doType(KeyEvent.VK_5); break; 
        case '6': doType(KeyEvent.VK_6); break; 
        case '7': doType(KeyEvent.VK_7); break; 
        case '8': doType(KeyEvent.VK_8); break; 
        case '9': doType(KeyEvent.VK_9); break; 
        case '-': doType(KeyEvent.VK_MINUS); break; 
        case '=': doType(KeyEvent.VK_EQUALS); break; 
        case '~': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break; 
        case '!': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_1); break; 
        case '@': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_2); break; 
        case '#': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_3); break; 
        case '$': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_4); break; 
        case '%': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break; 
        case '^': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_6); break; 
        case '&': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_7); break; 
        case '*': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_8); break; 
        case '(': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_9); break; 
        case ')': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_0); break; 
        case '_': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS); break;
        case '+': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS); break; 
        case '\t': doType(KeyEvent.VK_TAB); break; 
        case '\n': doType(KeyEvent.VK_ENTER); break; 
        case '[': doType(KeyEvent.VK_OPEN_BRACKET); break; 
        case ']': doType(KeyEvent.VK_CLOSE_BRACKET); break; 
        case '\\': doType(KeyEvent.VK_BACK_SLASH); break; 
        case '{': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break; 
        case '}': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break; 
        case '|': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break; 
        case ';': doType(KeyEvent.VK_SEMICOLON); break; 
        case ':': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON); break; 
        case '\'': doType(KeyEvent.VK_QUOTE); break; 
        case '"': doType(KeyEvent.VK_QUOTEDBL); break; 
        case ',': doType(KeyEvent.VK_COMMA); break; 
        case '<': doType(KeyEvent.VK_LESS); break; 
        case '.': doType(KeyEvent.VK_PERIOD); break; 
        case '>': doType(KeyEvent.VK_GREATER); break; 
        case '/': doType(KeyEvent.VK_SLASH); break; 
        case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break; 
        case ' ': doType(KeyEvent.VK_SPACE); break; 
        default: 
                throw new IllegalArgumentException("Cannot type character " + character); 
        } 
    } 

    private void doType(int... keyCodes) { 
        doType(keyCodes, 0, keyCodes.length); 
    } 
 
    private void doType(int[] keyCodes, int offset, int length) { 
        if (length == 0) { 
                return; 
        } 
 
        robot.keyPress(keyCodes[offset]); 
        doType(keyCodes, offset + 1, length - 1); 
        robot.keyRelease(keyCodes[offset]); 
    } 
	
	public void perform(GComponent gComponent) {
		if (gComponent == null) {
			return;
		}

		final Widget widget = getWidget(gComponent);
		final SitarWidget sitar_widget = (SitarWidget) gComponent;
		final AtomicReference<String> window_title = new AtomicReference<String>();

		sync_flag = 0;
		close_flag = 1;
		widget.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				window_title.set(sitar_widget.getWindow().getShell().getText());
			
				Pattern p = Pattern.compile("open|file", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(widget.toString().replaceAll("&",""));
				boolean b = m.find();

				if(b) {
					open_flag = 1;
				}
				else {
					open_flag = 0;
				}

			
				sitar_widget.getWindow().getShell().forceActive();
			
				Event event = new Event();
				
				for (int eventType : SitarConstants.SWT_EVENT_LIST) {
					if (widget.isListening(eventType)) {
						event.type = eventType;
						widget.notifyListeners(eventType, event);
					}
				}
				sync_flag = 1;
			}
		});


		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {			
				if( widget.getDisplay().getFocusControl () == sitar_widget.getWindow().getShell() )
				{
					close_flag = 0;
				}
				else
				{
					close_flag = 1;				
				}
			}
		});

		int local_counter = 0;

		if(open_flag == 1)
		{
			try {
			  Thread.sleep(50);
			} catch (InterruptedException e) { }

			if(close_flag == 0)
				return;

			if(System.getProperty("testfile") != null)
			{
				type(System.getProperty("testfile"));
				
				try {
				  Thread.sleep(50);
				} catch (InterruptedException e) { }
				
				robot.keyPress( KeyEvent.VK_ENTER );
				robot.keyRelease( KeyEvent.VK_ENTER );
				
				try {
				  Thread.sleep(500);
				} catch (InterruptedException e) { }

			}
			
			robot.keyPress( KeyEvent.VK_ESCAPE );
			robot.keyRelease( KeyEvent.VK_ESCAPE );
		}
		else
		{
		do{
			try {
			  Thread.sleep(50);
			} catch (InterruptedException e) { }

			if(close_flag == 0) {
				break;
			}
			
				robot.keyPress( KeyEvent.VK_ESCAPE );
				robot.keyRelease( KeyEvent.VK_ESCAPE );

				if(local_counter++ > 5 || sync_flag == 1)
					break;			
			} while(true);
			}

		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if(sitar_widget.getWindow().getShell().getText() != window_title.get())
			{
					sitar_widget.getWindow().getShell().setText(window_title.get());
				}
			}
		});
	}

}
