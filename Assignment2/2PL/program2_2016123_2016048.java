/* ******************************************
*  Yashit Maheshwary (2016123)
*  Kaustav Vats (2016048)
******************************************* */

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;


class Transaction implements Runnable {

	private Flight f1, f2;
	private int passengerId;
	private int option;
	public static volatile int transaction_count = 0;

	public Transaction(){
		this.option = -1;
	}

	public void putOption(int i)
	{
		this.option = i;
	}

	public void putFlight1(Flight f1)
	{
		this.f1 = f1;
	}
	public void putFlight2(Flight f2)
	{
		this.f2 = f2;
	}
	public void putPassengerId(int i)
	{
		this.passengerId = i;
	}

	private static void sleep() {
		try {
			Thread.sleep(5);
		} 
		catch (InterruptedException e) {
		}
	}

	@Override
	public void run(){
		if (option == 1) {
			Reserve(f1, passengerId);
		}
		else if(option == 2) {
			Cancel(f1, passengerId);
				
		}
		else if(option == 3) {
			My_Flights(passengerId);
		}
		else if(option == 4) {
			Total_Reservations();
		}
		else if(option == 5) {
			Transfer(f1, f2, passengerId);
		}
		else {
			System.out.println("Invalid Condition");
		}

		transaction_count += 1;
		sleep();
	}

	/**
	 * Function to reserve a seat in a flight for a passenger
	 */
	public void Reserve(Flight flight, int passengerId) {
		int i = program2_2016123_2016048.flights.indexOf(flight);
		int j = passengerId;
		try{
			if ( program2_2016123_2016048.flights_lock.get(i).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) ){
				try {
					if(flight.book(passengerId)) {
						try {
							if ( program2_2016123_2016048.passengers_lock.get(j).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) )
							{
								try {
									program2_2016123_2016048.passengers.get(passengerId).addBookedFlight(flight);
								}
								finally {
									program2_2016123_2016048.passengers_lock.get(j).unlock();
								}
							}
						}
						catch (InterruptedException e) {

						}
					}
				} 
				finally {
					program2_2016123_2016048.flights_lock.get(i).unlock();
				}
			}
		}
		catch(InterruptedException e)
		{

		}
	}

	/**
	 * Function to cancel a reservation of a flight for a passenger
	 */
	public void Cancel(Flight flight, int passengerId) {
		int i = program2_2016123_2016048.flights.indexOf(flight);
		int j = passengerId;
		try {
			if ( program2_2016123_2016048.flights_lock.get(i).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS)) {
				try {
					if(flight.cancel(passengerId)) {
						if ( program2_2016123_2016048.passengers_lock.get(j).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) )
						{
							try {
								program2_2016123_2016048.passengers.get(passengerId).removeBookedFlight(flight);
							}
							finally {
								program2_2016123_2016048.passengers_lock.get(j).unlock();
							}
						}
					}
				}
				catch ( InterruptedException e )
				{

				}
				finally {
					program2_2016123_2016048.flights_lock.get(i).unlock();
				}
			}
		} 
		catch (InterruptedException e) {

		}
	}

	/**
	 * Function to print the flights of a particular passenger
	 */
	public void My_Flights(int passengerId) {
		int j = passengerId;
		try {
			if ( program2_2016123_2016048.passengers_lock.get(j).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) )
			{
				try {
					program2_2016123_2016048.passengers.get(passengerId).getAllFlights();
				}
				finally {
					program2_2016123_2016048.passengers_lock.get(j).unlock();
				}
			}
		} 
		catch (InterruptedException e) {

		} 
	}

	/**
	 * Function to print the total number of reservations made in all the flights
	 */
	public void Total_Reservations() {
		int totalReservations = 0;
		try {
			for(Flight flight: program2_2016123_2016048.flights) {
				int i = program2_2016123_2016048.flights.indexOf(flight);
				if (program2_2016123_2016048.flights_lock.get(i).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS))
				{
					try {
						if(flight.getNumReserved() > 0) {
							totalReservations += flight.getNumReserved();
						}
					}
					finally {
						program2_2016123_2016048.flights_lock.get(i).unlock();
					}
				}
			}
			System.out.println("Total Number of Reservations are: " + String.valueOf(totalReservations));
		} catch (InterruptedException e) {

		}
		
	}

	/**
	 * Function to Transfer the user from one flight to the other
	 */
	public void Transfer(Flight f1, Flight f2, int passengerId) {
		int i = program2_2016123_2016048.flights.indexOf(f1);
		int k = program2_2016123_2016048.flights.indexOf(f2);
		int j = passengerId;

		if(i < k) {
			try{
			    if ( program2_2016123_2016048.flights_lock.get(i).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) )
			    {
			        try{
			            if ( program2_2016123_2016048.flights_lock.get(k).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) )
					    {
					        try{
					            if ( program2_2016123_2016048.passengers_lock.get(j).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) )
							    {
							        try{
							            if(f1.getPassenger(passengerId) != null) {
											if(f2.getPassengers().size() < f2.getSeats()) {
												f1.cancel(passengerId);
												f2.book(passengerId);
												program2_2016123_2016048.passengers.get(passengerId).removeBookedFlight(f1);
												program2_2016123_2016048.passengers.get(passengerId).addBookedFlight(f2);
											}
											else {
												System.out.println("No more seats available.");
											}
										}
										else {
											System.out.println("Passenger hasn't booked this flight");
										}
							        }
							        finally {
							            program2_2016123_2016048.passengers_lock.get(j).unlock();
							        }
							    }
					        }
					        catch ( InterruptedException e ) {

					        }
					        finally {
					            program2_2016123_2016048.flights_lock.get(k).unlock();
					        }
					    }
			        }
			        catch (InterruptedException e) {
			        }
			        finally {
			            program2_2016123_2016048.flights_lock.get(i).unlock();
			        }
			    }
			}
			catch (InterruptedException e) {
			}
		}
		else if(k > i) {
						try{
			    if ( program2_2016123_2016048.flights_lock.get(k).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) )
			    {
			        try{
			            if ( program2_2016123_2016048.flights_lock.get(i).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) )
					    {
					        try{
					            if ( program2_2016123_2016048.passengers_lock.get(j).tryLock(Long.MAX_VALUE,TimeUnit.MILLISECONDS) )
							    {
							        try{
							            if(f1.getPassenger(passengerId) != null) {
											if(f2.getPassengers().size() < f2.getSeats()) {
												f1.cancel(passengerId);
												f2.book(passengerId);
												program2_2016123_2016048.passengers.get(passengerId).removeBookedFlight(f1);
												program2_2016123_2016048.passengers.get(passengerId).addBookedFlight(f2);
											}
											else {
												System.out.println("No more seats available.");
											}
										}
										else {
											System.out.println("Passenger hasn't booked this flight");
										}
							        }
							        finally {
							            program2_2016123_2016048.passengers_lock.get(j).unlock();
							        }
							    }
					        }
					        catch ( InterruptedException e ) {

					        }
					        finally {
					            program2_2016123_2016048.flights_lock.get(i).unlock();
					        }
					    }
			        }
			        catch (InterruptedException e) {
			        }
			        finally {
			            program2_2016123_2016048.flights_lock.get(k).unlock();
			        }
			    }
			}
			catch (InterruptedException e) {
			}
		}
		else{
			System.out.println("Passenger is already in this flight.");
			return;
		}
	}
}

