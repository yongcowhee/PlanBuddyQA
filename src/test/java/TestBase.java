import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.openqa.selenium.By.xpath;

public class TestBase {
    XCUITestOptions options = new XCUITestOptions();
    static IOSDriver driver;

    static int timeLineLeftX = 96;
    static int timeLineLeftY = 352;
    static int timeLineRightX = 320;
    static int timeLineRightY = 320;
    static int eventBlankX = 376;
    static int eventBlankY = 598;

    @BeforeEach
    public void setup() throws MalformedURLException {
        options.setPlatformName("iOS");
        options.setDeviceName("TestiPhone");
        options.setPlatformVersion("18.6.1");
        options.setAutomationName("XCUITest");
        options.setBundleId("nadoc.planBuddy.dev");
        options.setUdid("00008101-001258A12298001E");
        options.setNewCommandTimeout(Duration.ofMinutes(60));
//        options.setCapability("updatedWDABundleId", "com.heedong.WebDriverAgentRunner");
//        options.setCapability("usePrebuiltWDA",true);
//        options.setCapability("useNewWDA",false);
        driver = new IOSDriver(new URL("http://localhost:4723"), options);
    }

    @AfterEach
    public void clear() {
        if (driver != null) {
            driver.quit();
            System.out.print("드라이버가 성공적으로 종료되었습니다.");
        }
    }

    public static void touchTimeLineLeftSpace() {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), timeLineLeftX, timeLineLeftY));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(tap));
    }

    public static void touchTimeLineRightSpace() {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), timeLineRightX, timeLineRightY));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(tap));
    }

    public static void touchEventBlankSpace() {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), eventBlankX, eventBlankY));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(tap));
    }

    public static String getSpecificDate(int offsetDays) {
        LocalDate date = LocalDate.now().plusDays(offsetDays);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일");
        return date.format(formatter);
    }

    public static void swipeLeft() {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;

        int startX = (int) (width * 0.8);  // 오른쪽에서 시작
        int endX = (int) (width * 0.2);    // 왼쪽에서 끝
        int y = height / 2;                // 화면 세로 중앙

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, y));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), endX, y));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    public static void swipeRight() {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;

        int startX = (int) (width * 0.2);  // 왼쪽에서 시작
        int endX = (int) (width * 0.8);    // 오른쪽에서 끝
        int y = height / 2;                // 화면 세로 중앙

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, y));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), endX, y));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    public static WebElement getStartDatePicker() {
        return driver.findElement(xpath("(//XCUIElementTypeButton[@name=\"날짜 선택기\"])[1]"));
    }

    public static WebElement getEndDatePicker() {
        return driver.findElement(xpath("(//XCUIElementTypeButton[@name=\"날짜 선택기\"])[2]"));
    }
}