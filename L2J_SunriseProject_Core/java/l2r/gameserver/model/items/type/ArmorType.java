/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.model.items.type;

/**
 * Armor Type enumerated.
 */
public enum ArmorType implements ItemType
{
	NONE("NONE"),
	LIGHT("LIGHT"),
	HEAVY("HEAVY"),
	MAGIC("ROBE"),
	SIGIL("SIGIL"),
	
	// L2J CUSTOM
	SHIELD("SHIELD");
	
	final int _mask;
	private final String _descr;
	
	/**
	 * Constructor of the ArmorType.
	 * @param descr
	 */
	private ArmorType(String descr)
	{
		_mask = 1 << (ordinal() + WeaponType.values().length);
		_descr = descr;
	}
	
	/**
	 * @return the ID of the ArmorType after applying a mask.
	 */
	@Override
	public int mask()
	{
		return _mask;
	}
	
	public String getDescription()
	{
		return _descr;
	}
}
