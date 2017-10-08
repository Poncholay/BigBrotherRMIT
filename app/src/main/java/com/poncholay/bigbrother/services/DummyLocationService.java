package com.poncholay.bigbrother.services;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.poncholay.bigbrother.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class DummyLocationService {
   private static final String LOG_TAG = DummyLocationService.class.getName();
   final private LinkedList<FriendLocation> locationList = new LinkedList<>();

   // singleton support
   private static class LazyHolder {
      static final DummyLocationService INSTANCE = new DummyLocationService();
   }

   public static DummyLocationService getSingletonInstance() {
      return LazyHolder.INSTANCE;
   }

   private DummyLocationService() {}

   public static class FriendLocation {
      public Date time;
      public String id;
      public String name;
      public double latitude;
      public double longitude;

      @Override
      public String toString() {
         return String.format(Locale.US,
                 "Time=%s, id=%s, name=%s, lat=%.5f, long=%.5f",
                 DateFormat.getTimeInstance(DateFormat.MEDIUM).format(time),
                 id, name, latitude, longitude);
      }
   }

   /**
    * check if the source time is with the range of target time +/- minutes and seconds
    * @param source
    * @param target
    * @param periodMinutes
    * @param periodSeconds
    * @return
    */
   private boolean timeInRange(Date source, Date target, int periodMinutes, int periodSeconds) {
      Calendar sourceCal = Calendar.getInstance();
      Calendar targetCalStart = Calendar.getInstance();
      Calendar targetCalEnd = Calendar.getInstance();

      sourceCal.setTime(source);
      targetCalStart.setTime(target);
      targetCalEnd.setTime(target);

      sourceCal.set(Calendar.DAY_OF_MONTH, targetCalStart.get(Calendar.DAY_OF_MONTH));
      sourceCal.set(Calendar.MONTH, targetCalStart.get(Calendar.MONTH));
      sourceCal.set(Calendar.YEAR, targetCalStart.get(Calendar.YEAR));

      targetCalStart.set(Calendar.MINUTE, targetCalStart.get(Calendar.MINUTE) - periodMinutes);
      targetCalStart.set(Calendar.SECOND, targetCalStart.get(Calendar.SECOND) - periodSeconds);

      targetCalEnd.set(Calendar.MINUTE, targetCalEnd.get(Calendar.MINUTE) + periodMinutes);
      targetCalEnd.set(Calendar.SECOND, targetCalEnd.get(Calendar.SECOND) + periodMinutes);

      // return if source time in the target range
      return sourceCal.after(targetCalStart) && sourceCal.before(targetCalEnd);
   }

   /**
    * called internally before usage
    * @param context
    */
   private void parseFile(Context context) {
      locationList.clear();
      // resource reference to dummy_data.txt in res/raw/ folder of your project
      try (Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.dummy_data))) {
         // match comma and 0 or more whitepace (to catch newlines)
         scanner.useDelimiter(",\\s*");
         while (scanner.hasNext()) {
            FriendLocation friend = new FriendLocation();
            friend.time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(scanner.next());
            friend.id = scanner.next();
            friend.name = scanner.next();
            friend.latitude = Double.parseDouble(scanner.next());
            friend.longitude = Double.parseDouble(scanner.next());
            locationList.addLast(friend);
         }
      } catch (Resources.NotFoundException e) {
         Log.e(LOG_TAG, "File Not Found Exception Caught");
      } catch (ParseException e) {
         Log.e(LOG_TAG, "ParseException Caught : " + e.getMessage());
      }
   }

   /**
    * the main method you can call periodcally to get data that matches a given time period
    * time +/- period minutes/seconds to check
    * @param time
    * @param periodMinutes
    * @param periodSeconds
    * @return
    */
   public List<FriendLocation> getFriendLocationsForTime(Context context, Date time, int periodMinutes, int periodSeconds) {
      parseFile(context);
      List<FriendLocation> returnList = new ArrayList<>();
      for (FriendLocation friend : locationList) {
		  if (timeInRange(friend.time, time, periodMinutes, periodSeconds)) {
			  returnList.add(friend);
		  }
	  }
      return returnList;
   }
}
