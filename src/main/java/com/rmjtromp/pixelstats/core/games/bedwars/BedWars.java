package com.rmjtromp.pixelstats.core.games.bedwars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;

import com.rmjtromp.pixelstats.core.EventsManager;
import com.rmjtromp.pixelstats.core.Hypixel;
import com.rmjtromp.pixelstats.core.Hypixel.GameActivity;
import com.rmjtromp.pixelstats.core.events.KeyPressEvent;
import com.rmjtromp.pixelstats.core.events.MessageReceiveEvent;
import com.rmjtromp.pixelstats.core.events.MouseInputEvent;
import com.rmjtromp.pixelstats.core.games.AbstractGame;
import com.rmjtromp.pixelstats.core.games.bedwars.gui.BedwarsOverlay;
import com.rmjtromp.pixelstats.core.games.bedwars.gui.HypixelProfileGUI;
import com.rmjtromp.pixelstats.core.utils.AntiSpam;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.ComponentUtils;
import com.rmjtromp.pixelstats.core.utils.Console;
import com.rmjtromp.pixelstats.core.utils.HTTPRequest;
import com.rmjtromp.pixelstats.core.utils.Multithreading;
import com.rmjtromp.pixelstats.core.utils.ReflectionUtil;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile;

import net.hypixel.api.util.GameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public class BedWars extends AbstractGame implements Listener {

	private static GuiPlayerTabOverlay OVERLAY_DEFAULT = null;
	private static BedwarsOverlay OVERLAY_BEDWARS = null;
	
	static {
		Multithreading.runAsync(() -> {
			try {
				Console.debug("Fetching bwstats endpoint url...");
				HttpURLConnection connection = HTTPRequest.get("https://raw.githubusercontent.com/Boom22545/overlay/master/loginurl.txt");
				if(connection != null && connection.getResponseCode() == 200) {
					// Get Response
					InputStream is = connection.getInputStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
					StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
					String line;
					while ((line = rd.readLine()) != null) {
						response.append(line);
						response.append('\r');
					}
					rd.close();
					
					bwStatsEndpointUrl = response.toString();
					Console.info("Endpoint url fetched: ", response.toString());
				} else Console.error(connection == null ? "Connection is null" : "Server responded with: ", connection.getResponseCode());
			} catch(IOException e) {
				Console.error("Error fetching bwstats endpoint url: ", e);
				e.printStackTrace();
			}
		});
	}
	
	private final Field chatLinesField, lineStringField, scrollPositionField, overlayPlayerListField, isScrolledField, field_146253_i;
	private final Method setChatLineMethod, setTextMethod;
	public BedWars() throws NoSuchFieldException, NoSuchMethodException {
		super(GameType.BEDWARS);
		overlayPlayerListField = ReflectionUtil.findField(GuiIngame.class, "field_175196_v", "overlayPlayerList");
		scrollPositionField = ReflectionUtil.findField(GuiNewChat.class, "field_146250_j", "scrollPos");
		isScrolledField = ReflectionUtil.findField(GuiNewChat.class, "field_146251_k", "isScrolled");
		chatLinesField = ReflectionUtil.findField(GuiNewChat.class, "field_146252_h", "chatLines");
		field_146253_i = ReflectionUtil.findField(GuiNewChat.class, "field_146253_i");
		lineStringField = ReflectionUtil.findField(ChatLine.class, "field_74541_b", "lineString");
		setChatLineMethod = ReflectionUtil.findMethod(GuiNewChat.class, Arrays.asList("func_146237_a", "setChatLine"), IChatComponent.class, int.class, int.class, boolean.class);
		setTextMethod = ReflectionUtil.findMethod(GuiChat.class, Arrays.asList("func_175274_a", "setText"), String.class, boolean.class);
	}
	
	@Override
	public void initialize() {
		Console.log("Initializing Bedwars");
		EventsManager.registerEvents(this);
		
		if(OVERLAY_DEFAULT == null) OVERLAY_DEFAULT = Minecraft.getMinecraft().ingameGUI.getTabList();
		if(OVERLAY_BEDWARS == null) {
			try {
				OVERLAY_BEDWARS = new BedwarsOverlay(Minecraft.getMinecraft());
			} catch (NoSuchFieldException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		if(OVERLAY_BEDWARS != null) {
			OVERLAY_BEDWARS.initialize(OVERLAY_DEFAULT);
			setTablistOverlay(OVERLAY_BEDWARS);
		}
	}

	@Override
	public void uninitialize() {
		Console.log("Uninitializing Bedwars");
		HandlerList.unregisterAll(this);
		if(OVERLAY_DEFAULT != null) {
			try {
				if(OVERLAY_BEDWARS != null) OVERLAY_BEDWARS.uninitialize(OVERLAY_DEFAULT);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			setTablistOverlay(OVERLAY_DEFAULT);
		}
	}
	
	@EventHandler
	public void onMouseInput(MouseInputEvent e) {
		if(e.getGuiScreen() instanceof GuiChat && e.getButton() != -1) {
			GuiChat chat = (GuiChat) e.getGuiScreen();
			IChatComponent component = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatComponent(e.getX(), e.getY());
			if (component != null) {
	            ClickEvent clickevent = component.getChatStyle().getChatClickEvent();
	            // 0 left
	            // 1 right
	            // 2 middle
	             if (clickevent != null) {
	                if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
	                	if(clickevent.getValue().matches("^pixelstats:\\w{1,16}$")) {
	                		String username = clickevent.getValue().split(":", 2)[1];
	                		if(e.getButton() == 0) {
	                			if(GuiScreen.isShiftKeyDown()) {
	                				Minecraft.getMinecraft().displayGuiScreen(new HypixelProfileGUI(HypixelProfile.get(username)));
	                			} else {
									try {
				                		setTextMethod.invoke(chat, "/p invite "+username, true);
									} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
										e1.printStackTrace();
									}
	                			}
		                	} else if(e.getButton() == 1) {
		                		// view profile
								HypixelProfile profile = HypixelProfile.get(username);
		                		profile.openProfileGUI();
		                	} else if(e.getButton() == 2) {
		                		// copy username
		                		GuiScreen.setClipboardString(username);
		                	}
	                	}
	                }
	            }
	        }
		}
	}
	
	private static final String MESSAGE_SEPARATOR_PATTERN = "(?=(?:(?:§[0-9a-fl-or]){0,}:))";
	private static final Pattern LOBBY_CHAT_PATTERN = Pattern.compile("^\\[\\d+.\\] (?:\\[(.+?)\\] )?(\\w{1,16}): (.*?)$");
	private static final Pattern IN_GAME_CHAT_PATTERN = Pattern.compile("^\\[(.*?)\\] \\[.*?\\] ((?:\\[(.*?)\\] )?(\\w{1,16})): (.*?)$");
	private static final Pattern AWAITING_GAME_CHAT_PATTERN = Pattern.compile("^((?:\\[(.+?)\\] )?(\\w{1,16})): (.*?)$");
	private static final Pattern PLAYER_STREAM_PATTERN = Pattern.compile("^(\\w{1,16}) (?:has (?:joined \\(\\d+\\/\\d+\\)|quit)!|reconnected|disconnected)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern WEIRD_SYMBOL_PATTERN = Pattern.compile("[^\\w\\s!@#\\$%\\^&\\*\\(\\)\\{\\}\\[\\]\\.'\";:<,>?\\/\\\\+_=-`~]", Pattern.UNICODE_CASE);
	private static final Pattern DISCORD_INVITE_PATTERN = Pattern.compile("discord\\.gg\\/\\w+", Pattern.CASE_INSENSITIVE);
	
	private static final List<Long> responseTime = new ArrayList<>();
	private static long lastUpdate = 0L;
	private static long averageResponseTime = 0;
	
	public static long getAverageResponseTime() {
		if(System.currentTimeMillis() - lastUpdate > 1000) {
			lastUpdate = System.currentTimeMillis();
			if(responseTime.isEmpty()) averageResponseTime = 0;
			else {
				long a = 0L;
				for(long r : responseTime) a+=r;
				averageResponseTime = a / responseTime.size();
			}
		}
		return averageResponseTime;
	}
	
	@EventHandler
	public void onKeyPress(KeyPressEvent e) {
		if(Keyboard.isKeyDown(Keyboard.KEY_DELETE) && e.isCtrlDown()) {
			responseTime.clear();
		}
	}
	
	@EventHandler
	public void onMessageReceive(MessageReceiveEvent e) {
		long start = System.currentTimeMillis();
		final String strippedMessage = ChatColor.stripcolor(e.getMessage().getUnformattedText());
		final IChatComponent component = e.getMessage();
		final String message = component.getFormattedText();
		final GameActivity status = Hypixel.getInstance().getActivity();
		
		if(status != null) {
			if(status.equals(GameActivity.LOBBY)) {
				if(strippedMessage.length() > 1 && strippedMessage.charAt(0) == '[' && Character.isDigit(strippedMessage.charAt(1))) {
					Matcher matcher = LOBBY_CHAT_PATTERN.matcher(strippedMessage);
					if(matcher.matches()) {
						final String username = matcher.group(2);
						final String[] msg = message.split(MESSAGE_SEPARATOR_PATTERN, 2);
						final String displayName = msg[0].split(" ", 2)[1];
						
						final String strippedMsg = WEIRD_SYMBOL_PATTERN.matcher(ChatColor.stripcolor(msg[1])).replaceAll("");
						if(strippedMsg.toLowerCase().contains("discord") && DISCORD_INVITE_PATTERN.matcher(strippedMsg).find()) {
							e.setCancelled(true);
							return;
						}
						
						
						HypixelProfile profile = HypixelProfile.get(username, hypixelProfile -> {
							String index = hypixelProfile.getBedwars() == null ? ChatColor.DARK_RED + '\u2589' : hypixelProfile.getBedwars().getIndex().toString();
							
							IChatComponent left1 = new ChatComponentText(String.format("%s %s", index, msg[0])).setChatStyle(getStyle(hypixelProfile, hypixelProfile.hasDisplayName() ? hypixelProfile.getDisplayName() : displayName));
							IChatComponent right1 = new ChatComponentText(AntiSpam.replaceRepeatingCharacters(msg[1]));
							try {
								editChatLine(e.getMessage(), ComponentUtils.join("", left1, right1));
							} catch (IllegalAccessException | InvocationTargetException e1) {
								e1.printStackTrace();
							}
						});

						String index = profile.getBedwars() == null ? ChatColor.DARK_RED + '\u2589' : profile.getBedwars().getIndex().toString();
						
						IChatComponent left1 = new ChatComponentText(String.format("%s %s", index, msg[0])).setChatStyle(getStyle(profile, profile.hasDisplayName() ? profile.getDisplayName() : displayName));
						IChatComponent right1 = new ChatComponentText(Minecraft.getMinecraft().thePlayer.getName().equals(username) ? msg[1] : AntiSpam.replaceRepeatingCharacters(msg[1]));
						e.setMessage(ComponentUtils.join("", left1, right1));
						responseTime.add(System.currentTimeMillis() - start);
					}
				}
			} else if(status.equals(GameActivity.IN_GAME)) {
				Matcher m1 = IN_GAME_CHAT_PATTERN.matcher(strippedMessage);
				Matcher m2 = AWAITING_GAME_CHAT_PATTERN.matcher(strippedMessage);
				Matcher m3 = PLAYER_STREAM_PATTERN.matcher(strippedMessage);
				
				if(m1.matches()) {
					final String type = m1.group(1); // either SHOUT or their star level
					final String username = m1.group(4);
					final String[] msg = message.split(MESSAGE_SEPARATOR_PATTERN, 2);
					final String displayName = msg[0].split(" ", 3)[2];
					
					HypixelProfile profile = HypixelProfile.get(username, hypixelProfile -> {
						String index = hypixelProfile.getBedwars() == null ? ChatColor.DARK_RED + '\u2589' : hypixelProfile.getBedwars().getIndex().toString();
						String level = hypixelProfile.getBedwars() == null ? ChatColor.DARK_RED + "[~0"+'\u272B'+"]" : hypixelProfile.getBedwars().getLevel().toString();
						
						String leftHandMessage = msg[0];
						if(type.equals("SHOUT") || type.equals("SPECTATOR")) {
							String[] brokenMsg = msg[0].split(" ", 2);
							leftHandMessage = String.format("%s %s %s", brokenMsg[0], level, brokenMsg[1]);
						}
						
						IChatComponent left1 = new ChatComponentText(String.format("%s %s", index, leftHandMessage)).setChatStyle(getStyle(hypixelProfile, hypixelProfile.hasDisplayName() ? hypixelProfile.getDisplayName() : displayName));
						IChatComponent right1 = new ChatComponentText(msg[1]);
						try {
							editChatLine(e.getMessage(), ComponentUtils.join("", left1, right1));
						} catch (IllegalAccessException | InvocationTargetException e1) {
							e1.printStackTrace();
						}
					});

					String index = profile.getBedwars() == null ? ChatColor.DARK_RED + '\u2589' : profile.getBedwars().getIndex().toString();
					String level = profile.getBedwars() == null ? ChatColor.DARK_RED + "[~0"+'\u272B'+"]" : profile.getBedwars().getLevel().toString();
					String leftHandMessage = msg[0];
					if(type.equals("SHOUT") || type.equals("SPECTATOR")) {
						String[] brokenMsg = msg[0].split(" ", 2);
						leftHandMessage = String.format("%s %s %s", brokenMsg[0], level, brokenMsg[1]);
					}
					
					IChatComponent left1 = new ChatComponentText(String.format("%s %s", index, leftHandMessage)).setChatStyle(getStyle(profile, profile.hasDisplayName() ? profile.getDisplayName() : displayName));
					IChatComponent right1 = new ChatComponentText(msg[1]);
					e.setMessage(ComponentUtils.join("", left1, right1));
					responseTime.add(System.currentTimeMillis() - start);
				} else if(m2.matches()) {
					final String username = m2.group(3);
					final String[] msg = message.split(MESSAGE_SEPARATOR_PATTERN, 2);
					final String displayName = msg[0];
					
					HypixelProfile profile = HypixelProfile.get(username, hypixelProfile -> {
						String index = hypixelProfile.getBedwars() == null ? ChatColor.DARK_RED + '\u2589' : hypixelProfile.getBedwars().getIndex().toString();
						String level = hypixelProfile.getBedwars() == null ? ChatColor.DARK_RED + "[~0"+'\u272B'+"]" : hypixelProfile.getBedwars().getLevel().toString();
						
						IChatComponent left1 = new ChatComponentText(String.format("%s %s %s", index, level, msg[0])).setChatStyle(getStyle(hypixelProfile, hypixelProfile.hasDisplayName() ? hypixelProfile.getDisplayName() : displayName));
						IChatComponent right1 = new ChatComponentText(msg[1]);
						try {
							editChatLine(e.getMessage(), ComponentUtils.join("", left1, right1));
						} catch (IllegalAccessException | InvocationTargetException e1) {
							e1.printStackTrace();
						}
					});

					String index = profile.getBedwars() == null ? ChatColor.DARK_RED + '\u2589' : profile.getBedwars().getIndex().toString();
					String level = profile.getBedwars() == null ? ChatColor.DARK_RED + "[~0"+'\u272B'+"]" : profile.getBedwars().getLevel().toString();
					
					IChatComponent left1 = new ChatComponentText(String.format("%s %s %s", index, level, msg[0])).setChatStyle(getStyle(profile, profile.hasDisplayName() ? profile.getDisplayName() : displayName));
					IChatComponent right1 = new ChatComponentText(AntiSpam.replaceRepeatingCharacters(msg[1]));
					e.setMessage(ComponentUtils.join("", left1, right1));
					responseTime.add(System.currentTimeMillis() - start);
				} else if(m3.matches()) {
					final String username = m3.group(1);
					final String[] msgBreakdown = component.getFormattedText().split(" ", 2);
					final String displayName = msgBreakdown[0];
					final String rightHandMessage = msgBreakdown[1];
					final String lastColor = ChatColor.getLastColors(displayName);
					
					HypixelProfile profile = HypixelProfile.get(username, hypixelProfile -> {
						String index = hypixelProfile.getBedwars() == null ? ChatColor.DARK_RED + '\u2589' : hypixelProfile.getBedwars().getIndex().toString();
						String level = hypixelProfile.getBedwars() == null ? ChatColor.DARK_RED + "[~0"+'\u272B'+"]" : hypixelProfile.getBedwars().getLevel().toString();
						
						IChatComponent left1 = new ChatComponentText(String.format("%s %s %s", index, level, displayName)).setChatStyle(getStyle(hypixelProfile, hypixelProfile.hasDisplayName() ? hypixelProfile.getDisplayName() : displayName));
						IChatComponent right1 = new ChatComponentText(lastColor + " " + rightHandMessage);
						try {
							editChatLine(e.getMessage(), ComponentUtils.join("", left1, right1));
						} catch (IllegalAccessException | InvocationTargetException e1) {
							e1.printStackTrace();
						}
					});
					
					String index = profile.getBedwars() == null ? ChatColor.DARK_RED + '\u2589' : profile.getBedwars().getIndex().toString();
					String level = profile.getBedwars() == null ? ChatColor.DARK_RED + "[~0"+'\u272B'+"]" : profile.getBedwars().getLevel().toString();
					
					IChatComponent left1 = new ChatComponentText(String.format("%s %s %s", index, level, displayName)).setChatStyle(getStyle(profile, profile.hasDisplayName() ? profile.getDisplayName() : displayName));
					IChatComponent right1 = new ChatComponentText(lastColor + " " + rightHandMessage);
					e.setMessage(ComponentUtils.join("", left1, right1));
					responseTime.add(System.currentTimeMillis() - start);
				}
			}
		}
	}
	
	private static String bwStatsEndpointUrl = null;
	public static String getEndpointUrl() {
		return bwStatsEndpointUrl;
	}
	
	private ChatStyle getStyle(HypixelProfile profile, String displayName) {
		IChatComponent hoverMessage;
		if(profile.getBedwars() == null) hoverMessage = ComponentUtils.join("\n", "&cUnable to find any information for", "&cthis player. They are likely nicked!");
		else hoverMessage = ComponentUtils.join("\n", displayName, "&7Final K/D Ratio: &7"+profile.getBedwars().getFKDR(), "&7Bed Break/Lose Ratio: &7"+profile.getBedwars().getBBLR(), "&7Win/Lose Ratio: &7"+profile.getBedwars().getWLR(), "&7Winstreak: &7"+profile.getBedwars().getWinstreak());

		ChatStyle style = new ChatStyle();
		style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
		
		if(!Minecraft.getMinecraft().thePlayer.getName().equals(profile.getName())) hoverMessage.appendSibling(ChatColor.colorEncodeToComponent("\n\n&eClick to invite to party"));
		style.setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "pixelstats:"+profile.getName()));
		return style;
	}
	
	@SuppressWarnings("unchecked")
	private List<ChatLine> getChatLines() throws IllegalAccessException {
		return (List<ChatLine>) chatLinesField.get(Minecraft.getMinecraft().ingameGUI.getChatGUI());
	}
	
	private void editChatLine(IChatComponent oldComponent, IChatComponent newComponent) throws IllegalAccessException, InvocationTargetException {
		List<ChatLine> lines = getChatLines();
		for(ChatLine line : lines) {
			if(line.getChatComponent().equals(oldComponent)) {
				lineStringField.set(line, newComponent);
				
				refreshChatNoFlicker();
				break;
			}
		}
	}
	
	private void refreshChatNoFlicker() throws IllegalAccessException, InvocationTargetException {
		int scrollPosition = (int) scrollPositionField.get(Minecraft.getMinecraft().ingameGUI.getChatGUI());
		boolean isScrolling = (boolean) isScrolledField.get(Minecraft.getMinecraft().ingameGUI.getChatGUI());
		field_146253_i.set(Minecraft.getMinecraft().ingameGUI.getChatGUI(), new ArrayList<ChatLine>());
		
		List<ChatLine> chatLines = getChatLines();
		for (int i = chatLines.size() - 1; i >= 0; --i) {
            ChatLine chatline = chatLines.get(i);
            setChatLineMethod.invoke(Minecraft.getMinecraft().ingameGUI.getChatGUI(), chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
		
		if(isScrolling) Minecraft.getMinecraft().ingameGUI.getChatGUI().scroll(scrollPosition);
	    
	}
	
	private void setTablistOverlay(GuiPlayerTabOverlay overlay) {
		try {
			overlayPlayerListField.set(Minecraft.getMinecraft().ingameGUI, overlay);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
