import java.sql.*;
import java.util.*;

class DBConnection {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/event_system";
        String user = "root"; // change as needed
        String password = "17072004"; // change as needed
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }
}

class Event {
    private int id;
    private String name;
    private String location;
    private String date;

    public Event(int id, String name, String location, String date) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public Event(String name, String location, String date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return String.format("Event ID: %-4d | Name: %-20s | Location: %-15s | Date: %s",
                             id, name, location, date);
    }
}

class Attendee {
    private int id;
    private String name;
    private String email;

    public Attendee(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Attendee(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return String.format("Attendee ID: %-4d | Name: %-20s | Email: %s",
                             id, name, email);
    }
}

class EventDAO {
    public void addEvent(Event event) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO events (name, location, date) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getLocation());
            stmt.setString(3, event.getDate());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM events");
            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
}

class AttendeeDAO {
    public void registerAttendee(Attendee attendee, int eventId) {
        try (Connection conn = DBConnection.getConnection()) {
            String insertAttendee = "INSERT INTO attendees (name, email) VALUES (?, ?)";
            PreparedStatement stmt1 = conn.prepareStatement(insertAttendee, Statement.RETURN_GENERATED_KEYS);
            stmt1.setString(1, attendee.getName());
            stmt1.setString(2, attendee.getEmail());
            stmt1.executeUpdate();

            ResultSet keys = stmt1.getGeneratedKeys();
            if (keys.next()) {
                int attendeeId = keys.getInt(1);
                String register = "INSERT INTO event_registrations (event_id, attendee_id) VALUES (?, ?)";
                PreparedStatement stmt2 = conn.prepareStatement(register);
                stmt2.setInt(1, eventId);
                stmt2.setInt(2, attendeeId);
                stmt2.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Attendee> getAttendeesForEvent(int eventId) {
        List<Attendee> attendees = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT a.id, a.name, a.email FROM attendees a " +
                         "JOIN event_registrations er ON a.id = er.attendee_id " +
                         "WHERE er.event_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attendees.add(new Attendee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attendees;
    }

    public void cancelRegistration(int eventId, int attendeeId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM event_registrations WHERE event_id = ? AND attendee_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, eventId);
            stmt.setInt(2, attendeeId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class EventRegistrationSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EventDAO eventDAO = new EventDAO();
        AttendeeDAO attendeeDAO = new AttendeeDAO();

        while (true) {
            System.out.println("\n====== Event Registration System ======");
            System.out.println("1. Create Event");
            System.out.println("2. List Events");
            System.out.println("3. Register Attendee");
            System.out.println("4. View Attendees");
            System.out.println("5. Cancel Registration");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Event Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Location: ");
                    String location = sc.nextLine();
                    System.out.print("Enter Date (YYYY-MM-DD): ");
                    String date = sc.nextLine();
                    eventDAO.addEvent(new Event(name, location, date));
                    System.out.println("Event added successfully!");
                    break;

                case 2:
                    List<Event> events = eventDAO.getAllEvents();
                    System.out.println("\n--- List of Events ---");
                    System.out.printf("%-10s %-25s %-20s %-12s\n", "Event ID", "Name", "Location", "Date");
                    System.out.println("---------------------------------------------------------------");
                    for (Event e : events) {
                        System.out.printf("%-10d %-25s %-20s %-12s\n",
                                e.getId(), e.getName(), e.getLocation(), e.getDate());
                    }
                    break;

                case 3:
                    System.out.print("Enter Event ID: ");
                    int eventId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Attendee Name: ");
                    String attendeeName = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    attendeeDAO.registerAttendee(new Attendee(attendeeName, email), eventId);
                    System.out.println("Attendee registered successfully!");
                    break;

                case 4:
                    System.out.print("Enter Event ID: ");
                    int viewId = sc.nextInt();
                    List<Attendee> attendees = attendeeDAO.getAttendeesForEvent(viewId);
                    System.out.println("\n--- Attendees for Event ID: " + viewId + " ---");
                    System.out.printf("%-15s %-25s %-30s\n", "Attendee ID", "Name", "Email");
                    System.out.println("------------------------------------------------------------");
                    for (Attendee a : attendees) {
                        System.out.printf("%-15d %-25s %-30s\n",
                                a.getId(), a.getName(), a.getEmail());
                    }
                    break;

                case 5:
                    System.out.print("Enter Event ID: ");
                    int eId = sc.nextInt();
                    System.out.print("Enter Attendee ID: ");
                    int aId = sc.nextInt();
                    attendeeDAO.cancelRegistration(eId, aId);
                    System.out.println("Registration canceled successfully!");
                    break;

                case 6:
                    System.out.println("Thank you for using the Event Registration System. Goodbye!");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
