import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.HashMap;
import java.util.Map;

import static io.appium.java_client.AppiumBy.*;

public class EventTest extends TestBase {
    @Nested
    @DisplayName("이벤트_생성이_잘_되는지_학인")
    class EventCreateTest {
        WebElement newEvent;
        WebElement comment;
        WebElement startDatePicker;
        WebElement endDatePicker;
        WebElement check;

        @Test
        public void 당일_이벤트_생성() throws InterruptedException {

            // 시간선 기준 왼쪽 터치
            touchTimeLineLeftSpace();

            newEvent = driver.findElement(accessibilityId("새로운 이벤트"));
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
            newEvent = driver.findElement(accessibilityId("새로운 이벤트"));
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
            newEvent = driver.findElement(accessibilityId("새로운 이벤트"));
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

            // 시간이 같은지 아닌지 비교하고, 종료 시간 늦추기
            String startTime = driver.findElement(xpath("(//XCUIElementTypeButton[@name=\"시간 선택기\"])[1]")).getAttribute("value");
            WebElement endTimePicker = driver.findElement(xpath("(//XCUIElementTypeButton[@name=\"시간 선택기\"])[2]"));
            String endTime = endTimePicker.getAttribute("value");

            if (startTime.equals(endTime)) {
                endTimePicker.click();
                String classChain = "**/XCUIElementTypeWindow/XCUIElementTypeOther[4]/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther[2]/XCUIElementTypeDatePicker/XCUIElementTypePicker/XCUIElementTypePickerWheel[3]";
                WebElement endTimeHourWheel = driver.findElement(iOSClassChain(classChain));
                Map<String, Object> params = new HashMap<>();
                params.put("order", "next");
                params.put("offset", 0.15);
                params.put("element", ((RemoteWebElement) endTimeHourWheel).getId());
                driver.executeScript("mobile: selectPickerWheelValue", params);
            }

            touchTimeLineLeftSpace();

            check.click();

            // 정상적으로 생성된 경우 3일 전 날짜로 이동해 잘 생성됐는지 확인
            for (int i = 0; i < 3; i++) swipeLeft();

            Thread.sleep(2000);
//            driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"미래 이벤트 생성 테스트☁️🎀🌼\"]"));
        }
    }
}
