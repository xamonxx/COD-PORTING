package com.vanguard.mobile.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ServerAddressTest {
    @Test fun appliesDefaultPort() {
        assertEquals(ServerAddress("example.org", 28960), ServerAddress.parse("example.org"))
    }

    @Test fun acceptsExplicitIpv4AndIpv6() {
        assertEquals("10.0.0.2:30000", ServerAddress.parse("10.0.0.2:30000").toString())
        assertEquals("[2001:db8::1]:28960", ServerAddress.parse("[2001:db8::1]").toString())
    }

    @Test fun rejectsCommandsWhitespaceAndInvalidPorts() {
        assertNull(ServerAddress.parse("host;quit"))
        assertNull(ServerAddress.parse("host name"))
        assertNull(ServerAddress.parse("host:0"))
        assertNull(ServerAddress.parse("host:65536"))
    }
}
