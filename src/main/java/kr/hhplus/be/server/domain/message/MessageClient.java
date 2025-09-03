package kr.hhplus.be.server.domain.message;

public interface MessageClient {

    void sendOrder(MessageCommand.Order message);
}
