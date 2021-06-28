package com.example.project_layouts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RegisterActivityTest {
    RegisterActivity registerActivity = new RegisterActivity();

    @Test
    public void correctDetails() {
        boolean result = registerActivity.testDetails("tom@gmail.com", "123123", "rom");
        assertEquals(true, result);
    }
    @Test
    public void EmptyEmail() {
        boolean result = registerActivity.testDetails("", "123123", "rom");
        assertEquals(false, result);
    }
    @Test
    public void ShortPassword() {
        boolean result = registerActivity.testDetails("tom@gmail.com", "12", "rom");
        assertEquals(false, result);
    }
    @Test
    public void EmptyName() {
        boolean result = registerActivity.testDetails("tom@gmail.com", "123123", "");
        assertEquals(false, result);
    }


}