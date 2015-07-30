package org.openmrs.module.emrmonitor.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.loader.custom.Return;
import org.openmrs.api.context.Context;
public class ExtraSystemInformation {

	private static final long serialVersionUID = 1L;
	
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private String totalPhyisycalMemory;

	private String freePhyisycalMemory;

	private String availablePhyisycalMemory;

	private String usedPhyisycalMemory;
	 
	private SessionFactory factory;
	
	 Date date = null;
	
     long totalheartBeatsWeekly=0L;
	
	 long totalheartBeatsMonthly=0L;
	
	private String totalWeeklyuptime;
	
	private String totalLastWeekyuptime;
	
	private String totalThisMonthyuptime;
	
	private String totalLastMonthyuptime;
	
	long monthlyDates;
	
	public SessionFactory getFactory() {
		return factory;
	}

	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}


	File[] roots = File.listRoots();
    

	public String getTotalPhyisycalMemory() {
		
		for (File root : roots) {
			totalPhyisycalMemory=root.getTotalSpace()/(1024*1024*1024)+" GiB";
		}
		
		return totalPhyisycalMemory;
	}

	public void setTotalPhyisycalMemory(String totalPhyisycalMemory) {
		this.totalPhyisycalMemory = totalPhyisycalMemory;
	}

	public String getFreePhyisycalMemory() {
		
		for (File root : roots) {
			freePhyisycalMemory=root.getFreeSpace()/(1024*1024*1024)+" GiB";
		}
		
		return freePhyisycalMemory;
	}

	public void setFreePhyisycalMemory(String freePhyisycalMemory) {
		this.freePhyisycalMemory = freePhyisycalMemory;
	}

	public String getAvailablePhyisycalMemory() {
		
		for (File root : roots) {
			availablePhyisycalMemory=root.getUsableSpace()/(1024*1024*1024)+" GiB";
		}
		
		return availablePhyisycalMemory;
	}

	public void setAvailablePhyisycalMemory(String availablePhyisycalMemory) {
		this.availablePhyisycalMemory = availablePhyisycalMemory;
	}

	public String getUsedPhyisycalMemory() {
		
		for (File root : roots) {
			usedPhyisycalMemory=(root.getTotalSpace()-root.getFreeSpace())/(1024*1024*1024)+" GiB";
		}
		
		return usedPhyisycalMemory;
	}

	public void setUsedPhyisycalMemory(String usedPhyisycalMemory) {
		this.usedPhyisycalMemory = usedPhyisycalMemory;
	}
public String getVersion(String command) {
	String s=null;
	Process p;
	try {
	    p = Runtime.getRuntime().exec(command);
	    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    p.waitFor();
	    s = br.readLine();
	    p.destroy();
	    return s;    
	} catch (Exception e) {			
	}		
	return "";
}


