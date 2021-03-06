package mobileTesting.Util;

import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.ElementOption;
import mobileTesting.configuration.InitiateDevice;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;

import java.time.Duration;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.ElementOption.element;

public abstract class CrossPlatformUtility extends InitiateDevice {

    /**
     * Hold test fot time interval
     *
     * @param durationInSec time interval in seconds
     */
    public static void threadSleep(int durationInSec) {
        try {
            Thread.sleep(durationInSec * 1000);
        } catch (Exception e){
            System.out.println("Can't sleep " + e);
        }
    }

    /**
     * Locate element
     *
     * @param identifier
     */
    public static void locateElement(By identifier) {
        WebElement e = getElement(identifier);
        if (e != null) {
            return;
        }
        throw new UnhandledAlertException("Unable to find element");
    }

    /**
     * * Locate element and click on it
     *
     * @param identifier
     */
    public static void locateElementClick(By identifier) {
        getElement(identifier).click();
    }

    /**
     * * Locate element and send value
     *
     * @param identifier
     * @param sendValue
     */
    public static void locateElementSendKeys(By identifier, String sendValue) {
        getElement(identifier).sendKeys(sendValue);
    }

    /**
     * Get elements text
     *
     * @param identifier
     */
    public static String getElementText (By identifier) {
        WebElement e = getElement(identifier);
        if (e == null) {
            throw new UnhandledAlertException("Unable to find element");
        }
        return e.getText();
    }

    /**
     * Verifies that element contains text
     *
     * @param identifier
     * @param text
     */
    public static boolean verifyElementContainsText(By identifier, String text) {
        if (!getElementText (identifier).contains(text)) {
            failTest("Element '" + identifier + "' does not contain '" + text + "'");
        }
        return true;
    }

    /**
     * Swipe the element to the left
     *
     * @param identifier
     */
    public static void swipeElementToLeft(By identifier) {
        TouchAction swipe = new TouchAction(null);
        switch (getPlatform()) {
            case IOS:
                swipe = new TouchAction(getIOSDriver());
                break;
            case ANDROID:
                swipe = new TouchAction(getAndroidDriver());
                break;
        }
        WebElement element = getElement(identifier);
        Dimension size = element.getSize();
        ElementOption press = element(element, (int) (size.width * 0.8), size.height / 2);
        ElementOption move = element(element, 1, size.height / 2);

        swipe.press(press)
                .waitAction(waitOptions(Duration.ofSeconds(2)))
                .moveTo(move)
                .release()
                .perform();
    }

    /**
     * Check if element is present
     *
     * @param identifier
     * @return
     */
    public static Boolean isPresent(By identifier) {
        try {
            locateElement(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get element by identifier
     *
     * @param identifier
     * @return
     */
    public static WebElement getElement(By identifier) {
        switch (getPlatform()) {
            case IOS:
                new WebDriverWait(getIOSDriver(), normalTimeInterval).until(ExpectedConditions.visibilityOfElementLocated(identifier));
                return getIOSDriver().findElement(identifier);
            case ANDROID:
                new WebDriverWait(getAndroidDriver(), normalTimeInterval).until(ExpectedConditions.visibilityOfElementLocated(identifier));
                return getAndroidDriver().findElement(identifier);
        }
        return null;
    }

    /**
     * Clear any valie from text input field
     * @param identifier
     */
    public static void clearElement(By identifier) {
        getElement(identifier).clear();
    }

    /**
     * Fail the test and trow a message
     *
     * @param message
     */
    public static void failTest(String message) {
        System.out.println(message);
        Assert.assertTrue(false);
    }

    /**
     * Skip the test
     *
     * @param message
     */
    public static void skipTest(String message) {
        throw new SkipException("Test skipped");
    }
}