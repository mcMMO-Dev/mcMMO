package com.gmail.nossr50.datatypes;

import java.util.ArrayList;

import com.gmail.nossr50.datatypes.PlayerStat;

public class Tree {

	TreeNode root = null;

	public Tree(){}

	public void add(String p, int in)
	{
		if(root == null){
			root = new TreeNode(p, in);
		}
		else
			root.add(p,in);
	}
	
	public PlayerStat[] inOrder()
	{
		if(root != null){
			ArrayList<PlayerStat> order = root.inOrder(new ArrayList<PlayerStat>());
			return order.toArray(new PlayerStat[order.size()]);	
		} else {
			//Throw some dummy info in case the users file is empty
			//It's not a good fix but its better than rewriting the whole system
			ArrayList<PlayerStat> x = new ArrayList<PlayerStat>();
			PlayerStat y = new PlayerStat();
			y.name = "$mcMMO_DummyInfo";
			y.statVal = 0;
			x.add(y);
			return x.toArray(new PlayerStat[x.size()]);
		}
	}

}
