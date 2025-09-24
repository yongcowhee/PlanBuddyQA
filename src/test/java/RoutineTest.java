import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.appium.java_client.AppiumBy.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RoutineTest extends TestBase {
    WebElement routineTab;
    WebElement createRoutineButton;
    WebElement newRoutine;
    WebElement addRoutineButton;
    WebElement newToDo;
    WebElement checkmark;
    int findRoutineTotalHour;
    int findRoutineTotalMinute;
    String expectedRoutineTotalTime;
    WebElement addToDoButtonInRoutine;

    @Nested
    @DisplayName("시간 조절 없이 루틴 등록 테스트")
    class CreateRoutineTest {
        @Test
        public void 루틴_액션_할일_모두_입력() {
            String routineTitle = "루틴 생성 테스트 - 루틴 이름과 할일 이름 모두 등록";
            String toDoTitle = "루틴 생성 시 할일 이름이 있을 경우";

            enterBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

            checkmark = driver.findElement(accessibilityId("checkmark"));
            // 할 일 생성
            checkmark.click();
            // 루틴 생성
            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createdRoutine = driver.findElement(accessibilityId(routineTitle));
            assertEquals(createdRoutine.getAttribute("name"), routineTitle);

            createdRoutine.click();
            WebElement createdTodo = driver.findElement(accessibilityId(toDoTitle));
            assertEquals(createdTodo.getAttribute("name"), toDoTitle);
        }

        @Test
        public void 루틴_이름만_입력() {
            String routineTitle = "루틴 생성 테스트 - 루틴 이름만 입력";

            enterBasicInformationRoutineAndToDo(routineTitle, "");

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement notEnterToDoTitleErrorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("새로운 할 일의 이름을 입력해주세요")));

            assertTrue(notEnterToDoTitleErrorMessage.isDisplayed());
        }

        @Test
        public void 할일_이름만_입력() {
            String toDoTitle = "루틴 생성 테스트 - 할일 이름만 입력";

            enterBasicInformationRoutineAndToDo("", toDoTitle);

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement notEnterRoutineTitleErrorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("루틴 이름을 입력해주세요.")));

            assertTrue(notEnterRoutineTitleErrorMessage.isDisplayed());
        }

        @Test
        public void 루틴_이름_액션_이름_모두_입력X() {
            enterBasicInformationRoutineAndToDo("", "");

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement notEnterToDoTitleErrorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("새로운 할 일의 이름을 입력해주세요")));

            assertTrue(notEnterToDoTitleErrorMessage.isDisplayed());
            newToDo.sendKeys("루틴 에러 메시지 확인을 위한 할 일 이름 입력");

            checkmark.click();

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement notEnterRoutineTitleErrorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("루틴 이름을 입력해주세요.")));

            assertTrue(notEnterRoutineTitleErrorMessage.isDisplayed());
        }
    }

    @Test
    public void 루틴_이름_수정() {
        String expectTitle = "루틴 이름 수정";

        createRoutineForModifyAndRemoveRoutine();

        WebElement routine = driver.findElement(accessibilityId("루틴"));
        routine.click();

        WebElement routineTitle = driver.findElement(accessibilityId("루틴"));
        routineTitle.click();

        WebElement routineTitleField = driver.findElement(iOSClassChain("**/XCUIElementTypeTextField"));
        routineTitleField.click();
        routineTitleField.clear();
        routineTitleField.sendKeys(expectTitle);

        checkmark = driver.findElement(accessibilityId("checkmark"));
        checkmark.click();

        WebElement modifiedRoutineTitle = driver.findElement(accessibilityId(expectTitle));
        assertEquals(modifiedRoutineTitle.getAttribute("name"), expectTitle);

        WebElement backButton = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"chevron.left\"`]"));
        backButton.click();

        modifiedRoutineTitle = driver.findElement(accessibilityId(expectTitle));
        assertEquals(modifiedRoutineTitle.getAttribute("name"), expectTitle);
    }

    @Test
    public void 루틴_삭제() {
        // 기등록된 루틴이 있는 경우에만 삭제 가능하므로 루틴 등록 동작 실행
        createRoutineForModifyAndRemoveRoutine();

        WebElement routine = driver.findElement(accessibilityId("루틴"));
        routine.click();

        WebElement removeButton = driver.findElement(accessibilityId("trash"));
        removeButton.click();

        WebElement removeConfirmButton = driver.findElement(accessibilityId("삭제"));
        removeConfirmButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean isElementDisappeared;
        try {
            isElementDisappeared = wait.until(ExpectedConditions.invisibilityOfElementLocated(accessibilityId("루틴")));
            System.out.println("요소가 성공적으로 삭제되었습니다.");
        } catch (TimeoutException e) {
            System.out.println("요소가 지정된 시간 내에 사라지지 않았습니다. 삭제 실패.");
            isElementDisappeared = false;
        }

        assertTrue(isElementDisappeared, "삭제하려는 요소가 화면에 여전히 존재합니다.");
    }

    @Nested
    @DisplayName("할 일 생성 테스트 - 루틴 생성과 동시에 이뤄지는 경우")
    class CreateToDoTest {
        @Test
        public void 액션_타이머_시간을_0시간_0분으로_설정() {
            String routineTitle = "루틴 생성과 동시에 할일 생성";
            String toDoTitle = "액션(할일) 둥록 테스트";

            enterBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

            // 0시간 0분 액션 생성
            regulateToDoHourPickerWheel(0);
            regulateToDoMinutePickerWheel(0);

            // 액션 등록
            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement toDoSettingZeroMinuteError = driver.findElement(accessibilityId("최소 실행 시간은 1분입니다"));
            Assert.assertTrue(toDoSettingZeroMinuteError.isDisplayed());
        }

        @Test
        public void 액션_타이머_시간을_23시_59분으로_설정() {
            String routineTitle = "루틴 생성과 동시에 할일 생성";
            String toDoTitle = "액션(할일) 둥록 테스트";

            enterBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

            // 23시간 59분 액션 생성
            regulateToDoHourPickerWheel(23);
            regulateToDoMinutePickerWheel(59);

            // 액션 등록
            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            // 루틴 등록
            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createdRoutine = driver.findElement(accessibilityId(routineTitle));
            assertEquals(createdRoutine.getAttribute("name"), routineTitle);

            createdRoutine.click();

            // 루틴 내 등록된 액션의 전체 시간 합 계산
            findAllToDoInRoutineAndCalculateTotalTime();

            convertExpectedRoutineTotalTimeToString();

            WebElement realRoutineTotalTime = driver.findElement(iOSClassChain("**/XCUIElementTypeScrollView/**/XCUIElementTypeStaticText[`name CONTAINS '시간' OR name CONTAINS '분'`]"));
            assertEquals(realRoutineTotalTime.getAttribute("name"), expectedRoutineTotalTime);
        }

        @Test
        public void 액션_타이머_시간을_임의로_설정() {
            String routineTitle = "루틴 생성과 동시에 할 일 생성";
            String toDoTitle = "할 일 타이머 시간 임의로 설정";

            enterBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

            regulateToDoHourPickerWheel(3);
            regulateToDoMinutePickerWheel(15);

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createdRoutine = driver.findElement(accessibilityId(routineTitle));
            assertEquals(createdRoutine.getAttribute("name"), routineTitle);

            createdRoutine.click();

            WebElement createdToDo = driver.findElement(accessibilityId(toDoTitle));
            assertEquals(createdToDo.getAttribute("name"), toDoTitle);

            findAllToDoInRoutineAndCalculateTotalTime();

            convertExpectedRoutineTotalTimeToString();

            // 전체 할 일 합산 시간 요소
            WebElement realRoutineTotalTime = driver.findElement(iOSClassChain("**/XCUIElementTypeScrollView/**/XCUIElementTypeStaticText[`name CONTAINS '시간' OR name CONTAINS '분'`]"));
            // 실제 합산 값과 비교
            assertEquals(realRoutineTotalTime.getAttribute("name"), expectedRoutineTotalTime);
        }

        @Test
        public void 이미_등록된_루틴에서_액션_추가_등록_0시간_0분() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "이미 등록된 루틴에서 액션 추가 등록 - 0시간 0분";

            addToDoInRoutine(routineTitle, toDoTitle);

            regulateToDoHourPickerWheel(0);
            regulateToDoMinutePickerWheel(0);

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createToDoErrorMessage = driver.findElement(accessibilityId("최소 실행 시간은 1분입니다"));
            assertTrue(createToDoErrorMessage.isDisplayed());
        }

        @Test
        public void 이미_등록된_루틴에서_액션_추가_등록_무작위_시간() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "이미 등록된 루틴 내 액션 추가 등록 - 무작위 시간";
            int expectedHour = 1;
            int expectedMinute = 18;

            addToDoInRoutine(routineTitle, toDoTitle);

            regulateToDoHourPickerWheel(expectedHour);
            regulateToDoMinutePickerWheel(expectedMinute);

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createdToDo = driver.findElement(accessibilityId(toDoTitle));
            assertTrue(createdToDo.isDisplayed());

            WebElement createdToDoTime = driver.findElement(
                    xpath("//XCUIElementTypeStaticText[@name='" + toDoTitle + "']/following-sibling::XCUIElementTypeStaticText"));

            if (expectedHour != 0 && expectedMinute != 0) {
                assertEquals(createdToDoTime.getAttribute("name"), expectedHour + "시간 " + expectedMinute + "분");
            } else if (expectedHour != 0 && expectedMinute == 0) {
                assertEquals(createdToDoTime.getAttribute("name"), expectedHour + "시간");
            } else {
                assertEquals(createdToDoTime.getAttribute("name"), expectedMinute + "분");
            }

            findAllToDoInRoutineAndCalculateTotalTime();

            convertExpectedRoutineTotalTimeToString();

            WebElement realRoutineTotalTime = driver.findElement(iOSClassChain("**/XCUIElementTypeScrollView/**/XCUIElementTypeStaticText[`name CONTAINS '시간' OR name CONTAINS '분'`]"));
            assertEquals(realRoutineTotalTime.getAttribute("name"), expectedRoutineTotalTime);
        }

        @Test
        public void 이미_등록된_루틴에서_액션_추가_등록_액션_이름X() {
            String routineTitle = "미리 등록된 루틴";

            addToDoInRoutine(routineTitle, "");

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createToDoErrorMessage = driver.findElement(accessibilityId("새로운 할 일의 이름을 입력해주세요"));
            assertTrue(createToDoErrorMessage.isDisplayed());
        }
    }

    private void findAllToDoInRoutineAndCalculateTotalTime() {
        List<WebElement> toDoList = driver.findElements(iOSClassChain("**/XCUIElementTypeCollectionView/**/XCUIElementTypeCell/**/XCUIElementTypeStaticText"));
        int index = 0;
        for (WebElement el : toDoList) {
            // 인덱스는 0부터 시작하고 모든 시간 값은 홀수번째에 위치
            if (index % 2 == 1) {
                String totalTime = el.getAttribute("name");
                String[] splitTotalTime = totalTime.split(" ");

                // 시간 혹은 분 만으로 이뤄진 경우 (ex. 1시간 or 30분)
                if (splitTotalTime.length == 1) {
                    if (splitTotalTime[0].contains("시간")) {
                        // 시간 String 제외 후 합산
                        findRoutineTotalHour += Integer.parseInt(splitTotalTime[0].substring(0, splitTotalTime[0].length() - 2));
                    } else {
                        // 분 String 제외 후 합산
                        findRoutineTotalMinute += Integer.parseInt(splitTotalTime[0].substring(0, splitTotalTime[0].length() - 1));
                    }
                } else { // 2시간 30분 처럼 시간과 분으로 이뤄진 경우 (length == 2)
                    findRoutineTotalHour += Integer.parseInt(splitTotalTime[0].substring(0, splitTotalTime[0].length() - 2));
                    findRoutineTotalMinute += Integer.parseInt(splitTotalTime[1].substring(0, splitTotalTime[1].length() - 1));
                }
            }

            index++;
        }

        if (findRoutineTotalMinute > 59) {
            int quotient = findRoutineTotalMinute / 60;
            findRoutineTotalHour += quotient;
            findRoutineTotalMinute = findRoutineTotalMinute % 60;
        }
    }

    private void regulateToDoHourPickerWheel(int targetHour) {
        if (targetHour < 0 || targetHour > 23) {
            System.out.println("시간은 음수 혹은 23시 초과로 설정할 수 없습니다.");
        } else {
            while (true) {
                WebElement routineHourPickerWheel = driver.findElement(iOSClassChain(
                        "**/XCUIElementTypePicker[`name == \"시간\"`]/XCUIElementTypePickerWheel"));

                int currentHour = Integer.parseInt(routineHourPickerWheel.getAttribute("value"));

                // 목표 값에 도달했는지 확인
                if (currentHour == targetHour) {
                    System.out.println("시간 설정 완료: " + currentHour);
                    break;
                }

                String direction = (currentHour > targetHour) ? "previous" : "next";

                Map<String, Object> params = new HashMap<>();
                params.put("element", ((RemoteWebElement) routineHourPickerWheel).getId());
                params.put("order", direction);
                params.put("offset", 0.15);

                driver.executeScript("mobile: selectPickerWheelValue", params);
            }
        }
    }

    private void regulateToDoMinutePickerWheel(int targetMinute) {
        if (targetMinute < 0 || targetMinute > 59) {
            System.out.println("분은 음수 혹은 59분 초과로 설정할 수 없습니다.");
        } else {
            while (true) {
                WebElement routineHourPickerWheel = driver.findElement(iOSClassChain(
                        "**/XCUIElementTypePicker[`name == \"분\"`]/XCUIElementTypePickerWheel"));

                int currentHour = Integer.parseInt(routineHourPickerWheel.getAttribute("value"));

                // 목표 값에 도달했는지 확인
                if (currentHour == targetMinute) {
                    System.out.println("분 설정 완료: " + currentHour);
                    break;
                }

                String direction = (currentHour > targetMinute) ? "previous" : "next";

                Map<String, Object> params = new HashMap<>();
                params.put("element", ((RemoteWebElement) routineHourPickerWheel).getId());
                params.put("order", direction);
                params.put("offset", 0.15);

                driver.executeScript("mobile: selectPickerWheelValue", params);
            }
        }
    }

    private void enterBasicInformationRoutineAndToDo(String routineTitle, String toDoTitle) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        routineTab = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("clock.arrow.circlepath")));
        routineTab.click();

        createRoutineButton = driver.findElement(accessibilityId("시작하기"));
        createRoutineButton.click();

        newRoutine = driver.findElement(iOSClassChain("**/XCUIElementTypeScrollView/**/XCUIElementTypeTextField"));
        newRoutine.click();
        newRoutine.sendKeys(routineTitle);

        addRoutineButton = driver.findElement(iOSNsPredicateString("name == \"plus\" AND label == \"추가\" AND type == \"XCUIElementTypeButton\""));
        addRoutineButton.click();

        newToDo = wait.until(ExpectedConditions.presenceOfElementLocated(iOSClassChain("**/XCUIElementTypeWindow/**/XCUIElementTypeTextField")));
        newToDo.click();
        newToDo.sendKeys(toDoTitle);
    }

    private void createRoutineForModifyAndRemoveRoutine() {
        String routineTitle = "루틴";
        String toDoTitle = "할 일";

        enterBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

        checkmark = driver.findElement(accessibilityId("checkmark"));
        // 할 일 생성
        checkmark.click();
        // 루틴 생성
        checkmark = driver.findElement(accessibilityId("checkmark"));
        checkmark.click();

        WebElement createdRoutine = driver.findElement(accessibilityId(routineTitle));
        assertEquals(createdRoutine.getAttribute("name"), routineTitle);
    }

    public void convertExpectedRoutineTotalTimeToString() {
        if (findRoutineTotalMinute != 0 && findRoutineTotalMinute != 0) {
            expectedRoutineTotalTime = findRoutineTotalHour + "시간 " + findRoutineTotalMinute + "분";
        } else if (findRoutineTotalHour != 0 && findRoutineTotalMinute == 0) {
            expectedRoutineTotalTime = findRoutineTotalHour + "시간";
        } else {
            expectedRoutineTotalTime = findRoutineTotalMinute + "분";
        }
    }

    private void addToDoInRoutine(String routineTitle, String toDoTitle) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        routineTab = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("clock.arrow.circlepath")));
        routineTab.click();

        WebElement createdRoutine = driver.findElement(accessibilityId(routineTitle));
        createdRoutine.click();

        addToDoButtonInRoutine = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"plus\"`]"));
        addToDoButtonInRoutine.click();

        newToDo = wait.until(ExpectedConditions.presenceOfElementLocated(iOSClassChain("**/XCUIElementTypeWindow/**/XCUIElementTypeTextField")));
        newToDo.click();
        newToDo.sendKeys(toDoTitle);
    }
}
