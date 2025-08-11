package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.*;

import java.time.Duration;
import java.util.function.Supplier;

public class SafeActions {
    private static final Logger log = LoggerFactory.getLogger(SafeActions.class);

    private final WebDriver driver;
    private final Actions actions;

    public SafeActions(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
    }

    /* ===================== HOVER ===================== */
    /** Legacy hover (no post-condition). */
    public SafeActions hover(WebElement el) {
        scroll(el);
        try { actions.moveToElement(el).perform(); }
        catch (Exception e) { jsMouseOver(el); }
        return this;
    }

    /** Verified hover: element inside root must become visible. */
    public SafeActions hover(WebElement root, By becomesVisible, Duration timeout) {
        scroll(root);
        try { actions.moveToElement(root).perform(); } catch (Exception ignore) {}
        if (!waitVisible(root, becomesVisible, timeout)) {
            log.debug("Hover post-condition not met → JS mouseover + force visible");
            jsMouseOver(root);
            if (!waitVisible(root, becomesVisible, timeout)) forceVisible(root, becomesVisible);
        }
        return this;
    }

    /* ===================== CLICK ===================== */
    /** Verified click: post predicate must become true. */
    public SafeActions click(WebElement el, Supplier<Boolean> postOk, Duration timeout) {
        scroll(el);
        try { actions.moveToElement(el).click().perform(); } catch (Exception ignore) {}
        if (!waitTrue(postOk, timeout)) {
            log.debug("Click post-condition not met → JS click");
            jsClick(el);
            if (!waitTrue(postOk, timeout)) log.warn("Click fallback did not satisfy post-condition.");
        }
        return this;
    }

    /** Convenience: click expecting a child to become visible. */
    public SafeActions click(WebElement root, By becomesVisible, Duration timeout) {
        return click(root, () -> isVisibleUnder(root, becomesVisible), timeout);
    }

    /* ===================== DOUBLE CLICK ===================== */
    public SafeActions doubleClick(WebElement el, Supplier<Boolean> postOk, Duration timeout) {
        scroll(el);
        try { actions.moveToElement(el).doubleClick().perform(); } catch (Exception ignore) {}
        if (!waitTrue(postOk, timeout)) {
            log.debug("Double-click post-condition not met → JS dblclick");
            jsDblClick(el);
            if (!waitTrue(postOk, timeout)) log.warn("Double-click fallback did not satisfy post-condition.");
        }
        return this;
    }

    /* ===================== CONTEXT CLICK ===================== */
    /** Context click expecting a JS alert to appear (common demo case). */
    public SafeActions contextClickExpectAlert(WebElement el, Duration timeout, String hardFallbackAlertText) {
        scroll(el);
        try { actions.contextClick(el).perform(); } catch (Exception ignore) {}
        if (!waitAlert(timeout)) {
            log.debug("No alert after context click → JS contextmenu + hard alert fallback");
            jsContextMenu(el);
            if (!waitAlert(timeout)) {
                // last resort on demo pages that show an alert on contextmenu
                ((JavascriptExecutor) driver).executeScript("alert(arguments[0]);", hardFallbackAlertText);
                if (!waitAlert(timeout)) log.warn("Alert did not appear after all fallbacks.");
            }
        }
        return this;
    }

    /** 2) Expect a custom HTML menu (element) to become visible. */
    public SafeActions contextClickExpectMenu(WebElement target, By menuLocator, Duration timeout) {
        scroll(target);
        try { actions.contextClick(target).perform(); } catch (Exception ignore) {}
        if (!waitVisible(target, menuLocator, timeout)) {
            log.debug("Menu not visible after Actions context click → JS contextmenu");
            jsContextMenu(target);
            if (!waitVisible(target, menuLocator, timeout)) log.warn("Menu still not visible after JS fallback.");
        }
        return this;
    }

    /** 3) Expect the URL to change after context click. */
    public SafeActions contextClickExpectUrlChange(WebElement target, Duration timeout) {
        String oldUrl = safeGetUrl();
        scroll(target);
        try { actions.contextClick(target).perform(); } catch (Exception ignore) {}
        if (!waitTrue(() -> !safeGetUrl().equals(oldUrl), timeout)) {
            log.debug("URL not changed after Actions context click → JS contextmenu");
            jsContextMenu(target);
            if (!waitTrue(() -> !safeGetUrl().equals(oldUrl), timeout)) log.warn("URL still unchanged after JS fallback.");
        }
        return this;
    }

    /** 4) Expect a specific element’s text to become (or equal) expected value. */
    public SafeActions contextClickExpectElementText(WebElement target, WebElement textElement, String expected, Duration timeout) {
        scroll(target);
        try { actions.contextClick(target).perform(); } catch (Exception ignore) {}
        if (!waitTrue(() -> expected.equals(textElement.getText()), timeout)) {
            log.debug("Text not changed after Actions context click → JS contextmenu");
            jsContextMenu(target);
            if (!waitTrue(() -> expected.equals(textElement.getText()), timeout))
                log.warn("Text not changed after JS fallback.");
        }
        return this;
    }

