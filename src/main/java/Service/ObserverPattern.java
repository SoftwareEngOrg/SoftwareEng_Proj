package Service;
import javafx.beans.Observable;
import java.util.Observer;  //interface (sub)

import static jdk.jfr.internal.periodic.PeriodicEvents.setChanged;

public class ObserverPattern {


}
@SuppressWarnings("deprecation")
abstract class BookInventory implements Observable {
    @SuppressWarnings("deprecation")
    public void bookReturned (String bookTitle)
    {
        System.out.println("Book" + bookTitle + "Returned To In Inventory");

        setChanged();

        notifyObservers(bookTitle);
    }


}