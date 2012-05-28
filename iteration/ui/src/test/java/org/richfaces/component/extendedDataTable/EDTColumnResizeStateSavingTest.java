package org.richfaces.component.extendedDataTable;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Collection;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

@RunWith(Arquillian.class)
@WarpTest
public class EDTColumnResizeStateSavingTest {

    @Drone
    RemoteWebDriver browser;

    @ArquillianResource
    URL contextPath;

    @Deployment
    public static WebArchive createDeployment() {
        Collection<GenericArchive> mavenDependencies = Maven.withPom("pom.xml").dependencies(
                "org.richfaces.ui.iteration:richfaces-ui-iteration-api",
                "org.richfaces.ui.iteration:richfaces-ui-iteration-ui", "org.richfaces.core:richfaces-core-api",
                "org.richfaces.core:richfaces-core-impl", "com.google.guava:guava",
                "org.richfaces.ui.common:richfaces-ui-common-api", "org.richfaces.ui.common:richfaces-ui-common-ui",
                "net.sourceforge.cssparser:cssparser:0.9.5", "org.w3c.css:sac:1.3");

        return ShrinkWrap.create(WebArchive.class, "edt-test.war").addAsManifestResource("META-INF/iteration.taglib.xml")
                .addAsWebInfResource("faces-config.xml")
                .addAsWebResource("extendedDataTable/stateSaving.xhtml", "index.xhtml").addAsLibraries(mavenDependencies)
                .addAsWebInfResource("beans.xml").addClass(TableModelBean.class);
    }

    @Test
    @RunAsClient
    public void test() {

        // given
        browser.navigate().to(contextPath + "index.jsf");
        WebElement renderTableButton = browser.findElement(By.id("renderTable"));
        long width = getSecondColumnWidth();
        long newWidth = width + 50;

        // when
        setSecondColumnWidth(newWidth);
        assertEquals(newWidth, getSecondColumnWidth());
        renderTableButton.click();
        renderTableButton.click();

        // then
        assertEquals(newWidth, getSecondColumnWidth());
        assertEquals(newWidth, getSecondColumnWidth());
    }

    public void setSecondColumnWidth(long width) {
        browser.executeScript("RichFaces.$('table').setColumnWidth('column2', " + width + ")");
    }

    public long getSecondColumnWidth() {
        return (Long) browser.executeScript("return $('.rf-edt-hdr-c.rf-edt-c-column2').width()");
    }
}
