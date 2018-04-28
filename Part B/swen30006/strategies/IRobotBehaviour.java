package strategies;

import automail.*;
import automail.Robot.RobotState;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;

public interface IRobotBehaviour {

    int MAX_WEIGHT_FOR_WEAK = 2000;

	/**
	 * startDelivery() provides the robot the opportunity to initialise state
	 * in support of the other methods below. 
	 */
	
	void startDelivery();
	
	/** 
	 * @param tube refers to the pack the robot uses to deliver mail.
	 * @return When this is true, the robot is returned to the mail room.
	 * The robot will always return to the mail room when the tube is empty.
	 * This method allows the robot to return with items still in the tube,
	 * if circumstances make this desirable.
	 */
    boolean returnToMailRoom(StorageTube tube);
    
    /**
     * @param priority is that of the priority mail item which just arrived.
     * @param weight is that of the same item.
     * The automail system broadcasts this information to all robots
     * when a new priority mail items arrives at the building.
     */
    void priorityArrival(int priority, int weight);
    
    default void setRoute(Robot robot) throws ItemTooHeavyException{
        /** Pop the item from the StorageUnit */
        robot.getTube().getDeliveryItem(true);
        if ((robot.getType() == Robot.RobotType.WEAK) && robot.getTube().getDeliveryItem(false).getWeight() > MAX_WEIGHT_FOR_WEAK) throw new ItemTooHeavyException();
        /** Set the destination floor */
        robot.setDestinationFloor(robot.getTube().getDeliveryItem(false).getDestFloor());
    }
    
    default void moveTowards(Robot robot, int destination){
        if(robot.getCurrentFloor() < destination){
            robot.setCurrentFloor(robot.getCurrentFloor() + 1);
        }
        else{
        	robot.setCurrentFloor(robot.getCurrentFloor() - 1);
        }
    }
    
    default void step(IMailPool mailPool, IMailDelivery delivery, Robot robot) throws ExcessiveDeliveryException, ItemTooHeavyException {
    	StorageTube tube = robot.getTube();
    	switch(robot.getState()) {
            /** This state is triggered when the robot is returning to the mailroom after a delivery */
            case RETURNING:
                /** If its current position is at the mailroom, then the robot should change state */
                if(robot.getCurrentFloor() == Building.MAILROOM_LOCATION){
                    while(!tube.isEmpty()) {
                        MailItem mailItem = tube.pop();
                        mailPool.addToPool(mailItem);
                        System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), mailItem.toString());
                    }
                    robot.setState(RobotState.WAITING);
                } else {
                    /** If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(robot, Building.MAILROOM_LOCATION);
                    break;
                }
            case WAITING:
                /** Tell the sorter the robot is ready */
                mailPool.fillStorageTube(robot);
                // System.out.println("Tube total size: "+tube.getTotalOfSizes());
                /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
                if(!tube.isEmpty()){
                    robot.setDeliveryCounter(0); // reset delivery counter
                    startDelivery();
                    setRoute(robot);
                    robot.setState(RobotState.DELIVERING);
                }
                break;
            case DELIVERING:
                /** Check whether or not the call to return is triggered manually **/
                boolean wantToReturn = returnToMailRoom(tube);
                if(robot.getCurrentFloor() == robot.getDestinationFloor()){ // If already here drop off either way
                    /** Delivery complete, report this to the simulator! */
                    delivery.deliver(robot.getDeliveryItem());
                    robot.setDeliveryCounter(robot.getDeliveryCounter() + 1);
                    if(robot.getDeliveryCounter() > robot.getTube().getCapacity()){
                        throw new ExcessiveDeliveryException();
                    }
                    /** Check if want to return or if there are more items in the tube*/
                    if(wantToReturn || tube.isEmpty()){
                        // if(tube.isEmpty()){
                        robot.setState(RobotState.RETURNING);
                    }
                    else{
                        /** If there are more items, set the robot's route to the location to deliver the item */
                        setRoute(robot);
                        robot.setState(RobotState.DELIVERING);
                    }
                } else
                {/*
	    			if(wantToReturn){
	    				// Put the item we are trying to deliver back
	    				try {
							tube.addItem(deliveryItem);
						} catch (TubeFullException e) {
							e.printStackTrace();
						}
	    				changeState(RobotState.RETURNING);
	    			}
	    			else{*/
                    /** The robot is not at the destination yet, move towards it! */
                    moveTowards(robot, robot.getDestinationFloor());
	                /*
	    			}
	    			*/
                }
                break;
        }
    }
    
}
