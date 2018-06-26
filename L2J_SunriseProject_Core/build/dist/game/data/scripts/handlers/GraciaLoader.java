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

import gracia.AI.EnergySeeds;
import gracia.AI.Lindvior;
import gracia.AI.Maguen;
import gracia.AI.StarStones;
import gracia.AI.NPC.FortuneTelling.FortuneTelling;
import gracia.AI.NPC.GeneralDilios.GeneralDilios;
import gracia.AI.NPC.Lekon.Lekon;
import gracia.AI.NPC.Nemo.Nemo;
import gracia.AI.NPC.Nottingale.Nottingale;
import gracia.AI.NPC.Seyo.Seyo;
import gracia.AI.NPC.ZealotOfShilen.ZealotOfShilen;
import gracia.AI.SeedOfAnnihilation.SeedOfAnnihilation;
import gracia.instances.HallOfErosionAttack.HallOfErosionAttack;
import gracia.instances.HallOfErosionDefence.HallOfErosionDefence;
import gracia.instances.HallOfSufferingAttack.HallOfSufferingAttack;
import gracia.instances.HallOfSufferingDefence.HallOfSufferingDefence;
import gracia.instances.HeartInfinityAttack.HeartInfinityAttack;
import gracia.instances.HeartInfinityDefence.HeartInfinityDefence;
import gracia.instances.SecretArea.SecretArea;
import gracia.instances.SeedOfDestruction.SeedOfDestruction;
import gracia.vehicles.AirShipGludioGracia.AirShipGludioGracia;
import gracia.vehicles.KeucereusNorthController.KeucereusNorthController;
import gracia.vehicles.KeucereusSouthController.KeucereusSouthController;
import gracia.vehicles.SoDController.SoDController;
import gracia.vehicles.SoIController.SoIController;

/**
 * Gracia class-loader.
 * @author Pandragon
 */
public final class GraciaLoader
{
	private static final Logger _log = LoggerFactory.getLogger(GraciaLoader.class);
	
	private static final Class<?>[] SCRIPTS =
	{
		// AIs
		EnergySeeds.class,
		Lindvior.class,
		Maguen.class,
		StarStones.class,
		// NPCs
		FortuneTelling.class,
		GeneralDilios.class,
		Lekon.class,
		Nemo.class,
		Nottingale.class,
		Seyo.class,
		ZealotOfShilen.class,
		// Seed of Annihilation
		SeedOfAnnihilation.class,
		// Instances
		HallOfErosionAttack.class,
		HallOfErosionDefence.class,
		HallOfSufferingAttack.class,
		HallOfSufferingDefence.class,
		HeartInfinityAttack.class,
		HeartInfinityDefence.class,
		SecretArea.class,
		SeedOfDestruction.class,
		// Vehicles
		AirShipGludioGracia.class,
		KeucereusNorthController.class,
		KeucereusSouthController.class,
		SoIController.class,
		SoDController.class,
	};
	
	public GraciaLoader()
	{
		_log.info(GraciaLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : SCRIPTS)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(GraciaLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
