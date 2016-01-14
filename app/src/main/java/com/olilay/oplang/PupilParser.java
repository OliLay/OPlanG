package com.olilay.oplang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oliver Layer on 08.10.2015.
 */
public class PupilParser extends Parser
{

    public PupilParser(String content)
    {
        super(content, false);
    }


    @Override
    public List<Change> getChanges()
    {
        if(content.contains("Kommentar")) //make sure we actually have a table
        {
            String changeText = content.split("Kommentar")[1].split("Sonstiges:")[0]; //we have to split the whole PDF twice to get the part where the changes table is

            String[] changeTextsPlans = new String[1];
            if(changeText.contains("Otfried-Preußler-Gymnasium Pullach") && changeText.contains("Vertretungen:"))
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

        List<String> courseNames = new ArrayList<String>();
        List<String> courseDetails = new ArrayList<String>();
        List<String> exchangeTeachers = new ArrayList<String>();
        List<String> newRooms = new ArrayList<String>();
        List<String> remarks = new ArrayList<String>();

        int currentTableDepth = 0; //how big the current parsed row is
        int currentExchangeTeachersDepth = 0;
        int currentRoomDepth = 0;

        for(int i = 0; i < courseStartIndex; i++) //this will get all the classes on that page
        {
            String current = planTexts[i];

            if(current != null && current.length() > 1 && !current.contains("Vertretungen:"))
            {
                identifiers.add(current);
            }
        }

        //now the courseStartIndex gives us the start of the course items.
        for(int i = courseStartIndex; i < planTexts.length; i++)
        {
            String current = planTexts[i];

            if (current != null && current.length() != 0)
            {
                    if (!current.contains(" ")) //this is a course, cause a course can't contain a empty space
                    {
                        courseNames.add(current);
                    }
                    else
                    {
                        if (current.startsWith("  ")) //this seems to be a remark cause it starts with two empty spaces
                        {
                            remarks.add(current);

                            currentTableDepth = 0; //set row vars to default, cause the row ends with the remark
                            currentExchangeTeachersDepth = 0;
                            currentRoomDepth = 0;
                        }
                        else if (current.startsWith(" ")) //if it starts with an empty space it is either an exchangeTeacher or a room
                        {
                            if (current.length() == 4) //with 4 letters it surely is a room (4 letters cause the empty space at the beginning counts :))
                            {
                                current = current.replaceAll(" ", "");
                                newRooms.add(current);

                                currentRoomDepth += 1;
                            }
                            else //otherwise its the exchange teacher OR an empty cell
                            {
                                if (current.length() <= 3) //empty cell
                                {
                                    if(currentExchangeTeachersDepth < currentTableDepth) //add teachers if they're not filled already
                                    {
                                        currentExchangeTeachersDepth += 1;
                                        exchangeTeachers.add("");
                                    }
                                    else if(currentRoomDepth < currentTableDepth) //add rooms if they're not filled already
                                    {
                                        currentRoomDepth += 1;
                                        newRooms.add("");
                                    }
                                }
                                else
                                {
                                    exchangeTeachers.add(current);

                                    currentExchangeTeachersDepth += 1;
                                }
                            }
                        }
                        else //this is a course detail (i.e. 3 Mu Gd)
                        {
                            String[] d = current.split(" ");

                            if (d.length <= 3)
                            {
                                courseDetails.add(current);

                                currentTableDepth += 1;
                            }
                            else
                            { //if the change only consists of 1 line, then we have all info in this string
                                if(d.length == 4) // d[0]: course, d[1]: hour, d[2]: subject, d[3] oldTeacher, others empty
                                {
                                    changes.add(new Change(courseToClass(d[0]), d[0], Integer.parseInt(d[1]), d[2], d[3], "", "", ""));
                                }
                                else if(d.length == 5) // d[0]: course, d[1]: hour, d[2]: subject, d[3] oldTeacher, d[4] newTeacher _or_ Room
                                {
                                    if(d[4].length() == 3) // d[4] is a room
                                    {
                                        changes.add(new Change(courseToClass(d[0]), d[0], Integer.parseInt(d[1]), d[2], d[3], "", d[4], ""));
                                    }
                                    else // d[4] is a newTeacher
                                    {
                                        changes.add(new Change(courseToClass(d[0]), d[0], Integer.parseInt(d[1]), d[2], d[3], d[4], "", ""));
                                    }
                                }
                                else
                                {
                                    if (d[5].length() > 3) {
                                        d[6] = d[7];
                                    }

                                    changes.add(new Change(courseToClass(d[0]), d[0], Integer.parseInt(d[1]), d[2], d[3], d[5], d[6], ""));
                                }
                            }
                    }
                }
            }
            else //we've finished
            {
                break;
            }
        }

        for(int i = 0; i < courseNames.size(); i++) //this formats all courses
        {
            String[] splittedCourse = courseDetails.get(i).split(" ");

            int hour = Integer.parseInt(splittedCourse[0]);
            String course;
            String oldTeacher;

            if(splittedCourse.length == 3) //i.e. 3 Mu Gd
            {
                course = splittedCourse[1];
                oldTeacher = splittedCourse[2];
            }
            else //i.e. 3 LaufenTr (happens when the subject string is longer than 4 chars)
            {
                String[] secondAtUpper = splittedCourse[1].split("(?=\\p{Lu})"); //we split at the second upper case

                course = secondAtUpper[0];
                oldTeacher = secondAtUpper[1];
            }

            changes.add(new Change(courseToClass(courseNames.get(i)), courseNames.get(i), hour, course, oldTeacher, exchangeTeachers.get(i), newRooms.get(i), remarks.get(i)));
        }

        changes = removeDuplicates(changes);
    }

    @Override
    protected int getStartIndex(String[] planTexts)
    {
        List<String> classes = new ArrayList<String>(); //that's where we save all class strings

        for(int i = 0; i < planTexts.length; i++) //Gets all classes (not courses tho!)
        {
            String current = planTexts[i];

            if(current != null && !current.contains("Vertretungen:"))
            {
                current = current.replaceAll(" ", ""); //remove the annoying empty spaces

                if(classes.size() > 0 && compareClassToCourse(classes.get(0), current) || current.startsWith("5orc") || current.startsWith("5cho"))  //if we encounter a class we have already, its a course because we went through all classes!! so we get out of the loop!
                {
                    return i;
                }

                if(current.length() > 1) //make sure it is not something else than a class
                {
                    classes.add(current);
                }
            }
        }

        return 0;
    }


    private List<String> courseToClass(String course)
    {
        List<String> relatedClasses = new ArrayList<String>();

        if(course.startsWith("5orc") || course.startsWith("5cho"))
        {
            relatedClasses.add("11"); //we have to check for this explicitly, because "5orc" and "5cho" would be added to any 5th grade, but in reality they are 11th and 12th grade courses
            relatedClasses.add("12");

            return relatedClasses;
        }

        for(int i = identifiers.size() - 1; i > -1; --i) //reverse for-loop cause if a course is "8a7a", 7a will be picked which is wrong
        {
            if(course.contains(identifiers.get(i)) || compareClassToDetailedCourse(identifiers.get(i), course))
            {
                relatedClasses.add(identifiers.get(i));
            }
        }

        if(relatedClasses.size() > 0)
        {
            return relatedClasses; //we already got some classes below the 11th grade, so we have to exit here.
        }

        if(course.startsWith("1")) //11th class courses start with 1
        {
            relatedClasses.add("11");
        }

        if(course.startsWith("2")) //12th class courses start with 2
        {
            relatedClasses.add("12");
        }

        return relatedClasses;
    }

    private boolean compareClassToDetailedCourse(String affectedClass, String course)
    {
        String[] splittedCourse = splitAtNumber(course);
        String[] splittedClass = splitAtNumber(affectedClass);

        if(splittedCourse.length >= 2 && splittedClass.length >= 2)
        {
            if(splittedCourse[0].equals(splittedClass[0]) && splittedCourse[1].contains(splittedClass[1]))
            {
                return true;
            }
        }

        return false;
    }

    private boolean compareClassToCourse(String affectedClass, String course)
    {
        if(course.contains(affectedClass) || course.length() > 4 || compareClassToDetailedCourse(affectedClass, course)) //normal courses strings aren't bigger than 4 chars, so if they are they have to be some special courses like "Laufen"
        {
            return true;
        }
        else if(affectedClass.equals("11") && course.startsWith("1") && !course.equals("12") || affectedClass.equals("12") && course.startsWith("2"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
