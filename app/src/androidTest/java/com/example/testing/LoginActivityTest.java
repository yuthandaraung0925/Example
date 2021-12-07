package com.example.testing;

import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginActivityTest {

    @Rule
    //public ActivityTestRule<LoginActivity> loginActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity loginActivity = null;

    @Before
    public void setUp() throws Exception {
        //loginActivity = loginActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View view = loginActivity.findViewById(R.id.btnLogin);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        loginActivity = null;
    }
}