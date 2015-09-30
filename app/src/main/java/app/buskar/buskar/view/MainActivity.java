package app.buskar.buskar.view;

import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import app.buskar.buskar.R;

public class MainActivity extends ActionBarActivity {

    private TextView fromHome, fromSchool;
    private static List<Integer> rustic_g, rustic_b, gleason, barnes, rustic_g_s, rustic_b_s, gleason_s, barnes_s, rustic_g_sun, rustic_b_sun, gleason_sun, barnes_sun;

    public static int getNext(int a, List<Integer> list)
    {
        int low = 0;
        int high = list.size() - 1;
        while(high >= low)
        {
            int middle = (low + high) / 2;
            int cent = list.get(middle);
            if(list.get(middle) <= a)
            {
                if(middle ==list.size()-1)
                    return -1;
                if(list.get(middle+1) > a)
                    return middle+1;
                low = middle + 1;
            }
            else
            {
                if(middle== 0)
                    return -1;
                if(list.get(middle-1) < a)
                    return middle;
                high = middle - 1;
            }
        }
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            InputStream inputStream = getResources().openRawResource(R.raw.timetable);
            POIFSFileSystem myFileSystem = new POIFSFileSystem(inputStream);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            setWeekdays(myWorkBook);
            setSaturday(myWorkBook);
            setSunday(myWorkBook);

            setDisplay();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setDisplay() throws Exception
    {
        fromSchool = (TextView)findViewById(R.id.textView);
        fromHome = (TextView)findViewById(R.id.textView2);

        SimpleDateFormat simpDate = new SimpleDateFormat("kk:mm:ss");
        Date now = new Date();
        String currentTime=simpDate.format(now);
        String token[] = currentTime.split(":");
        int currTime = Integer.parseInt(token[0] + token[1]);
        if(Integer.parseInt(token[2])>0)
            currTime++;

        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK);

        List<Integer> home = new ArrayList<Integer>();
        List<Integer> school = new ArrayList<Integer>();

        if(day_of_week == 1) {
            home = rustic_g_sun;
            school = gleason_sun;
        }

        else if(day_of_week == 7) {
            home = rustic_g_s;
            school = gleason_s;
        }

        else {
            home = rustic_g;
            school = gleason;
        }

        int next=getNext(currTime, home);
        String toPrint = "";
        if(next == -1)
        {
            toPrint = "nextDay\n";
            if(day_of_week == 7) {
                school = gleason_sun;
            }
            else if(day_of_week == 6) {
                school = gleason_s;
            }
            else {
                school = gleason;
            }
            next = 0;
        }
        int temp = school.get(next);
        TimeHandler time = new TimeHandler(temp % 100, temp / 100);
        toPrint = toPrint + time.toString();
        fromHome.setText(toPrint);

        toPrint = "";
        next=getNext(currTime, school);
        if(next== -1) {
            toPrint = "nextDay\n";
            if(day_of_week == 7) {
                school = gleason_sun;
            }
            else if(day_of_week == 6) {
                school = gleason_s;
            }
            else {
                school = gleason;
            }
            next = 0;
        }
        temp = school.get(next);
        time = new TimeHandler(temp % 100, temp / 100);
        toPrint = toPrint + time.toString();
        fromSchool.setText(toPrint);
    }

    public  void setWeekdays(HSSFWorkbook myWorkBook) {

        HSSFSheet mySheet = myWorkBook.getSheet("Weekdays");
        rustic_g = new ArrayList<Integer>();
        rustic_b = new ArrayList<Integer>();
        gleason = new ArrayList<Integer>();
        barnes = new ArrayList<Integer>();

        Iterator<Row> rowIterator = mySheet.iterator();
        Row row = rowIterator.next();
        rustic_g=common(row);;

        row = rowIterator.next();
        rustic_b=common(row);

        row = rowIterator.next();
        gleason=common(row);

        row = rowIterator.next();
        barnes=common(row);
    }

    public void setSaturday(HSSFWorkbook myWorkBook) {
        HSSFSheet mySheet = myWorkBook.getSheet("Saturday");
        rustic_g_s = new ArrayList<Integer>();
        rustic_b_s = new ArrayList<Integer>();
        gleason_s = new ArrayList<Integer>();
        barnes_s = new ArrayList<Integer>();

        Iterator<Row> rowIterator = mySheet.iterator();
        Row row = rowIterator.next();
        rustic_g_s=common(row);;

        row = rowIterator.next();
        rustic_b_s=common(row);

        row = rowIterator.next();
        gleason_s=common(row);

        row = rowIterator.next();
        barnes_s=common(row);
    }

    public void setSunday(HSSFWorkbook myWorkBook) {
        HSSFSheet mySheet = myWorkBook.getSheet("Sunday");
        rustic_g_sun = new ArrayList<Integer>();
        rustic_b_sun = new ArrayList<Integer>();
        gleason_sun = new ArrayList<Integer>();
        barnes_sun = new ArrayList<Integer>();

        Iterator<Row> rowIterator = mySheet.iterator();
        Row row = rowIterator.next();
        rustic_g_sun=common(row);;

        row = rowIterator.next();
        rustic_b_sun=common(row);

        row = rowIterator.next();
        gleason_sun=common(row);

        row = rowIterator.next();
        barnes_sun=common(row);
    }

    public static ArrayList<Integer> common(Row row ) {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        Iterator<Cell> cellIterator = row.cellIterator();
        while(cellIterator.hasNext())
        {
            Cell cell = cellIterator.next();
            int temp1=(int)cell.getNumericCellValue();
            temp.add(temp1);
        }
        return temp;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class TimeHandler
    {
        public TimeHandler(int minutes, int hours)
        {
            this.minutes = minutes;
            this.hours = hours;
        }

        public String toString24()
        {
            if(minutes<10)
                return hours+":0"+minutes;
            return hours+":"+minutes;
        }

        public String toString()
        {
            String AM_PM= "AM";
            int currHrs = hours;
            if(currHrs>12)
            {
                AM_PM = "PM";
                currHrs = hours-12;
            }
            if(minutes<10)
                return hours+":0"+minutes+AM_PM;
            return currHrs+":"+minutes+AM_PM;
        }

        public final int seconds = 0;
        public int minutes;
        public int hours;
    }
}