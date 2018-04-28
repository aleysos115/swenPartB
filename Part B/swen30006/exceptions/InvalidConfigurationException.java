package exceptions;
/**
 * An exception thrown when the robot tries to deliver more items than its tube capacity without refilling.
 */

public class InvalidConfigurationException extends Throwable {
    public InvalidConfigurationException(){
        super("Invalid configuration! Weak and Weak is invalid.");
    }
}

