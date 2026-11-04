package com.example

import com.example.data.model.AgentCluster
import com.example.data.model.AgentStatus
import com.example.data.model.TaskPriority
import com.example.data.model.TaskType
import com.example.data.model.ThrottleMode
import com.example.data.repository.RealYouTubeService
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
    assertEquals(8, AgentStatus.values().size)
    assertTrue(AgentStatus.values().any { it.name == "WORKING" })
    assertTrue(AgentStatus.values().any { it.name == "IDLE" })
    assertTrue(AgentStatus.values().any { it.name == "PAUSED" })
    assertTrue(AgentStatus.values().any { it.name == "FAILED" })
    assertTrue(AgentStatus.values().any { it.name == "COMPLETED" })
  }

  @Test
  fun verifyAgentIdFormat() {
    val agentNumber = 42
    val formattedId = "AGT-%04d".format(agentNumber)
    assertEquals("AGT-0042", formattedId)

    val maxAgent = 1000
    val maxId = "AGT-%04d".format(maxAgent)
    assertEquals("AGT-1000", maxId)
  }

  @Test
  fun verifyThrottleModes() {
    assertEquals(100, ThrottleMode.SAFE.maxOpsPerSec)
    assertEquals(500, ThrottleMode.BURST.maxOpsPerSec)
    assertEquals(25, ThrottleMode.ECO.maxOpsPerSec)
  }

  @Test
  fun verifyYouTubeVideoIdExtraction() {
    val service = RealYouTubeService()
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
