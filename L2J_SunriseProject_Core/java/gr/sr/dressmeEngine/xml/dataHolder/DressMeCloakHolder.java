package gr.sr.dressmeEngine.xml.dataHolder;

import java.util.ArrayList;
import java.util.List;

import gr.sr.data.xml.AbstractHolder;
import gr.sr.dressmeEngine.data.DressMeCloakData;

public final class DressMeCloakHolder extends AbstractHolder
{
	private static final DressMeCloakHolder _instance = new DressMeCloakHolder();
	
	public static DressMeCloakHolder getInstance()
	{
		return _instance;
	}
	
	private final List<DressMeCloakData> _cloak = new ArrayList<>();
	
	public void addCloak(DressMeCloakData cloak)
	{
		_cloak.add(cloak);
	}
	
	public List<DressMeCloakData> getAllCloaks()
	{
		return _cloak;
	}
	
	public DressMeCloakData getCloak(int id)
	{
		for (DressMeCloakData cloak : _cloak)
		{
			if (cloak.getId() == id)
			{
				return cloak;
			}
		}
		
		return null;
	}
	
	@Override
	public int size()
	{
		return _cloak.size();
	}
	
	@Override
	public void clear()
	{
		_cloak.clear();
	}
}
