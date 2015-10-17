import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.sun.awt.AWTUtilities;
import com.sun.java.swing.plaf.windows.WindowsGraphicsUtils;
import com.sun.jna.Native;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;


public class AVWindow extends Window{

	int width, height;
	BufferedImage img;
	
	public AVWindow(Frame owner) {
		super(owner);

		setAlwaysOnTop(true);
	    
	    add ( new JComponent ()
	    {
			protected void paintComponent ( Graphics g )
	        {
				if(img != null){
					g.setColor(new Color(0x00000000, true));
					g.drawImage(img, 0, 0, width, height, null);
				}
	        }

	        public Dimension getPreferredSize ()
	        {
	            return new Dimension ( getWidth(), getHeight() );
	        }

	        public boolean contains ( int x, int y )
	        {
	            return false;
	        }
	    } );

	    AWTUtilities.setWindowOpaque ( this, false );
	    
	    pack ();
	    setLocationRelativeTo ( null );
	    setVisible ( true );
	}
	
	public void set(int x, int y, int w, int h, float a, BufferedImage img){
		setBounds(x, y, w, h);
		
		this.width = w;
		this.height = h;
		this.img = img;

	    set(a);
	}
	public void set(BufferedImage img){
		this.img = img;
		
	    set(AWTUtilities.getWindowOpacity(this));
	}
	
	public void set(float a){
	    AWTUtilities.setWindowOpaque ( this, false );
        AWTUtilities.setWindowOpacity(this, a);
	    //WindowUtils.setWindowAlpha(this, a);
	    setTransparent(this);

	    repaint();
	}
	
	public static void setTransparent(Component w) {
	    WinDef.HWND hwnd = getHWnd(w);
	    int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
	    wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
	    User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
	}
	
	public static void setAlpha(Component w, float a) {
	    WinDef.HWND hwnd = getHWnd(w);
	    int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
	    wl = wl | WinUser.WS_EX_LAYERED;
	    User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
	    User32.INSTANCE.SetLayeredWindowAttributes(hwnd, 0, (byte)(255 * a), WinUser.LWA_ALPHA);
	    
	}

	/**
	 * Get the window handle from the OS
	 */
	private static HWND getHWnd(Component w) {
	    HWND hwnd = new HWND();
	    hwnd.setPointer(Native.getComponentPointer(w));
	    return hwnd;
	}
}
