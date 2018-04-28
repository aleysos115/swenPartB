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

    	this.delivery = delivery;



		createRobotsAndMailPool(Robot.RobotType.STRONG, Robot.RobotType.WEAK);
    }




    public void stepAllRobots() throws ItemTooHeavyException, ExcessiveDeliveryException {
    	robot1.step(mailPool, delivery);
		robot2.step(mailPool, delivery);
	}





	public void priorityArrival(PriorityMailItem priority) {
		robot1.priorityArrival(priority);
		robot2.priorityArrival(priority);
	}



	public void createRobotsAndMailPool(Robot.RobotType robot1Type, Robot.RobotType robot2Type) {
		/** Initialize the RobotAction */
		IRobotBehaviour robotBehaviour1 = new MyRobotBehaviour(robot1Type);
		IRobotBehaviour robotBehaviour2 = new MyRobotBehaviour(robot2Type);



		/** Initialize robot */
		robot1 = new Robot(robotBehaviour1, robot1Type); /* shared behaviour because identical and stateless */
		robot2 = new Robot(robotBehaviour2, robot2Type);


		/** Initialize the MailPool */
		// a configuration of a strong and a weak robot
		Configuration config = new Configuration(robot1, robot2);
		mailPool = new MyMailPool(config);
	}
    
}