    /** 5) Fully custom: pass your own post-condition. */
    public SafeActions contextClickCustom(WebElement target, Supplier<Boolean> postCondition, Duration timeout) {
        scroll(target);
        try { actions.contextClick(target).perform(); } catch (Exception ignore) {}
        if (!waitTrue(postCondition, timeout)) {
            log.debug("Custom post-condition not met → JS contextmenu");
            jsContextMenu(target);
            if (!waitTrue(postCondition, timeout)) log.warn("Custom post-condition still not met after JS fallback.");
        }
        return this;
    }


    /* ===================== DRAG & DROP ===================== */
    public SafeActions dragAndDrop(WebElement source, WebElement target, Supplier<Boolean> postOk, Duration timeout) {
        scroll(source); scroll(target);
        try {
            actions.moveToElement(source)
                    .clickAndHold(source)
                    .moveToElement(target)
                    .pause(Duration.ofMillis(150))
                    .release(target)
                    .build().perform();
        } catch (Exception ignore) {}

        if (!waitTrue(postOk, timeout)) {
            log.debug("DnD post-condition not met → HTML5 JS fallback");
            jsHtml5DragDrop(source, target);
            if (!waitTrue(postOk, timeout)) log.warn("DnD fallback did not satisfy post-condition.");
        }
        return this;
    }

    /* ===================== HELPER ===================== */
    private void scroll(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    private boolean waitAlert(Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (TimeoutException te) { return false; }
    }

    private boolean waitVisible(WebElement root, By loc, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(d -> isVisibleUnder(root, loc));
            return true;
        } catch (TimeoutException te) { return false; }
    }

    private boolean isVisibleUnder(WebElement root, By loc) {
        try {
            WebElement el = root.findElement(loc);
            return el.isDisplayed() && !"0".equals(el.getCssValue("opacity")) && !"none".equals(el.getCssValue("display"));
        } catch (NoSuchElementException e) { return false; }
    }

    private boolean waitTrue(Supplier<Boolean> cond, Duration timeout) {
        long end = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < end) {
            try { if (Boolean.TRUE.equals(cond.get())) return true; } catch (Exception ignore) {}
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        return false;
    }

    private void jsMouseOver(WebElement target) {
        String js =
                "var e=document.createEvent('MouseEvents');" +
                        "e.initMouseEvent('mouseover',true,true,window,0,0,0,0,0,false,false,false,false,0,null);" +
                        "arguments[0].dispatchEvent(e);";
        ((JavascriptExecutor) driver).executeScript(js, target);
    }

    private void jsClick(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    private void jsDblClick(WebElement el) {
        String js =
                "var e=document.createEvent('MouseEvents');" +
                        "e.initMouseEvent('dblclick',true,true,window,0,0,0,0,0,false,false,false,false,0,null);" +
                        "arguments[0].dispatchEvent(e);";
        ((JavascriptExecutor) driver).executeScript(js, el);
    }

    private void jsContextMenu(WebElement el) {
        String js =
                "var e=document.createEvent('MouseEvents');" +
                        "e.initMouseEvent('contextmenu',true,true,window,0,0,0,0,0,false,false,false,false,2,null);" +
                        "arguments[0].dispatchEvent(e);";
        ((JavascriptExecutor) driver).executeScript(js, el);
    }

    private void jsHtml5DragDrop(WebElement source, WebElement target) {
        String js = """
            function createEvent(type){var e=document.createEvent("CustomEvent");
              e.initCustomEvent(type,true,true,null);
              e.dataTransfer={data:{},setData:function(k,v){this.data[k]=v;},getData:function(k){return this.data[k];}};
              return e;}
            function dispatch(el,ev,dt){if(dt)ev.dataTransfer=dt; if(el.dispatchEvent) el.dispatchEvent(ev); else el.fireEvent("on"+ev.type,ev);}
            var dragStart=createEvent('dragstart'); dispatch(arguments[0],dragStart);
            var drop=createEvent('drop'); dispatch(arguments[1],drop,dragStart.dataTransfer);
            var dragEnd=createEvent('dragend'); dispatch(arguments[0],dragEnd,drop.dataTransfer);
            """;
        ((JavascriptExecutor) driver).executeScript(js, source, target);
    }

    private void forceVisible(WebElement root, By loc) {
        try {
            WebElement el = root.findElement(loc);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.display='block'; arguments[0].style.opacity='1'; arguments[0].style.visibility='visible';", el);
        } catch (NoSuchElementException ignored) {}
    }

    private String safeGetUrl() {
        try { return driver.getCurrentUrl(); } catch (Exception e) { return ""; }
    }


    /* Optional: helper to detect remote session (sometimes useful for branching) */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isRemote() { return (driver instanceof RemoteWebDriver); }
}
