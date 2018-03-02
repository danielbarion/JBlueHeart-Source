/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.model.stats.functions;

import l2r.gameserver.model.stats.Env;

/**
 * @author mkizub
 */
public final class LambdaCalc extends Lambda
{
	public AbstractFunction[] funcs;
	
	public LambdaCalc()
	{
		funcs = new AbstractFunction[0];
	}
	
	@Override
	public double calc(Env env)
	{
		double saveValue = env.getValue();
		try
		{
			env.setValue(0);
			for (AbstractFunction f : funcs)
			{
				f.calc(env);
			}
			return env.getValue();
		}
		finally
		{
			env.setValue(saveValue);
		}
	}
	
	public void addFunc(AbstractFunction f)
	{
		int len = funcs.length;
		AbstractFunction[] dest = new AbstractFunction[len + 1];
		System.arraycopy(funcs, 0, dest, 0, len);
		dest[len] = f;
		funcs = dest;
	}
}
