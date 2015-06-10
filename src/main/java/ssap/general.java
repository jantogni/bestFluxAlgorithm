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
	
	public static boolean isBetween(Double x, double d, double e) {
		  return d <= x && x <= e;
	}
	
	public static int almaBand(double freq){
		if(isBetween(freq, 84E09, 116E09))
			return 3;
		else
			if(isBetween(freq, 125E09, 163E09))
				return 4;
			else
				if(isBetween(freq, 163E09, 211E09))
					return 5;
				else
					if(isBetween(freq, 211E09, 275E09))
						return 6;
					else
						if(isBetween(freq, 275E09, 373E09))
							return 7;
						else
							if(isBetween(freq, 385E09, 500E09))
								return 8;
							else
								if(isBetween(freq, 602E09, 720E09))
									return 9;
								else
									if(isBetween(freq, 787E09, 950E09))
										return 10;
		return 0;
	}
	
}
