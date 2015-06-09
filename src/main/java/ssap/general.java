package ssap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class general {
	public general(){		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Hashtable convertToHashtable(Object obj){    			        			
		try{
			Hashtable cast = (Hashtable)obj;
			
			SimpleDateFormat formatter_db = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);
			Date date_db = formatter_db.parse(cast.get("date_observed").toString());
			
			cast.remove("date_observed");
			cast.put("date_observed", date_db);
			cast.put("date.getTime", date_db.getTime());
			
			return cast;
		}catch(ParseException e){
			System.out.println("Error trying to cast date");
			e.printStackTrace();
			
			return (Hashtable)null;
		}
	}
	
	public static boolean isBetween(Double x, int lower, int upper) {
		  return lower <= x && x <= upper;
	}
	
	public static int almaBand(Double freq){
		if(isBetween(freq, 84, 116))
			return 3;
		else
			if(isBetween(freq, 125, 163))
				return 4;
			else
				if(isBetween(freq, 163, 211))
					return 5;
				else
					if(isBetween(freq, 211, 275))
						return 6;
					else
						if(isBetween(freq, 275, 373))
							return 7;
						else
							if(isBetween(freq, 385, 500))
								return 8;
							else
								if(isBetween(freq, 602, 720))
									return 9;
								else
									if(isBetween(freq, 787, 950))
										return 10;
		return 0;
	}
	
}
