package strategies;

import java.util.*;

import automail.*;
import exceptions.TubeFullException;

public class WeakStrongMailPool implements IMailPool{


	private Configuration config;


	private LinkedList<MailItem> upper;  // weak robot will take this set
	private LinkedList<MailItem> lower;  // strong robot will take this set
	private int divider;
	private static final int MAX_WEIGHT = 2000;

	public WeakStrongMailPool(Configuration config){
		// Start empty
		upper = new LinkedList<MailItem>();
		lower = new LinkedList<MailItem>();
		divider = Building.FLOORS / 2;  // Top normal floor for strong robot

		this.config = config;
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
			lower.addLast(mailItem); // Just add it on the end of the lower (strong robot) list
		}
		else{
			upper.addLast(mailItem); // Just add it on the end of the upper (weak robot) list
		}
	}
	
	@Override
	public void fillStorageTube(Robot robot) {

		Queue<MailItem> q = null;

		switch (robot.getType()) {
			case STRONG:
				q = lower;
				break;
			case BIG:
				break;
			case WEAK:
				q = upper;
				break;
		}



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
