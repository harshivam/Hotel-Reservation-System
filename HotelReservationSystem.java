import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;
public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "895310";
    private static Connection connection;


    public static void main(String[] args) throws ClassNotFoundException ,SQLException{
    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
    }
    catch (ClassNotFoundException e){
        System.out.println(e.getMessage());
    }
    try {
         connection = DriverManager.getConnection(url,username,password);
        while(true){
            System.out.println("\n");
            System.out.println("**********************HOTEL RESERVATION SYSTEM*******************");
            Scanner scanner = new Scanner(System.in);
            System.out.println("1. Reserve a Room");
            System.out.println("2. View Reservations");
            System.out.println("3. Get Room Number");
            System.out.println("4. Update Reservation");
            System.out.println("5. Delete Reservation");
            System.out.println("0. Exit");
            System.out.println("Choose an option: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    reserveRoom(connection, scanner);
                    break;
                case 2:
                    viewReservation(connection);
                    break;
                case 3:
                    getRoomNumber(connection, scanner);
                    break;
                case 4:
                    updateReservation(connection, scanner);
                    break;
                case 5:
                    deleteReservation(connection, scanner);
                    break;
                case 0:
                    exit();
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

        }
    }
    catch (SQLException e){
        System.out.println(e.getMessage());
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }


    }

    private static void reserveRoom(Connection connection,Scanner scanner){
        try {
            System.out.println("Enter guest name: ");
            String guestname = scanner.next();
            scanner.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = scanner.next();
            String sql = "insert into reservations (guest_name,room_number,contact_number)"+
                    "values('"+guestname+"',+"+roomNumber+",'"+contactNumber +"')";
            try(Statement statement  =  connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("Reservation successful");
                }
                else {
                    System.out.println("Reservation failed");
                }


            }catch (SQLException e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
public static void viewReservation(Connection connetion) throws SQLException{
        String sql = "Select reservation_id, guest_name,room_number,contact_number,reservation_date FROM reservations";
        try(Statement statement = connetion.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)){

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

        while(resultSet.next()){
            int reservationId = resultSet.getInt("reservation_id");
            String guestName = resultSet.getString("guest_name");
            int roomNumber = resultSet.getInt("room_number");
            String contactNumber = resultSet.getString("contact_number");
            String reservationDate = resultSet.getTimestamp("reservation_date").toString();


            //foratted display of data
            System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                    reservationId, guestName, roomNumber, contactNumber, reservationDate);
        }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }}
    private static void getRoomNumber(Connection con,Scanner scanner){
        try {
            System.out.println("Enter reservation id: ");
            int reservationId = scanner.nextInt();
            System.out.println("Enter guest name: ");
            String name  = scanner.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId + " " +
                    " AND guest_name = '" + name + "'";


            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){
                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID "+ reservationId +"and Guest "+ name+"is "+roomNumber);
                }
                else{
                    System.out.println("Reservation not found");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
private static  void updateReservation(Connection connection,Scanner scanner){
        try{
            System.out.println("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();
            if(!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for the given ID. ");
                return;
            }
            System.out.println("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = scanner.next();
            String sql = "Update reservations Set guest_name = '" + newGuestName +"',"+
                    "room_number =" + newRoomNumber + ", " +
                    "contact_number = '" + newRoomNumber + "' "+
                    "Where reservation_id= " + reservationId;
            try (Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows>0){
                    System.out.println("Reservation updated successfully");
                }
                else{
                    System.out.println("Reservation update failed");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
}
       public  static void deleteReservation(Connection connection,Scanner scanner){
        try {
            System.out.println("Enter reservation id to delete: ");
            int reservationId = scanner.nextInt();
            if(!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for the given ID. ");
                return;
            }
            String sql = "Delete from reservations where reservation_id = " + reservationId;
            try (Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows>0){
                    System.out.println("Reservation deletion successfully");
                }
                else{
                    System.out.println("Reservation deletion failed");
                }


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
       }
       private static boolean reservationExists(Connection connection,int reservationID){
        try {
            String sql = "Select reservation_id from reservations Where reservation_id= "+ reservationID;
            try (
                Statement statement=connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);){
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
       }
public static void exit() throws InterruptedException{
    System.out.println("Exiting Sysytem");
    int i = 6;
    while (i!=0){
        System.out.print(".");
        Thread.sleep(400);
        i--;
    }

    System.out.println("Thankyou for using hotel reservation System");
}

}