import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

public class EventTest extends TestBase {
    @Nested
    @DisplayName("이벤트_생성이_잘_되는지_학인")
    class EventCreateTest {
        WebElement newEvent;
        WebElement comment;
        WebElement startDatePicker;
        WebElement endDatePicker;
        WebElement check;

        int timeLineLeftX = 96;
        int timeLineLeftY = 756;
        int eventBlanckX = 376;
        int eventBlankY = 598;

        @Test
        public void 당일_이벤트_생성() throws InterruptedException {

            // 시간선 기준 왼쪽 터치
            touchByCoordinate(timeLineLeftX, timeLineLeftY);

            newEvent = driver.findElement(AppiumBy.accessibilityId("새로운 이벤트"));
            comment = driver.findElement(AppiumBy.xpath("//XCUIElementTypeTextView"));
            check = driver.findElement(AppiumBy.accessibilityId("checkmark"));

            newEvent.click();
            newEvent.sendKeys("당일 이벤트 생성 테스트");

            // 설명 추가
            comment.click();
            comment.sendKeys("당일 이벤트 생성 테스트 - 설명 추가");

            // 이벤트 등록
            check.click();

            Thread.sleep(2000);

            // 생성된 이벤트가 존재하는지 검증
            driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"당일 이벤트 생성 테스트\"]"));

            // 이벤트 내부 설명 검증
            touchByCoordinate(timeLineLeftX, timeLineLeftY);

            driver.findElement(AppiumBy.xpath("//XCUIElementTypeTextView[@value=\"당일 이벤트 생성 테스트 - 설명 추가\"]"));
        }
    }
}
