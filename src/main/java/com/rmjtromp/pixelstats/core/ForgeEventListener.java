package com.rmjtromp.pixelstats.core;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Mouse;

import com.rmjtromp.pixelstats.core.events.ActionbarReceiveEvent;
import com.rmjtromp.pixelstats.core.events.ChunkLoadEvent;
import com.rmjtromp.pixelstats.core.events.ChunkUnloadEvent;
import com.rmjtromp.pixelstats.core.events.ClientTickEvent;
import com.rmjtromp.pixelstats.core.events.CommandPreprocessEvent;
import com.rmjtromp.pixelstats.core.events.EntityJoinWorldEvent;
import com.rmjtromp.pixelstats.core.events.KeyPressEvent;
import com.rmjtromp.pixelstats.core.events.MessageReceiveEvent;
import com.rmjtromp.pixelstats.core.events.MessageSendEvent;
import com.rmjtromp.pixelstats.core.events.MouseInputEvent;
import com.rmjtromp.pixelstats.core.events.PacketReadEvent;
import com.rmjtromp.pixelstats.core.events.PostMessageReceiveEvent;
import com.rmjtromp.pixelstats.core.events.ReadyEvent;
import com.rmjtromp.pixelstats.core.events.RenderTickEvent;
import com.rmjtromp.pixelstats.core.events.ScoreboardUpdateEvent;
import com.rmjtromp.pixelstats.core.events.ScreenDrawingEvent;
import com.rmjtromp.pixelstats.core.events.ScreenshotEvent;
import com.rmjtromp.pixelstats.core.events.ServerJoinEvent;
import com.rmjtromp.pixelstats.core.events.ServerQuitEvent;
import com.rmjtromp.pixelstats.core.events.ServerTickEvent;
import com.rmjtromp.pixelstats.core.events.TablistUpdateEvent;
import com.rmjtromp.pixelstats.core.events.TitleReceiveEvent;
import com.rmjtromp.pixelstats.core.events.WorldLoadEvent;
import com.rmjtromp.pixelstats.core.events.WorldUnloadEvent;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.Listener;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public final class ForgeEventListener {
	
    private ChannelDuplexHandler handler = null;

    @SubscribeEvent
    public void onClientConnectedToServer(ClientConnectedToServerEvent e) {
    	ServerData data = Minecraft.getMinecraft().getCurrentServerData();
    	if(data != null) EventsManager.callEvent(new ServerJoinEvent(data, e.isLocal));
    	if(!e.isLocal) {
    		handler = new ChannelDuplexHandler() {
    			@Override
    			public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
    				EventsManager.callEvent(new PacketReadEvent((Packet<?>) packet));
    				if(packet instanceof S02PacketChat) {
    					S02PacketChat p = (S02PacketChat) packet;
    					
    					final IChatComponent message = p.getChatComponent();
    					if(!p.isChat()) { // for some reason its opposite
							MessageReceiveEvent MRE = new MessageReceiveEvent(message);
        					Listener listener = new Listener() {
        						
        						private int tick = 0;
        						@EventHandler
        						public void onTick(ClientTickEvent e) {
        							tick++;
        							if(tick < 21) {
            							List<ChatLine> lines = getChatLines();
            							for(int i = 0; i < (lines.size() > 10 ? 10 : lines.size()); i++) {
            								ChatLine line = lines.get(0);
            								if(line.getChatComponent().equals(message)) {
            									PostMessageReceiveEvent PMRE = new PostMessageReceiveEvent(line);
            									EventsManager.callEvent(PMRE);
            									ClientTickEvent.getHandlerList().unregister(this);
            									break;
            								}
            							}
        							} else ClientTickEvent.getHandlerList().unregister(this);
        						}
        						
        					};
        					EventsManager.callEvent(MRE);
        					if(MRE.isCancelled()) return;
        					packet = new S02PacketChat(MRE.getMessage(), p.getType());
        					EventsManager.registerEvents(listener);
    					} else {
    						ActionbarReceiveEvent ARE = new ActionbarReceiveEvent(message);
							EventsManager.callEvent(ARE);
							if(ARE.isCancelled()) return;
    					}
    				} else if(packet instanceof S3APacketTabComplete) {
    					S3APacketTabComplete p = (S3APacketTabComplete)packet;
    					System.out.print(String.join(", ", p.func_149630_c())+"\n");
    				} else if(packet instanceof S45PacketTitle) {
    					S45PacketTitle p = (S45PacketTitle) packet;
    					TitleReceiveEvent TRE = new TitleReceiveEvent(p.getMessage(), p.getFadeInTime(), p.getDisplayTime(), p.getFadeOutTime(), p.getType());
    					EventsManager.callEvent(TRE);
    					if(TRE.isCancelled()) return;
    				} else if(packet instanceof S3DPacketDisplayScoreboard || packet instanceof S3CPacketUpdateScore || packet instanceof S3DPacketDisplayScoreboard || packet instanceof S3EPacketTeams) {
    					ScoreboardUpdateEvent SUE = new ScoreboardUpdateEvent();
    					EventsManager.callEvent(SUE);
    				} else if(packet instanceof S38PacketPlayerListItem) {
    					S38PacketPlayerListItem p = (S38PacketPlayerListItem) packet;
    					TablistUpdateEvent TUE = new TablistUpdateEvent(p.func_179768_b(), p.func_179767_a());
    					EventsManager.callEvent(TUE);
    					if(TUE.wasManipulated()) {
    						Field playersField = S38PacketPlayerListItem.class.getDeclaredField("players");
    						playersField.setAccessible(true);
    						Field modifiersField = Field.class.getDeclaredField("modifiers");
    					    modifiersField.setAccessible(true);
    					    modifiersField.setInt(playersField, playersField.getModifiers() & ~Modifier.FINAL);
    					    
    					    playersField.set(p, TUE.getData());
    					    packet = p;
    					}
    				}
    				super.channelRead(ctx, packet);
    			}
    			
    			@Override
    			public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
    				super.write(ctx, packet, promise);
    			}
    		};
    		e.manager.channel().pipeline().addBefore("packet_handler", "pixelstats", handler);
    	}
    }
    
    @SubscribeEvent
    public void onClientDisconnectionFromServer(ClientDisconnectionFromServerEvent e) {
    	ServerData data = Minecraft.getMinecraft().getCurrentServerData();
    	EventsManager.callEvent(new ServerQuitEvent(data));
    	try {
    		if(handler != null) {
        		e.manager.channel().pipeline().remove(handler);
        		handler = null;
    		}
    	} catch(NoSuchElementException e1) { }
    	
    }
    
	private boolean a = false;
	@SubscribeEvent
	public void onScreenDrawing(DrawScreenEvent.Post e) {
		EventsManager.callEvent(new ScreenDrawingEvent(e.gui));
	    if(e.gui instanceof GuiMainMenu && !a) {
	    	a = !a;
	    	EventsManager.callEvent(new ReadyEvent());
	    }
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent e) {
		EventsManager.callEvent(new RenderTickEvent());
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent e) {
		EventsManager.callEvent(new ClientTickEvent());
	}
	
	// THIS IS SERVERSIDE ONLY
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent e) {
		EventsManager.callEvent(new ServerTickEvent()); 
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent e) {
		KeyPressEvent KPE = new KeyPressEvent();
		EventsManager.callEvent(KPE);
		if(KPE.isCancelled()) e.setCanceled(true);

		if(Minecraft.getMinecraft().gameSettings.keyBindScreenshot.isPressed()) {
			CompletableFuture.runAsync(new Runnable() {
				
				@Override
				public void run() {
					try {
						Field field = GuiNewChat.class.getDeclaredField("chatLines");
						field.setAccessible(true);
						
						@SuppressWarnings("unchecked")
						List<ChatLine> chatLines = (List<ChatLine>) field.get(Minecraft.getMinecraft().ingameGUI.getChatGUI());
						Pattern pattern = Pattern.compile("^Saved screenshot as (.*?\\.png)$");
						int max = chatLines.size() < 10 ? chatLines.size() : 10;
						
						for(int i = 0; i < max; i++) {
							ChatLine line = chatLines.get(i);
							Matcher matcher = pattern.matcher(line.getChatComponent().getUnformattedText());
							if(matcher.find()) {
								File ssfile = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "screenshots" + File.separator + matcher.group(1));

								ScreenshotEvent SE = new ScreenshotEvent(ssfile, line);
								EventsManager.callEvent(SE);
								break;
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			});
		}
	}
	
	@SubscribeEvent
	public void onMouseInput(GuiScreenEvent.MouseInputEvent.Post e) {
        EventsManager.callEvent(new MouseInputEvent(e.gui, Mouse.getEventX(), Mouse.getEventY(), Mouse.getEventButton()));
	}
	
	@SubscribeEvent
	public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Post e) {
//		GuiScreen gui = e.gui;
	}
	
	private List<ChatLine> getChatLines() {
		try {
			Field field = GuiNewChat.class.getDeclaredField("chatLines");
			field.setAccessible(true);
			
			@SuppressWarnings("unchecked")
			List<ChatLine> chatLines = (List<ChatLine>) field.get(Minecraft.getMinecraft().ingameGUI.getChatGUI());
			return chatLines;
		} catch(Exception e1) {
			return new ArrayList<>();
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld1(net.minecraftforge.event.entity.EntityJoinWorldEvent e) {
		if(e.entity instanceof EntityFallingBlock) {
			if(e.isCancelable()) e.setCanceled(true);
			else e.entity.setInvisible(true);
		}
	}
	
	@SubscribeEvent
	public void onClientChat(ServerChatEvent e) {
		MessageSendEvent MSE = new MessageSendEvent(e.message);
		EventsManager.callEvent(MSE);
		if(MSE.isCancelled()) e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onCommand(CommandEvent e) {
		CommandPreprocessEvent CPE = new CommandPreprocessEvent(e.command, e.parameters);
		EventsManager.callEvent(CPE);
		if(CPE.isCancelled()) e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load e) {
		EventsManager.callEvent(new ChunkLoadEvent(e.getChunk()));
	}
	
	@SubscribeEvent
	public void onChunkUnLoad(ChunkEvent.Unload e) {
		EventsManager.callEvent(new ChunkUnloadEvent(e.getChunk()));
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(net.minecraftforge.event.entity.EntityJoinWorldEvent e) {
		EventsManager.callEvent(new EntityJoinWorldEvent(e.entity));
	}
	
	private long lwl = 0, lwu = 0;
	@SubscribeEvent
	public void on(net.minecraftforge.event.world.WorldEvent.Load e) {
		if(System.currentTimeMillis() - lwl < 1000) return;
		lwl = System.currentTimeMillis();
		EventsManager.callEvent(new WorldLoadEvent(e.world));
	}

	@SubscribeEvent
	public void on(net.minecraftforge.event.world.WorldEvent.Unload e) {
		if(System.currentTimeMillis() - lwu < 1000) return;
		lwu = System.currentTimeMillis();
		EventsManager.callEvent(new WorldUnloadEvent(e.world));
	}
	
}
