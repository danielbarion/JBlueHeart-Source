/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import conquerablehalls.DevastatedCastle.DevastatedCastle;
import conquerablehalls.FortressOfResistance.FortressOfResistance;
import conquerablehalls.FortressOfTheDead.FortressOfTheDead;
import conquerablehalls.RainbowSpringsChateau.RainbowSpringsChateau;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class ConquerableHallsLoader
{
	private static final Logger _log = LoggerFactory.getLogger(ConquerableHallsLoader.class);
	
	private static final Class<?>[] HALLS =
	{
		// BanditStronghold.class,
		// WildBeastReserve.class,
		DevastatedCastle.class,
		FortressOfResistance.class,
		FortressOfTheDead.class,
		RainbowSpringsChateau.class,
	};
	
	public ConquerableHallsLoader()
	{
		_log.info(ConquerableHallsLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : HALLS)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(ConquerableHallsLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
