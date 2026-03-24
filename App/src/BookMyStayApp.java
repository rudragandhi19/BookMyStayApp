import java.util.*;

// Domain Model: Room
class Room {
    private String type;
    private double price;
    private List<String> amenities;

    public Room(String type, double price, List<String> amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getAmenities() {
        return amenities;
    }
}

// Inventory as State Holder (read-only access in search)
class Inventory {
    private Map<String, Integer> availabilityMap;

    public Inventory() {
        availabilityMap = new HashMap<>();
    }

    public void setAvailability(String roomType, int count) {
        availabilityMap.put(roomType, count);
    }

    // Read-only access method
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }

    public Set<String> getAllRoomTypes() {
        return availabilityMap.keySet();
    }
}

// Search Service (Read-only logic)
class SearchService {
    private Inventory inventory;
    private Map<String, Room> roomCatalog;

    public SearchService(Inventory inventory, Map<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    // Core search method
    public List<Room> searchAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();

        for (String roomType : inventory.getAllRoomTypes()) {

            int availableCount = inventory.getAvailability(roomType);

            // Defensive Programming: filter invalid or unavailable rooms
            if (availableCount <= 0) {
                continue;
            }

            Room room = roomCatalog.get(roomType);

            // Defensive check: ensure room exists in catalog
            if (room != null) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Setup Room Catalog (Domain Model)
        Map<String, Room> roomCatalog = new HashMap<>();

        roomCatalog.put("Single",
                new Room("Single", 2000, Arrays.asList("WiFi", "TV")));

        roomCatalog.put("Double",
                new Room("Double", 3500, Arrays.asList("WiFi", "TV", "AC")));

        roomCatalog.put("Suite",
                new Room("Suite", 6000, Arrays.asList("WiFi", "TV", "AC", "Mini Bar")));

        // Step 2: Setup Inventory (State Holder)
        Inventory inventory = new Inventory();
        inventory.setAvailability("Single", 3);
        inventory.setAvailability("Double", 0); // unavailable
        inventory.setAvailability("Suite", 2);

        // Step 3: Create Search Service
        SearchService searchService = new SearchService(inventory, roomCatalog);

        // Step 4: Guest searches for rooms
        List<Room> availableRooms = searchService.searchAvailableRooms();

        // Step 5: Display results (Read-only output)
        System.out.println("Available Rooms:");

        for (Room room : availableRooms) {
            System.out.println("----------------------");
            System.out.println("Type: " + room.getType());
            System.out.println("Price: ₹" + room.getPrice());
            System.out.println("Amenities: " + room.getAmenities());
        }

        // Important: Inventory state remains unchanged
    }
}