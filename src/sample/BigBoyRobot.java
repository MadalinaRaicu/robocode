package sample;

import java.awt.Color;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class BigBoyRobot extends Robot {

	double moveAmount; // How much to move

	public void changeColor(Color color) {
		// Set colors
		setBodyColor(color);
		setGunColor(color);
		setRadarColor(color);
		setBulletColor(color);
		setScanColor(color);
	}

	/**
	 * run: Move around the walls
	 */
	public void run() {

		changeColor(Color.blue);

		// Initialize moveAmount to the maximum possible for this battlefield.
		moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		// Initialize peek to false


		// turnLeft to face a wall.
		// getHeading() % 90 means the remainder of
		// getHeading() divided by 90.
		turnLeft(getHeading() % 90);
		ahead(moveAmount);
		// Turn the gun to turn right 90 degrees.
		turnGunRight(90);
		turnRight(90);

		while(true){
		    ahead(100); //Go ahead 100 pixels
		    turnGunRight(360); //scan
		    back(75); //Go back 75 pixels
		    turnGunRight(360); //scan

		    //For each second the robot go ahead 25 pixels.
		}
	}
	
	/**
	 * onHitWall: Move away a bit.
	 */
	public void onHitWall(HitWallEvent e){
	    double bearing = e.getBearing(); //get the bearing of the wall
	    turnRight(-bearing); //This isn't accurate but release your robot.
	    ahead(100); //The robot goes away from the wall.
	}

	/**
	 * onHitRobot: Move away a bit.
	 */
	public void onHitRobot(HitRobotEvent e) {
		changeColor(Color.ORANGE);

		// If he's in front of us, set back up a bit.
		if (e.getBearing() > -90 && e.getBearing() < 90) {
			back(100);
		} // else he's in back of us, so set ahead a bit.
		else {
			ahead(100);
		}
	}

	/**
	 * onScannedRobot: Fire!
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		changeColor(Color.RED);
		double distance = e.getDistance(); // get the distance of the scanned
											// robot
		if (distance > 800) // this conditions adjust the fire force according
							// the distance of the scanned robot.
			fire(5);
		else if (distance > 600 && distance <= 800)
			fire(4);
		else if (distance > 400 && distance <= 600)
			fire(3);
		else if (distance > 200 && distance <= 400)
			fire(2);
		else if (distance < 200)
			fire(1);
		changeColor(Color.BLUE);
	}

}