package l2r.features.auctionEngine.house.managers.holder;

public class HouseItem
{
	private final int _ownerId;
	private final int _itemId;
	private final int _count;
	private final long _salePrice;
	private final long _expirationTime;
	
	public HouseItem(int ownerId, int itemId, int count, long salePrice, long expirationTime)
	{
		_ownerId = ownerId;
		_itemId = itemId;
		_count = count;
		_salePrice = salePrice;
		_expirationTime = expirationTime;
	}
	
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getCount()
	{
		return _count;
	}
	
	public long getSalePrice()
	{
		return _salePrice;
	}
	
	public long getExpirationTime()
	{
		return _expirationTime;
	}
}