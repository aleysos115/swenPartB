package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.InvalidConfigurationException;
import exceptions.ItemTooHeavyException;
import exceptions.MailAlreadyDeliveredException;
import strategies.Automail;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * This class simulates the behaviour of AutoMail
 */
public class Simulation {

    /** Constant for the mail generator */
    private static ArrayList<MailItem> MAIL_DELIVERED;
    private static double total_score = 0;
    private static float DELIVERY_PENALTY;


    public static void main(String[] args) throws InvalidConfigurationException { //throws IOException {
    	// Should probably be using properties here
    	Properties automailProperties = new Properties();
		// Defaults
		automailProperties.setProperty("Name_of_Property", "20");  // Property value may need to be converted from a string to the appropriate type

		FileReader inStream = null;
		
		try {
			inStream = new FileReader("automail.properties");
			automailProperties.load(inStream);
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
			 if (inStream != null) {
                 try {
                     inStream.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
		}


		/* properties */
        int SEED = Integer.parseInt(automailProperties.getProperty("Seed"));
        Building.FLOORS = Integer.parseInt(automailProperties.getProperty("Number_of_Floors"));
        DELIVERY_PENALTY = Float.parseFloat(automailProperties.getProperty("Delivery_Penalty"));
        Clock.LAST_DELIVERY_TIME = Integer.parseInt(automailProperties.getProperty("Last_Delivery_Time"));
        int MAIL_TO_CREATE = Integer.parseInt(automailProperties.getProperty("Mail_to_Create"));
        String ROBOT_TYPE_1 = automailProperties.getProperty("Robot_Type_1");
        String ROBOT_TYPE_2 = automailProperties.getProperty("Robot_Type_2");



        MAIL_DELIVERED = new ArrayList<MailItem>();
                
        /** Used to see whether a seed is initialized or not */
        HashMap<Boolean, Integer> seedMap = new HashMap<>();

        seedMap.put(true, SEED);

        Automail automail = new Automail(new ReportDelivery(), ROBOT_TYPE_1, ROBOT_TYPE_2);
        MailGenerator generator = new MailGenerator(MAIL_TO_CREATE, automail.mailPool, seedMap);
        
        /** Initiate all the mail */
        generator.generateAllMail();
        PriorityMailItem priority;
        while(MAIL_DELIVERED.size() != generator.MAIL_TO_CREATE) {
        	//System.out.println("-- Step: "+Clock.Time());
            priority = generator.step();
            if (priority != null) {
            	automail.priorityArrival(priority);
            }
            try {
                automail.step();
			} catch (ExcessiveDeliveryException|ItemTooHeavyException e) {
				e.printStackTrace();
				System.out.println("Simulation unable to complete.");
				System.exit(0);
			}
            Clock.Tick();
        }
        printResults();
    }
    
    static class ReportDelivery implements IMailDelivery {
    	
    	/** Confirm the delivery and calculate the total score */
    	public void deliver(MailItem deliveryItem){
    		if(!MAIL_DELIVERED.contains(deliveryItem)){
                System.out.printf("T: %3d > Delivered     [%s]%n", Clock.Time(), deliveryItem.toString());
    			MAIL_DELIVERED.add(deliveryItem);
    			// Calculate delivery score
    			total_score += calculateDeliveryScore(deliveryItem);
    		}
    		else{
    			try {
    				throw new MailAlreadyDeliveredException();
    			} catch (MailAlreadyDeliveredException e) {
    				e.printStackTrace();
    			}
    		}
    	}

    }




    private static double calculateDeliveryScore(MailItem deliveryItem) {
    	// Penalty for longer delivery times
    	double priority_weight = 0;
        // Take (delivery time - arrivalTime)**penalty * (1+sqrt(priority_weight))
    	if(deliveryItem instanceof PriorityMailItem){
    		priority_weight = ((PriorityMailItem) deliveryItem).getPriorityLevel();
    	}
        return Math.pow(Clock.Time() - deliveryItem.getArrivalTime(),DELIVERY_PENALTY)*(1+Math.sqrt(priority_weight));
    }

    public static void printResults(){
        System.out.println("T: "+Clock.Time()+" | Simulation complete!");
        System.out.println("Final Delivery time: "+Clock.Time());
        System.out.printf("Final Score: %.2f%n", total_score);
    }
}
