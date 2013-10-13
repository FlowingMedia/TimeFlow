package timeflow.date.time.test;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import timeflow.data.time.Interval;

public class IntervalTest {

	@Test
	public void testInterval() {
		Interval i = new Interval(1L, 10L);
		assertEquals(i.start, 1L);
		assertEquals(i.end, 10L);
	}
	@Test
	public void testCopy(){
		Interval i = new Interval(1L, 10L);
		Interval j = new Interval(1L, 10L);
		Interval k = i.copy();
		assertEquals(j.start, k.start);
		assertEquals(j.end, k.end);
		
	}
	@Test
	public void testContains(){
		Interval i = new Interval(1L, 10L);
		assertTrue(i.contains(1L));
		assertTrue(i.contains(2L));
		assertTrue(i.contains(3L));
		assertTrue(i.contains(4L));
		assertTrue(i.contains(5L));
		assertTrue(i.contains(6L));
		assertTrue(i.contains(7L));
		assertTrue(i.contains(8L));
		assertTrue(i.contains(9L));
		assertTrue(i.contains(10L));
		assertFalse(i.contains(11L));
		assertFalse(i.contains(12L));
	}
	@Test
	public void testIntersects1(){
		Interval i = new Interval(3L, 10L);
		Interval x = new Interval(5L, 6L);
		Interval y = new Interval(1L, 12L);
		Interval z = new Interval(1L, 2L);
		assertTrue(i.intersects(x));
		assertTrue(i.intersects(y));
		assertFalse(i.intersects(z));
		
		
	}
	@Test
	public void testIntersects2(){
		Interval i = new Interval(3L, 10L);
		assertTrue(i.intersects(6L, 8L));
		assertTrue(i.intersects(1L, 12L));
		assertFalse(i.intersects(1L, 2L));
		
	}
	@Test
	public void testSubInterval(){
		Interval i = new Interval(3L, 10L);
		Interval j =i.subinterval(2, 5);
		assertEquals(17L, j.start);
		assertEquals(38L, j.end);
	}
	@Test
	public void testSetTo1(){
		Interval i = new Interval(3L, 10L);
		assertEquals(i.start, 3L);
		assertEquals(i.end, 10L);
		i.setTo(1L,9L);
		assertEquals(i.start, 1L);
		assertEquals(i.end, 9L);
		
	}
	@Test
	public void testSetTo2(){
		Interval i = new Interval(3L, 10L);
		assertEquals(i.start, 3L);
		assertEquals(i.end, 10L);
		Interval x = new Interval(5L, 6L);
		i.setTo(x);
		assertEquals(i.start, 5L);
		assertEquals(i.end, 6L);
	}
	@Test
	public void testInclude1(){
		Interval i = new Interval(3L, 10L);
		i.include(11L);
		assertTrue(i.contains(11L));
		assertEquals(i.end, 11L);
		i.include(5L);
		assertEquals(i.start, 3L);
		assertEquals(i.end, 11L);
		i.include(1L);
		assertEquals(i.start, 1L);
		assertTrue(i.contains(1L));
		assertFalse(i.contains(12L));	
	}
	@Test
	public void testInclude2(){
		Interval i = new Interval(3L, 10L);
		Interval x = new Interval(5L, 6L);
		assertFalse(x.contains(3L));
		assertFalse(x.contains(4L));
		assertFalse(x.contains(8L));
		x.include(i);
		assertEquals(x.start, 3L);
		assertEquals(x.end, 10L);
		assertTrue(x.contains(3L));
		assertTrue(x.contains(9L));
	}
	@Test
	public void testExpand(){
		Interval i = new Interval(3L, 10L);
		i.expand(1L);
		assertEquals(i.start, 2L);
		assertEquals(i.end, 11L);
	}
	@Test
	public void testAdd(){
		Interval i = new Interval(3L, 10L);
		i.add(1L);
		assertEquals(i.start, 4L);
		assertEquals(i.end, 11L);
	}
	@Test
	public void testLength(){
		Interval i = new Interval(3L, 10L);
		assertEquals(i.length(), 7L);
	}
	@Test
	public void testTranslateTo(){
		Interval i = new Interval(3L, 10L);
		i.translateTo(5L);
		assertEquals(i.start, 5L);
		assertEquals(i.end, 12L);
		assertEquals(i.length(), 7L);
	}
	@Test
	public void testIntersection(){
		Interval i = new Interval(3L, 10L);
		Interval j = new Interval(5L, 11L);
		i.intersection(j);
		assertEquals(i.start, 5L);
		assertEquals(i.end, 10L);
		assertTrue(i.contains(5L));
		assertFalse(i.contains(11L));
	}
	@Test
	public void testClampInside(){
		Interval i = new Interval(3L, 9L);
		Interval j = new Interval(5L, 11L);
		i.clampInside(j);
		assertEquals(i.start, 5L);
		assertEquals(i.end, 11L);
		assertTrue(i.intersects(j));
	}
	@Test
	public void testToString(){
		Interval i = new Interval(3L, 9L);
		String x = "[Interval: From "+new Date(3L)+" to "+new Date(9L)+"]";
		assertEquals(x, i.toString());
	}

}
