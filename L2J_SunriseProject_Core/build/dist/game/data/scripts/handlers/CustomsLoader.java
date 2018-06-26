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

import custom.EchoCrystals.EchoCrystals;
import custom.FifthAnniversary.FifthAnniversary;
import custom.NewbieCoupons.NewbieCoupons;
import custom.NpcLocationInfo.NpcLocationInfo;
import custom.PinsAndPouchUnseal.PinsAndPouchUnseal;
import custom.RaidbossInfo.RaidbossInfo;
import custom.ShadowWeapons.ShadowWeapons;
import custom.Validators.SubClassSkills;
import custom.events.Wedding.Wedding;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class CustomsLoader
{
	private static final Logger _log = LoggerFactory.getLogger(CustomsLoader.class);
	
	private static final Class<?>[] CUSTOMS =
	{
		// AutoAdenaToGoldBar.class,
		EchoCrystals.class,
		FifthAnniversary.class,
		NewbieCoupons.class,
		NpcLocationInfo.class,
		PinsAndPouchUnseal.class,
		RaidbossInfo.class,
		ShadowWeapons.class,
		SubClassSkills.class,
		Wedding.class,
	};
	
	public CustomsLoader()
	{
		_log.info(CustomsLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : CUSTOMS)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(CustomsLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
