package com.rmjtromp.pixelstats.core.games.bedwars.gui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.rmjtromp.pixelstats.core.Hypixel;
import com.rmjtromp.pixelstats.core.Hypixel.GameActivity;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.ReflectionUtil;
import com.rmjtromp.pixelstats.core.utils.TabColumn;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars.BedwarsStats;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars.Tag;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BedwarsOverlay extends GuiPlayerTabOverlay {
	
    private final Minecraft mc;

	private final Field headerField, footerField, comparatorOrderingField, locationSkinField;
	private final Method drawScoreboardValuesMethod;
	public BedwarsOverlay(Minecraft mcIn) throws NoSuchFieldException, NoSuchMethodException {
		super(mcIn, mcIn.ingameGUI);
		mc = mcIn;
		
		headerField = ReflectionUtil.findField(GuiPlayerTabOverlay.class, "field_175256_i", "header");
		footerField = ReflectionUtil.findField(GuiPlayerTabOverlay.class, "field_175255_h", "footer");
		comparatorOrderingField = ReflectionUtil.findField(GuiPlayerTabOverlay.class, "field_175252_a");
		locationSkinField = ReflectionUtil.findField(NetworkPlayerInfo.class, "field_178865_e", "locationSkin");
		drawScoreboardValuesMethod = ReflectionUtil.findMethod(GuiPlayerTabOverlay.class, Arrays.asList("func_175247_a", "drawScoreboardValues"), ScoreObjective.class, int.class, String.class, int.class, int.class, NetworkPlayerInfo.class);
	}
	
	public void initialize(GuiPlayerTabOverlay overlay) {
		try {
			setHeader((IChatComponent) headerField.get(overlay));
			setFooter((IChatComponent) footerField.get(overlay));
			comparatorOrderingField.set(this, comparatorOrderingField.get(overlay));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void uninitialize(GuiPlayerTabOverlay overlay) throws IllegalAccessException {
		overlay.setHeader(getHeader());
		overlay.setFooter(getFooter());
		comparatorOrderingField.set(overlay, comparatorOrderingField.get(this));
	}
	
	@SuppressWarnings("unchecked")
	private Ordering<NetworkPlayerInfo> getComparatorOrdering() throws IllegalAccessException {
		return (Ordering<NetworkPlayerInfo>) comparatorOrderingField.get(this);
	}
	
	private IChatComponent getHeader() throws IllegalAccessException {
		return (IChatComponent) headerField.get(this);
	}
	
	private IChatComponent getFooter() throws IllegalAccessException {
		return (IChatComponent) footerField.get(this);
	}
	
	private TablistEntryArray getTablistEntries() {
        TablistEntryArray entries = new TablistEntryArray();
		NetHandlerPlayClient nethandlerplayclient = this.mc.thePlayer.sendQueue;
		try {
			List<NetworkPlayerInfo> list = getComparatorOrdering().<NetworkPlayerInfo>sortedCopy(nethandlerplayclient.getPlayerInfoMap());
	        if(list != null) {
	            list = list.stream().filter(np -> !ChatColor.stripcolor(super.getPlayerName(np)).startsWith("[NPC]")).collect(Collectors.toList());
	            list = list.stream().filter(np -> np.getPlayerTeam() == null || !np.getPlayerTeam().getTeamName().equalsIgnoreCase("a999-76d80d5f")).collect(Collectors.toList());
	    		list.stream().map(TablistEntry::new).forEach(entries::add);
	        }
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return entries;
	}
	
	@Override
	public void renderPlayerlist(int width, Scoreboard scoreboard, ScoreObjective scoreObjective) {
		TablistEntryArray entries = getTablistEntries();
		if(entries.isEmpty()) { return; }
		
		try {
	        entries.sort((np1, np2) -> {
	    		// if their bw is null, means they're likely a nicked player therefore needs to be listed higher
	    		double pp1 = np1.getHypixelProfile().isNick() ? Double.MAX_VALUE : np1.getHypixelProfile().getBedwars().getIndex().getValue();
	    		double pp2 = np2.getHypixelProfile().isNick() ? Double.MAX_VALUE : np2.getHypixelProfile().getBedwars().getIndex().getValue();
	    		return (int)(pp1 - pp2);
	        });
		} catch(Exception e) {
			e.printStackTrace();
		}
        
        // if list is bigger than 40 remove the least threatening players from the list
        if(entries.size() > 40) {
            Collections.reverse(entries);
            for(int i = 40; i < entries.size(); i++) entries.remove(i);
            Collections.reverse(entries);
        }

		int i = 0;
		int j = 0;
		int padding = !Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME) ? 0 : mc.fontRendererObj.FONT_HEIGHT;
		
		TabColumn[] columns = !Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME) ? new TabColumn[]{entries.getColumns()[0]} : Arrays.stream(entries.getColumns()).filter(TabColumn::shouldShowColumn).collect(Collectors.toList()).toArray(new TabColumn[0]); 
		for(TabColumn c : entries.getColumns()) i += c.getWidestValueWidth();
		i += (columns.length - 1) * entries.spacer;
		
		
		for(TablistEntry entry : entries) {
            if (scoreObjective != null && scoreObjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                j = Math.max(j, this.mc.fontRendererObj.getStringWidth(" " + scoreboard.getValueFromObjective(entry.getNetworkPlayerInfo().getGameProfile().getName(), scoreObjective).getScorePoints()));
            }
		}
		
        int l3 = entries.size();
        int i4 = l3;
        int j4;

        for (j4 = 1; i4 > 20; i4 = (l3 + j4 - 1) / j4) ++j4;

        boolean flag = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
        int l;

        if (scoreObjective != null) l = scoreObjective.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS ? 90 : j;
        else l = 0;

        int i1 = Math.min(j4 * ((flag ? 9 : 0) + i + l + 13), width - 50) / j4;
        int j1 = width / 2 - (i1 * j4 + (j4 - 1) * 5) / 2;
        int k1 = 10;
        int l1 = i1 * j4 + (j4 - 1) * 5;
        List<String> list1 = null;
        List<String> list2 = null;

        try {
			if (getHeader() != null) {
				IChatComponent header = getHeader();
			    list1 = this.mc.fontRendererObj.listFormattedStringToWidth(header.getFormattedText(), width - 50);
			    for (String s : list1) l1 = Math.max(l1, this.mc.fontRendererObj.getStringWidth(s));
			}
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

        try {
			if (getFooter() != null) {
			    list2 = this.mc.fontRendererObj.listFormattedStringToWidth(getFooter().getFormattedText(), width - 50);
			    for (String s2 : list2) l1 = Math.max(l1, this.mc.fontRendererObj.getStringWidth(s2));
			}
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

        if (list1 != null) {
            drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * this.mc.fontRendererObj.FONT_HEIGHT + padding, Integer.MIN_VALUE);

            for (String s3 : list1) {	
                int i2 = this.mc.fontRendererObj.getStringWidth(s3);
                this.mc.fontRendererObj.drawStringWithShadow(s3, (float)(width / 2 - i2 / 2), (float)k1, -1);
                k1 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
            k1 += padding;
            ++k1;
        }

        drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + i4 * 9, Integer.MIN_VALUE);

        int cC = ((entries.size() - 1) / i4)+1;
        for(int c = 0; c < cC; c++) {
            int y = k1 - this.mc.fontRendererObj.FONT_HEIGHT;
            int x = ((width / 2 - l1 / 2 - 1) + c * ((cC-1) * i1 + (cC-1) * 5)) + 9;
            
            // draw labels
            for(TabColumn column : columns) {
            	if(column.getDisplayLabel()) {
            		int lx = column.shouldCenter() ? x + ((column.getWidestValueWidth() / 2) - (column.getLabelWidth() / 2)) : x;
            		this.mc.fontRendererObj.drawStringWithShadow(column.getLabel(), lx, y, -1);
            	}
                x += column.getWidestValueWidth() + entries.spacer;
            }
        }
        
        for (int k4 = 0; k4 < entries.size(); ++k4) {
            int l4 = k4 / i4;
            int i5 = k4 % i4;
            int j2 = j1 + l4 * i1 + l4 * 5;
            int k2 = k1 + i5 * 9;
            drawRect(j2, k2, j2 + i1, k2 + 8, 553648127);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            if (k4 < entries.size()) {
            	TablistEntry entry = entries.get(k4);
                NetworkPlayerInfo networkplayerinfo1 = entry.getNetworkPlayerInfo();
                HypixelProfile player = entry.getHypixelProfile();
                GameProfile gameprofile = networkplayerinfo1.getGameProfile();
                UUID uuid = gameprofile.getId();
                String s1 = columns[0].get(uuid);
                
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                if(Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME) && (Hypixel.getPlayer().equals(player) || player.isInParty())) {
                	GlStateManager.color(1.0F, 1.0F, 1.0F, .5F);
                }
                

                if (flag) {
                	if(entry.getLocationSkin() != null) {
                        EntityPlayer entityplayer = this.mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                        boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                        this.mc.getTextureManager().bindTexture(entry.getLocationSkin());
                        int l2 = 8 + (flag1 ? 8 : 0);
                        int i3 = 8 * (flag1 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(j2, k2, 8.0F, (float)l2, 8, i3, 8, 8, 64.0F, 64.0F);

                        if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                            int j3 = 8 + (flag1 ? 8 : 0);
                            int k3 = 8 * (flag1 ? -1 : 1);
                            Gui.drawScaledCustomSizeModalRect(j2, k2, 40.0F, (float)j3, 8, k3, 8, 8, 64.0F, 64.0F);
                        }
                	} else drawRect(j2, k2, 8, 8, 0x0000007F);
                    j2 += 9;
                }

                // this renders the player at low opacity if they are the client-player AND in-game
            	int opacity = Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME) && (Hypixel.getPlayer().equals(player) || player.isInParty()) ? -1862270977 : -1;
                if (networkplayerinfo1.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    s1 = EnumChatFormatting.ITALIC + s1;
                    opacity = -1862270977;
                }

                float x = (float)j2;
                for(TabColumn column : columns) {
                	String value = column.get(uuid);
                	if(value != null) this.mc.fontRendererObj.drawStringWithShadow(value, column.shouldCenter() ? x + (column.getWidestValueWidth() / 2 - mc.fontRendererObj.getStringWidth(value) / 2) : x, (float)k2, opacity);
                    x += column.getWidestValueWidth() + entries.spacer;
                }


                if (scoreObjective != null && networkplayerinfo1.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    int k5 = j2 + i + 1;
                    int l5 = k5 + l;

                    if (l5 - k5 > 5) {
						try {
	                    	drawScoreboardValuesMethod.invoke(this, scoreObjective, k2, gameprofile.getName(), k5, l5, networkplayerinfo1);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
                    }
                }

                this.drawPing(i1, j2 - (flag ? 9 : 0), k2, networkplayerinfo1);
            }
        }

        if (list2 != null) {
            k1 = k1 + i4 * 9 + 1;
            drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String s4 : list2) {
                int j5 = this.mc.fontRendererObj.getStringWidth(s4);
                this.mc.fontRendererObj.drawStringWithShadow(s4, (float)(width / 2 - j5 / 2), (float)k1, -1);
                k1 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
        }
	}
	
	private final class TablistEntryArray extends ArrayList<TablistEntry> {

		private static final long serialVersionUID = -3565302131857843382L;

		private int spacer = 10;
		private TabColumn[] columns = new TabColumn[] {
				new TabColumn("Player", false, false, false),
				new TabColumn("Tags"),
				new TabColumn("FKDR"),
				new TabColumn("WLR"),
				new TabColumn("WS"),
				new TabColumn("BBLR")
		};
		
		private TablistEntryArray() {}
		
		@Override
		public boolean add(TablistEntry e) {
			String color = e.getNetworkPlayerInfo().getPlayerTeam() != null && e.getNetworkPlayerInfo().getPlayerTeam().getColorPrefix() != null ? e.getNetworkPlayerInfo().getPlayerTeam().getColorPrefix() : "";
			UUID uuid = e.getHypixelProfile().getUniqueId();
			if(e.getHypixelProfile().isNick()) {
				columns[0].add(uuid, String.format("%s %s %s", ChatColor.DARK_RED + '\u2589', "[~0"+'\u272B'+"]", color+e.getHypixelProfile().getName()));
				if(Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME)) {
					columns[1].add(uuid, Tag.NICK.toString());
					columns[2].add(uuid, ChatColor.DARK_RED + "?");
					columns[3].add(uuid, ChatColor.DARK_RED + "?");
					columns[4].add(uuid, ChatColor.DARK_RED + "?");
					columns[5].add(uuid, ChatColor.DARK_RED + "?");
				}
			} else {
				BedwarsStats bwstats = e.getHypixelProfile().getBedwars();
				columns[0].add(uuid, String.format("%s %s %s", bwstats.getIndex(), bwstats.getLevel(), color+e.getHypixelProfile().getName()));
				if(Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME)) {
					columns[1].add(uuid, String.join(ChatColor.WHITE + "+", bwstats.getTags().stream().map(Object::toString).collect(Collectors.toList())));
					columns[2].add(uuid, bwstats.getFKDR());
					columns[3].add(uuid, bwstats.getWLR());
					columns[4].add(uuid, bwstats.getWinstreak());
					columns[5].add(uuid, bwstats.getBBLR());
				}
			}
			return super.add(e);
		}
		
		public TabColumn[] getColumns() {
			return columns;
		}
		
	}
	
	private final class TablistEntry {
		
		private final NetworkPlayerInfo playerInfo;
		private final HypixelProfile hypixelProfile;
		private final String[] entries = new String[5];
		
		private TablistEntry(NetworkPlayerInfo npi) {
			playerInfo = npi;
			hypixelProfile = HypixelProfile.get(npi.getGameProfile());
			
			String color = npi.getPlayerTeam() != null && npi.getPlayerTeam().getColorPrefix() != null ? npi.getPlayerTeam().getColorPrefix() : "";
			entries[0] = hypixelProfile.getBedwars() != null ? String.format("%s %s %s", hypixelProfile.getBedwars().getIndex(), hypixelProfile.getBedwars().getLevel(), color+hypixelProfile.getName()) : String.format("%s %s %s", ChatColor.DARK_RED + '\u2589', "[~0"+'\u272B'+"]", color+playerInfo.getGameProfile().getName());
			entries[1] = Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME) && hypixelProfile.getBedwars() != null ? String.join(ChatColor.WHITE + "+", hypixelProfile.getBedwars().getTags().stream().map(Object::toString).collect(Collectors.toList())) : "";
			entries[2] = Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME) && hypixelProfile.getBedwars() != null ? hypixelProfile.getBedwars().getFKDR().toString() : "?";
			entries[3] = Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME) && hypixelProfile.getBedwars() != null ? hypixelProfile.getBedwars().getWLR().toString() : "?";
			entries[4] = Hypixel.getInstance().getActivity().equals(GameActivity.IN_GAME) && hypixelProfile.getBedwars() != null ? hypixelProfile.getBedwars().getBBLR().toString() : "?";
		}
		
		private ResourceLocation getLocationSkin() {
			ResourceLocation location = null;
			try {
				location = (ResourceLocation) locationSkinField.get(playerInfo);
				if(location == null) new Thread(playerInfo::getLocationSkin).start();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return location;
		}
		
		private NetworkPlayerInfo getNetworkPlayerInfo() {
			return playerInfo;
		}
		
		private HypixelProfile getHypixelProfile() {
			return hypixelProfile;
		}
		
	}

}
