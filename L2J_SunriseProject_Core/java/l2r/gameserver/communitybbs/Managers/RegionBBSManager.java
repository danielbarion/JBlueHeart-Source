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
package l2r.gameserver.communitybbs.Managers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.data.xml.impl.RecipeData;
import l2r.gameserver.enums.PrivateStoreType;
import l2r.gameserver.model.L2ManufactureItem;
import l2r.gameserver.model.L2RecipeList;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.TradeItem;
import l2r.gameserver.model.TradeList;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.type.CrystalType;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.RadarControl;
import l2r.gameserver.util.HtmlUtils;
import l2r.gameserver.util.MapUtils;

public class RegionBBSManager extends TopBBSManager
{
	// @formatter:off
	private static final int[][] _towns = new int[][] { {1010005, 19, 21}, {1010006, 20, 22}, {1010007, 22, 22}, {1010013, 22, 19}, {1010023, 24, 18}, {1010049, 23, 24}, {1010199, 24, 16}, {1010200, 21, 16}, {1010574, 22, 13}};
	private static final String[] _regionTypes = { "&$596;", "&$597;", "&$665;" };
	private static final String[] _elements = { "&$1622;", "&$1623;", "&$1624;", "&$1625;", "&$1626;", "&$1627;" };
	private static final String[] _grade = { "&$1291;", "&$1292;", "&$1293;", "&$1294;", "&$1295;", "S80 Grade", "S84 Grade" };
	private static final int SELLER_PER_PAGE = 12;
	private static byte AttrFire = 0;
	private static byte AttrWater = 1;
	private static byte AttrWind = 2;
	private static byte AttrEarth = 3;
	private static byte AttrHoly = 4;
	private static byte AttrDark = 5;
	
	private static final String _pageRegionTpl = "data/html/CommunityBoard/region/bbs_regiontpl.htm";
	private static final String _pageRegionSTpl = "data/html/CommunityBoard/region/bbs_region_stpl.htm";
	private static final String _pageRegionStoreTpl = "data/html/CommunityBoard/region/bbs_region_storetpl.htm";
	private static final String _pageRegionList = "data/html/CommunityBoard/region/bbs_region_list.htm";
	private static final String _pageRegionSelers = "data/html/CommunityBoard/region/bbs_region_sellers.htm";
	private static final String _pageRegionView = "data/html/CommunityBoard/region/bbs_region_view.htm";
	// @formatter:on
	
