/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.features.auctionEngine.managers;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import l2r.Config;
import l2r.features.auctionEngine.house.managers.AuctionHouseGenerator;
import l2r.features.auctionEngine.house.managers.holder.HouseItem;
import l2r.features.auctionEngine.itemcontainer.AuctionHouseItem;
import l2r.features.auctionEngine.templates.AuctionHouse;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.communitybbs.Managers.AuctionBBSManager;
import l2r.gameserver.data.sql.CharNameTable;
import l2r.gameserver.enums.ItemLocation;
import l2r.gameserver.enums.PrivateStoreType;
import l2r.gameserver.instancemanager.MailManager;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Message;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.itemcontainer.ItemContainer;
import l2r.gameserver.model.itemcontainer.Mail;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.CrystalType;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.network.serverpackets.ItemList;
import l2r.gameserver.network.serverpackets.ShowBoard;
import l2r.gameserver.network.serverpackets.StatusUpdate;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for the Auction House This does all the needed functions, checks, etc
 * @author GodFather
 */
public class AuctionHouseManager
{
	protected static final Logger _log = LoggerFactory.getLogger(AuctionHouseManager.class);
	
	private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#,##0");
	private static final DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, new Locale("en", "US"));
	
	public static final boolean NORMAL_WEAPON = false;
	public static final boolean MAGICAL_WEAPON = true;
	
	protected final Map<Integer, AuctionHouseEntrance> _entrancesById = new ConcurrentHashMap<>();
	private final Map<String, List<Integer>> _entrancesByCategory = new TreeMap<>();
	
	private final Map<Integer, AuctionHouseItem> _containersByCharId = new ConcurrentHashMap<>();
	
	protected AuctionHouseManager()
	{
		AuctionHouse.generateEntrances(_entrancesById);
		
		loadItems();
	}
	
	public void loadItems()
	{
		int itemCount = 0;
		for (HouseItem auction : AuctionHouseGenerator.getInstance().loadItems())
		{
			if (!_containersByCharId.containsKey(auction.getOwnerId()))
			{
				AuctionHouseItem container = new AuctionHouseItem(auction.getOwnerId());
				container.restore();
				_containersByCharId.put(auction.getOwnerId(), container);
			}
			
			final L2ItemInstance item = (L2ItemInstance) L2World.getInstance().findObject(auction.getItemId());
			if (item == null)
			{
				_log.error(getClass().getSimpleName() + ": The item (" + auction.getItemId() + ") doesnt exist in the auction. It exists in other location");
				continue;
			}
			
			AuctionHouse entrance = AuctionHouse.getEntranceIdFromItem(item);
			_entrancesById.get(entrance.getTopId()).addItem(new AuctionHouseEntranceItem(entrance.getTopId(), auction.getOwnerId(), item, auction.getCount(), auction.getSalePrice(), auction.getExpirationTime()));
			itemCount++;
		}
		
		_log.info(getClass().getSimpleName() + ": Loaded " + itemCount + " items on sale in the Auction House");
		
		for (AuctionHouseEntrance rank : _entrancesById.values())
		{
			if (!_entrancesByCategory.containsKey(rank.getCategory()))
			{
				_entrancesByCategory.put(rank.getCategory(), new ArrayList<Integer>());
			}
			
			_entrancesByCategory.get(rank.getCategory()).add(rank.getTopId());
		}
	}
	
	public AuctionHouseEntrance getEntranceById(int topId)
	{
		return _entrancesById.get(topId);
	}
	
	public Map<Integer, AuctionHouseEntrance> getAllEntrances()
	{
		return _entrancesById;
	}
	
	public Map<String, List<Integer>> getEntranceByCategory()
	{
		return _entrancesByCategory;
	}
	
	public String processBypass(L2PcInstance activeChar, String command)
	{
		if (command.equals("" + AuctionBBSManager.getInstance().BBS_COMMAND + ""))
		{
			try
			{
				return makeServerAuctionsHtm(activeChar, -1, null);
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("" + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				st.nextToken();
				final int idTop = Integer.parseInt(st.nextToken());
				
				return makeServerAuctionsHtm(activeChar, idTop, AuctionHouseGenerator.getInstance().processBypass(command));
			}
			catch (Exception e)
			{
				return makeServerAuctionsHtm(activeChar, -1, null);
			}
		}
		else if (command.startsWith("" + AuctionBBSManager.getInstance().BBS_COMMAND + ";my"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				st.nextToken();
				
				return makeMyAuctionsHtm(activeChar, AuctionHouseGenerator.getInstance().processBypass(command));
			}
			catch (Exception e)
			{
				return makeMyAuctionsHtm(activeChar, null);
			}
		}
		else if (command.startsWith("" + AuctionBBSManager.getInstance().BBS_COMMAND + ";purchase;"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				st.nextToken();
				final int idTop = Integer.parseInt(st.nextToken());
				
				return makePurchaseHtm(activeChar, idTop, AuctionHouseGenerator.getInstance().processBypass(command));
			}
			catch (Exception e)
			{
				return makeServerAuctionsHtm(activeChar, -1, null);
			}
		}
		
		return null;
	}
	
	public String makeServerAuctionsHtm(L2PcInstance activeChar, int topId, StatsSet set)
	{
		String content = AuctionHouseGenerator.getInstance().getServerAuctionHtml();
		if (content == null)
		{
			return "<html><body><br><br><center>Auction House Server Html is missing!!!!!</center></body></html>";
		}
		
		final int MAX_ENTRANCES_PER_PAGE = 9;
		final AuctionHouseEntrance top = getEntranceById(topId);
		final String category = (set != null ? set.getString("category", null) : null);
		final String rankSelected = (set != null ? set.getString("rank", "All") : "All");
		String searchSelected = (set != null ? set.getString("search", null) : null);
		if (searchSelected != null)
		{
			searchSelected = searchSelected.toLowerCase();
		}
		final String order = (set != null ? set.getString("order", "NameAsc") : "NameAsc");
		
		final List<AuctionHouseEntranceItem> items = new ArrayList<>();
		if (top != null)
		{
			final long currentTime = System.currentTimeMillis();
			final Iterator<AuctionHouseEntranceItem> it = top.getItems().iterator();
			AuctionHouseEntranceItem item;
			while (it.hasNext())
			{
				item = it.next();
				if (item == null)
				{
					continue;
				}
				
				if (item.isRemoved())
				{
					continue;
				}
				
				if (item.getEndTime() < currentTime)
				{
					returnItemToOwner(item);
					continue;
				}
				
				switch (rankSelected)
				{
					case "NG":
						if (item.getItemInstance().getItem().getCrystalType() != CrystalType.NONE)
						{
							continue;
						}
						break;
					case "D":
						if (item.getItemInstance().getItem().getCrystalType() != CrystalType.D)
						{
							continue;
						}
						break;
					case "C":
						if (item.getItemInstance().getItem().getCrystalType() != CrystalType.C)
						{
							continue;
						}
						break;
					case "B":
						if (item.getItemInstance().getItem().getCrystalType() != CrystalType.B)
						{
							continue;
						}
						break;
					case "A":
						if (item.getItemInstance().getItem().getCrystalType() != CrystalType.A)
						{
							continue;
						}
						break;
					case "S":
						if (item.getItemInstance().getItem().getCrystalType() != CrystalType.S)
						{
							continue;
						}
						break;
					case "S80":
						if (item.getItemInstance().getItem().getCrystalType() != CrystalType.S80)
						{
							continue;
						}
						break;
					case "S84":
						if (item.getItemInstance().getItem().getCrystalType() != CrystalType.S84)
						{
							continue;
						}
						break;
				}
				
				if ((searchSelected != null) && !item.getItemInstance().getItem().getName().toLowerCase().contains(searchSelected))
				{
					continue;
				}
				
				items.add(item);
			}
		}
		else if ((category != null) && _entrancesByCategory.containsKey(category))
		{
			final long currentTime = System.currentTimeMillis();
			AuctionHouseEntranceItem item;
			for (int allTopId : _entrancesByCategory.get(category))
			{
				final Iterator<AuctionHouseEntranceItem> it = getEntranceById(allTopId).getItems().iterator();
				
				while (it.hasNext())
				{
					item = it.next();
					if (item == null)
					{
						continue;
					}
					
					if (item.isRemoved())
					{
						continue;
					}
					
					if (item.getEndTime() < currentTime)
					{
						returnItemToOwner(item);
						continue;
					}
					
					switch (rankSelected)
					{
						case "NG":
							if (item.getItemInstance().getItem().getCrystalType() != CrystalType.NONE)
							{
								continue;
							}
							break;
						case "D":
							if (item.getItemInstance().getItem().getCrystalType() != CrystalType.D)
							{
								continue;
							}
							break;
						case "C":
							if (item.getItemInstance().getItem().getCrystalType() != CrystalType.C)
							{
								continue;
							}
							break;
						case "B":
							if (item.getItemInstance().getItem().getCrystalType() != CrystalType.B)
							{
								continue;
							}
							break;
						case "A":
							if (item.getItemInstance().getItem().getCrystalType() != CrystalType.A)
							{
								continue;
							}
							break;
						case "S":
							if (item.getItemInstance().getItem().getCrystalType() != CrystalType.S)
							{
								continue;
							}
							break;
						case "S80":
							if (item.getItemInstance().getItem().getCrystalType() != CrystalType.S80)
							{
								continue;
							}
							break;
						case "S84":
							if (item.getItemInstance().getItem().getCrystalType() != CrystalType.S84)
							{
								continue;
							}
							break;
					}
					
					if ((searchSelected != null) && !item.getItemInstance().getItem().getName().toLowerCase().contains(searchSelected))
					{
						continue;
					}
					
					items.add(item);
				}
			}
		}
		
		if ((order != null) && !items.isEmpty())
		{
			switch (order)
			{
				case "NameAsc":
					Collections.sort(items, NAME_ASC_COMPARATOR);
					break;
				case "NameDesc":
					Collections.sort(items, NAME_DESC_COMPARATOR);
					break;
				case "RankAsc":
					Collections.sort(items, RANK_ASC_COMPARATOR);
					break;
				case "RankDesc":
					Collections.sort(items, RANK_DESC_COMPARATOR);
					break;
				case "CountAsc":
					Collections.sort(items, COUNT_ASC_COMPARATOR);
					break;
				case "CountDesc":
					Collections.sort(items, COUNT_DESC_COMPARATOR);
					break;
				case "PriceAsc":
					Collections.sort(items, PRICE_ASC_COMPARATOR);
					break;
				case "PriceDesc":
					Collections.sort(items, PRICE_DESC_COMPARATOR);
					break;
			}
		}
		
		final int entrancesSize = items.size();
		final int maxPageN = (entrancesSize - 1) / MAX_ENTRANCES_PER_PAGE;
		final StringBuilder valores = new StringBuilder();
		boolean changeColor = false;
		
		content = content.replace("%itemCount%", "<font color=8a7c62>(" + entrancesSize + ")</font>");
		
		final int ITEMS_TABLE_WIDTH = 565;
		final int currentPage = (set != null ? Math.min(maxPageN, set.getInt("page", 0)) : 0);
		final int selectedItemId = (set != null ? set.getInt("selectedItemId", -1) : -1);
		for (int i = currentPage * MAX_ENTRANCES_PER_PAGE; i < ((currentPage * MAX_ENTRANCES_PER_PAGE) + MAX_ENTRANCES_PER_PAGE); i++)
		{
			if (entrancesSize > i)
			{
				if (items.get(i).getCharId() == activeChar.getObjectId())
				{
					valores.append("<table width=" + ITEMS_TABLE_WIDTH + " height=36 cellspacing=-1 bgcolor=" + (changeColor ? "171612" : "23221e") + ">");
					valores.append("<tr>");
					valores.append("<td width=42 valign=top><img src=" + items.get(i).getItemInstance().getItem().getIcon() + " width=32 height=32></td>");
				}
				else if (items.get(i).getObjectId() == selectedItemId)
				{
					valores.append("<table width=" + ITEMS_TABLE_WIDTH + " height=36 cellspacing=-1 bgcolor=999a45>");
					valores.append("<tr>");
					valores.append("<td width=42 valign=top><img src=" + items.get(i).getItemInstance().getItem().getIcon() + " width=32 height=32></td>");
				}
				else
				{
					valores.append("<table width=" + ITEMS_TABLE_WIDTH + " height=36 cellspacing=-1 bgcolor=" + (changeColor ? "171612" : "23221e") + ">");
					valores.append("<tr>");
					valores.append("<td width=42 valign=top><button value=\"\" width=32 height=32 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";selectedItemId" + items.get(i).getObjectId() + ";page" + currentPage + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "") + "\" fore=" + items.get(i).getItemInstance().getItem().getIcon() + " back=" + items.get(i).getItemInstance().getItem().getIcon() + "></td>");
				}
				
				valores.append("<td fixwidth=250>" + getCompleteItemName(items.get(i).getItemInstance(), true, false) + "</td>");
				valores.append("<td width=60 align=center>" + items.get(i).getItemInstance().getItem().getCrystalName() + "</td>");
				valores.append("<td width=100 align=center>" + DECIMAL_FORMATTER.format(items.get(i).getQuantity()).replace(".", ",") + "</td>");
				valores.append("<td width=130 align=center><font color=777978>" + DECIMAL_FORMATTER.format(items.get(i).getPrice()).replace(".", ",") + "</font></td>");
				valores.append("<td width=10 align=right></td>");
				valores.append("</tr>");
				valores.append("</table>");
				changeColor = !changeColor;
			}
			else
			{
				valores.append("<table width=" + ITEMS_TABLE_WIDTH + " height=36 cellspacing=-1 bgcolor=" + (changeColor ? "171612" : "23221e") + ">");
				valores.append("<tr>");
				valores.append("<td width=42><font color=" + (changeColor ? "151412" : "242320") + ">.</font></td>");
				/**
				 * valores.append("<td width=250></td>"); valores.append("<td width=60></td>"); valores.append("<td width=100></td>"); valores.append("<td width=130></td>"); valores.append("<td width=10></td>");
				 */
				valores.append("<td width=550></td>");
				
				valores.append("</tr>");
				valores.append("</table>");
				changeColor = !changeColor;
			}
		}
		
		final int initialPage = Math.max(0, currentPage - 10);
		final int finalPage = Math.min(maxPageN, currentPage + 10);
		valores.append("<table width=" + ITEMS_TABLE_WIDTH + " height=38 cellspacing=-1 bgcolor=" + (changeColor ? "171612" : "23221e") + ">");
		valores.append("<tr>");
		valores.append("<td width=" + ((ITEMS_TABLE_WIDTH - ((finalPage - initialPage) + ((finalPage - initialPage) * 30) + 40)) / 2) + " height=16></td>");
		valores.append("<td width=20></td>");
		for (int i = initialPage; i <= finalPage; i++)
		{
			if (i != initialPage)
			{
				valores.append("<td width=1></td>");
			}
			
			valores.append("<td width=30></td>");
		}
		valores.append("<td width=20></td>");
		valores.append("<td width=" + ((ITEMS_TABLE_WIDTH - ((finalPage - initialPage) + ((finalPage - initialPage) * 30) + 40)) / 2) + "></td>");
		valores.append("</tr>");
		valores.append("<tr>");
		if (currentPage > 0)
		{
			valores.append("<td fixheight=10 align=right><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";page0;rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "") + "\" fore=L2UI_CH3.shortcut_prev_down back=L2UI_CH3.shortcut_prev></td>");
			valores.append("<td align=right><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";page" + (currentPage - 1) + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "") + "\" fore=L2UI_CH3.shortcut_prev_down back=L2UI_CH3.shortcut_prev></td>");
		}
		else
		{
			valores.append("<td fixheight=10 align=right><button value=\"\" width=16 height=16 action=\"\" fore=L2UI_CH3.shortcut_prev back=L2UI_CH3.shortcut_prev></td>");
			valores.append("<td align=right><button value=\"\" width=16 height=16 action=\"\" fore=L2UI_CH3.shortcut_prev back=L2UI_CH3.shortcut_prev></td>");
		}
		for (int i = initialPage; i <= finalPage; i++)
		{
			if (i != initialPage)
			{
				valores.append("<td align=center valign=middle><img src=L2UI.SquareGray width=1 height=12></td>");
			}
			
			if (i == currentPage)
			{
				valores.append("<td align=center valign=middle><font color=bab43e>" + (i + 1) + "</font></td>");
			}
			else
			{
				valores.append("<td align=center valign=middle>" + (i + 1) + "</td>");
			}
		}
		if (currentPage < maxPageN)
		{
			valores.append("<td><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";page" + (currentPage + 1) + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "") + "\" fore=L2UI_CH3.shortcut_next_down back=L2UI_CH3.shortcut_next_down></td>");
			valores.append("<td><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";page" + maxPageN + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "") + "\" fore=L2UI_CH3.shortcut_next_down back=L2UI_CH3.shortcut_next_down></td>");
		}
		else
		{
			valores.append("<td><button value=\"\" width=16 height=16 action=\"\" fore=L2UI_CH3.shortcut_next back=L2UI_CH3.shortcut_next></td>");
			valores.append("<td><button value=\"\" width=16 height=16 action=\"\" fore=L2UI_CH3.shortcut_next back=L2UI_CH3.shortcut_next></td>");
		}
		valores.append("</tr>");
		valores.append("</table>");
		
		final String purchaseButtonName;
		final String purchaseButtonAction;
		if (selectedItemId >= 0)
		{
			purchaseButtonName = "Purchase";
			purchaseButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";purchase;" + topId + ";selectedItemId" + selectedItemId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";page" + (currentPage + 1) + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
		}
		else
		{
			purchaseButtonName = "Purchase";
			purchaseButtonAction = "";
		}
		
		final StringBuilder categorias = new StringBuilder();
		int freeCategoryHeight = 382;
		
		for (Entry<String, List<Integer>> tops : getEntranceByCategory().entrySet())
		{
			String catName = tops.getKey();
			switch (catName)
			{
				case "Accesory":
					catName += "  ";
					break;
				case "Armor":
					catName += "      ";
					break;
				case "Etc":
					catName += "          ";
					break;
				case "Supplies":
					catName += "   ";
					break;
				case "Weapon":
					catName += "   ";
					break;
			}
			
			if (((top != null) && tops.getKey().equalsIgnoreCase(top.getCategory())) || ((category != null) && tops.getKey().equalsIgnoreCase(category)))
			{
				categorias.append("<tr>");
				categorias.append("<td fixwidth=10 fixheight=10 valign=top><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;-1\" fore=L2UI_CH3.QuestWndMinusBtn back=L2UI_CH3.QuestWndMinusBtn></td>");
				if ((category != null) && category.equalsIgnoreCase(tops.getKey()))
				{
					categorias.append("<td width=140 valign=top><button value=\"" + catName + "\" width=60 height=13 action=\"\" fore=\"\" back=\"\"></td>");
				}
				else
				{
					categorias.append("<td width=140 valign=top><button value=\"" + catName + "\" width=60 height=13 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;-1;category" + tops.getKey() + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "") + "\" fore=\"\" back=\"\"></td>");
				}
				categorias.append("</tr>");
				
				freeCategoryHeight -= 38;
				
				for (Integer rankId : tops.getValue())
				{
					categorias.append("<tr>");
					categorias.append("<td></td>");
					categorias.append("<td>");
					categorias.append("<table width=140 cellspacing=0 bgcolor=" + (topId == rankId ? "726c42" : (changeColor ? "171612" : "23221e")) + ">");
					categorias.append("<tr>");
					if (topId == rankId)
					{
						categorias.append("<td fixheight=13><button value=\"" + getEntranceById(rankId).getTopName() + "\" width=140 height=13 action=\"\" fore=\"\" back=\"\"></td>");
					}
					else
					{
						categorias.append("<td fixheight=13><button value=\"" + getEntranceById(rankId).getTopName() + "\" width=140 height=13 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + rankId + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "") + "\" fore=\"\" back=\"\"></td>");
					}
					categorias.append("</tr>");
					categorias.append("<tr>");
					categorias.append("<td fixheight=2></td>");
					categorias.append("</tr>");
					categorias.append("</table>");
					categorias.append("</td>");
					categorias.append("</tr>");
					changeColor = !changeColor;
					
					freeCategoryHeight -= 15;
				}
				
				categorias.append("<tr>");
				categorias.append("<td fixheight=2></td>");
				categorias.append("<td></td>");
				categorias.append("</tr>");
				
				freeCategoryHeight -= 2;
			}
			else
			{
				categorias.append("<tr>");
				categorias.append("<td fixwidth=10 fixheight=10 valign=top><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;-1;category" + tops.getKey() + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "") + "\" fore=L2UI_CH3.QuestWndPlusBtn back=L2UI_CH3.QuestWndPlusBtn></td>");
				if ((category != null) && category.equalsIgnoreCase(tops.getKey()))
				{
					categorias.append("<td width=140 valign=top><button value=\"" + catName + "\" width=60 height=13 action=\"\" fore=\"\" back=\"\"></td>");
				}
				else
				{
					categorias.append("<td width=140 valign=top><button value=\"" + catName + "\" width=60 height=13 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;-1;category" + tops.getKey() + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "") + "\" fore=\"\" back=\"\"></td>");
				}
				categorias.append("</tr>");
				
				if ((top == null) && (category == null))
				{
					freeCategoryHeight -= 19;
				}
				else
				{
					freeCategoryHeight -= 16;
				}
			}
		}
		
		categorias.append("<tr>");
		categorias.append("<td height=" + Math.min(382 - (getEntranceByCategory().size() * 19) - 2, freeCategoryHeight) + "></td>");
		categorias.append("<td></td>");
		categorias.append("</tr>");
		
		final String searchButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + ";rank " + rankSelected + " ;search $search";
		
		final String applyButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + ";rank $rank " + (searchSelected != null ? ";search " + searchSelected : "");
		
		final String refreshButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";page" + currentPage + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
		
		final String itemOrderAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + ((order == null) || order.equalsIgnoreCase("NameDesc") ? ";orderNameAsc" : ";orderNameDesc") + ";page" + currentPage + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
		final String rankOrderAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + ((order == null) || order.equalsIgnoreCase("RankDesc") ? ";orderRankAsc" : ";orderRankDesc") + ";page" + currentPage + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
		final String countOrderAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + ((order == null) || order.equalsIgnoreCase("CountDesc") ? ";orderCountAsc" : ";orderCountDesc") + ";page" + currentPage + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
		final String priceOrderAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + ((order == null) || order.equalsIgnoreCase("PriceDesc") ? ";orderPriceAsc" : ";orderPriceDesc") + ";page" + currentPage + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
		
		content = content.replace("%selectedGrade%", rankSelected);
		content = content.replace("%categorias%", categorias.toString());
		content = content.replace("%tablas%", valores.toString());
		content = content.replace("%adenaCount%", "\"" + DECIMAL_FORMATTER.format(activeChar.getInventory().getAdena()).replace(".", ",") + "\"");
		content = content.replace("%applyButtonAction%", applyButtonAction);
		content = content.replace("%searchButtonAction%", searchButtonAction);
		content = content.replace("%refreshButtonAction%", refreshButtonAction);
		content = content.replace("%purchaseButtonName%", purchaseButtonName);
		content = content.replace("%purchaseButtonAction%", purchaseButtonAction);
		content = content.replace("%itemOrderAction%", itemOrderAction);
		content = content.replace("%rankOrderAction%", rankOrderAction);
		content = content.replace("%countOrderAction%", countOrderAction);
		content = content.replace("%priceOrderAction%", priceOrderAction);
		content = content.replace("%bbsCommand%", AuctionBBSManager.getInstance().BBS_COMMAND);
		return content;
	}
	
	public String makePurchaseHtm(L2PcInstance activeChar, int topId, StatsSet set) throws Exception
	{
		String content = AuctionHouseGenerator.getInstance().getPurchaseHtml();
		if (content == null)
		{
			return "<html><body><br><br><center>Auction House Purchase Html is missing!!!!!</center></body></html>";
		}
		
		if (set == null)
		{
			throw new Exception();
		}
		
		final int selectedItemId = set.getInt("selectedItemId", -1);
		if (selectedItemId < 1)
		{
			throw new Exception();
		}
		
		final String category = set.getString("category", null);
		AuctionHouseEntranceItem item = null;
		
		if ((category != null) && _entrancesByCategory.containsKey(category))
		{
			boolean isFound = false;
			for (int allTopId : _entrancesByCategory.get(category))
			{
				for (AuctionHouseEntranceItem items : getEntranceById(allTopId).getItems())
				{
					if (items.getObjectId() == selectedItemId)
					{
						item = items;
						isFound = true;
						break;
					}
				}
				if (isFound)
				{
					break;
				}
			}
		}
		else
		{
			final AuctionHouseEntrance top = getEntranceById(topId);
			if (top == null)
			{
				throw new Exception();
			}
			
			for (AuctionHouseEntranceItem items : top.getItems())
			{
				if (items.getObjectId() == selectedItemId)
				{
					item = items;
					break;
				}
			}
		}
		
		if ((item == null) || (item.getQuantity() < 1) || (item.getItemInstance() == null) || item.isRemoved())
		{
			throw new Exception();
		}
		
		final String rankSelected = set.getString("rank", "All");
		final String searchSelected = set.getString("search", null);
		final String order = set.getString("order", null);
		final int page = set.getInt("page", 0);
		final boolean isStackable = item.getItemInstance().isStackable();
		final int purchaseCount = Math.min(item.getQuantity(), set.getInt("purchaseCount", 1));
		
		if (set.getBoolean("confirm", false))
		{
			purchaseItem(activeChar, set, item, purchaseCount);
			return makeServerAuctionsHtm(activeChar, topId, set);
		}
		
		final String purchaseButtonName;
		final String purchaseButtonAction;
		final String cancelButtonName;
		final String cancelButtonAction;
		if (set.getBoolean("apply", false))
		{
			purchaseButtonName = "Confirm Purchase";
			purchaseButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";purchase;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";confirm;selectedItemId" + selectedItemId + ";purchaseCount " + purchaseCount + ";page" + page + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
			cancelButtonName = "Edit Purchase";
			cancelButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";purchase;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";selectedItemId" + selectedItemId + ";page" + page + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
		}
		else
		{
			purchaseButtonName = "Apply";
			if (!isStackable || (item.getQuantity() == 1))
			{
				purchaseButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";purchase;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";apply;selectedItemId" + selectedItemId + ";purchaseCount 1;page" + page + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
			}
			else
			{
				purchaseButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";purchase;" + topId + (category != null ? ";category" + category : "") + (order != null ? ";order" + order : "") + ";apply;selectedItemId" + selectedItemId + ";purchaseCount $quantity ;page" + page + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
			}
			cancelButtonName = "Cancel Purchase";
			cancelButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";server;" + topId + (category != null ? ";category" + category : "") + ";selectedItemId" + selectedItemId + ";page" + page + ";rank " + rankSelected + (searchSelected != null ? ";search " + searchSelected : "");
		}
		
		final StringBuilder itemSelected = new StringBuilder();
		itemSelected.append("<tr>");
		itemSelected.append("<td width=35 valign=middle><img src=" + item.getItemInstance().getItem().getIcon() + " width=32 height=32></td>");
		itemSelected.append("<td width=5></td>");
		itemSelected.append("<td fixwidth=225 align=center>" + getCompleteItemName(item.getItemInstance(), true, false) + (item.getQuantity() > 1 ? " X" + item.getQuantity() : "") + "</td>");
		itemSelected.append("</tr>");
		
		final String quantity;
		final String salePrice;
		final String totalPrice;
		
		if (set.getBoolean("apply", false))
		{
			quantity = "<button value=\"" + DECIMAL_FORMATTER.format(purchaseCount).replace(".", ",") + "\" width=160 height=20 action=\"\" fore=L2UI_CT1.Windows_DF_TooltipBG back=L2UI_CT1.Windows_DF_TooltipBG>";
			salePrice = DECIMAL_FORMATTER.format(item.getPrice()).replace(".", ",");
			totalPrice = DECIMAL_FORMATTER.format(purchaseCount * item.getPrice()).replace(".", ",");
		}
		else
		{
			if (!isStackable || (item.getQuantity() == 1))
			{
				quantity = "<button value=\"1\" width=160 height=20 action=\"\" fore=L2UI_CT1.Windows_DF_TooltipBG back=L2UI_CT1.Windows_DF_TooltipBG>";
			}
			else
			{
				quantity = "<edit var=quantity width=160 height=10>";
			}
			salePrice = DECIMAL_FORMATTER.format(item.getPrice()).replace(".", ",");
			totalPrice = DECIMAL_FORMATTER.format(item.getPrice()).replace(".", ",");
		}
		
		final String itemName = getCompleteItemName(item.getItemInstance(), true, true);
		final String sellerName = CharNameTable.getInstance().getNameById(item.getCharId());
		
		final String itemGrade;
		switch (item.getItemInstance().getItem().getCrystalType())
		{
			case D:
				itemGrade = "<td fixwidth=16 fixheight=16><img src=L2UI_CT1.Icon_DF_ItemGrade_D width=16 height=16></td>";
				break;
			case C:
				itemGrade = "<td fixwidth=16 fixheight=16><img src=L2UI_CT1.Icon_DF_ItemGrade_C width=16 height=16></td>";
				break;
			case B:
				itemGrade = "<td fixwidth=16 fixheight=16><img src=L2UI_CT1.Icon_DF_ItemGrade_B width=16 height=16></td>";
				break;
			case A:
				itemGrade = "<td fixwidth=16 fixheight=16><img src=L2UI_CT1.Icon_DF_ItemGrade_A width=16 height=16></td>";
				break;
			case S:
				itemGrade = "<td fixwidth=16 fixheight=16><img src=L2UI_CT1.Icon_DF_ItemGrade_S width=16 height=16></td>";
				break;
			case S80:
				itemGrade = "<td fixwidth=16 fixheight=16><img src=L2UI_CT1.Icon_DF_ItemGrade_S width=16 height=16></td><td fixwidth=16 fixheight=16><img src=L2UI_CT1.Icon_DF_ItemGrade_80 width=16 height=16></td>";
				break;
			case S84:
				itemGrade = "<td fixwidth=16 fixheight=16><img src=L2UI_CT1.Icon_DF_ItemGrade_S width=16 height=16></td><td fixwidth=16 fixheight=16><img src=L2UI_CT1.Icon_DF_ItemGrade_84 width=16 height=16></td>";
				break;
			default:
				itemGrade = "<td></td>";
				break;
		}
		
		final String itemStats;
		if (item.getItemInstance().isWeapon())
		{
			itemStats = "<tr><td height=25 align=center><font color=888a89>P. Atk:</font> <font color=a09675>" + (int) item.getItemInstance().getItem().getValueFromStatFunc(activeChar, item.getItemInstance(), "pAtk", "0x08") + "</font></td></tr>" + "<tr><td height=25 align=center><font color=888a89>M. Atk:</font> <font color=a09675>" + (int) item.getItemInstance().getItem().getValueFromStatFunc(activeChar, item.getItemInstance(), "mAtk", "0x08") + "</font></td></tr>";
		}
		else if (item.getItemInstance().getItem().isJewel())
		{
			itemStats = "<tr><td height=25 align=center><font color=888a89>M. Def:</font> <font color=a09675>" + (int) item.getItemInstance().getItem().getValueFromStatFunc(activeChar, item.getItemInstance(), "mDef", "0x10") + "</font></td></tr>";
		}
		else if (item.getItemInstance().isArmor())
		{
			itemStats = "<tr><td height=25 align=center><font color=888a89>P. Def:</font> <font color=a09675>" + (int) item.getItemInstance().getItem().getValueFromStatFunc(activeChar, item.getItemInstance(), "pDef", "0x10") + "</font></td></tr>";
		}
		else
		{
			itemStats = "";
		}
		
		final StringBuilder itemElements = new StringBuilder();
		if (item.getItemInstance().getAttackElementPower() > 0)
		{
			itemElements.append("<tr><td height=20 align=center>");
			itemElements.append(Elementals.getElementName(item.getItemInstance().getAttackElementType()) + " P. Atk " + item.getItemInstance().getAttackElementPower());
			itemElements.append("</td></tr>");
			itemElements.append("<tr><td height=10 align=center>");
			switch (item.getItemInstance().getAttackElementType())
			{
				case Elementals.WATER:
					itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Water width=100 height=8>");
					break;
				case Elementals.DARK:
					itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Dark width=100 height=8>");
					break;
				case Elementals.EARTH:
					itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Earth width=100 height=8>");
					break;
				case Elementals.FIRE:
					itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Fire width=100 height=8>");
					break;
				case Elementals.HOLY:
					itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Divine width=100 height=8>");
					break;
				case Elementals.WIND:
					itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Wind width=100 height=8>");
					break;
			}
			itemElements.append("</td></tr>");
			itemElements.append("<tr><td height=5></td></tr>");
		}
		for (byte i = 0; i < 6; i++)
		{
			if (item.getItemInstance().getElementDefAttr(i) > 0)
			{
				itemElements.append("<tr><td height=20 align=center>");
				itemElements.append(Elementals.getElementName(i) + " P. Def " + item.getItemInstance().getElementDefAttr(i));
				itemElements.append("</td></tr>");
				itemElements.append("<tr><td height=10 align=center>");
				switch (i)
				{
					case Elementals.WATER:
						itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Water width=100 height=8>");
						break;
					case Elementals.DARK:
						itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Dark width=100 height=8>");
						break;
					case Elementals.EARTH:
						itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Earth width=100 height=8>");
						break;
					case Elementals.FIRE:
						itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Fire width=100 height=8>");
						break;
					case Elementals.HOLY:
						itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Divine width=100 height=8>");
						break;
					case Elementals.WIND:
						itemElements.append("<img src=L2UI_CT1.Gauge_DF_Attribute_Wind width=100 height=8>");
						break;
				}
				itemElements.append("</td></tr>");
				itemElements.append("<tr><td height=5></td></tr>");
			}
		}
		
		content = content.replace("%itemSelected%", itemSelected.toString());
		content = content.replace("%adenaCount%", DECIMAL_FORMATTER.format(activeChar.getInventory().getAdena()).replace(".", ","));
		content = content.replace("%cancelButtonName%", cancelButtonName);
		content = content.replace("%cancelButtonAction%", cancelButtonAction);
		content = content.replace("%purchaseButtonName%", purchaseButtonName);
		content = content.replace("%purchaseButtonAction%", purchaseButtonAction);
		content = content.replace("%quantity%", quantity);
		content = content.replace("%salePrice%", salePrice);
		content = content.replace("%totalPrice%", totalPrice);
		content = content.replace("%itemName%", itemName);
		content = content.replace("%itemGrade%", itemGrade);
		content = content.replace("%sellerName%", (sellerName != null ? sellerName : ""));
		content = content.replace("%itemStats%", itemStats);
		content = content.replace("%itemElements%", itemElements.toString());
		return content;
	}
	
	public String makeMyAuctionsHtm(L2PcInstance activeChar, StatsSet set)
	{
		String content = AuctionHouseGenerator.getInstance().getMyAuctionHtml();
		if (content == null)
		{
			return "<html><body><br><br><center>Auction House Player Html is missing!!!!!</center></body></html>";
		}
		
		if ((set != null) && set.getBoolean("create", false))
		{
			createNewAuction(activeChar, set);
		}
		else if ((set != null) && (set.getInt("cancelConfirm", -1) >= 0))
		{
			cancelAuction(activeChar, set);
		}
		
		final int MAX_ITEMS_PER_LINE = 5;
		final StringBuilder valores = new StringBuilder();
		boolean changeColor = false;
		
		final List<AuctionHouseEntranceItem> items = new ArrayList<>();
		for (AuctionHouseEntrance entrance : getAllEntrances().values())
		{
			for (AuctionHouseEntranceItem item : entrance.getItems())
			{
				if ((item != null) && (item.getCharId() == activeChar.getObjectId()) && !item.isRemoved())
				{
					items.add(item);
				}
			}
		}
		
		content = content.replace("%itemCount%", "<font color=8a7c62>(" + items.size() + "/10)</font>");
		
		final int selectedAuction = (set != null ? set.getInt("selectedAuction", -1) : -1);
		for (int i = 0; i < 10; i++)
		{
			if (items.size() > i)
			{
				if (items.get(i).getObjectId() == selectedAuction)
				{
					valores.append("<table height=36 cellspacing=-1 bgcolor=999a45>");
					valores.append("<tr>");
					valores.append("<td width=38 valign=middle><img src=" + items.get(i).getItemInstance().getItem().getIcon() + " width=32 height=32></td>");
				}
				else
				{
					valores.append("<table height=36 valign=middle cellspacing=-1 bgcolor=" + (changeColor ? "171612" : "23221e") + ">");
					valores.append("<tr>");
					valores.append("<td width=38 valign=middle><button value=\"\" width=32 height=32 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;selectedAuction" + items.get(i).getObjectId() + "\" fore=" + items.get(i).getItemInstance().getItem().getIcon() + " back=" + items.get(i).getItemInstance().getItem().getIcon() + "></td>");
				}
				valores.append("<td fixwidth=192>" + getCompleteItemName(items.get(i).getItemInstance(), false, true) + "</td>");
				valores.append("<td width=60 align=center>" + items.get(i).getItemInstance().getItem().getCrystalName() + "</td>");
				valores.append("<td width=80 align=center>" + DECIMAL_FORMATTER.format(items.get(i).getQuantity()).replace(".", ",") + "</td>");
				valores.append("<td width=100 align=center>" + DECIMAL_FORMATTER.format(items.get(i).getPrice()).replace(".", ",") + "</td>");
				valores.append("<td width=80 align=center>" + items.get(i).getRemainingTimeString() + "</td>");
				valores.append("</tr>");
				valores.append("</table>");
				changeColor = !changeColor;
			}
			else
			{
				valores.append("<table height=36 cellspacing=-1 bgcolor=" + (changeColor ? "171612" : "23221e") + ">");
				valores.append("<tr>");
				valores.append("<td width=38><font color=" + (changeColor ? "151412" : "242320") + ">.</font></td>");
				valores.append("<td width=192></td>");
				valores.append("<td width=60></td>");
				valores.append("<td width=80></td>");
				valores.append("<td width=100></td>");
				valores.append("<td width=80></td>");
				valores.append("</tr>");
				valores.append("</table>");
				changeColor = !changeColor;
			}
		}
		
		final String cancelButtonName;
		final String cancelButtonAction;
		if (selectedAuction >= 0)
		{
			if ((set != null) && (set.getInt("cancel", -1) >= 0))
			{
				cancelButtonName = "Confirm Cancel";
				cancelButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;cancelConfirm" + selectedAuction;
			}
			else
			{
				cancelButtonName = "Cancel Auction";
				cancelButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;selectedAuction" + selectedAuction + ";cancel" + selectedAuction;
			}
		}
		else
		{
			cancelButtonName = "Cancel Auction";
			cancelButtonAction = "";
		}
		
		final StringBuilder inventario = new StringBuilder();
		
		final List<L2ItemInstance> sellList = new ArrayList<>();
		for (L2ItemInstance item : activeChar.getInventory().getItems())
		{
			if (!item.isEquipped() && item.isSellable() && item.isTradeable() && (item.getId() != Inventory.ADENA_ID) && ((activeChar.getSummon() == null) || (item.getObjectId() != activeChar.getSummon().getControlObjectId())))
			{
				sellList.add(item);
			}
		}
		
		final int maxPage = (int) Math.ceil((double) sellList.size() / MAX_ITEMS_PER_LINE) - 3;
		int selectedQuantity = (set != null ? set.getInt("quantity", 0) : 0);
		int selectedItemId = (set != null ? set.getInt("itemId", 0) : 0);
		long selectedSalePrice = (set != null ? set.getLong("saleprice", 0) : 0);
		int currentPage = (set != null ? Math.min(maxPage, set.getInt("page", -1)) : -1);
		int selectedItemIndex = 0;
		String itemName = "";
		String itemIcon = "<img src=\"\" width=32 height=32>";
		long itemCount = 1;
		boolean isStackable = false;
		
		if ((currentPage > -1) && ((currentPage * MAX_ITEMS_PER_LINE) <= sellList.size()) && (selectedItemId < 1))
		{
			selectedItemIndex = currentPage * MAX_ITEMS_PER_LINE;
			itemIcon = "<img src=" + sellList.get(selectedItemIndex).getItem().getIcon() + " width=32 height=32>";
			itemName = getCompleteItemName(sellList.get(selectedItemIndex), true, false);
			itemCount = sellList.get(selectedItemIndex).getCount();
			isStackable = sellList.get(selectedItemIndex).isStackable();
			selectedItemId = sellList.get(selectedItemIndex).getObjectId();
			
			if (selectedQuantity > itemCount)
			{
				selectedQuantity = (int) itemCount;
			}
			
			if (selectedSalePrice < (sellList.get(selectedItemIndex).getReferencePrice() / 2))
			{
				selectedSalePrice = sellList.get(selectedItemIndex).getReferencePrice() / 2;
			}
		}
		else
		{
			for (int i = 0; i < sellList.size(); i++)
			{
				if ((selectedItemId == 0) || (sellList.get(i).getObjectId() == selectedItemId))
				{
					selectedItemIndex = i;
					itemIcon = "<img src=" + sellList.get(i).getItem().getIcon() + " width=32 height=32>";
					itemName = getCompleteItemName(sellList.get(i), true, false);
					itemCount = sellList.get(i).getCount();
					isStackable = sellList.get(i).isStackable();
					if ((currentPage < 0) || ((currentPage * MAX_ITEMS_PER_LINE) > sellList.size()))
					{
						currentPage = Math.max(0, Math.min(maxPage, i / MAX_ITEMS_PER_LINE));
					}
					selectedItemId = sellList.get(i).getObjectId();
					
					if (selectedQuantity > itemCount)
					{
						selectedQuantity = (int) itemCount;
					}
					
					if (selectedSalePrice < (sellList.get(i).getReferencePrice() / 2))
					{
						selectedSalePrice = sellList.get(i).getReferencePrice() / 2;
					}
					break;
				}
			}
		}
		if (currentPage < 0)
		{
			currentPage = 0;
		}
		
		final int startIndex = currentPage * MAX_ITEMS_PER_LINE;
		for (int i = startIndex; i < sellList.size(); i++)
		{
			if ((i - startIndex) >= (MAX_ITEMS_PER_LINE * 3))
			{
				break;
			}
			
			if ((i % MAX_ITEMS_PER_LINE) == 0)
			{
				inventario.append("<tr>");
			}
			
			inventario.append("<td fixwidth=42 height=42 valign=top>");
			if (i == selectedItemIndex)
			{
				inventario.append("<table width=42 height=42 cellpadding=5 cellspacing=0 background=BranchSys.br_icon_limited><tr><td align=center valign=middle>");
				inventario.append("<button value=\"\" width=32 height=32 action=\"\" fore=" + sellList.get(i).getItem().getIcon() + " back=" + sellList.get(i).getItem().getIcon() + ">");
				inventario.append("</td></tr></table>");
			}
			else
			{
				inventario.append("<table width=42 height=42 cellpadding=5 cellspacing=0><tr><td align=center valign=middle>");
				inventario.append("<button value=\"\" width=32 height=32 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;itemId" + sellList.get(i).getObjectId() + ";page" + currentPage + "\" fore=" + sellList.get(i).getItem().getIcon() + " back=" + sellList.get(i).getItem().getIcon() + ">");
				inventario.append("</td></tr></table>");
			}
			inventario.append("</td>");
			
			if ((i + 1) == sellList.size())
			{
				for (int z = 0; z < Math.round(MAX_ITEMS_PER_LINE - (i % MAX_ITEMS_PER_LINE) - 1); z++)
				{
					inventario.append("<td fixwidth=42 height=42 valign=top><button value=\"\" width=32 height=32 action=\"\" fore=\"\" back=\"\"></td>");
				}
			}
			
			if ((((i + 1) % MAX_ITEMS_PER_LINE) == 0) || ((i - startIndex) >= (MAX_ITEMS_PER_LINE * 3)))
			{
				inventario.append("</tr>");
			}
			
			if ((i + 1) == sellList.size())
			{
				if (sellList.size() <= MAX_ITEMS_PER_LINE)
				{
					inventario.append("<tr>");
					inventario.append("<td fixwidth=42 height=42 valign=top></td>");
					inventario.append("</tr>");
					inventario.append("<tr>");
					inventario.append("<td fixwidth=42 height=42 valign=top></td>");
					inventario.append("</tr>");
				}
				else if (sellList.size() <= (MAX_ITEMS_PER_LINE * 2))
				{
					inventario.append("<tr>");
					inventario.append("<td fixwidth=42 height=42 valign=top></td>");
					inventario.append("</tr>");
				}
			}
		}
		
		final StringBuilder flechasInventario = new StringBuilder();
		flechasInventario.append("<table cellpadding=-2 cellspacing=3>");
		flechasInventario.append("<tr>");
		flechasInventario.append("<td width=4></td>");
		if (currentPage < 1)
		{
			flechasInventario.append("<td fixheight=16><button value=\"\" width=16 height=16 action=\"\" fore=L2UI_CH3.joypad_shortcut back=L2UI_CH3.joypad_shortcut></td>");
		}
		else
		{
			flechasInventario.append("<td fixheight=16><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;page0" + (selectedItemId > 0 ? ";itemId" + selectedItemId : "") + "\" fore=L2UI_CH3.joypad_shortcut_over back=L2UI_CH3.joypad_shortcut_over></td>");
		}
		flechasInventario.append("</tr>");
		flechasInventario.append("<tr>");
		flechasInventario.append("<td></td>");
		if (currentPage < 1)
		{
			flechasInventario.append("<td fixheight=16><button value=\"\" width=16 height=16 action=\"\" fore=L2UI_CH3.shortcut_nextv back=L2UI_CH3.shortcut_nextv></td>");
		}
		else
		{
			flechasInventario.append("<td fixheight=16><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;page" + (currentPage - 1) + (selectedItemId > 0 ? ";itemId" + selectedItemId : "") + "\" fore=L2UI_CH3.shortcut_nextv_over back=L2UI_CH3.shortcut_nextv_over></td>");
		}
		flechasInventario.append("</tr>");
		flechasInventario.append("<tr>");
		flechasInventario.append("<td></td>");
		flechasInventario.append("<td height=58></td>");
		flechasInventario.append("</tr>");
		flechasInventario.append("<tr>");
		flechasInventario.append("<td></td>");
		if (currentPage >= maxPage)
		{
			flechasInventario.append("<td fixheight=16><button value=\"\" width=16 height=16 action=\"\" fore=L2UI_CH3.shortcut_prevv back=L2UI_CH3.shortcut_prevv></td>");
		}
		else
		{
			flechasInventario.append("<td fixheight=16><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;page" + (currentPage + 1) + (selectedItemId > 0 ? ";itemId" + selectedItemId : "") + "\" fore=L2UI_CH3.shortcut_prevv_over back=L2UI_CH3.shortcut_prevv_over></td>");
		}
		flechasInventario.append("</tr>");
		flechasInventario.append("<tr>");
		flechasInventario.append("<td></td>");
		if (currentPage >= maxPage)
		{
			flechasInventario.append("<td fixheight=16><button value=\"\" width=16 height=16 action=\"\" fore=L2UI_CH3.joypad_shortcut back=L2UI_CH3.joypad_shortcut></td>");
		}
		else
		{
			flechasInventario.append("<td fixheight=16><button value=\"\" width=16 height=16 action=\"bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;page" + maxPage + (selectedItemId > 0 ? ";itemId" + selectedItemId : "") + "\" fore=L2UI_CH3.joypad_shortcut_over back=L2UI_CH3.joypad_shortcut_over></td>");
		}
		flechasInventario.append("</tr>");
		flechasInventario.append("</table>");
		
		final StringBuilder itemSelected = new StringBuilder();
		itemSelected.append("<tr>");
		itemSelected.append("<td><table width=60 height=48 cellpadding=7 cellspacing=0 background=L2UI_ct1.ShortcutWnd_DF_Select><tr><td align=center valign=middle>" + itemIcon + "</td></tr></table></td>");
		itemSelected.append("<td width=5></td>");
		itemSelected.append("<td fixwidth=120>" + itemName + (itemCount > 1 ? " X" + itemCount : "") + "</td>");
		itemSelected.append("</tr>");
		
		final String auctionButtonName;
		final String auctionButtonAction;
		final String quantity;
		final String salePrice;
		final String duration;
		final String totalPrice;
		final String saleFee;
		
		if ((selectedItemId > 0) && (set != null) && set.getBoolean("apply", false))
		{
			auctionButtonName = "Auction Item";
			auctionButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;itemId" + selectedItemId + ";create;quantity " + selectedQuantity + " ;saleprice " + selectedSalePrice + " ;duration " + set.getLong("duration", 1);
			
			quantity = "<button value=\"" + DECIMAL_FORMATTER.format(selectedQuantity).replace(".", ",") + "\" width=120 height=20 action=\"\" fore=L2UI_CT1.Windows_DF_TooltipBG back=L2UI_CT1.Windows_DF_TooltipBG>";
			salePrice = "<button value=\"" + DECIMAL_FORMATTER.format(selectedSalePrice).replace(".", ",") + "\" width=120 height=20 action=\"\" fore=L2UI_CT1.Windows_DF_TooltipBG back=L2UI_CT1.Windows_DF_TooltipBG>";
			duration = "<button value=\"" + set.getInt("duration", 1) + " day(s)\" width=120 height=20 action=\"\" fore=L2UI_CT1.Windows_DF_TooltipBG back=L2UI_CT1.Windows_DF_TooltipBG>";
			totalPrice = DECIMAL_FORMATTER.format(selectedQuantity * selectedSalePrice).replace(".", ",");
			
			saleFee = DECIMAL_FORMATTER.format(Math.max(10000, selectedQuantity * selectedSalePrice * set.getInt("duration", 1) * Config.AUCTION_HOUSE_SALE_FEE)).replace(".", ",");
		}
		else if (selectedItemId > 0)
		{
			auctionButtonName = "Apply";
			
			if (!isStackable || (itemCount == 1))
			{
				auctionButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;itemId" + selectedItemId + ";apply;quantity 1 ;saleprice $saleprice ;duration $duration";
				quantity = "<button value=\"1\" width=120 height=20 action=\"\" fore=L2UI_CT1.Windows_DF_TooltipBG back=L2UI_CT1.Windows_DF_TooltipBG>";
			}
			else
			{
				auctionButtonAction = "bypass " + AuctionBBSManager.getInstance().BBS_COMMAND + ";my;itemId" + selectedItemId + ";apply;quantity $quantity ;saleprice $saleprice ;duration $duration";
				quantity = "<edit var=quantity width=120 height=10>";
			}
			salePrice = "<edit var=saleprice width=120 height=10>";
			duration = "<combobox width=120 height=10 var=duration list=\"1 day(s);2 day(s);3 day(s);4 day(s);5 day(s);6 day(s);7 day(s);8 day(s);9 day(s)\">";
			totalPrice = "0";
			saleFee = "10,000";
		}
		else
		{
			auctionButtonName = "Auction Item";
			auctionButtonAction = "";
			quantity = "<edit var=quantity width=120 height=10>";
			salePrice = "<edit var=saleprice width=120 height=10>";
			duration = "<combobox width=120 height=10 var=duration list=\"1 day(s);2 day(s);3 day(s);4 day(s);5 day(s);6 day(s);7 day(s);8 day(s);9 day(s)\">";
			totalPrice = "0";
			saleFee = "10,000";
		}
		
		content = content.replace("%inventario%", inventario.toString());
		content = content.replace("%inventarioFlechas%", flechasInventario.toString());
		content = content.replace("%itemSelected%", itemSelected.toString());
		content = content.replace("%tablas%", valores.toString());
		content = content.replace("%adenaCount%", DECIMAL_FORMATTER.format(activeChar.getInventory().getAdena()).replace(".", ","));
		content = content.replace("%auctionButtonName%", auctionButtonName);
		content = content.replace("%auctionButtonAction%", auctionButtonAction);
		content = content.replace("%cancelButtonName%", cancelButtonName);
		content = content.replace("%cancelButtonAction%", cancelButtonAction);
		content = content.replace("%quantity%", quantity);
		content = content.replace("%salePrice%", salePrice);
		content = content.replace("%duration%", duration);
		content = content.replace("%totalPrice%", totalPrice);
		content = content.replace("%saleFee%", saleFee);
		content = content.replace("%bbsCommand%", AuctionBBSManager.getInstance().BBS_COMMAND);
		return content;
	}
	
	private synchronized void createNewAuction(L2PcInstance activeChar, StatsSet set)
	{
		if (!activeChar.getClient().getFloodProtectors().getTransaction().tryPerformAction("createAuction"))
		{
			return;
		}
		
		if (!activeChar.getAccessLevel().allowTransaction())
		{
			activeChar.sendMessage("Transactions are disabled for your Access Level");
			return;
		}
		
		if (activeChar.getActiveTradeList() != null)
		{
			activeChar.sendMessage("You cant create an auction while having an active exchange");
			return;
		}
		
		if (activeChar.isEnchanting())
		{
			activeChar.sendMessage("You cant create an auction while enchanting");
			return;
		}
		
		if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			activeChar.sendMessage("You cant create an auction while in Private Store");
			return;
		}
		
		if (activeChar.isJailed())
		{
			activeChar.sendMessage("You cant create an auction while in Jail");
			return;
		}
		
		if (activeChar.isTeleporting() || activeChar.isCastingNow() || activeChar.isAttackingNow())
		{
			activeChar.sendMessage("You cant create an auction while casting/attacking");
			return;
		}
		
		final int itemId = set.getInt("itemId", 0);
		if (itemId < 1)
		{
			return;
		}
		
		final int quantity = set.getInt("quantity", 0);
		if ((quantity < 1) || (quantity > 1000000))
		{
			activeChar.sendMessage("Wrong Auction Item Quantity");
			return;
		}
		
		final long salePrice = set.getLong("saleprice", 0);
		if ((salePrice < 1) || (salePrice > 10000000000L))
		{
			activeChar.sendMessage("Wrong Auction Sale Price");
			return;
		}
		
		final int duration = set.getInt("duration", 1);
		if ((duration < 1) || (duration > 9))
		{
			activeChar.sendMessage("Wrong Auction Duration");
			return;
		}
		
		final long saleFee = (long) Math.max(10000, quantity * salePrice * duration * Config.AUCTION_HOUSE_SALE_FEE);
		if (activeChar.getAdena() < saleFee)
		{
			activeChar.sendMessage("Not enough adena to pay the Auction Fee");
			return;
		}
		
		final L2ItemInstance item = activeChar.checkItemManipulation(itemId, quantity, "Auction");
		if ((item == null) || !item.isSellable() || !item.isTradeable() || item.isEquipped())
		{
			activeChar.sendMessage("Not a valid item for Auction");
			return;
		}
		
		synchronized (_entrancesById)
		{
			int auctionedItemsN = 0;
			for (AuctionHouseEntrance entrance : _entrancesById.values())
			{
				for (AuctionHouseEntranceItem it : entrance.getItems())
				{
					if (it.getObjectId() == item.getObjectId())
					{
						activeChar.sendMessage("This item is already in auction");
						return;
					}
					
					if (it.getCharId() == activeChar.getObjectId())
					{
						auctionedItemsN++;
						
						// Stackables items can only be made once sold, because they share the objectId as stacked. So there can only be a sale of a stackable
						if (item.isStackable() && (it.getItemInstance().getId() == item.getId()))
						{
							activeChar.sendMessage("You cannot auction this item because its an stackable item and its already in auction by you");
							activeChar.sendMessage("If you still want to auction this item, you must cancel your sale first");
							return;
						}
					}
				}
			}
			
			if (auctionedItemsN >= 10)
			{
				activeChar.sendMessage("You can only have 10 auctions maximum at the same time");
				return;
			}
			
			if (!activeChar.reduceAdena("Auction", saleFee, null, true))
			{
				activeChar.sendMessage("Not enough adena to pay the Auction Fee");
				return;
			}
			
			final AuctionHouseItem container;
			if (!_containersByCharId.containsKey(activeChar.getObjectId()))
			{
				container = new AuctionHouseItem(activeChar.getObjectId());
				_containersByCharId.put(activeChar.getObjectId(), container);
			}
			else
			{
				container = _containersByCharId.get(activeChar.getObjectId());
			}
			
			final L2ItemInstance newItem = activeChar.getInventory().transferItem("AuctionHouse", itemId, quantity, container, activeChar, "AuctionHouse");
			if (newItem == null)
			{
				activeChar.sendMessage("An error has ocurred while recieving your item. Please report to a Game Master");
				_log.warn("Error recieving item for auction house of " + activeChar.getName() + " (newitem == null)");
				return;
			}
			newItem.setItemLocation(container.getBaseLocation());
			
			final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
			if (playerIU != null)
			{
				if ((item.getCount() > 0) && (item != newItem))
				{
					playerIU.addModifiedItem(item);
				}
				else
				{
					playerIU.addRemovedItem(item);
				}
			}
			
			if (playerIU != null)
			{
				activeChar.sendPacket(playerIU);
			}
			else
			{
				activeChar.sendPacket(new ItemList(activeChar, false));
			}
			
			final StatusUpdate su = new StatusUpdate(activeChar);
			su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
			activeChar.sendPacket(su);
			
			addNewAuctionToDB(activeChar, newItem, quantity, salePrice, duration);
		}
	}
	
	private synchronized void cancelAuction(L2PcInstance activeChar, StatsSet set)
	{
		if (!activeChar.getClient().getFloodProtectors().getTransaction().tryPerformAction("cancelAuction"))
		{
			return;
		}
		
		if (!activeChar.getAccessLevel().allowTransaction())
		{
			activeChar.sendMessage("Transactions are disabled for your Access Level");
			return;
		}
		
		if (activeChar.getActiveTradeList() != null)
		{
			activeChar.sendMessage("You cant cancel an auction while having an active exchange");
			return;
		}
		
		if (activeChar.isEnchanting())
		{
			activeChar.sendMessage("You cant cancel an auction while enchanting");
			return;
		}
		
		if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			activeChar.sendMessage("You cant cancel an auction while in Private Store");
			return;
		}
		
		if (activeChar.isJailed())
		{
			activeChar.sendMessage("You cant cancel an auction while in Jail");
			return;
		}
		
		if (activeChar.isTeleporting() || activeChar.isCastingNow() || activeChar.isAttackingNow())
		{
			activeChar.sendMessage("You cant cancel an auction while casting/attacking");
			return;
		}
		
		synchronized (_entrancesById)
		{
			final int cancelObjectId = set.getInt("cancelConfirm", -1);
			for (AuctionHouseEntrance entrance : _entrancesById.values())
			{
				for (AuctionHouseEntranceItem item : entrance.getItems())
				{
					if (item.getCharId() != activeChar.getObjectId())
					{
						continue;
					}
					
					if (item.getObjectId() != cancelObjectId)
					{
						continue;
					}
					
					final ItemContainer container = _containersByCharId.get(activeChar.getObjectId());
					if ((container == null) || (container.getSize() == 0))
					{
						activeChar.sendMessage("You cant cancel this auction. Something is missing");
						return;
					}
					
					if (item.getItemInstance().getOwnerId() != activeChar.getObjectId())
					{
						Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get not own item from cancelled attachment!", Config.DEFAULT_PUNISH);
						return;
					}
					
					if (item.getItemInstance().getItemLocation() != ItemLocation.AUCTION)
					{
						Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get items not from mail !", Config.DEFAULT_PUNISH);
						return;
					}
					
					final long weight = item.getItemInstance().getCount() * item.getItemInstance().getItem().getWeight();
					int slots = 0;
					if (!item.getItemInstance().isStackable())
					{
						slots += item.getItemInstance().getCount();
					}
					else if (activeChar.getInventory().getItemByItemId(item.getItemInstance().getId()) == null)
					{
						slots++;
					}
					
					if (!activeChar.getInventory().validateCapacity(slots))
					{
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_CANCEL_INVENTORY_FULL));
						return;
					}
					
					if (!activeChar.getInventory().validateWeight(weight))
					{
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_CANCEL_INVENTORY_FULL));
						return;
					}
					
					final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
					
					final long count = item.getItemInstance().getCount();
					final L2ItemInstance newItem = container.transferItem(container.getName(), item.getObjectId(), count, activeChar.getInventory(), activeChar, null);
					if (newItem == null)
					{
						activeChar.sendMessage("You cant cancel this auction. Something is wrong. Report to a Game Master");
						return;
					}
					
					if (playerIU != null)
					{
						if (newItem.getCount() > count)
						{
							playerIU.addModifiedItem(newItem);
						}
						else
						{
							playerIU.addNewItem(newItem);
						}
					}
					
					final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_ACQUIRED_S2_S1);
					sm.addItemName(item.getItemInstance().getId());
					sm.addLong(count);
					activeChar.sendPacket(sm);
					
					if (playerIU != null)
					{
						activeChar.sendPacket(playerIU);
					}
					else
					{
						activeChar.sendPacket(new ItemList(activeChar, false));
					}
					
					final StatusUpdate su = new StatusUpdate(activeChar);
					su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
					activeChar.sendPacket(su);
					
					modifyAuctionItemCountToDB(item, -1);
					
					activeChar.sendMessage("Auction cancelled succesfully");
					
					set.set("itemId", 0);
					set.set("page", 0);
					return;
				}
			}
		}
		
		activeChar.sendMessage("The selected auction couldn't be cancelled");
	}
	
	private synchronized void purchaseItem(L2PcInstance activeChar, StatsSet set, AuctionHouseEntranceItem item, int purchaseCount)
	{
		if (item == null)
		{
			activeChar.sendMessage("The selected auction couldn't be purchased");
			return;
		}
		
		if (!activeChar.getClient().getFloodProtectors().getTransaction().tryPerformAction("purchaseItem"))
		{
			return;
		}
		
		if (!activeChar.getAccessLevel().allowTransaction())
		{
			activeChar.sendMessage("Transactions are disabled for your Access Level");
			return;
		}
		
		if (activeChar.getActiveTradeList() != null)
		{
			activeChar.sendMessage("You cant purchase an item while having an active exchange");
			return;
		}
		
		if (activeChar.isEnchanting())
		{
			activeChar.sendMessage("You cant purchase an item while while enchanting");
			return;
		}
		
		if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			activeChar.sendMessage("You cant purchase an item while in Private Store");
			return;
		}
		
		if (activeChar.isJailed())
		{
			activeChar.sendMessage("You cant purchase an item while in Jail");
			return;
		}
		
		if (activeChar.isTeleporting() || activeChar.isCastingNow() || activeChar.isAttackingNow())
		{
			activeChar.sendMessage("You cant purchase an item while casting/attacking");
			return;
		}
		
		synchronized (_entrancesById)
		{
			if (item.getCharId() == activeChar.getObjectId())
			{
				activeChar.sendMessage("You cant purchase your own items!");
				return;
			}
			
			final int purchaseObjectId = set.getInt("selectedItemId", -1);
			if (item.getObjectId() != purchaseObjectId)
			{
				activeChar.sendMessage("Invalid item");
				return;
			}
			
			final ItemContainer container = _containersByCharId.get(item.getCharId());
			if ((container == null) || (container.getSize() == 0))
			{
				activeChar.sendMessage("You cant purchase this item. Something is missing");
				return;
			}
			
			if (item.getItemInstance().getItemLocation() != ItemLocation.AUCTION)
			{
				Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get items not from mail !", Config.DEFAULT_PUNISH);
				return;
			}
			
			if (activeChar.getAdena() < (item.getPrice() * purchaseCount))
			{
				activeChar.sendMessage("Not enough adena to purchase this item");
				return;
			}
			
			final long weight = purchaseCount * item.getItemInstance().getItem().getWeight();
			int slots = 0;
			if (!item.getItemInstance().isStackable())
			{
				slots += purchaseCount;
			}
			else if (activeChar.getInventory().getItemByItemId(item.getItemInstance().getId()) == null)
			{
				slots++;
			}
			
			if (!activeChar.getInventory().validateCapacity(slots))
			{
				activeChar.sendMessage("You cant purchase this item. You dont have enough slots");
				return;
			}
			
			if (!activeChar.getInventory().validateWeight(weight))
			{
				activeChar.sendMessage("You cant purchase this item. You dont have enough weight available");
				return;
			}
			
			if (!activeChar.reduceAdena("AuctionHouse", item.getPrice() * purchaseCount, null, true))
			{
				activeChar.sendMessage("Not enough adena to purchase this item");
				return;
			}
			
			final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
			final L2ItemInstance newItem = container.transferItem(container.getName(), item.getObjectId(), purchaseCount, activeChar.getInventory(), activeChar, null);
			if (newItem == null)
			{
				activeChar.sendMessage("You cant purchase this item. Something is wrong. Report to a Game Master");
				return;
			}
			
			if (playerIU != null)
			{
				if (newItem.getCount() > purchaseCount)
				{
					playerIU.addModifiedItem(newItem);
				}
				else
				{
					playerIU.addNewItem(newItem);
				}
			}
			
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_ACQUIRED_S2_S1);
			sm.addItemName(item.getItemInstance().getId());
			sm.addLong(purchaseCount);
			activeChar.sendPacket(sm);
			
			if (playerIU != null)
			{
				activeChar.sendPacket(playerIU);
			}
			else
			{
				activeChar.sendPacket(new ItemList(activeChar, false));
			}
			
			final StatusUpdate su = new StatusUpdate(activeChar);
			su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
			activeChar.sendPacket(su);
			
			final String message = "The player " + activeChar.getName() + " has bought " + item.getItemInstance().getItem().getName() + "\nQuantity: " + DECIMAL_FORMATTER.format(purchaseCount).replace(".", ",") + "\nTotal: " + DECIMAL_FORMATTER.format(item.getPrice() * purchaseCount).replace(".", ",") + " Adena" + "\nDate: " + DATE_FORMATTER.format(new Date(System.currentTimeMillis())) + "\n\nThanks for using our Auction House system";
			Message msg = new Message(item.getCharId(), "Auction House Sell", message, Message.SendBySystem.NONE);
			Mail attachments = msg.createAttachments();
			attachments.addItem("AuctionHouse", Inventory.ADENA_ID, item.getPrice() * purchaseCount, null, null);
			MailManager.getInstance().sendMessage(msg);
			
			modifyAuctionItemCountToDB(item, item.getQuantity() - purchaseCount);
			
			activeChar.sendMessage("You have succesfully purchased " + newItem.getItem().getName());
		}
	}
	
	private synchronized void returnItemToOwner(AuctionHouseEntranceItem item)
	{
		if (item.isRemoved())
		{
			return;
		}
		
		item.setIsRemoved(true);
		
		final ItemContainer container = _containersByCharId.get(item.getCharId());
		if ((container == null) || (container.getSize() == 0))
		{
			return;
		}
		
		if (item.getItemInstance().getItemLocation() != ItemLocation.AUCTION)
		{
			_log.warn(getClass().getSimpleName() + ": The item " + item.getObjectId() + " that is being returned to the owner doesnt belong to the auction house");
			return;
		}
		
		ThreadPoolManager.getInstance().scheduleGeneral(new ReturnItemsToOwnerThread(container, item), 100);
	}
	
	private class ReturnItemsToOwnerThread implements Runnable
	{
		private final ItemContainer _container;
		private final AuctionHouseEntranceItem _item;
		
		public ReturnItemsToOwnerThread(ItemContainer container, AuctionHouseEntranceItem item)
		{
			_container = container;
			_item = item;
		}
		
		@Override
		public void run()
		{
			synchronized (_entrancesById)
			{
				final String message = "Your item couldn't be sold on the set period, so it's now returning to you\nThanks for using our Auction House system";
				final Message msg = new Message(_item.getCharId(), "Auction House Return", message, Message.SendBySystem.NONE);
				final Mail attachments = msg.createAttachments();
				final L2ItemInstance newItem = _container.transferItem("AuctionHouse", _item.getObjectId(), _item.getQuantity(), attachments, null, null);
				if (newItem == null)
				{
					_log.warn(getClass().getSimpleName() + ": Error adding attachment item " + _item.getObjectId() + " for char " + _item.getCharId() + " (newitem == null)");
					return;
				}
				newItem.setItemLocation(newItem.getItemLocation(), msg.getId());
				
				MailManager.getInstance().sendMessage(msg);
				
				modifyAuctionItemCountToDB(_item, -1);
			}
		}
	}
	
	private void addNewAuctionToDB(L2PcInstance activeChar, L2ItemInstance item, int quantity, long salePrice, int duration)
	{
		final AuctionHouse entrance = AuctionHouse.getEntranceIdFromItem(item);
		final AuctionHouseEntranceItem newItem = new AuctionHouseEntranceItem(entrance.getTopId(), activeChar.getObjectId(), item, quantity, salePrice, System.currentTimeMillis() + (duration * 24 * 60 * 60 * 1000));
		_entrancesById.get(entrance.getTopId()).addItem(newItem);
		
		AuctionHouseGenerator.getInstance().addNewAuctionToDB(newItem.getObjectId(), newItem.getCharId(), newItem.getQuantity(), newItem.getPrice(), newItem.getEndTime());
	}
	
	protected void modifyAuctionItemCountToDB(AuctionHouseEntranceItem item, int newCount)
	{
		item.setQuantity(newCount);
		
		if (item.getQuantity() <= 0)
		{
			_entrancesById.get(item.getTopId()).getItems().remove(item);
			
			AuctionHouseGenerator.getInstance().deleteItemFromDB(item.getObjectId());
		}
		else
		{
			AuctionHouseGenerator.getInstance().updateItemCountToDB(item.getObjectId(), item.getQuantity());
		}
	}
	
	private String getCompleteItemName(L2ItemInstance item, boolean showCompleteInfo, boolean showIcons)
	{
		if (showIcons)
		{
			final StringBuilder builder = new StringBuilder();
			
			if (item.getEnchantLevel() > 0)
			{
				builder.append("<font color=a09675>+" + item.getEnchantLevel() + "</font> ");
			}
			
			final String[] parts = item.getItem().getName().split(" - ");
			if (parts.length > 2)
			{
				builder.append("<font color=fcf900>" + parts[0] + " - " + parts[1] + "</font> <font color=d3aa4e>" + parts[2] + "</font> ");
			}
			else if (parts.length > 1)
			{
				builder.append(parts[0] + " <font color=d3aa4e>" + parts[1] + "</font> ");
			}
			else
			{
				builder.append(parts[0] + " ");
			}
			
			return builder.toString();
		}
		
		if (showCompleteInfo)
		{
			final StringBuilder builder = new StringBuilder();
			
			if (item.getEnchantLevel() > 0)
			{
				builder.append("<font color=a09675>+" + item.getEnchantLevel() + "</font> ");
			}
			
			final String[] parts = item.getItem().getName().split(" - ");
			if (parts.length > 2)
			{
				builder.append("<font color=fcf900>" + parts[0] + " - " + parts[1] + "</font> <font color=d3aa4e>" + parts[2] + "</font> ");
			}
			else if (parts.length > 1)
			{
				builder.append(parts[0] + " <font color=d3aa4e>" + parts[1] + "</font> ");
			}
			else
			{
				builder.append(parts[0] + " ");
			}
			
			if (item.getAttackElementPower() > 0)
			{
				switch (item.getAttackElementType())
				{
					case Elementals.WATER:
						builder.append("<font color=396191>Water +");
						break;
					case Elementals.DARK:
						builder.append("<font color=52544b>Dark +");
						break;
					case Elementals.EARTH:
						builder.append("<font color=68692f>Earth +");
						break;
					case Elementals.FIRE:
						builder.append("<font color=914e39>Fire +");
						break;
					case Elementals.HOLY:
						builder.append("<font color=7a878b>Holy +");
						break;
					case Elementals.WIND:
						builder.append("<font color=324461>Wind +");
						break;
				}
				builder.append(item.getAttackElementPower() + "</font>");
			}
			for (byte i = 0; i < 6; i++)
			{
				if (item.getElementDefAttr(i) > 0)
				{
					switch (i)
					{
						case Elementals.WATER:
							builder.append("<font color=396191>Water +");
							break;
						case Elementals.DARK:
							builder.append("<font color=52544b>Dark +");
							break;
						case Elementals.EARTH:
							builder.append("<font color=68692f>Earth +");
							break;
						case Elementals.FIRE:
							builder.append("<font color=914e39>Fire +");
							break;
						case Elementals.HOLY:
							builder.append("<font color=7a878b>Holy +");
							break;
						case Elementals.WIND:
							builder.append("<font color=324461>Wind +");
							break;
					}
					builder.append(item.getElementDefAttr(i) + "</font> ");
				}
			}
			
			return builder.toString();
		}
		
		return (item.getEnchantLevel() > 0 ? "+" + item.getEnchantLevel() + " " : "") + item.getItem().getName();
	}
	
	public static AuctionHouseManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AuctionHouseManager _instance = new AuctionHouseManager();
	}
	
	public static class AuctionHouseEntrance
	{
		private final int _topId;
		private final String _name;
		private final String _category;
		private final ArrayList<AuctionHouseEntranceItem> _items = new ArrayList<>();
		
		public AuctionHouseEntrance(int topId, String name, String category)
		{
			_topId = topId;
			_name = name;
			_category = category;
		}
		
		public int getTopId()
		{
			return _topId;
		}
		
		public String getTopName()
		{
			return _name;
		}
		
		public String getCategory()
		{
			return _category;
		}
		
		public void addItem(AuctionHouseEntranceItem item)
		{
			_items.add(item);
		}
		
		public void cleanItems()
		{
			_items.clear();
		}
		
		public ArrayList<AuctionHouseEntranceItem> getItems()
		{
			return _items;
		}
	}
	
	public static class AuctionHouseEntranceItem
	{
		private final int _topId;
		private final int _charId;
		private final int _objectId;
		private final L2ItemInstance _item;
		private int _quantity;
		private final long _price;
		private final long _endTime;
		private volatile boolean _isRemoved = false;
		
		public AuctionHouseEntranceItem(int topId, int charId, L2ItemInstance item, int quantity, long price, long endTime)
		{
			_topId = topId;
			_charId = charId;
			_objectId = item.getObjectId();
			_item = item;
			_quantity = quantity;
			_price = price;
			_endTime = endTime;
		}
		
		public void setQuantity(int count)
		{
			_quantity = count;
		}
		
		public int getTopId()
		{
			return _topId;
		}
		
		public int getCharId()
		{
			return _charId;
		}
		
		public int getObjectId()
		{
			return _objectId;
		}
		
		public L2ItemInstance getItemInstance()
		{
			return _item;
		}
		
		public int getQuantity()
		{
			return _quantity;
		}
		
		public long getPrice()
		{
			return _price;
		}
		
		public long getEndTime()
		{
			return _endTime;
		}
		
		public String getRemainingTimeString()
		{
			final long diffTime = _endTime - System.currentTimeMillis();
			if (diffTime < (60 * 60 * 1000))
			{
				return (int) (diffTime / 60000) + " Minutes";
			}
			if (diffTime < (24 * 60 * 60 * 1000))
			{
				return (int) (diffTime / 3600000) + " Hours";
			}
			
			return (int) (diffTime / 86400000) + " Days";
		}
		
		public void setIsRemoved(boolean val)
		{
			_isRemoved = val;
		}
		
		public boolean isRemoved()
		{
			return _isRemoved;
		}
	}
	
	// Sorting comparators
	private static final Comparator<AuctionHouseEntranceItem> NAME_ASC_COMPARATOR = (left, right) -> left.getItemInstance().getItem().getName().compareTo(right.getItemInstance().getItem().getName());
	
	private static final Comparator<AuctionHouseEntranceItem> NAME_DESC_COMPARATOR = (left, right) -> -left.getItemInstance().getItem().getName().compareTo(right.getItemInstance().getItem().getName());
	
	private static final Comparator<AuctionHouseEntranceItem> RANK_ASC_COMPARATOR = (left, right) ->
	{
		if (left.getItemInstance().getItem().getCrystalType().getId() > right.getItemInstance().getItem().getCrystalType().getId())
		{
			return 1;
		}
		if (left.getItemInstance().getItem().getCrystalType().getId() < right.getItemInstance().getItem().getCrystalType().getId())
		{
			return -1;
		}
		return 0;
	};
	
	private static final Comparator<AuctionHouseEntranceItem> RANK_DESC_COMPARATOR = (left, right) ->
	{
		if (left.getItemInstance().getItem().getCrystalType().getId() > right.getItemInstance().getItem().getCrystalType().getId())
		{
			return -1;
		}
		if (left.getItemInstance().getItem().getCrystalType().getId() < right.getItemInstance().getItem().getCrystalType().getId())
		{
			return 1;
		}
		return 0;
	};
	
	private static final Comparator<AuctionHouseEntranceItem> COUNT_ASC_COMPARATOR = (left, right) ->
	{
		if (left.getQuantity() > right.getQuantity())
		{
			return 1;
		}
		if (left.getQuantity() < right.getQuantity())
		{
			return -1;
		}
		return 0;
	};
	
	private static final Comparator<AuctionHouseEntranceItem> COUNT_DESC_COMPARATOR = (left, right) ->
	{
		if (left.getQuantity() > right.getQuantity())
		{
			return -1;
		}
		if (left.getQuantity() < right.getQuantity())
		{
			return 1;
		}
		return 0;
	};
	
	private static final Comparator<AuctionHouseEntranceItem> PRICE_ASC_COMPARATOR = (left, right) ->
	{
		if (left.getPrice() > right.getPrice())
		{
			return 1;
		}
		if (left.getPrice() < right.getPrice())
		{
			return -1;
		}
		return 0;
	};
	
	private static final Comparator<AuctionHouseEntranceItem> PRICE_DESC_COMPARATOR = (left, right) ->
	{
		if (left.getPrice() > right.getPrice())
		{
			return -1;
		}
		if (left.getPrice() < right.getPrice())
		{
			return 1;
		}
		return 0;
	};
	
	public void showCBHtml(String html, L2PcInstance acha)
	{
		if (html == null)
		{
			return;
		}
		if (html.length() < 4096)
		{
			acha.sendPacket(new ShowBoard(html, "101"));
			acha.sendPacket(new ShowBoard(null, "102"));
			acha.sendPacket(new ShowBoard(null, "103"));
			
		}
		else if (html.length() < 8192)
		{
			acha.sendPacket(new ShowBoard(html.substring(0, 4096), "101"));
			acha.sendPacket(new ShowBoard(html.substring(4096), "102"));
			acha.sendPacket(new ShowBoard(null, "103"));
			
		}
		else if (html.length() < 16384)
		{
			acha.sendPacket(new ShowBoard(html.substring(0, 4096), "101"));
			acha.sendPacket(new ShowBoard(html.substring(4096, 8192), "102"));
			acha.sendPacket(new ShowBoard(html.substring(8192), "103"));
		}
	}
	
	/**
	 * @param html
	 * @param acha
	 */
	public void send1001(String html, L2PcInstance acha)
	{
		acha.sendPacket(new ShowBoard(html, "1001"));
	}
	
	/**
	 * @param acha
	 */
	protected void send1002(L2PcInstance acha)
	{
		send1002(acha, " ", " ", "0");
	}
	
	/**
	 * @param activeChar
	 * @param string
	 * @param string2
	 * @param string3
	 */
	public void send1002(L2PcInstance activeChar, String string, String string2, String string3)
	{
		List<String> _arg = new LinkedList<>();
		_arg.add("0");
		_arg.add("0");
		_arg.add("0");
		_arg.add("0");
		_arg.add("0");
		_arg.add("0");
		_arg.add(activeChar.getName());
		_arg.add(Integer.toString(activeChar.getObjectId()));
		_arg.add(activeChar.getAccountName());
		_arg.add("9");
		_arg.add(string2);
		_arg.add(string2);
		_arg.add(string);
		_arg.add(string3);
		_arg.add(string3);
		_arg.add("0");
		_arg.add("0");
		activeChar.sendPacket(new ShowBoard(_arg));
	}
}
