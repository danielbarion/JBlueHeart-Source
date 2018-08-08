package gr.sr.dressmeEngine.xml.dataHolder;

import java.util.ArrayList;
import java.util.List;

import gr.sr.data.xml.AbstractHolder;
import gr.sr.dressmeEngine.data.DressMeArmorData;

public final class DressMeArmorHolder extends AbstractHolder
{
	private static final DressMeArmorHolder _instance = new DressMeArmorHolder();
	
	public static DressMeArmorHolder getInstance()
	{
		return _instance;
	}
	
	private final List<DressMeArmorData> _dress = new ArrayList<>();
	
	public void addDress(DressMeArmorData armorset)
	{
		_dress.add(armorset);
	}
	
	public List<DressMeArmorData> getAllDress()
	{
		return _dress;
	}
	
	public DressMeArmorData getArmor(int id)
	{
		for (DressMeArmorData dress : _dress)
		{
			if (dress.getId() == id)
			{
				return dress;
			}
		}
		
		return null;
	}
	
	@Override
	public int size()
	{
		return _dress.size();
	}
	
	@Override
	public void clear()
	{
		_dress.clear();
	}
}
