package gr.sr.dressmeEngine.xml.dataHolder;

import java.util.ArrayList;
import java.util.List;

import gr.sr.data.xml.AbstractHolder;
import gr.sr.dressmeEngine.data.DressMeShieldData;

public final class DressMeShieldHolder extends AbstractHolder
{
	private static final DressMeShieldHolder _instance = new DressMeShieldHolder();
	
	public static DressMeShieldHolder getInstance()
	{
		return _instance;
	}
	
	private final List<DressMeShieldData> _shield = new ArrayList<>();
	
	public void addShield(DressMeShieldData shield)
	{
		_shield.add(shield);
	}
	
	public List<DressMeShieldData> getAllShields()
	{
		return _shield;
	}
	
	public DressMeShieldData getShield(int id)
	{
		for (DressMeShieldData shield : _shield)
		{
			if (shield.getId() == id)
			{
				return shield;
			}
		}
		
		return null;
	}
	
	@Override
	public int size()
	{
		return _shield.size();
	}
	
	@Override
	public void clear()
	{
		_shield.clear();
	}
}
