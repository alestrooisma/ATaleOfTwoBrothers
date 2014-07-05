package atotb.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import atotb.TwoBrothersGame;

public class DesktopLauncher {

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "A Tale of Two Brothers";
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new TwoBrothersGame(), config);
	}
}
