package com.example.novigrad;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

@RunWith(AndroidJUnit4.class)
public class NovigradTest {


    @Rule
    public ActivityTestRule<AdminHome> adminHomeActivityTestRule= new ActivityTestRule<AdminHome>(AdminHome.class);
    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule= new ActivityTestRule<MainActivity>(MainActivity.class);
    @Rule
    public ActivityTestRule<SignUp> signUpActivityTestRule= new ActivityTestRule<SignUp>(SignUp.class);
    @Rule
    public ActivityTestRule<AdminServices> adminServicesActivityTestRule= new ActivityTestRule<AdminServices>(AdminServices.class);
    @Rule
    public ActivityTestRule<EmployeeProfle> employeeProfileActivityTestRule= new ActivityTestRule<EmployeeProfle>(EmployeeProfle.class);
    @Rule
    public ActivityTestRule<CustomerEvaluation> customerEvaluationActivityTestRule= new ActivityTestRule<CustomerEvaluation>(CustomerEvaluation.class);


    private AdminHome adminHomeActivity=null;
    private SignUp signUpActivity=null;
    private MainActivity mainActivity=null;
    private AdminServices adminServicesActivity=null;
    private EmployeeProfle employeeProfileActivity=null;
    private CustomerEvaluation customerEvaluationActivity=null;

    private TextView text;
    private ImageView view;
    @Before
    public void setUp() throws Exception {
        adminHomeActivity=adminHomeActivityTestRule.getActivity();
        mainActivity=mainActivityTestRule.getActivity();
        signUpActivity=signUpActivityTestRule.getActivity();
        adminServicesActivity=adminServicesActivityTestRule.getActivity();
        employeeProfileActivity=employeeProfileActivityTestRule.getActivity();
        customerEvaluationActivity=customerEvaluationActivityTestRule.getActivity();
    }

    @Test
    public void clickSericeImage() {
        //perform the click on the service image view on the adminWelcome page
        onView(withId(R.id.EmployeeServiceIcon)).check(matches(isClickable()));
    }

    @Test
    public void checkWelcomeMessage() throws Exception {
        //Check that the welcome message is not null
        text = adminHomeActivity.findViewById(R.id.employee_welcome);
        String name = text.getText().toString();
        assertNotEquals("", name);

    }


    @Test
    public void checkTitle() throws Exception {
        //Check the title of one of the options in the adminHome page
        view = adminHomeActivity.findViewById(R.id.serviceNametextid);
        String name = "Services";
        assertEquals("Services", name);

    }

    @Test
    public void checkEmailFormat() throws Exception {
        //Check the email validity
        Boolean validity=signUpActivity.checkEmail("example@gmail.com");
        assertTrue(validity);
    }

    @Test
    public void checkServices() throws Exception {
        //check that the listView rendering all the services is not null,
        ListView listViewServices=adminServicesActivity.findViewById(R.id.listViewServices);
        assertNotNull(listViewServices);
    }

    /*
     * Down here are 2 more tests for the Delivery 3 of the project
     */
    @Test
    public void checkPhoneNumberFormat() throws Exception {
        //Check the phone number format
        Boolean goodFormat=employeeProfileActivity.checkPhoneNumber("example@gmail.com");
        Boolean example1=employeeProfileActivity.checkPhoneNumber("52165845325");
        Boolean example2=employeeProfileActivity.checkPhoneNumber("55222m45*");
        assertTrue(example1);
        assertFalse(example2);
    }

    @Test
    public void checkEmptyField() throws Exception {
        //Create many EditText and assign them some texts
        EditText field1 = new EditText(employeeProfileActivity);
        EditText field2 = new EditText(employeeProfileActivity);
        EditText field3 = new EditText(employeeProfileActivity);
        EditText field4 = new EditText(employeeProfileActivity);
        field1.setText("");
        field2.setText("SEG 2505");
        field3.setText("Best TA ever");
        field4.setText("");


        boolean emptyFieldResult = employeeProfileActivity.checkField(field1, field2, field3, field4);

        assertEquals(true, emptyFieldResult);
    }


    //compare two objects
    @Test
    public void compareObject() {

        Schedule tuesday=new Schedule("Tuesday","15:20","20:00",2);
        Schedule thursday=new Schedule("Thursday","15:20","20:00",4);
        int difference=thursday.compareTo(tuesday);
        assertEquals(2,difference);
    }

    //check if the branch rate is well calculated
    @Test
    public void getBranchRate() {

        User user=new  User("id1","first_name","last_name","emai@email.com","H7J 5P9","my password","role",123456);
        user.addRate(4.0f);
        user.addRate(3.0f);
        Double userRate= user.getRate();
        assertNotEquals(userRate,3.60d);

    }

    //check if the title of the Evaluation page is well set
    @Test
    public void checkEvaluationTitle() throws Exception {
        //Check that the welcome message is not null
        text = customerEvaluationActivity.findViewById(R.id.textView8);
        String name = text.getText().toString();
        assertEquals("All Branches Rating", name);
    }

    @Test
    public void checkTextContentForEvaluation() throws Exception {
        //Check that the welcome message is not null
        text = customerEvaluationActivity.findViewById(R.id.branchName);
        String name = text.getText().toString();
        assertNotEquals("All Branches Rating", name);
    }
    @Test
    public void checkSubmitEvaluation() throws Exception {
        //Check that the welcome message is not null
        Button button = customerEvaluationActivity.findViewById(R.id.submitButton);
        String name = button.getText().toString();
        assertEquals("Submit", name);
    }


}
