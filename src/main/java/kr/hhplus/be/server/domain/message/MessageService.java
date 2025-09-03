package kr.hhplus.be.server.domain.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageClient messageClient;

    public void sendOrder(MessageCommand.Order message) {
        messageClient.sendOrder(message);
    }
}
