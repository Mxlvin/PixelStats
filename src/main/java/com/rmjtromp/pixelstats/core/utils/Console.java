package com.rmjtromp.pixelstats.core.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Console {
	
	private Console() {
	    throw new IllegalStateException("Utility class");
	}

    private static final Object socket;

    static {
    	if(socketIOIsPresent()) {
            socket = io.socket.client.IO.socket(URI.create("http://localhost:3000"));
            ((io.socket.client.Socket) socket).connect();
		} else  socket = null;
    }
    
    private static boolean socketIOIsPresent() {
    	try {
    		Class.forName("io.socket.client.IO");
    		Class.forName("io.socket.client.Socket");
    		
            return true;
    	} catch(ClassNotFoundException e) {
    		return false;
    	}
    }

    public static void log(Object...args) {
        emitMessage(1, args);
    }

    public static void info(Object...args) {
        emitMessage(2, args);
    }

    public static void warning(Object...args) {
        emitMessage(4, args);
    }

    public static void error(Object...args) {
        emitMessage(8, args);
    }

    public static void severe(Object...args) {
        emitMessage(16, args);
    }

    public static void debug(Object...args) {
        emitMessage(32, args);
    }

    private static void emitMessage(int type, Object... args) {
    	if(socket != null) {
            try { ((io.socket.client.Socket) socket).emit("message", build(args), type);
            } catch(Exception ignore) {}
    	}
    }

    private static String build(Object ...args) {
        StringBuilder builder = new StringBuilder();
        for(Object obj : args) builder.append(stringify(obj));
        return builder.toString();
    }

    public static String stringify(Object arg) {
        if(arg == null) return "null";
        else if(arg instanceof Boolean) return Boolean.toString((Boolean) arg);
        else if(arg instanceof String) return (String) arg;
        else if(arg instanceof Integer) return Integer.toString((Integer) arg);
        else if(arg instanceof Double) return Double.toString((Double) arg);
        else if(arg instanceof Long) return Long.toString((Long) arg);
        else if(arg instanceof Byte) return Byte.toString((Byte) arg);
        else if(arg instanceof Short) return Short.toString((Short) arg);
        else if(arg instanceof Exception) return arg.getClass().getSimpleName() + ": "+((Exception) arg).getMessage();
        else if(arg instanceof Iterable) {
            List<String> array = new ArrayList<>();
            ((Iterable<?>)arg).forEach(object -> array.add(stringify(object)));
            return "["+String.join(", ", array)+"]";
        } else if(arg instanceof Map) {
            List<String> array = new ArrayList<>();
            ((Map<?, ?>)arg).forEach((key, value) -> array.add("{"+stringify(key)+": "+stringify(value)+"}"));
            return "["+String.join(", ", array)+"]";
        } else return arg.toString();
    }

}
