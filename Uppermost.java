import java.util.ArrayList;
import java.util.*;

public class Uppermost
{
	public static class Line{ //Line instances to hold and sort
		double slope;
		double y_int;
		int index;
        
        public Line(double slope, double y_int, int num){
            this.slope = slope;
            this.y_int = y_int;
            this.index = num;
        }
	}
    public static class Intersection{ //Intersection instance to hold and sort
		double x;
        double y;
		int rIdx; //index of the line of the right side of the intersection
		boolean is_left;

	    public Intersection(double x, double y, int rIdx, boolean is_leftHalf){
            this.x = x;
            this.y = y;
            this.rIdx = rIdx;
            this.is_left = is_leftHalf;
        }
    }

//USEFUL FORMULAS
    public static double y_vals(double x, Line line1){
    	return line1.slope * x + line1.y_int;
    }
    public static double intersection(Line line1, Line line2){
    	return (line2.y_int - line1.y_int)/(line1.slope - line2.slope);
    }
	public static boolean intersectIsBelow(Intersection currentIntersect, Line currentLine){
		return currentIntersect.y<y_vals(currentIntersect.x,currentLine);
	}

	public static ArrayList<Line> lineListMaker(double[] slopes, double[] intercepts){
		ArrayList <Line> lineList = new ArrayList <Line>(); //create an array with all of the lines
    	for(int i=0; i<slopes.length; i++){
    		Line aLine = new Line(slopes[i], intercepts[i], i); 
    		lineList.add(aLine);
    	}
       lineList = sorter(lineList);
	   return lineList;
	}

    //sorter creates a list of lines with only one per unique slope, selcting the one with the highest y-intercept
    public static ArrayList<Line> sorter (ArrayList<Line> lineList){
        lineList.sort(new Comparator <Line>(){ //Sorts the specified array of objects according to the order induced by the specified comparator.
            public int compare(Line line1, Line line2){
                if(line1.slope == line2.slope){
                    if(line1.y_int == line2.y_int)
                        return 0;
					else if(line1.y_int < line2.y_int)
                        return -1;
					else
                        return 1;
                }
				else if(line1.slope < line2.slope)
                    return -1;
                else
                    return 1;
            }
        });
            ArrayList <Line> uniqueSlopes = new ArrayList <Line>();
            for(int i=0; i<lineList.size()-1; i++){
                if(lineList.get(i).slope != lineList.get(i+1).slope){ //unique slopes go in the array with the highest y-int remaining
                    uniqueSlopes.add(lineList.get(i));
                }
            }
            if(lineList.size()-1 >= 0){
                //the above for loop doesn't check the final line, so as long as it the list is long enough, it should be added
                uniqueSlopes.add(lineList.get(lineList.size()-1));
            }
            return uniqueSlopes;
    }
	
