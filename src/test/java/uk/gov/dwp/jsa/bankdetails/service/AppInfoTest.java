package uk.gov.dwp.jsa.bankdetails.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertEquals;

public class AppInfoTest {

    @Value("${app.version}")
    private String version;

    private AppInfo testSubject;

    @Test
    public void givenPropertiesWithoutVerion_getVersion_ShouldReturnUnknown() {
        testSubject = new AppInfo(version);
        assertEquals("unknown", testSubject.getVersion());
    }

    @Test
    public void givenPropertiesWithMajorMinorVerion_getVersion_ShouldReturnVMajor() {
        testSubject = new AppInfo("1.0.0");
        assertEquals("v1.0.0", testSubject.getVersion());
    }

    @Test
    public void givenPropertiesWithVerionEmpty_getVersion_ShouldReturnUnknown() {
        testSubject = new AppInfo("");
        assertEquals("unknown", testSubject.getVersion());
    }

}
