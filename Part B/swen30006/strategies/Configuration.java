package strategies;

import automail.Robot;

public class Configuration {

    private Robot.RobotType robot1Type;

    private Robot.RobotType robot2Type;

    public Configuration(Robot.RobotType robot1Type, Robot.RobotType robot2Type) {
        this.robot1Type = robot1Type;
        this.robot2Type = robot2Type;
    }


    public Robot.RobotType getRobot1Type() {
        return robot1Type;
    }

    public Robot.RobotType getRobot2Type() {
        return robot2Type;
    }
}
