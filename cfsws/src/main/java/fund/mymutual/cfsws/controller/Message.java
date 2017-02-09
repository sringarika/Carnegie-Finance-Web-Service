package fund.mymutual.cfsws.controller;

// { “message”:“Welcome first name”}
public class Message {
    private String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
