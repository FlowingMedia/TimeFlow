package timeflow.date.time.test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import timeflow.data.time.TimeUtils;

public class TimeUtilsTest {

	@Test
	public void testCal1() {
		Calendar c=new GregorianCalendar();
		c.setTimeInMillis(5L);
		assertEquals(c, TimeUtils.cal(5L));
	}
	@Test
	public void testCal2(){
		Calendar c=new GregorianCalendar();
		@SuppressWarnings("deprecation")
		Date date = new Date(95, 10, 10);
		c.setTime(date);
		assertEquals(c, TimeUtils.cal(date));
	}
}
