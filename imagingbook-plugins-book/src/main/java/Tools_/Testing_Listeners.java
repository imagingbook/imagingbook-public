package Tools_;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Testing_Listeners implements PlugInFilter {
	
	private ImageWindow win;
	private ImageCanvas canvas;
	private ImagePlus im;
	
	private MouseAdapter ma;

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		this.win = this.im.getWindow();
		this.canvas = this.win.getCanvas();
		
//		canvas.addMouseListener(this);
		
		ma = new MouseAdapterPlus(true) {
			@Override
			public void mouseClickedPlus(MouseEvent e) {
				IJ.log("clicked on " + im.getShortTitle());
				someAction();
			}
			
			@Override
			public void mousePressedPlus(MouseEvent e) {
				IJ.log("pressed on " + im.getShortTitle());
				someAction();
			}
			
			@Override
			public void mouseReleasedPlus(MouseEvent e) {
				IJ.log("released on " + im.getShortTitle());
				someAction();
			}
		};
		
		canvas.addMouseListener(ma);
		
		
	}
	
	private void someAction() {
		IJ.log("throwing exception now ");
		int[] numbers = new int[4];
//		numbers[5] = 77;
	}

	// --- mouse listener --------------------------------------
	
	// alternatively imolement MouseListener instead!
	public static abstract class MouseAdapterPlus extends MouseAdapter {
		
		enum MouseEventType {
			Clicked, Pressed, Released;
		}
		
		private final boolean handleExceptions;
		
		protected MouseAdapterPlus() {
			this(true);
		}
		
		protected MouseAdapterPlus(boolean handleExceptions) {
			this.handleExceptions = handleExceptions;
		}
		
//		public void mouseClicked(MouseEvent e) {
//			try {
//				mouseClickedPlus(e);
//			} catch (Throwable thr) {
//				if (handleExceptions) {
//					IJ.log("caught throwable " + thr);
//					IJ.handleException(thr);
//				}
//			}
//		};
		
		@Override
		public void mouseClicked(MouseEvent e) {
			dispatch(MouseEventType.Clicked, e);
		};
		
		@Override
		public void mousePressed(MouseEvent e) {
			dispatch(MouseEventType.Pressed, e);
		};
		
		@Override
		public void mouseReleased(MouseEvent e) {
			dispatch(MouseEventType.Released, e);
		};
		
		
		// write only once, handle exception in central place:
		private void dispatch(MouseEventType type, MouseEvent e) {
			try {
				switch (type) {
				case Clicked:
					mouseClickedPlus(e); break;
				case Pressed:
					mousePressedPlus(e); break;
				case Released:
					mouseReleasedPlus(e); break;

				}
			} catch (Throwable thr) {
				if (handleExceptions) {
					IJ.log("caught throwable " + thr);
					IJ.handleException(thr);
				}
			}

		}
		
		public void mouseClickedPlus(MouseEvent e) {};
		public void mousePressedPlus(MouseEvent e) {};
		public void mouseReleasedPlus(MouseEvent e) {};
	}


}
