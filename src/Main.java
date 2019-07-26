import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
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
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Main extends Application {
    static String adurl;
    static int tr = 0;
    static String companyname;
    static String jobdescription;
    static String opendate;
    static String cloasedate;
    static List <WebElement>webElements;

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
    WebDriver adviewdriver = new ChromeDriver();
   public static BufferedImage capture;


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


        String png = encodeToString(capture, "png");

//        System.out.println("stream is"+png);
        PreparedStatement statement = Dbcon.getCon().prepareStatement("INSERT INTO advertisements (comany, description, opendate, closedate, adurl, adimage) VALUES (?,?,?,?,?,?)");
        statement.setString(1, companyname);
        statement.setString(2, jobdescription);
        statement.setString(3, opendate);
        statement.setString(4, cloasedate);
        statement.setString(5, adurl);
        statement.setString(6, png);

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

        driver.navigate().to("http://topjobs.lk/applicant/vacancybyfunctionalarea.jsp;jsessionid=qjMQRXM+hy5-GbRR3VecA+Qi?FA=SDQ");

        File screenshotAs = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        imgscreenshot.setImage(new Image(screenshotAs.toURI().toString()));
        lblurl.setText("http://topjobs.lk/applicant/vacancybyfunctionalarea.jsp;jsessionid=qjMQRXM+hy5-GbRR3VecA+Qi?FA=SDQ");
        webElements = getWebElement();
        tr=0;
        return;
    }



    public void gotonextad(ActionEvent actionEvent) throws Exception {


        find_details();
        String readadimage = readadimage();
        if (readadimage==null){
            capturescreen();
        }else {
            URL imgurl=new URL(readadimage);
            capture=ImageIO.read(imgurl);
            imgscreenshot.setImage(SwingFXUtils.toFXImage(capture,null));
        }
        lblurl.setText(adurl);
        lblcompanyname.setText(companyname);
        lbldescription.setText(jobdescription);
        lblopendate.setText(opendate);
        lblclosingdate.setText(cloasedate);
    }

    List getWebElement() throws Exception {
        WebElement a = driver.findElement(By.xpath("//table[@id='table']/tbody"));
        final List<WebElement> Elements = a.findElements(By.tagName("tr"));
        return Elements;
    }

    void capturescreen(){
        Screenshot shot=new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1)).takeScreenshot(adviewdriver);
        capture=shot.getImage();
        imgscreenshot.setImage(SwingFXUtils.toFXImage(capture,null));
    }

    void find_details() {
        String link = webElements.get(tr).findElement((By.tagName("a"))).getAttribute("href");
        String[] getopendate = webElements.get(tr).findElement(By.className("add_startdate")).getText().split(":", 2);
        String[] getclosedate = webElements.get(tr).findElement(By.className("add_enddate")).getText().split(":", 2);
        opendate = getopendate[1].trim();
        cloasedate = getclosedate[1].trim();
        companyname = webElements.get(tr).findElement(By.tagName("h1")).getText().trim();
        jobdescription = webElements.get(tr).findElement(By.tagName("a")).getText().trim();
        String[] arrOfStr = link.split("/", 2);
        String x = arrOfStr[1];
        String[] y = x.split(".jsp", 2);
        String correcturl = y[0];
        tr += 1;
        adurl = "http://topjobs.lk/" + "" + correcturl + "" + ".jsp";
        adviewdriver.navigate().to(adurl);
    }

    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

//            BASE64Encoder encoder = new BASE64Encoder();
//            imageString = encoder.encode(imageBytes);
            imageString= java.util.Base64.getEncoder().encodeToString(imageBytes);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    String readadimage() throws Exception{
        WebElement element = adviewdriver.findElement(By.className("shrunk-image"));
        String src = element.getAttribute("src");
         if (src.contains("Apply")){
             src=null;
         }
        System.out.println(src);
        return src;
    }

}
