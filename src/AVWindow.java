import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;


public class AVWindow extends Window{

	int width, height;
	float alpha;
	BufferedImage img;
	
	public AVWindow(Frame owner) {
		super(owner);
	    
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
	    
	    pack ();
	    setLocationRelativeTo ( null );
	    setVisible ( true );
		setAlwaysOnTop(true);
	    setAlpha(1.0f);

		AVTools.setTransparent(this);
	}
	
	public void set(int x, int y, int w, int h, float a, BufferedImage img){
		setBounds(x, y, w, h);
		
		this.width = w;
		this.height = h;
		this.img = img;

	    setAlpha(a);
	}
	
	public void set(BufferedImage img){
		this.img = img;
	    repaint();
	}
	
	public void setAlpha(float alpha){
	    AVTools.setAlpha(this, alpha);
	}
	
}
