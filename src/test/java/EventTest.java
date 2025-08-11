import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.HashMap;
import java.util.Map;

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

        @Test
        public void 과거_이벤트_생성() throws InterruptedException {

            // 시간선 기준 왼쪽 터치
            touchByCoordinate(timeLineLeftX, timeLineLeftY);

            // 제목, 설명, 체크마트 element 객체 생성
            newEvent = driver.findElement(AppiumBy.accessibilityId("새로운 이벤트"));
            comment = driver.findElement(AppiumBy.xpath("//XCUIElementTypeTextView"));
            check = driver.findElement(AppiumBy.accessibilityId("checkmark"));

            newEvent.click();
            newEvent.sendKeys("과거 이벤트 생성 테스트");

            // 설명 추가
            comment.click();
            comment.sendKeys("과거 이벤트 생성 테스트 - 설명 추가");

            // 오늘 날짜를 기준으로 3일 전 과거 날짜 지정
            String specificPastDate = getSpecificDate(-3);
            String pastDateXpath = "//XCUIElementTypeButton[contains(@name, '" + specificPastDate + "')]";

            startDatePicker = driver.findElement(AppiumBy.xpath("(//XCUIElementTypeButton[@name=\"날짜 선택기\"])[1]"));
            startDatePicker.click();

            WebElement pastStartDate = driver.findElement(AppiumBy.xpath(pastDateXpath));
            pastStartDate.click();

            // 빈 공간 터치로 다음 작업 진행
            touchByCoordinate(eventBlanckX, eventBlankY);

            endDatePicker = driver.findElement(AppiumBy.xpath("(//XCUIElementTypeButton[@name=\"날짜 선택기\"])[2]"));
            endDatePicker.click();

            WebElement pastEndDate = driver.findElement(AppiumBy.xpath(pastDateXpath));
            pastEndDate.click();

            touchByCoordinate(eventBlanckX, eventBlankY);

            check.click();

            // 정상적으로 생성된 경우 3일 전 날짜로 이동해 잘 생성됐는지 확인
            for (int i = 0; i < 3; i++) {
                swipeRight();
            }

            Thread.sleep(2000);
            driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"과거 이벤트 생성 테스트\"]"));
        }

        @Test
        public void 미래_이벤트_생성_및_이모지_테스트() throws InterruptedException {
            int blankX = 376;
            int blankY = 598;

            // 시간선 기준 왼쪽 터치
            touchByCoordinate(timeLineLeftX, timeLineLeftY);

            // 제목, 설명, 체크마트 element 객체 생성
            newEvent = driver.findElement(AppiumBy.accessibilityId("새로운 이벤트"));
            comment = driver.findElement(AppiumBy.xpath("//XCUIElementTypeTextView"));
            check = driver.findElement(AppiumBy.accessibilityId("checkmark"));

            newEvent.click();
            newEvent.sendKeys("미래 이벤트 생성 테스트☁️🎀🌼");

            // 설명 추가
            comment.click();
            comment.sendKeys("미래 이벤트 생성 테스트 - 설명 추가 및 이모지 테스트❤️");

            // 오늘 날짜를 기준으로 3일 전 미래 날짜 지정
            String specificPastDate = getSpecificDate(3);
            String futureDateXpath = "//XCUIElementTypeButton[contains(@name, '" + specificPastDate + "')]";

            startDatePicker = driver.findElement(AppiumBy.xpath("(//XCUIElementTypeButton[@name=\"날짜 선택기\"])[1]"));
            startDatePicker.click();

            WebElement futureStartDate = driver.findElement(AppiumBy.xpath(futureDateXpath));
            futureStartDate.click();

            // 빈 공간 터치로 다음 작업 진행
            touchByCoordinate(eventBlanckX, eventBlankY);

            endDatePicker = driver.findElement(AppiumBy.xpath("(//XCUIElementTypeButton[@name=\"날짜 선택기\"])[2]"));
            endDatePicker.click();

            WebElement futureEndDate = driver.findElement(AppiumBy.xpath(futureDateXpath));
            futureEndDate.click();

            // 빈 공간 터치
            touchByCoordinate(eventBlanckX, eventBlankY);

            // 시간이 같은지 아닌지 비교하고, 종료 시간 늦추기
            String startTime = driver.findElement(AppiumBy.xpath("(//XCUIElementTypeButton[@name=\"시간 선택기\"])[1]")).getAttribute("value");
            WebElement endTimePicker = driver.findElement(AppiumBy.xpath("(//XCUIElementTypeButton[@name=\"시간 선택기\"])[2]"));
            String endTime = endTimePicker.getAttribute("value");

            if (startTime.equals(endTime)) {
                endTimePicker.click();
                String classChain = "**/XCUIElementTypeWindow/XCUIElementTypeOther[4]/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther[2]/XCUIElementTypeDatePicker/XCUIElementTypePicker/XCUIElementTypePickerWheel[3]";
                WebElement endTimeHourWheel = driver.findElement(AppiumBy.iOSClassChain(classChain));
                Map<String, Object> params = new HashMap<>();
                params.put("order", "next");
                params.put("offset", 0.15);
                params.put("element", ((RemoteWebElement) endTimeHourWheel).getId());
                driver.executeScript("mobile: selectPickerWheelValue", params);
            }

            touchByCoordinate(eventBlanckX, eventBlankY);

            check.click();

            // 정상적으로 생성된 경우 3일 전 날짜로 이동해 잘 생성됐는지 확인
            for (int i = 0; i < 3; i++) swipeLeft();

            Thread.sleep(2000);
//            driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"미래 이벤트 생성 테스트☁️🎀🌼\"]"));
        }
    }
}
