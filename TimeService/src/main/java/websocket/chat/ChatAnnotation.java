package websocket.chat;

import com.example.service.Dictionary;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/chat")
public class ChatAnnotation {

    private static final String GUEST_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatAnnotation> connections = new CopyOnWriteArraySet<ChatAnnotation>();
    private final String nickname;
    private Session session;

    public ChatAnnotation() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        broadcast(String.format("* %s %s", nickname, "has joined."));
    }

    @OnClose
    public void end() {
        connections.remove(this);
        broadcast(String.format("* %s %s", nickname, "has disconnected."));
    }

    @OnMessage
    public void incoming(String message) {
        String text = message == null ? "" : message.trim();
        if ("time".equalsIgnoreCase(text)) {
            broadcast("Current time: " + currentTime());
            return;
        }
        String translation = Dictionary.translate(text);
        if (text.length() > 0 && !"не найдено".equals(translation) && !translation.equals("")) {
            broadcast("Перевод: " + translation);
            return;
        }
        broadcast(String.format("%s: %s", nickname, text));
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("Chat Error: " + t);
    }

    private static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private static void broadcast(String msg) {
        for (ChatAnnotation client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException ignored) {
                }
                broadcast(String.format("* %s %s", client.nickname, "has been disconnected."));
            }
        }
    }
}