public class program2_2016123_2016048 {

	public static ArrayList<Flight> flights = new ArrayList<>();
	public static ArrayList<Passenger> passengers = new ArrayList<>();
	public static ReentrantLock lock = new ReentrantLock();
	public static ArrayList<ReentrantLock> flights_lock = new ArrayList<ReentrantLock>();
	public static ArrayList<ReentrantLock> passengers_lock = new ArrayList<ReentrantLock>();

	private static int getRand(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public static ArrayList<Flight> getFlights() {
		return flights;
	}

	public static ArrayList<Passenger> getPassengers() {
		return passengers;
	}

	public static void main(String[] args) throws IOException,  InterruptedException {
		ArrayList<Flight> flights_local =program2_2016123_2016048.getFlights();
		ArrayList<Passenger> passengers_local = program2_2016123_2016048.getPassengers();
		
		for(int i=0; i<=10; i++) {
			flights_local.add(new Flight(i,getRand(1,10)));			
			flights_lock.add(new ReentrantLock());
			passengers_local.add(new Passenger(i));
			passengers_lock.add(new ReentrantLock());
		}
		
		ExecutorService exec = Executors.newFixedThreadPool(50);
		
		long start_time = System.currentTimeMillis();
		long wait_time = 5000;
		long end_time = start_time + wait_time;


		while(System.currentTimeMillis() < end_time) {

			int randomNum = getRand(1,5);
			// System.out.println("randomNum: "+randomNum);

			int randomFlight = getRand(0, flights_local.size()-1);
			Flight selectedFlight = flights_local.get(randomFlight);

			int randomPassenger = getRand(0, passengers_local.size()-1);
			Passenger selectedPassenger = passengers_local.get(randomPassenger);

			int randomFlight2 = getRand(0, flights.size() - 1);
			Flight selectedFlight2 = flights_local.get(randomFlight);
			
			Transaction transaction = new Transaction();
			transaction.putOption(randomNum);
			transaction.putFlight1(selectedFlight);
			transaction.putFlight2(selectedFlight2);
			transaction.putPassengerId(selectedPassenger.getId());

			exec.execute(transaction);
		}
		System.out.println("Transaction Count: " + Transaction.transaction_count);
		if ( !exec.isTerminated() )
		{
			exec.shutdownNow();
		}
	}
}