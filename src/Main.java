import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main extends Application {
    static String currentUrl;
    static FileInputStream inputStream;
    static File screenshot;
    static String adurl;
    static int tr = 0;
    static String companyname;
    static String jobdescription;
    static String opendate;
    static String cloasedate;
    public ImageView imgscreenshot;
    public Label lblurl;
    public Button btnsave;
    public Button btngeturl;
    public Button btnnextad;
    public Label lblcompanyname;
    public Label lbldescription;
    public Label lblopendate;
    public Label lblclosingdate;
    WebDriver driver = new ChromeDriver();

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "D:/wixis/work_02/lib/chromedriver.exe");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(this.getClass().getResource("/View.fxml"));
        Scene sc = new Scene(root);
        primaryStage.setScene(sc);
        primaryStage.show();
    }

    public void saveimg(ActionEvent actionEvent) throws SQLException, IOException {

//        URI uri = new URI(currentUrl);
//        String host = uri.getHost();
        // FileUtils.copyFile(screenshot,new File("D:/wixis/work_02/captureimg/" + ""+host+""+""+tr+""+".png"));

        InputStream stream = new FileInputStream(screenshot);
        PreparedStatement statement = Dbcon.getCon().prepareStatement("INSERT INTO advertisements (comany, description, opendate, closedate, adurl, adimage) VALUES (?,?,?,?,?,?)");
        statement.setString(1, companyname);
        statement.setString(2, jobdescription);
        statement.setString(3, opendate);
        statement.setString(4, cloasedate);
        statement.setString(5, adurl);
        statement.setBlob(6, stream);

        int i = statement.executeUpdate();
        if (i > 0) {
            new Alert(Alert.AlertType.INFORMATION, "Successfully Saved", ButtonType.OK).show();
            lblurl.setText("");
            lblcompanyname.setText("");
            lbldescription.setText("");
            lblopendate.setText("");
            lblclosingdate.setText("");
            imgscreenshot.setImage(null);
            return;
        } else {
            new Alert(Alert.AlertType.ERROR, "Unable to Save", ButtonType.OK).show();
            return;
        }
    }

    public void geturllink(ActionEvent actionEvent) throws Exception {
        mainconfiguration();
        lblurl.setText(currentUrl);
        screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Image image = new Image(screenshot.toURI().toString());
        imgscreenshot.setImage(image);
        return;
    }


    public void gotonextad(ActionEvent actionEvent) throws Exception {

        mainconfiguration();
        find_details();
        screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Image image = new Image(screenshot.toURI().toString());
        imgscreenshot.setImage(image);
        lblurl.setText(adurl);
        lblcompanyname.setText(companyname);
        lbldescription.setText(jobdescription);
        lblopendate.setText(opendate);
        lblclosingdate.setText(cloasedate);
    }

    void mainconfiguration() throws Exception {
        driver.navigate().to("http://topjobs.lk/applicant/vacancybyfunctionalarea.jsp?FA=SDQ&jst=OPEN");
        currentUrl = driver.getCurrentUrl();

    }

    void find_details() {
        WebElement element = driver.findElement(By.id("tr" + "" + tr + ""));
        String link = element.findElement((By.tagName("a"))).getAttribute("href");
        String[] getopendate = element.findElement(By.className("add_startdate")).getText().split(":", 2);
        String[] getclosedate = element.findElement(By.className("add_enddate")).getText().split(":", 2);
        opendate = getopendate[1].trim();
        cloasedate = getclosedate[1].trim();
        companyname = element.findElement(By.tagName("h1")).getText().trim();
        jobdescription = element.findElement(By.tagName("a")).getText().trim();
        String[] arrOfStr = link.split("/", 2);
        String x = arrOfStr[1];
        String[] y = x.split(".jsp", 2);
        String correcturl = y[0];
        tr += 1;
        adurl = "http://topjobs.lk/" + "" + correcturl + "" + ".jsp";
        driver.navigate().to(adurl);
    }

}
