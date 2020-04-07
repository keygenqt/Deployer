//package com.keygenqt.deployer.web
//
//import com.keygenqt.deployer.web.controllers.Oauth2Controller
//import org.hamcrest.Matchers
//import org.hamcrest.core.IsEqual
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
//import org.springframework.test.context.junit4.SpringRunner
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers
//import java.util.*
//
//
//@RunWith(SpringRunner::class)
//@WebMvcTest(controllers = [Oauth2Controller::class])
//class WelcomeControllerTest {
//    @Autowired
//    private val mockMvc: MockMvc? = null
//    var expectedList =
//        Arrays.asList("a", "b", "c", "d", "e", "f", "g")
//
//    @Test
//    @Throws(Exception::class)
//    fun main() {
//        val resultActions = mockMvc!!.perform(MockMvcRequestBuilders.get("/"))
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.view().name("welcome"))
//            .andExpect(
//                MockMvcResultMatchers.model()
//                    .attribute("message", IsEqual.equalTo("Mkyong"))
//            )
//            .andExpect(
//                MockMvcResultMatchers.model().attribute(
//                    "tasks",
//                    Matchers.`is`(expectedList)
//                )
//            )
//            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Hello, Mkyong")))
//        val mvcResult = resultActions.andReturn()
//        val mv = mvcResult.modelAndView
//        //
//    }
//
//    // Get request with Param
//    @Test
//    @Throws(Exception::class)
//    fun hello() {
//        mockMvc!!.perform(MockMvcRequestBuilders.get("/hello").param("name", "I Love Kotlin!"))
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.view().name("welcome"))
//            .andExpect(
//                MockMvcResultMatchers.model().attribute(
//                    "message",
//                    IsEqual.equalTo("I Love Kotlin!")
//                )
//            )
//            .andExpect(
//                MockMvcResultMatchers.content().string(Matchers.containsString("Hello, I Love Kotlin!"))
//            )
//    }
//}