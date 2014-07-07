/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atotb.controller;

import atotb.TwoBrothersGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 *
 * @author ale
 */
public class MainInputHandler extends InputAdapter {

	private TwoBrothersGame game;

	public MainInputHandler(TwoBrothersGame game) {
		this.game = game;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Keys.ESCAPE:
				Gdx.app.exit();
				return true;
		} 
		return false;
	}

}
