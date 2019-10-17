package cc.blynk.clickhouse.util;

import cc.blynk.clickhouse.settings.ClickHouseProperties;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;

public class ClickHouseRowBinaryInputStreamTest {

	@Test
	public void testUInt8() throws Exception {
		ClickHouseRowBinaryInputStream input = prepareStream(new byte[]{1, 0, 1, 0, -1, -128});

		assertEquals(input.readBoolean(), true);
		assertEquals(input.readBoolean(), false);
		assertEquals(input.readUInt8(), 1);
		assertEquals(input.readUInt8(), 0);
		assertEquals(input.readUInt8(), 255);
		assertEquals(input.readUInt8(), 128);
	}

	@Test
	public void testUInt8AsByte() throws Exception {
		ClickHouseRowBinaryInputStream input = prepareStream(new byte[]{1, 0, 1, 0, -1, -128});

		assertEquals(input.readUInt8AsByte(), (byte) 1);
		assertEquals(input.readUInt8AsByte(), (byte) 0);
		assertEquals(input.readUInt8AsByte(), (byte) 1);
		assertEquals(input.readUInt8AsByte(), (byte) 0);
		assertEquals(input.readUInt8AsByte(), (byte) 255);
		assertEquals(input.readUInt8AsByte(), (byte) 128);
	}

	@Test
	public void testUInt16() throws Exception {
		ClickHouseRowBinaryInputStream input = prepareStream(new byte[]{0, 0, -1, -1, 0, -128});

		assertEquals(input.readUInt16(), 0);
		assertEquals(input.readUInt16(), 65535);
		assertEquals(input.readUInt16(), 32768);
	}

	@Test
	public void testUInt16AsShort() throws Exception {
		ClickHouseRowBinaryInputStream input = prepareStream(new byte[]{0, 0, -1, -1, 0, -128});

		assertEquals(input.readUInt16AsShort(), (short) 0);
		assertEquals(input.readUInt16AsShort(), (short) 65535);
		assertEquals(input.readUInt16AsShort(), (short) 32768);
	}


	@Test
	public void testFloat64() throws Exception {
		ClickHouseRowBinaryInputStream input = prepareStream(new byte[]{0, 0, 0, 0, 0, 0, -8, 127});

		assertEquals(input.readFloat64(), Double.NaN);
	}

	@Test
	public void testUInt64AsLong() throws Exception {
		ClickHouseRowBinaryInputStream input = prepareStream(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1});

		assertEquals(input.readUInt64(), BigInteger.ZERO);
		assertEquals(input.readUInt64(), new BigInteger("18446744073709551615"));
	}


	@Test
	public void testOne() throws Exception {
		ClickHouseRowBinaryInputStream input = prepareStream(new byte[]{5, 97, 46, 98, 46, 99, 123, 20, -82, 71, -31, 26, 69, 64, 34, 87, -13, 88, 120, 67, 48, 116, -13, 88});

		assertEquals(input.readString(), "a.b.c");
		assertEquals(input.readFloat64(), 42.21);
		assertEquals(input.readUInt32(), 1492342562L);
		assertEquals(input.readDate(), new Date(117, 3, 16));
		assertEquals(input.readUInt32(), 1492350000L);
	}

	@Test
	public void testUnsignedLeb128() throws Exception {

		ClickHouseRowBinaryInputStream input = prepareStream(new byte[]{0});
		assertEquals(input.readUnsignedLeb128(), 0);

		input = prepareStream(new byte[]{-27, -114, 38});
		assertEquals(input.readUnsignedLeb128(), 624485);

		input = prepareStream(new byte[]{-128, -62, -41, 47});
		assertEquals(input.readUnsignedLeb128(), 100000000);
	}

	private ClickHouseRowBinaryInputStream prepareStream(byte[] input) {
		return new ClickHouseRowBinaryInputStream(new ByteArrayInputStream(input), TimeZone.getTimeZone("ETC"), new ClickHouseProperties());
	}
}
