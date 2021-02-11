/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - MPJ Version 1.0         *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         *
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                 Original version of this code by                        *
*            Florian Doyon (Florian.Doyon@sophia.inria.fr)                *
*              and  Wilfried Klauser (wklauser@acm.org)                   *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package jgf_mpj_benchmarks.section3.raytracer;


//package raytracer; 

import mpi.*;

public class RayTracer { 


  Scene scene;
  /**
   * Lights for the rendering scene
   */
  Light lights[];
  
  /**
   * Objects (spheres) for the rendering scene
   */
  Primitive prim[];
  

  /**
   * The view for the rendering scene
   */
  View view;
  
  /**
   * Temporary ray
   */
   Ray tRay=new Ray();

  /**
   * Alpha channel
   */
  static final int alpha=255<<24;
    
  /**
   * Null vector (for speedup, instead of <code>new Vec(0,0,0)</code>
   */
  static final Vec voidVec=new Vec();
    
  /**
   * Temporary vect
   */
  Vec L = new Vec();
    
  /**
   * Current intersection instance (only one is needed!)
   */
  Isect inter=new Isect();
    
  /**
   * Height of the <code>Image</code> to be rendered
   */
  int height;

  /**
   * Width of the <code>Image</code> to be rendered
   */
  int width;
 
  int datasizes[] = {150,500};    

  long checksum = 0; 

  /* create a temporary checksum array for MPI */

  double [] tmp_checksum = new double[1];
 
  int size; 

  int numobjects; 

  /**
   * Create and initialize the scene for the rendering picture.
   * @return The scene just created
   */

