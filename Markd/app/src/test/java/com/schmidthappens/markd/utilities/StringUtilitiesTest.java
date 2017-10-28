package com.schmidthappens.markd.utilities;

import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.utilities.StringUtilities;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class StringUtilitiesTest {
    @Test
    public void single_doctor() {
        String nameString = StringUtilities.getFormattedName("Dr.", "Jerry", "Seinfeld", "M.D.", "Single");
        assertTrue(nameString, nameString.equals("Dr. Jerry Seinfeld M.D."));
    }

    @Test
    public void married_doctor() {
        String nameString = StringUtilities.getFormattedName("Dr.", "O.J.", "Simpson", "M.D.", "Married");
        assertTrue(nameString, nameString.equals("Dr. and Mrs. Simpson M.D."));
    }

    @Test
    public void single_massage_therapist() {
        String nameString = StringUtilities.getFormattedName(null, "Brittany", "Schmidt", "M.T.", "Single");
        assertTrue(nameString, nameString.equals("Brittany Schmidt M.T."));
    }

    @Test
    public void single_man_with_prefix()  {
        String nameString = StringUtilities.getFormattedName("Mr.", "Jerry", "Seinfeld", null, "Single");
        assertTrue(nameString, nameString.equals("Mr. Jerry Seinfeld"));
    }

    @Test
    public void single_man_without_prefix() {
        String nameString = StringUtilities.getFormattedName(null, "Joshua", "Schmidt", null, "Single");
        assertTrue(nameString, nameString.equals("Joshua Schmidt"));
    }

    @Test
    public void single_man_with_empty_strings() {
        String nameString = StringUtilities.getFormattedName("", "Joshua", "Schmidt", "", "Single");
        assertTrue(nameString, nameString.equals("Joshua Schmidt"));
    }

    @Test
    public void single_woman_without_prefix() {
        String nameString = StringUtilities.getFormattedName("Mrs.", "Brittany", "Pond", null, "Single");
        assertTrue(nameString, nameString.equals("Mrs. Brittany Pond"));
    }

    @Test
    public void married_man() {
        String nameString = StringUtilities.getFormattedName("Mr.", "Joshua", "Schmidt", null, "Married");
        assertTrue(nameString, nameString.equals("Mr. and Mrs. Schmidt"));
    }

    @Test
    public void married_man_no_prefix() {
        String nameString = StringUtilities.getFormattedName("", "Joshua", "Schmidt", "", "Married");
        assertTrue(nameString, nameString.equals("Joshua Schmidt"));
    }

    @Test
    public void marriend_man_no_suffix_or_prefix() {
        String nameString = StringUtilities.getFormattedName(null, "Joshua", "Schmidt", "", "Married");
        assertTrue(nameString, nameString.equals("Joshua Schmidt"));
    }
}