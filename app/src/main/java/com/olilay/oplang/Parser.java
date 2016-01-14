package com.olilay.oplang;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Oliver Layer on 12.01.2016.
 */
public abstract class Parser
{
    public static DateFormat DateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    protected String content;
    protected boolean isTeacherPlan;

    protected List<Change> changes;
    protected List<String> identifiers;


    public Parser(String content, boolean isTeacherPlan)
    {
        this.content = content;
        this.isTeacherPlan = isTeacherPlan;

        changes = new ArrayList<Change>();
        identifiers = new ArrayList<String>();
    }


    public Date getRefreshDate() throws ParseException
    {
        String[] lines = splitToMultipleLines(content);
        return DateFormat.parse(lines[1]);
    }

    public Date getPlanDate() throws ParseException
    {
        String[] lines = splitToMultipleLines(content);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        return dateFormat.parse(lines[0].split(" ")[4]);
    }

    public abstract List<Change> getChanges();

    protected abstract void getChangesFromSinglePlan(String planText) throws Exception;

    protected abstract int getStartIndex(String[] planTexts);


    public static String[] splitToMultipleLines(String content)
    {
        List<String> lines = new ArrayList<String>();

        Scanner scanner = new Scanner(content);

        while (scanner.hasNextLine())
        {
            lines.add(scanner.nextLine());
        }
        scanner.close();

        return lines.toArray(new String[1]);
    }

    protected List<Change> removeDuplicates(List<Change> changes)
    {
        List<Change> newChanges = new ArrayList<Change>();
        for(int i = 0; i < changes.size(); i++)
        {
            boolean duplicate = false;
            for(int j = 0; j < newChanges.size(); j++)
            {
                if(changes.get(i).equals(newChanges.get(j)))
                {
                    duplicate = true;
                }
            }

            if(!duplicate)
            {
                newChanges.add(changes.get(i));
            }
        }

        return newChanges;
    }

    protected String[] splitAtNumber(String s)
    {
        return s.split("(?<=[0-9])(?=[a-zA-Z])");
    }

}
