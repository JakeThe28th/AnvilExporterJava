package mc2obj;

import java.math.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Quad {
	public Double x1 = (double) 0;
	public Double y1 = (double) 0;
	public Double z1 = (double) 0;
	
	public Double x2 = (double) 0;
	public Double y2 = (double) 0;
	public Double z2 = (double) 0;
	
	public Double x3 = (double) 0;
	public Double y3 = (double) 0;
	public Double z3 = (double) 0;
	
	public Double x4 = (double) 0;
	public Double y4 = (double) 0;
	public Double z4 = (double) 0;
	
	public Quad(Double x1_, Double y1_, Double z1_, 
			Double x2_, Double y2_, Double z2_,
			Double x3_, Double y3_, Double z3_,
			Double x4_, Double y4_, Double z4_) {
		
	x1 = x1_;
	y1 = y1_;
	z1 = z1_;
	
	x2 = x2_;
	y2 = y2_;
	z2 = z2_;
	
	x3 = x3_;
	y3 = y3_;
	z3 = z3_;
	
	x4 = x4_;
	y4 = y4_;
	z4 = z4_;
		
	//System.out.prlongln(x1);
	
	}
	
	public void rotate(double xx, double yy, double zz, double xx_rot, double yy_rot, double zz_rot) {
		
		System.out.println(xx);
		System.out.println(yy);
		System.out.println(zz);
		
		
		double sinTheta = Math.sin(yy_rot);
		double cosTheta = Math.cos(yy_rot);
	
		double[] pt = {x1, y1};
		AffineTransform.getRotateInstance(Math.toRadians(yy_rot), xx, yy)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x1 = pt[0];
		y1 = pt[1];
		
		pt[0] = x2;
		pt[1] = y2;
		AffineTransform.getRotateInstance(Math.toRadians(yy_rot), xx, yy)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x2 = pt[0];
		y2 = pt[1];
		
		pt[0] = x3;
		pt[1] = y3;
		AffineTransform.getRotateInstance(Math.toRadians(yy_rot), xx, yy)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x3 = pt[0];
		y3 = pt[1];
		
		pt[0] = x4;
		pt[1] = y4;
		AffineTransform.getRotateInstance(Math.toRadians(yy_rot), xx, yy)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x4 = pt[0];
		y4 = pt[1];
		
		//
		
		sinTheta = Math.sin(zz_rot);
		cosTheta = Math.cos(zz_rot);
	
		pt[0] = x1;
		pt[1] = z1;
		AffineTransform.getRotateInstance(Math.toRadians(zz_rot), xx, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x1 = pt[0];
		z1 = pt[1];
		
		pt[0] = x2;
		pt[1] = z2;
		AffineTransform.getRotateInstance(Math.toRadians(zz_rot), xx, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x2 = pt[0];
		z2 = pt[1];
		
		pt[0] = x3;
		pt[1] = z3;
		AffineTransform.getRotateInstance(Math.toRadians(zz_rot), xx, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x3 = pt[0];
		z3 = pt[1];
		
		pt[0] = x4;
		pt[1] = z4;
		AffineTransform.getRotateInstance(Math.toRadians(zz_rot), xx, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		x4 = pt[0];
		z4 = pt[1];
		
//
		
		sinTheta = Math.sin(xx_rot);
		cosTheta = Math.cos(xx_rot);
	
		pt[0] = y1;
		pt[1] = z1;
		AffineTransform.getRotateInstance(Math.toRadians(zz_rot), yy, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		y1 = pt[0];
		z1 = pt[1];
		
		pt[0] = y2;
		pt[1] = z2;
		AffineTransform.getRotateInstance(Math.toRadians(zz_rot), yy, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		y2 = pt[0];
		z2 = pt[1];
		
		pt[0] = y3;
		pt[1] = z3;
		AffineTransform.getRotateInstance(Math.toRadians(zz_rot), yy, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		y3 = pt[0];
		z3 = pt[1];
		
		pt[0] = y4;
		pt[1] = z4;
		AffineTransform.getRotateInstance(Math.toRadians(zz_rot), yy, zz)
		  .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		y4 = pt[0];
		z4 = pt[1];
		
		//( thank you https://stackoverflow.com/questions/9985473/java-rotate-point-around-another-by-specified-degree-value)
		
		System.out.println(cosTheta);
		System.out.println(sinTheta);
	}
}