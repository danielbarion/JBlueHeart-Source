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

import ai.sunriseNpc.AchievementManager.AchievementManager;
import ai.sunriseNpc.BetaManager.BetaManager;
import ai.sunriseNpc.CasinoManager.CasinoManager;
import ai.sunriseNpc.CastleManager.CastleManager;
import ai.sunriseNpc.DelevelManager.DelevelManager;
import ai.sunriseNpc.GrandBossManager.GrandBossManager;
import ai.sunriseNpc.NoblesseManager.NoblesseManager;
import ai.sunriseNpc.PointsManager.PointsManager;
import ai.sunriseNpc.PremiumManager.PremiumManager;
import ai.sunriseNpc.ReportManager.ReportManager;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class SunriseNpcsLoader
{
	private static final Logger _log = LoggerFactory.getLogger(SunriseNpcsLoader.class);
	
	private static final Class<?>[] NPCS =
	{
		AchievementManager.class,
		BetaManager.class,
		CasinoManager.class,
		CastleManager.class,
		DelevelManager.class,
		GrandBossManager.class,
		NoblesseManager.class,
		PointsManager.class,
		PremiumManager.class,
		ReportManager.class,
	};
	
	public SunriseNpcsLoader()
	{
		_log.info(SunriseNpcsLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> instance : NPCS)
		{
			try
			{
				instance.newInstance();
			}
			catch (Exception e)
			{
				_log.error(SunriseNpcsLoader.class.getSimpleName() + ": Failed loading " + instance.getSimpleName() + ":", e);
			}
		}
	}
}
