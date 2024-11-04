package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Message;
import coms309.backEnd.demo.repository.MessageRepository;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ServerEndpoint(value = "/chat/{netId}")
public class ChatSocketController {

    private static MessageRepository messageRepository;

    @Autowired
    public void setMessageRepository(MessageRepository messageRepo){
        messageRepository = messageRepo;
    }

    private static Map<Session, String> sessionNetIdMap = new HashMap<>();
    private static Map<String, Session> netIdSessionMap = new HashMap<>();

    private void sendMessageToPArticularUser(String netId, String message){
        try{
            netIdSessionMap.get(netId).getBasicRemote().sendText(message);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

//    private String getChatHistory(){
//        List<Message> messages = messageRepository.findAll();
//
//    }


    @OnOpen
    public void onOpen(Session session, @PathParam("netId") String netId ) throws IOException{

        sessionNetIdMap.put(session, netId);
        netIdSessionMap.put(netId,session);


    }
}
