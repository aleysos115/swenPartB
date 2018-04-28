package strategies;

import java.util.*;

import automail.*;
import exceptions.InvalidConfigurationException;
import exceptions.TubeFullException;

public class MyMailPool implements IMailPool{


	private Configuration config;


	private LinkedList<MailItem> upper;  // weak robot will take this set
	private LinkedList<MailItem> lower;  // strong robot will take this set
	private HashMap<String, LinkedList<MailItem>> map = new HashMap<>();
	private int divider;
	private int MAX_WEIGHT;


	public MyMailPool(Configuration config) throws InvalidConfigurationException {
		// Start empty
		upper = new LinkedList<MailItem>();
		lower = new LinkedList<MailItem>();
		divider = Building.FLOORS / 2;  // Top normal floor for strong robot

		this.config = config;

		// if contains weak robot, then set MAX_WEIGHT to 2000
		if (this.config.containsWeakRobot()) {
			this.MAX_WEIGHT = 2000;
		} else {
			this.MAX_WEIGHT = Integer.MAX_VALUE;
		}


		// decide on which half each robot delivers
		if (config.getRobot1Type() == Robot.RobotType.WEAK) {
			/* If one robot is weak, the weak robot will deliver to the upper half of the building and the lower robot will
			deliver all parcels too heavy for the weak robot. */
			map.put(config.getRobot1Id(), upper);

			if (config.getRobot2Type() == Robot.RobotType.WEAK) {
				/* Two weak robots is considered an invalid configuration. */
				throw new InvalidConfigurationException();
			} else {
				// strong or big
				map.put(config.getRobot2Id(), lower);
			}
		} else if (config.getRobot1Type() == Robot.RobotType.STRONG) {
			if (config.getRobot2Type() == Robot.RobotType.WEAK) {
				map.put(config.getRobot2Id(), upper);
				map.put(config.getRobot1Id(), lower);
			} else if (config.getRobot2Type() == Robot.RobotType.STRONG) {
				map.put(config.getRobot1Id(), upper);
				map.put(config.getRobot2Id(), lower);
			} else {
				// robot 2 is big
				/* If one robot is big and the other is strong, the big robot will deliver to the upper half of the building. */
				map.put(config.getRobot2Id(), upper);
				map.put(config.getRobot1Id(), lower);
			}
		} else { // big robot
			if (config.getRobot2Type() == Robot.RobotType.WEAK) {
				map.put(config.getRobot2Id(), upper);
				map.put(config.getRobot1Id(), lower);
			} else if (config.getRobot2Type() == Robot.RobotType.STRONG) {
				map.put(config.getRobot1Id(), upper);
				map.put(config.getRobot2Id(), lower);
			} else { // 2 big
				map.put(config.getRobot1Id(), upper);
				map.put(config.getRobot2Id(), lower);
			}
		}

	}

	private int priority(MailItem m) {
		return (m instanceof PriorityMailItem) ? ((PriorityMailItem) m).getPriorityLevel() : 0;
	}
	
	public void addToPool(MailItem mailItem) {
		// This doesn't attempt to put the re-add items back in time order but there will be relatively few of them,
		// from the strong robot only, and only when it is recalled with undelivered items.
		// Check whether mailItem is for strong robot
		if (mailItem instanceof PriorityMailItem || mailItem.getWeight() > MAX_WEIGHT || mailItem.getDestFloor() <= divider) {
			if (mailItem instanceof PriorityMailItem) {  // Add in priority order
				int priority = ((PriorityMailItem) mailItem).getPriorityLevel();
				ListIterator<MailItem> i = lower.listIterator();
				while (i.hasNext()) {
					if (priority(i.next()) < priority) {
						i.previous();
						i.add(mailItem);
						return; // Added it - done
					}
				}
			}
			lower.addLast(mailItem);
			// the lower must deliver all priority items, will get items if they are too heavy.
		} else {
			upper.addLast(mailItem);
			// items for upper
		}
	}



	@Override
	public void fillStorageTube(Robot robot) {

		// check the configuration first

		Queue<MailItem> q = map.get(robot.getId());

		try{
			while(!robot.getTube().isFull() && !q.isEmpty()) {
				robot.getTube().addItem(q.remove());  // Could group/order by floor taking priority into account - but already better than simple
			}
		}
		catch(TubeFullException e){
			e.printStackTrace();
		}
	}

}
