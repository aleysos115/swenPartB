package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;
import strategies.IRobotBehaviour;

/**
 * The robot delivers mail!
 */
public class Robot {

	StorageTube tube;
    IRobotBehaviour behaviour;
    protected final String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    public enum RobotType { WEAK, STRONG, BIG }
    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private RobotType type;

    
    private int deliveryCounter;
    

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param behaviour governs selection of mail items for delivery and behaviour on priority arrivals
     * @param type the type of the robot, weak strong or big
     */
    public Robot(IRobotBehaviour behaviour, RobotType type){
    	id = "R" + hashCode();
        // current_state = RobotState.WAITING;
    	current_state = RobotState.RETURNING;
        current_floor = Building.MAILROOM_LOCATION;
        tube = new StorageTube(type);
        this.behaviour = behaviour;
        this.type = type;
        this.deliveryCounter = 0;
    }


    /**
     * This is called on every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public void step(IMailPool mailPool, IMailDelivery delivery) throws ExcessiveDeliveryException, ItemTooHeavyException {
        behaviour.step(mailPool, delivery, this);
    }
    
    public StorageTube getTube() {
    	return tube;
    }

    public int getCurrentFloor() {
    	return current_floor;
    }
    public void setCurrentFloor(int floor) {
    	current_floor = floor;
    }
    
    public int getDestinationFloor() {
    	return destination_floor;
    }
    public void setDestinationFloor(int floor) {
    	destination_floor = floor;
    }

    public RobotState getState() {
    	return current_state;
    }
    public void setState(RobotState state) {
    	current_state = state;
    }

    public RobotType getType() {
        return type;
    }

    public int getDeliveryCounter() {
    	return deliveryCounter;
    }
    public void setDeliveryCounter(int counter) {
    	deliveryCounter = counter;
    }
    
    public void priorityArrival(PriorityMailItem priority) {
        behaviour.priorityArrival(priority.getPriorityLevel(), priority.weight);
    }

    public String getId() {
        return id;
    }

    public MailItem getDeliveryItem() {
        return tube.getDeliveryItem(false);
    }


}

