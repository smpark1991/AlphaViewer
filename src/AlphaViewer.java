import java.awt.AWTException;

public class AlphaViewer{
	@SuppressWarnings("serial")
	public static void main ( String[] args )
	{
		try {
			AVFrame win = new AVFrame();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}
