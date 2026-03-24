import java.util.*;

// Actor: Reservation (Guest's booking intent)
class Reservation {
    private String guestName;
    private String roomType;
    private Date requestTime;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.requestTime = new Date(); // capture arrival time
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "guest='" + guestName + '\'' +
                ", roomType='" + roomType + '\'' +
                ", requestTime=" + requestTime +
                '}';
    }
}

// Booking Request Queue (FIFO structure)
class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        this.queue = new LinkedList<>();
    }

    // Accept booking request (enqueue)
    public void addRequest(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        queue.offer(reservation); // FIFO insertion
        System.out.println("Request added to queue: " + reservation.getGuestName());
    }

    // View next request (read-only, no removal)
    public Reservation peekNextRequest() {
        return queue.peek();
    }

    // Fetch next request for processing (dequeue)
    public Reservation getNextRequest() {
        return queue.poll();
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Display all queued requests (read-only)
    public void displayQueue() {
        System.out.println("\nCurrent Booking Queue:");
        for (Reservation r : queue) {
            System.out.println(r);
        }
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Initialize Booking Request Queue
        BookingRequestQueue requestQueue = new BookingRequestQueue();

        // Step 2: Simulate Guest Booking Requests
        Reservation r1 = new Reservation("Alice", "Single");
        Reservation r2 = new Reservation("Bob", "Suite");
        Reservation r3 = new Reservation("Charlie", "Double");

        // Step 3: Add requests to queue (arrival order preserved)
        requestQueue.addRequest(r1);
        requestQueue.addRequest(r2);
        requestQueue.addRequest(r3);

        // Step 4: Display queued requests
        requestQueue.displayQueue();

        // Step 5: Peek (no removal, read-only)
        System.out.println("\nNext request to process (peek):");
        System.out.println(requestQueue.peekNextRequest());

        // Step 6: Simulate processing (dequeue)
        System.out.println("\nProcessing requests in FIFO order:");
        while (!requestQueue.isEmpty()) {
            Reservation next = requestQueue.getNextRequest();
            System.out.println("Processing: " + next.getGuestName());
        }

        // Important: No inventory mutation occurs here
    }
}