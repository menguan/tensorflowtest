package tensorflowtest;
import java.util.Scanner;

import tensorflowtest.dispatch;
public class Main {
	public static void main(String[] args) throws Exception {
		dispatch a=new dispatch();//����
		
		float res[]=new float[2];
		int i=0;
		Scanner input = new Scanner(System.in);
		while(input.hasNext()) {
			String b=input.next();
			res=a.fun("C:/Users/menguan/Desktop/����/appmodels/imgs/test"+i%20+".png",i);//ͼƬ·�� �� runid(���⣩
			System.out.println(res[0]+" "+res[1]);
			i++;
			
		}
		
	}
}

