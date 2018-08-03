package tensorflowtest;
import java.util.concurrent.*;

import tensorflowtest.densenet_310;
public class dispatch {
	private int cnt;
	private densenet_310 model[];
    private static Object obj = new Object();
    final int tnum=5;	/////////////线程数
    final Semaphore se=new Semaphore(tnum);
    private ExecutorService threadPool;
    public dispatch(){
        model=new densenet_310[tnum];
        for(int i=0;i<tnum;i++)
        {
        	model[i]=new densenet_310();
        }
        threadPool=Executors.newFixedThreadPool(tnum);//Executors.newCachedThreadPool();
        cnt=0;
    }
    
    public float[] fun(String filepath,int runid)
    {
     	float res[]=new float[2];
    	try {
     		res=threadPool.submit(new work(se,filepath,runid,cnt++)).get();} 
     		catch (Exception e) {
            e.printStackTrace();
        }
     	return res;
    }
    class work implements Callable<float[]>{
    	private Semaphore semaphore;
    	float res[];
    	int id,runid;
    	String source;
    	/*
    	public work(){
            
        }*/
    	public work(Semaphore semaphore,String source,int runid,int id){
    		this.semaphore=semaphore;
    		this.source=source;
    		this.runid=runid;
            this.id=id;
        }
        public float[] call() {
        			//System.out.println(id+ " run");
	                id=id%tnum;
        			try {
	                    semaphore.acquire();
	                    model[id].setimage(this.source);//设置图片
	            		res=model[id].getresult();//返回结果
	            		semaphore.release();
	            		
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
        			return res;
            	
        }
        
    }
    
    
}


/*
6.010914E-8 0.9999999
0.15182313 0.84817684
0.9380052 0.06199475
1.10210024E-7 0.9999999
0.99989307 1.0691335E-4
6.1331764E-7 0.9999994
1.9100048E-4 0.99980897
0.9291599 0.070840046
2.4746536E-9 1.0
0.09259806 0.907402
*/