	@Override
	public void cbByPass(String command, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(command, ";");
		
		if (command.equals("_bbsloc"))
		{
			String tpl = HtmCache.getInstance().getHtm(_pageRegionTpl);
			StringBuilder rl = new StringBuilder("");
			
			for (int townId = 0; townId < _towns.length; townId++)
			{
				int[] town = _towns[townId];
				
				String reg = tpl.replace("%region_bypass%", "_bbsregion;" + String.valueOf(townId));
				reg = reg.replace("%region_name%", HtmlUtils.htmlNpcString(town[0]));
				reg = reg.replace("%region_desc%", "&$498;: &$1157;, &$1434;, &$645;.");
				reg = reg.replace("%region_type%", "l2ui.bbs_folder");
				int sellers = 0;
				
				int rx = town[1];
				int ry = town[2];
				int offset = 0;
				
				for (L2PcInstance seller : L2World.getInstance().getPlayers())
				{
					int tx = MapUtils.regionX(seller);
					int ty = MapUtils.regionY(seller);
					
					if ((tx >= (rx - offset)) && (tx <= (rx + offset)) && (ty >= (ry - offset)) && (ty <= (ry + offset)))
					{
						if (seller.getPrivateStoreType() != PrivateStoreType.NONE)
						{
							sellers++;
						}
					}
				}
				reg = reg.replace("%sellers_count%", String.valueOf(sellers));
				rl.append(reg);
			}
			
			String html = HtmCache.getInstance().getHtm(_pageRegionList);
			html = html.replace("%REGION_LIST%", rl.toString());
			
			separateAndSend(html, player);
		}
		else if (command.startsWith("_bbsregion"))
		{
			st.nextToken();
			String tpl = HtmCache.getInstance().getHtm(_pageRegionTpl);
			int townId = Integer.parseInt(st.nextToken());
			StringBuilder rl = new StringBuilder("");
			int[] town = _towns[townId];
			
			for (int type = 0; type < _regionTypes.length; type++)
			{
				String reg = tpl.replace("%region_bypass%", "_bbsreglist;" + townId + ";" + type + ";1;0;");
				reg = reg.replace("%region_name%", _regionTypes[type]);
				reg = reg.replace("%region_desc%", _regionTypes[type] + ".");
				reg = reg.replace("%region_type%", "l2ui.bbs_board");
				int sellers = 0;
				
				int rx = town[1];
				int ry = town[2];
				int offset = 0;
				
				for (L2PcInstance seller : L2World.getInstance().getPlayers())
				{
					int tx = MapUtils.regionX(seller);
					int ty = MapUtils.regionY(seller);
					
					if ((tx >= (rx - offset)) && (tx <= (rx + offset)) && (ty >= (ry - offset)) && (ty <= (ry + offset)))
					{
						if ((type == 0) && ((seller.getPrivateStoreType() == PrivateStoreType.SELL) || (seller.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL)))
						{
							sellers++;
						}
						else if ((type == 1) && (seller.getPrivateStoreType() == PrivateStoreType.BUY))
						{
							sellers++;
						}
						else if ((type == 2) && (seller.getPrivateStoreType() == PrivateStoreType.MANUFACTURE))
						{
							sellers++;
						}
					}
				}
				
				reg = reg.replace("%sellers_count%", String.valueOf(sellers));
				rl.append(reg);
			}
			
			String html = HtmCache.getInstance().getHtm(_pageRegionList);
			html = html.replace("%REGION_LIST%", rl.toString());
			
			separateAndSend(html, player);
		}
		else if (command.startsWith("_bbsreglist"))
		{
			st.nextToken();
			int townId = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			int byItem = Integer.parseInt(st.nextToken());
			String search = st.hasMoreTokens() ? st.nextToken().toLowerCase() : "";
			int[] town = _towns[townId];
			
			List<L2PcInstance> sellers = getSellersList(townId, type, search, byItem == 1);
			
			int start = (page - 1) * SELLER_PER_PAGE;
			int end = Math.min(page * SELLER_PER_PAGE, sellers.size());
			
			String html = HtmCache.getInstance().getHtm(_pageRegionSelers);
			
			if (page == 1)
			{
				html = html.replace("%ACTION_GO_LEFT%", "");
				html = html.replace("%GO_LIST%", "");
				html = html.replace("%NPAGE%", "1");
			}
			else
			{
				html = html.replace("%ACTION_GO_LEFT%", "bypass _bbsreglist;" + townId + ";" + type + ";" + (page - 1) + ";" + byItem + ";" + search);
				html = html.replace("%NPAGE%", String.valueOf(page));
				StringBuilder goList = new StringBuilder("");
				for (int i = page > 10 ? page - 10 : 1; i < page; i++)
				{
					goList.append("<td><a action=\"bypass _bbsreglist;").append(townId).append(";").append(type).append(";").append(i).append(";").append(byItem).append(";").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");
				}
				
				html = html.replace("%GO_LIST%", goList.toString());
			}
			
			int pages = Math.max(sellers.size() / SELLER_PER_PAGE, 1);
			if (sellers.size() > (pages * SELLER_PER_PAGE))
			{
				pages++;
			}
			
			if (pages > page)
			{
				html = html.replace("%ACTION_GO_RIGHT%", "bypass _bbsreglist;" + townId + ";" + type + ";" + (page + 1) + ";" + byItem + ";" + search);
				int ep = Math.min(page + 10, pages);
				StringBuilder goList = new StringBuilder("");
				for (int i = page + 1; i <= ep; i++)
				{
					goList.append("<td><a action=\"bypass _bbsreglist;").append(townId).append(";").append(type).append(";").append(i).append(";").append(byItem).append(";").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");
				}
				
				html = html.replace("%GO_LIST2%", goList.toString());
			}
			else
			{
				html = html.replace("%ACTION_GO_RIGHT%", "");
				html = html.replace("%GO_LIST2%", "");
			}
			
			StringBuilder seller_list = new StringBuilder("");
			String tpl = HtmCache.getInstance().getHtm(_pageRegionSTpl);
			
			for (int i = start; i < end; i++)
			{
				L2PcInstance seller = sellers.get(i);
				TradeList tl = null;
				if (seller.getPrivateStoreType() == PrivateStoreType.BUY)
				{
					tl = seller.getBuyList();
				}
				else
				{
					tl = seller.getSellList();
				}
				
				Map<Integer, L2ManufactureItem> cl = seller.getManufactureItems();
				
				if ((tl == null) && (cl == null))
				{
					continue;
				}
				
				String stpl = tpl;
				stpl = stpl.replace("%view_bypass%", "bypass _bbsregview;" + townId + ";" + type + ";" + page + ";" + seller.getObjectId() + ";" + byItem + ";" + search);
				stpl = stpl.replace("%seller_name%", seller.getName());
				String title = "N/A";
				if (type == 0)
				{
					title = (tl != null) && (tl.getTitle() != null) && !tl.getTitle().isEmpty() ? tl.getTitle() : "N/A";
				}
				else if (type == 1)
				{
					title = (tl != null) && (tl.getTitle() != null) && !tl.getTitle().isEmpty() ? tl.getTitle() : "N/A";
				}
				else if ((type == 2) && (seller.getPrivateStoreType() == PrivateStoreType.MANUFACTURE))
				{
					title = (cl != null) && (seller.getStoreName() != null) && !seller.getStoreName().isEmpty() ? seller.getStoreName() : "-";
				}
				
				title = title.replace("<", "");
				title = title.replace(">", "");
				title = title.replace("&", "");
				title = title.replace("$", "");
				
				if (title.isEmpty())
				{
					title = "N/A";
				}
				
				stpl = stpl.replace("%seller_title%", title);
				
				seller_list.append(stpl);
			}
			
			html = html.replace("%SELLER_LIST%", seller_list.toString());
			html = html.replace("%search_bypass%", "_bbsregsearch_" + townId + "_" + type);
			html = html.replace("%TREE%", "&nbsp;>&nbsp;<a action=\"bypass _bbsregion;" + townId + "\"><font color=\"aa9977\"> " + HtmlUtils.htmlNpcString(town[0]) + "</font></a>&nbsp;>&nbsp;" + _regionTypes[type]);
			
			separateAndSend(html, player);
		}
		else if (command.startsWith("_bbsregview"))
		{
			st.nextToken();
			int townId = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			int objectId = Integer.parseInt(st.nextToken());
			int byItem = Integer.parseInt(st.nextToken());
			String search = st.hasMoreTokens() ? st.nextToken().toLowerCase() : "";
			int[] town = _towns[townId];
			
			L2PcInstance seller = L2World.getInstance().getPlayer(objectId);
			if ((seller == null) || (seller.getPrivateStoreType() == PrivateStoreType.NONE))
			{
				cbByPass("_bbsreglist;" + townId + ";" + type + ";" + page + ";" + byItem + ";" + search, player);
				return;
			}
			
			String title = "-";
			String tpl = HtmCache.getInstance().getHtm(_pageRegionStoreTpl);
			StringBuilder sb = new StringBuilder("");
			
			if (type < 2)
			{
				TradeList sl = type == 0 ? seller.getSellList() : seller.getBuyList();
				
				if (sl == null)
				{
					cbByPass("_bbsreglist;" + townId + ";" + type + ";" + page + ";" + byItem + ";" + search, player);
					return;
				}
				
				if ((type == 0) && (sl.getTitle() != null) && !sl.getTitle().isEmpty())
				{
					title = sl.getTitle();
				}
				else if ((type == 1) && (sl.getTitle() != null) && !sl.getTitle().isEmpty())
				{
					title = sl.getTitle();
				}
				
				for (TradeItem ti : sl.getItems())
				{
					L2Item item = ItemData.getInstance().getTemplate(ti.getItem().getId());
					if (item != null)
					{
						String stpl = tpl.replace("%item_name%", ti.getItem().getName() + (ti.getItem().isEquipable() && (ti.getEnchant() > 0) ? " +" + ti.getEnchant() : ""));
						stpl = stpl.replace("%item_img%", item.getIcon());
						stpl = stpl.replace("%item_count%", String.valueOf(ti.getCount()));
						stpl = stpl.replace("%item_price%", String.format("%,3d", ti.getPrice()).replace(" ", ","));
						
						String desc = "";
						if (item.getCrystalType() != CrystalType.NONE)
						{
							desc = _grade[item.getCrystalType().getId() - 1];
							desc += item.getCrystalCount() > 0 ? " Crystals: " + item.getCrystalCount() + ";&nbsp;" : ";&nbsp;";
						}
						
						if (item.isEquipable())
						{
							if ((ti.getAttackElementType() >= 0) && (ti.getAttackElementPower() > 0))
							{
								desc += "&$1620;: " + _elements[ti.getAttackElementType()] + " +" + ti.getAttackElementPower();
							}
							else if ((ti.getElementDefAttr(AttrFire) > 0) || (ti.getElementDefAttr(AttrWater) > 0) || (ti.getElementDefAttr(AttrWind) > 0) || (ti.getElementDefAttr(AttrEarth) > 0) || (ti.getElementDefAttr(AttrHoly) > 0) || (ti.getElementDefAttr(AttrDark) > 0))
							{
								desc += "&$1651;:";
								if (ti.getElementDefAttr(AttrFire) > 0)
								{
									desc += " &$1622; +" + ti.getElementDefAttr(AttrFire) + ";&nbsp;";
								}
								if (ti.getElementDefAttr(AttrWater) > 0)
								{
									desc += " &$1623; +" + ti.getElementDefAttr(AttrWater) + ";&nbsp;";
								}
								if (ti.getElementDefAttr(AttrWind) > 0)
								{
									desc += " &$1624; +" + ti.getElementDefAttr(AttrWind) + ";&nbsp;";
								}
								if (ti.getElementDefAttr(AttrEarth) > 0)
								{
									desc += " &$1625; +" + ti.getElementDefAttr(AttrEarth) + ";&nbsp;";
								}
								if (ti.getElementDefAttr(AttrHoly) > 0)
								{
									desc += " &$1626; +" + ti.getElementDefAttr(AttrHoly) + ";&nbsp;";
								}
								if (ti.getElementDefAttr(AttrDark) > 0)
								{
									desc += " &$1627; +" + ti.getElementDefAttr(AttrDark) + ";&nbsp;";
								}
							}
						}
						if (item.isStackable())
						{
							desc += "Stackable;&nbsp;";
						}
						
						// FIXME
						/**
						 * if (item.isSealedItem()) { desc += "Sealed;&nbsp;"; } if (item.isShadowItem()) { desc += "Shadow item;&nbsp;"; } if (item.isTimeItem()) { desc += "Temporal;&nbsp;"; }
						 */
						
						stpl = stpl.replace("%item_desc%", desc);
						sb.append(stpl);
					}
				}
			}
			else
			{
				Map<Integer, L2ManufactureItem> cl = seller.getManufactureItems();
				if (cl == null)
				{
					cbByPass("_bbsreglist;" + townId + ";" + type + ";" + page + ";" + byItem + ";" + search, player);
					return;
				}
				
				if ((title = seller.getStoreName()) == null)
				{
					title = "-";
				}
				
				for (L2ManufactureItem mi : seller.getManufactureItems().values())
				{
					L2RecipeList rec = RecipeData.getInstance().getRecipeByItemId(mi.getRecipeId() - 1);
					if (rec == null)
					{
						continue;
					}
					L2Item item = ItemData.getInstance().getTemplate(rec.getId());
					if (item == null)
					{
						continue;
					}
					String stpl = tpl.replace("%item_name%", item.getName());
					stpl = stpl.replace("%item_img%", item.getIcon());
					stpl = stpl.replace("%item_count%", "N/A");
					stpl = stpl.replace("%item_price%", String.format("%,3d", mi.getCost()).replace(" ", ","));
					String desc = "";
					if (item.getCrystalType() != CrystalType.NONE)
					{
						desc = _grade[item.getCrystalType().getId() - 1] + (item.getCrystalCount() > 0 ? " Crystals: " + item.getCrystalCount() + ";&nbsp;" : ";&nbsp;");
					}
					
					if (item.isStackable())
					{
						desc = "Stackable;&nbsp;";
					}
					
					// FIXME
					/**
					 * if (item.isSealedItem()) { desc += "Sealed;&nbsp;"; }
					 */
					
					stpl = stpl.replace("%item_desc%", desc);
					sb.append(stpl);
				}
			}
			
			String html = HtmCache.getInstance().getHtm(_pageRegionView);
			
			html = html.replace("%sell_type%", _regionTypes[type]);
			
			title = title.replace("<", "");
			title = title.replace(">", "");
			title = title.replace("&", "");
			title = title.replace("$", "");
			if (title.isEmpty())
			{
				title = "-";
			}
			html = html.replace("%title%", title);
			html = html.replace("%char_name%", seller.getName());
			html = html.replace("%object_id%", String.valueOf(seller.getObjectId()));
			html = html.replace("%STORE_LIST%", sb.toString());
			html = html.replace("%list_bypass%", "_bbsreglist;" + townId + ";" + type + ";" + page + ";" + byItem + ";" + search);
			html = html.replace("%TREE%", "&nbsp;>&nbsp;<a action=\"bypass _bbsregion;" + townId + "\">" + HtmlUtils.htmlNpcString(town[0]) + "</a>&nbsp;>&nbsp;<a action=\"bypass _bbsreglist;" + townId + ";" + type + ";" + page + ";" + byItem + "\">" + _regionTypes[type] + "</a>&nbsp;>&nbsp;" + seller.getName());
			
			separateAndSend(html, player);
		}
		else if (command.startsWith("_bbsregtarget"))
		{
			st.nextToken();
			int objectId = Integer.parseInt(st.nextToken());
			L2PcInstance seller = L2World.getInstance().getPlayer(objectId);
			if (seller != null)
			{
				player.sendPacket(new RadarControl(0, 2, seller.getLocation().getX(), seller.getLocation().getY(), seller.getLocation().getZ()));
				
				if (player.getKnownList().getKnownObject(seller.getObjectId()) != null)
				{
					player.setTarget(seller);
					seller.broadcastRelationChanged();
				}
			}
			else
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		
		return;
	}
	
	@SuppressWarnings("synthetic-access")
	private static List<L2PcInstance> getSellersList(int townId, int type, String search, boolean byItem)
	{
		List<L2PcInstance> list = new LinkedList<>();
		int town[] = _towns[townId];
		int rx = town[1];
		int ry = town[2];
		int offset = 0;
		
		for (L2PcInstance seller : L2World.getInstance().getPlayers())
		{
			int tx = MapUtils.regionX(seller);
			int ty = MapUtils.regionY(seller);
			
			if ((tx >= (rx - offset)) && (tx <= (rx + offset)) && (ty >= (ry - offset)) && (ty <= (ry + offset)))
			{
				TradeItem[] tl = null;
				if (seller.getPrivateStoreType() == PrivateStoreType.BUY)
				{
					tl = seller.getBuyList().getItems();
				}
				else
				{
					tl = seller.getSellList().getItems();
				}
				
				Map<Integer, L2ManufactureItem> cl = seller.getManufactureItems();
				if (seller.getPrivateStoreType() != PrivateStoreType.NONE)
				{
					if ((type == 0) && (tl != null) && ((seller.getPrivateStoreType() == PrivateStoreType.SELL) || (seller.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL)))
					{
						list.add(seller);
					}
					else if ((type == 1) && (tl != null) && (seller.getPrivateStoreType() == PrivateStoreType.BUY))
					{
						list.add(seller);
					}
					else if ((type == 2) && (cl != null) && (seller.getPrivateStoreType() == PrivateStoreType.MANUFACTURE))
					{
						list.add(seller);
					}
				}
			}
		}
		
		if (!search.isEmpty() && !list.isEmpty())
		{
			List<L2PcInstance> s_list = new LinkedList<>();
			for (L2PcInstance seller : list)
			{
				TradeList tl = null;
				if (seller.getPrivateStoreType() == PrivateStoreType.BUY)
				{
					tl = seller.getBuyList();
				}
				else
				{
					tl = seller.getSellList();
				}
				Map<Integer, L2ManufactureItem> cl = seller.getManufactureItems();
				if (byItem)
				{
					if (((type == 0) || (type == 1)) && (tl != null))
					{
						TradeItem[] sl = type == 0 ? seller.getSellList().getItems() : seller.getBuyList().getItems();
						if (sl != null)
						{
							for (TradeItem ti : sl)
							{
								L2Item item = ItemData.getInstance().getTemplate(ti.getItem().getId());
								if ((item != null) && (item.getName() != null) && item.getName().toLowerCase().contains(search))
								{
									s_list.add(seller);
									break;
								}
							}
						}
					}
					else if ((type == 2) && (cl != null))
					{
						for (L2ManufactureItem mi : seller.getManufactureItems().values())
						{
							L2RecipeList recipe = RecipeData.getInstance().getRecipeList(mi.getRecipeId() - 1);
							if (recipe != null)
							{
								L2Item item = ItemData.getInstance().getTemplate(recipe.getId());
								if ((item != null) && (item.getName() != null) && item.getName().toLowerCase().contains(search))
								{
									s_list.add(seller);
									break;
								}
							}
						}
					}
				}
				else if ((type == 0) && (tl != null) && (tl.getTitle() != null) && tl.getTitle().toLowerCase().contains(search))
				{
					s_list.add(seller);
				}
				else if ((type == 1) && (tl != null) && (tl.getTitle() != null) && tl.getTitle().toLowerCase().contains(search))
				{
					s_list.add(seller);
				}
				else if ((type == 2) && (cl != null) && (seller.hasManufactureShop()) && (seller.getStoreName() != null) && seller.getStoreName().toLowerCase().contains(search))
				{
					s_list.add(seller);
				}
			}
			list = s_list;
		}
		
		if (!list.isEmpty())
		{
			L2PcInstance[] players = new L2PcInstance[list.size()];
			list.toArray(players);
			Arrays.sort(players, new PlayersComparator<L2PcInstance>());
			list.clear();
			list.addAll(Arrays.asList(players));
		}
		
		return list;
	}
	
	@Override
	public void parsewrite(String url, String arg1, String arg2, String arg3, String arg4, String arg5, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(url, "_");
		String cmd = st.nextToken();
		if ("bbsregsearch".equals(cmd))
		{
			int townId = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			String byItem = "Item".equals(arg4) ? "1" : "0";
			
			if (arg3 == null)
			{
				arg3 = "";
			}
			
			arg3 = arg3.replace("<", "");
			arg3 = arg3.replace(">", "");
			arg3 = arg3.replace("&", "");
			arg3 = arg3.replace("$", "");
			
			if (arg3.length() > 30)
			{
				arg3 = arg3.substring(0, 30);
			}
			
			cbByPass("_bbsreglist;" + townId + ";" + type + ";1;" + byItem + ";" + arg3, player);
		}
	}
	
	private static class PlayersComparator<T> implements Comparator<T>
	{
		@Override
		public int compare(Object o1, Object o2)
		{
			if ((o1 instanceof L2PcInstance) && (o2 instanceof L2PcInstance))
			{
				L2PcInstance p1 = (L2PcInstance) o1;
				L2PcInstance p2 = (L2PcInstance) o2;
				return p1.getName().compareTo(p2.getName());
			}
			return 0;
		}
	}
	
	public static RegionBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final RegionBBSManager _instance = new RegionBBSManager();
	}
}
