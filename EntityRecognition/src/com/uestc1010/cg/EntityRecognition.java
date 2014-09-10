package com.uestc1010.cg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

/*
 * date 2014.4
 * author uestc1010
 * 说明：对读入的文本进行匹配标注
 * 
 * */
public class EntityRecognition {

	//实体识别，句子集合识别
	public void entitiyRecognition(Vector<String> sentence) throws IOException{
		//按句处理(遇到 。？！……即为一句)
		Iterator iter = sentence.iterator();
		while(iter.hasNext()){
			String sen = (String) iter.next();
			//System.out.println(sen);
			String doSen = match(sen);
//			doSen = mark(doSen);
//			System.out.println(doSen);
			doSen = markTwo(doSen);
		}

		
	}
	//字符串处理
	public void entitiyRecognition(String string) throws IOException{
		Vector<String> words = sentence(string);
		Iterator iter = words.iterator();
		while(iter.hasNext()){
			String sen = (String) iter.next();
			//System.out.println(sen);
			String doSen = match(sen);
//			doSen = mark(doSen);
//			System.out.println(doSen);
			doSen = markTwo(doSen);
		}
		
	}
	//文件文本识别
	public void entitiyRecognition(File file) throws IOException{
		// to be continue~~~
	}
	//文件读入
	String readFile(File file) throws IOException{
		BufferedReader read = new BufferedReader(new FileReader(file));
		String temp = null;
		String sb = "";
		while((temp = read.readLine()) != null){
			sb += temp;
		}
		return sb;
	}
	//断句
	Vector<String> sentence(String str){
		Vector<String> words = new Vector<String>();
		String buf ="";
		boolean flag = false;
		int len = str.length();
		for(int i = 0 ; i < len; i++){
			char word = str.charAt(i);
			if((word == '！')||(word == '。')||(word == '？')||(word == '…')||(word == '；')){
				buf += word ;
				flag = true;
		    }else
				 buf += word;
			 //把句子全部存入hashmap ,初始权值全部为1
			 if(flag == true){
				 words.add(buf);
				 flag = false;
				 buf ="";
			 }
		}
		//System.out.println(words);
		return words;
	}
	//与语料库进行匹配
	String match(String s) throws IOException{
		//分别与人名 地名 组织机构名相匹配 满足其中一个条件即为满足
		File file = new File("姓氏大全.txt");
		BufferedReader read = new BufferedReader(new FileReader(file));
		String temp = null;
		String sb = s;
		while((temp = read.readLine()) != null){  //有点小问题，为毛每次IF判断都尼玛是true！！
			//如果包含某个姓氏，就进行标注
			if(sb.contains(temp)){
				sb = mark(sb,temp);
//				System.out.println(sb);
			}
		}
		return sb;
	}
	
	//进行标注,即对与语料库相匹配的关键字进行标注
	String mark(String s,String key){
		//在匹配的字符串前后插入特定的标注,在s中的key字符串前后标注
		//注意：一句话中有多个key
		StringBuffer buf = new StringBuffer(s);
		int count = 0; //记录添加标注后字符串buf的长度
		int length = s.length();
		for(int i = 0; i < length ; i++){
			String buff="";
			buff += s.charAt(i);
//			System.out.println(buff);
			if(!buff.equals(key)){
				//System.out.println("no");
				//break;
			}
			else{
				//System.out.println("YES");
				buf.insert(i+count, "ε");
				count += 2;
				buf.insert(i+count, "ε");
				
			}
		}
//		System.out.println(buf.toString());
		return buf.toString();
	}
	
	/*二次标注，对上下文环境处理后的关键字进行标注，确定实体的边界
	 * 判断关键词前面与关键词是否能组成词语
	 * 判断关键词后面与关键词能否组成词语
	 * 判断关键词后面与关键词能否组成名字
	 * 简单语义判断（比如关键字：说，道，曰，是等等）
	 */
	String markTwo(String s) throws IOException{
		//在确定边界后的字符串前后插入特定的标注
		Vector<String> name = new Vector<String>();
		String buf = s;
		
		if(!buf.contains("ε")){
//			System.out.println("no");
		}else{
//			System.out.println("YES");
			int length = buf.length();
			for(int i = 0 ;i < length ;i ++){
				String buf1 = "";
				String buf2 = "";
				String word="";
				String word1="";
				 word += buf.charAt(i);
				if(word.equals("ε")){
					if(((i+2) < length) && (word1 += buf.charAt(i+2)).equals("ε")){
						buf1 += buf.charAt(i+1);  //获得第一次标记的关键字= 
//						System.out.println(buf1);
						name = nameProbability(buf1); //获得姓氏的全部名字（以后用概率来获得）
						if((i+3) < length){
							buf1 += buf.charAt(i+3);
							buf2 = buf1;
						}
						if((i+4) < length)
							buf1 +=buf.charAt(i+4);
						if(name.contains(buf1)){
							outPut(buf1);
						}else if(name.contains(buf2)){
							outPut(buf2);
						}else;
						
					}
				}
			}
		}
		return null;
	}
	//根据姓氏，找到对应的姓名文本，找到名字返回姓名
	Vector<String> nameProbability(String str) throws IOException{
		Vector<String>word = new Vector<String>();
		String buf ="";
		File file = new File(".\\Data");
		String name[];
		name = file.list();
		for(int i = 0 ; i < name.length; i++ ){
//			System.out.println(name[i]);
			if(name[i].contains(str)){
				buf = name[i];
			}
			if(buf != ""){
				String filename = ".\\Data"+"\\"+buf;
				File file1 = new File(filename);
				InputStreamReader isr =new InputStreamReader(new FileInputStream(file1),"UTF-8");
				BufferedReader read = new BufferedReader(isr);
				String temp =null;
		
				while((temp = read.readLine()) != null){
//					System.out.println(temp);
					word.add(temp);
				}
//				System.out.println(word);
				
			}
			buf ="";
		}
		
		return word;
	}
	//输出识别的实体
	void outPut(String s){
		System.out.print(s+"  ");
	}
	
	//是名字的概率,地区名的概率以及组织机构名的概率
	double Probability(String name){
		//统计名字用字规律 用字概率
		return 0;
	}
	
	//是中国词语的概率
	double wordsProbability(String word){
		//统计名字用作词语的概率
		return 0;
	}
	
	
	public static void main(String []args) throws IOException{

		String test = "陈峥，秦隆，尹训春，胡可奇，施展，杜小清，张凯，王二蛋，李咏，胡锦涛是国家主席。";
		EntityRecognition xx = new EntityRecognition();
		xx.entitiyRecognition(xx.sentence(test));
		
		
	}
}

