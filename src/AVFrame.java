import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;

public class AVFrame extends JFrame implements WindowListener{
 
	JPanel p;
	AVWindow win;
	BufferedImage img;
	
	File file;
	ArrayList<String> files;
	int pos;
	int alpha = 10;
	
	boolean keyCtrl;
	boolean keyAlt;
	boolean keyShift;
	final static int KEY_SHIFT = 42;
	final static int KEY_CTRL = 29;
	final static int KEY_ALT = 56;
	
	public AVFrame() throws AWTException {
        super("AlphaViewer");
        setSize(500, 500);
        setVisible(true);
        
        addWindowListener(this);
        addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				loadSize();
			}

			public void componentShown(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        p = new JPanel(){
			@Override
			public void paint(Graphics g) {
				// TODO Auto-generated method stub
				super.paint(g);
				if(img != null){
					int w = getWidth();
					int h = getWidth() * img.getHeight() / img.getWidth();
					g.setColor(new Color(0x00000000, true));
					g.drawImage(img, 0, 0, w, h, null);
				}
			}
			
			@Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		    }

        };
        p.setBackground(new Color(0, 0, 0, 0));
        add(p);
        
        win = new AVWindow(this);
        win.setVisible(false);
         
        files = new ArrayList<String>();
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
        	System.exit(1);
        }
        
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
			public void nativeKeyTyped(NativeKeyEvent e) {
				
			}
			
			public void nativeKeyReleased(NativeKeyEvent e) {
				switch(e.getKeyCode()){
				case KEY_SHIFT:keyShift = false;break;
				case KEY_CTRL:keyCtrl = false;break;
				case KEY_ALT:keyAlt = false;break;
				}
			}
			
			public void nativeKeyPressed(NativeKeyEvent e) {
				switch(e.getKeyCode()){
				case KEY_SHIFT:keyShift = true;break;
				case KEY_CTRL:keyCtrl = true;break;
				case KEY_ALT:keyAlt = true;break;
				}
				
				if(keyShift && keyCtrl && keyAlt){
					System.out.println(e.getKeyCode());
					switch(e.getKeyCode()){
					case 87://KeyEvent.VK_F12:
						win.setVisible(!win.isVisible());
						break;
						
					case 88://KeyEvent.VK_F12:
						openFile();
						break;
						
					case 57419://KeyEvent.VK_LEFT:
						if(files != null){
							pos = (pos + files.size() - 1) % files.size();
							loadImage();
						}
						break;

					case 57421://KeyEvent.VK_RIGHT:
						if(files != null){
							pos = (pos + 1) % files.size();
							loadImage();
						}
						break;

					case 57416://KeyEvent.VK_UP:
						alpha = Math.min(alpha + 1, 10);
						loadAlpha();
						break;
						
					case 57424://KeyEvent.VK_DOWN:
						alpha = Math.max(alpha - 1, 0);
						loadAlpha();
						break;
					}
				}
			}
		});
    }
	
	public void openFile(){
		FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.LOAD);
		fd.setDirectory("C:\\");
		fd.setFile("*.png");
		fd.setVisible(true);
		
		if (fd.getFile() == null)
			return;
		
		String curFilePath = fd.getDirectory() + fd.getFile();
		
		files.clear();
		
		File folder = new File(fd.getDirectory());
		for(int i=0; i<folder.list().length; i++){
			String filename = folder.list()[i];
			
			if(!filename.toUpperCase().endsWith(".PNG"))
				continue;
			
			String path = fd.getDirectory() + filename;
			
			files.add(path);
			
			if(curFilePath.equals(path))
				pos = i;
		}
		
		loadImage();
	}

	public void loadImage(){
		if(files == null)
			return;
		
		String path = files.get(pos);
		
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int w = getWidth();
		int h = getWidth() * img.getHeight() / img.getWidth();
	
		loadSize();

		p.repaint();
		win.set(img);
	}
	
	public void loadAlpha(){
		float a = alpha / 10.0f;
		
		win.set(a);
		
		//WindowUtils.setWindowAlpha(this, a);
        AVWindow.setAlpha(this, a);

	}
	
	public void loadSize(){
		if(img != null){
			int w = p.getWidth();
			int h = p.getWidth() * img.getHeight() / img.getWidth();

			Insets in = getInsets();
			p.setSize(w, h);
		
			setTitle(w + "/" + h);	
		}
	}
	
	public void windowActivated(WindowEvent e) {
		win.setVisible(false);
	}

	public void windowDeactivated(WindowEvent e) {
		Rectangle b = getBounds();
		Insets in = getInsets();
		Rectangle pb = p.getBounds();
		
		
		
		win.setVisible(true);
		win.set(b.x + in.left, b.y + in.top, pb.width, pb.width * img.getHeight() / img.getWidth(), alpha / 10.0f, img);
	}
	
	public void windowOpened(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}

}
