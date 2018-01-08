package org.usfirst.frc.team6679.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;

public class Robot extends IterativeRobot 
{
	// Code Programmed by Sharjeel Junaid (https://www.facebook.com/sharjeel.junaid)

	/*
	 * 	START OF README
	 * 
	 * 	Wiring:
	 * 		Front Left Drive Motor = PWM Port 2
	 * 		Front Right Drive Motor = PWM Port 3
	 *  	Back Left Drive Motor = PWM Port 1
	 *  	Back Right Drive Motor = PWM Port 4
	 *  	Arm Motor = PWM Port 5
	 *  	Arm Solenoid 1 = PCM Port 0
	 *  	Arm Solenoid 2 = PCM Port 1
	 *  
	 *  Controls:
	 *  	Left Stick = Moves the robot (arcade drive)
	 *  	A Button = Opens the arms
	 *  	B Button = Closes the arms
	 *  	Right Trigger = Moves the arm up
	 *  	Left Trigger = Moves the arm down
	 *  	Start Button = Enables the robot to move / use inputs from the joystick
	 *  	Back Button = Disables the robot from moving / using inputs from the joystick
	 *  
	 *  END OF README
	 */
	
	// Initialize joystick and the X + Y axis
	private Joystick stick1 = new Joystick(0);
	private double stick1Y = 0;
	private double stick1X = 0;

	// Initialize left & right drivetrain powers
	private double leftPower = 0;
	private double rightPower = 0;

	// Initialize the drivetrain motors
	private VictorSP leftDriveMotor1;
	private VictorSP leftDriveMotor2;
	private VictorSP rightDriveMotor1;
	private VictorSP rightDriveMotor2;
	
	// Initialize the arm motor
	//private CANTalon armMotor;
	private VictorSP armMotor;
	
	// Initialize the arm solenoids
	private Solenoid armSolenoid1;
	private Solenoid armSolenoid2;
	
	// Initialize a boolean to toggle the robot's controls on and off
	private boolean drivingEnabled = false;

	// Initialize an array to store the status of the joystick buttons
	private boolean[] stick1ButtonPressed = new boolean[10];

	// Code run when the robot is turned on
	public void robotInit()
	{
		// Assigns all the motors and solenoids to their respective objects (the number in () is the port # of what is connected where)
		leftDriveMotor1 = new VictorSP(2);
		leftDriveMotor2 = new VictorSP(3);
		rightDriveMotor1 = new VictorSP(1);
		rightDriveMotor2 = new VictorSP(4);
		//armMotor = new CANTalon(0);
		armMotor = new VictorSP(5);
		armSolenoid1 = new Solenoid(0);
		armSolenoid2 = new Solenoid(1);	
	}

	// Code run non stop during the teleop mode
	public void teleopPeriodic() 
	{		
		// Calls the method to get & store the input from the joystick
		getInput();
		
		// Start Button (Allows the drivetrain motors to run)
		if (stick1.getRawButton(8) && stick1ButtonPressed[8] == false) 
		{
			stick1ButtonPressed[8] = true;
			drivingEnabled = true;
		}
		else if (stick1.getRawButton(8) && stick1ButtonPressed[8] == true) 
		{
			stick1ButtonPressed[8] = false;
		}
		
		// Back Button (Stops the drivetrain motors from running)
		if (stick1.getRawButton(7) && stick1ButtonPressed[7] == false) 
		{
			stick1ButtonPressed[7] = true;
			drivingEnabled = false;
		}
		else if (stick1.getRawButton(7) && stick1ButtonPressed[7] == true) 
		{
			stick1ButtonPressed[7] = false;
		}
		
		// A Button (Opens the arms)
		if (stick1.getRawButton(1) && stick1ButtonPressed[1] == false) 
		{
			stick1ButtonPressed[1] = true;
			armSolenoid1.set(false);
			armSolenoid2.set(true);
		}
		else if (stick1.getRawButton(1) == false && stick1ButtonPressed[1]) 
		{
			stick1ButtonPressed[1] = false;
		}

		// B Button (Closes the arms)
		if (stick1.getRawButton(2) && stick1ButtonPressed[2] == false) 
		{
			stick1ButtonPressed[2] = true;
			armSolenoid1.set(true);
			armSolenoid2.set(false);
		}
		else if (stick1.getRawButton(2) == false && stick1ButtonPressed[2]) 
		{
			stick1ButtonPressed[2] = false;
		}
		
		// Sets the value of the arm motor to either go up, down or stop providing power depending on which trigger (left / right) is being pressed (does not allow the motor to run if the driving toggle is set to off)
		if (drivingEnabled == true)
		{
			if (stick1.getRawAxis(3) > 0)
				armMotor.set(stick1.getRawAxis(3));
			else if (stick1.getRawAxis(2) > 0)
				armMotor.set(-stick1.getRawAxis(2));
			else
				armMotor.set(0);
		}
		
		// Sets the drivetrain motors to off if the driving toggle has been disabled
		if (drivingEnabled == false)
		{
			leftPower = 0;
			rightPower = 0;
		}

		// Passes on the input grabbed from the above method to move the robot around
		drive();
	}

	// Method to get called in order to get the input from the user
	public void getInput() 
	{
		// Gets the X & Y axis input from the joystick's left stick
		stick1Y = stick1.getY();
		stick1X = stick1.getX();

		// Calculates the final X & Y values to send to the motors
		double x = (0.5 * stick1X * stick1X * stick1X) + (0.5 * stick1X);
		double y = (0.5 * stick1Y * stick1Y * stick1Y) + (0.5 * stick1Y);

		// If the Start button is pressed stops the motors
		if (stick1.getRawButton(8) == false) 
		{
			leftPower = -(y - x);
			rightPower = (y + x);
		}
		// Stops the motor(s)
		else 
		{
			leftPower = 0;
			rightPower = 0;
		}
	}

	// Method to set the motor speeds to what has been stored from the getInput method
	public void drive() 
	{
		leftDriveMotor1.set(leftPower);
		leftDriveMotor2.set(leftPower);
		rightDriveMotor1.set(rightPower);
		rightDriveMotor2.set(rightPower);
	}

}
