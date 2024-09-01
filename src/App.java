import business.UserController;
import core.Helper;
import entity.User;
import view.DashboardUI;

public class App {
    public static void main(String[] args) {
        Helper.setTheme();
        //LoginUI log = new LoginUI();

        UserController userController = new UserController();
        User user = userController.findByLogin("omerfrkvrl@patika.dev", "123123");
        DashboardUI dashboardUI = new DashboardUI(user);


    }
}
