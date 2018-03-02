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

import ai.modifier.FlyingNpcs;
import ai.modifier.NoChampionMobs;
import ai.modifier.NoMovingNpcs;
import ai.modifier.NoRandomAnimation;
import ai.modifier.NoRandomWalkMobs;
import ai.modifier.NonAttackingNpcs;
import ai.modifier.NonLethalableNpcs;
import ai.modifier.NonTalkingNpcs;
import ai.modifier.RunningNpcs;
import ai.modifier.SeeThroughSilentMove;
import ai.modifier.dropEngine.FortressReward;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class ModifiersLoader
{
	private static final Logger _log = LoggerFactory.getLogger(ModifiersLoader.class);
	
	private static final Class<?>[] MODIFIERS =
	{
		FlyingNpcs.class,
		NoChampionMobs.class,
		NoMovingNpcs.class,
		NonAttackingNpcs.class,
		NonLethalableNpcs.class,
		NonTalkingNpcs.class,
		NoRandomAnimation.class,
		NoRandomWalkMobs.class,
		RunningNpcs.class,
		SeeThroughSilentMove.class,
		
		// Drop Modifiers
		FortressReward.class,
	};
	
	public ModifiersLoader()
	{
		_log.info(ModifiersLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : MODIFIERS)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(ModifiersLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":" + e.getMessage());
			}
		}
	}
}
