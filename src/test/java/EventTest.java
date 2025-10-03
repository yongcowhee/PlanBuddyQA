import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static io.appium.java_client.AppiumBy.*;
import static org.testng.Assert.assertTrue;

public class EventTest extends TestBase {
    WebElement newEvent;
    WebElement comment;
    WebElement startDatePicker;
    WebElement endDatePicker;
    WebElement check;
    WebElement alarm;
    WebElement firstAlarmSelector;
    WebElement secondAlarmSelector;
    WebElement firstAlarm;
    WebElement secondAlarm;

    @Nested
    @DisplayName("이벤트_생성이_잘_되는지_학인")
    class EventCreateTest {
        @Test
        public void 당일_이벤트_생성() throws InterruptedException {

            // 시간선 기준 왼쪽 터치
            touchTimeLineLeftSpace();

            newEvent = driver.findElement(accessibilityId("새로운 일정"));
            comment = driver.findElement(xpath("//XCUIElementTypeTextView"));
            check = driver.findElement(accessibilityId("checkmark"));

            newEvent.click();
            newEvent.sendKeys("당일 이벤트 생성 테스트");

            // 설명 추가
            comment.click();
            comment.sendKeys("당일 이벤트 생성 테스트 - 설명 추가");

            // 이벤트 등록
            check.click();

            Thread.sleep(2000);

            // 생성된 이벤트가 존재하는지 검증
            driver.findElement(xpath("//XCUIElementTypeStaticText[@name=\"당일 이벤트 생성 테스트\"]"));

            // 이벤트 내부 설명 검증
            touchTimeLineLeftSpace();
            driver.findElement(xpath("//XCUIElementTypeTextView[@value=\"당일 이벤트 생성 테스트 - 설명 추가\"]"));
        }

        @Test
        public void 과거_이벤트_생성() throws InterruptedException {

            // 시간선 기준 왼쪽 터치
            touchTimeLineLeftSpace();

            // 제목, 설명, 체크마트 element 객체 생성
            newEvent = driver.findElement(accessibilityId("새로운 일정"));
            comment = driver.findElement(xpath("//XCUIElementTypeTextView"));
            check = driver.findElement(accessibilityId("checkmark"));

            newEvent.click();
            newEvent.sendKeys("과거 이벤트 생성 테스트");

            // 설명 추가
            comment.click();
            comment.sendKeys("과거 이벤트 생성 테스트 - 설명 추가");

            startDatePicker = getStartDatePicker();
            startDatePicker.click();

            // 오늘 날짜를 기준으로 3일 전 과거 날짜 지정
            String specificPastDate = getSpecificDate(-3);
            String pastDateXpath = "//XCUIElementTypeButton[contains(@name, '" + specificPastDate + "')]";
            WebElement pastStartDate = driver.findElement(xpath(pastDateXpath));
            pastStartDate.click();

            // 빈 공간 터치로 다음 작업 진행
            touchEventBlankSpace();

            endDatePicker = getEndDatePicker();
            endDatePicker.click();

            WebElement pastEndDate = driver.findElement(xpath(pastDateXpath));
            pastEndDate.click();

            touchTimeLineLeftSpace();

            check.click();

            // 정상적으로 생성된 경우 3일 전 날짜로 이동해 잘 생성됐는지 확인
            for (int i = 0; i < 3; i++) {
                swipeRight();
            }

            Thread.sleep(2000);
            driver.findElement(xpath("//XCUIElementTypeStaticText[@name=\"과거 이벤트 생성 테스트\"]"));
        }

        @Test
        public void 미래_이벤트_생성_및_이모지_테스트() throws InterruptedException {

            // 시간선 기준 왼쪽 터치
            touchTimeLineLeftSpace();

            // 제목, 설명, 체크마트 element 객체 생성
            newEvent = driver.findElement(accessibilityId("새로운 일정"));
            comment = driver.findElement(xpath("//XCUIElementTypeTextView"));
            check = driver.findElement(accessibilityId("checkmark"));

            newEvent.click();
            newEvent.sendKeys("미래 이벤트 생성 테스트☁️🎀🌼");

            // 설명 추가
            comment.click();
            comment.sendKeys("미래 이벤트 생성 테스트 - 설명 추가 및 이모지 테스트❤️");

            // 오늘 날짜를 기준으로 3일 전 미래 날짜 지정
            String specificFutureDate = getSpecificDate(3);
            String futureDateXpath = "//XCUIElementTypeButton[contains(@name, '" + specificFutureDate + "')]";

            startDatePicker = getStartDatePicker();
            startDatePicker.click();

            WebElement futureStartDate = driver.findElement(xpath(futureDateXpath));
            futureStartDate.click();

            // 빈 공간 터치로 다음 작업 진행
            touchEventBlankSpace();

            endDatePicker = getEndDatePicker();
            endDatePicker.click();

            WebElement futureEndDate = driver.findElement(xpath(futureDateXpath));
            futureEndDate.click();

            // 빈 공간 터치
            touchEventBlankSpace();

            ifStartTimeEqualEndTimeModifyEndTimeHourToOneHourLater();

            check.click();

            // 정상적으로 생성된 경우 3일 전 날짜로 이동해 잘 생성됐는지 확인
            for (int i = 0; i < 3; i++) swipeLeft();

            Thread.sleep(2000);
//            driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"미래 이벤트 생성 테스트☁️🎀🌼\"]"));
        }
    }

