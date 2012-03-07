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
