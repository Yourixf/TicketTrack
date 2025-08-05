package nl.yourivb.TicketTrack.exceptions;

public class IllegalStateException extends RuntimeException {
    public IllegalStateException(String message) {
        super(message);
    }

    public IllegalStateException() {super();}
}
