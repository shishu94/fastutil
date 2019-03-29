package it.unimi.dsi.fastutil.ints;

/*
 * Copyright (C) 2017 Sebastiano Vigna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.io.BinIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.IntPredicate;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntArraySetTest {

	@SuppressWarnings("boxing")
	@Test
	public void testNullInEquals() {
		assertFalse(new IntArraySet(Arrays.asList(42)).equals(Collections.singleton(null)));
	}

	@Test
	public void testSet() {
		for(int i = 0; i <= 1; i++) {
			final IntArraySet s = i == 0 ? new IntArraySet() : new IntArraySet(new int[i]);
			assertTrue(s.add(1));
			assertEquals(1 + i, s.size());
			assertTrue(s.contains(1));
			assertTrue(s.add(2));
			assertTrue(s.contains(2));
			assertEquals(2 + i, s.size());
			assertFalse(s.add(1));
			assertFalse(s.remove(3));
			assertTrue(s.add(3));
			assertEquals(3 + i, s.size());
			assertTrue(s.contains(1));
			assertTrue(s.contains(2));
			assertTrue(s.contains(2));
			assertEquals(new IntOpenHashSet(i == 0 ? new int[] { 1, 2, 3 } : new int[] { 0, 1, 2, 3 }), new IntOpenHashSet(s.iterator()));
			assertTrue(s.remove(3));
			assertEquals(2 + i, s.size());
			assertTrue(s.remove(1));
			assertEquals(1 + i, s.size());
			assertFalse(s.contains(1));
			assertTrue(s.remove(2));
			assertEquals(0 + i, s.size());
			assertFalse(s.contains(1));
		}
	}

	@Test
	public void testClone() {
		IntArraySet s = new IntArraySet();
		assertEquals(s, s.clone());
		s.add(0);
		assertEquals(s, s.clone());
		s.add(0);
		assertEquals(s, s.clone());
		s.add(1);
		assertEquals(s, s.clone());
		s.add(2);
		assertEquals(s, s.clone());
		s.remove(0);
		assertEquals(s, s.clone());
	}

	@Test
	public void testSerialisation() throws IOException, ClassNotFoundException {
		IntArraySet s = new IntArraySet();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(s);
		oos.close();
		assertEquals(s, BinIO.loadObject(new ByteArrayInputStream(baos.toByteArray())));

		s.add(0);
		s.add(1);

		baos.reset();
		oos = new ObjectOutputStream(baos);
		oos.writeObject(s);
		oos.close();
		assertEquals(s, BinIO.loadObject(new ByteArrayInputStream(baos.toByteArray())));
	}

	@Test
	public void testRemove() {
		IntSet set = new IntArraySet(new int[] { 42 });

		IntIterator iterator = set.iterator();
		assertTrue(iterator.hasNext());
		iterator.nextInt();
		iterator.remove();
		assertFalse(iterator.hasNext());
		assertEquals(0, set.size());

		set = new IntArraySet(new int[] { 42, 43, 44 });

		iterator = set.iterator();
		assertTrue(iterator.hasNext());
		iterator.nextInt();
		iterator.nextInt();
		iterator.remove();
		assertEquals(44, iterator.nextInt ());
		assertFalse(iterator.hasNext());
		assertEquals(new IntArraySet(new int[] { 42, 44 }), set);
	}
	
	@Test
    public void testRemoveAll() {
	    IntSet l = new IntArraySet(new int[] { 0, 1, 1, 2 });
        l.removeAll(IntSets.singleton(1));
        assertEquals(new IntArraySet(new int[] { 0, 2 }), l);

        l = new IntArraySet(new int[] { 0, 1, 1, 2 });
        l.removeAll(Collections.singleton(Integer.valueOf(1)));
        assertEquals(new IntArraySet(new int[] { 0, 2 }), l);
    }
    
    @Test
    public void testRetainAll() {
        IntSet l = new IntArraySet(new int[] { 0, 1, 1, 2 });
        l.retainAll(IntSets.singleton(1));
        assertEquals(new IntArraySet(new int[] { 1 }), l);

        l = new IntArraySet(new int[] { 0, 1, 1, 2 });
        l.retainAll(Collections.singleton(Integer.valueOf(1)));
        assertEquals(new IntArraySet(new int[] { 1 }), l);
    }
    
    @Test
    public void testRemoveIf() {
        IntSet l = new IntArraySet(new int[] { 0, 1, 2, 3, 4, 5 });
        l.removeIf((int v) -> v % 2 == 0);
        assertEquals(new IntArraySet(new int[] { 1, 3, 5 }), l);

        l = new IntArraySet(new int[] { 0, 1, 2, 3, 4, 5 });
        l.removeIf((int v) -> v % 2 == 1);
        assertEquals(new IntArraySet(new int[] { 0, 2, 4 }), l);
    }
    
    @Test
    public void testForEach() {
        IntSet l = new IntArraySet(new int[] { 3, 4, 5, 6, 7 });
        LongAdder adder = new LongAdder();
        l.forEach((int i) -> adder.add(i));
        assertEquals(adder.intValue(), 25);
        
        l = new IntArraySet(new IntArrayList(new int[]{ 3, 4, 5, 5, 6, 6, 7 }));
        adder = new LongAdder();
        l.forEach((int i) -> adder.add(i));
        assertEquals(adder.intValue(), 25);
    }
}
