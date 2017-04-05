import java.sql.*;
import java.util.*;


/**
 * Created by Kenneth Faria - 213846597 on 2017-03-23.
 */

public class dbInit {

    private String userInformation;
    private Connection conDB;
    private ArrayList<String> cat = new ArrayList<String>();
    private ArrayList<HashMap> book_test = new ArrayList<HashMap>();
    private TreeMap<Double, String> priceLookup = new TreeMap<>();
    private String sql;

    public dbInit() throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
        String url = "jdbc:db2:c3421m";
        conDB = DriverManager.getConnection(url);
    }

    public String find_customer(int CID) throws SQLException {

        sql = "SELECT cid AS #cid, name AS #name, city AS #city FROM YRB_CUSTOMER WHERE cid = " + CID;
        PreparedStatement queryBody = null;
        queryBody = conDB.prepareStatement(sql);
        ResultSet res = null;
        String response ="";
            res = queryBody.executeQuery();

        try {
            res.next();
            response = res.getString("#cid") + "\t" + res.getString("#name") +
                    "\t"  + res.getString("#city");
        } catch (SQLException e) {
            System.out.println("Sorry user not found! Please enter another CID.");
            response = null;
        }
        return response;
    }

    public ArrayList<String> fetch_categories() throws SQLException {

        sql = "SELECT cat AS #cat FROM YRB_CATEGORY";
        PreparedStatement queryBody = null;
        queryBody = conDB.prepareStatement(sql);
        ResultSet res = null;
        int i = 0;
        res = queryBody.executeQuery();

        try{
            while(res.next()){
                cat.add(res.getString("#cat"));
            }

        } catch(SQLException e) {
            System.out.println("There was an error with the category query");
        }
        return cat;
    }

    public ArrayList<HashMap> find_book(String bookTitle, String category) throws SQLException {
        bookTitle = bookTitle.replaceAll("'", "''");

        sql = "SELECT title AS #title, year AS #year, language AS #language, weight AS #weight FROM YRB_BOOK"
                + " WHERE cat = '" + category
                + "' AND title = '" + bookTitle
                + "'";
        PreparedStatement queryBody = conDB.prepareStatement(sql);
        ResultSet res = null;
        res = queryBody.executeQuery();

        try{
            boolean test = res.next();
            if(!test){
                System.out.println("Book does not exist within this category, please enter a new book title");
                bookTitle = new Scanner(System.in).nextLine();
                find_book(bookTitle, category);
            }

            while(test){
                HashMap<String, String> bookInfo = new HashMap<>();
                bookInfo.put("Title", res.getString("#title"));
                bookInfo.put("Year", res.getString("#year"));
                bookInfo.put("Language", res.getString("#language"));
                bookInfo.put("Weight", res.getString("#weight") );
                book_test.add(bookInfo);
                test = res.next();
            }

        } catch(SQLException e){
            System.out.printf("There was an error with the find book query");
        }
        return book_test;
    }

    public Map.Entry<Double, String> min_price(int CID, String bookTitle, String category, int year) throws SQLException {
        bookTitle = bookTitle.replaceAll("'", "''");
        ArrayList<String> cid_club = find_club(CID);
        for(int i = 0 ; i < cid_club.size(); i++){
            sql = "SELECT * FROM YRB_OFFER" +
                    " WHERE title='" + bookTitle +"'" +
                    " AND club='" + cid_club.get(i) + "'" +
                    " AND year=" + year;
            PreparedStatement queryBody = conDB.prepareStatement(sql);
            ResultSet res = queryBody.executeQuery();

            try {
                while(res.next()){
                    priceLookup.put(res.getDouble("price"), res.getString("club"));
                }
            } catch (SQLException e){
                System.out.println("There was a problem with the min_price query");
            }
        }
    return priceLookup.firstEntry();
    }

    public void insert_purchase(int CID, String club, String bookTitle, int year, int qnty) throws SQLException {
        bookTitle = bookTitle.replaceAll("'", "''");
        String date = String.valueOf(new Timestamp(System.currentTimeMillis()));
        sql = "INSERT INTO YRB_PURCHASE VALUES(" +
                "'" + CID + "', " +
                "'" + club + "', " +
                "'" + bookTitle + "', " +
                "'" + year + "', " +
                "'" + date + "', " +
                "'" + qnty + "'" +
                ")";
        Statement update = conDB.createStatement();
        try {
            update.executeUpdate(sql);
            System.out.println("Purchase successful! ");

        } catch (SQLException e) {
            System.out.println("There was an error making the purchase");
            e.printStackTrace();
        }
    }

    private ArrayList<String> find_club(int CID) throws SQLException {

        ArrayList<String> cid_club = new ArrayList<String>();

        sql = "SELECT club AS #club FROM YRB_MEMBER where cid = " + CID;
        PreparedStatement queryBody = null;
        queryBody = conDB.prepareStatement(sql);
        ResultSet res = null;
        res = queryBody.executeQuery();

        try {
            while(res.next()){
                cid_club.add(res.getString("#club"));
            }

        } catch (SQLException e){
            System.out.println("There was an error with the find club query");
        }
        return cid_club;
    }

}