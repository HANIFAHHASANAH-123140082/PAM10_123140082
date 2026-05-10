package com.example.notesapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesScreenTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_mainActivity_isDisplayed() {
        onView(withId(android.R.id.content))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_fabAdd_isDisplayed_andClickable() {
        onView(withId(R.id.fabAdd))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }

    @Test
    fun test_searchView_isDisplayed() {
        onView(withId(R.id.searchView))
            .check(matches(isDisplayed()))
    }
}