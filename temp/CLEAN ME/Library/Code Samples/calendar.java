/*
	 * Works from dates after Sep 14, 1752 (New System of Gregorian Calendar (UK adoption))
	 * May crash or return junk value for negative or other invalid inputs.
	 */
	public static String calendar(int d, int m, int y)	{return "Sun    Mon    Tues   Wednes Thurs  Fri    Satur  ".substring(d=((6-2*(y/100%4)+(m<3&&y%4==0&&(y%100!=0||y%400==0)?-1:0)+(y%=100)/12+5*(y%12)/4+"xbeehcfhdgbeg".charAt(m)+d)%7)*7,d+7).trim()+"day";}