import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

;

public class BillingPeriod {


    public static List<Integer> getWeekEnds (int month, int year){
        YearMonth billingMonthYear = YearMonth.of(year,month);
        List<Integer> result = new ArrayList<>();
        for (LocalDate day = billingMonthYear.atDay(1);
             day.isBefore(billingMonthYear.atEndOfMonth())
                     || day.isEqual(billingMonthYear.atEndOfMonth());
             day = day.plusDays(1)){
            if (day.getDayOfWeek().equals(DayOfWeek.SUNDAY) || day.getDayOfWeek().equals(DayOfWeek.SATURDAY)){
                result.add(day.getDayOfMonth());
            }
        }
        return result;
    }
    public static List<Integer> getPublickHollidays (int month, int year){
        YearMonth billingMonthYear = YearMonth.of(year,month);
        List<Integer> result = new ArrayList<>();

        for (LocalDate day = billingMonthYear.atDay(1);
             day.isBefore(billingMonthYear.atEndOfMonth())
                     || day.isEqual(billingMonthYear.atEndOfMonth());
             day = day.plusDays(1)){
            try {
                String address = "https://holidays.abstractapi.com/v1/?api_key=fb86474cbe5d4d98a6e5013d880c8ff1&country=ES&year=" + year + "&month=" + month + "&day=" + day.getDayOfMonth();
                Content content = Request.Get(address)
                        .execute().returnContent();
                Thread.sleep(1000);
                JSONParser jp = new JSONParser();
                JSONObject jo = new JSONObject();
                JSONArray ja = (JSONArray) jp.parse(content.asString());
                if (!ja.isEmpty()){
                    jo = (JSONObject) ja.get(0);
                    String location = (String) jo.get("location");
                    String type = (String) jo.get("type");
                    String dateDay = (String) jo.get("date_day");
                    if (type.equals("National")){
                        result.add(Integer.parseInt(dateDay));
                    }else if (type.equals("Local holiday") && location.equals("Spain - Valencia")){
                        result.add(Integer.parseInt(dateDay));
                    }
                }

            }
            catch (IOException error) { System.out.println(error); } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
