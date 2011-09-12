/*
 * This file is part of mmoMinecraft (https://github.com/mmoMinecraftDev).
 *
 * mmoMinecraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.nossr50.spout.util;

import org.getspout.spoutapi.gui.GenericTexture;

import com.gmail.nossr50.config.LoadProperties;

public final class GenericFace extends GenericTexture {

	private static String facePath = "http://face.rycochet.net/";
	private static int defaultSize = 8;
	private String name;

	public GenericFace() {
		this("", defaultSize);
	}

	public GenericFace(String name) {
		this(name, defaultSize);
	}

	public GenericFace(String name, int size) {
		if (LoadProperties.showFaces) {
			this.setWidth(size).setHeight(size).setFixed(true);
			setName(name);
		} else {
			this.setVisible(false);
		}
	}

	public String getName() {
		return name;
	}

	public GenericFace setName(String name) {
		if (LoadProperties.showFaces) {
			this.name = name == null ? "" : name;
			super.setUrl(facePath + this.name + ".png");
			super.setDirty(true);
		}
		return this;
	}

	public GenericFace setSize(int size) {
		if (LoadProperties.showFaces) {
			super.setWidth(size).setHeight(size);
		}
		return this;
	}
}