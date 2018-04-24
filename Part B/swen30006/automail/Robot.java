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
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param strong is whether the robot can carry heavy items
     */
    public Robot(IRobotBehaviour behaviour, IMailDelivery delivery, IMailPool mailPool, boolean strong){
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
    public void setRoute() throws ItemTooHeavyException{
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
    public void moveTowards(int destination){
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




    public void priorityArrival(PriorityMailItem priority) {
        behaviour.priorityArrival(priority.getPriorityLevel(), priority.weight);
    }



    // getters
    public int getCurrent_floor() {
        return current_floor;
    }


    public StorageTube getTube() {
        return tube;
    }

    public String getId() {
        return id;
    }

    public MailItem getDeliveryItem() {
        return tube.getDeliveryItem(false);
    }

    public boolean isStrong() {
        return strong;
    }


    public int getDeliveryCounter() {
        return deliveryCounter;
    }

    public void setDeliveryCounter(int deliveryCounter) {
        this.deliveryCounter = deliveryCounter;
    }

    public IRobotBehaviour getBehaviour() {
        return behaviour;
    }

    public int getDestination_floor() {
        return destination_floor;
    }

}
