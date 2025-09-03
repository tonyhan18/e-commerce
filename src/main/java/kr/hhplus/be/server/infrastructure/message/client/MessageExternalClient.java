package kr.hhplus.be.server.infrastructure.message.client;

import kr.hhplus.be.server.domain.message.MessageClient;
import kr.hhplus.be.server.domain.message.MessageCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageExternalClient implements MessageClient {

    private final MessageDataPlatformClient messageDataPlatformClient;

    @Override
    public void sendOrder(MessageCommand.Order message) {
        messageDataPlatformClient.sendOrder(message);
    }
}
