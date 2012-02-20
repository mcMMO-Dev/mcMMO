/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50.runnables;

import java.util.ArrayDeque;

import org.bukkit.block.Block;

/*
 * This file was created for a breakage introduced in 1.1-R2
 * It should be removed afterwards if the breakage is removed.
 */
public class ChangeDataValueTimer implements Runnable {
	private ArrayDeque<Block> queue;
	
	public ChangeDataValueTimer(ArrayDeque<Block> queue) {
		this.queue = queue;
	}
	
	public void run() {
		int size = queue.size();
		if(size == 0) return;
		if(size > 25) {
			size = (int) Math.floor(size / 10);
		}
		
		for(int i = 0; i < size; i++) {
			Block change = queue.poll();
			if(change == null) continue;
			change.setData((byte) 5);
		}
	}
}
