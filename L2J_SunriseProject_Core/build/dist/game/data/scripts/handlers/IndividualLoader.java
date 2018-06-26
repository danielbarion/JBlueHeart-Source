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

import ai.individual.Anais;
import ai.individual.Ballista;
import ai.individual.CrimsonHatuOtis;
import ai.individual.DarkWaterDragon;
import ai.individual.DivineBeast;
import ai.individual.DrChaos;
import ai.individual.Epidos;
import ai.individual.EvasGiftBox;
import ai.individual.FrightenedRagnaOrc;
import ai.individual.Gordon;
import ai.individual.QueenShyeed;
import ai.individual.SinEater;
import ai.individual.SinWardens;
import ai.individual.Venom.Venom;
import ai.individual.extra.Aenkinel;
import ai.individual.extra.Barakiel;
import ai.individual.extra.BladeOtis;
import ai.individual.extra.EtisEtina;
import ai.individual.extra.FollowerOfAllosce;
import ai.individual.extra.FollowerOfMontagnar;
import ai.individual.extra.Gargos;
import ai.individual.extra.Hellenark;
import ai.individual.extra.HolyBrazier;
import ai.individual.extra.KaimAbigore;
import ai.individual.extra.Kechi;
import ai.individual.extra.KelBilette;
import ai.individual.extra.OlAriosh;
import ai.individual.extra.SeerFlouros;
import ai.individual.extra.SeerUgoros;
import ai.individual.extra.SelfExplosiveKamikaze;
import ai.individual.extra.ValakasMinions;
import ai.individual.extra.VenomousStorace;
import ai.individual.extra.WeirdBunei;
import ai.individual.extra.WhiteAllosce;
import ai.individual.extra.ToiRaids.Golkonda;
import ai.individual.extra.ToiRaids.Hallate;
import ai.individual.extra.ToiRaids.Kernon;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class IndividualLoader
{
	private static final Logger _log = LoggerFactory.getLogger(IndividualLoader.class);
	
	private static final Class<?>[] individual =
	{
		Anais.class,
		Ballista.class,
		CrimsonHatuOtis.class,
		DarkWaterDragon.class,
		DivineBeast.class,
		DrChaos.class,
		Epidos.class,
		EvasGiftBox.class,
		FrightenedRagnaOrc.class,
		Gordon.class,
		QueenShyeed.class,
		SinEater.class,
		SinWardens.class,
		
		// Extras
		Aenkinel.class,
		Barakiel.class,
		BladeOtis.class,
		EtisEtina.class,
		FollowerOfAllosce.class,
		FollowerOfMontagnar.class,
		Gargos.class,
		Hellenark.class,
		HolyBrazier.class,
		KaimAbigore.class,
		Kechi.class,
		KelBilette.class,
		OlAriosh.class,
		SeerFlouros.class,
		SeerUgoros.class,
		SelfExplosiveKamikaze.class,
		ValakasMinions.class,
		VenomousStorace.class,
		WeirdBunei.class,
		WhiteAllosce.class,
		
		// Extra Toi Raids
		Golkonda.class,
		Hallate.class,
		Kernon.class,
		
		// Other
		Venom.class,
	};
	
	public IndividualLoader()
	{
		_log.info(IndividualLoader.class.getSimpleName() + ": Loading related scripts.");
		for (Class<?> script : individual)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(IndividualLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
