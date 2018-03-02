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
package handlers.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import handlers.BloodAltarsLoader;
import handlers.ConquerableHallsLoader;
import handlers.CustomsLoader;
import handlers.EventsLoader;
import handlers.FeaturesLoader;
import handlers.GraciaLoader;
import handlers.GrandBossLoader;
import handlers.GroupTemplatesLoader;
import handlers.HellboundLoader;
import handlers.IndividualLoader;
import handlers.InstanceLoader;
import handlers.MasterHandler;
import handlers.ModifiersLoader;
import handlers.NpcLoader;
import handlers.QuestLoader;
import handlers.SunriseNpcsLoader;
import handlers.TeleportersLoader;
import handlers.VillageMastersLoader;
import handlers.ZonesLoader;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class GlobalLoader
{
	private static final Logger _log = LoggerFactory.getLogger(GlobalLoader.class);
	
	private static final Class<?>[] loader =
	{
		BloodAltarsLoader.class,
		ConquerableHallsLoader.class,
		CustomsLoader.class,
		EventsLoader.class,
		FeaturesLoader.class,
		GraciaLoader.class,
		GrandBossLoader.class,
		GroupTemplatesLoader.class,
		IndividualLoader.class,
		HellboundLoader.class,
		InstanceLoader.class,
		MasterHandler.class,
		ModifiersLoader.class,
		NpcLoader.class,
		QuestLoader.class,
		SunriseNpcsLoader.class,
		TeleportersLoader.class,
		// VehiclesLoader.class,
		VillageMastersLoader.class,
		ZonesLoader.class,
	};
	
	public GlobalLoader()
	{
		long serverLoadStart = System.currentTimeMillis();
		for (Class<?> script : loader)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(GlobalLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
		_log.info(GlobalLoader.class.getSimpleName() + ": Global scripts loaded in " + (System.currentTimeMillis() - serverLoadStart) + " ms.");
	}
	
	public static void main(String[] args)
	{
		new GlobalLoader();
	}
}
