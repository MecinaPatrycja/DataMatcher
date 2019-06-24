package pl.patrycjamecina;
import pl.patrycjamecina.view.View;
public class App {
    public static void main(String[] args) {
        createAndShowGUI();
    }

    public static void createAndShowGUI() {
        View view = new View();
        view.initView();
    }
}
