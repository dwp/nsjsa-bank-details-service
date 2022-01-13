package uk.gov.dwp.jsa.bankdetails.service.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.dwp.jsa.bankdetails.service.AppInfo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.bankdetails.service.config.WithVersionUriComponentsBuilder.VERSION_SPEL;

@RunWith(MockitoJUnitRunner.class)
public class WithVersionUriComponentsBuilderTest {

    private static final String PATH_TEMPLATE = "/path/%s/test";

    private static final String PATH_WITH_VERSION_SPEL = String.format(PATH_TEMPLATE, VERSION_SPEL);
    private static final String PATH_WITHOUT_VERSION_SPEL = "/any/path";
    private static final String VERSION = "v1";
    private static final String PATH_RESULT = String.format(PATH_TEMPLATE, VERSION);
    @Mock
    private AppInfo mockAppInfo;

    private WithVersionUriComponentsBuilder testSubject;

    @Before
    public void setUp() {
        testSubject = new WithVersionUriComponentsBuilder(mockAppInfo);
        when(mockAppInfo.getVersion()).thenReturn(VERSION);
    }

    @Test
    public void givenAPathWithVersionSpel_path_ShouldReplaceTheSPELByTheVersion() {
        UriComponentsBuilder componentsBuilder = testSubject.path(PATH_WITH_VERSION_SPEL);
        assertEquals(PATH_RESULT, componentsBuilder.build().toString());
    }

    @Test
    public void givenAPathWithoutVersionSpel_path_ShouldNotAffectThePath() {
        UriComponentsBuilder componentsBuilder = testSubject.path(PATH_WITHOUT_VERSION_SPEL);
        assertEquals(PATH_WITHOUT_VERSION_SPEL, componentsBuilder.build().toString());
    }

    @Test
    public void givenAPathWithVersionSpelButNotAppInfo_path_ShouldNotAffectThePath() {
        testSubject = new WithVersionUriComponentsBuilder(null);
        UriComponentsBuilder componentsBuilder = testSubject.path(PATH_WITH_VERSION_SPEL);
        assertEquals(PATH_WITH_VERSION_SPEL, componentsBuilder.build().toString());
    }

    @Test
    public void givenAPathWithVersionSpelButAppInfoVersionIsNull_path_ShouldNotAffectThePath() {
        when(mockAppInfo.getVersion()).thenReturn(null);
        UriComponentsBuilder componentsBuilder = testSubject.path(PATH_WITH_VERSION_SPEL);
        assertEquals(PATH_WITH_VERSION_SPEL, componentsBuilder.build().toString());
    }

}
