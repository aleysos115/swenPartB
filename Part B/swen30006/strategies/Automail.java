package strategies;

import automail.*;
import exceptions.ExcessiveDeliveryException;
import exceptions.InvalidConfigurationException;
import exceptions.ItemTooHeavyException;

public class Automail {
	      
    public Robot robot1, robot2;
    public IMailPool mailPool;
    private IMailDelivery delivery;


    public Automail(IMailDelivery delivery, String robot1TypeString, String robot2TypeString){
    	// Swap between simple provided strategies and your strategies here
    	this.delivery = delivery;

    	Robot.RobotType robot1Type = parseTypeFromString(robot1TypeString);
		Robot.RobotType robot2Type = parseTypeFromString(robot2TypeString);

		createRobotsAndMailPool(robot1Type, robot2Type);
    }




    public void step() throws ItemTooHeavyException, ExcessiveDeliveryException {
    	robot1.step(mailPool, delivery);
		robot2.step(mailPool, delivery);
	}





	public void priorityArrival(PriorityMailItem priority) {
		robot1.priorityArrival(priority);
		robot2.priorityArrival(priority);
	}


	/**
	 * Create robots and mailpool with robot types
	 */
	public void createRobotsAndMailPool(Robot.RobotType robot1Type, Robot.RobotType robot2Type){
		/** Initialize the RobotAction */
		IRobotBehaviour robotBehaviour1 = new MyRobotBehaviour(robot1Type);
		IRobotBehaviour robotBehaviour2 = new MyRobotBehaviour(robot2Type);



		/** Initialize robot */
		robot1 = new Robot(robotBehaviour1, robot1Type); /* shared behaviour because identical and stateless */
		robot2 = new Robot(robotBehaviour2, robot2Type);


		/** Initialize the MailPool */
		Configuration config = new Configuration(robot1, robot2);
		try {
			mailPool = new MyMailPool(config);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}


	// parse robot type enum from string
	private Robot.RobotType parseTypeFromString(String typeString) {
		Robot.RobotType type = null;
		switch (typeString) {
			case "weak":
				type = Robot.RobotType.WEAK;
				break;
			case "strong":
				type = Robot.RobotType.STRONG;
				break;
			case "big":
				type = Robot.RobotType.BIG;
		}
		return type;
	}

    
}
