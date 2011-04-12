package com.gmail.nossr50;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Tree {

	TreeNode root;

	public Tree(){}

	public void add(Player p, int in )
	{
	if(root == null)
	root = new TreeNode(p, in);
	else
	root.add(p,in);
	}
	
	public Player[] inOrder()
	{
	return (Player[]) root.inOrder(new ArrayList<Player>()).toArray();
	}

}
