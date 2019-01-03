package com.fishlikewater.client.sample;

import com.fishlikewater.client.sample.schedule.ScheduleServer1;
import com.fishlikewater.client.sample.schedule.ScheduleServer2;
import org.junit.Test;

/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class SpringBootScheduleClientSampleApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void test1(){
        ScheduleServer1 server1 = new ScheduleServer1();
        ScheduleServer2 server2 = new ScheduleServer2();
    }

}

