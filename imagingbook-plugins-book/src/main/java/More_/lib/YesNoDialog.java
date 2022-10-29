package More_.lib;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import ij.IJ;
import ij.Prefs;
import ij.gui.GUI;
import ij.gui.MultiLineLabel;
import ij.gui.YesNoCancelDialog;

/**
 * A modal dialog box with a single message and configurable "Yes" and "Cancel" buttons.
 * This is a simplified copy of {@link YesNoCancelDialog}, without a "No" button and
 * some overhead removed.
 */
public class YesNoDialog extends Dialog implements ActionListener, KeyListener, WindowListener {
	private static final long serialVersionUID = 1L;
	
	private final Button yesB;
	private final Button cclB;
	
	private boolean yesPressed = false;
    private boolean cancelPressed = false;
 
	private boolean firstPaint = true;

	public YesNoDialog(String title, String msg) {
		this(title, msg, "Yes", "Cancel");
	}

	public YesNoDialog(String title, String msg, String yesLabel, String cancelLabel) {
		super((Frame)null, title, true);
		setLayout(new BorderLayout());
		Panel panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		MultiLineLabel message = new MultiLineLabel(msg);
		message.setFont(new Font("Dialog", Font.PLAIN, 14));
		panel.add(message);
		add("North", panel);
		
		panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 8));
		
		yesB = new Button(yesLabel);
		cclB = new Button(cancelLabel);

		yesB.addActionListener(this);
		yesB.addKeyListener(this);

		cclB.addActionListener(this);
		cclB.addKeyListener(this);
		
		if (IJ.isWindows() || Prefs.dialogCancelButtonOnRight) {
			panel.add(yesB);
			panel.add(cclB);
		} else {
			panel.add(cclB);
			panel.add(yesB);
		}
		
		if (IJ.isMacintosh()) {
			setResizable(false);
		}
		add("South", panel);
		addWindowListener(this);
		GUI.scale(this);
		pack();
		yesB.requestFocusInWindow();
		GUI.centerOnImageJScreen(this);
		setVisible(true);
	}
    
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cclB)
			cancelPressed = true;
		else if (e.getSource() == yesB)
			yesPressed = true;
		closeDialog();
	}
	
	/** Returns true if the user dismissed dialog by pressing "Cancel". */
	public boolean cancelPressed() {
		return cancelPressed;
	}

	/** Returns true if the user dismissed dialog by pressing "Yes". */
	public boolean yesPressed() {
		return yesPressed;
	}
	
	void closeDialog() {
		dispose();
	}

	@Override
	public void keyPressed(KeyEvent e) { 
		int keyCode = e.getKeyCode(); 
		IJ.setKeyDown(keyCode); 
		if (keyCode == KeyEvent.VK_ENTER) {
			if (cclB.isFocusOwner()) {
				cancelPressed = true; 
				closeDialog(); 
			} else {
				yesPressed = true;
				closeDialog(); 
			}
		} else if (keyCode == KeyEvent.VK_Y||keyCode == KeyEvent.VK_S) {
			yesPressed = true;
			closeDialog(); 
		} else if (keyCode == KeyEvent.VK_N || keyCode == KeyEvent.VK_D) {
			closeDialog(); 
		} else if (keyCode == KeyEvent.VK_ESCAPE||keyCode == KeyEvent.VK_C) { 
			cancelPressed = true; 
			closeDialog(); 
			IJ.resetEscape();
		} 
	} 

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode(); 
		IJ.setKeyUp(keyCode); 
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

    @Override
	public void paint(Graphics g) {
    	super.paint(g);
      	if (firstPaint) {
    		yesB.requestFocus();
    		firstPaint = false;
    	}
    }

	@Override
	public void windowClosing(WindowEvent e) {
		cancelPressed = true; 
		closeDialog(); 
	}
    
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	
}