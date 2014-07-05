package atotb.controller;

import com.badlogic.gdx.graphics.Texture;

public class Resources {
	public static Texture cursor;
	public static Texture selectionMarkerUnder;
	public static Texture selectionMarkerOver;
	public static Texture hoverMarkerUnder;
	public static Texture hoverMarkerOver;
	public static Texture targetMarkerUnder;
	public static Texture targetMarkerOver;
	public static Texture dale;
	public static Texture harryn;
	public static Texture wolf;
        
	/* Creation of static members */
	public static void loadResources() {
		//TODO placeholder implementation: fix.
		cursor = new Texture("markers/border.png");
		selectionMarkerUnder = new Texture("markers/ellipse-selected-top-blue.png");
		selectionMarkerOver = new Texture("markers/ellipse-selected-bottom-blue.png");
		hoverMarkerUnder = new Texture("markers/ellipse-top-blue.png");
		hoverMarkerOver = new Texture("markers/ellipse-bottom-blue.png");
		targetMarkerUnder = new Texture("markers/ellipse-hero-selected-top.png");
		targetMarkerOver = new Texture("markers/ellipse-hero-selected-bottom.png");
		cursor = new Texture("markers/border.png");
		wolf = new Texture("units/wolf.png");
		dale = new Texture("units/peasant.png");
		harryn = new Texture("units/woodsman.png");
	}

	public static void unload() {
		cursor.dispose();
		selectionMarkerUnder.dispose();
		selectionMarkerOver.dispose();
		hoverMarkerUnder.dispose();
		hoverMarkerOver.dispose();
		targetMarkerUnder.dispose();
		targetMarkerOver.dispose();
		dale.dispose();
		harryn.dispose();
		wolf.dispose();
	}
}
