package EE308FZ_LAB2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class EE308FZ_LAB2 {
    //set some variables to store the number
    public static int keywordTotalNum=0;
    public static int switchNum=0;
    public static int[] caseNum=new int [500];
    public static int ifelseNum = 0;
    public static int elseNum = 0;
    public static int if_elseif_elseNum = 0;
    public static boolean annotationFlag = false;
    public static boolean contentFlag = false;
    
    /**
     * we suppose 'if' as 1, 'else-if' as 2, 'else' as 3
     * and store them in a collection
     * if ith term is 3(else), and i-1th term is 1(if), which means
     * it's a if-else structure, then we can make ifelseNum++
     * to get the if_elseif_elseNum, we can just let elseNum minus ifelseNum
     * then we can get the number of if-elseif-else structure  
     * 
     */
    public static ArrayList <Integer> ifTypeStructure = new ArrayList<>();
	
	
	public static void main(String[] args)throws IOException {
		// TODO Auto-generated method stub
		ArrayList <String> keyTable = new ArrayList<>();
		keyWords(keyTable);
		//input the address of test file
		System.out.println("please input the path of the code file:");
		Scanner sc=new Scanner(System.in);
		String filePath=sc.nextLine();
		int level;//there are 4 level you can choose(from low to high as 1, 2, 3, 4)
		System.out.println("Please enter the required level:");
		while(true) {
			level=sc.nextInt();
			if(level==1||level==2||level==3||level==4) {
				break;
			}else {
				System.out.println("please enter the Integer number from 1 to 4");
			}
		}
		
		//read every line of this file
		FileReader fileReader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        
        //start to scan this file
        while(line != null){
        	operateLine(line,keyTable);
            line = bufferedReader.readLine();
        }
        
        //close streams
        bufferedReader.close();
        fileReader.close();
		
        //according the required level, complete the corresponding function
        if(level==1) {
        	level1();
        }
        
        if(level==2) {
        	level2();
        }
        
        if(level==3) {
        	level3();
        }
        
        if(level==4) {
        	level4();	
        }
        
   }
	
	//add all keywords to a list
	public static void keyWords(ArrayList<String> keyTable) {
		keyTable.add("auto");
		keyTable.add("break");
		keyTable.add("char");
		keyTable.add("const");
		keyTable.add("continue");
		keyTable.add("default");
		keyTable.add("do");
		keyTable.add("double");
		keyTable.add("enum");
		keyTable.add("extern");
		keyTable.add("float");
		keyTable.add("for");
		keyTable.add("goto");
		keyTable.add("int");
		keyTable.add("long");
		keyTable.add("register");
		keyTable.add("return");
		keyTable.add("short");
		keyTable.add("signed");
		keyTable.add("sizeof");
		keyTable.add("static");
		keyTable.add("struct");
		keyTable.add("typedef");
		keyTable.add("union");
		keyTable.add("unsigned");
		keyTable.add("void");
		keyTable.add("volatile");
		keyTable.add("while");
		
	}
	
	// check whether char c is in 'a' to 'z'
    public static boolean checkWord(char c){
        return c >= 'a' && c <= 'z';
    }
    
    //delete the annotation of//
    public static String DeleteLineAnnotation(String str){
        for(int i = 0; i < str.length() ;i++){
            if(str.charAt(i) == '/' && str.charAt(i) == '/'){
                return str.substring(0,i);
            }
        }
        return str;
    }
    
    //find /* and */, but don't deal with the annotation in it
    public static String DeleteMultiAnnotation(String str){
        StringBuilder sb = new StringBuilder(200);
        for(int i = 0; i < str.length() ;i++){
            if(!annotationFlag){
                if(str.charAt(i) == '/' && str.charAt(i+1) == '*'){
                    annotationFlag = true;
                }else{
                    sb.append(str.charAt(i));
                }
            }else{
                if(str.charAt(i) == '*' && str.charAt(i+1) == '/'){
                    i++;
                    annotationFlag = false;
                }
            }
        }
        return sb.toString();
    }
    
    //use contentFlag to delete the specific content in /* */
    public static String DeleteInsideString(String str){
        StringBuilder sb = new StringBuilder(200);
        for(int i = 0; i < str.length() ;i++){
            if(!contentFlag){
                if(str.charAt(i) == '"'){
                	contentFlag = true;
                }else{
                    sb.append(str.charAt(i));
                }
            }else{

                if(str.charAt(i) == '"'){
                	contentFlag = false;
                }
            }
        }
        return sb.toString();
    }
	
    
    //deal with every line without annotation
    public static void operateLine(String line, ArrayList<String> keyTable) {
    	//delete all the annotation
    	line = DeleteLineAnnotation(line);
        line = DeleteInsideString(line);
        line = DeleteMultiAnnotation(line);
    	
        int len=line.length();
        int firstIndex = 0;//the first index of a word
        int lastIndex = 0;//the last index of a word
        Boolean inWord=false;//judge whether enter in a word
        
        for(int i=0;i<len;i++) {
        	char c=line.charAt(i);
        	if(checkWord(c)) {
        		if(!inWord) {
        			firstIndex=i;
        			inWord=true;
        		}
        	}else {
        		if(inWord) {
        			lastIndex=i;
        			String thisWord=line.substring(firstIndex, lastIndex);//capture current word
        			if("switch".equals(thisWord)) {
        				keywordTotalNum++;
        				switchNum++;
        				i=lastIndex;
        			}else if("case".equals(thisWord)) {
        				keywordTotalNum++;
        				caseNum[switchNum]++;
        				i = lastIndex;
        			}else if("if".equals(thisWord)) {
        				keywordTotalNum++;
        				ifTypeStructure.add(1);//we denote 1 as if, and add it to ifTypeStructure collection
        				i=lastIndex;
        			}else if("else".equals(thisWord)) {
        				if (lastIndex+3 <= line.length() &&line.substring(firstIndex, lastIndex + 3).equals("else if")) {
                            keywordTotalNum +=2;
                            ifTypeStructure.add(2);//we denote 2 as else-if, and add it to ifTypeStructure collection
                            i = lastIndex+3;
                        } else {
                            keywordTotalNum++;
                            ifTypeStructure.add(3);//we denote 3 as else, and add it to ifTypeStructure collection
                            i = lastIndex;
                        }
        			}else {//match other key words
        				for(String str:keyTable){
                            if(thisWord.equals(str)){
                                keywordTotalNum++;
                                break;
                            }
                        }
        				i = lastIndex;
        			}
        			inWord = false;
        		}
        	 }
           }
     }
    
    
    /**
     * go through all the term in ifTypeStructure collection
     * and calculate ifelseNum and if_elseif_elseNum
     */
    public static void calculate(){
    	for(int i=0;i<ifTypeStructure.size();i++) {
    		if(ifTypeStructure.get(i).equals(3)) {
    			elseNum++;
    			if(ifTypeStructure.get(i-1).equals(1)) {
    				ifelseNum++;
    			}
    		}
    	}
    	
    	if_elseif_elseNum=elseNum-ifelseNum;

    }
    
    //print the result of level1
    public static void level1() {
    	System.out.println("total num:"+keywordTotalNum);
    }
    
    //print the result of level2
    public static void level2() {
    	level1();
    	System.out.println("switch num:"+switchNum);
    	System.out.print("case num:");
        for(int i = 1; i <= switchNum; i++){
            System.out.print(caseNum[i]+" ");
        }
    }
    
    //print the result of level3
    public static void level3() {
    	level2();
        calculate();
        System.out.println("\nif-else num:"+ifelseNum);
    }
    
    //print the result of level4
    public static void level4() {
    	level3();
    	System.out.println("if-elseif-else num:"+if_elseif_elseNum);
    	
    }   
}