  Scene createScene() {
    int x = 0;
    int y = 0;
    
    Scene scene = new Scene();
   

    /* create spheres */ 

    Primitive p; 
    int nx = 4;
    int ny = 4;
    int nz = 4;
      for (int i = 0; i<nx; i++) {
	for (int j = 0; j<ny; j++) {
	  for (int k = 0; k<nz; k++) {
              double xx = 20.0 / (nx - 1) * i - 10.0; 
              double yy = 20.0 / (ny - 1) * j - 10.0; 
              double zz = 20.0 / (nz - 1) * k - 10.0; 
	      
	      p = new Sphere(new Vec(xx,yy,zz), 3);
	      //p.setColor(i/(double) (nx-1), j/(double)(ny-1), k/(double) (nz-1));
	      p.setColor(0, 0, (i+j)/(double) (nx+ny-2));
	      p.surf.shine = 15.0;
	      p.surf.ks = 1.5 - 1.0;
	      p.surf.kt= 1.5 - 1.0;
	      scene.addObject(p);
	  }
	}
      }
  

      
    /* Creates five lights for the scene */
    scene.addLight(new Light(100, 100, -50, 1.0));
    scene.addLight(new Light(-100, 100, -50, 1.0));
    scene.addLight(new Light(100,-100,-50, 1.0));
    scene.addLight(new Light(-100,-100,-50, 1.0));
    scene.addLight(new Light(200, 200, 0, 1.0));

    /* Creates a View (viewing point) for the rendering scene */
    View v = new View(  new Vec(x, 20, -30),
                        new Vec(x, y, 0),
                        new Vec(0, 1, 0),
                        1.0,
			35.0 * 3.14159265 / 180.0,
                        1.0);
/*
    v.from = new Vec(x, y, -30);
    v.at = new Vec(x, y, -15);
    v.up = new Vec(0, 1, 0);
    v.angle = 35.0 * 3.14159265 / 180.0;
    v.aspect = 1.0; 
    v.dist = 1.0;
    
    */
    scene.setView(v);
    
    return scene;
  }


public void setScene(Scene scene)
    {
    // Get the objects count
    int nLights = scene.getLights();
    int nObjects = scene.getObjects();
    
    lights = new Light[nLights];
    prim = new Primitive[nObjects];
    
    // Get the lights
    for (int l = 0; l < nLights; l++) {
      lights[l]=scene.getLight(l);
    }

    // Get the primitives
    for (int o = 0; o < nObjects; o++) {
      prim[o]=scene.getObject(o);
    }
    
    // Set the view
    view = scene.getView();
  }

public void render(Interval interval) throws MPIException
    {  
  
  // Screen variables

    int row[] = null;

    if(JGFRayTracerBench.rank==0){
      row = new int[interval.width * (interval.yto-interval.yfrom)];
    }
    int p_row[] = new int[((((interval.width * (interval.yto-interval.yfrom))
                  /interval.width)+ JGFRayTracerBench.nprocess-1) / 
                  JGFRayTracerBench.nprocess)*interval.width];

    int pixCounter=0; //iterator
    int t_count; // temporary counter
 
  // Rendering variables
    int x, y, red, green, blue;
    double xlen, ylen;    
    Vec viewVec;
    

    viewVec = Vec.sub(view.at, view.from);
    
    viewVec.normalize();
    
    Vec tmpVec = new Vec(viewVec);
    tmpVec.scale(Vec.dot(view.up, viewVec));
    
    Vec upVec = Vec.sub(view.up, tmpVec);
    upVec.normalize();
      
    Vec leftVec = Vec.cross(view.up, viewVec);
    leftVec.normalize();

    double frustrumwidth = view.dist * Math.tan(view.angle);
      
    upVec.scale(-frustrumwidth);
    leftVec.scale(view.aspect * frustrumwidth);
      
    Ray r = new Ray(view.from, voidVec);    
    Vec col = new Vec();
      
    // Header for .ppm file 
    // System.out.println("P3"); 
    // System.out.println(width + " " + height);
    // System.out.println("255"); 
 

    // All loops are reversed for 'speedup' (cf. thinking in java p331)
    
    // For each line
    for(y = interval.yfrom+JGFRayTracerBench.rank; y < interval.yto; y+=JGFRayTracerBench.nprocess) {
      ylen = (double)(2.0 * y) / (double)interval.width - 1.0;
      // System.out.println("Doing line " + y);
      // For each pixel of the line
      for(x = 0; x < interval.width; x++) {
	xlen = (double)(2.0 * x) / (double)interval.width - 1.0;
	r.D = Vec.comb(xlen, leftVec, ylen, upVec);
	r.D.add(viewVec);
	r.D.normalize();
	col = trace(0, 1.0, r);
	
	// computes the color of the ray
	red = (int)(col.x * 255.0);
	if (red > 255)
	  red = 255;
	green = (int)(col.y * 255.0);
	if (green > 255)
	  green = 255;
	blue = (int)(col.z * 255.0);
	if (blue > 255)
	  blue = 255;

        checksum += red;
        checksum += green;
        checksum += blue;

	// RGB values for .ppm file 
        // System.out.println(red + " " + green + " " + blue); 
	// Sets the pixels
	p_row[pixCounter++] =  alpha | (red << 16) | (green << 8) | (blue);
      } // end for (x)
    } // end for (y)

/* carry out a global sum on checksum */

    tmp_checksum[0] = (double) checksum;
    MPI.COMM_WORLD.Reduce(tmp_checksum,0,tmp_checksum,0,1,MPI.DOUBLE,MPI.SUM,0);
    if(JGFRayTracerBench.rank==0) {
     checksum = (long) tmp_checksum[0];
    }

/* send temporary copies of p_row back to row */ 

    if(JGFRayTracerBench.rank==0) {
     for(int k=0;k<JGFRayTracerBench.nprocess;k++){
       if(k!=0){
         MPI.COMM_WORLD.Recv(p_row,0,p_row.length,MPI.INT,k,k);
       }
       t_count = 0;
       for(int i = k; i < (interval.yto-interval.yfrom); i+=JGFRayTracerBench.nprocess){
         for(x = 0; x < interval.width; x++) {
           row[i*interval.width+x] = p_row[t_count];
           t_count++;
         }
       }
     }
    } else {
      MPI.COMM_WORLD.Send(p_row,0,p_row.length,MPI.INT,0,JGFRayTracerBench.rank);
    }

    }

  boolean intersect(Ray r, double maxt) {
    Isect tp;
    int i, nhits;
    
    nhits = 0;
    inter.t = 1e9;
    for(i = 0; i < prim.length; i++) {
      // uses global temporary Prim (tp) as temp.object for speedup
      tp = prim[i].intersect(r);
      if (tp != null && tp.t < inter.t) {
	inter.t = tp.t;
	inter.prim = tp.prim;
	inter.surf = tp.surf;
	inter.enter = tp.enter;
	nhits++;
      }
    }
    return nhits > 0 ? true : false;
  }
  
