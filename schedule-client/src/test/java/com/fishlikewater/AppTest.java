package com.fishlikewater;

import com.fishlikewater.schedule.client.kit.ScannerScheduleKit;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.CronSequenceGenerator;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testScannerPackage(){
        List<TaskDetail> taskDetails = ScannerScheduleKit.loadJobClass("com.fishlikewater");
        System.out.println(taskDetails.size());
        for (TaskDetail taskDetail : taskDetails) {
            System.out.println(taskDetail);
        }
    }

    @Test
    public void testCorn(){
        CronSequenceGenerator generator = new CronSequenceGenerator("0 40 6,15 * * *");
        Calendar instance = Calendar.getInstance();
        System.out.println(generator.next(System.currentTimeMillis()));

    }
}
