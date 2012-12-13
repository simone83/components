package org.richfaces.component.extendedDataTable;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.ClientAction;
import org.jboss.arquillian.warp.ServerAssertion;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.jsf.AfterPhase;
import org.jboss.arquillian.warp.jsf.Phase;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.richfaces.component.AbstractExtendedDataTable;
import org.richfaces.component.ExtendedDataTableState;
import org.richfaces.component.UIColumn;
import org.richfaces.integration.IterationDeployment;
import org.richfaces.shrinkwrap.descriptor.FaceletAsset;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.net.URL;
import java.util.List;

import static org.jboss.arquillian.graphene.Graphene.guardXhr;

@RunAsClient
@WarpTest
@RunWith(Arquillian.class)
public class TestBuiltInSort {

    @Drone
    private WebDriver browser;

    @ArquillianResource
    private URL contextPath;

    @FindBy(id = "myForm:edt")
    private WebElement edt;

    @FindBy(id = "myForm:edt:0:n")
    private WebElement firstRow;

    @FindBy(id = "myForm:ajax")
    private WebElement button;

    @FindBy(id = "myForm:edt:header")
    private WebElement header;

    @FindBy(className = "rf-edt-srt")
    private WebElement sortHandle;

    @Deployment
    public static WebArchive createDeployment() {
        IterationDeployment deployment = new IterationDeployment(TestBuiltInSort.class);
        deployment.archive().addClass(IterationBean.class);
        addIndexPage(deployment);

        return deployment.getFinalArchive();
    }

    @Test
    public void table_sort() throws InterruptedException {
        // given
        browser.get(contextPath.toExternalForm());
        WebElement cell = browser.findElements(By.cssSelector(".rf-edt-c-column2 .rf-edt-c-cnt")).get(0);
        Assert.assertEquals("3", cell.getText());

        guardXhr(sortHandle).click();
        Thread.sleep(500);
        cell = browser.findElements(By.cssSelector(".rf-edt-c-column2 .rf-edt-c-cnt")).get(0);
        Assert.assertEquals("0", cell.getText());

        guardXhr(sortHandle).click();
        Thread.sleep(500);
        cell = browser.findElements(By.cssSelector(".rf-edt-c-column2 .rf-edt-c-cnt")).get(0);
        Assert.assertEquals("9", cell.getText());

        guardXhr(sortHandle).click();
        Thread.sleep(500);
        cell = browser.findElements(By.cssSelector(".rf-edt-c-column2 .rf-edt-c-cnt")).get(0);
        Assert.assertEquals("0", cell.getText());

    }

    @Test
    @Ignore
    public void table_filter() throws InterruptedException {
        // given
        browser.get(contextPath.toExternalForm());

        List<WebElement> cells = browser.findElements(By.cssSelector(".rf-edt-c-column2 .rf-edt-c-cnt"));
        WebElement cell = cells.get(cells.size() - 1);
        Assert.assertEquals("6", cell.getText());

        WebElement filterInput = browser.findElement(By.id("myForm:edt:filterInput"));
        filterInput.clear();
        filterInput.sendKeys("3");
        filterInput.sendKeys(Keys.TAB);

        Thread.sleep(500);
        cells = browser.findElements(By.cssSelector(".rf-edt-c-column2 .rf-edt-c-cnt"));
        cell = cells.get(cells.size() - 1);
        Assert.assertEquals("3", cell.getText());
    }

    private static void addIndexPage(IterationDeployment deployment) {
        FaceletAsset p = new FaceletAsset();
        p.xmlns("rich", "http://richfaces.org/iteration");
        p.xmlns("a4j", "http://richfaces.org/a4j");

        p.body("<script type='text/javascript'>");
        p.body("function sortEdt(currentSortOrder) { ");
        p.body("  var edt = RichFaces.$('myForm:edt'); ");
        p.body("  var sortOrder = currentSortOrder == 'ascending' ? 'descending' : 'ascending'; ");
        p.body("  edt.sort('column2', sortOrder, true); ");
        p.body("} ");
        p.body("function filterEdt(filterValue) { ");
        p.body("  var edt = RichFaces.$('myForm:edt'); ");
        p.body("  edt.filter('column2', filterValue, true); ");
        p.body("} ");
        p.body("</script>");
        p.body("<h:form id='myForm'> ");
        p.body("    <rich:extendedDataTable id='edt' value='#{iterationBean.values}' var='bean' filterVar='fv' > ");
        p.body("        <rich:column id='column1' width='150px' > ");
        p.body("            <f:facet name='header'>Column 1</f:facet> ");
        p.body("            <h:outputText value='Bean:' /> ");
        p.body("        </rich:column> ");
        p.body("        <rich:column id='column2' width='150px' ");
        p.body("                         sortBy='#{bean}' ");
        p.body("                         sortOrder='#{iterationBean.sortOrder}' ");
        p.body("                         filterValue='#{iterationBean.filterValue}' ");
        p.body("                         filterExpression='#{bean le fv}' > ");
        p.body("            <f:facet name='header'> ");
        p.body("                <h:panelGrid columns='1'> ");
        p.body("                    <h:link id='sort' onclick=\"sortEdt('#{iterationBean.sortOrder}'); return false;\">Column 2</h:link> ");
        p.body("                    <h:inputText id='filterInput' value='#{iterationBean.filterValue}' label='Filter' ");
        p.body("                                 onblur='filterEdt(this.value); return false; ' style='width: 25px' > ");
        p.body("                        <f:convertNumber /> ");
        p.body("                        <f:validateLongRange minimum='0' maximum='10' /> ");
        p.body("                    </h:inputText> ");
        p.body("                </h:panelGrid> ");
        p.body("            </f:facet> ");
        p.body("            <h:outputText value='#{bean}' /> ");
        p.body("        </rich:column> ");
        p.body("        <rich:column id='column3' width='150px'" );
        p.body("                     sortBy='#{bean}' ");
        p.body("                     sortOrder='#{iterationBean.sortOrder2}' > ");
        p.body("            <f:facet name='header'>Column 3</f:facet> ");
        p.body("            <h:outputText value='R#{bean}C3' /> ");
        p.body("        </rich:column> ");
        p.body("    </rich:extendedDataTable> ");
        p.body("    <a4j:commandButton id='ajax' execute='edt' render='edt' value='Ajax' /> ");
        p.body("</h:form> ");

        deployment.archive().addAsWebResource(p, "index.xhtml");
    }

}