  /**
   * Checks if there is a shadow
   * @param r The ray
   * @return Returns 1 if there is a shadow, 0 if there isn't
   */
  int Shadow(Ray r, double tmax) {
    if (intersect(r, tmax))
      return 0;
    return 1;
  }
  

  /**
   * Return the Vector's reflection direction
   * @return The specular direction
   */
  Vec SpecularDirection(Vec I, Vec N) {
    Vec r;
    r = Vec.comb(1.0/Math.abs(Vec.dot(I, N)), I, 2.0, N);
    r.normalize();
    return r;
  }
  
  /**
   * Return the Vector's transmission direction
   */
  Vec TransDir(Surface m1, Surface m2, Vec I, Vec N) {
    double n1, n2, eta, c1, cs2;
    Vec r;
    n1 = m1 == null ? 1.0 : m1.ior;
    n2 = m2 == null ? 1.0 : m2.ior;
    eta = n1/n2;
    c1 = -Vec.dot(I, N);
    cs2 = 1.0 - eta * eta * (1.0 - c1 * c1);
    if (cs2 < 0.0)
      return null;
    r = Vec.comb(eta, I, eta * c1 - Math.sqrt(cs2), N);
    r.normalize();
    return r;
  }
  

  /**
   * Returns the shaded color
   * @return The color in Vec form (rgb)
   */
  Vec shade(int level, double weight, Vec P, Vec N, Vec I, Isect hit) {
    double n1, n2, eta, c1, cs2;
    Vec r;
    Vec tcol;
    Vec R;
    double t, diff, spec;
    Surface surf;
    Vec col;
    int l;
    
    col = new Vec();
    surf = hit.surf;
    R = new Vec();
    if (surf.shine > 1e-6) {
      R = SpecularDirection(I, N);
    }

    // Computes the effectof each light
    for(l = 0; l < lights.length; l++) {
      L.sub2(lights[l].pos, P);
      if (Vec.dot(N, L) >= 0.0) {
	t = L.normalize();

	tRay.P=P;
	tRay.D=L;

	// Checks if there is a shadow
	if (Shadow(tRay, t) > 0) {
	  diff = Vec.dot(N, L) * surf.kd *
	    lights[l].brightness;

	  col.adds(diff,surf.color);
	  if (surf.shine > 1e-6) {
	    spec = Vec.dot(R, L);
	    if (spec > 1e-6) {
	      spec = Math.pow(spec, surf.shine);
	      col.x += spec;
	      col.y += spec;
	      col.z += spec;
	    }
	  }
	}
      } // if
    } // for
    
    tRay.P=P;
    if (surf.ks * weight > 1e-3) {
      tRay.D = SpecularDirection(I, N);
      tcol = trace(level + 1, surf.ks * weight, tRay);
      col.adds(surf.ks, tcol);
    }
    if (surf.kt * weight > 1e-3) {
      if (hit.enter > 0)
	tRay.D = TransDir(null, surf, I, N);
      else
	tRay.D = TransDir(surf, null, I, N);
      tcol = trace(level + 1, surf.kt * weight, tRay);
      col.adds(surf.kt, tcol);
    }

    // garbaging...
    tcol=null;
    surf=null;

    return col;
  }
  
  /**
   * Launches a ray
   */
  Vec trace (int level, double weight, Ray r) {
    Vec P, N;
    boolean hit;
    
    // Checks the recursion level
    if (level > 6) {
      return new Vec();
    }
    
    hit = intersect(r, 1e6);
    if (hit) {
      P = r.point(inter.t);
      N = inter.prim.normal(P);
      if (Vec.dot(r.D, N) >= 0.0) {
	N.negate();
      }
      return shade(level, weight, P, N, r.D, inter);
    }
    // no intersection --> col = 0,0,0
    return voidVec;
  }


  public static void main(String argv[]) throws MPIException{


      RayTracer rt = new RayTracer(); 

    // create the objects to be rendered 
      rt.scene = rt.createScene(); 

    // get lights, objects etc. from scene. 
      rt.setScene(rt.scene); 

    // Set interval to be rendered to the whole picture 
    // (overkill, but will be useful to retain this for parallel versions)
      Interval interval = new Interval(0,rt.width,rt.height,0,rt.height,1); 

    // Do the business!
      rt.render(interval); 

  }


}
