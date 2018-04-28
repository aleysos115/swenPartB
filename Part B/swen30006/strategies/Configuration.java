package strategies;

import automail.Robot;

public class Configuration {

    private Robot.RobotType robot1Type;

    private Robot.RobotType robot2Type;

    private String robot1Id;

    private String robot2Id;

    public Configuration(Robot robot1, Robot robot2) {
        this.robot1Type = robot1.getType();
        this.robot2Type = robot2.getType();

        this.robot1Id = robot1.getId();
        this.robot2Id = robot2.getId();
    }


    public Robot.RobotType getRobot1Type() {
        return robot1Type;
    }

    public Robot.RobotType getRobot2Type() {
        return robot2Type;
    }


    public String getRobot1Id() {
        return robot1Id;
    }

    public String getRobot2Id() {
        return robot2Id;
    }

    public boolean containsWeakRobot() {
        if (robot1Type == Robot.RobotType.WEAK || robot2Type == Robot.RobotType.WEAK) {
            return true;
        } else {
            return false;
        }
    }

}
