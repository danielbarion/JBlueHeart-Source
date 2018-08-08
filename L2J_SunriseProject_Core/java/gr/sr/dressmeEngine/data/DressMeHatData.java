package gr.sr.dressmeEngine.data;

public class DressMeHatData
{
	private final int _id;
	private final int _hat;
	private final String _name;
	private final int _slot;
	private final int _priceId;
	private final long _priceCount;
	
	public DressMeHatData(int id, int hat, String name, int slot, int priceId, long priceCount)
	{
		_id = id;
		_hat = hat;
		_name = name;
		_slot = slot;
		_priceId = priceId;
		_priceCount = priceCount;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getHatId()
	{
		return _hat;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getSlot()
	{
		return _slot;
	}
	
	public int getPriceId()
	{
		return _priceId;
	}
	
	public long getPriceCount()
	{
		return _priceCount;
	}
}
