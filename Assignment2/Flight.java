/* ******************************************
*  Yashit Maheshwary (2016123)
*  Kaustav Vats (2016048)
******************************************* */

import java.io.*;
import java.util.*;

public class Flight {
	
	private ArrayList<Passenger> passengers;
	private int seats;
	private int id;

	// Constructors

	public Flight() {
		this.passengers = new ArrayList<>();
	}

	public Flight(int id, int seats) {
		this.seats = seats;
		this.id = id;
		this.passengers = new ArrayList<>();		
	}

	// Getters and Setters

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the seats
	 */
	public int getSeats() {
		return seats;
	}

	/**
	 * @param seats the seats to set
	 */
	public void setSeats(int seats) {
		this.seats = seats;
	}

	/**
	 * @return the passengers
	 */
	public ArrayList<Passenger> getPassengers() {
		return passengers;
	}

	/**
	 * @param passengers the passengers to set
	 */
	public void setPassengers(ArrayList<Passenger> passengers) {
		this.passengers = passengers;
	}

	// Functions

	public int getNumReserved() {
		return this.passengers.size();
	}

	public Passenger getPassenger(int passengerId) {
		for (Passenger p : this.passengers) {
			if (p.getId() == passengerId) {
				return p;
			}
		}
		return null;
	}

	public void book(int passengerId) {
		Passenger passenger = getPassenger(passengerId);
		if(passenger != null)
			if(getNumReserved() < this.seats)
				if(!this.passengers.contains(passenger))
					this.passengers.add(passenger);
				else
					System.out.println("Passenger Already Booked this Flight.");
			else
				System.out.println("No more seats available.");
		else
			System.out.println("Passenger doesn't exist.");
			
	}

	public void cancel(int passengerId) {
		Passenger passenger = getPassenger(passengerId);
		boolean passengerFound = false;
		if(passenger != null) {
			for(Passenger p : this.passengers) {
				if(p.getId() == passenger.getId()) {
					this.passengers.remove(this.passengers.indexOf(passenger));
					passengerFound = true;
				}
			}
			if(!passengerFound)
				System.out.println("No Passenger Found");
		}
		else {
			System.out.println("Passenger doesn't exist.");
		}
	}
}