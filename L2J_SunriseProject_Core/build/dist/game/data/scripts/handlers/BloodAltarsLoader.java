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

import ai.npc.BloodAltars.AdenBloodAltar;
import ai.npc.BloodAltars.DarkElfBloodAltar;
import ai.npc.BloodAltars.DionBloodAltar;
import ai.npc.BloodAltars.DwarenBloodAltar;
import ai.npc.BloodAltars.ElvenBloodAltar;
import ai.npc.BloodAltars.GiranBloodAltar;
import ai.npc.BloodAltars.GludinBloodAltar;
import ai.npc.BloodAltars.GludioBloodAltar;
import ai.npc.BloodAltars.GoddardBloodAltar;
import ai.npc.BloodAltars.HeineBloodAltar;
import ai.npc.BloodAltars.KamaelBloodAltar;
import ai.npc.BloodAltars.OrcBloodAltar;
import ai.npc.BloodAltars.OrenBloodAltar;
import ai.npc.BloodAltars.PrimevalBloodAltar;
import ai.npc.BloodAltars.RuneBloodAltar;
import ai.npc.BloodAltars.SchutgartBloodAltar;
import ai.npc.BloodAltars.TalkingIslandBloodAltar;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class BloodAltarsLoader
{
	private static final Logger _log = LoggerFactory.getLogger(BloodAltarsLoader.class);
	
	private static final Class<?>[] ALTARs =
	{
		AdenBloodAltar.class,
		DarkElfBloodAltar.class,
		DionBloodAltar.class,
		DwarenBloodAltar.class,
		ElvenBloodAltar.class,
		GiranBloodAltar.class,
		GludinBloodAltar.class,
		GludioBloodAltar.class,
		GoddardBloodAltar.class,
		HeineBloodAltar.class,
		KamaelBloodAltar.class,
		OrcBloodAltar.class,
		OrenBloodAltar.class,
		PrimevalBloodAltar.class,
		RuneBloodAltar.class,
		SchutgartBloodAltar.class,
		TalkingIslandBloodAltar.class,
	};
	
	public BloodAltarsLoader()
	{
		_log.info(BloodAltarsLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : ALTARs)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(BloodAltarsLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
