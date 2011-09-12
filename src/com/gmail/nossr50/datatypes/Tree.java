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
