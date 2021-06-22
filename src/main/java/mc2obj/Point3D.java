package mc2obj;

import java.math.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Point3D {
	public Double x = (double) 0;
	public Double y = (double) 0;
	public Double z = (double) 0;
	
	public Point3D(Double x1_, Double y1_, Double z1_) {
		
		x = x1_;
		y = y1_;
		z = z1_;
		}
	
	public void rotate(double xx, double yy, double zz, double xx_rot, double yy_rot, double zz_rot) {
		
		//System.out.println(xx);
		//System.out.println(yy);
		//System.out.println(zz);
		
		
		double sinTheta = Math.sin(yy_rot);
		double cosTheta = Math.cos(yy_rot);
	
		double[] pt = {x, y};
		AffineTransform.getRotateInstance(Math.toRadians(yy_rot), xx, yy)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x = pt[0];
		y = pt[1];
		
		//
		
		sinTheta = Math.sin(zz_rot);
		cosTheta = Math.cos(zz_rot);
	
		pt[0] = x;
		pt[1] = z;
		AffineTransform.getRotateInstance(Math.toRadians(zz_rot), xx, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x = pt[0];
		z = pt[1];
		
		//
		
		sinTheta = Math.sin(xx_rot);
		cosTheta = Math.cos(xx_rot);
	
		pt[0] = y;
		pt[1] = z;
		AffineTransform.getRotateInstance(Math.toRadians(xx_rot), yy, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		y = pt[0];
		z = pt[1];
		
		
		//( thank you https://stackoverflow.com/questions/9985473/java-rotate-point-around-another-by-specified-degree-value)
		
		//System.out.println(cosTheta);
		//System.out.println(sinTheta);
	}
}