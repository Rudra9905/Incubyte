package com.incubyte.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SampleUnitTest {

    @Mock
    private List<String> mockList;

    @Test
    void testMockitoConfiguration() {
        when(mockList.size()).thenReturn(5);
        assertEquals(5, mockList.size());
    }
}
