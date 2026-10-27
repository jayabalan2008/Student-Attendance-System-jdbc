
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/college";
        String user = "root";
        String password = "1234";

        try (
            Connection con = DriverManager.getConnection(
                url, user, password
            );
            Scanner sc = new Scanner(System.in)
        ) {
            System.out.println("Database connected successfully!");

            int choice;

            do {
                System.out.println("\n--- STUDENT ATTENDANCE ---");
                System.out.println("1. Mark Attendance");
                System.out.println("2. Display Student Records");
                System.out.println("3. Calculate Attendance Percentage");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1: {
                        System.out.print("Enter Student ID: ");
                        int id = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter Date (YYYY-MM-DD): ");
                        String date = sc.nextLine();

                        System.out.print("Enter Status (Present/Absent): ");
                        String status = sc.nextLine();

                        if (!status.equalsIgnoreCase("Present")
                                && !status.equalsIgnoreCase("Absent")) {
                            System.out.println("Invalid status!");
                            break;
                        }

                        String sql =
                            "INSERT INTO attendance VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE status = ?";

                        try (PreparedStatement ps =
                                con.prepareStatement(sql)) {

                            ps.setInt(1, id);
                            ps.setDate(2, Date.valueOf(date));
                            ps.setString(3, status);
                            ps.setString(4, status);

                            ps.executeUpdate();
                            System.out.println("Attendance marked successfully!");
                        }
                        break;
                    }

                    case 2: {
                        System.out.print("Enter Student ID: ");
                        int id = Integer.parseInt(sc.nextLine());

                        String sql =
                            "SELECT * FROM attendance WHERE student_id = ? " +
                            "ORDER BY attendance_date";

                        try (PreparedStatement ps =
                                con.prepareStatement(sql)) {

                            ps.setInt(1, id);

                            try (ResultSet rs = ps.executeQuery()) {
                                System.out.println("Date         Status");

                                boolean found = false;

                                while (rs.next()) {
                                    found = true;
                                    System.out.println(
                                        rs.getDate("attendance_date") + "   " +
                                        rs.getString("status")
                                    );
                                }

                                if (!found) {
                                    System.out.println("No records found.");
                                }
                            }
                        }
                        break;
                    }

                    case 3: {
                        System.out.print("Enter Student ID: ");
                        int id = Integer.parseInt(sc.nextLine());

                        String sql =
                            "SELECT COUNT(*) AS total, " +
                            "SUM(CASE WHEN status = 'Present' " +
                            "THEN 1 ELSE 0 END) AS present_count " +
                            "FROM attendance WHERE student_id = ?";

                        try (PreparedStatement ps =
                                con.prepareStatement(sql)) {

                            ps.setInt(1, id);

                            try (ResultSet rs = ps.executeQuery()) {
                                rs.next();

                                int total = rs.getInt("total");
                                int present = rs.getInt("present_count");

                                if (total == 0) {
                                    System.out.println("No attendance records.");
                                } else {
                                    double percentage =
                                        (present * 100.0) / total;

                                    System.out.printf(
                                        "Attendance Percentage: %.2f%%%n",
                                        percentage
                                    );
                                }
                            }
                        }
                        break;
                    }

                    case 4:
                        System.out.println("Exiting application.");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }

            } while (choice != 4);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