	public static ArrayList<Line> merger(ArrayList <Line> uniqueSlopes, int start, int end){
		ArrayList <Line> compLines = new ArrayList <Line>();
        //BASE CASES: updates compLines so that once else is reached, it only deals with necessary lines
    	if((end - start) < 0)//one line left
    		return compLines;
    	if((end - start) == 0)//two lines left
    		return uniqueSlopes;
		else if((end - start) == 1){ 
			compLines.add(uniqueSlopes.get(start));
			compLines.add(uniqueSlopes.get(end));
		}
		else if((end - start) == 2){ //four lines left
			//get the intersection point of the first and last line, check if the second line goes above their intersection
			//if yes: add the second line to lines to compre, if no: add the end line
			compLines.add(uniqueSlopes.get(start)); 
			double x = intersection(uniqueSlopes.get(start), uniqueSlopes.get(end)); 
			double y = y_vals(x, uniqueSlopes.get(start)); 
			double yOfNextLine = y_vals(x, uniqueSlopes.get(start+1)); 
			if(yOfNextLine > y)
				compLines.add(uniqueSlopes.get(start+1));
			compLines.add(uniqueSlopes.get(end)); 
		}

		else{
			ArrayList<Intersection> intersections = new ArrayList<Intersection>();
			ArrayList <Line> leftHalf = new ArrayList <Line>();
			ArrayList <Line> rightHalf = new ArrayList <Line>();

			//recursion call:
			int mid = start + (end - start)/2;
			leftHalf=merger(uniqueSlopes, start, mid);
			rightHalf=merger(uniqueSlopes, mid+1, end);

			//get the intersections of the left half
			for(int i=0; i<leftHalf.size()-1; i++){
				double x = intersection(leftHalf.get(i), leftHalf.get(i+1)); //get the of x-vale of thhe intersection of a line and the following line
				double y = y_vals(x, leftHalf.get(i)); //gets the y-value of the line and the intersection
				
				Intersection intersectedAt = new Intersection(x, y, i+1, true); //create an instance of an intersection for the first half where all intersections are is_left true (they are 
				//the left half of the line list)
				intersections.add(intersectedAt);
				
			}

			//get the intersections of the right half
			for(int i=0; i<rightHalf.size()-1; i++){
				double x = intersection(rightHalf.get(i), rightHalf.get(i+1));
				double y = y_vals(x, rightHalf.get(i));
				Intersection intersectedAt = new Intersection(x, y, i+1, false);//create an instance of an intersection for the first half where all intersections are is_left false
				intersections.add(intersectedAt);
				
			}
			
			//intersection comparer
			intersections.sort(new Comparator <Intersection>(){ //sort the intersections based on the following scheme
				public int compare(Intersection intersection1, Intersection intersection2){
					if(intersection1.x == intersection2.x)
						return 0;
					else if(intersection1.x < intersection2.x)
						return -1;
					else
						return 1;
				}
			});
			ArrayList <Line> visibleLines = new ArrayList <Line>();
			int getLeftLine = 0;
			int getRightLine = 0;
			//add the first line because ordered list so it is the highest with the most negative slope 
			visibleLines.add(leftHalf.get(0));
			
			
			for(int i=0;i<intersections.size();i++){ 
				if(intersections.get(i).is_left){
					if(intersectIsBelow(intersections.get(i), rightHalf.get(getRightLine))) //checking the lines and adding them to the list
						break; //intersection is below another line 
					else{ //if the intersection is along the visible area, add the next line to visible lines and move on to the next line 
						visibleLines.add(leftHalf.get(intersections.get(i).rIdx)); //add from left to right
						getLeftLine = intersections.get(i).rIdx;//moves to the line to the right of the index
					}
				}
				else if(!intersections.get(i).is_left){ //we're not coming from left to right SO this is getting rid of any lines not worth checking
					if(intersectIsBelow(intersections.get(i), leftHalf.get(getLeftLine))) //if its below, skip over it
						getRightLine = intersections.get(i).rIdx;//moves the lines to compare intersections to past covered lines from the right side
					else//since a later loop will add all of the first half based on the getLeftLine index
						break; //its visible so it should be gone through
				}
			}
			for(int i = getRightLine; i<rightHalf.size();i++){
				visibleLines.add(rightHalf.get(i));
			}
			return visibleLines;//if the line on the right half has a higher y-int then the one on the left select that oen
		}
		return compLines;
	}
    
    public static int[] visibleLines(double[] slopes, double[] intercepts){
		ArrayList<Line> sortedList = lineListMaker(slopes,intercepts);
		if(sortedList.size() == 0){
    		return new int[0];
    	}
		ArrayList<Line> visibleLines = merger(sortedList, 0, sortedList.size()-1);
    	int[] visibleLineIndices = new int [visibleLines.size()];
    	for(int i=0; i < visibleLines.size(); i++){
    		visibleLineIndices[i] = visibleLines.get(i).index;
    	}
    	return visibleLineIndices;
    }
  }
  


