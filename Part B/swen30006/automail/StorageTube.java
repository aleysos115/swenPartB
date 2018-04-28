package automail;

// import exceptions.RobotNotInMailRoomException;
import exceptions.TubeFullException;

import java.util.Stack;

/**
 * The storage tube carried by the robot.
 */
public class StorageTube {

    private int MAXIMUM_CAPACITY;
    private Stack<MailItem> tube;
    private MailItem deliveryItem;

    /**
     * Constructor for the storage tube
     */
    public StorageTube(Robot.RobotType type){
        this.tube = new Stack<MailItem>();
        switch (type) {
            case WEAK:
            case STRONG:
                MAXIMUM_CAPACITY = 4;
                break;
            case BIG:
                MAXIMUM_CAPACITY = 6;
                break;
        }
    }

    /**
     * @return if the storage tube is full
     */
    public boolean isFull(){
        return tube.size() == MAXIMUM_CAPACITY;
    }

    /**
     * @return if the storage tube is empty
     */
    public boolean isEmpty(){
        return tube.isEmpty();
    }
    
    /**
     * @return the first item in the storage tube (without removing it)
     */
    public MailItem peek() {
    	return tube.peek();
    }

    /**
     * Add an item to the tube
     * @param item The item being added
     * @throws TubeFullException thrown if an item is added which exceeds the capacity
     */
    public void addItem(MailItem item) throws TubeFullException {
        if(tube.size() < MAXIMUM_CAPACITY){
        	tube.add(item);
        } else {
            throw new TubeFullException();
        }
    }

    /** @return the size of the tube **/
    public int getSize(){
    	return tube.size();
    }
    
    /** 
     * @return the first item in the storage tube (after removing it)
     */
    public MailItem pop(){
        return tube.pop();
    }


    public MailItem getDeliveryItem(boolean newItem) {
        if (newItem) {
            deliveryItem = pop();
        }
        return deliveryItem;
    }


    public int getCapacity() {
        return MAXIMUM_CAPACITY;
    }
}
