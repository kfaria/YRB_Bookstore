import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Start up text:
 * cd Documents/EECS3421/Project2/EECS3421_Project2/
 * source ~db2leduc/cshrc.runtime
 * scp -r com/ kfaria@red.eecs.yorku.ca:/eecs/home/kfaria/Documents/EECS3421/Project2/EECS3421_Project2/src
 *
 * Created by Kenneth Faria - 213846597 on 2017-03-23.
 *
 *
 */

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException, IOException {

        //Main vars
        ArrayList<String> categories = null;
        ArrayList<HashMap> book = null;
        int userCatInput;
        String userCat = "";
        String userTitleInput = "";
        String bookTitle;
        int bookYear;
        int purchaseQnty = 1;
        Map.Entry minimumBookPriceSearch = null;
        double minBookPrice = 0;
        String minBookClub = null;
        Scanner sc = new Scanner(System.in);
        dbInit yrb_bookstore = new dbInit();
        int bookPurchaseInput = 0;
        int CID;


        //CID Lookup
        System.out.print("Enter CID (Integers Only): ");
        CID = sc.nextInt();
        String user = yrb_bookstore.find_customer(CID);

        while(user == null)
        {
            CID = sc.nextInt();
            user = yrb_bookstore.find_customer(CID);
        }
        System.out.println(user);


        //Category print + selection
        if(user!= null)
        {

           categories = yrb_bookstore.fetch_categories();
            System.out.println("\nSelect a category number to search a title through:");
            Iterator catIt = categories.iterator();
            for(int i = 0 ; catIt.hasNext() ; i++)
            {
                System.out.printf("%d: %s\n", i, catIt.next());
            }

            userCatInput = sc.nextInt();

            while(userCatInput < 0 || userCatInput >11)
            {
                System.out.println("Sorry, invalid category input, please try again");
                userCatInput = sc.nextInt();
            }

            switch(userCatInput)
            {
                case 0 : userCat = "children";
                    break;
                case 1 : userCat = "cooking";
                    break;
                case 2 : userCat = "drama";
                    break;
                case 3 : userCat = "guide";
                    break;
                case 4 : userCat = "history";
                    break;
                case 5 : userCat = "horror";
                    break;
                case 6: userCat = "humor";
                    break;
                case 7 : userCat = "mystery";
                    break;
                case 8 : userCat = "phil";
                    break;
                case 9 : userCat = "romance";
                    break;
                case 10 : userCat = "science";
                    break;
                case 11 : userCat = "travel";
                    break;
            }

            System.out.printf("The category '%s' was selected \n", userCat);
        }

        //Book query
        System.out.println("\nPlease enter a book title for a query");

        while(userTitleInput.isEmpty())
        {
            userTitleInput = sc.nextLine();
        }

        book = yrb_bookstore.find_book(userTitleInput, userCat);

        if (book != null)
        {
            Iterator bookHMIt = book.iterator();
            String output = "";

            for(int i = 1 ; bookHMIt.hasNext() ; i++)
            {

                HashMap hmObj = (HashMap) bookHMIt.next();
                Set set = hmObj.entrySet();
                Iterator setIt = set.iterator();
                while (setIt.hasNext()){
                    Map.Entry me = (Map.Entry)setIt.next();
                    output += me.getKey() + ": " + me.getValue() + "\t";
                }
                output += '\n';
                System.out.printf("%d: %s", i, output);
            }
            System.out.println("\nSelect book to purchase by choosing the line number above.");
            bookPurchaseInput = sc.nextInt();

        }


        while(bookPurchaseInput <=  0 || bookPurchaseInput > book.size())
        {
            System.out.println("Sorry, invalid book input, please try again");
            bookPurchaseInput = sc.nextInt();
        }

        bookTitle = (String)book.get(bookPurchaseInput - 1).get("Title");
        bookYear = Integer.parseInt((String)book.get(bookPurchaseInput - 1).get("Year"));

        //Minimum price of book
        minimumBookPriceSearch = yrb_bookstore.min_price(CID, bookTitle, userCat, bookYear);

        if(minimumBookPriceSearch != null)
        {
                minBookPrice = (double) minimumBookPriceSearch.getKey();
                minBookClub = (String) minimumBookPriceSearch.getValue();
        }

        System.out.printf("The cheapest book available costs: $%.2f\n", minBookPrice);

        String decision = null;
        boolean flag = false;
        System.out.print("Would you like to purchase the book? [Y/N]: ");
        decision = sc.next();

        while (!(decision.equals("Y") | decision.equals("y") | decision.equals("N") | decision.equals("n")))
        {
            System.out.println("Inalvid entry. Would you like to purchase the book? [Y/N]: ");
            decision = sc.next();
        }

        if ((decision.equals("Y") | decision.equals("y")))
        {
            System.out.print("How many books do you want?: ");
            purchaseQnty = sc.nextInt();

            while(purchaseQnty < 0 )
            {
                System.out.println("Sorry, invalid input, please try again enter a positive integer");
                purchaseQnty = sc.nextInt();
            }

            System.out.printf("This will cost you $%.2f\n", ( minBookPrice * purchaseQnty));
            System.out.println();

        } 
        else if ((decision.equals("N") | decision.equals("n")))
        {
            System.out.println("Ok goodbye.");
            System.exit(0);
        }

        System.out.print("Are you okay with this? [Y/N]: ");
        decision = sc.next();

        while (!(decision.equals("Y") | decision.equals("y") | decision.equals("N") | decision.equals("n")))
        {
            System.out.println("Invalid entry.");
            System.out.println("Would you like to go ahead with the purchase [Y/N]: ");
            decision = sc.next();
        }

        if ((decision.equals("Y") | decision.equals("y")))
        {
            yrb_bookstore.insert_purchase(CID, minBookClub, bookTitle, bookYear, purchaseQnty);
        } else if ((decision.equals("N") | decision.equals("n"))){
            System.out.println("Ok goodbye.");
            System.exit(0);
        }
    }
}
