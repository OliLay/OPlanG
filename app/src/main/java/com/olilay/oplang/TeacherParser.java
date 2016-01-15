package com.olilay.oplang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oliver Layer on 12.01.2016.
 */
public class TeacherParser extends Parser
{
    private int currentIdentifier;
    private List<String> rooms = new ArrayList<String>();
    private List<String> hours = new ArrayList<String>();
    private List<Change> courseDetails = new ArrayList<Change>();
    private List<String> remarks = new ArrayList<String>();

    public TeacherParser(String content)
    {
        super(content, true);

        currentIdentifier = 0;
    }


    @Override
    public List<Change> getChanges()
    {
        if(content.contains("Kommentar")) //make sure we actually have a table
        {
            String changeText;

            if(content.contains("Aufsichten:"))
            {
                changeText = content.split("Kommentar")[1].split("Aufsichten")[0]; //we have to split the whole PDF twice to get the part where the changes table is
            }
            else
            {
                changeText = content.split("Kommentar")[1].split("Pausenordnungsdienst:")[0]; //we have to split the whole PDF twice to get the part where the changes table is
            }

            String[] changeTextsPlans = new String[1];
            if(changeText.contains("Otfried-Preußler-Gymnasium Pullach"))
            {
                changeTextsPlans = changeText.split("Otfried-Preußler-Gymnasium Pullach"); //we've got multiple plans
            }
            else
            {
                changeTextsPlans[0] = changeText; //we've got only one plan
            }

            for (String changeTextsPlan : changeTextsPlans)
            {
                try
                {
                    getChangesFromSinglePlan(changeTextsPlan); //parse each plan separately
                }
                catch(Exception e)
                {
                    new ExceptionSender(e);
                }
            }
        }
        return changes;
    }

    @Override
    protected void getChangesFromSinglePlan(String planText) throws Exception
    {
        String[] planTexts = splitToMultipleLines(planText);

        int courseStartIndex = getStartIndex(planTexts);

        for(int i = 1; i < courseStartIndex; i++)
        {
            identifiers.add(planTexts[i]);
        }

        for(int i = courseStartIndex; i < planTexts.length; i++)
        {
            String current = planTexts[i];

            if (current != null && current.length() != 0)
            {
                String[] splittedCurrent = current.split(" ");

                if(current.startsWith("  ")) //remark
                {
                    remarks.add(current);

                    if(remarks.size() == hours.size()) //Teacher row completed
                    {
                        for(int j = 0; j < hours.size(); j++)
                        {
                            Change courseDetail;

                            if(courseDetails.size() - 1 >= j) //sometimes there can be no courseDetails at all
                            {
                                courseDetail = courseDetails.get(j);
                            }
                            else
                            {
                                courseDetail = new Change("", "", 0, "", "", "", "", "");
                            }

                            int hour = courseDetail.getHour();

                            if(hour == 0) { hour = Integer.parseInt(hours.get(j)); }

                            changes.add(new Change(courseDetail.getAffectedClasses().get(0), courseDetail.getCourse(), hour, courseDetail.getSubject(), courseDetail.getOldTeacher(), identifiers.get(currentIdentifier), "", remarks.get(j)));
                        }

                        rooms.clear();
                        hours.clear();
                        courseDetails.clear();
                        remarks.clear();

                        currentIdentifier += 1;
                    }
                }
                else if(splittedCurrent.length == 2) //course detail, e.g. 6ce St
                {
                    courseDetails.add(new Change(splittedCurrent[0], splittedCurrent[0], 0, "", splittedCurrent[1], "", "0", ""));
                }
                else if(splittedCurrent.length == 3) //course detail, e.g. 6ce St Sw
                {
                    courseDetails.add(new Change(splittedCurrent[0], splittedCurrent[0], 0, splittedCurrent[2], splittedCurrent[1], "", "0", ""));
                }
                else if(splittedCurrent.length == 4) //course detail, e.g. 076 10a Cc Sk
                {
                    courseDetails.add(new Change(splittedCurrent[1], splittedCurrent[1], 0, splittedCurrent[3], splittedCurrent[2], "", splittedCurrent[0], ""));
                }
                else if(splittedCurrent.length == 5) //course detail, e.g. 6 518 9e Gp D
                {
                    changes.add(new Change(splittedCurrent[2], splittedCurrent[2], Integer.parseInt(splittedCurrent[0]), splittedCurrent[4], splittedCurrent[3], identifiers.get(currentIdentifier), splittedCurrent[1], ""));

                    currentIdentifier += 1;
                }
                else if(splittedCurrent.length >= 6) //course detail, e.g. 6 518 9e Gp D Raumänderung
                {
                    if(splittedCurrent.length > 6)
                    {
                        String[] detailedSplit = current.split("  ");

                        changes.add(new Change(splittedCurrent[2], splittedCurrent[2], Integer.parseInt(splittedCurrent[0]), splittedCurrent[4], splittedCurrent[3], identifiers.get(currentIdentifier), splittedCurrent[1], detailedSplit[1]));
                    }
                    else
                    {
                        changes.add(new Change(splittedCurrent[2], splittedCurrent[2], Integer.parseInt(splittedCurrent[0]), splittedCurrent[4], splittedCurrent[3], identifiers.get(currentIdentifier), splittedCurrent[1], splittedCurrent[5]));
                    }

                    currentIdentifier += 1;
                }
                else
                {
                    if((current.length() == 1 || current.length() == 2) && !current.contains(" ")) //hour
                    {
                        hours.add(current);
                    }
                    else if(current.length() == 3) //room
                    {
                        rooms.add(current);
                    }
                }
            }
        }
    }

    @Override
    protected int getStartIndex(String[] planTexts)
    {
        List<String> teachers = new ArrayList<String>();

        for(int i = 0; i < planTexts.length; i++)
        {
            String current = planTexts[i];

            if(current != null && current.length() > 0)
            {
                if(teachers.size() > 0 && containsNumbers(current))
                {
                    return i;
                }
                else
                {
                    teachers.add(current);
                }
            }
        }

        return 0;
    }


    private boolean containsNumbers(String s)
    {
        String[] numbers = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        for(int i = 0; i < numbers.length; i++)
        {
            if(s.contains(numbers[i]))
            {
                return true;
            }
        }

        return false;
    }
}
