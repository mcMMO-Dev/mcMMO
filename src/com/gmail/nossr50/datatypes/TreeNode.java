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

public class TreeNode 
{
	TreeNode left = null
	, right = null;
	PlayerStat ps = new PlayerStat();

	public TreeNode(String p, int in) {ps.statVal = in; ps.name = p;}

	public void add (String p, int in) 
	{
		if (in >= ps.statVal)
		{
			if (left == null)
				left = new TreeNode(p,in);
			else
				left.add(p, in);
		}
		else if(in < ps.statVal)
		{
		if (right == null)
			right = new TreeNode(p,in);
		else
			right.add(p, in);
		}
	}
	
	public ArrayList<PlayerStat> inOrder(ArrayList<PlayerStat> a)
	{
		//if left node is not null than assign arrayList(a) to left.inOrder()
		
		//GOES THROUGH THE ENTIRE LEFT BRANCH AND GRABS THE GREATEST NUMBER
		
		if(left != null)
			a = left.inOrder(a);
	
		a.add(ps);
	
		if(right != null)
			a = right.inOrder(a);
	
		return a;
	}
}
