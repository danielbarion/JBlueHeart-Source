package gr.sr.dressmeEngine.util;

import java.text.NumberFormat;
import java.util.Locale;

import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;

public class Util
{
	public static String getItemName(int itemId)
	{
		if (itemId == Inventory.ITEM_ID_FAME)
		{
			return "Fame";
		}
		else if (itemId == Inventory.ITEM_ID_PC_BANG_POINTS)
		{
			return "PC Bang point";
		}
		else if (itemId == Inventory.ITEM_ID_CLAN_REPUTATION_SCORE)
		{
			return "Clan reputation";
		}
		else
		{
			return ItemData.getInstance().getTemplate(itemId).getName();
		}
	}
	
	public static String getItemIcon(int itemId)
	{
		return ItemData.getInstance().getTemplate(itemId).getIcon();
	}
	
	public static String formatPay(L2PcInstance player, long count, int item)
	{
		if (count > 0)
		{
			return formatAdena(count) + " " + getItemName(item);
		}
		return "Free";
	}
	
	private static NumberFormat adenaFormatter = NumberFormat.getIntegerInstance(Locale.FRANCE);
	
	/**
	 * Return amount of adena formatted with " " delimiter
	 * @param amount
	 * @return String formatted adena amount
	 */
	public static String formatAdena(long amount)
	{
		return adenaFormatter.format(amount);
	}
}
