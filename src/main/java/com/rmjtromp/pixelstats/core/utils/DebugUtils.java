package com.rmjtromp.pixelstats.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public final class DebugUtils {

	public static void printFields(Object obj) {
		if(obj != null && !(obj instanceof Class<?>)) {
			List<Field> fields = Arrays.asList(obj.getClass().getDeclaredFields());
			List<IChatComponent> components = new ArrayList<>();
			fields.forEach(field -> {
				int mod = field.getModifiers();
			    String visibility = "public";
			    boolean isFinal = false;
			    boolean isStatic = false;
			    boolean isAbstract = false;
			    if(Modifier.isPrivate(mod)) visibility = "private";
			    else if(Modifier.isProtected(mod)) visibility = "protected";
			    if(Modifier.isFinal(mod)) isFinal = true;
			    if(Modifier.isStatic(mod)) isStatic = true;
			    if(Modifier.isAbstract(mod)) isAbstract = true;
				if(!field.isAccessible()) field.setAccessible(true);
				Object value = null;
				try {
					value = field.get(obj);
				} catch (IllegalArgumentException | IllegalAccessException e1) {/* ignore */}
			    
				IChatComponent component = ComponentUtils.fromString("&e"+field.getName());
				List<String> hoverComponents = new ArrayList<>();
				
				if(value != null) {
					if(value instanceof List<?>) {
						try {
							List<?> list = (List<?>) field.get(obj);
							Class<?> listItemType = !list.isEmpty() ? list.get(0).getClass() : null;
							String type = String.format("&7type: &fList<%s>", listItemType != null ? listItemType.getSimpleName() : "?");
							hoverComponents.add(type);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							hoverComponents.add("&7type: &fList<?>");
						}
					} else hoverComponents.add("&7type: &f"+value.getClass().getSimpleName());
					
					if(value instanceof Number) {
						hoverComponents.add("&7value: &f" + value);
					} else if(value instanceof Boolean) {
						hoverComponents.add("&7value: &f" + Boolean.toString((boolean) value));
					} else if(value instanceof String) {
						hoverComponents.add("&7value: &f" + value);
					}
				}
				
				hoverComponents.add("&7visibility: &f" + visibility);
				hoverComponents.add("&7final: &f" + (isFinal ? "true" : "false"));
				hoverComponents.add("&7static: &f" + (isStatic ? "true" : "false"));
				hoverComponents.add("&7abstract: &f" + (isAbstract ? "true" : "false"));
				
				ChatStyle style = new ChatStyle();
				style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentUtils.join("\n", hoverComponents.toArray(new String[0]))));
				component.setChatStyle(style);
				components.add(component);
			});
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(ComponentUtils.fromString("&eList of fields for &6"+obj.getClass().getSimpleName()+"&e:"));
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(ComponentUtils.join("&f, ", components.toArray(new IChatComponent[0])));
		}
	}
	
	public static void printMethods(Object obj) {
		if(obj != null && !(obj instanceof Class<?>)) {
			List<Method> methods = Arrays.asList(obj.getClass().getDeclaredMethods());
			List<IChatComponent> components = new ArrayList<>();
			methods.forEach(method-> {
				int mod = method.getModifiers();
			    String visibility = "public";
			    boolean isFinal = false;
			    boolean isStatic = false;
			    boolean isAbstract = false;
			    if(Modifier.isPrivate(mod)) visibility = "private";
			    else if(Modifier.isProtected(mod)) visibility = "protected";
			    if(Modifier.isFinal(mod)) isFinal = true;
			    if(Modifier.isStatic(mod)) isStatic = true;
			    if(Modifier.isAbstract(mod)) isAbstract = true;
			    
				IChatComponent component = ComponentUtils.fromString("&e"+method.getName()+"()");
				List<String> hoverComponents = new ArrayList<>();
				
				Class<?> returnType = method.getReturnType();
				
				hoverComponents.add("&7returnType: &f"+(returnType != null ? returnType.getSimpleName() : "void"));
				List<String> paramTypes = new ArrayList<>();
				Arrays.asList(method.getParameterTypes()).forEach(c -> paramTypes.add(c.getSimpleName()));
				if(!paramTypes.isEmpty()) hoverComponents.add("&7paramTypes: &f"+String.join(", ", paramTypes));
				
				hoverComponents.add("&7visibility: &f" + visibility);
				hoverComponents.add("&7final: &f" + (isFinal ? "true" : "false"));
				hoverComponents.add("&7static: &f" + (isStatic ? "true" : "false"));
				hoverComponents.add("&7abstract: &f" + (isAbstract ? "true" : "false"));
				
				ChatStyle style = new ChatStyle();
				style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentUtils.join("\n", hoverComponents.toArray(new String[0]))));
				component.setChatStyle(style);
				components.add(component);
			});
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(ComponentUtils.fromString("&eList of methods for &6"+obj.getClass().getSimpleName()+"&e:"));
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(ComponentUtils.join("&f, ", components.toArray(new IChatComponent[0])));
		}
	}
	
	public static JsonObject getInfo(Object obj) {
		if(obj != null && !(obj instanceof Class<?>)) {
			JsonObject jsonObj = new JsonObject();
			jsonObj.addProperty("name", obj.getClass().getName());
			jsonObj.addProperty("simpleName", obj.getClass().getSimpleName());

			JsonArray fieldsArray = new JsonArray();
			Arrays.asList(obj.getClass().getDeclaredFields()).forEach(field -> {
				if(!field.isAccessible()) field.setAccessible(true);
				Object value = null;
				try { value = field.get(obj); }
				catch (IllegalArgumentException | IllegalAccessException e1) {/* ignore */}
				JsonObject fieldObj = new JsonObject();
				fieldObj.addProperty("name", field.getName());
				
				String type = "null";
				if(value != null) {
					if(value instanceof List<?>) {
						try {
							List<?> list = (List<?>) field.get(obj);
							Class<?> listItemType = !list.isEmpty() ? list.get(0).getClass() : null;
							type = String.format("List<%s>", listItemType != null ? listItemType.getSimpleName() : "?");
						} catch (IllegalArgumentException | IllegalAccessException e) {
							type = "List<?>";
						}
					} else type = value.getClass().getSimpleName();
				}
				fieldObj.addProperty("type", type);
				
				if(value != null) {
					if(value instanceof Number) {
						fieldObj.addProperty("value", (Number) value);
					} else if(value instanceof Boolean) {
						fieldObj.addProperty("value", (Boolean) value);
					} else if(value instanceof String) {
						fieldObj.addProperty("value", (String) value);
					}
				} else {
					fieldObj.addProperty("value", "null");
				}
				
				int mod = field.getModifiers();
			    String visibility = "public";
			    if(Modifier.isPrivate(mod)) visibility = "private";
			    else if(Modifier.isProtected(mod)) visibility = "protected";
			    fieldObj.addProperty("visibility", visibility);

			    fieldObj.addProperty("final", Modifier.isFinal(mod));
			    fieldObj.addProperty("static", Modifier.isStatic(mod));
			    fieldObj.addProperty("abstract", Modifier.isAbstract(mod));
			    
			    fieldsArray.add(fieldObj);
			});
			
			JsonArray methodsArray = new JsonArray();
			Arrays.asList(obj.getClass().getDeclaredMethods()).forEach(method -> {
				JsonObject methodObj = new JsonObject();
				methodObj.addProperty("name", method.getName());
				
				Class<?> returnType = method.getReturnType();
				methodObj.addProperty("returnType", (returnType != null ? returnType.getSimpleName() : "void"));
				List<String> paramTypes = new ArrayList<>();
				Arrays.asList(method.getParameterTypes()).forEach(c -> paramTypes.add(c.getSimpleName()));
				methodObj.addProperty("paramTypes", String.join(", ", paramTypes));
				
				int mod = method.getModifiers();
			    String visibility = "public";
			    if(Modifier.isPrivate(mod)) visibility = "private";
			    else if(Modifier.isProtected(mod)) visibility = "protected";
			    methodObj.addProperty("visibility", visibility);

			    methodObj.addProperty("final", Modifier.isFinal(mod));
			    methodObj.addProperty("static", Modifier.isStatic(mod));
			    methodObj.addProperty("abstract", Modifier.isAbstract(mod));
			    
			    methodsArray.add(methodObj);
			});

			jsonObj.add("fields", fieldsArray);
			jsonObj.add("methods", methodsArray);
			return jsonObj;
		}
		return null;
	}
	
}
