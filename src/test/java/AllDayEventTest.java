import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static io.appium.java_client.AppiumBy.accessibilityId;
import static io.appium.java_client.AppiumBy.xpath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
            allDayCountBefore = getCurAllDayCount();

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

            allDayCountAfter = getCurAllDayCount();

            assertEquals(Integer.parseInt(allDayCountAfter), Integer.parseInt(allDayCountBefore) + 1);
        }

        @Test
        public void 과거_올데이_이벤트_생성() {
            // 과거 일자로 이동해 기존 올데이 이벤트 개수 파악 및 저장
            for (int i = 0; i < 3; i++) {
                swipeRight();
            }

            allDayCountBefore = getCurAllDayCount();

            // 다시 현재로 복귀
            for (int i = 0; i < 3; i++) {
                swipeLeft();
            }

            touchTimeLineLeftSpace();

            WebElement allDay = driver.findElement(xpath("//XCUIElementTypeButton[@name=\"calendar\"]"));
            allDay.click();

            newEvent = driver.findElement(accessibilityId("새로운 이벤트"));
            newEvent.sendKeys("과거 올데이 이벤트 생성");

            comment = driver.findElement(xpath("//XCUIElementTypeTextView"));
            comment.sendKeys("과거 올데이 이벤트 설명 추가");

            // 과거 일자로 조정
            String specificPastDate = getSpecificDate(-3);
            startDatePicker = getStartDatePicker();
            startDatePicker.click();

            String pastDateXpath = "//XCUIElementTypeButton[contains(@name, '" + specificPastDate + "')]";
            WebElement pastDate = driver.findElement(xpath(pastDateXpath));

            pastDate.click();

            touchEventBlankSpace();

            endDatePicker = getEndDatePicker();
            endDatePicker.click();

            WebElement pastEndDate = driver.findElement(xpath(pastDateXpath));
            pastEndDate.click();

            touchEventBlankSpace();

            check = driver.findElement(accessibilityId("checkmark"));
            check.click();

            // 과거 위치로 이동
            for (int i = 0; i < 3; i++) {
                swipeRight();
            }

            // 올데이 이벤트 생성 검증
            WebElement allDayEventStorage = driver.findElement(xpath("(//XCUIElementTypeImage[@name=\"calendar\"])[1]"));
            allDayEventStorage.click();

            driver.findElement(accessibilityId("과거 올데이 이벤트 생성"));

            allDayCountAfter = getCurAllDayCount();

            assertEquals(Integer.parseInt(allDayCountAfter), Integer.parseInt(allDayCountBefore) + 1);
        }

        @Test
        public void 미래_올데이_이벤트_생성() {
            // 미래 일자로 이동해 기존 올데이 이벤트 개수 파악 및 저장
            for (int i = 0; i < 3; i++) {
                swipeLeft();
            }

            allDayCountBefore = getCurAllDayCount();

            // 다시 현재로 복귀
            for (int i = 0; i < 3; i++) {
                swipeRight();
            }

            touchTimeLineLeftSpace();

            WebElement allDay = driver.findElement(xpath("//XCUIElementTypeButton[@name=\"calendar\"]"));
            allDay.click();

            newEvent = driver.findElement(accessibilityId("새로운 이벤트"));
            newEvent.sendKeys("미래 올데이 이벤트 생성");

            comment = driver.findElement(xpath("//XCUIElementTypeTextView"));
            comment.sendKeys("미래 올데이 이벤트 설명 추가");

            // 과거 일자로 조정
            String specificFutureDate = getSpecificDate(3);
            startDatePicker = getStartDatePicker();
            startDatePicker.click();

            String futureDateXpath = "//XCUIElementTypeButton[contains(@name, '" + specificFutureDate + "')]";
            WebElement futureDate = driver.findElement(xpath(futureDateXpath));

            futureDate.click();

            touchEventBlankSpace();

            endDatePicker = getEndDatePicker();
            endDatePicker.click();

            WebElement futureEndDate = driver.findElement(xpath(futureDateXpath));
            futureEndDate.click();

            touchEventBlankSpace();

            check = driver.findElement(accessibilityId("checkmark"));
            check.click();

            // 미래 위치로 이동
            for (int i = 0; i < 3; i++) {
                swipeLeft();
            }

            // 올데이 이벤트 생성 검증
            WebElement allDayEventStorage = driver.findElement(xpath("(//XCUIElementTypeImage[@name=\"calendar\"])[1]"));
            allDayEventStorage.click();

            driver.findElement(accessibilityId("미래 올데이 이벤트 생성"));

            allDayCountAfter = getCurAllDayCount();

            assertEquals(Integer.parseInt(allDayCountAfter), Integer.parseInt(allDayCountBefore) + 1);
        }
    }

    @Test
    public void 올데이_이벤트_시작_날짜와_종료_날짜가_역전되는_경우() {
        touchTimeLineLeftSpace();

        allDay = driver.findElement(xpath("//XCUIElementTypeButton[@name=\"calendar\"]"));
        allDay.click();

        endDatePicker = getEndDatePicker();
        endDatePicker.click();

        // 현재 날짜로부터 3일 전 날짜 계산 (과거)
        String specificPastDate = getSpecificDate(-3);

        String pastDateXpath = "//XCUIElementTypeButton[contains(@name, '" + specificPastDate + "')]";
        WebElement pastDate = driver.findElement(xpath(pastDateXpath));

        assertFalse(pastDate.isEnabled());
    }

    private String getCurAllDayCount() {
        List<WebElement> eventAllNumbers = driver.findElements(
                AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name MATCHES '\\\\d+'"));

        String curAllDayCount = "";

        for (WebElement el : eventAllNumbers) {
            int y = el.getRect().y;
            if (y <= 100) {
                curAllDayCount = el.getAttribute("name");
                return curAllDayCount;
            }
        }
        return curAllDayCount;
    }
}
