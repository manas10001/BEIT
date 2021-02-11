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
*                  Original version of this code by                       *
*                         Dieter Heermann                                 * 
*                       converted to Java by                              *
*                Lorna Smith  (l.smith@epcc.ed.ac.uk)                     *
*                   (see copyright notice below)                          *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package jgf_mpj_benchmarks.section3.moldyn;



//package moldyn;

import java.util.*;
import java.text.NumberFormat;
import mpi.*;

public class md {

  public static final int ITERS = 100;
  public static final double LENGTH = 50e-10;
  public static final double m = 4.0026;
  public static final double mu = 1.66056e-27;
  public static final double kb = 1.38066e-23;
  public static final double TSIM = 50;
  public static final double deltat = 5e-16;
  public static particle one [] = null;
  public static double epot = 0.0;
  public static double vir = 0.0;
  public static double count = 0.0;
  int size;
  int datasizes[] = {8,13};

  public static int interactions = 0;

  int i,j,k,lg,mdsize,move,mm;

  double l,rcoff,rcoffs,side,sideh,hsq,hsq2,vel; 
  double a,r,sum,tscale,sc,ekin,ek,ts,sp;    
  double den = 0.83134;
  double tref = 0.722;
  double h = 0.064;
  double vaver,vaverh,rand;
  double etot,temp,pres,rp;
  double u1,u2,v1,v2,s;

  double [] tmp_xforce;
  double [] tmp_yforce;
  double [] tmp_zforce;

  double [] tmp_epot;
  double [] tmp_vir;
  int [] tmp_interactions;
  //double[] tmp_interactions; 

  int ijk,npartm,PARTSIZE,iseed,tint;
  int irep = 10;
  int istop = 19;
  int iprint = 10;
  int movemx = 50;

  random randnum;

  public void initialise() {

/* Parameter determination */

    mm = datasizes[size];
    PARTSIZE = mm*mm*mm*4;
    mdsize = PARTSIZE;
    one = new particle [mdsize];
    l = LENGTH;
   
    side = Math.pow((mdsize/den),0.3333333);
    rcoff = mm/4.0;

    a = side/mm;
    sideh = side*0.5;
    hsq = h*h;
    hsq2 = hsq*0.5;
    npartm = mdsize - 1;
    rcoffs = rcoff * rcoff;
    tscale = 16.0 / (1.0 * mdsize - 1.0);
    vaver = 1.13 * Math.sqrt(tref / 24.0);
    vaverh = vaver * h;

/* temporary arrays for MPI operations */

    tmp_xforce = new double [mdsize];
    tmp_yforce = new double [mdsize];
    tmp_zforce = new double [mdsize];

    tmp_epot = new double[1];
    tmp_vir = new double[1];
    tmp_interactions = new int[1];


/* Particle Generation */

    ijk = 0;
    for (lg=0; lg<=1; lg++) {
     for (i=0; i<mm; i++) {
      for (j=0; j<mm; j++) {
       for (k=0; k<mm; k++) {
        one[ijk] = new particle((i*a+lg*a*0.5),(j*a+lg*a*0.5),(k*a),
        0.0,0.0,0.0,0.0,0.0,0.0);
        ijk = ijk + 1;
       }
      }
     }
    }
    for (lg=1; lg<=2; lg++) {
     for (i=0; i<mm; i++) {
      for (j=0; j<mm; j++) {
       for (k=0; k<mm; k++) {
        one[ijk] = new particle((i*a+(2-lg)*a*0.5),(j*a+(lg-1)*a*0.5),
        (k*a+a*0.5),0.0,0.0,0.0,0.0,0.0,0.0);
        ijk = ijk + 1;
       }
      }
     }
    }

/* Initialise velocities */

    iseed = 0;
    v1 = 0.0;
    v2 = 0.0;

    randnum = new random(iseed,v1,v2);

    for (i=0; i<mdsize; i+=2) {
     r  = randnum.seed();
     one[i].xvelocity = r*randnum.v1;
     one[i+1].xvelocity  = r*randnum.v2;
    }

    for (i=0; i<mdsize; i+=2) {
     r  = randnum.seed();
     one[i].yvelocity = r*randnum.v1;
     one[i+1].yvelocity  = r*randnum.v2;
    }

    for (i=0; i<mdsize; i+=2) {
     r  = randnum.seed();
     one[i].zvelocity = r*randnum.v1;
     one[i+1].zvelocity  = r*randnum.v2;
    }

/* velocity scaling */

    ekin = 0.0;
    sp = 0.0;

    for(i=0;i<mdsize;i++) {
     sp = sp + one[i].xvelocity;
    }
    sp = sp / mdsize;

    for(i=0;i<mdsize;i++) {
     one[i].xvelocity = one[i].xvelocity - sp;
     ekin = ekin + one[i].xvelocity*one[i].xvelocity;
    }

    sp = 0.0;
    for(i=0;i<mdsize;i++) {
     sp = sp + one[i].yvelocity;
    }
    sp = sp / mdsize;

    for(i=0;i<mdsize;i++) {
     one[i].yvelocity = one[i].yvelocity - sp;
     ekin = ekin + one[i].yvelocity*one[i].yvelocity;
    }

    sp = 0.0;
    for(i=0;i<mdsize;i++) {
     sp = sp + one[i].zvelocity;
    }
    sp = sp / mdsize;

    for(i=0;i<mdsize;i++) {
     one[i].zvelocity = one[i].zvelocity - sp;
     ekin = ekin + one[i].zvelocity*one[i].zvelocity;
    }

    ts = tscale * ekin;
    sc = h * Math.sqrt(tref/ts);

    for(i=0;i<mdsize;i++) {

    one[i].xvelocity = one[i].xvelocity * sc;     
    one[i].yvelocity = one[i].yvelocity * sc;     
    one[i].zvelocity = one[i].zvelocity * sc;     

    }

/* MD simulation */

  }

