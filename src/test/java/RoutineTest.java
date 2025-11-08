import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static io.appium.java_client.AppiumBy.*;
import static org.testng.Assert.*;

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

            writeBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

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

            writeBasicInformationRoutineAndToDo(routineTitle, "");

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement notEnterToDoTitleErrorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("새로운 할 일의 이름을 입력해주세요")));

            assertTrue(notEnterToDoTitleErrorMessage.isDisplayed());
        }

        @Test
        public void 할일_이름만_입력() {
            String toDoTitle = "루틴 생성 테스트 - 할일 이름만 입력";

            writeBasicInformationRoutineAndToDo("", toDoTitle);

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
            writeBasicInformationRoutineAndToDo("", "");

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

            writeBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

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
            String toDoTitle = "액션(할일) 등록 테스트";

            writeBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

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

            writeBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

            WebElement routineHourPickerWheel = driver.findElement(iOSClassChain(
                    "**/XCUIElementTypePicker[`name == \"시간\"`]/XCUIElementTypePickerWheel"));
            routineHourPickerWheel.sendKeys(Integer.toString(3));
//            regulateToDoHourPickerWheel(3);
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

        @Test
        public void 액션_수정() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "액션 수정 테스트 할 일";
            String modifyToDoTitle = "액션 이름 수정";
            int modifyHour = 3;
            int modifyMinute = 0;

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            routineTab = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("clock.arrow.circlepath")));
            routineTab.click();

            WebElement createdRoutine = driver.findElement(accessibilityId(routineTitle));
            createdRoutine.click();

            WebElement createdToDo = driver.findElement(accessibilityId(toDoTitle));
            createdToDo.click();

            WebElement toDoTextField = driver.findElement(iOSClassChain("**/XCUIElementTypeTextField"));
            toDoTextField.clear();
            toDoTextField.sendKeys(modifyToDoTitle);

            regulateToDoHourPickerWheel(modifyHour);
            regulateToDoMinutePickerWheel(modifyMinute);

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement modifiedToDo = driver.findElement(accessibilityId(modifyToDoTitle));
            assertTrue(modifiedToDo.isDisplayed());

            WebElement modifiedToDoTime = driver.findElement(
                    xpath("//XCUIElementTypeStaticText[@name='" + modifyToDoTitle + "']/following-sibling::XCUIElementTypeStaticText"));

            verifyToDoTime(modifyHour, modifyMinute, modifiedToDoTime);

            findAllToDoInRoutineAndCalculateTotalTime();

            convertExpectedRoutineTotalTimeToString();
            WebElement realRoutineTotalTime = driver.findElement(iOSClassChain("**/XCUIElementTypeScrollView/**/XCUIElementTypeStaticText[`name CONTAINS '시간' OR name CONTAINS '분'`]"));

            assertEquals(realRoutineTotalTime.getAttribute("name"), expectedRoutineTotalTime);
        }
    }

    @Nested
    @DisplayName("액션 수정 탭에서 연속 실행 및 알람 버튼 동작 테스트")
    class ToDoPlayButtonTest {
        @Test
        public void 액션_수정_탭에서_액션_알람_버튼_ON() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "미리 등록된 할 일";

            enterRoutineTabAndClickRoutine(routineTitle);
            clickToDo(toDoTitle);

            alarmOnOff();

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createdToDo = driver.findElement(accessibilityId(toDoTitle));
            createdToDo.click();

            WebElement alarmOnButton = driver.findElement(accessibilityId("bell"));
            assertTrue(alarmOnButton.isDisplayed(), "기대 결과와 다릅니다. 알람 초기 설정 상태를 확인해주세요.");
        }

        @Test
        public void 액션_수정_탭에서_액션_알람_버튼_OFF() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "미리 등록된 할 일";

            enterRoutineTabAndClickRoutine(routineTitle);
            clickToDo(toDoTitle);

            alarmOnOff();

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createdToDo = driver.findElement(accessibilityId(toDoTitle));
            createdToDo.click();

            WebElement alarmOffButton = driver.findElement(accessibilityId("bell.slash"));
            assertTrue(alarmOffButton.isDisplayed(), "기대 결과와 다릅니다. 알람 초기 설정 상태를 확인해주세요.");
        }

        @Test
        public void 액션_수정_탭에서_액션_연속_실행_OFF() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "미리 등록된 할 일";

            enterRoutineTabAndClickRoutine(routineTitle);
            clickToDo(toDoTitle);

            continuousExecutionOnOff();

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createdToDo = driver.findElement(accessibilityId(toDoTitle));
            createdToDo.click();

            WebElement executionOffButton = driver.findElement(accessibilityId("line.diagonal, 달리기"));
            assertTrue(executionOffButton.isDisplayed(), "기대 결과와 다릅니다. 알람 초기 설정 상태를 확인해주세요.");
        }

        @Test
        public void 액션_수정_탭에서_액션_연속_실행_ON() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "미리 등록된 할 일";

            enterRoutineTabAndClickRoutine(routineTitle);
            clickToDo(toDoTitle);

            continuousExecutionOnOff();

            checkmark = driver.findElement(accessibilityId("checkmark"));
            checkmark.click();

            WebElement createdToDo = driver.findElement(accessibilityId(toDoTitle));
            createdToDo.click();

            WebElement executionOnButton = driver.findElement(accessibilityId("figure.run"));
            assertTrue(executionOnButton.isDisplayed(), "기대 결과와 다릅니다. 알람 초기 설정 상태를 확인해주세요.");
        }
    }

    @Nested
    @DisplayName("액션 타이머 실행 테스트")
    class RunToDoTimerTest {
        @Test
        public void 루틴_외부에서_타이머_실행() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "미리 등록된 할 일";

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            routineTab = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("clock.arrow.circlepath")));
            routineTab.click();

            WebElement routineTimerPlayButton = driver.findElement
                    (xpath("//XCUIElementTypeStaticText[@name='" + routineTitle + "']/following-sibling::XCUIElementTypeButton[@name='play']"));
            routineTimerPlayButton.click();

            /* 타이머가 정상적으로 실행되는지 확인
             * 1. 타이머 실행 화면이 떴는가
             * */
            WebElement currentToDoTitle = driver.findElement(accessibilityId(toDoTitle));
            assertEquals(currentToDoTitle.getAttribute("name"), toDoTitle);
        }

        @Test
        public void 루틴_내부에서_타이머_실행() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "미리 등록된 할 일";

            enterRoutineTabAndClickRoutine(routineTitle);

            WebElement routineTimerPlayButton = driver.findElement(accessibilityId("play.fill"));
            routineTimerPlayButton.click();

            WebElement currentToDoTitle = driver.findElement(accessibilityId(toDoTitle));
            assertEquals(currentToDoTitle.getAttribute("name"), toDoTitle);
        }

        @Test
        public void 루틴_외부_실행_후_뒤로가기_눌렀을때_타이머_유지_테스트() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "미리 등록된 할 일";

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            routineTab = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("clock.arrow.circlepath")));
            routineTab.click();

            WebElement routineTimerPlayButton = driver.findElement
                    (xpath("//XCUIElementTypeStaticText[@name='" + routineTitle + "']/following-sibling::XCUIElementTypeButton[@name='play']"));
            routineTimerPlayButton.click();

            WebElement currentToDoTitle = driver.findElement(accessibilityId(toDoTitle));
            assertEquals(currentToDoTitle.getAttribute("name"), toDoTitle);

            WebElement backButton = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"chevron.backward\"`]"));
            backButton.click();

            // assertEquals를 이용한 검증 대신 driver가 해당 요소를 찾지 못하면 화면에 나타나지 않은 것으로 판단하고 에러 발생
            driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name CONTAINS \"" + toDoTitle + "\"`]"));
        }

        @Test
        public void 루틴_내부_실행_후_뒤로가기_눌렀을때_타이머_유지_테스트() {
            String routineTitle = "미리 등록된 루틴";
            String toDoTitle = "미리 등록된 할 일";

            enterRoutineTabAndClickRoutine(routineTitle);

            WebElement routineTimerPlayButton = driver.findElement(accessibilityId("play.fill"));
            routineTimerPlayButton.click();

            WebElement backButton = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name == \"chevron.backward\"`]"));
            backButton.click();

            driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name CONTAINS \"" + toDoTitle + "\"`]"));
        }

        @Test
        public void 액션_정지_토글_테스트_루틴_내_액션이_한_개일_때() {
            String routineTitle = "액션 정지 테스트 루틴";
            String toDoTitle = "액션 정지 테스트 할 일";

            enterRoutineTabAndClickRoutine(routineTitle);

            WebElement routineTimerPlayButton = driver.findElement(accessibilityId("play.fill"));
            routineTimerPlayButton.click();

            // 화면에 기대한 할 일 제목이 표시됐는지 확인
            driver.findElement(accessibilityId(toDoTitle));

            WebElement toggle = driver.findElement(accessibilityId("chevron.up"));
            toggle.click();

            WebElement pauseButton = driver.findElement(accessibilityId("pause"));
            pauseButton.click();

            // 정지 버튼 클릭 후 화면에 기대한 재생 버튼이 표시됐는지 확인
            WebElement playButton = driver.findElement(accessibilityId("play.fill"));

            // 정지한 뒤 액션 제목의 계층에서 타이머 시간을 찾음 (시간, 분, 초가 포함되지 않은 값)
            String findTimerXpathBeforeReplay = "//XCUIElementTypeStaticText[@name='" + toDoTitle + "']/following-sibling::XCUIElementTypeStaticText" +
                    "[not(contains(@name, '시간')) and not(contains(@name, '분')) and not(contains(@name, '초'))]";
            WebElement curPausedTime = driver.findElement(xpath(findTimerXpathBeforeReplay));
            String curPausedTimeValue = curPausedTime.getAttribute("name");

            // 타이머 다시 재생
            playButton.click();

            String findTimerXpathAfterReplay = "//XCUIElementTypeStaticText[@name='" + toDoTitle + "']/following-sibling::XCUIElementTypeStaticText" +
                    "[not(contains(@name, '시간')) and not(contains(@name, '분')) and not(contains(@name, '초'))]";

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            try {
                WebElement curPlayedTime = wait.until(driver -> {
                    WebElement timeElement = driver.findElement(xpath(findTimerXpathAfterReplay));
                    String timeElementValue = timeElement.getAttribute("name");

                    if (!timeElementValue.equals(curPausedTimeValue)) {
                        return timeElement;
                    }
                    return null;
                });

                assertNotEquals(curPausedTimeValue, curPlayedTime.getAttribute("name"), "타이머 값이 동일합니다.");

            } catch (Exception e) {
                System.out.println("타이머 값이 10초 동안 변경되지 않았습니다.");
                fail();
            }
        }

        @Test
        public void 액션_정지_토글_테스트_루틴_내_액션이_여러_개일_때() {
            String routineTitle = "액션 정지 테스트 루틴 - 액션 여러 개";

            enterRoutineTabAndClickRoutine(routineTitle);

            List<WebElement> toDoList = driver.findElements(iOSClassChain("**/XCUIElementTypeCollectionView/**/XCUIElementTypeStaticText"));
            List<String> toDoNameList = new ArrayList<>();

            // 액션 이름 및 시간을 가져왔기 때문에, toDoList에서 시간 값을 제외한 이름 값만 String List에 add
            for (int i = 0; i < toDoList.size(); i++) {
                if (i % 2 == 0) {
                    String toDoName = toDoList.get(i).getAttribute("name");
                    toDoNameList.add(toDoName);
                }
            }

            String toDoTitle = toDoList.get(0).getAttribute("name");

            WebElement routineTimerPlayButton = driver.findElement(accessibilityId("play.fill"));
            routineTimerPlayButton.click();

            WebElement toggle = driver.findElement(accessibilityId("chevron.up"));
            toggle.click();

            // 앞서 확인한 루틴 내 액션들이 토글 내 화면에 위치하는지 확인
            for (String curToDoName : toDoNameList) {
                driver.findElement(xpath("//XCUIElementTypeStaticText[@name='" + curToDoName + "']"));
            }

            // 정지 버튼 클릭 시 재생 버튼으로 바뀌는지 확인
            WebElement pauseButton = driver.findElement(accessibilityId("pause"));
            pauseButton.click();

            WebElement playButton = driver.findElement(accessibilityId("play.fill"));

            // 정지한 뒤 액션 제목의 계층에서 타이머 시간을 찾음 (시간, 분, 초가 포함되지 않은 값)
            String findTimerXpathBeforeReplay = "//XCUIElementTypeStaticText[@name='" + toDoTitle + "']/following-sibling::XCUIElementTypeStaticText" +
                    "[not(contains(@name, '시간')) and not(contains(@name, '분')) and not(contains(@name, '초'))]";
            WebElement curPausedTime = driver.findElement(xpath(findTimerXpathBeforeReplay));
            String curPausedTimeValue = curPausedTime.getAttribute("name");

            playButton.click();

            String findTimerXpathAfterReplay = "//XCUIElementTypeStaticText[@name='" + toDoTitle + "']/following-sibling::XCUIElementTypeStaticText" +
                    "[not(contains(@name, '시간')) and not(contains(@name, '분')) and not(contains(@name, '초'))]";

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement curPlayedTime = wait.until(driver -> {
                    WebElement timeElement = driver.findElement(xpath(findTimerXpathAfterReplay));
                    String timeElementValue = timeElement.getAttribute("name");

                    if (!curPausedTimeValue.equals(timeElementValue)) {
                        return timeElement;
                    }
                    return null;
                });

                assertNotEquals(curPlayedTime.getAttribute("name"), curPausedTimeValue, "타이머 값이 변경되지 않았습니다.");
            } catch (Exception e) {
                System.out.println("타이머 값이 10초 동안 변경되지 않았습니다.");
                fail();
            }
        }

        @Test
        public void 알람_ON_푸시_알림이_포그라운드에서_정상적으로_동작하는지_확인() throws InterruptedException {
            /*
             * 테스트 편의상 타이머는 모두 1분으로 설정함
             * 포그라운드에서 푸시 알림이 동작할 경우 알림 센터에 쌓이지 않아 로그로 확인함
             * */

            // 테스트 시작 시간
            final long verificationStartTime = Instant.now().toEpochMilli();
            System.out.println("시작 타임스탬프: " + verificationStartTime);

            String routineTitle = "액션 알람 테스트 - 할 일이 한 개일 때";
            String toDoTitle = "할 일";
            enterRoutineTabAndClickRoutine(routineTitle);

            final String expectedNotificationLogMessage = "event = ViewDidAppear";

            WebElement playButton = driver.findElement(accessibilityId("play.fill"));
            playButton.click();

            // 알람의 초기 상태에 상관 없이, 알람을 반대 상태로 돌려주는 함수 호출 (꺼짐 <-> 켜짐)
            alarmOnOff();

            try {
                driver.findElement(accessibilityId("bell"));
            } catch (Exception e) {
                System.out.println("알람 초기 설정을 꺼진 상태로 설정 후 재시도 하십시오");
                fail();
            }

            // 타이머가 종료돼서 루틴 탭으로 복귀할 때까지 대기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(70));
            wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("clock.arrow.circlepath")));

            // 로그 버퍼 초기화 -> 이전 로그 걸러냄
            cleanLogsBuffer(driver);

            // 푸시 알림 로그 확인 동작
            // iOS Driver는 WebDriver를 구현한 것이므로 업캐스팅
            Wait<WebDriver> waitLog = new FluentWait<WebDriver>(driver)
                    .withTimeout(Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofSeconds(1))
                    .ignoring(Exception.class);

            System.out.println("푸시 알림 로그 대기 시작: " + toDoTitle);

            try {
                boolean foundLog = waitLog.until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        LogEntries logEntries;
                        try {
                            logEntries = driver.manage().logs().get("syslog");
                        } catch (Exception e) {
                            System.out.println("로그 가져오기 실패: " + e.getMessage());
                            return false;
                        }

                        for (LogEntry logEntry : logEntries) {
                            // 테스트 시작 이후의 로그 선별
                            if (logEntry.getTimestamp() >= verificationStartTime) {
                                System.out.println("현재 메시지: " + logEntry.getMessage() + "현재 타임스탬프: " + logEntry.getTimestamp());
                                if (logEntry.getMessage().contains(expectedNotificationLogMessage)) {
                                    System.out.println("✅포그라운드 알림 로그 가져오기 성공: " + logEntry.getMessage());
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });
                if (!foundLog) {
                    System.out.println("푸시 알림 검증 실패: 시간 내에 해당 로그를 찾지 못했습니다.");
                    fail();
                }
            } catch (Exception e) {
                System.out.println("푸시 알림 검증 실패: " + e.getMessage());
                fail();
            }
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
            WebElement routineHourPickerWheel = driver.findElement(iOSClassChain(
                    "**/XCUIElementTypePicker[`name == \"시간\"`]/XCUIElementTypePickerWheel"));
            routineHourPickerWheel.sendKeys(Integer.toString(targetHour));
        }
    }

    private void regulateToDoMinutePickerWheel(int targetMinute) {
        if (targetMinute < 0 || targetMinute > 59) {
            System.out.println("분은 음수 혹은 59분 초과로 설정할 수 없습니다.");
        } else {
            WebElement routineMinutePickerWheel = driver.findElement(iOSClassChain(
                    "**/XCUIElementTypePicker[`name == \"분\"`]/XCUIElementTypePickerWheel"));
            routineMinutePickerWheel.sendKeys(Integer.toString(targetMinute));
        }
    }

    private void writeBasicInformationRoutineAndToDo(String routineTitle, String toDoTitle) {
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

        writeBasicInformationRoutineAndToDo(routineTitle, toDoTitle);

        checkmark = driver.findElement(accessibilityId("checkmark"));
        // 할 일 생성
        checkmark.click();
        // 루틴 생성
        checkmark = driver.findElement(accessibilityId("checkmark"));
        checkmark.click();

        WebElement createdRoutine = driver.findElement(accessibilityId(routineTitle));
        assertEquals(createdRoutine.getAttribute("name"), routineTitle);
    }

    private void convertExpectedRoutineTotalTimeToString() {
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

    private void verifyToDoTime(int expectedHour, int expectedMinute, WebElement todoTime) {
        if (expectedHour != 0 && expectedMinute != 0) {
            assertEquals(todoTime.getAttribute("name"), expectedHour + "시간 " + expectedMinute + "분");
        } else if (expectedHour != 0 && expectedMinute == 0) {
            assertEquals(todoTime.getAttribute("name"), expectedHour + "시간");
        } else {
            assertEquals(todoTime.getAttribute("name"), expectedMinute + "분");
        }
    }

    private void enterRoutineTabAndClickRoutine(String routineTitle) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        routineTab = wait.until(ExpectedConditions.presenceOfElementLocated(accessibilityId("clock.arrow.circlepath")));
        routineTab.click();

        WebElement createdRoutine = driver.findElement(accessibilityId(routineTitle));
        createdRoutine.click();
    }

    private void clickToDo(String toDoTitle) {
        WebElement createdToDo = driver.findElement(accessibilityId(toDoTitle));
        createdToDo.click();
    }

    private void alarmOnOff() {
        WebElement alarmButton = driver.findElement(iOSClassChain("**/XCUIElementTypeButton[`name CONTAINS 'bell'`]"));
        alarmButton.click();
    }

    private void continuousExecutionOnOff() {
        WebElement continuousExecutionButton = driver.findElement(accessibilityId("figure.run"));
        continuousExecutionButton.click();
    }

    private void cleanLogsBuffer(WebDriver webDriver) {
        // 로그 한 번 호출해서 비우기
        webDriver.manage().logs().get("syslog");
    }

    private String getTextFromPushNotificationBanner(String imageSavePath) throws IOException, TesseractException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullScreenshot = ImageIO.read(screenshot);

        int bannerX = 0;
        int bannerY = 0;
        int bannerWidth = fullScreenshot.getWidth();
        int bannerHeight = (int) (fullScreenshot.getHeight() * 0.15);

        BufferedImage croppedImage = fullScreenshot.getSubimage(bannerX, bannerY, bannerWidth, bannerHeight);
        // 이미지 저장 경로와 이름 지정
        String fileName = "push_notification_cropped_+" + LocalDateTime.now() + ".png";
        File croppedFile = new File(imageSavePath + "/" + fileName);

        // 메모리에 있는 croppedImage를 가져와서 png 형식으로 변환한 뒤 croppedFile이 지정하는 경로에 저장해라
        ImageIO.write(croppedImage, "png", croppedFile);
        System.out.println("잘라낸 배너 이미지 저장: " + croppedFile.getAbsolutePath());

        // OCR 텍스트 인식
        String recognizedText = tesseract.doOCR(croppedImage);

        System.out.println("인식된 텍스트:\n" + recognizedText);
        return recognizedText;
    }
}
