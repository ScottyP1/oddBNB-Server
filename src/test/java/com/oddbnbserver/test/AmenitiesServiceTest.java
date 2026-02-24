package com.oddbnbserver.test;

import com.oddbnbserver.repositories.AmenitiesRepo;
import com.oddbnbserver.service.AmenitiesService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AmenitiesServiceTest {

    @Mock
    private AmenitiesRepo amenitiesRepo;

    @InjectMocks
    private AmenitiesService amenitiesService;


}
