package lensDesignProblem.plot.epsMaker;

import java.util.Calendar;

public class TEPSConstants {

	public static String getHeader(String title, double bx1, double by1, double bx2, double by2) {
		return getHeader(title, bx1, by1, bx2, by2, Calendar.getInstance().getTime() + "");
	}
	
	public static String getHeader(String title, double bx1, double by1, double bx2, double by2, String creationDate) {		
		String header = "%!PS-Adobe-3.0 EPSF-3.0\n" +
		"%%Title: " + title + "\n" +
		"%%BoundingBox: 0.0 0.0 " + (bx2 - bx1) + " " + (by2 - by1) + "\n" +
		"%%CreationDate: " + creationDate + "\n" +
		"%%Creator:  TEPSMaker.java\n" +
		"%%Pages: 1\n" +
		"%%DocumentFonts: Times-Roman Helvetica Courier Symbol\n" +
		"%%EndComments\n" +
		"%%BeginSetup\n" +
		"\n" +
		"120 dict begin\n" +
		"\n" +
		"/myline %(x1 y1 x2 y2 width capstyle array offset grayscale)\n" +
		"{\n" +
		"  gsave\n" +
		"    setgray\n" +
		"    setdash\n" +
		"    setlinecap\n" +
		"    setlinewidth\n" +
		"    newpath\n" +
		"      moveto\n" +
		"      lineto\n" +
		"    stroke\n" +
		"  grestore\n" +
		"} def\n" +
		"\n" +
		"/mymakerect %(x1 y1 x2 y2)\n" +
		"{\n" +
		"  4 dict begin\n" +
		"  /y2 exch def\n" +
		"  /x2 exch def\n" +
		"  /y1 exch def\n" +
		"  /x1 exch def\n" +
		"  x1 y1 moveto\n" +
		"  x1 y2 lineto\n" +
		"  x2 y2 lineto\n" +
		"  x2 y1 lineto\n" +
		"  closepath\n" +
		" end\n" +
		"} def\n" +
		"\n" +
		"/myframerect %(x1 y1 x2 y2 width joinstyle array offset grayscale)\n" +
		"{\n" +
		"  gsave\n" +
		"    setgray\n" +
		"    setdash\n" +
		"    setlinejoin\n" +
		"    setlinewidth\n" +
		"    newpath\n" +
		"      mymakerect\n" +
		"    stroke\n" +
		"  grestore\n" +
		"} def\n" +
		"\n" +
		"/myfillrect %(x1 y1 x2 y2 grayscale)\n" +
		"{\n" +
		"  gsave\n" +
		"    setgray\n" +
		"    newpath\n" +
		"      mymakerect\n" +
		"    fill\n" +
		"  grestore\n" +
		"} def\n" +
		"\n" +
		"/myfillframerect %(x1 y1 x2 y2 fillgray width joinstyle array offset framegray)\n" +
		"{\n" +
		"  gsave\n" +
		"    /framegray exch def\n" +
		"    setdash\n" +
		"    setlinejoin\n" +
		"    setlinewidth\n" +
		"    /fillgray exch def\n" +
		"    newpath\n" +
		"      mymakerect\n" +
		"    gsave fillgray setgray fill\n" +
		"    grestore framegray setgray stroke\n" +
		"  grestore\n" +
		"} def\n" +
		"\n" +
		"/mymakearc %(x y xrad yrad startangle endangle)\n" +
		"{\n" +
		"  7 dict begin\n" +
		"  neg\n" +
		"  /endangle exch def\n" +
		"  neg\n" +
		"  /startangle exch def\n" +
		"  /yrad exch def\n" +
		"  /xrad exch def\n" +
		"  /y exch def\n" +
		"  /x exch def\n" +
		"  /oldmatrix matrix currentmatrix def\n" +
		"  x y translate\n" +
		"  xrad yrad scale\n" +
		"  0 0 1 startangle endangle arcn\n" +
		"  oldmatrix setmatrix\n" +
		"  end\n" +
		"} def\n" +
		"\n" +
		"/myframearc\n" +
		"%(x y xrad yrad startangle endangle width capstyle array offset grayscale)\n" +
		"{\n" +
		"  gsave\n" +
		"    setgray\n" +
		"    setdash\n" +
		"    setlinecap\n" +
		"    setlinewidth\n" +
		"    newpath\n" +
		"      mymakearc\n" +
		"    stroke\n" +
		"  grestore\n" +
		"} def\n" +
		"\n" +
		"/myfillarc\n" +
		"%(x y xrad yrad startangle endangle grayscale mode)\n" +
		"% mode=(0:ArcPieSlice, 1:ArcChord)\n" +
		"{\n" +
		"  gsave\n" +
		"    9 dict begin\n" +
		"    /mode exch def\n" +
		"    /grayscale exch def\n" +
		"    /endangle exch def\n" +
		"    /startangle exch def\n" +
		"    /yrad exch def\n" +
		"    /xrad exch def\n" +
		"    /y exch def\n" +
		"    /x exch def\n" +
		"    /isellipse startangle endangle sub abs 360 lt def\n" +
		"    newpath\n" +
		"      mode 1 ne isellipse and {x y moveto} if\n" +
		"      x y xrad yrad startangle endangle mymakearc\n" +
		"      isellipse {closepath} if\n" +
		"    grayscale setgray\n" +
		"    fill\n" +
		"    end\n" +
		"  grestore\n" +
		"} def\n" +
		"\n" +
		"/myfillframearc\n" +
		"%(x y xrad yrad startangle endangle fillgray\n" +
		"% width joinstyle array offset framegray mode)\n" +
		"% mode=(0:ArcPieSlice, 1:ArcChord)\n" +
		"{\n" +
		"  gsave\n" +
		"    10 dict begin\n" +
		"    /mode exch def\n" +
		"    /framegray exch def\n" +
		"    setdash\n" +
		"    setlinejoin\n" +
		"    setlinewidth\n" +
		"    /fillgray exch def\n" +
		"    /endangle exch def\n" +
		"    /startangle exch def\n" +
		"    /yrad exch def\n" +
		"    /xrad exch def\n" +
		"    /y exch def\n" +
		"    /x exch def\n" +
		"    /isellipse startangle endangle sub abs 360 lt def\n" +
		"    newpath\n" +
		"      mode 1 ne isellipse and {x y moveto} if\n" +
		"      x y xrad yrad startangle endangle mymakearc\n" +
		"      isellipse {closepath} if\n" +
		"    gsave fillgray setgray fill\n" +
		"    grestore framegray setgray stroke\n" +
		"    end\n" +
		"  grestore\n" +
		"} def\n" +
		"\n" +
		"/mymin %(x y)\n" +
		"{\n" +
		"  2 dict begin\n" +
		"  /y exch def\n" +
		"  /x exch def\n" +
		"  x y lt {x} {y} ifelse\n" +
		"  end\n" +
		"} def\n" +
		"\n" +
		"/mymax %(x y)\n" +
		"{\n" +
		"  2 dict begin\n" +
		"  /y exch def\n" +
		"  /x exch def\n" +
		"  x y gt {x} {y} ifelse\n" +
		"  end\n" +
		"} def\n" +
		"\n" +
		"/mystringextent %(str x y)\n" +
		"{\n" +
		"  4 dict begin\n" +
		"  gsave\n" +
		"    moveto\n" +
		"    false charpath pathbbox\n" +
		"    /y2 exch def    \n" +
		"    /x2 exch def    \n" +
		"    /y1 exch def    \n" +
		"    /x1 exch def    \n" +
		"    x1 x2 mymin\n" +
		"    y1 y2 mymin\n" +
		"    x1 x2 mymax\n" +
		"    y1 y2 mymax\n" +
		"  grestore\n" +
		"  end\n" +
		"} def\n" +
		"        \n" +
		"/mystring %(str x y hgrid vgrid grayscale size fontname)\n" +
		"%(hgrid=0:left, 1:center, 2:right)\n" +
		"%(vgrid=0:bottom, 1:center, 2:top)\n" +
		"{\n" +
		"  11 dict begin\n" +
		"  findfont\n" +
		"  exch\n" +
		"  dup matrix scale\n" + //オリジナル：dup neg matrix scale\n => 上下反転する
		"  makefont\n" +
		"  setfont\n" +
		"  gsave\n" +
		"    setgray\n" +
		"    /vgrid exch def\n" +
		"    /hgrid exch def\n" +
		"    /y exch def\n" +
		"    /x exch def\n" +
		"    /targetstr exch def\n" +
		"    targetstr x y mystringextent\n" +
		"    /b exch def /r exch def\n" +
		"    /t exch def /l exch def\n" +
		"    /hc r l add 2 div def\n" +
		"    /vc t b add 2 div def\n" +
		"    x y moveto\n" +
		"    hgrid 2 eq\n" +
		"      {x r sub 0 rmoveto}\n" +
		"      {hgrid 1 eq\n" +
		"         {x hc sub 0 rmoveto}\n" +
		"         {x l sub 0 rmoveto}\n" +
		"      ifelse}\n" +
		"    ifelse\n" +
		"    vgrid 2 eq\n" +
		"      {0 y t sub rmoveto}\n" +
		"      {vgrid 1 eq\n" +
		"         {0 y vc sub rmoveto}\n" +
		"         {0 y b sub rmoveto}\n" +
		"      ifelse}\n" +
		"    ifelse\n" +
		"    targetstr show\n" +
		"  grestore\n" +
		"  end\n" +
		"} def\n" +
		"\n" +
		"/mydogrid %(string mode)\n" +
		"{\n" +
		"  1 dict begin\n" +
		"  /mode exch def\n" +
		"  mode 2 eq {\n" +
		"    stringwidth pop neg 0 rmoveto\n" +
		"  } {\n" +
		"    mode 1 eq {\n" +
		"      stringwidth pop 2 div neg 0 rmoveto\n" +
		"    } {\n" +
		"      pop\n" +
		"    } ifelse\n" +
		"  } ifelse\n" +
		"  end\n" +
		"} def    \n" +
		"\n" +
		"/mymultistrings %([str1 ... strn] x y gap grid grayscale size fontname)\n" +
		"%(grid=0:left, 1:center, 2:right)\n" +
		"{\n" +
		"  5 dict begin\n" +
		"  findfont\n" +
		"  exch\n" +
		"  dup /size exch def\n" +
		"  dup matrix scale\n" + //オリジナル：dup neg matrix scale\n => 上下反転する
		"  makefont\n" +
		"  setfont\n" +
		"  gsave\n" +
		"    setgray\n" +
		"    /grid exch def\n" +
		"    /gap exch def\n" +
		"    /y exch def\n" +
		"    /x exch def\n" +
		"    x y moveto\n" +
		"    {gsave dup grid mydogrid show grestore 0 size gap mul rmoveto} forall\n" +
		"  grestore\n" +
		"  end\n" +
		"} def\n" +
		"\n" +
		"%%EndSetup\n" +
		"%%Page: 1 1\n" +
		"gsave\n" +
		"%%Begin:BODY\n" +
		(-bx1) + " " + (-by1) + " translate\n";
		return header;
	}
	
	public static String getTrailer() {
		return 
				"%%End:BODY\n" + 
				"grestore\n" +
				"end\n" +
				"showpage\n" +
				"%%Trailer\n" +
				"%%EOF\n";
	}

	public static void main(String[] args) {
		System.out.println(TEPSConstants.getHeader("hoge.eps", 10, 20, 30, 40));
		System.out.println(TEPSConstants.getTrailer());
	}

}
