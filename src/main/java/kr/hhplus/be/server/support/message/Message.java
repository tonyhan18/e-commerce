package kr.hhplus.be.server.support.message;

public interface Message {

    String getTopic();

    String getKey();

    String getPayload();
}
