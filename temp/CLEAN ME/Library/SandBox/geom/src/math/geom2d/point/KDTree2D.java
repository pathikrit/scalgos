 /**
 * file KDTree2D.java
 */

package math.geom2d.point;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.line.StraightLine2D;


/**
 * A data structure for storing a great number of points. During construction
 * of the tree, median point in current coordinate is chosen for each step,
 * ensuring the final tree is balanced. The cost for retrieving a point is 
 * O(log n).<br>
 * The cost for building the tree is O(n log^2 n), that can take some time for
 * large points sets.<br>
 * This implementation is semi-dynamic: points can be added, but can not be
 * removed.
 * @author dlegland
 *
 */
public class KDTree2D {
    
    public class Node{
        private Point2D point;
        private Node left;
        private Node right;
        
        public Node(Point2D point){
            this.point  = point;
            this.left   = null;
            this.right  = null;
        }

        public Node(Point2D point, Node left, Node right){
            this.point  = point;
            this.left   = left;
            this.right  = right;
        }
        
        public Point2D getPoint() {
            return point;
        }
        
        public Node getLeftChild() {
            return left;
        }
        
        public Node getRightChild() {
            return right;
        }
        
        public boolean isLeaf() {
            return left==null && right==null;
        }
    }
        
    private class XComparator implements Comparator<Point2D> {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Point2D p1, Point2D p2){
            if(p1.getX()<p2.getX())
                return -1;
            if(p1.getX()>p2.getX())
                return +1;
            return Double.compare(p1.getY(), p2.getY());
        }
    }
    
    private class YComparator implements Comparator<Point2D> {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Point2D p1, Point2D p2){
            if(p1.getY()<p2.getY())
                return -1;
            if(p1.getY()>p2.getY())
                return +1;
            return Double.compare(p1.getX(), p2.getX());
        }
    }
   
    private Node root;
    
    private Comparator<Point2D> xComparator;
    private Comparator<Point2D> yComparator;
    
    public KDTree2D(ArrayList<Point2D> points) {
        this.xComparator = new XComparator();
        this.yComparator = new YComparator();
        root = makeTree(points, 0);
    }
        
    private Node makeTree(List<Point2D> points, int depth) {
        // Add a leaf
        if(points.size()==0)
            return null;
        
        // select direction
        int dir = depth%2;
        
        // sort points according to i-th dimension
        if(dir==0){
            // Compare points based on their x-coordinate
            Collections.sort(points, xComparator);
        }else{
            // Compare points based on their x-coordinate
            Collections.sort(points, yComparator);
        }
        
        int n = points.size();
        int med = n/2;    // compute median
        
        return new Node(
                points.get(med),
                makeTree(points.subList(0, med), depth+1),
                makeTree(points.subList(med+1, n), depth+1));
    }

    public Node getRoot() {
        return root;
    }
    
    public boolean contains(Point2D value){
        return contains(value, root, 0);
    }
    
    private boolean contains(Point2D point, Node node, int depth){
        if(node==null) return false;
        
        // select direction
        int dir = depth%2;
        
        // sort points according to i-th dimension
        int res;
        if(dir==0){
            // Compare points based on their x-coordinate
            res = xComparator.compare(point, node.point);
        }else{
            // Compare points based on their x-coordinate
            res = yComparator.compare(point, node.point);
        }
        
        if(res<0)
            return contains(point, node.left, depth+1);
        if(res>0)
            return contains(point, node.right, depth+1);
        
        return true;
    }

    public Node getNode(Point2D point) {
        return getNode(point, root, 0);
    }
    
    private Node getNode(Point2D point, Node node, int depth){
        if(node==null) return null;
        // select direction
        int dir = depth%2;
        
        // sort points according to i-th dimension
        int res;
        if(dir==0){
            // Compare points based on their x-coordinate
            res = xComparator.compare(point, node.point);
        }else{
            // Compare points based on their x-coordinate
            res = yComparator.compare(point, node.point);
        }
        
        if(res<0)
            return getNode(point, node.left, depth+1);
        if(res>0)
            return getNode(point, node.right, depth+1);
        
        return node;
    }

    public void add(Point2D point){
       add(point, root, 0);
    }
    
    private void add(Point2D point, Node node, int depth) {
        // select direction
        int dir = depth%2;
        
        // sort points according to i-th dimension
        int res;
        if(dir==0){
            // Compare points based on their x-coordinate
            res = xComparator.compare(point, node.point);
        }else{
            // Compare points based on their x-coordinate
            res = yComparator.compare(point, node.point);
        }
        
        if(res<0){
            if(node.left==null)
                node.left = new Node(point);
            else
                add(point, node.left, depth+1);
        }
        if(res>0)
            if(node.right==null)
                node.right = new Node(point);
            else
                add(point, node.right, depth+1);
    }
    
    public Collection<Point2D> rangeSearch(Box2D range) {
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        rangeSearch(range, points, root, 0);
        return points;
    }
    
    /**
     * range search, by recursively adding points to the collection.
     */
    private void rangeSearch(Box2D range, 
            Collection<Point2D> points, Node node, int depth) {
        if(node==null)
            return;
        
        // extract the point
        Point2D point = node.getPoint();
        double x = point.getX();
        double y = point.getY();
        
        // check if point is in range
        boolean tx1 = range.getMinX()<x;
        boolean ty1 = range.getMinY()<y;
        boolean tx2 = x<=range.getMaxX();
        boolean ty2 = y<=range.getMaxY();
        
        // adds the point if it is present
        if(tx1 && tx2 && ty1 && ty2)
            points.add(point);
        
        // select direction
        int dir = depth%2;
        
        if(dir==0 ? tx1 : ty1)
            rangeSearch(range, points, node.left, depth+1);
        if(dir==0 ? tx2 : ty2)
            rangeSearch(range, points, node.right, depth+1);
    }
    
    
    public Point2D nearestNeighbor(Point2D point) {
        return nearestNeighbor(point, root, root, 0).getPoint();
    }
    
    /**
     * Return either the same node as candidate, or another node whose point
     * is closer.
     */
    private Node nearestNeighbor(Point2D point, Node candidate, Node node, 
            int depth) {
        // Check if the current node is closest that current candidate
        double distCand = candidate.point.getDistance(point);
        double dist     = node.point.getDistance(point);
        if(dist<distCand){
            candidate = node;
        }
        
        // select direction
        int dir = depth%2;

        Node node1, node2;
        
        // First try on the canonical side,
        // the result is the closest node found by depth-firth search
        Point2D anchor = node.getPoint();
        StraightLine2D line;
        if(dir==0){
            boolean b = point.getX()<anchor.getX();
            node1 = b ? node.left : node.right;
            node2 = b ? node.right : node.left;
           line = StraightLine2D.create(anchor, new Vector2D(0, 1)); 
        } else {
            boolean b = point.getY()<anchor.getY();
            node1 = b ? node.left : node.right;
            node2 = b ? node.right : node.left;
            line = StraightLine2D.create(anchor, new Vector2D(1, 0)); 
        }
        
        if(node1!=null) {
            // Try to find a better candidate
            candidate = nearestNeighbor(point, candidate, node1, depth+1);

            // recomputes distance to the (possibly new) candidate
            distCand = candidate.getPoint().getDistance(point);
        }
        
        // If line is close enough, there can be closer points to the other
        // side of the line
        if(line.getDistance(point)<distCand && node2!=null) {
            candidate = nearestNeighbor(point, candidate, node2, depth+1);
        }
        
        return candidate;
    }
    
    
    /**
     * Gives a small example of use.
     */
    public static void main(String[] args){
        int n = 3;
        ArrayList<Point2D> points = new ArrayList<Point2D>(n);
        points.add(new Point2D(5, 5));
        points.add(new Point2D(10, 10));
        points.add(new Point2D(20, 20));
        
        System.out.println("Check KDTree2D");
        
        KDTree2D tree = new KDTree2D(points);
        
        System.out.println(tree.contains(new Point2D(5, 5)));
        System.out.println(tree.contains(new Point2D(6, 5)));
    }
    
}