  public void runiters() throws MPIException{

   move = 0;
   for (move=0;move<movemx;move++) {

    for (i=0;i<mdsize;i++) {
     one[i].domove(side);        /* move the particles and update velocities */
    }

    epot = 0.0;
    vir = 0.0;

    MPI.COMM_WORLD.Barrier();

    for (i=0+JGFMolDynBench.rank;i<mdsize;i+=JGFMolDynBench.nprocess) {
     one[i].force(side,rcoff,mdsize,i);  /* compute forces */
    }

    MPI.COMM_WORLD.Barrier();

/* global reduction on partial sums of the forces, epot, vir and interactions */ 


    for (i=0;i<mdsize;i++) {
      tmp_xforce[i] = one[i].xforce; 
      tmp_yforce[i] = one[i].yforce; 
      tmp_zforce[i] = one[i].zforce; 
    }

    MPI.COMM_WORLD.Allreduce(tmp_xforce,0,tmp_xforce,0,mdsize,MPI.DOUBLE,MPI.SUM);
    MPI.COMM_WORLD.Allreduce(tmp_yforce,0,tmp_yforce,0,mdsize,MPI.DOUBLE,MPI.SUM);
    MPI.COMM_WORLD.Allreduce(tmp_zforce,0,tmp_zforce,0,mdsize,MPI.DOUBLE,MPI.SUM);

    for (i=0;i<mdsize;i++) {
      one[i].xforce = tmp_xforce[i]; 
      one[i].yforce = tmp_yforce[i];
      one[i].zforce = tmp_zforce[i];
    }

    tmp_epot[0] = epot; 
    tmp_vir[0] = vir; 
    tmp_interactions[0] = interactions; 

    MPI.COMM_WORLD.Allreduce(tmp_epot,0,tmp_epot,0,1,MPI.DOUBLE,MPI.SUM);
    MPI.COMM_WORLD.Allreduce(tmp_vir,0,tmp_vir,0,1,MPI.DOUBLE,MPI.SUM);
    MPI.COMM_WORLD.Allreduce(tmp_interactions,0,tmp_interactions,0,1,MPI.INT,MPI.SUM);

    epot = tmp_epot[0]; 
    vir = tmp_vir[0]; 
    interactions = tmp_interactions[0]; 

    MPI.COMM_WORLD.Barrier();

    sum = 0.0;

    for (i=0;i<mdsize;i++) {
     sum = sum + one[i].mkekin(hsq2);    /*scale forces, update velocities */
    }

    ekin = sum/hsq;

    vel = 0.0;
    count = 0.0;

    for (i=0;i<mdsize;i++) {
     vel = vel + one[i].velavg(vaverh,h); /* average velocity */
    }

    vel = vel / h;

/* tmeperature scale if required */

    if((move < istop) && (((move+1) % irep) == 0)) {
     sc = Math.sqrt(tref / (tscale*ekin));
     for (i=0;i<mdsize;i++) {
      one[i].dscal(sc,1);
     }
     ekin = tref / tscale;
    }

/* sum to get full potential energy and virial */

    if(((move+1) % iprint) == 0) {
     ek = 24.0*ekin;
     epot = 4.0*epot;
     etot = ek + epot;
     temp = tscale * ekin;
     pres = den * 16.0 * (ekin - vir) / mdsize;
     vel = vel / mdsize; 
     rp = (count / mdsize) * 100.0;
    }

   }



  }




}


class particle {

  public double xcoord, ycoord, zcoord;
  public double xvelocity,yvelocity,zvelocity;
  public double xforce,yforce,zforce;

  public particle(double xcoord, double ycoord, double zcoord, double xvelocity,
                  double yvelocity,double zvelocity,double xforce, 
                  double yforce, double zforce) {

   this.xcoord = xcoord; 
   this.ycoord = ycoord; 
   this.zcoord = zcoord;
   this.xvelocity = xvelocity;
   this.yvelocity = yvelocity;
   this.zvelocity = zvelocity;
   this.xforce = xforce;
   this.yforce = yforce;
   this.zforce = zforce;

  }