    @Nested
    @DisplayName("이벤트_생성시_알람_설정이_잘_되는지_확인")
    class EventAlarmTest {
        @Test
        public void 이벤트_등록시_위쪽_한_개의_알람만_설정() {
            // 시작 시간에, 알람 없음 설정
            enterBasicEventInformation("위쪽 한 개 알람 설정 - 시작 시간에/알람 없음", "알람 테스트");

            alarm = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"알람\"`]"));
            alarm.click();

            ifStartTimeEqualEndTimeModifyEndTimeHourToOneHourLater();

            check.click();

            WebElement findEvent = driver.findElement(accessibilityId("위쪽 한 개 알람 설정 - 시작 시간에/알람 없음"));
            assertTrue(findEvent.isDisplayed());
        }

        @Test
        public void 이벤트_등록시_아래쪽_한_개의_알람만_설정() throws InterruptedException {
            // 알람 없음, 시작 시간에 설정
            enterBasicEventInformation("아래쪽 한 개 알람 설정 - 알람 없음/시작 시간에", "알람 테스트");

            alarm = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"알람\"`]"));
            alarm.click();

            firstAlarmSelector = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"시작 시간에\"`]"));
            secondAlarmSelector = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"없음\"`]"));

            secondAlarmSelector.click();
            secondAlarm = driver.findElement(accessibilityId("5분 전"));
            secondAlarm.click();

            firstAlarmSelector.click();
            firstAlarm = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"없음\"`]"));
            firstAlarm.click();

            check.click();

            WebElement findEvent = driver.findElement(accessibilityId("아래쪽 한 개 알람 설정 - 알람 없음/시작 시간에"));
            findEvent.click();

            Thread.sleep(2000);
        }
    }

    private void enterBasicEventInformation(String eventTitle, String eventComment) {
        touchTimeLineLeftSpace();

        newEvent = driver.findElement(accessibilityId("새로운 일정"));
        comment = driver.findElement(xpath("//XCUIElementTypeTextView"));
        check = driver.findElement(accessibilityId("checkmark"));

        newEvent.click();
        newEvent.sendKeys(eventTitle);

        // 설명 추가
        comment.click();
        comment.sendKeys(eventComment);
    }

    private WebElement findCreatedEvent(String createdEventName) {

        return driver.findElement(accessibilityId(createdEventName));
    }

    private void setAlarm(WebElement alarmSelector, String accessibilityId) {
        alarmSelector.click();

        WebElement choiceAlarm;

        if (accessibilityId.equals("없음")) {
            try {
                choiceAlarm = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"없음\"`][2]"));
            } catch (Exception e) {
                choiceAlarm = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"없음\"`]"));
            }
        } else if (accessibilityId.equals("시작 시간에")) {
            choiceAlarm = driver.findElement(accessibilityId("시작 시간에"));
        } else {
            choiceAlarm = driver.findElement(accessibilityId(accessibilityId));
        }

        choiceAlarm.click();
    }

    private void ifStartTimeEqualEndTimeModifyEndTimeHourToOneHourLater() {
        // 시간이 같은지 아닌지 비교하고, 종료 시간 늦추기
        String startTime = driver.findElement(xpath("(//XCUIElementTypeButton[@name=\"시간 선택기\"])[1]")).getAttribute("value");
        WebElement endTimePicker = driver.findElement(xpath("(//XCUIElementTypeButton[@name=\"시간 선택기\"])[2]"));
        String endTime = endTimePicker.getAttribute("value");

        if (startTime.equals(endTime)) {
            endTimePicker.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement endTimeHourWheel = wait.until(ExpectedConditions.presenceOfElementLocated(xpath("//XCUIElementTypePickerWheel[contains(@value, \"시\")]")));
            Map<String, Object> params = new HashMap<>();
            params.put("elementId", ((RemoteWebElement) endTimeHourWheel).getId());
            params.put("order", "next");
            params.put("offset", 0.15);

            driver.executeScript("mobile: selectPickerWheelValue", params);
        }

        touchEventBlankSpace();
    }
}
