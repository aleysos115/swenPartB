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
    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private boolean strong;

    
    private int deliveryCounter;
    

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param behaviour governs selection of mail items for delivery and behaviour on priority arrivals
     * @param strong is whether the robot can carry heavy items
     */
    public Robot(IRobotBehaviour behaviour, boolean strong){
    	id = "R" + hashCode();
        // current_state = RobotState.WAITING;
    	current_state = RobotState.RETURNING;
        current_floor = Building.MAILROOM_LOCATION;
        tube = new StorageTube();
        this.behaviour = behaviour;
        this.strong = strong;
        this.deliveryCounter = 0;
    }


    /**
     * Sets the route for the robot
     */
    private void setRoute() throws ItemTooHeavyException{
        /** Pop the item from the StorageUnit */
        tube.getDeliveryItem(true);
        if (!strong && tube.getDeliveryItem(false).weight > 2000) throw new ItemTooHeavyException();
        /** Set the destination floor */
        destination_floor = tube.getDeliveryItem(false).getDestFloor();
    }

    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    private void moveTowards(int destination){
        if(current_floor < destination){
            current_floor++;
        }
        else{
            current_floor--;
        }
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %11s changed from %s to %s%n", Clock.Time(), id, current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %11s-> [%s]%n", Clock.Time(), id, tube.getDeliveryItem(false).toString());
    	}
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

    public boolean getStrong() {
    	return strong;
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



    public MailItem getDeliveryItem() {
        return tube.getDeliveryItem(false);
    }


}
