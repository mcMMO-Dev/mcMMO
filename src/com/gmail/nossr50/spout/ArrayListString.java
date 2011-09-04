/*
 * This file is part of mmoMinecraft (http://code.google.com/p/mmo-minecraft/).
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

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gmail.nossr50.spout;

import java.util.ArrayList;

/**
 * Case insensitive ArrayList<String>.
 * Overrides the .contains(), .indexOf(), .lastIndexOf() and .remove() methods.
 */
public class ArrayListString extends ArrayList<String> {
	
	private static final long serialVersionUID = -8111006526598412404L;

	/**
	 * Returns true if this list contains the specified string.
	 * @param o String whose presence in this list is to be tested
	 * @return true if this list contains the specified string
	 */
	public boolean contains(String o) {
		for (String e : this) {
			if (o == null ? e == null : o.equalsIgnoreCase(e)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the index of the first occurrence of the specified string in this list, or -1 if this list does not contain the string.
	 * @param o String to search for
	 * @return The index of the first occurrence of the specified string in this list, or -1 if this list does not contain the string
	 */
	public int indexOf(String o) {
		for (int i = 0; i < this.size(); i++) {
			if (o == null ? get(i) == null : o.equalsIgnoreCase(get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified string in this list, or -1 if this list does not contain the string.
	 * @param o String to search for
	 * @return The index of the last occurrence of the specified string in this list, or -1 if this list does not contain the string
	 */
	public int lastIndexOf(String o) {
		for (int i = size() - 1; i >= 0; i--) {
			if (o == null ? get(i) == null : o.equalsIgnoreCase(get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Removes the first occurrence of the specified string from this list, if it is present. If the list does not contain the string, it is unchanged.
	 * @param o String to be removed from this list, if present
	 * @return true if this list contained the specified string
	 */
	public boolean remove(String o) {
		int i = indexOf(o);
		if (i != -1) {
			remove(i);
			return true;
		}
		return false;
	}

	/**
	 * Returns the element at the specified position in this list.
	 * This is for finding the correct capitalisation of an element.
	 * @param index String to search for
	 * @return the correctly capitalised element
	 */
	public String get(String index) {
		int i = this.indexOf(index);
		if (i != -1) {
			return this.get(i);
		}
		return null;
	}

	public ArrayListString meFirst(String name) {
		ArrayListString copy = new ArrayListString();
		if (this.contains(name)) {
			copy.add(name);
		}
		for (String next : this) {
			if (!next.equalsIgnoreCase(name)) {
				copy.add(next);
			}
		}
		return copy;
	}
}