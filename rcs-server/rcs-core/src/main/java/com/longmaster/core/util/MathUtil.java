package com.longmaster.core.util;

import java.math.BigDecimal;


public class MathUtil {
	private static final int DEF_DIV_SCALE = 4;// 保留小数点之后多少位

	// 加
	public static double add(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.add(b2).doubleValue();

	}

	// 减
	public static double sub(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2).doubleValue();

	}

	// 乘
	public static double mul(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.multiply(b2).doubleValue();

	}

	// 除
	public static double div(double d1, double d2) {
		return div(d1, d2, DEF_DIV_SCALE);
	}

	// 除 scale 表示保留小数点之后多少位
	public static double div(double d1, double d2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();// BigDecimal.ROUND_HALF_UP表示四舍五入
	}

}
