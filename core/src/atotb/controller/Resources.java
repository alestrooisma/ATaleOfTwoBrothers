package atotb.controller;

import com.badlogic.gdx.graphics.Texture;

public class Resources {
	// Markers
	public static Texture cursor;
	public static Texture selectionMarkerUnder;
	public static Texture selectionMarkerOver;
	public static Texture hoverMarkerUnder;
	public static Texture hoverMarkerOver;
	public static Texture targetMarkerUnder;
	public static Texture targetMarkerOver;
	public static Texture walkMarker;
	public static Texture dashMarker;
	//
	// Units
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
		walkMarker = new Texture("markers/dot-blue.png");
		dashMarker = new Texture("markers/dot-yellow.png");
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
		walkMarker.dispose();
		dashMarker.dispose();
		dale.dispose();
		harryn.dispose();
		wolf.dispose();
	}
}
