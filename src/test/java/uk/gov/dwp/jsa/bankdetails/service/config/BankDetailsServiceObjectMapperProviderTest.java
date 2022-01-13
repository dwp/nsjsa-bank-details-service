package uk.gov.dwp.jsa.bankdetails.service.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BankDetailsServiceObjectMapperProviderTest {

    private ObjectMapper EXPECTED_MAPPER;

    @Before
    public void setUp() {
        EXPECTED_MAPPER = new ObjectMapper();
        EXPECTED_MAPPER.setDateFormat(new StdDateFormat());
    }

    @Test
    public void objectMapperProviderShouldReturnMapperWithCorrectDateFormatter() {
        BankDetailsServiceObjectMapperProvider objectMapperProvider = new BankDetailsServiceObjectMapperProvider();
        ObjectMapper mapper = objectMapperProvider.get();
        assertThat(mapper.getDateFormat(), is(instanceOf(EXPECTED_MAPPER.getDateFormat().getClass())));
    }

}
