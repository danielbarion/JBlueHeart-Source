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

import ai.grandboss.Beleth;
import ai.grandboss.Core;
import ai.grandboss.Orfen;
import ai.grandboss.QueenAnt;
import ai.grandboss.VanHalter;
import ai.grandboss.Antharas.Antharas;
import ai.grandboss.Baium.Baium;
import ai.grandboss.Sailren.Sailren;
import ai.grandboss.Valakas.Valakas;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class GrandBossLoader
{
	private static final Logger _log = LoggerFactory.getLogger(GrandBossLoader.class);
	
	private static final Class<?>[] GRANDBOSSES =
	{
		Beleth.class,
		Core.class,
		Orfen.class,
		QueenAnt.class,
		VanHalter.class,
		Antharas.class,
		Baium.class,
		Sailren.class,
		Valakas.class,
	};
	
	public GrandBossLoader()
	{
		_log.info(GrandBossLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : GRANDBOSSES)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(GrandBossLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
