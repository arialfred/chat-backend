package fi.silvan.chat.controller;

import fi.silvan.chat.model.Message;
import fi.silvan.chat.model.MessageType;
import fi.silvan.chat.model.SignInMessage;
import fi.silvan.chat.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

	private UserService userService;

	public ChatController(@Autowired UserService userService) {
		this.userService = userService;
	}

	@MessageMapping("/chat.signIn")
	@SendToUser("/sign-in-reply")
	public Message signIn(@Payload SignInMessage message, SimpMessageHeaderAccessor headerAccessor) {
		String username = message.getUsername();
		if (userService.signIn(username)) {
			LOGGER.trace(String.format("User %s signed in.", username));
			// Add username in web socket session
			headerAccessor.getSessionAttributes().put("username", username);
			return new Message(MessageType.SIGN_IN_REPLY, "OK", null);
		} else {
			LOGGER.trace(String.format("User %s already exists.", username));
			return new Message(MessageType.SIGN_IN_REPLY, "DUPLICATE_USERNAME", null);
		}
	}

	@MessageMapping("/chat.broadcastUserJoin")
	@SendTo("/topic/public")
	public Message broadcastUserJoin(@Payload Message message) {
		return message;
	}

	@MessageMapping("/chat.broadcastMessage")
	@SendTo("/topic/public")
	public Message broadcastMessage(@Payload Message message) {
		LOGGER.trace(String.format("User %s sent a message: %s.", message.getSender(), message.getContent()));
		return message;
	}
}