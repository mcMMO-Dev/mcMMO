package com.gmail.nossr50;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class TreeNode {
	TreeNode left = null
	, right = null;
	Player player;
	int stat;

	public TreeNode(Player p, int in) {stat = in; player = p;}

	public void add (Player p, int in) {
		if (in <= stat)
		{
			if (left == null)
				left = new TreeNode(p,in);
			else
				left.add(p, in);
		}
		else if(in > stat)
		{
		if (right == null)
			right = new TreeNode(p,in);
		else
			right.add(p, in);
		}
	}
	
	public ArrayList<Player> inOrder(ArrayList<Player> a)
	{
	if(left != null)
	a = left.inOrder(a);

	a.add(player);

	if(right != null)
	a = right.inOrder(a);

	return a;
	}
}
