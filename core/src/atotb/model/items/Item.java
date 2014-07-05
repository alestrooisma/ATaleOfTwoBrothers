/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package atotb.model.items;

import atotb.model.Element;

/**
 *
 * @author ale
 */
public abstract class Item extends Element {

	public Item(String name, String summary, String description) {
		super(name, summary, description);
	}
	
}
