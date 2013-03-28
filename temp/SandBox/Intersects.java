//Mine is (for segments (x1, y1)--(x2, y2) and (x3, y3)--(x4, y4)):

return vp (x1, y1, x2, y2, x3, y3) * vp (x1, y1, x2, y2, x4, y4) <= 0 &&
       vp (x3, y3, x4, y4, x1, y1) * vp (x3, y3, x4, y4, x2, y2) <= 0;



//where vp is vector product:

inline real vp (int x0, int y0, int x1, int y1, int x2, int y2)
{
 return (x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0);
}
