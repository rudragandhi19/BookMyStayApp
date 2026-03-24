import java.util.*;

// Reservation (from previous queue system)
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// Inventory Service (state holder + mutation allowed here)
class InventoryService {
    private Map<String, Integer> availability = new HashMap<>();

    public void setAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public void decrement(String roomType) {
        int count = getAvailability(roomType);
        if (count <= 0) {
            throw new IllegalStateException("No rooms available for type: " + roomType);
        }
        availability.put(roomType, count - 1);
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNext() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Booking Service (core allocation logic)
class BookingService {

    private InventoryService inventoryService;

    // Track all allocated room IDs (global uniqueness)
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Map room type -> assigned room IDs
    private Map<String, Set<String>> roomTypeToIds = new HashMap<>();

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Process one reservation safely
    public void processReservation(Reservation reservation) {

        String roomType = reservation.getRoomType();

        // Step 1: Check availability
        if (inventoryService.getAvailability(roomType) <= 0) {
            System.out.println("❌ No availability for " + roomType +
                    " (Guest: " + reservation.getGuestName() + ")");
            return;
        }

        // Step 2: Generate unique room ID
        String roomId = generateUniqueRoomId(roomType);

        // Step 3: Atomic logical operation
        // (assign + update inventory together)
        allocatedRoomIds.add(roomId);

        roomTypeToIds
                .computeIfAbsent(roomType, k -> new HashSet<>())
                .add(roomId);

        // Step 4: Update inventory immediately
        inventoryService.decrement(roomType);

        // Step 5: Confirm reservation
        System.out.println("✅ Booking Confirmed!");
        System.out.println("Guest: " + reservation.getGuestName());
        System.out.println("Room Type: " + roomType);
        System.out.println("Assigned Room ID: " + roomId);
        System.out.println("-----------------------------");
    }

    // Unique ID generator with collision protection
    private String generateUniqueRoomId(String roomType) {
        String roomId;
        do {
            roomId = roomType.substring(0, 1).toUpperCase() +
                    UUID.randomUUID().toString().substring(0, 6);
        } while (allocatedRoomIds.contains(roomId)); // enforce uniqueness

        return roomId;
    }

    // Process entire queue in FIFO order
    public void processQueue(BookingRequestQueue queue) {
        while (!queue.isEmpty()) {
            Reservation r = queue.getNext();
            processReservation(r);
        }
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.setAvailability("Single", 2);
        inventory.setAvailability("Double", 1);

        // Step 2: Setup Booking Queue
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Alice", "Single"));
        queue.addRequest(new Reservation("Bob", "Single"));
        queue.addRequest(new Reservation("Charlie", "Single")); // should fail
        queue.addRequest(new Reservation("David", "Double"));

        // Step 3: Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Step 4: Process requests (FIFO)
        bookingService.processQueue(queue);
    }
}