package com.example

import com.example.data.model.AgentCluster
import com.example.data.model.AgentStatus
import com.example.data.model.TaskPriority
import com.example.data.model.TaskType
import com.example.data.model.ThrottleMode
import com.example.data.repository.YouTubeService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun verifyAgentClusters() {
    assertEquals(7, AgentCluster.values().size)
    assertTrue(AgentCluster.values().any { it.name == "CONTENT_ANALYZER" })
    assertTrue(AgentCluster.values().any { it.name == "DISPATCH_SYNTHESIS" })
  }

  @Test
  fun verifyAgentStatuses() {
    assertEquals(5, AgentStatus.values().size)
  }

  @Test
  fun verifyThrottleModes() {
    assertEquals(100, ThrottleMode.SAFE.maxOpsPerSec)
    assertEquals(500, ThrottleMode.BURST.maxOpsPerSec)
    assertEquals(25, ThrottleMode.ECO.maxOpsPerSec)
  }

  @Test
  fun verifyYouTubeVideoIdExtraction() {
    val service = YouTubeService()
    val id1 = service.extractVideoId("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
    val id2 = service.extractVideoId("https://youtu.be/dQw4w9WgXcQ")
    val id3 = service.extractVideoId("https://www.youtube.com/shorts/dQw4w9WgXcQ")
    val id4 = service.extractVideoId("dQw4w9WgXcQ")

    assertEquals("dQw4w9WgXcQ", id1)
    assertEquals("dQw4w9WgXcQ", id2)
    assertEquals("dQw4w9WgXcQ", id3)
    assertEquals("dQw4w9WgXcQ", id4)
  }
}
