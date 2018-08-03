package tensorflowtest;

import tensorflowtest.densenet_310;
public class work extends Thread {
	private densenet_310 model;
	private boolean working=false;
	private String source;
	float [] res=new float[2];//�����һ������ ������������
	public work(){
        model=new densenet_310();
    }
	public void run() {
        while(true)
        {
        	try {wait();} catch (InterruptedException e) {e.printStackTrace();}
        	if(working)
        	{
        		System.out.println(Thread.currentThread().getName());
        		model.setimage(source);//����ͼƬ
        		try{res=model.getresult();} catch (Exception e) {
        			e.printStackTrace();}

        		System.out.println(res[0]+ " "+res[1]);
        		working=false;
        	}
        }
    }
	public boolean newrequest(String s)
	{
		source=s;
		return working=true;
	}
	public boolean isworking()
	{
		return working;
	}
}
