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

import events.CharacterBirthday.CharacterBirthday;
import events.SquashEvent.SquashEvent;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class EventsLoader
{
	private static final Logger _log = LoggerFactory.getLogger(EventsLoader.class);
	
	private static final Class<?>[] EVENTS =
	{
		CharacterBirthday.class,
		
		// Disabled by default events
		// FreyaCelebration.class,
		// GiftOfVitality.class,
		// HeavyMedal.class,
		// LoveYourGatekeeper.class,
		// MasterOfEnchanting.class,
		// SavingSanta.class,
		SquashEvent.class,
		// TheValentineEvent.class,
	};
	
	public EventsLoader()
	{
		_log.info(EventsLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : EVENTS)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(EventsLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
