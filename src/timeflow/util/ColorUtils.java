package timeflow.util;

import java.awt.*;
import java.awt.image.*;

public class ColorUtils
{
	
	public static Color alpha(Color c, int a)
	{
		return new Color(c.getRed(), c.getGreen(), c.getBlue(),a);
	}
	
	public static Color interpolate(Color x, Color y, double u)
	{
		return new Color(interp(x.getRed(), y.getRed(), u), 
				         interp(x.getGreen(), y.getGreen(), u),
				         interp(x.getBlue(), y.getBlue(), u));
	}
	
	private static int interp(int x, int y, double u)
	{
		return (int)(y*u+x*(1-u));
	}
	
	public static float[] hsb(Color c)
	{
		return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), new float[3]);
	}
	
}
