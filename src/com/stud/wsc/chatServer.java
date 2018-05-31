package com.stud.wsc;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class chatServer {
	static Set<Session> chatUsers = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void handleOpen(Session user) {
		chatUsers.add(user);
	}
	
	@OnMessage
	public void handleMessage(String msg, Session user) throws IOException {
		String username = (String) user.getUserProperties().get("username");
		if(username == null) {
			user.getUserProperties().put("username", msg);
			user.getBasicRemote().sendText(buildJsonData("System"," you are connected as: "+msg));
		}
		else {
			Iterator<Session> iterator = chatUsers.iterator();
			while(iterator.hasNext()) iterator.next().getBasicRemote().sendText(buildJsonData(username,msg));
		}
	}
	
	@OnClose
	public void handleClose(Session user) {
		chatUsers.remove(user);
	}
	
	private String buildJsonData(String username, String msg) {
		JsonObject jsonObject = Json.createObjectBuilder().add("msg", username+": "+msg).build();
		StringWriter sw = new StringWriter();
		try (JsonWriter jw = Json.createWriter(sw)) {
			jw.write(jsonObject);
		}
		
		return sw.toString();
	}
}
