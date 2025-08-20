import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static io.appium.java_client.AppiumBy.accessibilityId;
import static io.appium.java_client.AppiumBy.xpath;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllDayEventTest extends TestBase {
    WebElement newEvent;
    WebElement comment;
    WebElement allDay;
    String allDayCountBefore;
    String allDayCountAfter;
    WebElement startDatePicker;
    WebElement endDatePicker;
    WebElement check;

    @Nested
    @DisplayName("올데이_이벤트_생성이_잘_되는지_확인")
    public class AllDayEventCreateTest {

        @Test
        public void 당일_올데이_이벤트_생성() {
            List<WebElement> beforeCreateEventAllNumbers = driver.findElements(
                    AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name MATCHES '\\\\d+'"));

            for (WebElement el : beforeCreateEventAllNumbers) {
                int y = el.getRect().y;
                if (y <= 100) {
                    allDayCountBefore = el.getAttribute("name");
                    break;
                }
            }

            touchTimeLineLeftSpace();

            allDay = driver.findElement(xpath("//XCUIElementTypeButton[@name=\"calendar\"]"));
            allDay.click();

            newEvent = driver.findElement(accessibilityId("새로운 이벤트"));
            newEvent.sendKeys("당일 올데이 이벤트 생성");

            comment = driver.findElement(xpath("//XCUIElementTypeTextView"));
            comment.sendKeys("당일 올데이 이벤트 설명 추가");

            check = driver.findElement(accessibilityId("checkmark"));
            check.click();

            // 올데이 이벤트 생성 검증
            WebElement allDayEventStorage = driver.findElement(xpath("(//XCUIElementTypeImage[@name=\"calendar\"])[1]"));

            allDayEventStorage.click();

            driver.findElement(accessibilityId("당일 올데이 이벤트 생성"));

            List<WebElement> afterCreateEventAllNumbers = driver.findElements(
                    AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name MATCHES '\\\\d+'"));

            for (WebElement el : afterCreateEventAllNumbers) {
                int y = el.getRect().y;
                if (y <= 100) {
                    allDayCountAfter = el.getAttribute("name");
                    break;
                }
            }

            assertEquals(Integer.parseInt(allDayCountAfter), Integer.parseInt(allDayCountBefore) + 1);
        }
    }
}
