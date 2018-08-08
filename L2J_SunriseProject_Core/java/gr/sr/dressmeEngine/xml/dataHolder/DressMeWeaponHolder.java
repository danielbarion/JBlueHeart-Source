package gr.sr.dressmeEngine.xml.dataHolder;

import java.util.ArrayList;
import java.util.List;

import gr.sr.data.xml.AbstractHolder;
import gr.sr.dressmeEngine.data.DressMeWeaponData;

public final class DressMeWeaponHolder extends AbstractHolder
{
	private static final DressMeWeaponHolder _instance = new DressMeWeaponHolder();
	
	public static DressMeWeaponHolder getInstance()
	{
		return _instance;
	}
	
	private final List<DressMeWeaponData> _weapons = new ArrayList<>();
	
	public void addWeapon(DressMeWeaponData weapon)
	{
		_weapons.add(weapon);
	}
	
	public List<DressMeWeaponData> getAllWeapons()
	{
		return _weapons;
	}
	
	public DressMeWeaponData getWeapon(int id)
	{
		for (DressMeWeaponData weapon : _weapons)
		{
			if (weapon.getId() == id)
			{
				return weapon;
			}
		}
		
		return null;
	}
	
	@Override
	public int size()
	{
		return _weapons.size();
	}
	
	@Override
	public void clear()
	{
		_weapons.clear();
	}
}
