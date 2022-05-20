package id.sireto.reviewjujur.authentication

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import id.sireto.reviewjujur.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class RegistrationActivityTest {
    private val name = "Imam Aprido Simarmata"
    private val email = "apridos@yandex.com"
    private val whatsappNo = "6281268176572"
    private val password = "dummyPassword"

    @Before
    fun setup(){
        ActivityScenario.launch(RegistrationActivity::class.java)
    }

    @Test
    fun assertRegisterUser() {
        onView(withId(R.id.registration_name)).perform(typeText(name), closeSoftKeyboard())
        onView(withId(R.id.registration_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.registration_whatsappNumber)).perform(typeText(whatsappNo), closeSoftKeyboard())
        onView(withId(R.id.registration_password)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.registration_register)).perform(click())
    }
}