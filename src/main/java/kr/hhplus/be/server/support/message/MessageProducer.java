package kr.hhplus.be.server.support.message;

public interface MessageProducer {

    void send(Message message);

    void sendSync(Message message) throws Exception;
}
