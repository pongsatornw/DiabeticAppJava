package com.example.yggdrasil.realdiabeticapp;

import android.util.Log;

import java.util.StringTokenizer;

public class Record {
	private static final String TAG = "Meter Record";
	public static final int SIZE = 6;
	public static final int EVENT_BMEL = 16;
	public static final int EVENT_MEAL = 8;
	public static final int EVENT_MEDI = 4;
	public static final int EVENT_EXER = 2;
	public static final int EVENT_ATTN = 1;
	private int year;
	private int mon;
	private int day;
	private int temp;
	private int result;
	private int event;
	private int hour;
	private int min;
	private int meal=4;
	private int ampm;
	private int interpret=0;
    String minConvert ="";
	
	public Record(byte[] buf, int start) {
		super();
		year = (buf[start] >> 1);
		mon  =( (buf[start] & 0x01) << 3 ) + ( (buf[start+1] & 0xE0) >>5 );
		day = buf[start+1] & 0x1F;
		temp = (buf[start+2] >> 2) & 0x3F;
		result = ( (buf[start+2] & 0x03) << 8 ) + ( buf[start+3] & 0xFF );//0xFF correction
		event = (buf[start+4]&0xF8) >> 3;
		hour = ((buf[start+4] & 0x07) << 2) + ( (buf[start+5] & 0xC0) >> 6 );
		min = buf[start+5] & 0x3F;
		ampm = 0;
	}
	public Record(int year, int mon, int day, int temp, int result, int event, int hour, int min) {
		this.year = year;
		this.mon = mon;
		this.day = day;
		this.temp = temp;
		this.result = result;
		this.event = event;
		this.hour = hour;
		this.min = min;
		ampm = 0;
	}
	
	public String convertToLastSync(){
		return	year + "-" 
					+ mon + "-"
					+ day + "-"
					+ temp + "-"
					+ result + "-"
					+ event + "-"
					+ hour + "-"
					+ min;
	}
	public static Record parseLastSync(String value){
		Log.d(TAG, "Parse Last Sync: " + value);
		if (value == null || value.equals(""))
			return null;
		Record rec = null;
		try {
			StringTokenizer st = new StringTokenizer(value, "-");
			int sYear = Integer.parseInt(st.nextToken());
			int sMon = Integer.parseInt(st.nextToken());
			int sDay = Integer.parseInt(st.nextToken());
			int sTemp = Integer.parseInt(st.nextToken());
			int sResult = Integer.parseInt(st.nextToken());
			int sEvent = Integer.parseInt(st.nextToken());
			int sHour = Integer.parseInt(st.nextToken());
			int sMin = Integer.parseInt(st.nextToken());
			rec = new Record(sYear, sMon, sDay, sTemp, sResult, sEvent, sHour, sMin); 
		} catch (Exception e) {
			//Log.printException(e);
		}
		
		return rec;
	}

	public int getYear() {
		return year+2000;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setInterpret(int role) {
		this.interpret = role;
	}

	public int getInterpret() {
		return interpret;
	}
	
	public int getMon() {
		return mon;
	}

	public void setMon(int mon) {
		this.mon = mon;
	}

	public String getDay() {
		return day+"/"+mon+"/20"+year;
	}

    public int getRealDay() {
        return day;
    }

    public String getTimeText() {
        if(min<10)minConvert="0"+min;
        else minConvert=""+min;
        return hour+"."+minConvert+" น.";
    }

	public String getDateText() {
		if(min<10)minConvert="0"+min;
		else minConvert=""+min;
		return  day+"/"+mon+"/20"+year+"   "+hour+"."+minConvert+" น.";
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getAmpm() {
		return ampm;
	}

	public void setAmpm(int ampm) {
		this.ampm = ampm;
	}

	public int getEvent() {
		return event;
	}

	public void setEvent(int event) {
		this.event = event;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	public void setMeal(int meal) {
		this.meal = meal;
	}

	public int getMeal() {
		return meal;
	}

	public Object getDetail(int position) {

		if(position==0){
			return getEvent();
		}else if(position==1){
			return getResult();
		}else if(position==2){
			return getResult();
		}else if(position==3){
			return getResult();
		}else return "error";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int res = 1;
		res = prime * res + ampm;
		res = prime * res + day;
		res = prime * res + event;
		res = prime * res + hour;
		res = prime * res + min;
		res = prime * res + mon;
		res = prime * res + result;
		res = prime * res + year;
		return res;
	}

	@Override
	public String toString() {
        if(min<10)minConvert="0"+min;
        else minConvert=""+min;
        String convert = "วันที่ "+day+"/"+mon+"/"+"20"+year+"\nเวลาที่ตรวจวัด: "+hour+"."+minConvert+" น."+"\nค่าระดับน้ำตาลในเลือด: "+result+
                " mg/dL"+"\nEvent code:"+event;
        return convert;
		/*return "&yr=" + year + "&mo=" + mon + "&da=" + day + "&val=" + result
				+ "&act=" + event + "&hr=" + hour	+ "&min=" + min;*/
	}
	
	public byte[] toBytes() {
		byte buf[] = new byte[SIZE];
		buf[0] = (byte)(((year << 1) & 0xfe) + ((mon >> 3) & 0x01));
		buf[1] = (byte)(((mon << 5) & 0xe0) + (day & 0x1f));
		buf[2] = (byte)(((temp & 0x3f) << 2) + ((result >> 8) & 0x03));
		buf[3] = (byte)(result & 0xff);
		buf[4] = (byte)(((event << 3) & 0xf8) + ((hour >> 2) & 0x07));
		buf[5] = (byte)(((hour << 6) & 0xc0) + (min & 0x3f));
		return buf;
	}
}
