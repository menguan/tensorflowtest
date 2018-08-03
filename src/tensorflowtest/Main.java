package tensorflowtest;
import java.util.Scanner;

import tensorflowtest.dispatch;
public class Main {
	public static void main(String[] args) throws Exception {
		dispatch a=new dispatch();//声明
		
		float res[]=new float[2];
		int i=0;
		Scanner input = new Scanner(System.in);
		while(input.hasNext()) {
			String b=input.next();
			res=a.fun("C:/Users/menguan/Desktop/代码/appmodels/imgs/test"+i%20+".png",i);//图片路径 和 runid(随意）
			System.out.println(res[0]+" "+res[1]);
			i++;
			
		}
		
	}
}

