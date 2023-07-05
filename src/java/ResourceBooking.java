import java.sql.*;
import java.util.Scanner;
​
public class ResourceBooking {
​
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String USER = "username";
    private static final String PASS = "password";
​
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Please select an operation:");
                System.out.println("1: Find the next free time slot for 'Space Laser'.");
                System.out.println("2: Find the last maintenance date for 'Space Laser'.");
                System.out.println("3: Average hours per week 'Space Laser' is occupied.");
                System.out.println("4: Peak usage time for 'Space Laser' in the past month.");
                System.out.println("5: User with most hours booked on 'Space Laser'.");
                System.out.println("6: Count 'unicorn'.");
                System.out.println("7: Add a unicorn.");
                System.out.println("8: Most under utilized resource.");
                System.out.println("9: Last time Invisibility Cloak was used.");
                System.out.println("10: All booked resources.");
                System.out.println("11: Create a new user.");
                System.out.println("12: Update a user.");
                System.out.println("0: Exit.");
​
                int choice = scanner.nextInt();
​
                switch (choice) {
                    case 1:
                        getNextFreeTimeSlot(conn);
                        break;
                    case 2:
                        getLastMaintenanceDate(conn);
                        break;
                    case 3:
                        getAvgHoursPerWeek(conn);
                        break;
                    case 4:
                        getPeakUsageTime(conn);
                        break;
                    case 5:
                        getUserWithMostHours(conn);
                        break;
                    case 6:
                        countUnicorns(conn);
                        break;
                    case 7:
                        addUnicorn(conn);
                        break;
                    case 8:
                        getUnderUtilizedResource(conn);
                        break;
                    case 9:
                        getLastUseOfInvisibilityCloak(conn);
                        break;
                    case 10:
                        getAllBookedResources(conn);
                        break;
                    case 11:
                        createNewUser(conn);
                        break;
                    case 12:
                        updateUser(conn);
                        break;
                    case 0:
                        System.out.println("Exiting.");
                        scanner.close();
                        return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
​
​
​
private static void getPeakUsageTime(Connection conn) throws SQLException {
    String sql = "SELECT BookingStartTime, COUNT(*) as Frequency FROM Booking WHERE ResourceID = 1 AND BookingDate >= DATE_ADD(CURRENT_DATE, INTERVAL -1 MONTH) GROUP BY BookingStartTime ORDER BY Frequency DESC LIMIT 1;";
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            System.out.println("Peak usage time: " + rs.getTime("BookingStartTime") + ", Frequency: " + rs.getInt("Frequency"));
        } else {
            System.out.println("No booking records found.");
        }
    }
}
​
private static void getUserWithMostHours(Connection conn) throws SQLException {
    String sql = "SELECT User.UserID, User.UserName, SUM(TIMESTAMPDIFF(HOUR, Booking.BookingStartTime, Booking.BookingEndTime)) as TotalBookingHours FROM Booking INNER JOIN User ON Booking.UserID = User.UserID WHERE Booking.ResourceID = 1 GROUP BY User.UserID, User.UserName ORDER BY TotalBookingHours DESC LIMIT 1;";
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            System.out.println("User ID: " + rs.getInt("UserID") + ", User name: " + rs.getString("UserName") + ", Total booking hours: " + rs.getInt("TotalBookingHours"));
        } else {
            System.out.println("No booking records found.");
        }
    }
}
​
private static void countUnicorns(Connection conn) throws SQLException {
    String sql = "SELECT COUNT(*) FROM Resource WHERE ResourceName = 'unicorn';";
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            System.out.println("Count of unicorns: " + rs.getInt(1));
        } else {
            System.out.println("No unicorns found.");
        }
    }
}
​
private static void addUnicorn(Connection conn) throws SQLException {
    String sql = "INSERT INTO Resource(ResourceID, ResourceName, ResourceDescription, ResourceStatus, MaintenanceStatus) VALUES (6, 'unicorn', 'rainbows and unicorns', 'Booked', 'Requires Maintenance');";
    try (Statement stmt = conn.createStatement()) {
        int rowsAffected = stmt.executeUpdate(sql);
        System.out.println(rowsAffected + " rows affected.");
    }
}
​
private static void getUnderUtilizedResource(Connection conn) throws SQLException {
    String sql = "SELECT Resource.ResourceName, SUM(TIMESTAMPDIFF(HOUR, Booking.BookingStartTime, Booking.BookingEndTime)) as TotalBookingHours FROM Booking INNER JOIN Resource ON Booking.ResourceID = Resource.ResourceID GROUP BY Booking.ResourceID, Resource.ResourceName ORDER BY TotalBookingHours ASC;";
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            System.out.println("Resource name: " + rs.getString("ResourceName") + ", Total booking hours: " + rs.getInt("TotalBookingHours"));
        } else {
            System.out.println("No booking records found.");
        }
    }
}
​
private static void getLastUseOfInvisibilityCloak(Connection conn) throws SQLException {
    String sql = "SELECT Booking.BookingDate, Booking.BookingStartTime, Booking.BookingEndTime FROM Booking INNER JOIN Resource ON Booking.ResourceID = Resource.ResourceID WHERE Resource.ResourceName = 'Invisibility Cloak' ORDER BY Booking.BookingDate DESC, Booking.BookingEndTime DESC LIMIT 1;";
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            System.out.println("Booking date: " + rs.getDate("BookingDate") + ", Start time: " + rs.getTime("BookingStartTime") + ", End time: " + rs.getTime("BookingEndTime"));
        } else {
            System.out.println("No booking records found.");
        }
    }
}
​
private static void getAllBookedResources(Connection conn) throws SQLException {
    String sql = "SELECT Resource.ResourceID, Resource.ResourceName, COUNT(*) as TotalBookings FROM Booking INNER JOIN Resource ON Booking.ResourceID = Resource.ResourceID GROUP BY Resource.ResourceID, Resource.ResourceName;";
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println("Resource ID: " + rs.getInt("ResourceID") + ", Resource name: " + rs.getString("ResourceName") + ", Total bookings: " + rs.getInt("TotalBookings"));
        }
    }
}
​
private static void createNewUser(Connection conn) throws SQLException {
    String sql = "INSERT INTO User(UserID, UserName, UserEmail, UserPassword, UserContact, Role) VALUES (6, 'Danny DataCruncher', 'dannydatacruncher@email.com', '24680', '10 Binary Boulevard', 'user');";
    try (Statement stmt = conn.createStatement()) {
        int rowsAffected = stmt.executeUpdate(sql);
        System.out.println(rowsAffected + " rows affected.");
    }
}
​
private static void updateUser(Connection conn) throws SQLException {
    String sql = "UPDATE User SET UserName = 'Danny DataCruncher', UserEmail = 'dannydatacruncher_new@email.com', UserPassword = '02468', UserContact = '20 Binary Boulevard', Role = 'admin' WHERE UserID = 6;";
    try (Statement stmt = conn.createStatement()) {
        int rowsAffected = stmt.executeUpdate(sql);
        System.out.println(rowsAffected + " rows affected.");
    }
}
​
private static void getNextFreeTimeSlot(Connection conn) throws SQLException {
    String sql = "SELECT DATE_ADD(BookingDate, INTERVAL 1 DAY) as FreeDate FROM Booking WHERE ResourceID = 1 AND (BookingEndTime <= '01:00:00' OR BookingStartTime >= '05:00:00') ORDER BY BookingDate DESC LIMIT 1;";
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            System.out.println("Next free time slot for 'Space Laser': " + rs.getTimestamp("FreeDate"));
        } else {
            System.out.println("No booking records found.");
        }
    }
}
​
private static void getLastMaintenanceDate(Connection conn) throws SQLException {
    String sql = "SELECT MaintenanceDate FROM Maintenance WHERE ResourceID = 4 ORDER BY MaintenanceDate DESC LIMIT 1;";
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            System.out.println("Last maintenance performed on 'Space Laser': " + rs.getDate("MaintenanceDate"));
        } else {
            System.out.println("No maintenance records found.");
        }
    }
}
​
private static void getAvgHoursPerWeek(Connection conn) throws SQLException {
    String sql = "SELECT AVG(BookingHoursPerWeek) as AverageWeeklyBookingHours FROM (SELECT WEEK(BookingDate) as BookingWeek, SUM(TIMESTAMPDIFF(HOUR, BookingStartTime, BookingEndTime)) as BookingHoursPerWeek FROM Booking WHERE ResourceID = 1 GROUP BY WEEK(BookingDate)) as weeklyBookingHours;";
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            System.out.println("Average hours per week 'Space Laser' is occupied: " + rs.getFloat("AverageWeeklyBookingHours"));
        } else {
            System.out.println("No booking records found.");
        }
    }
}
​
​
​
}
