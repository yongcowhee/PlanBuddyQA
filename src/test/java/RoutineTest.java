import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static io.appium.java_client.AppiumBy.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RoutineTest extends TestBase {
    WebElement routineTab;
    WebElement createRoutineButton;
    WebElement newRoutine;
    WebElement addRoutineButton;
    WebElement newToDo;
    WebElement routineHourPickerWheel;
    WebElement routineMinutePickerWheel;
    WebElement checkmark;
    int findRoutineTotalHour;
    int findRoutineTotalMinute;
    String expectedRoutineTotalTime;

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
}
