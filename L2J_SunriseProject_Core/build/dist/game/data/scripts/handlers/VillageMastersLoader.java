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

import ai.npc.VillageMasters.Alliance.Alliance;
import ai.npc.VillageMasters.Clan.Clan;
import ai.npc.VillageMasters.DarkElvenChange1.DarkElvenChange1;
import ai.npc.VillageMasters.DarkElvenChange2.DarkElvenChange2;
import ai.npc.VillageMasters.DwarvenOccupationChange.DwarvenOccupationChange;
import ai.npc.VillageMasters.ElvenHumanBuffers2.ElvenHumanBuffers2;
import ai.npc.VillageMasters.ElvenHumanFighters1.ElvenHumanFighters1;
import ai.npc.VillageMasters.ElvenHumanFighters2.ElvenHumanFighters2;
import ai.npc.VillageMasters.ElvenHumanMystics1.ElvenHumanMystics1;
import ai.npc.VillageMasters.ElvenHumanMystics2.ElvenHumanMystics2;
import ai.npc.VillageMasters.FirstClassTransferTalk.FirstClassTransferTalk;
import ai.npc.VillageMasters.KamaelChange1.KamaelChange1;
import ai.npc.VillageMasters.KamaelChange2.KamaelChange2;
import ai.npc.VillageMasters.OrcOccupationChange1.OrcOccupationChange1;
import ai.npc.VillageMasters.OrcOccupationChange2.OrcOccupationChange2;
import ai.npc.VillageMasters.SubclassCertification.SubclassCertification;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class VillageMastersLoader
{
	private static final Logger _log = LoggerFactory.getLogger(VillageMastersLoader.class);
	
	private static final Class<?>[] MASTERS =
	{
		Alliance.class,
		Clan.class,
		DarkElvenChange1.class,
		DarkElvenChange2.class,
		DwarvenOccupationChange.class,
		ElvenHumanBuffers2.class,
		ElvenHumanFighters1.class,
		ElvenHumanFighters2.class,
		ElvenHumanMystics1.class,
		ElvenHumanMystics2.class,
		FirstClassTransferTalk.class,
		KamaelChange1.class,
		KamaelChange2.class,
		OrcOccupationChange1.class,
		OrcOccupationChange2.class,
		SubclassCertification.class,
	};
	
	public VillageMastersLoader()
	{
		_log.info(VillageMastersLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : MASTERS)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(VillageMastersLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
