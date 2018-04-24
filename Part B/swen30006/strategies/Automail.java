package strategies;

import automail.*;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;

public class Automail {
	      
    public Robot robot1, robot2;
    public IMailPool mailPool;


    private IMailDelivery delivery;
    
    public Automail(IMailDelivery delivery) {
    	// Swap between simple provided strategies and your strategies here
    	    	
    	/** Initialize the MailPool */
    	
    	//// Swap the next line for the one below
    	mailPool = new WeakStrongMailPool();
    	this.delivery = delivery;
    	
        /** Initialize the RobotAction */
    	boolean weak = false;  // Can't handle more than 2000 grams
    	boolean strong = true; // Can handle any weight that arrives at the building
    	
    	//// Swap the next two lines for the two below those
    	IRobotBehaviour robotBehaviourW = new MyRobotBehaviour(weak);
    	IRobotBehaviour robotBehaviourS = new MyRobotBehaviour(strong);
    	    	
    	/** Initialize robot */
    	robot1 = new Robot(robotBehaviourW, delivery, mailPool, weak); /* shared behaviour because identical and stateless */
    	robot2 = new Robot(robotBehaviourS, delivery, mailPool, strong);
    }




    public void stepAllRobots() throws ItemTooHeavyException, ExcessiveDeliveryException {
    	this.step(robot1);
    	this.step(robot2);
	}




	/**
	 * This is called on every time step
	 * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
	 */
	private void step(Robot robot) throws ExcessiveDeliveryException, ItemTooHeavyException {
		switch(robot.current_state) {
			/** This state is triggered when the robot is returning to the mailroom after a delivery */
			case RETURNING:
				/** If its current position is at the mailroom, then the robot should change state */
				if(robot.getCurrent_floor() == Building.MAILROOM_LOCATION){
					while(!robot.getTube().isEmpty()) {
						MailItem mailItem = robot.getTube().pop();
						mailPool.addToPool(mailItem);
						System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), mailItem.toString());
					}
					changeRobotState(robot, Robot.RobotState.WAITING);
				} else {
					/** If the robot is not at the mailroom floor yet, then move towards it! */
					robot.moveTowards(Building.MAILROOM_LOCATION);
					break;
				}
			case WAITING:
				/** Tell the sorter the robot is ready */
				mailPool.fillStorageTube(robot.getTube(), robot.isStrong());
				// System.out.println("Tube total size: "+tube.getTotalOfSizes());
				/** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
				if(!robot.getTube().isEmpty()){
					robot.setDeliveryCounter(0); // reset delivery counter
					robot.getBehaviour().startDelivery();
					robot.setRoute();
					changeRobotState(robot, Robot.RobotState.DELIVERING);
				}
				break;
			case DELIVERING:
				/** Check whether or not the call to return is triggered manually **/
				boolean wantToReturn = robot.getBehaviour().returnToMailRoom(robot.getTube());
				if(robot.getCurrent_floor() == robot.getDestination_floor()){ // If already here drop off either way
					/** Delivery complete, report this to the simulator! */
					delivery.deliver(robot.getDeliveryItem());
					robot.setDeliveryCounter(robot.getDeliveryCounter() + 1);
					if(robot.getDeliveryCounter() > 4){
						throw new ExcessiveDeliveryException();
					}
					/** Check if want to return or if there are more items in the tube*/
					if(wantToReturn || robot.getTube().isEmpty()){
						// if(tube.isEmpty()){
						changeRobotState(robot, Robot.RobotState.RETURNING);
					}
					else{
						/** If there are more items, set the robot's route to the location to deliver the item */
						robot.setRoute();
						changeRobotState(robot, Robot.RobotState.DELIVERING);
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
					robot.moveTowards(robot.getDestination_floor());
	                /*
	    			}
	    			*/
				}
				break;
		}
	}



	private void changeRobotState(Robot robot, Robot.RobotState nextState){
		if (robot.current_state != nextState) {
			System.out.printf("T: %3d > %11s changed from %s to %s%n", Clock.Time(), robot.getId(), robot.current_state, nextState);
		}
		robot.current_state = nextState;
		if(nextState == Robot.RobotState.DELIVERING){
			System.out.printf("T: %3d > %11s-> [%s]%n", Clock.Time(), robot.getId(), robot.getDeliveryItem().toString());
		}
	}


	public void priorityArrival(PriorityMailItem priority) {
		robot1.priorityArrival(priority);
		robot2.priorityArrival(priority);
	}
    
}
