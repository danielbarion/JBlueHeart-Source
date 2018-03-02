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

import ai.npc.Teleports.CrumaTower.CrumaTower;
import ai.npc.Teleports.DelusionTeleport.DelusionTeleport;
import ai.npc.Teleports.ElrokiTeleporters.ElrokiTeleporters;
import ai.npc.Teleports.GatekeeperSpirit.GatekeeperSpirit;
import ai.npc.Teleports.GhostChamberlainOfElmoreden.GhostChamberlainOfElmoreden;
import ai.npc.Teleports.HuntingGroundsTeleport.HuntingGroundsTeleport;
import ai.npc.Teleports.Klein.Klein;
import ai.npc.Teleports.Klemis.Klemis;
import ai.npc.Teleports.MithrilMinesTeleporter.MithrilMinesTeleporter;
import ai.npc.Teleports.NewbieTravelToken.NewbieTravelToken;
import ai.npc.Teleports.NoblesseTeleport.NoblesseTeleport;
import ai.npc.Teleports.OracleTeleport.OracleTeleport;
import ai.npc.Teleports.PaganTeleporters.PaganTeleporters;
import ai.npc.Teleports.SeparatedSoul.SeparatedSoul;
import ai.npc.Teleports.StakatoNestTeleporter.StakatoNestTeleporter;
import ai.npc.Teleports.SteelCitadelTeleport.SteelCitadelTeleport;
import ai.npc.Teleports.StrongholdsTeleports.StrongholdsTeleports;
import ai.npc.Teleports.Survivor.Survivor;
import ai.npc.Teleports.TeleportToFantasy.TeleportToFantasy;
import ai.npc.Teleports.TeleportToRaceTrack.TeleportToRaceTrack;
import ai.npc.Teleports.TeleportToUndergroundColiseum.TeleportToUndergroundColiseum;
import ai.npc.Teleports.TeleportWithCharm.TeleportWithCharm;
import ai.npc.Teleports.ToIVortex.ToIVortex;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class TeleportersLoader
{
	private static final Logger _log = LoggerFactory.getLogger(TeleportersLoader.class);
	
	private static final Class<?>[] TELEPORTERS =
	{
		CrumaTower.class,
		DelusionTeleport.class,
		ElrokiTeleporters.class,
		GatekeeperSpirit.class,
		GhostChamberlainOfElmoreden.class,
		HuntingGroundsTeleport.class,
		Klein.class,
		Klemis.class,
		MithrilMinesTeleporter.class,
		NewbieTravelToken.class,
		NoblesseTeleport.class,
		OracleTeleport.class,
		PaganTeleporters.class,
		SeparatedSoul.class,
		StakatoNestTeleporter.class,
		SteelCitadelTeleport.class,
		StrongholdsTeleports.class,
		Survivor.class,
		TeleportToFantasy.class,
		TeleportToRaceTrack.class,
		TeleportToUndergroundColiseum.class,
		TeleportWithCharm.class,
		ToIVortex.class,
	};
	
	public TeleportersLoader()
	{
		_log.info(TeleportersLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : TELEPORTERS)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(TeleportersLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
