import java.sql.*;
import java.util.*;

class DBConnection {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/event_system";
        String user = "root"; // change this if needed
        String password = "your_password"; // change this
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

    public String toString() {
        return id + ": " + name + " at " + location + " on " + date;
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

    public String toString() {
        return id + ": " + name + " (" + email + ")";
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
            System.out.println("\n1. Create Event\n2. List Events\n3. Register Attendee\n4. View Attendees\n5. Cancel Registration\n6. Exit");
            int choice = sc.nextInt();
            sc.nextLine(); // clear newline

            switch (choice) {
                case 1:
                    System.out.print("Event name: ");
                    String name = sc.nextLine();
                    System.out.print("Location: ");
                    String location = sc.nextLine();
                    System.out.print("Date (YYYY-MM-DD): ");
                    String date = sc.nextLine();
                    eventDAO.addEvent(new Event(name, location, date));
                    break;

                case 2:
                    List<Event> events = eventDAO.getAllEvents();
                    for (Event e : events) {
                        System.out.println(e);
                    }
                    break;

                case 3:
                    System.out.print("Event ID: ");
                    int eventId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Attendee Name: ");
                    String attendeeName = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    attendeeDAO.registerAttendee(new Attendee(attendeeName, email), eventId);
                    break;

                case 4:
                    System.out.print("Event ID: ");
                    int viewId = sc.nextInt();
                    List<Attendee> attendees = attendeeDAO.getAttendeesForEvent(viewId);
                    for (Attendee a : attendees) {
                        System.out.println(a);
                    }
                    break;

                case 5:
                    System.out.print("Event ID: ");
                    int eId = sc.nextInt();
                    System.out.print("Attendee ID: ");
                    int aId = sc.nextInt();
                    attendeeDAO.cancelRegistration(eId, aId);
                    break;

                case 6:
                    System.exit(0);
            }
        }
    }
}