  public void domove(double side) {

    xcoord = xcoord + xvelocity + xforce;
    ycoord = ycoord + yvelocity + yforce;
    zcoord = zcoord + zvelocity + zforce;

    if(xcoord < 0) { xcoord = xcoord + side; } 
    if(xcoord > side) { xcoord = xcoord - side; }
    if(ycoord < 0) { ycoord = ycoord + side; }
    if(ycoord > side) { ycoord = ycoord - side; }
    if(zcoord < 0) { zcoord = zcoord + side; }
    if(zcoord > side) { zcoord = zcoord - side; }

    xvelocity = xvelocity + xforce;
    yvelocity = yvelocity + yforce;
    zvelocity = zvelocity + zforce;

    xforce = 0.0;
    yforce = 0.0;
    zforce = 0.0;

  }

  public void force(double side, double rcoff,int mdsize,int x) {

    double sideh;
    double rcoffs;

    double xx,yy,zz,xi,yi,zi,fxi,fyi,fzi;
    double rd,rrd,rrd2,rrd3,rrd4,rrd6,rrd7,r148;
    double forcex,forcey,forcez;

    int i;

    sideh = 0.5*side; 
    rcoffs = rcoff*rcoff;

     xi = xcoord;
     yi = ycoord;
     zi = zcoord;
     fxi = 0.0;
     fyi = 0.0;
     fzi = 0.0;

       for (i=x+1;i<mdsize;i++) {  
        xx = xi - md.one[i].xcoord;
        yy = yi - md.one[i].ycoord;
        zz = zi - md.one[i].zcoord;

        if(xx < (-sideh)) { xx = xx + side; }
        if(xx > (sideh))  { xx = xx - side; }
        if(yy < (-sideh)) { yy = yy + side; }
        if(yy > (sideh))  { yy = yy - side; }
        if(zz < (-sideh)) { zz = zz + side; }
        if(zz > (sideh))  { zz = zz - side; }

        rd = xx*xx + yy*yy + zz*zz;

        if(rd <= rcoffs) {
           rrd = 1.0/rd;
           rrd2 = rrd*rrd;
           rrd3 = rrd2*rrd;
           rrd4 = rrd2*rrd2;
           rrd6 = rrd2*rrd4;
           rrd7 = rrd6*rrd;
           md.epot = md.epot + (rrd6 - rrd3);
           r148 = rrd7 - 0.5*rrd4;
           md.vir = md.vir - rd*r148;
           forcex = xx * r148;
           fxi = fxi + forcex;
           md.one[i].xforce = md.one[i].xforce - forcex;
           forcey = yy * r148;
           fyi = fyi + forcey;
           md.one[i].yforce = md.one[i].yforce - forcey;
           forcez = zz * r148;
           fzi = fzi + forcez;
           md.one[i].zforce = md.one[i].zforce - forcez; 
           md.interactions++;
        }

       } 

     xforce = xforce + fxi;
     yforce = yforce + fyi;
     zforce = zforce + fzi;

  }

  public double mkekin(double hsq2) {

    double sumt = 0.0; 

    xforce = xforce * hsq2;
    yforce = yforce * hsq2;
    zforce = zforce * hsq2;
    
    xvelocity = xvelocity + xforce; 
    yvelocity = yvelocity + yforce; 
    zvelocity = zvelocity + zforce; 

    sumt = (xvelocity*xvelocity)+(yvelocity*yvelocity)+(zvelocity*zvelocity);
    return sumt;
  }

  public double velavg(double vaverh,double h) {
 
    double velt;
    double sq;

    sq = Math.sqrt(xvelocity*xvelocity + yvelocity*yvelocity +
                 zvelocity*zvelocity);

    if(sq > vaverh) { md.count = md.count + 1.0; }
    
    velt = sq;
    return velt;
  }

  public void dscal(double sc,int incx) {

    xvelocity = xvelocity * sc;
    yvelocity = yvelocity * sc;   
    zvelocity = zvelocity * sc;   



  }

}

class random {

  public int iseed;
  public double v1,v2;

  public random(int iseed,double v1,double v2) {
  this.iseed = iseed;
  this.v1 = v1;
  this.v2 = v2;
  }

  public double update() {

  double rand;
  double scale= 4.656612875e-10;

  int is1,is2,iss2;
  int imult=16807;
  int imod = 2147483647;

  if (iseed<=0) { iseed = 1; }

  is2 = iseed % 32768;
  is1 = (iseed-is2)/32768;
  iss2 = is2 * imult;
  is2 = iss2 % 32768;
  is1 = (is1*imult+(iss2-is2)/32768) % (65536);

  iseed = (is1*32768+is2) % imod;

  rand = scale * iseed;

  return rand;

  }

  public double seed() {

   double s,u1,u2,r;
     s = 1.0;
     do {
       u1 = update();
       u2 = update();

       v1 = 2.0 * u1 - 1.0;
       v2 = 2.0 * u2 - 1.0;
       s = v1*v1 + v2*v2;

     } while (s >= 1.0);

     r = Math.sqrt(-2.0*Math.log(s)/s);

     return r;

  }
}


