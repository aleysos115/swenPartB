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
		// a configuration of a strong and a weak robot
		Configuration config = new Configuration(Robot.RobotType.WEAK, Robot.RobotType.STRONG);
    	mailPool = new WeakStrongMailPool(config);
    	this.delivery = delivery;

        /** Initialize the RobotAction */
    	boolean weak = false;  // Can't handle more than 2000 grams
    	boolean strong = true; // Can handle any weight that arrives at the building
    	
    	//// Swap the next two lines for the two below those
    	IRobotBehaviour robotBehaviourW = new MyRobotBehaviour(weak);
    	IRobotBehaviour robotBehaviourS = new MyRobotBehaviour(strong);



    	/** Initialize robot */
    	robot1 = new Robot(robotBehaviourW, Robot.RobotType.WEAK); /* shared behaviour because identical and stateless */
    	robot2 = new Robot(robotBehaviourS, Robot.RobotType.STRONG);
    }




    public void stepAllRobots() throws ItemTooHeavyException, ExcessiveDeliveryException {
    	robot1.step(mailPool, delivery);
		robot2.step(mailPool, delivery);
	}





	public void priorityArrival(PriorityMailItem priority) {
		robot1.priorityArrival(priority);
		robot2.priorityArrival(priority);
	}
    
}
