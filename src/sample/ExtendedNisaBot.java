package sample;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

public class ExtendedNisaBot extends AdvancedRobot {
	private int timeSinceLastScan = 5; // set timeSinceLastScan > 3 so that radarOffset wont be calculated if enemy bot is not detected
	static double enemyAbsoluteBearing; // variable to store bearing of enemy robot not relative to robot'sheading
	int moveDirection = 1; // variable to toggle movement direction when bullet hits enemy robot
	double moveAmount; // How much to move, determined by battlefield size

	public void changeColor(Color color) {
		setBodyColor(color);
		setGunColor(color);
		setRadarColor(color);
		setBulletColor(color);
		setScanColor(color);
	}

	public void run() {
		setAdjustGunForRobotTurn(true); // if the robot is turned, don't turn the gun
		setAdjustRadarForGunTurn(true); // if the gun is turned, don't turn the radar

		moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		// how much to move, determined by battlefield size
		turnLeft(getHeading() % 90); // turnLeft to face a wall. 
		ahead(moveAmount); // move forward until robot hits a wall
		do {
			doScanner(); // scans and locks onto enemy robot
			execute(); // executes all pending actions --> eg. move forward, turn, fire...
		} while (true);
	}

	//method triggered when radar detects an enemy robot
	public void onScannedRobot(ScannedRobotEvent e) {
		enemyAbsoluteBearing = getHeadingRadians() + e.getBearingRadians(); //get enemy robots's bearings
		timeSinceLastScan = 0; //when enemy bot is detected, timeSinceLastScan = 0 to enable doScanner() to calculate radarOffset
		if (e.getDistance() <= 250) { 
			// When enemy bot is near, hornet fires bullets, firing at near distance ensures a high hit rate
			setFire(2);
		}
		setAhead(100 * moveDirection);
	}

	//when robot hits wall, it turns 90 degrees to the right.
	public void onHitWall(HitWallEvent e) {
		turnRight(90);
	}
	//when bullet hits enemy bot, hornet reverses its direction to get near enemy bot for another shot
	public void onBulletHit(BulletHitEvent e) {
		if (getEnergy() > 30)
			moveDirection *= -1;
	}

	//when robot hits an enemy bot, it turns 90 degrees to the right
	public void onHitRobot(HitRobotEvent e) {
		changeColor(Color.ORANGE);
		
		// If he's in front of us, set back up a bit.
		if (e.getBearing() > -90 && e.getBearing() < 90) {
			back(100);
			execute();
		} // else he's in back of us, so set ahead a bit.
		else {
			turnGunRight(180);
			execute();
			ahead(100);
		}
	}

	//calculate radarOffset
	public void doScanner() {
		timeSinceLastScan++; //increment timeSinceLastScan if no enemy bot is detected for 3 ticks(lock on enemy slipped) therefore radar is allowed to spin again to search for a new enemy bot  
			double radarOffset = Double.POSITIVE_INFINITY; //spins radar continously till it gets a lock on an enemy bot
			if(timeSinceLastScan < 3) {
				//calculates how much the radar has to move to hold the lock on the enemy bot
				radarOffset = robocode.util.Utils.normalRelativeAngle(getRadarHeadingRadians() - enemyAbsoluteBearing);
				radarOffset += sign(radarOffset) * 0.02;
	        }
		setTurnRadarLeftRadians(radarOffset); //turns radar for the amount of offs angle to keep lock on enemy bot
		setTurnGunLeftRadians(getGunHeadingRadians() - getRadarHeadingRadians()); //turns gun to aim towards enemy bot locked onto
	}

	// radar movement direction determined by direction of movement of enemy robot
	int sign(double v) {
		return v > 0 ? 1 : -1;
	}
}