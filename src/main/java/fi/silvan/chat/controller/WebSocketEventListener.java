package fi.silvan.chat.controller;

import fi.silvan.chat.model.Message;
import fi.silvan.chat.model.MessageType;
import fi.silvan.chat.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

	private UserService userService;
	private SimpMessageSendingOperations messagingTemplate;

	public WebSocketEventListener(@Autowired UserService userService,
								  @Autowired SimpMessageSendingOperations messagingTemplate) {
		this.userService = userService;
		this.messagingTemplate = messagingTemplate;
	}

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		LOGGER.trace("New web socket connection established.");
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		LOGGER.trace("Web socket connection closed.");
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String)headerAccessor.getSessionAttributes().get("username");
		if (username != null) {
			LOGGER.trace(String.format("User %s signed out.", username));
			userService.signOut(username);
			messagingTemplate.convertAndSend("/topic/public", new Message(MessageType.LEAVE, username));
		}
	}
}