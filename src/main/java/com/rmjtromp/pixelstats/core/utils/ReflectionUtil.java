package com.rmjtromp.pixelstats.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class ReflectionUtil {
	
	private ReflectionUtil() {}

	public static Field findField(Class<?> arg0, String...arg2) throws NoSuchFieldException {
		Field field = null;
		NoSuchFieldException NSFE = null;
		IllegalArgumentException IAE = null;
		for(String arg : arg2) {
			try {
				field = arg0.getDeclaredField(arg);
				break;
			} catch(IllegalArgumentException e) {
				IAE = e;
			} catch(NoSuchFieldException e) {
				NSFE = e;
			}
		}
		if(field != null) {
			if(!field.isAccessible()) field.setAccessible(true);
			
			if(Modifier.isFinal(field.getModifiers())) {
				try {
					Field modifiersField = Field.class.getDeclaredField("modifiers");
				    modifiersField.setAccessible(true);
				    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
				} catch(NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			return field;
		} else {
			if(NSFE != null) throw NSFE;
			else if(IAE != null) throw IAE;
			else throw new NullPointerException("field is null");
		}
	}
	
	public static Method findMethod(Class<?> arg0, List<String> arg1, Class<?>...arg2) throws NoSuchMethodException {
		Method method = null;
		IllegalArgumentException IAE = null;
		NoSuchMethodException NSME = null;
		SecurityException SE = null;
		for(String arg : arg1) {
			try {
				method = arg0.getDeclaredMethod(arg, arg2);
				break;
			} catch(IllegalArgumentException e) {
				IAE = e;
			} catch (NoSuchMethodException e) {
				NSME = e;
			} catch (SecurityException e) {
				SE = e;
			}
		}
		if(method != null) {
			if(!method.isAccessible()) method.setAccessible(true);
			return method;
		} else {
			if(IAE != null) throw IAE;
			else if(NSME != null) throw NSME;
			else if(SE != null) throw SE;
			else throw new NullPointerException("field is null");
		}
	}
	
}
