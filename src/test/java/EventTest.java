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
import static org.testng.Assert.assertEquals;
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

        @Test
        public void 이벤트_생성시_두_개의_알람_설정_무작위_무작위() {
            // '시작 시간에', '알람 없음'을 제외한 무작위 / '시작 시간에', '알람 없음'을 제외한 무작위
            enterBasicEventInformation("두 개 알람 설정 - 무작위/무작위", "알람 테스트");

            alarm = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"알람\"`]"));
            alarm.click();

            firstAlarmSelector = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"시작 시간에\"`]"));
            secondAlarmSelector = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"없음\"`]"));

            setAlarm(firstAlarmSelector, "30분 전");
            setAlarm(secondAlarmSelector, "1일 전");

            check.click();

            findCreatedEvent("두 개 알람 설정 - 무작위/무작위");
        }

        @Test
        public void 이벤트_생성시_두_개의_알람_설정_시작시간에_무작위() {
            // 시작시간에 / 시작시간에와 없음을 제외한 무작위
            String eventTitle = "두 개의 알람 설정 - 시작시간에/무작위";
            String comment = "알람 테스트";

            enterBasicEventInformation(eventTitle, comment);

            clickAlarmAndFindAlarmSelector();

            setAlarm(secondAlarmSelector, "2일 전");

            ifStartTimeEqualEndTimeModifyEndTimeHourToOneHourLater();

            check.click();

            WebElement createdEvent = findCreatedEvent(eventTitle);
            String createdEventTitle = createdEvent.getAttribute("name");

            assertTrue(createdEvent.isDisplayed(), "이벤트가 화면에 존재하지 않습니다.");
            assertEquals(eventTitle, createdEventTitle, "이벤트 제목이 입력값과 다릅니다.");

            createdEvent.click();

            // 이벤트 상세 화면이 완전히 로드될 때까지 명시적으로 대기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement alarmSettingOne = wait.until(ExpectedConditions.presenceOfElementLocated(iOSClassChain("**/XCUIElementTypeStaticText[`name == \"시작 시간에\"`]")));
            WebElement alarmSettingTwo = driver.findElement(iOSClassChain("**/XCUIElementTypeStaticText[`name == \"2일 전\"`]"));

            assertEquals(alarmSettingOne.getAttribute("name"), "시작 시간에", "첫 번째 알람 설정 값이 기대와 다릅니다.");
            assertEquals(alarmSettingTwo.getAttribute("name"), "2일 전", "두 번째 알람 설정 값이 기대와 다릅니다.");
        }

        @Test
        public void 이벤트_생성시_두_개의_알람_설정_무작위_시작시간에() {
            String eventTitle = "두 개의 알람 설정 - 무작위/시작 시간에";
            String comment = "알람 테스트";

            // 이벤트 제목 및 설명 텍스트 입력
            enterBasicEventInformation(eventTitle, comment);

            // 알람 클릭 -> 알림 ON, 알람 선택기 1,2 찾기 (클래스 변수)
            clickAlarmAndFindAlarmSelector();

            setAlarm(firstAlarmSelector, "1주 전");
            setAlarm(secondAlarmSelector, "시작 시간에");

            // 이벤트 시작, 종료 시간이 같을 경우 종료 시간을 한 시간 뒤로 조정
            ifStartTimeEqualEndTimeModifyEndTimeHourToOneHourLater();

            check.click();

            WebElement createdEvent = findCreatedEvent(eventTitle);
            String createdEventTitle = createdEvent.getAttribute("name");

            assertTrue(createdEvent.isDisplayed());
            assertEquals(createdEvent.getAttribute("name"), createdEventTitle);

            createdEvent.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement alarmSettingOne = wait.until(ExpectedConditions.presenceOfElementLocated(iOSNsPredicateString("name == \"시작 시간에\" AND label == \"시작 시간에\" AND value == \"시작 시간에\"")));
            WebElement alarmSettingTwo = driver.findElement(iOSNsPredicateString("name == \"1주 전\" AND label == \"1주 전\" AND value == \"1주 전\""));


            // 생성된 알람이 요구사항과 같은지 확인, 시작 시간에가 위로 올라가는 UI 설정이 존재해 순서는 바꿔서 테스트
            assertEquals(alarmSettingOne.getAttribute("name"), "시작 시간에");
            assertEquals(alarmSettingTwo.getAttribute("name"), "1주 전");
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

    private void clickAlarmAndFindAlarmSelector() {
        alarm = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"알람\"`]"));
        alarm.click();

        firstAlarmSelector = driver.findElement(iOSNsPredicateString("name == \"시작 시간에\" AND label == \"시작 시간에\" AND value == \"시작 시간에\""));
        secondAlarmSelector = driver.findElement(iOSNsPredicateString("name == \"없음\" AND label == \"없음\" AND value == \"없음\""));
    }
}
