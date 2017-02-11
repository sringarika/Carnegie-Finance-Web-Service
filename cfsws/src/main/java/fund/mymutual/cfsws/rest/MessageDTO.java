package fund.mymutual.cfsws.rest;

// { “message”:“Welcome first name”}
public class MessageDTO {
    private String message;

    public MessageDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