public String checkConnection() {
	
	String[] addresses=Context.getAdministrationService().getGlobalProperty("emrmonitor.productionServer.ips").split("|");
    for(String addr:addresses){
    	InetAddress inet;
    	try {
    		inet = InetAddress.getByName(addr);
			if(inet.isReachable(5000))
				return "Connected";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    		
    }		
		return "Not Connected";
}

public double getCPULoadAverage() {

    OperatingSystemMXBean osBean=(OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	return osBean.getSystemLoadAverage();
}
public String getCPUInfo(String startsWith) {

	String s;
	Process p;
	try {
	    p = Runtime.getRuntime().exec("lscpu");
	    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    //s = br.readLine();
	    while ((s=br.readLine())!=null) {
	    	if(s.startsWith(startsWith))
	    		return s.split(":")[1].trim();
	        System.out.println(s);
		}
	        
	} catch (Exception e) {}
	return "";
	}

//UPTIME FUNCTIONNALITY
	 // logging all PC activities in the emr.log file
  public void getmonitorFileData() throws IOException{
  	try {
  		String sCurrentLine;
			BufferedReader br=new BufferedReader(new FileReader("/home/emts.log"));
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   Runnable activeserver = new Runnable() {
	       public void run() {
	    	  Process p = null;
	    	try {
				p = Runtime.getRuntime().exec("src/main/resources/pcheartbeat.sh");
			 } catch (IOException e) {
					e.printStackTrace();
			}
		    	StringBuilder sb = new StringBuilder();
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    	String line="";
		       try {
				while ((line = reader.readLine())!= null) {
							sb.append(line); 
						}
				} catch (IOException e) {
					e.printStackTrace();
				}p.destroy();
	       }
	    };

	    //shutdown method to call the the startup.sh file to be executed when the
	    //JVM is shutting down so that it log shutdown informations when the module is shutting down
	    public void attachShutDownHook(){
 		 Runtime.getRuntime().addShutdownHook( new Thread(new Runnable() {
       @Override
      public void run() {
 	      Process p=null;
		     try {
			p = Runtime.getRuntime().exec("src/main/resources/pcstartup.sh");
		     } catch (IOException e) {
			e.printStackTrace();
		    }
		  StringBuilder sb = new StringBuilder();
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	String line="";
	       try{
				if ((line = reader.readLine())!= null) {
							sb.append(line); 
						}
			} catch (IOException e) {
				e.printStackTrace();
			 }
			p.destroy();
         }
       }));
   }
	// running and calling scripts for monitor heart beat infotmation
	public void getServerActivity() throws IOException, ParseException{
 	 
		 //check if day is between Monday and Friday to run the script.
		 Calendar now = Calendar.getInstance();
		 int day = now.get(Calendar.DAY_OF_WEEK);
		 if (day >= Calendar.MONDAY && day <= Calendar.FRIDAY){
       //get time that the pc should start beating
		 String string1 = "08:00:00";
	     Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
       Calendar calendar1 = Calendar.getInstance();
       calendar1.setTime(time1);
       
       //set the time that the pc should stop beating
       String string2 = "17:00:00";
	      Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
	      Calendar calendar2 = Calendar.getInstance();
	      calendar2.setTime(time2);
	      calendar2.add(Calendar.DATE, 1);
	      
	      //get the current time of each morning to compare if it is between 8am and 5pm, 
	      //and if it lies between, start logging pc activity every 15 minutes
	      Calendar currentime = Calendar.getInstance();
		  DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		  String someRandomTimes=dateFormat.format(currentime.getTime());
		  Date d= new SimpleDateFormat("HH:mm:ss").parse(someRandomTimes);
		  Calendar calendar4 = Calendar.getInstance();
		  calendar4.setTime(d);
		  calendar4.add(Calendar.DATE, 1);
   
        Date currentTime = calendar4.getTime();
       if (currentTime.after(calendar1.getTime()) && currentTime.before(calendar2.getTime())) {
       //call the executor method to run the heartbeat script every 15 minutes
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
 	      executor.scheduleAtFixedRate(activeserver, 1, 900, TimeUnit.SECONDS);
 	      attachShutDownHook(); 
 	      //System.out.println("======check if you are in this class======");
		} 
	  }  //call the getMonitorfileData to start writing logs information into the emr.log file.
		 getmonitorFileData();
   }
	
	    //methods for reading into the local file emr.log and getting its data to start calculating uptime
	   String obsId;
	  long totalheartBeats=0L;
      List<Long> heartBeatCollection = new ArrayList<Long>();
	  long heartBeatdifference=0L;
      long heartBeatdifferenceMonth=0L;
     
	  List<String> thisweeklogs=new ArrayList<String>();
	  String[] valueSplited;
	  String all;
	  double hoursWeekly =0;
	  double hoursLastWeek =0;
	  double hoursThisMonth=0;
	  double hoursLastMonth=0;
	  double totalHoursUptimeThisWeek=0;
	  double totalHoursUptimelastWeek=0;
	  double totalHoursUptimeThisMonth=0;
	  double totalHoursUptimeLastMonth=0;
	  
		 void getPCinformationMonitored() throws IOException, ParseException{
			try {
				String sCurrentLine = new String();
				BufferedReader br = new BufferedReader(new FileReader(
						"/Users/neza/emts.log"));
				//This Week range
				int firstDateThisWeek=getstartDate(0);
				int lastDateThisWeek=getendDate(-6);
				//last Week range
				int firstDateLastWeek=getstartDate(-1);
				int lastDateLastWeek=getendDate(-7);
				//this month range
				int firstDateThisMonth=getstartDate(0);
				int lastDateThisMonth=getendDate(-30);
				//last month range
				int firstDateLastMonth=getstartDate(-1);
				int lastDateLastMonth=getendDate(-30);
				
				while ((sCurrentLine = br.readLine()) != null) {
					 int firstIndexDate=Integer.parseInt(sCurrentLine.split("-")[0]);
				if(firstIndexDate >= lastDateThisWeek && firstIndexDate<= firstDateThisWeek){
					
							if (sCurrentLine.contains("HEARTBEAT")) {
								valueSplited = sCurrentLine.split(";");
								manipulateString(all);}
							 ArrayList<Double> hoursworked = new ArrayList<Double>(); 
						        hoursworked.add(hoursWeekly);
							    TreeSet<Double> set = new TreeSet<Double>(hoursworked);
							    totalHoursUptimeThisWeek=set.last();
							}
				
				if(firstIndexDate >= lastDateLastWeek && firstIndexDate<= firstDateLastWeek){
					if (sCurrentLine.contains("HEARTBEAT")) {
						valueSplited = sCurrentLine.split(";");
						manipulateString(all);}
					    ArrayList<Double> hoursworkedLastWeek = new ArrayList<Double>(); 
					    hoursworkedLastWeek.add(hoursLastWeek);
					    TreeSet<Double> setLastWeek = new TreeSet<Double>(hoursworkedLastWeek);
					    totalHoursUptimelastWeek=setLastWeek.last();  
					     }
				
				if(firstIndexDate >= lastDateThisMonth && firstIndexDate<= firstDateThisMonth){
					if (sCurrentLine.contains("HEARTBEAT")) {
						valueSplited = sCurrentLine.split(";");
						manipulateString(all);}
					    ArrayList<Double> hoursworkedThisMonth = new ArrayList<Double>(); 
					    hoursworkedThisMonth.add(hoursThisMonth);
					    TreeSet<Double> setThisMonth =new TreeSet<Double>(hoursworkedThisMonth);
					    totalHoursUptimeThisMonth=setThisMonth.last();
					     }
				if(firstIndexDate >= lastDateLastMonth && firstIndexDate<= firstDateLastMonth){
					if (sCurrentLine.contains("HEARTBEAT")) {
						valueSplited = sCurrentLine.split(";");
						manipulateString(all);}
					    ArrayList<Double> hoursworkedLastMonth = new ArrayList<Double>(); 
					    hoursworkedLastMonth.add(hoursLastMonth);
					    TreeSet<Double> setLastMonth =new TreeSet<Double>(hoursworkedLastMonth);
					    totalHoursUptimeLastMonth=setLastMonth.last();
					      }
				       }
				
				br.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	    public String getWeeklyUpTimeActivity(){
	    	try {
				getPCinformationMonitored();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("thisweekly HOURS :"+totalHoursUptimeThisWeek);
			double weeklyDailyExpectedHours=40;
			double weeklyUptimePercentage=totalHoursUptimeThisWeek*100/weeklyDailyExpectedHours;
			BigDecimal weeklypercent = new BigDecimal(weeklyUptimePercentage);
			weeklypercent = weeklypercent.setScale(2, RoundingMode.HALF_UP);
			//System.out.println("thisweek Uptime percentage :"+weeklypercent+ " %");
			totalWeeklyuptime=weeklypercent.toString();
			return  totalWeeklyuptime;
	   }
	    
	    public String getLastWeekUpTimeActivity(){
	    	try {
				getPCinformationMonitored();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("lastweek HOURS :"+totalHoursUptimelastWeek);
			double weeklyDailyExpectedHours=40;
			double lastWeekUptimePercentage=totalHoursUptimelastWeek*100/weeklyDailyExpectedHours;
			BigDecimal lastWeekpercent = new BigDecimal(lastWeekUptimePercentage);
			lastWeekpercent = lastWeekpercent.setScale(2, RoundingMode.HALF_UP);
			//System.out.println("last week Uptime percentage :"+lastWeekpercent+ " %");
			totalLastWeekyuptime=lastWeekpercent.toString();
			return  totalLastWeekyuptime;
	   }
	    
	    public String getThisMonthUpTimeActivity(){
	    	try {
				getPCinformationMonitored();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("ThisMonth HOURS :"+totalHoursUptimeThisMonth);
			double monthlyDailyExpectedHours=240;
			double monthlyUptimePercentage=totalHoursUptimeThisMonth*100/monthlyDailyExpectedHours;
			BigDecimal ThisMonthpercent = new BigDecimal(monthlyUptimePercentage);
			ThisMonthpercent = ThisMonthpercent.setScale(2, RoundingMode.HALF_UP);
			//System.out.println("last week Uptime percentage :"+ThisMonthpercent+ " %");
			totalThisMonthyuptime=ThisMonthpercent.toString();
			return  totalThisMonthyuptime;
	   }
	    
	    public String getLastMonthUpTimeActivity(){
	    	try {
				getPCinformationMonitored();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("LastMonth HOURS :"+totalHoursUptimeLastMonth);
			double monthlyDailyExpectedHours=240;
			double monthlyUptimePercentage=totalHoursUptimeLastMonth*100/monthlyDailyExpectedHours;
			BigDecimal LastMonthpercent = new BigDecimal(monthlyUptimePercentage);
			LastMonthpercent = LastMonthpercent.setScale(2, RoundingMode.HALF_UP);
			//System.out.println("last week Uptime percentage :"+LastMonthpercent+ " %");
			totalLastMonthyuptime=LastMonthpercent.toString();
			return  totalLastMonthyuptime;
	   }
	   
	     String manipulateString(String line) throws ParseException{
			String obsId = valueSplited[0];
			String[] valueSpliteds = obsId.split("-");
			String me = valueSpliteds[1];
			String firstOc = me.substring(0, 2);
			String secondOc = me.substring(2, 4);
			String thirdOc = me.substring(4, 6);
			String all = firstOc.concat(":").concat(secondOc)
					.concat(":").concat(thirdOc);
			if (all.compareTo("08:00:00") >= 0 && all.compareTo("17:00:00") <= 0) {
				DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
				date = df.parse(obsId);
				if(date!=null){
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
					if(!(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)) {
					//System.out.println("date: "+date);
						long weeklydatevar=date.getTime();
						heartBeatCollection.add(weeklydatevar);
						
						for(int i=0; i<heartBeatCollection.size(); i++){
							
							if(i > 0 & heartBeatCollection.size()!=0){
							 heartBeatdifference = heartBeatCollection.get(i)- heartBeatCollection.get(i-1);
							List<Long> heartBeatsDifferenceList=new ArrayList<Long>();
							heartBeatsDifferenceList.add(heartBeatdifference);
							 totalheartBeats = totalheartBeats + heartBeatdifference;
							   }
							  }
						 hoursWeekly   = (int) ((totalheartBeats / (1000*60*60)) % 24);
						 hoursLastWeek  = (int) ((totalheartBeats / (1000*60*60)) % 24);
						 hoursThisMonth = (int) ((totalheartBeats / (1000*60*60)) % 24);
						 hoursLastMonth= (int) ((totalheartBeats / (1000*60*60)) % 24);
							}
					}
				
				
				
			}
			
			return all;
			
		}
	    public int getstartDate(int days){
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, days);
	        int year1 = cal.get(Calendar.YEAR);
	         int month1 = cal.get(Calendar.MONTH) + 1; 
	         int day1 = cal.get(Calendar.DAY_OF_MONTH) ;          
	        
	        int inStartdate=Integer.parseInt(year1+""+appendZeroToNumber(month1)+""+appendZeroToNumber(day1));
			return inStartdate;
			
		}
		
		public int getendDate(int days){
			
		Calendar cal2 = Calendar.getInstance();
	    cal2.add(Calendar.DATE, days);
	    int day2 = cal2.get(Calendar.DAY_OF_MONTH);
	    int month2 = cal2.get(Calendar.MONTH) + 1; 
	    int year2 = cal2.get(Calendar.YEAR);          
	    
	    int intEnddate=Integer.parseInt(year2+""+appendZeroToNumber(month2)+""+appendZeroToNumber(day2));
		return intEnddate;
			
		}
	   public String appendZeroToNumber(int number){
			 
			if(number<10){
				String numberadded="0"+number;
				return numberadded;
			}
			else{
				return number+"";
			}
			
		}


	public Map<String, Map<String, String>> getExtraSystemInformation(){
		
		Map<String, Map<String, String>> extraSystemInformation = new HashMap<String, Map<String,String>>();
		
		extraSystemInformation.put("SystemInfo.title.hardDriverInformation", new LinkedHashMap<String, String>() {
        	
        private static final long serialVersionUID = 1L;
			
			{
				put("SystemInfo.hardDriverInformation.totalMemory", getTotalPhyisycalMemory());
				put("SystemInfo.hardDriverInformation.freeMemory", getFreePhyisycalMemory());
				put("SystemInfo.hardDriverInformation.availableMemory", getAvailablePhyisycalMemory());
				put("SystemInfo.hardDriverInformation.usedMemory", getUsedPhyisycalMemory());				
			}
		});
        
        
		extraSystemInformation.put("SystemInfo.title.emrPatientsDataInformation", new LinkedHashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {
                
             	put("SystemInfo.emrDataInformation.numberOfOrders", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("orders"));
             	put("SystemInfo.emrDataInformation.numberOfPatients", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("patients"));
             	put("SystemInfo.emrDataInformation.numberOfEncounters", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("encounters"));
             	put("SystemInfo.emrDataInformation.numberOfObservations", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("observations"));
                
            }
        });
		
		
		extraSystemInformation.put("SystemInfo.title.emrSyncDataInformation", new LinkedHashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {                
             	put("SystemInfo.emrSyncDataInformation.pendingRecords", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("pendingRecords"));
             	put("SystemInfo.emrSyncDataInformation.failedRecord", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("failedRecord"));
             	put("SystemInfo.emrSyncDataInformation.failedObject", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("failedObject"));
             	      
            }
        });
		
		extraSystemInformation.put("SystemInfo.title.softwareVersionInformation", new LinkedHashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {                
            	put("SystemInfo.softwareVersionInformation.ubuntu", ""+getVersion("lsb_release --release").split(":")[1].trim());
            	put("SystemInfo.softwareVersionInformation.mysql", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("mysqlVersion").split("-")[0]);
             	put("SystemInfo.softwareVersionInformation.tomcat", ""+getVersion("java -cp "+Context.getAdministrationService().getGlobalProperty("emrmonitor.tomcatPath")+"/lib/catalina.jar org.apache.catalina.util.ServerInfo").split(":")[1].trim());
             	put("SystemInfo.softwareVersionInformation.Firefox", ""+getVersion("firefox -version"));
             	put("SystemInfo.softwareVersionInformation.Chrome", ""+getVersion("google-chrome -version"));
             	
            }
        });
		
		extraSystemInformation.put("SystemInfo.title.connectionInformation", new LinkedHashMap<String, String>() {

            private static final long serialVersionUID = 1L;
            
            
            {                
            	put("SystemInfo.connectionInformation.connectedToTheServer", ""+checkConnection());
            	
            }
        });		
		extraSystemInformation.put("SystemInfo.title.cpuInformation", new LinkedHashMap<String, String>() {

            private static final long serialVersionUID = 1L;
            {                
            	put("SystemInfo.cpuInformation.cpuLoadAverage", ""+getCPULoadAverage());
            	put("SystemInfo.cpuInformation.architecture", ""+getCPUInfo("Architecture:"));
            	put("SystemInfo.cpuInformation.cpuopmodes", ""+getCPUInfo("CPU op-mode(s):"));
            	put("SystemInfo.cpuInformation.numberOfCPU", ""+getCPUInfo("CPU(s):"));
            	put("SystemInfo.cpuInformation.cpusize", ""+getCPUInfo("CPU MHz"));
             }
        });		
		
          extraSystemInformation.put("SystemInfo.title.uptimeMemoryInformation", new LinkedHashMap<String, String>() {
        	
	        private static final long serialVersionUID = 1L;
				{
					put("SystemInfo.uptimeInformation.ThisWeekUptimePercentage", getWeeklyUpTimeActivity());
					put("SystemInfo.uptimeInformation.LastWeekUptimePercentage", getLastWeekUpTimeActivity());
					put("SystemInfo.uptimeInformation.ThiMonthUptimePercentage", getThisMonthUpTimeActivity());
					put("SystemInfo.uptimeInformation.LastMonthUptimePercentage", getLastMonthUpTimeActivity());				
				}
			});
				
		
		return extraSystemInformation;		
	}
	
}